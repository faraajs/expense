package com.example.autospending.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.autospending.ai.GeminiClient
import com.example.autospending.data.HealthSnapshot
import com.example.autospending.data.ListenerHealthTracker
import com.example.autospending.data.SecureApiKeyStore
import com.example.autospending.data.SpendingRepository
import com.example.autospending.data.TransactionEntity
import com.example.autospending.data.UserSettingsStore
import com.example.autospending.data.currentMonthWindow
import com.example.autospending.ui.analytics.DEFAULT_CATEGORIES
import com.example.autospending.ui.analytics.TimeRange
import com.example.autospending.ui.analytics.aggregateByCategory
import com.example.autospending.ui.analytics.budgetRingModel
import com.example.autospending.ui.analytics.explorationFilterSummary
import com.example.autospending.ui.analytics.filterTransactions
import com.example.autospending.ui.analytics.hasActiveExplorationFilters
import com.example.autospending.ui.analytics.rangeBounds
import com.example.autospending.ui.navigation.AppTab
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.ZoneId
import java.util.UUID

@OptIn(ExperimentalCoroutinesApi::class)
class AppViewModel(
    private val repository: SpendingRepository,
    private val secureApiKeyStore: SecureApiKeyStore,
    private val userSettingsStore: UserSettingsStore,
    private val listenerHealthTracker: ListenerHealthTracker,
    private val appVersionName: String = "",
    private val zoneId: ZoneId = ZoneId.systemDefault(),
    private val nowMillis: () -> Long = { System.currentTimeMillis() },
    private val geminiClient: GeminiClient = GeminiClient()
) : ViewModel() {
    private val month = currentMonthWindow()

    private data class UiNavFilters(
        val selectedTab: AppTab = AppTab.HOME,
        val timeRange: TimeRange = TimeRange.THIS_MONTH,
        val categoryFilter: String? = null,
        val typeFilter: String? = null,
        val sourceFilter: String? = null
    )

    private val _navFilters = MutableStateFlow(UiNavFilters())

    private val _events = MutableSharedFlow<UiEvent>(extraBufferCapacity = 8)
    val events = _events.asSharedFlow()

    private val _chatMessages = MutableStateFlow<List<ChatMessage>>(emptyList())
    val chatMessages: StateFlow<List<ChatMessage>> = _chatMessages.asStateFlow()

    private val _isAiLoading = MutableStateFlow(false)
    val isAiLoading: StateFlow<Boolean> = _isAiLoading.asStateFlow()

    private val _apiKey = MutableStateFlow(secureApiKeyStore.getApiKey())
    val apiKey: StateFlow<String> = _apiKey.asStateFlow()

    val parsersEnabled: StateFlow<Boolean> = userSettingsStore.parsersEnabled
    val diagnosticLogAll: StateFlow<Boolean> = userSettingsStore.diagnosticLogAll
    val healthSnapshot: StateFlow<HealthSnapshot> = listenerHealthTracker.state

    private val _isExporting = MutableStateFlow(false)
    val isExporting: StateFlow<Boolean> = _isExporting.asStateFlow()

    val rawLogEntries: StateFlow<List<com.example.autospending.data.RawLogEntity>> =
        repository.observeRawLog(200).stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList(),
        )

    private val monthTx = repository.observeTransactionsForMonth(month)

    private val explorationTx = _navFilters.flatMapLatest { nav ->
        when (nav.timeRange) {
            TimeRange.THIS_MONTH -> monthTx
            TimeRange.TODAY, TimeRange.LAST_7_DAYS -> {
                val bounds = rangeBounds(nav.timeRange, nowMillis(), zoneId)
                repository.observeTransactionsForRange(bounds.startMillis, bounds.endMillis)
            }
        }
    }

    val uiState: StateFlow<AppUiState> =
        combine(
            monthTx,
            repository.observeBudget(month.key),
            explorationTx,
            _navFilters
        ) { monthList, budget, exploration, nav ->
            val totalExpense = monthList.filter { it.type == "expense" }.sumOf { it.amount }
            val totalIncome = monthList.filter { it.type == "income" }.sumOf { it.amount }
            val limit = budget?.limitAmount ?: 0L
            val thresholds = budget?.alertThresholdValues() ?: listOf(80, 100)
            val visible = filterTransactions(
                exploration,
                nav.categoryFilter,
                nav.typeFilter,
                nav.sourceFilter
            )
            val breakdown = aggregateByCategory(exploration)
            val cats = (breakdown.map { it.category } + DEFAULT_CATEGORIES).distinct().sorted()

            AppUiState(
                monthKey = month.key,
                monthTransactions = monthList,
                visibleTransactions = visible,
                categoryBreakdown = breakdown,
                availableCategories = cats,
                budgetLimit = limit,
                totalExpense = totalExpense,
                totalIncome = totalIncome,
                budgetRing = budgetRingModel(totalExpense, limit, thresholds),
                selectedTab = nav.selectedTab,
                timeRange = nav.timeRange,
                categoryFilter = nav.categoryFilter,
                typeFilter = nav.typeFilter,
                sourceFilter = nav.sourceFilter,
                hasActiveFilters = hasActiveExplorationFilters(
                    nav.timeRange, nav.categoryFilter, nav.typeFilter, nav.sourceFilter
                ),
                filterSummary = explorationFilterSummary(
                    nav.timeRange, nav.categoryFilter, nav.typeFilter, nav.sourceFilter
                )
            )
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = AppUiState(monthKey = month.key)
        )

    fun selectTab(tab: AppTab) {
        _navFilters.value = _navFilters.value.copy(selectedTab = tab)
    }

    fun setTimeRange(range: TimeRange) {
        _navFilters.value = _navFilters.value.copy(timeRange = range)
    }

    fun setCategoryFilter(category: String?) {
        _navFilters.value = _navFilters.value.copy(categoryFilter = category)
    }

    fun toggleCategoryFilter(category: String) {
        val current = _navFilters.value.categoryFilter
        _navFilters.value = _navFilters.value.copy(
            categoryFilter = if (current == category) null else category
        )
    }

    fun setTypeFilter(type: String?) {
        _navFilters.value = _navFilters.value.copy(typeFilter = type)
    }

    fun setSourceFilter(source: String?) {
        _navFilters.value = _navFilters.value.copy(sourceFilter = source)
    }

    fun clearFilters() {
        _navFilters.value = _navFilters.value.copy(
            categoryFilter = null,
            typeFilter = null,
            sourceFilter = null,
            timeRange = TimeRange.THIS_MONTH
        )
    }

    fun saveApiKey(key: String) {
        val trimmed = key.trim()
        secureApiKeyStore.saveApiKey(trimmed)
        _apiKey.value = secureApiKeyStore.getApiKey()
        emitSnackbar("API key disimpan (terenkripsi)")
    }

    fun clearApiKey() {
        secureApiKeyStore.clearApiKey()
        _apiKey.value = ""
        emitSnackbar("API key dihapus")
    }

    fun setParsersEnabled(enabled: Boolean) {
        userSettingsStore.setParsersEnabled(enabled)
    }

    fun setDiagnosticLogAll(enabled: Boolean) {
        userSettingsStore.setDiagnosticLogAll(enabled)
    }

    fun clearChat() {
        _chatMessages.value = emptyList()
    }

    /**
     * Writes all transactions to app cache as JSON and emits a share intent path via snackbar.
     * Returns the absolute file path, or null on failure.
     */
    fun exportTransactionsToCache(cacheDir: java.io.File, onReady: (java.io.File?) -> Unit) {
        if (_isExporting.value) return
        viewModelScope.launch {
            _isExporting.value = true
            try {
                val json = repository.exportTransactionsJson(appVersionName)
                val count = repository.countAllTransactions()
                val file = java.io.File(
                    cacheDir,
                    "autospending-export-${System.currentTimeMillis()}.json"
                )
                file.writeText(json, Charsets.UTF_8)
                emitSnackbar("Export $count transaksi siap dibagikan")
                onReady(file)
            } catch (e: Exception) {
                emitSnackbar("Export gagal: ${e.message?.take(80)}")
                onReady(null)
            } finally {
                _isExporting.value = false
            }
        }
    }

    fun importTransactionsJson(json: String) {
        viewModelScope.launch {
            try {
                val result = repository.importTransactionsJson(json)
                emitSnackbar(
                    "Import: ${result.imported} baru, ${result.skipped} skip, ${result.errors} error"
                )
            } catch (e: Exception) {
                emitSnackbar("Import gagal: ${e.message?.take(80)}")
            }
        }
    }

    fun sendChatMessage(userMessage: String) {
        val apiKey = _apiKey.value
        if (userMessage.isBlank()) return
        if (apiKey.isBlank()) {
            _chatMessages.value = _chatMessages.value + ChatMessage(
                ChatRole.Assistant,
                "Tolong masukkan API Key Gemini (Google AI Studio) dulu di kolom di bawah ya! (AIza...)"
            )
            return
        }

        _chatMessages.value = _chatMessages.value + ChatMessage(ChatRole.User, userMessage)
        _isAiLoading.value = true

        viewModelScope.launch {
            val currentTransactions = uiState.value.monthTransactions
            val transactionText = currentTransactions.joinToString("\n") { t ->
                val typeStr = if (t.type == "expense") "Pengeluaran" else "Pemasukan"
                val dateStr = java.text.SimpleDateFormat("dd MMM yyyy", java.util.Locale.getDefault())
                    .format(java.util.Date(t.occurredAt))
                "- $dateStr: $typeStr Rp${t.amount} di ${t.merchant ?: "Tanpa Nama"} (Kategori: ${t.category})"
            }

            val prompt = """
                You are a helpful, Jakarta-slang Indonesian AI Financial Advisor. 
                Analyze the user's transaction data for this month and answer their question.
                
                Transactions data:
                $transactionText
                
                User question: "$userMessage"
                
                Answer accurately, check your math, and reply in a friendly, mixed Indonesian-English style (Bahasa campur Inggris, santai kayak anak IT Jakarta).
            """.trimIndent()

            val response = geminiClient.generate(apiKey, prompt)
            _chatMessages.value = _chatMessages.value + ChatMessage(ChatRole.Assistant, response)
            _isAiLoading.value = false
        }
    }

    fun addManualTransaction(type: String, amountText: String, merchant: String, category: String) {
        val amount = amountText.toAmountOrNull()
        if (amount == null) {
            emitSnackbar("Nominal tidak valid")
            return
        }
        val now = nowMillis()
        viewModelScope.launch {
            repository.addManualTransaction(
                TransactionEntity(
                    type = type,
                    amount = amount,
                    occurredAt = now,
                    sourcePackage = "manual",
                    sourceTitle = "Manual entry",
                    rawText = "Manual entry",
                    merchant = merchant.trim().ifBlank { null },
                    category = category.trim().ifBlank { "Other" },
                    confidence = 1f,
                    contentHash = "manual-${UUID.randomUUID()}"
                )
            )
            emitSnackbar("Transaksi disimpan")
        }
    }

    fun updateTransaction(
        transaction: TransactionEntity,
        type: String,
        amountText: String,
        merchant: String,
        category: String
    ) {
        val amount = amountText.toAmountOrNull()
        if (amount == null) {
            emitSnackbar("Nominal tidak valid")
            return
        }
        viewModelScope.launch {
            repository.updateTransaction(
                transaction.copy(
                    type = type,
                    amount = amount,
                    merchant = merchant.trim().ifBlank { null },
                    category = category.trim().ifBlank { "Other" }
                )
            )
            emitSnackbar("Transaksi diupdate")
        }
    }

    fun deleteTransaction(transaction: TransactionEntity) {
        viewModelScope.launch {
            repository.deleteTransaction(transaction)
            emitSnackbar("Transaksi dihapus")
        }
    }

    fun setBudget(amountText: String) {
        val amount = amountText.toAmountOrNull()
        if (amount == null) {
            emitSnackbar("Nominal tidak valid")
            return
        }
        viewModelScope.launch {
            repository.setBudget(month.key, amount)
            emitSnackbar("Budget diupdate")
        }
    }

    private fun emitSnackbar(message: String) {
        _events.tryEmit(UiEvent.ShowSnackbar(message))
    }

    private fun String.toAmountOrNull(): Long? {
        val digits = filter { it.isDigit() }
        return digits.toLongOrNull()?.takeIf { it > 0 }
    }
}

class AppViewModelFactory(
    private val repository: SpendingRepository,
    private val secureApiKeyStore: SecureApiKeyStore,
    private val userSettingsStore: UserSettingsStore,
    private val listenerHealthTracker: ListenerHealthTracker,
    private val appVersionName: String = "",
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AppViewModel::class.java)) {
            return AppViewModel(
                repository = repository,
                secureApiKeyStore = secureApiKeyStore,
                userSettingsStore = userSettingsStore,
                listenerHealthTracker = listenerHealthTracker,
                appVersionName = appVersionName,
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}
