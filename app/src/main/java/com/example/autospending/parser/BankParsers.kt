package com.example.autospending.parser

/** GoPay & Gojek app — both post the same transaction notif, deduplicated via "com.gojek" family. */
class GoPayParser : BaseBankParser() {
    override val packagePrefixes = listOf("com.gojek.gopay", "com.gojek.app")
    override val dedupFamily = "com.gojek"

    override fun extractMerchant(raw: String): String? {
        // GoPay: "bayar Rp3 ke itemku pake GoPay Saldo"
        Regex("""(?i)bayar\s+(?:rp|idr)\.?\s*[0-9][0-9.,]*\s+ke\s+([A-Za-z0-9][A-Za-z0-9 ._'&-]{1,40}?)(?:\s+pake|\s+pakai|\s+dengan|\s+via|\s+di|\.|$)""")
            .find(raw)?.groupValues?.getOrNull(1)?.trim()?.let { return it.take(40) }
        // Fallback: "ke MERCHANT" pattern
        Regex("""(?i)ke\s+([A-Za-z0-9][A-Za-z0-9 ._'&-]{2,32})""")
            .find(raw)?.groupValues?.getOrNull(1)?.trim()?.let { return it.take(40) }
        return null
    }
}

/** blu by BCA Digital */
class BluParser : BaseBankParser() {
    override val packagePrefixes = listOf("com.bcadigital.blu")
}

/** SeaBank (formerly Bank BKE Mobile) */
class SeaBankParser : BaseBankParser() {
    override val packagePrefixes = listOf("id.co.bankbkemobile.digitalbank")
}

/** Shopee & ShopeePay — same family, deduplicated. */
class ShopeeParser : BaseBankParser() {
    override val packagePrefixes = listOf("com.shopee.id", "com.shopeepay.id")
    override val dedupFamily = "com.shopee"

    override fun extractMerchant(raw: String): String? {
        // "Pembayaran SpayLater kamu" / "Pembayaran MERCHANT kamu"
        Regex("""(?i)Pembayaran\s+([A-Za-z0-9][A-Za-z0-9 ._-]{2,32})\s+kamu""")
            .find(raw)?.groupValues?.getOrNull(1)?.trim()
            ?.let { return if (it.contains("SpayLater", ignoreCase = true)) "Shopee PayLater" else it }
        return null
    }
}

/** Tokopedia / TokopediaPay */
class TokopediaParser : BaseBankParser() {
    override val packagePrefixes = listOf("com.tokopedia.tkpd")
}

/** DANA e-wallet */
class DanaParser : BaseBankParser() {
    override val packagePrefixes = listOf("id.dana")
}

/** OVO e-wallet */
class OvoParser : BaseBankParser() {
    override val packagePrefixes = listOf("ovo.id")
}

/** BCA Mobile / myBCA */
class BcaParser : BaseBankParser() {
    override val packagePrefixes = listOf("com.bca", "com.bca.mobile", "com.bca.mybca.omni.android")
    override val dedupFamily = "com.bca"
}

/** BRImo — BRI mobile banking */
class BrimoParser : BaseBankParser() {
    override val packagePrefixes = listOf("id.co.bri.brimo")
}

/** Mandiri Online (legacy) */
class MandiriOnlineParser : BaseBankParser() {
    override val packagePrefixes = listOf("com.bankmandiri.mandirionline")
    override val dedupFamily = "com.bankmandiri"
}

/** Livin' by Mandiri */
class LivinParser : BaseBankParser() {
    override val packagePrefixes = listOf("id.bmri.livin")
    override val dedupFamily = "com.bankmandiri"
}

/** Bank Jago */
class JagoParser : BaseBankParser() {
    override val packagePrefixes = listOf("com.jago.digitalBanking")
}

/** Grab (GrabPay) */
class GrabParser : BaseBankParser() {
    override val packagePrefixes = listOf("com.grabtaxi.passenger")
}

/** by.U (Telkomsel digital) */
class ByUParser : BaseBankParser() {
    override val packagePrefixes = listOf("com.byu.id")
}

/** Telkomsel MyTelkomsel */
class TelkomselParser : BaseBankParser() {
    override val packagePrefixes = listOf("com.telkomsel.telkomselcm")
}
