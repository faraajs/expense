package com.example.autospending.parser

import java.util.Locale

/**
 * Single direction policy for live [SpendingParser].
 * Ties (equal income/expense scores) → expense (debit-biased, preserves historical AS behavior).
 */
object TransactionDirectionClassifier {
    fun classify(
        raw: String,
        expenseKeywords: List<String>,
        incomeKeywords: List<String>,
    ): String? {
        val lowered = raw.lowercase(Locale.ROOT)
        val expenseScore = expenseKeywords.count { lowered.contains(it) }
        val incomeScore = incomeKeywords.count { lowered.contains(it) }
        if (expenseScore == 0 && incomeScore == 0) return null
        return if (incomeScore > expenseScore) "income" else "expense"
    }
}
