# Changelog

[Back to README](README.md)

## Unreleased

- 2026-10-30: Reworded documentation and interface text in English, clarified optional Gemini data sharing, and updated the filter summary test for its new label. Parser rules, storage, permissions and published APKs are unchanged.
- Added Android CI for unit tests, debug builds, and lint with JDK 17 and SDK 36. Reports and debug APKs are not official release artifacts. Gradle Wrapper distribution integrity is verified with a pinned SHA-256.
- Added CodeQL Java/Kotlin analysis with a tracked debug build (no-build mode does not analyze Kotlin), weekly Dependabot updates without auto-merge, CODEOWNERS, and issue/PR templates.
- Added a security policy for the latest stable release and private reporting. Real bank notifications, private ledgers, and secrets are prohibited in public issues.
- Documented CI versus manual verification limits and the ABI coverage of APK 0.4.2. Preserved the `applicationId`, namespace, and `com.example.autospending` component identity for ledger data, the widget, and notification listener grants. Versions, dependencies, signing, and app behavior were not changed.

## 0.4.2

- Published the original Expense source in a clean repository with the MIT license, build, privacy, and third-party notice documentation, and provenance notes.
- The public APK contains no private transactions, databases or backups, bundled API key, or signing key from the source.
- Sanitized the source by removing the inactive private history transaction importer and private enrichment, removing key acceptance through launcher intents, masking key input, and limiting ingest logs to package status and source instead of transaction text.
- Kept `com.example.autospending`; versionCode increased from 16 to 17. The official release uses the same original signing key. Room schema remains version 2.
- This publication is not the Lab UI. The local ledger/parser and optional Gemini AI Q&A remain distinct. AI requires the user's key and sends questions together with a monthly transaction summary to Google.
- Replaced the discontinued Gemini 1.5/2.0 models with `gemini-2.5-flash` and `gemini-2.5-flash-lite`; see the [official Google schedule](https://ai.google.dev/gemini-api/docs/deprecations). AI calls with a user key have not been verified.

**Update:** export transactions as JSON first, then install over 0.4.1. Do not uninstall or clear data to update. [Download APK 0.4.2](https://github.com/faraajs/expense/releases/download/v0.4.2/expense-v0.4.2-universal.apk).

The release build, a debug build from the clean source archive, and 50 unit tests passed. Emulator smoke tests covered updates without loss of synthetic transactions, a fresh installation, notification ingest and deduplication, intent key protection, and JSON export. Details and test limits are in the [verification notes](docs/RECOVERY-NOTES.md#verification-042); AI calls with a valid key and physical device testing were not tested.

## 0.4.1, recovery reference

The original Expense APK used as the source recovery reference: versionCode 16, package `com.example.autospending`, VCS reference `598590ee`. There is no claim that the recovered source reproduces that APK byte for byte; see the [recovery notes](docs/RECOVERY-NOTES.md).
