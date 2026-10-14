package com.example.autospending.data

import android.content.Context
import android.content.SharedPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicLong

/** Per-package parse outcome counters, survives process death via SharedPreferences. */
data class PackageHealth(
    val packageName: String,
    val parsed: Int = 0,
    val skipped: Int = 0,
    val failed: Int = 0,
    val lastSuccessAt: Long = 0L
)

data class HealthSnapshot(
    val listenerConnected: Boolean,
    val lastEventAt: Long,
    val packages: List<PackageHealth>
)

/**
 * Tracks NotificationListenerService health and per-package parse outcome counters.
 * Thread-safe: uses AtomicLong/AtomicBoolean + ConcurrentHashMap.
 * Throttles SharedPreferences writes to every 5 seconds to avoid I/O spam.
 */
class ListenerHealthTracker(context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("listener_health", Context.MODE_PRIVATE)

    private val _connected = AtomicBoolean(false)
    private val _lastEventAt = AtomicLong(prefs.getLong(KEY_LAST_EVENT, 0L))

    // packageName -> mutable counters (parsed, skipped, failed, lastSuccessAt)
    private val _counters = ConcurrentHashMap<String, IntArray>() // [parsed, skipped, failed]
    private val _lastSuccess = ConcurrentHashMap<String, Long>()
    private var _lastPersistAt = 0L

    private val _state = MutableStateFlow(snapshot())
    val state: StateFlow<HealthSnapshot> = _state.asStateFlow()

    init {
        // Restore counters from prefs
        prefs.all.forEach { (key, value) ->
            if (key.startsWith(PREFIX_PARSED) && value is Int) {
                val pkg = key.removePrefix(PREFIX_PARSED)
                counters(pkg)[0] = value
            }
            if (key.startsWith(PREFIX_SKIPPED) && value is Int) {
                val pkg = key.removePrefix(PREFIX_SKIPPED)
                counters(pkg)[1] = value
            }
            if (key.startsWith(PREFIX_FAILED) && value is Int) {
                val pkg = key.removePrefix(PREFIX_FAILED)
                counters(pkg)[2] = value
            }
            if (key.startsWith(PREFIX_LAST_OK) && value is Long) {
                val pkg = key.removePrefix(PREFIX_LAST_OK)
                _lastSuccess[pkg] = value
            }
        }
    }

    fun onConnected() {
        _connected.set(true)
        touchEvent()
    }

    fun onDisconnected() {
        _connected.set(false)
        touchEvent()
    }

    fun recordParsed(packageName: String) {
        counters(packageName)[0]++
        _lastSuccess[packageName] = System.currentTimeMillis()
        touchEvent()
        maybePersist()
    }

    fun recordSkipped(packageName: String) {
        counters(packageName)[1]++
        touchEvent()
    }

    fun recordFailed(packageName: String) {
        counters(packageName)[2]++
        touchEvent()
        maybePersist()
    }

    private fun counters(pkg: String): IntArray =
        _counters.getOrPut(pkg) { IntArray(3) }

    private fun touchEvent() {
        _lastEventAt.set(System.currentTimeMillis())
        _state.value = snapshot()
    }

    private fun snapshot(): HealthSnapshot {
        val pkgs = _counters.keys.map { pkg ->
            val c = _counters[pkg] ?: IntArray(3)
            PackageHealth(pkg, c[0], c[1], c[2], _lastSuccess[pkg] ?: 0L)
        }.sortedByDescending { it.parsed + it.failed }
        return HealthSnapshot(_connected.get(), _lastEventAt.get(), pkgs)
    }

    private fun maybePersist() {
        val now = System.currentTimeMillis()
        if (now - _lastPersistAt < 5_000) return
        _lastPersistAt = now
        val editor = prefs.edit().putLong(KEY_LAST_EVENT, _lastEventAt.get())
        _counters.forEach { (pkg, c) ->
            editor.putInt("$PREFIX_PARSED$pkg", c[0])
                .putInt("$PREFIX_SKIPPED$pkg", c[1])
                .putInt("$PREFIX_FAILED$pkg", c[2])
            _lastSuccess[pkg]?.let { editor.putLong("$PREFIX_LAST_OK$pkg", it) }
        }
        editor.apply()
    }

    companion object {
        private const val KEY_LAST_EVENT = "last_event_at"
        private const val PREFIX_PARSED = "parsed_"
        private const val PREFIX_SKIPPED = "skipped_"
        private const val PREFIX_FAILED = "failed_"
        private const val PREFIX_LAST_OK = "last_ok_"
    }
}
