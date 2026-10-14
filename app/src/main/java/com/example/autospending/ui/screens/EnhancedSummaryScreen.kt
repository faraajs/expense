package com.example.autospending.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.autospending.data.TransactionEntity
import com.example.autospending.data.formatIdr
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun EnhancedSummaryScreen(
    transactions: List<TransactionEntity>,
    monthKey: String
) {
    val expenses = transactions.filter { it.type == "expense" }
    val incomes = transactions.filter { it.type == "income" }
    val totalExpense = expenses.sumOf { it.amount }
    val totalIncome = incomes.sumOf { it.amount }
    val net = totalIncome - totalExpense

    val categoryBreakdown = expenses
        .groupBy { it.category }
        .map { (cat, txs) -> Pair(cat, txs.sumOf { it.amount }) }
        .sortedByDescending { it.second }

    // Day-by-day data for the line chart
    val dayData = buildDayData(transactions)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            "Ringkasan $monthKey",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )

        // Summary cards row
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            SummaryCard(
                label = "Pengeluaran",
                amount = totalExpense,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.weight(1f)
            )
            SummaryCard(
                label = "Pemasukan",
                amount = totalIncome,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.weight(1f)
            )
        }
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = if (net >= 0) MaterialTheme.colorScheme.primaryContainer
                else MaterialTheme.colorScheme.errorContainer
            )
        ) {
            Row(
                Modifier.fillMaxWidth().padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Net bulan ini", style = MaterialTheme.typography.titleSmall)
                Text(
                    "${if (net >= 0) "+" else ""}${formatIdr(net)}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = if (net >= 0) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
                )
            }
        }

        // Donut pie chart for category breakdown
        if (categoryBreakdown.isNotEmpty()) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Column(Modifier.padding(16.dp)) {
                    Text("Kategori pengeluaran", fontWeight = FontWeight.SemiBold, style = MaterialTheme.typography.titleSmall)
                    Spacer(Modifier.height(16.dp))
                    Box(modifier = Modifier.align(Alignment.CenterHorizontally)) {
                        DonutChart(
                            segments = categoryBreakdown.map { (cat, amt) ->
                                DonutSegment(cat, amt.toFloat())
                            },
                            modifier = Modifier.size(200.dp)
                        )
                    }
                    Spacer(Modifier.height(16.dp))
                    categoryBreakdown.forEachIndexed { i, (cat, amt) ->
                        val pct = if (totalExpense > 0) (amt.toFloat() / totalExpense * 100).toInt() else 0
                        Row(
                            Modifier.fillMaxWidth().padding(vertical = 4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                                Canvas(Modifier.size(10.dp)) {
                                    drawCircle(categoryColors[i % categoryColors.size])
                                }
                                Text(cat, style = MaterialTheme.typography.bodySmall)
                            }
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Text("$pct%", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.outline)
                                Text(formatIdr(amt), style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Medium)
                            }
                        }
                        LinearProgressIndicator(
                            progress = { if (totalExpense > 0) amt.toFloat() / totalExpense else 0f },
                            modifier = Modifier.fillMaxWidth(),
                            color = categoryColors[i % categoryColors.size]
                        )
                        Spacer(Modifier.height(4.dp))
                    }
                }
            }
        }

        // Dual-line chart (expense vs income by day)
        if (dayData.isNotEmpty()) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Column(Modifier.padding(16.dp)) {
                    Text("Tren harian", fontWeight = FontWeight.SemiBold, style = MaterialTheme.typography.titleSmall)
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            Canvas(Modifier.size(8.dp)) { drawCircle(Color.Red) }
                            Text("Pengeluaran", style = MaterialTheme.typography.labelSmall)
                        }
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            Canvas(Modifier.size(8.dp)) { drawCircle(Color(0xFF4CAF50)) }
                            Text("Pemasukan", style = MaterialTheme.typography.labelSmall)
                        }
                    }
                    Spacer(Modifier.height(8.dp))
                    DualLineChart(
                        dayData = dayData,
                        modifier = Modifier.fillMaxWidth().height(120.dp)
                    )
                }
            }
        }

        // Day-by-day table
        if (dayData.isNotEmpty()) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text("Per hari", fontWeight = FontWeight.SemiBold, style = MaterialTheme.typography.titleSmall)
                    Spacer(Modifier.height(8.dp))
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Tanggal", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                        Text("Keluar", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                        Text("Masuk", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                    }
                    dayData.takeLast(14).reversed().forEach { (day, expense, income) ->
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text(day, style = MaterialTheme.typography.bodySmall)
                            Text(
                                if (expense > 0) formatIdr(expense) else "—",
                                style = MaterialTheme.typography.bodySmall,
                                color = if (expense > 0) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.outline
                            )
                            Text(
                                if (income > 0) formatIdr(income) else "—",
                                style = MaterialTheme.typography.bodySmall,
                                color = if (income > 0) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline
                            )
                        }
                    }
                }
            }
        }

        Spacer(Modifier.height(24.dp))
    }
}

// --- Data helpers ---

data class DayDataPoint(val label: String, val expense: Long, val income: Long)

private fun buildDayData(transactions: List<TransactionEntity>): List<DayDataPoint> {
    val fmt = SimpleDateFormat("dd/MM", Locale.getDefault())
    return transactions
        .groupBy { fmt.format(Date(it.occurredAt)) }
        .map { (day, txs) ->
            DayDataPoint(
                label = day,
                expense = txs.filter { it.type == "expense" }.sumOf { it.amount },
                income = txs.filter { it.type == "income" }.sumOf { it.amount }
            )
        }
        .sortedBy { it.label }
}

// --- Composables ---

@Composable
private fun SummaryCard(label: String, amount: Long, color: Color, modifier: Modifier = Modifier) {
    Card(modifier = modifier, colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
        Column(Modifier.padding(12.dp)) {
            Text(label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(formatIdr(amount), style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = color)
        }
    }
}

// --- Canvas charts ---

data class DonutSegment(val label: String, val value: Float)

private val categoryColors = listOf(
    Color(0xFF6200EE), Color(0xFF03DAC6), Color(0xFFFF5722),
    Color(0xFF4CAF50), Color(0xFF2196F3), Color(0xFFFF9800), Color(0xFF9C27B0)
)

@Composable
private fun DonutChart(segments: List<DonutSegment>, modifier: Modifier = Modifier) {
    val total = segments.sumOf { it.value.toDouble() }.toFloat().takeIf { it > 0 } ?: 1f
    Canvas(modifier = modifier) {
        var startAngle = -90f
        segments.forEachIndexed { i, seg ->
            val sweep = (seg.value / total) * 360f
            drawArc(
                color = categoryColors[i % categoryColors.size],
                startAngle = startAngle,
                sweepAngle = sweep,
                useCenter = false,
                style = Stroke(width = size.minDimension * 0.18f, cap = StrokeCap.Butt),
                topLeft = Offset(size.minDimension * 0.09f, size.minDimension * 0.09f),
                size = Size(size.minDimension * 0.82f, size.minDimension * 0.82f)
            )
            startAngle += sweep
        }
    }
}

@Composable
private fun DualLineChart(dayData: List<DayDataPoint>, modifier: Modifier = Modifier) {
    val maxVal = dayData.maxOf { maxOf(it.expense, it.income) }.toFloat().takeIf { it > 0 } ?: 1f
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val stepX = if (dayData.size > 1) w / (dayData.size - 1) else w

        fun points(selector: (DayDataPoint) -> Long): List<Offset> =
            dayData.mapIndexed { i, d ->
                Offset(i * stepX, h - (selector(d).toFloat() / maxVal) * h * 0.9f)
            }

        fun drawLine(pts: List<Offset>, color: Color) {
            if (pts.size < 2) return
            val path = Path().apply {
                moveTo(pts.first().x, pts.first().y)
                pts.drop(1).forEach { lineTo(it.x, it.y) }
            }
            drawPath(path, color = color, style = Stroke(width = 3f, cap = StrokeCap.Round))
        }

        drawLine(points { it.expense }, Color.Red.copy(alpha = 0.8f))
        drawLine(points { it.income }, Color(0xFF4CAF50).copy(alpha = 0.8f))
    }
}
