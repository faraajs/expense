package com.example.autospending.ui.navigation

/**
 * Type-safe routes for Jetpack Navigation Compose.
 * Bottom tabs map to [AppTab]; nested tools live under audit routes.
 */
sealed class Screen(val route: String) {
    data object Home : Screen("home")
    data object Transactions : Screen("transactions")
    data object Audit : Screen("audit")
    data object Budget : Screen("budget")
    data object TanyaAi : Screen("tanya_ai")
    data object RawLog : Screen("audit/raw_log")
    data object OemBattery : Screen("audit/oem_battery")

    companion object {
        val bottomTabs: List<Screen> = listOf(Home, Transactions, Audit, Budget, TanyaAi)

        fun fromAppTab(tab: AppTab): Screen = when (tab) {
            AppTab.HOME -> Home
            AppTab.TRANSAKSI -> Transactions
            AppTab.AUDIT -> Audit
            AppTab.BUDGET -> Budget
            AppTab.TANYA_AI -> TanyaAi
        }

        fun toAppTab(route: String?): AppTab = when {
            route == null -> AppTab.HOME
            route.startsWith(Home.route) -> AppTab.HOME
            route.startsWith(Transactions.route) -> AppTab.TRANSAKSI
            route.startsWith(Audit.route) || route.startsWith("audit") -> AppTab.AUDIT
            route.startsWith(Budget.route) -> AppTab.BUDGET
            route.startsWith(TanyaAi.route) -> AppTab.TANYA_AI
            else -> AppTab.HOME
        }
    }
}
