# Catatan pemulihan source

[Kembali ke README](../README.md)

Repository publik ini memuat source **Expense asli**, bukan UI Lab. Source dipulihkan dari snapshot pengembangan sebelumnya dan dipublikasikan dalam root Git bersih, tanpa menyalin riwayat `.git` lama.

## Referensi asal

- Snapshot utama: stash `26e341d5ccd5a93967c1c9a4a8d2d8f0f7cda957`.
- Source yang sebelumnya untracked: parent ketiga stash, dengan prefix objek `453d52e`.
- Referensi VCS APK lama: prefix `598590ee`.
- APK referensi: Expense 0.4.1, versionCode 16, applicationId `com.example.autospending`.
- Publikasi baru: 0.4.2, versionCode 17, applicationId yang sama; skema Room tetap versi 2 dan rilis resmi menggunakan signing key asli yang sama.

Identifier asal tersebut menjelaskan provenance; objek lama tidak harus tersedia dalam repository publik bersih ini. Pemulihan snapshot tidak membuktikan bahwa setiap file cocok dengan source APK referensi, dan **tidak ada klaim rekonstruksi bit-for-bit**. Perubahan toolchain, signing, serta metadata build juga dapat mengubah byte APK.

## Batas publikasi

Database pengguna, ekspor/cadangan pribadi, signing key, password, API key, dan konfigurasi lokal tidak disertakan. Importer transaksi riwayat pribadi dan enrichment pribadi yang tidak aktif dihapus untuk sanitasi publikasi. Penerimaan key melalui launcher intent dihapus, input key UI dimasking, dan log ingest tidak mencetak payload transaksi. Fitur audit lokal tetap menyimpan log sesuai [kebijakan privasi](PRIVACY.md).

MIT dipilih untuk source asli Expense; notice dan lisensi upstream tetap berlaku seperti dijelaskan pada [third-party notices](THIRD-PARTY-NOTICES.md). Source pemulihan bukan alasan untuk menimpa atau menghapus data instalasi lama: ekspor JSON terlebih dahulu lalu pasang APK resmi sebagai pembaruan, bukan uninstall.

## Verifikasi 0.4.2

- `:app:assembleRelease` berhasil dengan JDK 17, Gradle 9.4.1, dan AGP 9.2.1.
- 50 unit test lulus, tanpa failure/error/skip: parser, analitik, ekspor, dan impor.
- APK lolos `apksigner verify`; sertifikat SHA-256 sama dengan APK 0.4.1: `d12d80aacf5249420ed180ab5d73df8c91198361e4a9b2d96a45cad6f6da33aa`.
- Emulator Android 11 / API 30: memasang 0.4.2 di atas APK 0.4.1 berhasil tanpa uninstall. Dua transaksi sintetis tetap identik untuk setiap kolom; identity hash Room v2 tidak berubah.
- Key sintetis terenkripsi tetap utuh setelah update. Extra `gemini_api_key` pada warm launch dan `openrouter_api_key` pada cold launch tidak mengubah isi preference terenkripsi.
- APK diperiksa untuk ketiadaan importer/nama seed pribadi, database, dan keystore; aset lisensi pihak ketiga disertakan.
- Source hasil `git archive` juga berhasil dibangun dengan `:app:assembleDebug --offline`, tanpa konfigurasi signing lokal.
- Instalasi baru 0.4.2 memiliki database kosong. Notifikasi sintetis diterima melalui listener Android; duplikat tidak menambah transaksi dan OTP diabaikan. Logcat hanya mencatat status, bukan isi transaksi.
- Input API key dimasking dan penyimpanan melalui UI berhasil dengan key sintetis. Ekspor JSON melalui UI menghasilkan empat transaksi sintetis yang sesuai database, tanpa `rawText`. Share sheet emulator tidak memiliki aplikasi penerima; pengiriman ke aplikasi lain belum diuji.

Pengujian ini memakai emulator dan data sintetis, bukan database/HP pribadi. Panggilan Gemini dengan API key valid serta pengujian perangkat fisik belum dilakukan. Ini tidak menjamin format notifikasi semua bank/OEM didukung.
