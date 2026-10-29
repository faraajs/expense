package com.example.autospending.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.FactCheck
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.ui.graphics.vector.ImageVector

enum class AppTab(
    val label: String,
    val icon: ImageVector
) {
    HOME("Home", Icons.Filled.Home),
    TRANSAKSI("Transactions", Icons.Filled.ReceiptLong),
    AUDIT("Audit", Icons.Filled.FactCheck),
    BUDGET("Budget", Icons.Filled.AccountBalanceWallet),
    TANYA_AI("AI Q&A", Icons.Filled.SmartToy);

    companion object {
        val entriesInOrder: List<AppTab> = entries
    }
}
