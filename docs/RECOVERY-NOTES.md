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

Catatan ini mendokumentasikan asal dan batas pemulihan, bukan hasil pengujian. Bukti build, unit test, dan smoke test hanya dapat dinyatakan setelah benar-benar dijalankan; lihat [halaman rilis](https://github.com/faraajs/expense/releases) untuk bukti yang dipublikasikan.
