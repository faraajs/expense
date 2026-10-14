package com.example.autospending.parser

import java.security.MessageDigest

/**
 * Canonical SHA-256 deduplication fingerprint generator using epoch time-bucketing.
 */
object TransactionFingerprintGenerator {
    private const val TWO_MINUTES_MS = 120_000L

    fun generateHash(
        packageFamily: String,
        type: String,
        amount: Long,
        normalizedText: String,
        postedAtMs: Long
    ): String {
        val timeBucket = (postedAtMs / TWO_MINUTES_MS).toString()
        val rawPayload = "$packageFamily|$type|$amount|$normalizedText|$timeBucket"
        return MessageDigest.getInstance("SHA-256")
            .digest(rawPayload.toByteArray(Charsets.UTF_8))
            .joinToString("") { "%02x".format(it) }
    }
}
