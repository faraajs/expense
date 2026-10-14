package com.example.autospending.parser

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Test

class SpendingParserTest {
    private val parser = SpendingParser()

    @Test
    fun parsesBcaExpenseNotification() {
        val transaction = parser.parse(
            NotificationPayload(
                packageName = "com.bca.mobile",
                title = "BCA mobile",
                text = "Transaksi debit Rp125.000 di ALFAMART berhasil",
                postedAt = 1_700_000_000_000
            )
        )

        assertNotNull(transaction)
        assertEquals("expense", transaction!!.type)
        assertEquals(125_000, transaction.amount)
        assertEquals("Groceries", transaction.category)
    }

    @Test
    fun parsesDanaIncomeNotification() {
        val transaction = parser.parse(
            NotificationPayload(
                packageName = "id.dana",
                title = "DANA",
                text = "Transfer dari BUDI diterima Rp50.000",
                postedAt = 1_700_000_000_000
            )
        )

        assertNotNull(transaction)
        assertEquals("income", transaction!!.type)
        assertEquals(50_000, transaction.amount)
        assertEquals("Transfer", transaction.category)
    }

    @Test
    fun ignoresOtpNotification() {
        val transaction = parser.parse(
            NotificationPayload(
                packageName = "id.co.bri.brimo",
                title = "BRImo",
                text = "Kode OTP rahasia anda adalah 123456. Jangan berikan ke siapa pun.",
                postedAt = 1_700_000_000_000
            )
        )

        assertNull(transaction)
    }

    @Test
    fun ignoresUnknownSource() {
        val transaction = parser.parse(
            NotificationPayload(
                packageName = "com.chat.app",
                title = "Chat",
                text = "Bayar Rp25.000 nanti malam",
                postedAt = 1_700_000_000_000
            )
        )

        assertNull(transaction)
    }

    @Test
    fun producesSameHashWithinFiveMinuteBucket() {
        val first = parser.parse(
            NotificationPayload(
                packageName = "ovo.id",
                title = "OVO",
                text = "Payment Rp10.000 di COFFEE BAR berhasil",
                postedAt = 1_700_000_000_000
            )
        )
        val second = parser.parse(
            NotificationPayload(
                packageName = "ovo.id",
                title = "OVO",
                text = "Payment Rp10.000 di COFFEE BAR berhasil",
                // Same 2-minute hash bucket as first (parser uses floor(postedAt/120_000)).
                postedAt = 1_700_000_030_000
            )
        )

        assertNotNull(first)
        assertNotNull(second)
        assertEquals(first!!.contentHash, second!!.contentHash)
    }

    @Test
    fun supportsBluAndSeabankPackages() {
        val blu = parser.parse(
            NotificationPayload(
                packageName = "com.bcadigital.blu",
                title = "blu",
                text = "Pembayaran berhasil Rp25.000 di COFFEE SHOP",
                postedAt = 1_700_000_000_000
            )
        )
        val seabank = parser.parse(
            NotificationPayload(
                packageName = "id.co.bankbkemobile.digitalbank",
                title = "SeaBank",
                text = "Uang masuk Rp250.000 dari ANDI",
                postedAt = 1_700_000_000_000
            )
        )

        assertNotNull(blu)
        assertEquals("expense", blu!!.type)
        assertNotNull(seabank)
        assertEquals("income", seabank!!.type)
    }

    @Test
    fun ignoresShopeePromoWithAmount() {
        val transaction = parser.parse(
            NotificationPayload(
                packageName = "com.shopee.id",
                title = "Shopee",
                text = "Voucher diskon Rp50.000 buat kamu hari ini",
                postedAt = 1_700_000_000_000
            )
        )

        assertNull(transaction)
    }

    @Test
    fun ignoresUnpaidSpaylaterBillReminder() {
        val transaction = parser.parse(
            NotificationPayload(
                packageName = "com.shopee.id",
                title = "SPayLater",
                text = "Tagihan SPayLater Rp100.000 jatuh tempo besok. Bayar sebelum tanggal 5.",
                postedAt = 1_700_000_000_000
            )
        )

        assertNull(transaction)
    }

    @Test
    fun parsesPaidSpaylaterNotification() {
        val transaction = parser.parse(
            NotificationPayload(
                packageName = "com.shopee.id",
                title = "SPayLater",
                text = "Tagihan SPayLater berhasil dibayar Rp100.000",
                postedAt = 1_700_000_000_000
            )
        )

        assertNotNull(transaction)
        assertEquals("expense", transaction!!.type)
        assertEquals(100_000, transaction.amount)
    }

    @Test
    fun parsesRupiahWithDecimalSuffixCorrectly() {
        val transaction = parser.parse(
            NotificationPayload(
                packageName = "com.shopeepay.id",
                title = "ShopeePay",
                text = "Pembayaran berhasil Rp10.000,00 di SHOPEE",
                postedAt = 1_700_000_000_000
            )
        )

        assertNotNull(transaction)
        assertEquals(10_000, transaction!!.amount)
    }

    @Test
    fun parsesShopeePayPaymentWithNegativeSign() {
        val transaction = parser.parse(
            NotificationPayload(
                packageName = "com.shopeepay.id",
                title = "Pembayaran",
                text = "Pecel Lele Cak Gondrong -Rp55.000",
                postedAt = 1_700_000_000_000
            )
        )

        assertNotNull(transaction)
        assertEquals("expense", transaction!!.type)
        assertEquals(55_000, transaction.amount)
    }

    @Test
    fun parsesShopeePayCashbackWithPositiveSign() {
        val transaction = parser.parse(
            NotificationPayload(
                packageName = "com.shopeepay.id",
                title = "Bonus Cashback",
                text = "Dari ShopeePay +Rp250",
                postedAt = 1_700_000_000_000
            )
        )

        assertNotNull(transaction)
        assertEquals("income", transaction!!.type)
        assertEquals(250, transaction.amount)
    }

    @Test
    fun parsesRealGoPayBayarNotification() {
        val transaction = parser.parse(
            NotificationPayload(
                packageName = "com.gojek.gopay",
                title = "Pembayaran berhasil",
                text = "Kamu baru aja bayar Rp3 ke itemku pake GoPay Saldo.",
                postedAt = 1_700_000_000_000
            )
        )

        assertNotNull(transaction)
        assertEquals("expense", transaction!!.type)
        assertEquals(3, transaction.amount)
        assertEquals("itemku", transaction.merchant)
    }

    @Test
    fun parsesGoJekAppPackageSameCopy() {
        val transaction = parser.parse(
            NotificationPayload(
                packageName = "com.gojek.app",
                title = "Pembayaran berhasil",
                text = "Kamu baru aja bayar Rp12.500 ke Kopi Kenangan pake GoPay Saldo.",
                postedAt = 1_700_000_000_000
            )
        )

        assertNotNull(transaction)
        assertEquals(12_500, transaction!!.amount)
        assertEquals("Kopi Kenangan", transaction.merchant)
    }

    @Test
    fun gojekDualPackageSameMinuteSameHash() {
        val a = parser.parse(
            NotificationPayload(
                packageName = "com.gojek.app",
                title = "Pembayaran berhasil",
                text = "Kamu baru aja bayar Rp3 ke itemku pake GoPay Saldo.",
                postedAt = 1_700_000_000_000
            )
        )
        val b = parser.parse(
            NotificationPayload(
                packageName = "com.gojek.gopay",
                title = "Pembayaran berhasil",
                text = "Kamu baru aja bayar Rp3 ke itemku pake GoPay Saldo.",
                postedAt = 1_700_000_000_000
            )
        )
        assertNotNull(a)
        assertNotNull(b)
        assertEquals(a!!.contentHash, b!!.contentHash)
    }
}
