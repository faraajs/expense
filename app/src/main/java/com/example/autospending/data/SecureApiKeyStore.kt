package com.example.autospending.data

import android.content.Context
import android.util.Log
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys

/**
 * Stores user-pasted Gemini API key only (never shipped in APK).
 * Uses security-crypto 1.0.x [MasterKeys] API.
 *
 * One-time migrates keys from legacy plain SharedPreferences:
 * - `auto_spending_prefs` (AppViewModel)
 * - `your_spending_prefs` (older SecureApiKeyStore)
 */
class SecureApiKeyStore(context: Context) {
    private val appContext = context.applicationContext

    private val prefs = EncryptedSharedPreferences.create(
        PREFS_NAME,
        MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC),
        appContext,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM,
    )

    init {
        migrateFromPlainPrefsIfNeeded()
    }

    fun getApiKey(): String =
        prefs.getString(KEY_API, null)?.trim().orEmpty()

    fun saveApiKey(key: String) {
        prefs.edit().putString(KEY_API, key.trim()).apply()
    }

    fun clearApiKey() {
        prefs.edit().remove(KEY_API).apply()
    }

    /** Mask for Settings UI: show last 4 only. */
    fun maskedKey(): String? {
        val key = getApiKey().ifBlank { return null }
        if (key.length <= 4) return "••••"
        return "••••" + key.takeLast(4)
    }

    private fun migrateFromPlainPrefsIfNeeded() {
        if (getApiKey().isNotBlank()) return
        val candidates = listOf(
            LEGACY_PREFS_MAIN to LEGACY_KEY_GEMINI,
            LEGACY_PREFS_ALT to LEGACY_KEY_GEMINI,
        )
        for ((prefsName, keyName) in candidates) {
            try {
                val plain = appContext.getSharedPreferences(prefsName, Context.MODE_PRIVATE)
                val raw = plain.getString(keyName, null)?.trim().orEmpty()
                if (raw.isNotBlank() && !raw.startsWith("sk-or-")) {
                    saveApiKey(raw)
                    plain.edit().remove(keyName).apply()
                    Log.i(TAG, "Migrated API key from $prefsName to encrypted store")
                    return
                }
            } catch (e: Exception) {
                Log.w(TAG, "Legacy key migrate failed for $prefsName: ${e.message}")
            }
        }
    }

    companion object {
        private const val TAG = "SecureApiKeyStore"
        private const val PREFS_NAME = "secure_gemini_key"
        private const val KEY_API = "gemini_api_key"
        private const val LEGACY_PREFS_MAIN = "auto_spending_prefs"
        private const val LEGACY_PREFS_ALT = "your_spending_prefs"
        private const val LEGACY_KEY_GEMINI = "gemini_api_key"
    }
}
