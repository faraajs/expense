package com.example.autospending.parser

/**
 * Single source of truth for bank/e-wallet package prefixes.
 * Used by NLS health tracking, optional diagnostic, and rule matching.
 */
object PackageCatalog {
    val trackedPrefixes: List<String> = listOf(
        "com.gojek",
        "com.bca",
        "com.bcadigital",
        "com.shopee",
        "com.shopeepay",
        "com.tokopedia",
        "id.dana",
        "ovo.",
        "id.co.bri",
        "id.co.bank",
        "id.bmri",
        "com.bankmandiri",
        "com.jago",
        "com.grab",
        "com.telkomsel",
        "com.byu",
        "com.grabtaxi",
    )

    fun isTracked(packageName: String): Boolean =
        trackedPrefixes.any { prefix ->
            packageName.startsWith(prefix) ||
                (prefix.endsWith('.') && packageName.startsWith(prefix.trimEnd('.')))
        }

    fun dedupFamily(packageName: String): String = when {
        packageName.startsWith("com.gojek") -> "com.gojek"
        packageName.startsWith("com.shopee") || packageName.startsWith("com.shopeepay") -> "com.shopee"
        packageName.startsWith("com.bca") || packageName.startsWith("com.bcadigital") -> "com.bca"
        packageName.startsWith("com.bankmandiri") || packageName.startsWith("id.bmri") -> "com.bankmandiri"
        else -> packageName
    }
}
