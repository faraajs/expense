package com.example.autospending.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.autospending.data.formatIdr
import com.example.autospending.ui.analytics.CategorySpend
import com.example.autospending.ui.analytics.DEFAULT_CATEGORIES
import com.example.autospending.ui.theme.TealPrimary
import com.example.autospending.ui.theme.TealSecondary

private val CategoryPalette = listOf(
    Color(0xFFF97316),
    Color(0xFF3B82F6),
    Color(0xFF22C55E),
    Color(0xFFA855F7),
    Color(0xFFEC4899),
    Color(0xFF14B8A6),
    Color(0xFF64748B)
)

private val KnownCategoryColors: Map<String, Color> =
    DEFAULT_CATEGORIES.zip(CategoryPalette).toMap()

@Composable
fun CategoryBreakdown(
    items: List<CategorySpend>,
    selectedCategory: String?,
    onCategoryClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    if (items.isEmpty()) return
    val maxAmount = items.maxOf { it.amount }.coerceAtLeast(1L)
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Text(
            "Kategori",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold
        )
        items.forEach { item ->
            val selected = selectedCategory == item.category
            val barColor = KnownCategoryColors[item.category]
                ?: run {
                    val h = item.category.hashCode().and(0x7FFF_FFFF)
                    Color(
                        red = (80 + h % 120) / 255f,
                        green = (90 + (h / 7) % 100) / 255f,
                        blue = (100 + (h / 13) % 120) / 255f
                    )
                }
            val fraction = (item.amount.toFloat() / maxAmount.toFloat()).coerceIn(0.05f, 1f)
            Column(
                Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .clickable { onCategoryClick(item.category) }
                    .background(
                        if (selected) MaterialTheme.colorScheme.primaryContainer
                        else Color.Transparent
                    )
                    .padding(8.dp)
                    .semantics {
                        contentDescription = "${item.category} ${formatIdr(item.amount)}"
                    }
            ) {
                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        item.category,
                        fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium
                    )
                    Text(formatIdr(item.amount), fontWeight = FontWeight.SemiBold)
                }
                Box(
                    Modifier
                        .fillMaxWidth()
                        .padding(top = 6.dp)
                        .height(10.dp)
                        .clip(RoundedCornerShape(5.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Box(
                        Modifier
                            .fillMaxWidth(fraction)
                            .height(10.dp)
                            .clip(RoundedCornerShape(5.dp))
                            .background(if (selected) TealPrimary else barColor.copy(alpha = 0.9f))
                    )
                }
            }
        }
        if (selectedCategory != null) {
            Text(
                "Filter: $selectedCategory (tap lagi untuk clear)",
                style = MaterialTheme.typography.bodySmall,
                color = TealSecondary
            )
        }
    }
}
