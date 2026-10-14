package com.example.autospending

import com.example.autospending.parser.AmountParseUtils
import com.example.autospending.parser.GoPayParser
import com.example.autospending.parser.NotificationPayload
import com.example.autospending.parser.NotificationParserRegistry
import com.example.autospending.parser.ShopeeParser
import com.google.common.truth.Truth.assertThat
import com.google.common.truth.Truth.assertWithMessage
import org.junit.Test

class ParserTest {

    private fun payload(pkg: String, title: String, text: String) = NotificationPayload(
        packageName = pkg,
        title = title,
        text = text,
        postedAt = System.currentTimeMillis()
    )

    // --- AmountParseUtils ---

    @Test
    fun `extract Rp prefix with dots`() {
        val result = AmountParseUtils.extract("Bayar Rp55.000 berhasil")
        assertThat(result?.value).isEqualTo(55_000L)
    }

    @Test
    fun `extract IDR prefix`() {
        val result = AmountParseUtils.extract("IDR 1.000.000 diterima")
        assertThat(result?.value).isEqualTo(1_000_000L)
    }

    @Test
    fun `normalize strips decimal comma`() {
        assertThat(AmountParseUtils.normalize("55.000,00")).isEqualTo("55000")
    }

    @Test
    fun `normalize strips decimal dot`() {
        assertThat(AmountParseUtils.normalize("55000.00")).isEqualTo("55000")
    }

    @Test
    fun `extract returns null for no amount`() {
        assertThat(AmountParseUtils.extract("Promo cashback tersedia!")).isNull()
    }

    @Test
    fun `signed plus gives income type`() {
        val result = AmountParseUtils.extract("+Rp50.000 masuk")
        assertThat(result?.signedType).isEqualTo("income")
    }

    @Test
    fun `signed minus gives expense type`() {
        val result = AmountParseUtils.extract("-Rp10.000 keluar")
        assertThat(result?.signedType).isEqualTo("expense")
    }

    // --- Non-transaction filtering ---

    @Test
    fun `OTP notification is rejected`() {
        val tx = NotificationParserRegistry.parse(
            payload("com.gojek.gopay", "Kode OTP", "Jangan berikan kode 123456 ke siapapun")
        )
        assertThat(tx).isNull()
    }

    @Test
    fun `promo notification is rejected`() {
        val tx = NotificationParserRegistry.parse(
            payload("id.dana", "Promo spesial!", "Diskon 50% pakai DANA hari ini Rp10.000")
        )
        assertThat(tx).isNull()
    }

    // --- GoPay ---

    @Test
    fun `GoPay expense is parsed`() {
        val tx = NotificationParserRegistry.parse(
            payload(
                "com.gojek.gopay",
                "Bayar ke Alfamart",
                "Kamu baru aja bayar Rp55.000 ke Alfamart pake GoPay Saldo"
            )
        )
        assertThat(tx).isNotNull()
        assertThat(tx!!.type).isEqualTo("expense")
        assertThat(tx.amount).isEqualTo(55_000L)
    }

    @Test
    fun `GoPay income is parsed`() {
        val tx = NotificationParserRegistry.parse(
            payload(
                "com.gojek.gopay",
                "GoPay diterima",
                "Kamu menerima GoPay Rp100.000 dari Budi"
            )
        )
        assertThat(tx).isNotNull()
        assertThat(tx!!.type).isEqualTo("income")
        assertThat(tx.amount).isEqualTo(100_000L)
    }

    @Test
    fun `GoPay dedup family collapses gojek apps`() {
        val parser = GoPayParser()
        assertThat(parser.handles("com.gojek.gopay")).isTrue()
        assertThat(parser.handles("com.gojek.app")).isTrue()
        assertThat(parser.handles("com.grab.taxi")).isFalse()
    }

    // --- Shopee ---

    @Test
    fun `Shopee SpayLater is parsed as expense`() {
        val tx = NotificationParserRegistry.parse(
            payload(
                "com.shopee.id",
                "SpayLater",
                "Pembayaran SpayLater kamu berhasil Rp200.000 dibayar"
            )
        )
        assertThat(tx).isNotNull()
        assertThat(tx!!.type).isEqualTo("expense")
        assertThat(tx.amount).isEqualTo(200_000L)
    }

    @Test
    fun `Shopee merchant extracted as SpayLater`() {
        val parser = ShopeeParser()
        val tx = parser.parse(
            payload(
                "com.shopee.id",
                "SpayLater",
                "Pembayaran SpayLater kamu berhasil Rp200.000 dibayar"
            )
        )
        assertThat(tx?.merchant).isEqualTo("Shopee PayLater")
    }

    // --- Registry ---

    @Test
    fun `registry finds parser for all expected packages`() {
        val packages = listOf(
            "com.gojek.gopay", "com.gojek.app", "com.shopee.id", "com.shopeepay.id",
            "id.dana", "ovo.id", "com.bca", "id.co.bri.brimo",
            "com.bankmandiri.mandirionline", "id.bmri.livin", "com.bcadigital.blu",
            "com.jago.digitalBanking", "com.grabtaxi.passenger", "com.tokopedia.tkpd"
        )
        packages.forEach { pkg ->
            assertWithMessage("Package $pkg should be known")
                .that(NotificationParserRegistry.isKnownPackage(pkg))
                .isTrue()
        }
    }

    @Test
    fun `unknown package returns null from registry`() {
        val tx = NotificationParserRegistry.parse(
            payload("com.random.app", "Something", "Something Rp50.000")
        )
        assertThat(tx).isNull()
    }
}
