package com.example.autospending.data

/**
 * Maps Android package names (and special source keys) to short display names.
 * Extracted from MainActivity for reuse by UI and parser-adjacent code.
 */
fun String.toFriendlySourceName(): String {
    return when {
        equals("manual", ignoreCase = true) -> "Manual"
        contains("id.co.bankbkemobile.digitalbank") -> "SeaBank"
        contains("com.bcadigital.blu") -> "blu"
        contains("com.bca") -> "BCA Mobile"
        contains("id.co.bri.brimo") -> "BRImo"
        contains("bankmandiri") || contains("livin") -> "Livin' by Mandiri"
        contains("com.gojek") || contains("gopay") -> "GoPay"
        contains("ovo.id") -> "OVO"
        contains("id.dana") -> "DANA"
        contains("shopee") -> "ShopeePay"
        else -> this
    }
}

/** Known friendly source labels for filter chips and manual entry. */
val KnownSourceNames: List<String> = listOf(
    "Manual",
    "SeaBank",
    "blu",
    "BCA Mobile",
    "BRImo",
    "Livin' by Mandiri",
    "GoPay",
    "OVO",
    "DANA",
    "ShopeePay"
)
