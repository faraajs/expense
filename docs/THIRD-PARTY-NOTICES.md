# Third-party notices

[Kembali ke README](../README.md)

Source asli Expense berlisensi [MIT](../LICENSE). Dependency, build tooling, serta Gradle Wrapper tetap memakai lisensi upstream masing-masing; **MIT tidak melisensikan ulang dependency**. Copyright dan notice upstream tetap berlaku.

Teks lengkap [Apache-2.0](../app/src/main/assets/licenses/Apache-2.0.txt) dan [attribution](../app/src/main/assets/licenses/NOTICE.txt) disertakan di source serta aset APK (`assets/licenses/`).

Data Public Suffix List milik OkHttp berlisensi **MPL-2.0**, bukan MIT/Apache. [Notice upstream](../app/src/main/assets/licenses/OkHttp-Public-Suffix-NOTICE.txt), [lisensi MPL](../app/src/main/assets/licenses/MPL-2.0.txt), dan [source aturan yang dapat diedit](../app/src/main/assets/licenses/public_suffix_list.dat) ikut disertakan. Aturan diekstrak tanpa perubahan dari `publicsuffixes.gz` milik OkHttp 4.12.0; format biner library tidak diubah.

Runtime Protobuf yang di-shade ke Tink mempertahankan [lisensi BSD-3-Clause Google](../app/src/main/assets/licenses/Protobuf-BSD-3-Clause.txt); referensi versi upstream Tink 1.5.0 mematok Protobuf 3.11.4.

## Library dan tooling

Versi berikut berasal dari deklarasi build dan metadata upstream, bukan laporan dependency graph hasil build. Gradle dapat memilih versi transitive yang lebih tinggi saat resolusi; daftar ini bukan klaim inventaris lengkap semua berkas di APK.

| Komponen | Versi yang dideklarasikan / upstream | Lisensi |
| --- | --- | --- |
| AndroidX Activity Compose | 1.9.3 | Apache-2.0 |
| AndroidX Core KTX | 1.15.0 | Apache-2.0 |
| AndroidX Lifecycle runtime KTX / ViewModel Compose | 2.8.7 | Apache-2.0 |
| AndroidX Navigation Compose | 2.8.5 | Apache-2.0 |
| AndroidX Room runtime / KTX / compiler | 2.8.4 | Apache-2.0 |
| AndroidX WorkManager runtime KTX | 2.10.0 | Apache-2.0 |
| AndroidX Security Crypto | 1.0.0 | Apache-2.0 |
| Compose BOM | 2024.12.01 | Apache-2.0 |
| Compose UI, tooling/preview, Material icons extended | 1.7.6 (BOM) | Apache-2.0 |
| Compose Material 3 | 1.3.1 (BOM) | Apache-2.0 |
| Kotlin / Compose compiler Gradle plugins | 2.2.21 | Apache-2.0 |
| kotlinx.coroutines Android / test | 1.10.2 | Apache-2.0 |
| OkHttp | 4.12.0 | Apache-2.0 |
| Okio / Okio JVM (OkHttp transitive metadata) | 3.6.0 | Apache-2.0 |
| Tink Android (Security Crypto transitive metadata) | 1.5.0 | Apache-2.0 |
| Public Suffix List (data yang dibundel OkHttp) | OkHttp 4.12.0 | MPL-2.0 |
| Protobuf Java Lite (di-shade ke Tink) | referensi Tink: 3.11.4 | BSD-3-Clause |
| Android Gradle Plugin | 9.2.1 | Apache-2.0 |
| KSP | 2.2.21-2.0.5 | Apache-2.0 |
| Gradle / Gradle Wrapper | 9.4.1 distribution | Apache-2.0 |
| JUnit (unit tests) | 4.13.2 | EPL-1.0 |
| Google Truth (unit tests) | 1.4.2 | Apache-2.0 |
| AndroidX test JUnit extension / Espresso | 1.3.0 / 3.7.0 | Apache-2.0 |
| Compose UI test JUnit4 | 1.7.6 (BOM) | Apache-2.0 |

AndroidX/Compose dikembangkan oleh Android Open Source Project dan kontributor; Kotlin/coroutines oleh JetBrains dan kontributor; OkHttp/Okio oleh Square dan kontributor; Gradle oleh penulis aslinya dan kontributor. Attribution ini tidak menyiratkan endorsement.

## Sumber lisensi dan metadata

- [Apache License 2.0 — teks lengkap](https://www.apache.org/licenses/LICENSE-2.0.txt)
- [Eclipse Public License 1.0 — teks lengkap](https://www.eclipse.org/legal/epl-v10.html)
- [AndroidX source/license](https://android.googlesource.com/platform/frameworks/support/+/androidx-main/LICENSE.txt)
- [Compose BOM 2024.12.01 POM](https://dl.google.com/dl/android/maven2/androidx/compose/compose-bom/2024.12.01/compose-bom-2024.12.01.pom)
- [Security Crypto 1.0.0 POM](https://dl.google.com/dl/android/maven2/androidx/security/security-crypto/1.0.0/security-crypto-1.0.0.pom)
- [Coroutines 1.10.2 POM](https://repo.maven.apache.org/maven2/org/jetbrains/kotlinx/kotlinx-coroutines-android/1.10.2/kotlinx-coroutines-android-1.10.2.pom)
- [OkHttp 4.12.0 POM](https://repo.maven.apache.org/maven2/com/squareup/okhttp3/okhttp/4.12.0/okhttp-4.12.0.pom)
- [Okio 3.6.0 POM](https://repo.maven.apache.org/maven2/com/squareup/okio/okio/3.6.0/okio-3.6.0.pom)
- Gradle Wrapper menyertakan Apache-2.0 pada `gradle/wrapper/gradle-wrapper.jar` → `META-INF/LICENSE`. [Distribusi Gradle 9.4.1](https://services.gradle.org/distributions/gradle-9.4.1-bin.zip) menyertakan `LICENSE` dan `NOTICE` upstream.
- [JUnit 4 license](https://github.com/junit-team/junit4/blob/r4.13.2/LICENSE-junit.txt)

Header Apache-2.0 pada `gradlew` dan `gradlew.bat` dipertahankan, termasuk notice `Copyright © 2015 the original authors.` Wrapper bukan karya yang diubah lisensinya ke MIT. Pertahankan LICENSE/NOTICE/header yang disertakan upstream dalam source, distribusi Gradle, dan artefak dependency saat mendistribusikan ulang; jangan membuangnya hanya karena source aplikasi berlisensi MIT. Daftar di atas tidak menggantikan teks lisensi atau notice yang menyertai setiap artefak.
