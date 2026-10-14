# Kebijakan keamanan

## Versi yang didukung

Perbaikan keamanan ditujukan untuk [rilis stabil terbaru](https://github.com/faraajs/expense/releases/latest) saja. Versi lama tidak menerima backport yang dijanjikan. Sebelum memperbarui, ekspor transaksi ke lokasi aman lalu pasang APK resmi sebagai pembaruan; jangan uninstall atau hapus data ledger.

## Melaporkan kerentanan secara privat

Gunakan [GitHub Private Vulnerability Reporting](https://github.com/faraajs/expense/security/advisories/new) untuk kerentanan, kebocoran data, atau masalah keamanan. Jangan membuka issue/PR publik untuk detail eksploitasi atau data sensitif. Jika pelaporan privat belum tersedia, jangan mempublikasikan detail sensitif; tunggu sampai maintainer mengaktifkannya.

Sertakan versi aplikasi, versi Android, dampak, langkah reproduksi minimal, dan contoh sintetis jika perlu. **Jangan kirim notifikasi bank asli, data ledger, database, ekspor transaksi pribadi, nomor rekening, API key, keystore, atau password** melalui issue publik, screenshot, log, maupun laporan privat. Redaksi bukan pengganti contoh sintetis bila data keuangan dapat direkonstruksi.

Maintainer menilai laporan dan mengoordinasikan perbaikan/pengungkapan sesuai ketersediaan; tidak ada SLA respons atau jaminan semua kerentanan sudah ditemukan. Bug biasa dengan data sintetis dapat dilaporkan melalui template issue.

## Batas jaminan

CI, Android Lint, Dependabot, dan CodeQL membantu peninjauan, bukan audit keamanan menyeluruh. CodeQL memakai build debug terlacak untuk menganalisis Java/Kotlin; hasil tidak mencakup semua perilaku runtime, dependency, atau konfigurasi release. Lihat [cakupan CI dan verifikasi manual](docs/BUILD-FROM-SOURCE.md#ci-dan-verifikasi-manual). Ledger disimpan lokal, tetapi tidak berarti database terenkripsi. [Privasi dan izin](docs/PRIVACY.md) menjelaskan akses notifikasi dan pengiriman data ke Gemini opsional.
