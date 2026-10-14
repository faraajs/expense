# Expense

[![Rilis terbaru](https://img.shields.io/github/v/release/faraajs/expense)](https://github.com/faraajs/expense/releases/latest)
[![Source: MIT](https://img.shields.io/badge/source-MIT-green.svg)](LICENSE)
[![Android 8+](https://img.shields.io/badge/Android-8%2B%20(API%2026)-blue.svg)](docs/BUILD-FROM-SOURCE.md)

## Unduh APK

### [Unduh Expense 0.4.2 — APK universal](https://github.com/faraajs/expense/releases/download/v0.4.2/expense-v0.4.2-universal.apk)

[Halaman rilis 0.4.2](https://github.com/faraajs/expense/releases/tag/v0.4.2) · [Rilis terbaru](https://github.com/faraajs/expense/releases/latest)

Expense adalah pencatat pemasukan/pengeluaran Android dengan parser notifikasi bank dan dompet digital. Ini publikasi **aplikasi Expense asli**, bukan UI Lab. Android 8.0 / API 26 atau lebih baru diperlukan.

## Fitur

- Pencatatan transaksi dari notifikasi sumber yang didukung; hasil bergantung pada format notifikasi bank/dompet digital.
- Riwayat dan filter transaksi, ringkasan bulanan, serta analisis per kategori.
- Anggaran bulanan dan peringatan, pengeluaran berulang, serta kategori khusus.
- Ekspor/impor transaksi JSON, log notifikasi untuk audit, dan widget ringkasan.
- Tanya AI opsional melalui Gemini dengan API key milik pengguna.

Ledger dan parser dasar berjalan tanpa internet. Gemini memerlukan internet, key Google AI Studio yang didukung, kuota, dan model yang masih tersedia. Penggunaan Gemini langsung belum diklaim terverifikasi; jawaban AI bukan nasihat keuangan profesional.

## Instalasi dan pembaruan

1. Sebelum memperbarui, **ekspor transaksi ke JSON** melalui pengaturan dan simpan di lokasi aman. Ekspor transaksi bukan cadangan lengkap semua pengaturan/anggaran/key.
2. Unduh APK dari tautan di atas. Izinkan pemasangan dari sumber ini untuk browser/pengelola berkas bila Android meminta.
3. Untuk **0.4.1 → 0.4.2, pasang sebagai pembaruan; jangan uninstall**. Paket tetap `com.example.autospending`, memakai kunci penandatangan asli yang sama, dan skema database Room tetap versi **2** (berbeda dari versi library Room). Uninstall atau hapus data dapat menghilangkan ledger.
4. Aktifkan **Notification access / Akses notifikasi** untuk Expense agar notifikasi transaksi bisa dibaca. Ini berbeda dari izin menampilkan notifikasi untuk peringatan anggaran.
5. Pada Android 13+ atau perangkat yang memblokir akses aplikasi sideload: buka **Settings → Apps → Expense → menu tiga titik → Allow restricted settings / Izinkan setelan dibatasi**, lalu aktifkan akses notifikasi. Nama/menu dapat berbeda menurut produsen. Hanya lakukan untuk APK dari sumber yang Anda percaya.
6. Jika pencatatan berhenti di latar belakang, periksa panduan baterai/OEM di aplikasi dan pengaturan pembatasan baterai perangkat.

Jika Android menolak pembaruan karena sertifikat berbeda, jangan uninstall untuk memaksa pemasangan: periksa sumber APK terlebih dahulu. Build sendiri dengan key lain tidak dapat menggantikan APK resmi.

## Privasi

APK publik tidak menyertakan transaksi pengguna, database/cadangan pribadi, maupun API key bawaan. Data ledger disimpan lokal; tidak berarti database terenkripsi. Saat Anda memakai Tanya AI, pertanyaan **dan ringkasan ledger bulanan berbentuk baris transaksi** dikirim ke Google. Baca [privasi dan izin](docs/PRIVACY.md) sebelum mengaktifkannya.

## Source dan dokumentasi

- [Build dari source, SDK, dan signing](docs/BUILD-FROM-SOURCE.md)
- [Privasi](docs/PRIVACY.md)
- [Changelog](CHANGELOG.md)
- [Asal source dan pemulihan](docs/RECOVERY-NOTES.md)
- [Lisensi MIT](LICENSE) · [Lisensi pihak ketiga](docs/THIRD-PARTY-NOTICES.md)

Lisensi MIT berlaku untuk source asli Expense, bukan pelisensian ulang dependency. Rilis publik menambahkan sanitasi privasi untuk publikasi; source tidak menjanjikan rekonstruksi APK lama secara bit-for-bit.
