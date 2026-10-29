# Source recovery notes

[Back to README](../README.md)

This public repository contains the **original Expense source**, not the Lab UI. The source was recovered from an earlier development snapshot and published in a clean Git root without copying the old `.git` history.

## Provenance reference

- Main snapshot: stash `26e341d5ccd5a93967c1c9a4a8d2d8f0f7cda957`.
- Previously untracked source: the stash's third parent, with object prefix `453d52e`.
- VCS reference for the old APK: prefix `598590ee`.
- Reference APK: Expense 0.4.1, versionCode 16, applicationId `com.example.autospending`.
- New publication: 0.4.2, versionCode 17, with the same applicationId. Room schema remains version 2 and the official release uses the same original signing key.

These source identifiers explain provenance. The old objects do not need to be available in this clean public repository. Recovering the snapshot does not prove that every file matches the reference APK source, and there is **no claim of bit-for-bit reconstruction**. Toolchain, signing, and build metadata changes can also change APK bytes.

## Publication limits

User databases, private exports or backups, signing keys, passwords, API keys, and local configuration are not included. The inactive private history transaction importer and private enrichment were removed for publication sanitization. Key acceptance through launcher intents was removed, key input is masked in the UI, and ingest logs do not print transaction payloads. The local audit feature still stores logs as described in the [privacy policy](PRIVACY.md).

MIT was chosen for the original Expense source. Upstream notices and licenses remain in force as described in the [third-party notices](THIRD-PARTY-NOTICES.md). Recovered source is not a reason to overwrite or delete data from an old installation. Export JSON first, then install the official APK as an update rather than uninstalling.

<a id="verifikasi-042"></a>

## Verification 0.4.2

- `:app:assembleRelease` succeeded with JDK 17, Gradle 9.4.1, and AGP 9.2.1.
- 50 unit tests passed with no failures, errors, or skips: parser, analytics, export, and import.
- The APK passed `apksigner verify`. Its SHA-256 certificate matched APK 0.4.1: `d12d80aacf5249420ed180ab5d73df8c91198361e4a9b2d96a45cad6f6da33aa`.
- On an Android 11 / API 30 emulator, installing 0.4.2 over APK 0.4.1 succeeded without uninstalling. Two synthetic transactions remained identical in every column, and the Room v2 identity hash did not change.
- The encrypted synthetic key remained intact after the update. The `gemini_api_key` extra on warm launch and the `openrouter_api_key` extra on cold launch did not change the encrypted preference contents.
- The APK was checked for the absence of private importer or seed names, databases, and keystores. Third-party license assets were included.
- The source produced by `git archive` also built successfully with `:app:assembleDebug --offline`, without local signing configuration.
- A fresh 0.4.2 installation had an empty database. Synthetic notifications were received through the Android listener. Duplicates did not add transactions and OTPs were ignored. Logcat recorded status only, not transaction contents.
- API key input was masked and UI storage succeeded with a synthetic key. JSON export through the UI produced four synthetic transactions matching the database, without `rawText`. The emulator share sheet had no receiving app. Sending to another app was not tested.

These tests used an emulator and synthetic data, not a personal database or phone. Gemini calls with a valid API key and physical device testing were not performed. This does not guarantee that all bank or OEM notification formats are supported.
