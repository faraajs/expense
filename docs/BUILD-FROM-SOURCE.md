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

Ganti contoh dengan lokasi SDK Anda sendiri. Jangan commit `local.properties`, keystore, password, database, atau ekspor pengguna.

## Debug dan unit test

Jalankan dari root repository:

```sh
./gradlew assembleDebug
./gradlew testDebugUnitTest
```

Windows: gunakan `gradlew.bat` dengan task yang sama. APK debug berada di `app/build/outputs/apk/debug/app-debug.apk`; laporan unit test di `app/build/reports/tests/testDebugUnitTest/`. Debug ditandatangani dengan debug key lokal sehingga tidak dapat memperbarui APK resmi yang bertanda tangan release.

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

## Batas reproduksi

Instruksi ini menjelaskan toolchain dan langkah build, bukan jaminan hasil byte-identik dengan APK 0.4.1 atau aset release. Hasil dapat berbeda karena toolchain, signing, dan metadata build. Dokumen ini tidak menyatakan build, unit test, atau smoke test sudah lulus; lihat bukti hasil yang dipublikasikan bersama [rilis](https://github.com/faraajs/expense/releases).
