package com.example.autospending.data

import java.text.NumberFormat
import java.time.Instant
import java.time.LocalDateTime
import java.time.YearMonth
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

data class MonthWindow(
    val key: String,
    val startMillis: Long,
    val endMillis: Long
)

private val zone: ZoneId = ZoneId.systemDefault()
private val monthFormatter = DateTimeFormatter.ofPattern("yyyy-MM")
private val dateFormatter = DateTimeFormatter.ofPattern("dd MMM yyyy HH:mm", Locale("id", "ID"))

fun currentMonthWindow(): MonthWindow = monthWindow(YearMonth.now(zone))

fun monthWindow(key: String): MonthWindow =
    monthWindow(YearMonth.parse(key, monthFormatter))

fun monthWindow(month: YearMonth): MonthWindow {
    val start = month.atDay(1).atStartOfDay(zone).toInstant().toEpochMilli()
    val end = month.plusMonths(1).atDay(1).atStartOfDay(zone).toInstant().toEpochMilli()
    return MonthWindow(month.format(monthFormatter), start, end)
}

fun formatIdr(amount: Long): String {
    val formatter = NumberFormat.getNumberInstance(Locale("id", "ID"))
    return "Rp ${formatter.format(amount)}"
}

fun formatDateTime(millis: Long): String =
    LocalDateTime.ofInstant(Instant.ofEpochMilli(millis), zone).format(dateFormatter)
