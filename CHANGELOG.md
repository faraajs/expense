# Changelog

[Kembali ke README](README.md)

## 0.4.2

- Publikasi source Expense asli dalam repository bersih dengan lisensi MIT, dokumentasi build, privasi, notice pihak ketiga, dan catatan provenance.
- APK publik tidak menyertakan transaksi/database/cadangan pribadi, key API bawaan, atau signing key di source.
- Sanitasi source: menghapus importer transaksi riwayat pribadi dan enrichment pribadi yang tidak aktif; menghapus penerimaan key melalui launcher intent; memasking input key; membatasi log ingest ke status/sumber paket, bukan teks transaksi.
- Tetap `com.example.autospending`; versionCode naik dari 16 ke 17. Rilis resmi menggunakan signing key asli yang sama. Skema Room tetap versi 2.
- Tidak mengubah publikasi ini menjadi UI Lab. Ledger/parser lokal dan Tanya AI Gemini opsional tetap dibedakan; AI memerlukan key pengguna dan mengirim pertanyaan beserta ringkasan transaksi bulanan ke Google.
- Mengganti model Gemini 1.5/2.0 yang sudah dihentikan dengan `gemini-2.5-flash` dan `gemini-2.5-flash-lite`; lihat [jadwal resmi Google](https://ai.google.dev/gemini-api/docs/deprecations). Panggilan AI dengan key pengguna belum diverifikasi.

**Pembaruan:** ekspor transaksi JSON dahulu, lalu pasang di atas 0.4.1. Jangan uninstall atau hapus data untuk memperbarui. [Unduh APK 0.4.2](https://github.com/faraajs/expense/releases/download/v0.4.2/expense-v0.4.2-universal.apk).

Build release, build debug dari arsip source bersih, dan 50 unit test lulus. Smoke emulator mencakup update tanpa kehilangan transaksi sintetis, instalasi kosong, ingest/deduplikasi notifikasi, perlindungan key intent, dan ekspor JSON. Rincian serta batas pengujian ada di [catatan verifikasi](docs/RECOVERY-NOTES.md#verifikasi-042); panggilan AI dengan key valid dan perangkat fisik belum diuji.

## 0.4.1 — referensi pemulihan

APK Expense asli yang menjadi referensi pemulihan source: versionCode 16, paket `com.example.autospending`, referensi VCS `598590ee`. Tidak ada klaim bahwa source yang dipulihkan mereproduksi byte APK tersebut secara identik; lihat [catatan pemulihan](docs/RECOVERY-NOTES.md).
