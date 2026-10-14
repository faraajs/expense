# Privasi dan izin

[Kembali ke README](../README.md)

## Data lokal

Expense menyimpan transaksi, anggaran, pengeluaran berulang, kategori, dan log audit notifikasi di database Room lokal. Isi transaksi/log dapat mencakup nominal, waktu, merchant, sumber aplikasi, judul, serta teks notifikasi. Database ledger tidak menggunakan enkripsi database khusus; perlindungannya mengikuti sandbox dan keamanan perangkat Android. APK publik tidak berisi transaksi pengguna, database/cadangan pribadi, atau API key bawaan.

Akses notifikasi memberi aplikasi kemampuan membaca notifikasi perangkat. Parser hanya mengenali sumber/format yang didukung, tetapi izin Android sendiri bersifat luas; jangan mengaktifkannya jika tidak nyaman dengan akses tersebut. Log audit lokal dapat memuat teks sensitif. Worker dijadwalkan setiap hari untuk menghapus log mentah yang lebih tua dari 21 hari; eksekusi bergantung pada penjadwalan Android, bukan jaminan penghapusan tepat waktu. Retensi ini tidak menghapus transaksi ledger atau berkas ekspor.

Backup otomatis Android dinonaktifkan pada konfigurasi aplikasi. Jangan menganggap ini pengganti backup manual atau perlindungan terhadap perangkat yang sudah di-root/terkompromi. Ekspor JSON berisi data transaksi dan dapat menyertakan metadata/teks sensitif; aplikasi tujuan berbagi serta lokasi penyimpanan yang dipilih memiliki kebijakan sendiri. Simpan ekspor aman dan jangan unggah ke issue publik. Ekspor transaksi bukan backup lengkap semua data/pengaturan.

## Gemini opsional: data keluar perangkat

Ledger dan parser dasar tidak membutuhkan internet. Saat pengguna mengirim pertanyaan di Tanya AI, aplikasi menghubungi endpoint HTTPS Google Generative Language API (`generativelanguage.googleapis.com`). Payload berisi instruksi/prompt, pertanyaan pengguna, dan ringkasan ledger bulanan **per transaksi**, termasuk tanggal, tipe pemasukan/pengeluaran, nominal, merchant, dan kategori. Ringkasan ini bukan sekadar total agregat dan bukan data anonim.

Google menerima payload dan API key untuk autentikasi. Penggunaan, penyimpanan, serta pemrosesan data oleh Google mengikuti [ketentuan Gemini API](https://ai.google.dev/gemini-api/terms) dan [kebijakan privasi Google](https://policies.google.com/privacy), termasuk ketentuan yang dapat berbeda menurut layanan/tingkat penggunaan. Jangan kirim data yang tidak ingin dibagikan kepada Google. Menonaktifkan akses notifikasi tidak membatalkan data yang sebelumnya dikirim lewat AI.

Pengguna harus memasukkan key Google AI Studio sendiri yang didukung; ketersediaan model, kuota, dan koneksi menentukan keberhasilan. Tidak ada key bersama atau akses AI gratis yang dijanjikan. Penggunaan AI langsung belum diklaim terverifikasi. Jawaban AI dapat salah dan bukan nasihat keuangan profesional.

Key disimpan melalui `EncryptedSharedPreferences` dengan Android Keystore; ini tidak mengenkripsi ledger. Field key dimasking pada UI. Launcher publik tidak menerima key lewat intent extras. Jangan membagikan key, dan cabut/rotasi lewat penyedia jika bocor.

## Izin dan sideload

- **Notification access:** membaca notifikasi untuk pencatatan transaksi; dapat dicabut melalui pengaturan Android.
- **POST_NOTIFICATIONS:** menampilkan notifikasi aplikasi, misalnya peringatan anggaran; berbeda dari akses membaca notifikasi.
- **INTERNET:** digunakan fitur Gemini opsional.
- **REQUEST_IGNORE_BATTERY_OPTIMIZATIONS:** memungkinkan pengguna mengatur pengecualian baterai untuk reliabilitas listener.
- **RECEIVE_BOOT_COMPLETED:** membantu pemulihan layanan setelah restart/pergantian paket.

Pada Android 13+ yang memblokir aplikasi sideload, buka Settings → Apps → Expense → menu tiga titik → Allow restricted settings, lalu aktifkan Notification access. Menu berbeda menurut perangkat. Hanya berikan akses untuk APK yang dipercaya; tautan resmi ada di [README](../README.md).

## Sanitasi publikasi

Publikasi source menghapus importer riwayat pribadi dan enrichment pribadi yang tidak aktif. Logcat ingest dibatasi pada status/sumber paket, bukan payload transaksi; log audit lokal tetap ada untuk fungsi diagnosis. Tidak ada perubahan skema Room sebagai bagian sanitasi ini.

Untuk berhenti membagikan data ke AI, jangan gunakan Tanya AI dan hapus key melalui pengaturan aplikasi. Untuk menghentikan capture, cabut akses notifikasi. Hapus berkas ekspor terpisah bila tidak diperlukan. Hapus data aplikasi/uninstall menghapus ledger lokal tetapi juga berisiko kehilangan data: **ekspor sebelum tindakan tersebut; jangan uninstall ketika hanya memperbarui versi**.
