# Build from source

[Back to README](../README.md)

## Toolchain

Use the included Gradle Wrapper rather than a global Gradle installation:

| Component | Version |
| --- | --- |
| JDK / Java and Kotlin bytecode target | 17 |
| Gradle Wrapper | 9.4.1 |
| Android Gradle Plugin | 9.2.1 |
| Kotlin and Compose compiler plugin | 2.2.21 |
| KSP | 2.2.21-2.0.5 |
| Android compileSdk / targetSdk | 36 / 36 |
| Android minSdk | 26 |
| Compose BOM | 2024.12.01 |
| Room library / database schema | 2.8.4 / 2 |

Install Android SDK command-line tools or Android Studio with support for this toolchain. Through SDK Manager, install **Android SDK Platform 36**, **Build Tools 36.0.0**, and **Platform Tools**, then accept the SDK licenses. Android Studio is optional for terminal builds. Set `JAVA_HOME` to JDK 17. Gradle, dependency, and SDK downloads require internet access during initial setup.

## Checkout and SDK

```sh
git clone https://github.com/faraajs/expense.git
cd expense
```

Create `local.properties` at the checkout root. Git ignores this file:

```properties
sdk.dir=/absolute/path/to/Android/Sdk
```

Replace the example with your own SDK location. Alternatively, for CI or terminal environments, set `ANDROID_HOME` to the SDK location. `local.properties` is not required when the SDK is available through the environment. Do not commit `local.properties`, keystores, passwords, databases, or user exports.

## Debug, unit tests, and lint

Run from the repository root:

```sh
./gradlew --no-daemon --stacktrace --continue :app:testDebugUnitTest :app:assembleDebug :app:lintDebug
```

On Windows, use `gradlew.bat` with the same tasks. The debug APK is at `app/build/outputs/apk/debug/app-debug.apk`; unit test reports are in `app/build/reports/tests/testDebugUnitTest/`, XML results are in `app/build/test-results/testDebugUnitTest/`, and lint reports are in `app/build/reports/lint-results-debug.*`. `--continue` lets independent tasks continue after a failure, but a failure still produces a nonzero exit code. Debug builds use a local debug key and therefore cannot update the official release-signed APK.

## CI and manual verification

The [Android CI](../.github/workflows/android.yml) workflow runs the command above as the **Android checks** on pushes to `main`, all pull requests, and `workflow_dispatch`. The runner sets up Temurin JDK 17, Android SDK Platform 36, Build Tools 36.0.0, Platform Tools, and Gradle Wrapper 9.4.1. The setup action accepts SDK licenses. A clean checkout does not need `local.properties`, signing properties, a keystore, a Gemini API key, or user data. SDK, dependency, and Gradle downloads still require network access.

CI runs the existing source unit tests for the parser, analytics, import, and export. It does not merely check mocks or wiring. CI attempts to upload test, lint, and build reports even when a check fails, and uploads the debug APK if it is built successfully, with 14-day retention. A setup failure before reports are created produces no reports. The **expense-debug-not-official-release** APK is a development artifact, not the official release APK, not evidence of release signing, and not an update path for an installation containing a ledger.

The [CodeQL](../.github/workflows/codeql.yml) workflow uses `languages: java-kotlin` and **`build-mode: manual`**, then runs `./gradlew --no-daemon :app:assembleDebug` between initialization and analysis. The reason is that [the `none` mode analyzes only Java and skips Kotlin](https://docs.github.com/en/code-security/how-tos/find-and-fix-code-vulnerabilities/manage-your-configuration/codeql-for-compiled-languages), while Expense uses Kotlin. CodeQL runs on the same events and on a weekly schedule. Results appear in the Security/code scanning tab. Debug source analysis is not a complete security audit or proof of runtime, dependency, or release behavior.

Workflow actions are pinned to immutable commits with versions in comments. The Android CI token only reads source. CodeQL has `security-events: write` for scan results. There is no `pull_request_target`, signing secret, or user key on pull requests. Dependabot creates weekly Gradle and GitHub Actions PRs. Updates still require review and are not auto-merged.

**Not covered by CI:** instrumented or UI tests and emulator tests, granting restricted settings, real Android notifications, bank/OEM/battery format variations, widgets, official APK upgrades that preserve the ledger, schema, and grants, release signing, sharing to another app through the share sheet, and Gemini calls with a valid key. Manual checks must use synthetic data and record the version or device, steps, and actual results. Do not label them as passed only because CI is green.

Historical manual evidence for 0.4.2 is in the [verification notes](RECOVERY-NOTES.md#verification-042). It does not automatically verify a current commit or new CI artifact. CI status is proved by the run for the relevant commit, not by the presence of a workflow file.

## Release and signing outside the repository

Keep your keystore outside the checkout. Add the following properties to your local `local.properties`, using your own real values:

```properties
AUTOSPENDING_KEYSTORE=/absolute/path/outside/repository/release.jks
AUTOSPENDING_KEYSTORE_PASSWORD=<store-password>
AUTOSPENDING_KEY_ALIAS=<key-alias>
AUTOSPENDING_KEY_PASSWORD=<key-password>
```

The names above are read by `app/build.gradle.kts`; placeholders are not usable credentials. Protect the file permissions and do not include these values in logs, issues, or screenshots. The original official key is not shared in the repository. Your own key produces a different signing identity. Updating the official APK requires the same official key. Do not uninstall an installation containing data to work around a key mismatch.

```sh
./gradlew assembleRelease
```

With signing configured, the output is `app/build/outputs/apk/release/app-release.apk`. Without signing properties, the release output is unsigned and is not an official APK ready for installation or updating. Official release assets are named `expense-v0.4.2-universal.apk` when published.

A local build does not require a Gemini API key. For the AI feature, users enter a supported key after installation. No user transactions or bundled key are needed for the build.

## APK identity and architecture

The `applicationId` **and namespace** remain `com.example.autospending`. The source package and component names `.notifications.SpendingNotificationListenerService`, `.widget.SpendingWidgetProvider`, the launcher, and the `${applicationId}.fileprovider` authority are retained. Changing the identity for branding risks creating a new installation or data owner, or breaking the Android widget and notification listener grant. An official update requires the same original applicationId and certificate, along with compatible data schema.

The published 0.4.2 release APK, `expense-v0.4.2-universal.apk`, includes native ABIs **`arm64-v8a`, `armeabi-v7a`, `x86`, and `x86_64`** based on inspection of the release package. “Universal” here means those four ABIs, not every Android ABI or device. The app configuration does not filter or split ABIs, but transitive dependencies may include native libraries. When dependencies change, inspect the final APK again before claiming architecture coverage.

## Reproduction limits

These instructions describe the toolchain and build steps, not a guarantee of byte-identical output to APK 0.4.1 or the release assets. Results may differ because of the toolchain, signing, and build metadata. This document does not state that the build, unit tests, or smoke tests have passed. See the evidence published with the [release](https://github.com/faraajs/expense/releases).
