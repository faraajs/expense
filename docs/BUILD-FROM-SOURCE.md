# Build dari source

[Kembali ke README](../README.md)

## Toolchain

Gunakan Gradle Wrapper yang disertakan, bukan Gradle global:

| Komponen | Versi |
| --- | --- |
| JDK / target bytecode Java & Kotlin | 17 |
| Gradle Wrapper | 9.4.1 |
| Android Gradle Plugin | 9.2.1 |
| Kotlin dan Compose compiler plugin | 2.2.21 |
| KSP | 2.2.21-2.0.5 |
| Android compileSdk / targetSdk | 36 / 36 |
| Android minSdk | 26 |
| Compose BOM | 2024.12.01 |
| Room library / skema database | 2.8.4 / 2 |

Install Android SDK command-line tools atau Android Studio dengan dukungan toolchain tersebut. Install **Android SDK Platform 36**, **Build Tools 36.0.0**, dan **Platform Tools** melalui SDK Manager; terima lisensi SDK. Android Studio opsional untuk build terminal. Set `JAVA_HOME` ke JDK 17. Unduhan Gradle/dependency dan SDK membutuhkan internet pada penyiapan awal.

## Checkout dan SDK

```sh
git clone https://github.com/faraajs/expense.git
cd expense
```

Buat `local.properties` pada root checkout (diabaikan Git):

```properties
sdk.dir=/absolute/path/to/Android/Sdk
```

Ganti contoh dengan lokasi SDK Anda sendiri. Alternatif untuk lingkungan CI/terminal: set `ANDROID_HOME` ke lokasi SDK; `local.properties` tidak diperlukan jika SDK tersedia melalui environment. Jangan commit `local.properties`, keystore, password, database, atau ekspor pengguna.

## Debug, unit test, dan lint

Jalankan dari root repository:

```sh
./gradlew --no-daemon --stacktrace --continue :app:testDebugUnitTest :app:assembleDebug :app:lintDebug
```

Windows: gunakan `gradlew.bat` dengan task yang sama. APK debug berada di `app/build/outputs/apk/debug/app-debug.apk`; laporan unit test di `app/build/reports/tests/testDebugUnitTest/`, hasil XML di `app/build/test-results/testDebugUnitTest/`, dan laporan lint di `app/build/reports/lint-results-debug.*`. `--continue` memungkinkan task independen tetap berjalan setelah kegagalan, tetapi kegagalan tetap menghasilkan exit nonzero. Debug ditandatangani dengan debug key lokal sehingga tidak dapat memperbarui APK resmi yang bertanda tangan release.

## CI dan verifikasi manual

Workflow [Android CI](../.github/workflows/android.yml) menjalankan perintah di atas sebagai check **Android checks** pada push `main`, semua pull request, dan `workflow_dispatch`. Runner menyiapkan Temurin JDK 17, Android SDK Platform 36, Build Tools 36.0.0, Platform Tools, dan Gradle Wrapper 9.4.1; lisensi SDK diterima oleh action setup. Checkout bersih tidak memerlukan `local.properties`, properti signing, keystore, API key Gemini, atau data pengguna. SDK/dependency/Gradle tetap memerlukan akses unduh.

CI menjalankan unit test source yang ada untuk parser, analitik, impor, dan ekspor; tidak sekadar memeriksa mock atau wiring. CI mencoba mengunggah laporan test/lint/build walau pemeriksaan gagal, serta APK debug jika berhasil dibuat (retensi 14 hari). Kegagalan setup sebelum laporan dibuat tidak menghasilkan laporan. APK **expense-debug-not-official-release** adalah artefak pengembangan, bukan APK release resmi, bukan bukti signing release, dan bukan jalur pembaruan instalasi berisi ledger.

Workflow [CodeQL](../.github/workflows/codeql.yml) memakai `languages: java-kotlin` dan **`build-mode: manual`**, lalu menjalankan `./gradlew --no-daemon :app:assembleDebug` di antara init dan analyze. Alasannya: [mode `none` hanya menganalisis Java dan melewatkan Kotlin](https://docs.github.com/en/code-security/how-tos/find-and-fix-code-vulnerabilities/manage-your-configuration/codeql-for-compiled-languages), sedangkan Expense memakai Kotlin. CodeQL berjalan pada event yang sama serta jadwal mingguan; hasil ada di tab Security/code scanning. Analisis source debug bukan audit keamanan lengkap atau pembuktian perilaku runtime/dependency/release.

Action workflow dipin ke commit immutable dengan versi di komentar. Token Android CI hanya membaca source; CodeQL mendapat `security-events: write` untuk hasil scan. Tidak ada `pull_request_target`, secret signing, atau key pengguna pada PR. Dependabot membuat PR Gradle/GitHub Actions mingguan; update tetap perlu review dan tidak di-auto-merge.

**Bukan cakupan CI:** instrumented/UI test atau emulator, pemberian grant/restricted settings, notifikasi Android nyata, variasi format bank/OEM/baterai, widget, upgrade APK resmi dengan preservasi ledger/skema/grant, signing release, share sheet ke aplikasi lain, serta panggilan Gemini dengan key valid. Pemeriksaan manual harus memakai data sintetis dan mencatat versi/perangkat, langkah, serta hasil aktual—jangan melabelinya lulus hanya karena CI hijau.

Bukti manual historis untuk 0.4.2 ada di [catatan verifikasi](RECOVERY-NOTES.md#verifikasi-042); itu tidak otomatis memverifikasi commit atau artefak CI baru. Status CI dibuktikan oleh run terkait commit, bukan oleh keberadaan file workflow.

## Release dan signing di luar repository

Simpan keystore milik Anda di luar checkout. Tambahkan properti berikut ke `local.properties` lokal, dengan nilai asli Anda sendiri:

```properties
AUTOSPENDING_KEYSTORE=/absolute/path/outside/repository/release.jks
AUTOSPENDING_KEYSTORE_PASSWORD=<store-password>
AUTOSPENDING_KEY_ALIAS=<key-alias>
AUTOSPENDING_KEY_PASSWORD=<key-password>
```

Nama properti di atas dibaca oleh `app/build.gradle.kts`; placeholder bukan kredensial yang bisa digunakan. Lindungi izin akses berkas ini dan jangan memasukkannya ke log, issue, atau screenshot. Key resmi asli tidak dibagikan di repository. Key Anda sendiri menghasilkan identitas penandatangan berbeda; untuk memperbarui APK resmi harus memakai key resmi yang sama. Jangan uninstall instalasi berisi data untuk mengatasi perbedaan key.

```sh
./gradlew assembleRelease
```

Dengan signing yang dikonfigurasi, output adalah `app/build/outputs/apk/release/app-release.apk`. Tanpa properti signing, output release belum ditandatangani dan bukan APK pembaruan resmi siap pasang. Aset rilis resmi dinamai `expense-v0.4.2-universal.apk` saat dipublikasikan.

Build sendiri tidak memerlukan API key Gemini. Untuk fitur AI, pengguna memasukkan key yang didukung setelah pemasangan. Tidak ada transaksi pengguna atau key bawaan yang diperlukan untuk build.

## Identitas dan arsitektur APK

`applicationId` **dan namespace** tetap `com.example.autospending`. Paket source serta nama komponen `.notifications.SpendingNotificationListenerService`, `.widget.SpendingWidgetProvider`, launcher, dan authority `${applicationId}.fileprovider` dipertahankan. Mengganti identitas demi branding berisiko membuat instalasi/pemilik data baru atau memutus widget dan grant notification listener Android. Pembaruan resmi memerlukan applicationId dan sertifikat asli yang sama, serta kompatibilitas skema data.

APK rilis 0.4.2 yang dipublikasikan sebagai `expense-v0.4.2-universal.apk` mencakup native ABI **`arm64-v8a`, `armeabi-v7a`, `x86`, dan `x86_64`** berdasarkan inspeksi paket rilis. “Universal” di sini berarti empat ABI tersebut, bukan semua ABI/perangkat Android; minSdk tetap 26. Konfigurasi app tidak memfilter ABI atau memisah APK, tetapi dependency transitif dapat menyertakan native library. Saat dependency berubah, periksa APK final lagi sebelum mengklaim cakupan arsitektur.

## Batas reproduksi

Instruksi ini menjelaskan toolchain dan langkah build, bukan jaminan hasil byte-identik dengan APK 0.4.1 atau aset release. Hasil dapat berbeda karena toolchain, signing, dan metadata build. Dokumen ini tidak menyatakan build, unit test, atau smoke test sudah lulus; lihat bukti hasil yang dipublikasikan bersama [rilis](https://github.com/faraajs/expense/releases).
