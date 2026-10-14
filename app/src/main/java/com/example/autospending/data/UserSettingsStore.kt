package com.example.autospending.data

import android.content.Context
import android.content.SharedPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Feature flags and app-level settings.
 * - parsers_enabled: when false, notifications are only logged (Phase 0 safety mode)
 * - diagnostic_log_all_packages: when true, logs ALL notification packages (not just whitelist)
 */
class UserSettingsStore(context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("user_settings", Context.MODE_PRIVATE)

    private val _parsersEnabled = MutableStateFlow(prefs.getBoolean(KEY_PARSERS_ENABLED, true))
    val parsersEnabled: StateFlow<Boolean> = _parsersEnabled.asStateFlow()

    private val _diagnosticLogAll = MutableStateFlow(prefs.getBoolean(KEY_DIAGNOSTIC_LOG_ALL, false))
    val diagnosticLogAll: StateFlow<Boolean> = _diagnosticLogAll.asStateFlow()

    fun setParsersEnabled(enabled: Boolean) {
        _parsersEnabled.value = enabled
        prefs.edit().putBoolean(KEY_PARSERS_ENABLED, enabled).apply()
    }

    fun setDiagnosticLogAll(enabled: Boolean) {
        _diagnosticLogAll.value = enabled
        prefs.edit().putBoolean(KEY_DIAGNOSTIC_LOG_ALL, enabled).apply()
    }

    companion object {
        private const val KEY_PARSERS_ENABLED = "parsers_enabled"
        private const val KEY_DIAGNOSTIC_LOG_ALL = "diagnostic_log_all_packages"
    }
}
