# Expense

[![Rilis terbaru](https://img.shields.io/github/v/release/faraajs/expense)](https://github.com/faraajs/expense/releases/latest)
[![Source: MIT](https://img.shields.io/badge/source-MIT-green.svg)](LICENSE)
[![Android 8+](https://img.shields.io/badge/Android-8%2B%20(API%2026)-blue.svg)](docs/BUILD-FROM-SOURCE.md)
[![Android CI](https://github.com/faraajs/expense/actions/workflows/android.yml/badge.svg)](https://github.com/faraajs/expense/actions/workflows/android.yml)

## Unduh APK

### [Unduh Expense 0.4.2 — APK universal](https://github.com/faraajs/expense/releases/download/v0.4.2/expense-v0.4.2-universal.apk)

[Halaman rilis 0.4.2](https://github.com/faraajs/expense/releases/tag/v0.4.2) · [Rilis terbaru](https://github.com/faraajs/expense/releases/latest)

[Unduh source 0.4.2 (tar.gz)](https://github.com/faraajs/expense/releases/download/v0.4.2/expense-v0.4.2-source.tar.gz) · [Checksum SHA-256](https://github.com/faraajs/expense/releases/download/v0.4.2/SHA256SUMS.txt)

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

`applicationId` dan namespace source tetap **`com.example.autospending`**, bukan diganti menjadi nama baru demi branding. Identitas yang sudah dirilis ini menjaga hubungan instalasi dengan data ledger, widget, FileProvider, serta komponen/grant akses notification listener. Nama kelas/paket komponen Android juga dipertahankan; namespace bukan sekadar label aplikasi.

## Privasi

APK publik tidak menyertakan transaksi pengguna, database/cadangan pribadi, maupun API key bawaan. Data ledger disimpan lokal; tidak berarti database terenkripsi. Saat Anda memakai Tanya AI, pertanyaan **dan ringkasan ledger bulanan berbentuk baris transaksi** dikirim ke Google. Baca [privasi dan izin](docs/PRIVACY.md) sebelum mengaktifkannya.

## Source dan dokumentasi

- [Build dari source, SDK, dan signing](docs/BUILD-FROM-SOURCE.md)
- [Privasi](docs/PRIVACY.md)
- [Kebijakan keamanan dan pelaporan privat](SECURITY.md)
- [Changelog](CHANGELOG.md)
- [Asal source dan pemulihan](docs/RECOVERY-NOTES.md)
- [Lisensi MIT](LICENSE) · [Lisensi pihak ketiga](docs/THIRD-PARTY-NOTICES.md)

Lisensi MIT berlaku untuk source asli Expense, bukan pelisensian ulang dependency. Rilis publik menambahkan sanitasi privasi untuk publikasi; source tidak menjanjikan rekonstruksi APK lama secara bit-for-bit.

## CI dan kontribusi

[Android CI](https://github.com/faraajs/expense/actions/workflows/android.yml) menjalankan unit test nyata, build APK debug, dan Android Lint pada push `main`, pull request, atau pemicu manual. Laporan dan APK debug tersedia sebagai artefak run; APK itu **bukan rilis resmi** dan tidak dapat memperbarui APK resmi dengan sertifikat berbeda. Badge hanya menunjukkan status run terkait, bukan bukti semua fitur sudah diuji.

[CodeQL](https://github.com/faraajs/expense/actions/workflows/codeql.yml) menganalisis Java/Kotlin melalui build debug terlacak; Dependabot mengusulkan pembaruan Gradle dan GitHub Actions mingguan tanpa auto-merge. CI tidak menjalankan uji perangkat/emulator, akses notifikasi bank nyata, update yang mempertahankan data/grant, widget, atau Gemini dengan key valid. Bedakan bukti manual historis 0.4.2 dari hasil CI pada commit saat ini; lihat [cakupan dan perintah verifikasi](docs/BUILD-FROM-SOURCE.md#ci-dan-verifikasi-manual).

Gunakan template issue/PR dengan **data sintetis saja**, bukan notifikasi bank asli, ledger/ekspor pribadi, atau rahasia. Kerentanan dilaporkan [secara privat](SECURITY.md).
