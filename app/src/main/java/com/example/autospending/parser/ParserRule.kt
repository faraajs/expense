package com.example.autospending.parser

data class ParserRule(
    val sourcePackage: String,
    val enabled: Boolean = true,
    val directionKeywords: DirectionKeywords = DirectionKeywords(),
    val amountPatterns: List<Regex> = defaultAmountPatterns,
    val ignorePatterns: List<Regex> = defaultIgnorePatterns,
    val categoryHints: Map<String, List<String>> = defaultCategoryHints,
    val transactionIndicators: List<String> = defaultTransactionIndicators,
    val merchantPatterns: List<Regex> = defaultMerchantPatterns
)

data class DirectionKeywords(
    val expense: List<String> = listOf(
        "berhasil bayar",
        "bayar berhasil",
        "baru aja bayar",
        "baru saja bayar",
        "debit",
        "debet",
        "bayar",
        "pembayaran",
        "dibayar",
        "terbayar",
        "pembelian",
        "transaksi",
        "transfer ke",
        "transfer berhasil ke",
        "kirim",
        "terkirim",
        "sent",
        "payment",
        "paid",
        "purchase",
        "withdraw",
        "tarik tunai",
        "spaylater berhasil dibayar",
        "pake gopay",
        "pakai gopay",
        "gopay saldo"
    ),
    val income: List<String> = listOf(
        "dana masuk",
        "uang masuk",
        "kredit",
        "credit",
        // Avoid bare "masuk" — too many false positives on Indonesian copy.
        "saldo masuk",
        "diterima",
        "menerima",
        "transfer dari",
        "transfer berhasil dari",
        "received",
        "refund diterima",
        "bonus cashback",
        "cashback",
        "top up berhasil",
        "isi saldo berhasil",
        "terima gopay",
        "menerima gopay"
    )
)

val defaultAmountPatterns = listOf(
    // -Rp55.000 / +Rp250 / Rp 10.000,00 / Rp.50.000 / IDR 1.000.000
    Regex("""(?i)([+-]?)\s*\b(?:rp|idr)\.?\s*([0-9][0-9.,]*)"""),
    Regex("""(?i)([+-]?)\s*\b([0-9][0-9.,]*)\s*(?:rp|idr)\b""")
)

val defaultIgnorePatterns = listOf(
    Regex("""(?i)\botp\b"""),
    Regex("""(?i)\bkode\b.*\brahasia\b"""),
    Regex("""(?i)\bpassword\b"""),
    Regex("""(?i)\bpin\b"""),
    Regex("""(?i)\bpromo\b"""),
    Regex("""(?i)\bvoucher\b"""),
    Regex("""(?i)\bdiskon\b"""),
    Regex("""(?i)\bjangan\s+berikan\b"""),
    Regex("""(?i)\blogin\b"""),
    Regex("""(?i)\bverifikasi\b"""),
    Regex("""(?i)\btagihan\b(?!.*\b(?:berhasil|dibayar|terbayar|lunas)\b)"""),
    Regex("""(?i)\bjatuh\s+tempo\b"""),
    Regex("""(?i)\bbayar\s+sebelum\b"""),
    Regex("""(?i)\blimit\b(?!.*\b(?:berhasil|dibayar|terbayar|lunas)\b)"""),
    Regex("""(?i)\bajukan\b"""),
    Regex("""(?i)\bpinjaman\b""")
)

val defaultMerchantPatterns = listOf(
    // GoPay: "bayar Rp3 ke itemku pake GoPay Saldo"
    Regex("""(?i)bayar\s+(?:rp|idr)\.?\s*[0-9][0-9.,]*\s+ke\s+([A-Za-z0-9][A-Za-z0-9 ._'&-]{1,40}?)(?:\s+pake|\s+pakai|\s+dengan|\s+via|\s+di|\.|$)"""),
    // SeaBank/Shopee style: "Pembayaran SpayLater kamu" / "Pembayaran MERCHANT kamu"
    Regex("""(?i)Pembayaran\s+([A-Za-z0-9][A-Za-z0-9 ._-]{2,32})\s+kamu"""),
    // Generic: "di ALFAMART" / "ke BUDI" / "dari ANDI"
    Regex("""(?i)\b(?:di|ke|dari|to|from|at)\s+([A-Za-z0-9][A-Za-z0-9 ._'&-]{2,32})"""),
    Regex("""(?i)\bmerchant[:\s]+([A-Za-z0-9][A-Za-z0-9 ._-]{2,32})""")
)

val defaultCategoryHints = mapOf(
    "Food" to listOf("makan", "resto", "restaurant", "kopi", "coffee", "cafe", "gofood", "grabfood"),
    "Transport" to listOf("gojek", "grab", "taxi", "tol", "parkir", "transport"),
    "Groceries" to listOf("mart", "supermarket", "alfamart", "indomaret", "grocery"),
    "Bills" to listOf("pln", "listrik", "air", "pdam", "tagihan", "internet", "pulsa", "spaylater", "paylater"),
    "Shopping" to listOf("shopee", "tokopedia", "lazada", "mall", "belanja"),
    "Transfer" to listOf("transfer", "kirim", "terima")
)

val defaultTransactionIndicators = listOf(
    "berhasil",
    "transaksi",
    "pembayaran",
    "pembelian",
    "bayar",
    "dibayar",
    "terbayar",
    "lunas",
    "debit",
    "debet",
    "kredit",
    "transfer",
    "dana masuk",
    "uang masuk",
    "bonus cashback",
    "cashback",
    "menerima",
    "diterima",
    "sent",
    "paid",
    "payment",
    "purchase",
    "received",
    "withdraw"
)

object DefaultParserRules {
    val rules = listOf(
        ParserRule("com.bca"),
        ParserRule("com.bca.mobile"),
        ParserRule("com.bca.mybca.omni.android"),
        ParserRule("com.bcadigital.blu"),
        ParserRule("id.co.bankbkemobile.digitalbank"),
        ParserRule("id.co.bri.brimo"),
        ParserRule("com.bankmandiri.mandirionline"),
        ParserRule("id.bmri.livin"),
        ParserRule("com.gojek.gopay"),
        ParserRule("com.gojek.app"),
        ParserRule("ovo.id"),
        ParserRule("id.dana"),
        ParserRule("com.shopee.id"),
        ParserRule("com.shopeepay.id"),
        // Extra Indonesian sources commonly used on this device
        ParserRule("com.jago.digitalBanking"),
        ParserRule("com.byu.id"),
        ParserRule("com.telkomsel.telkomselcm"),
        ParserRule("com.grabtaxi.passenger"),
        ParserRule("com.tokopedia.tkpd")
    )
}
