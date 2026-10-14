package com.example.autospending.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.autospending.ui.analytics.BudgetRingModel
import com.example.autospending.ui.analytics.BudgetStatus
import com.example.autospending.ui.theme.ExpenseRed
import com.example.autospending.ui.theme.IncomeGreen
import com.example.autospending.ui.theme.TealPrimary

@Composable
fun BudgetRing(
    model: BudgetRingModel,
    modifier: Modifier = Modifier,
    ringSize: Dp = 160.dp,
    animate: Boolean = true
) {
    val progress by animateFloatAsState(
        targetValue = model.visualProgress,
        animationSpec = if (animate) {
            tween(600, easing = FastOutSlowInEasing)
        } else {
            tween(0)
        },
        label = "budgetRing"
    )
    val track = MaterialTheme.colorScheme.surfaceVariant
    val arcColor = when (model.status) {
        BudgetStatus.UNSET -> track
        BudgetStatus.AMAN -> IncomeGreen
        BudgetStatus.HATI_HATI -> Color(0xFFF59E0B)
        BudgetStatus.OVER -> ExpenseRed
    }

    Card(modifier = modifier.fillMaxWidth()) {
        Column(
            Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                "Budget bulan ini",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .padding(vertical = 12.dp)
                    .size(ringSize)
            ) {
                Canvas(Modifier.size(ringSize)) {
                    val stroke = 16.dp.toPx()
                    val diameter = size.minDimension - stroke
                    val topLeft = Offset((size.width - diameter) / 2f, (size.height - diameter) / 2f)
                    val arcSize = Size(diameter, diameter)
                    drawArc(
                        color = track,
                        startAngle = -90f,
                        sweepAngle = 360f,
                        useCenter = false,
                        topLeft = topLeft,
                        size = arcSize,
                        style = Stroke(width = stroke, cap = StrokeCap.Round)
                    )
                    drawArc(
                        color = arcColor,
                        startAngle = -90f,
                        sweepAngle = 360f * progress,
                        useCenter = false,
                        topLeft = topLeft,
                        size = arcSize,
                        style = Stroke(width = stroke, cap = StrokeCap.Round)
                    )
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        model.centerLabel,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = if (model.status == BudgetStatus.OVER) ExpenseRed else TealPrimary
                    )
                    Text(
                        model.centerSubLabel,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            val statusText = when (model.status) {
                BudgetStatus.UNSET -> "Status: Belum diset"
                BudgetStatus.AMAN -> "Status: Aman"
                BudgetStatus.HATI_HATI -> "Status: Hati-hati"
                BudgetStatus.OVER -> "Status: Over"
            }
            Text(statusText, style = MaterialTheme.typography.bodyMedium)
            model.overPercent?.let {
                Text(
                    "+$it% di atas budget",
                    color = ExpenseRed,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}
