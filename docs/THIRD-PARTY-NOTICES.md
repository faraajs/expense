# Third-party notices

[Back to README](../README.md)

The original Expense source is licensed under [MIT](../LICENSE). Dependencies, build tooling, and the Gradle Wrapper retain their respective upstream licenses. **MIT does not relicense dependencies.** Upstream copyright and notices remain in force.

The complete [Apache-2.0 text](../app/src/main/assets/licenses/Apache-2.0.txt) and [attribution](../app/src/main/assets/licenses/NOTICE.txt) are included in the source and APK assets (`assets/licenses/`).

OkHttp's Public Suffix List data is licensed under **MPL-2.0**, not MIT or Apache. The [upstream notice](../app/src/main/assets/licenses/OkHttp-Public-Suffix-NOTICE.txt), [MPL license](../app/src/main/assets/licenses/MPL-2.0.txt), and [editable rule source](../app/src/main/assets/licenses/public_suffix_list.dat) are included. The rules were extracted without changes from OkHttp 4.12.0's `publicsuffixes.gz`; the library's binary format was not changed.

The Protobuf runtime shaded into Tink retains the [Google BSD-3-Clause license](../app/src/main/assets/licenses/Protobuf-BSD-3-Clause.txt). Upstream Tink 1.5.0 metadata references Protobuf 3.11.4.

## Libraries and tooling

The following versions come from build declarations and upstream metadata, not from a dependency graph report produced by a build. Gradle may select higher transitive versions during resolution. This list is not a claim of a complete inventory of every file in the APK.

| Component | Declared / upstream version | License |
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
| Public Suffix List (data bundled with OkHttp) | OkHttp 4.12.0 | MPL-2.0 |
| Protobuf Java Lite (shaded into Tink) | Tink reference: 3.11.4 | BSD-3-Clause |
| Android Gradle Plugin | 9.2.1 | Apache-2.0 |
| KSP | 2.2.21-2.0.5 | Apache-2.0 |
| Gradle / Gradle Wrapper | 9.4.1 distribution | Apache-2.0 |
| JUnit (unit tests) | 4.13.2 | EPL-1.0 |
| Google Truth (unit tests) | 1.4.2 | Apache-2.0 |
| AndroidX test JUnit extension / Espresso | 1.3.0 / 3.7.0 | Apache-2.0 |
| Compose UI test JUnit4 | 1.7.6 (BOM) | Apache-2.0 |

AndroidX and Compose are developed by the Android Open Source Project and contributors. Kotlin and coroutines are developed by JetBrains and contributors. OkHttp and Okio are developed by Square and contributors. Gradle is developed by its original authors and contributors. This attribution does not imply endorsement.

## License and metadata sources

- [Apache License 2.0, complete text](https://www.apache.org/licenses/LICENSE-2.0.txt)
- [Eclipse Public License 1.0, complete text](https://www.eclipse.org/legal/epl-v10.html)
- [AndroidX source/license](https://android.googlesource.com/platform/frameworks/support/+/androidx-main/LICENSE.txt)
- [Compose BOM 2024.12.01 POM](https://dl.google.com/dl/android/maven2/androidx/compose/compose-bom/2024.12.01/compose-bom-2024.12.01.pom)
- [Security Crypto 1.0.0 POM](https://dl.google.com/dl/android/maven2/androidx/security/security-crypto/1.0.0/security-crypto-1.0.0.pom)
- [Coroutines 1.10.2 POM](https://repo.maven.apache.org/maven2/org/jetbrains/kotlinx/kotlinx-coroutines-android/1.10.2/kotlinx-coroutines-android-1.10.2.pom)
- [OkHttp 4.12.0 POM](https://repo.maven.apache.org/maven2/com/squareup/okhttp3/okhttp/4.12.0/okhttp-4.12.0.pom)
- [Okio 3.6.0 POM](https://repo.maven.apache.org/maven2/com/squareup/okio/okio/3.6.0/okio-3.6.0.pom)
- The Gradle Wrapper includes Apache-2.0 in `gradle/wrapper/gradle-wrapper.jar` → `META-INF/LICENSE`. The [Gradle 9.4.1 distribution](https://services.gradle.org/distributions/gradle-9.4.1-bin.zip) includes the upstream `LICENSE` and `NOTICE`.
- [JUnit 4 license](https://github.com/junit-team/junit4/blob/r4.13.2/LICENSE-junit.txt)

The Apache-2.0 headers in `gradlew` and `gradlew.bat` are retained, including the notice `Copyright © 2015 the original authors.` The Wrapper is not relicensed to MIT. Retain the upstream LICENSE, NOTICE, and headers included in the source, Gradle distribution, and dependency artifacts when redistributing them. Do not remove them merely because the application source is MIT licensed. This list does not replace the license text or notice accompanying each artifact.
