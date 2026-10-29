# Expense

[![Latest release](https://img.shields.io/github/v/release/faraajs/expense)](https://github.com/faraajs/expense/releases/latest)
[![Source: MIT](https://img.shields.io/badge/source-MIT-green.svg)](LICENSE)
[![Android 8+](https://img.shields.io/badge/Android-8%2B%20(API%2026)-blue.svg)](docs/BUILD-FROM-SOURCE.md)
[![Android CI](https://github.com/faraajs/expense/actions/workflows/android.yml/badge.svg)](https://github.com/faraajs/expense/actions/workflows/android.yml)

## Download the APK

### [Download Expense 0.4.2, universal APK](https://github.com/faraajs/expense/releases/download/v0.4.2/expense-v0.4.2-universal.apk)

[Expense 0.4.2 release page](https://github.com/faraajs/expense/releases/tag/v0.4.2) · [Latest release](https://github.com/faraajs/expense/releases/latest)

[Download source 0.4.2 (tar.gz)](https://github.com/faraajs/expense/releases/download/v0.4.2/expense-v0.4.2-source.tar.gz) · [SHA-256 checksum](https://github.com/faraajs/expense/releases/download/v0.4.2/SHA256SUMS.txt)

Expense is an Android income and expense tracker with parsers for bank and digital wallet notifications. This is a publication of the **original Expense app**, not the Lab UI. Android 8.0 / API 26 or later is required.

## Features

- Record transactions from supported notification sources. Results depend on the bank or digital wallet notification format.
- Browse and filter transactions, view monthly summaries, and analyze spending by category.
- Set monthly budgets and alerts, track recurring expenses, and create custom categories.
- Export and import transactions as JSON, inspect notification logs for auditing, and use a summary widget.
- Ask optional questions through Gemini with your own API key.

The ledger and basic parser work without internet access. Gemini requires internet access, a supported Google AI Studio key, quota, and an available model. Direct Gemini use has not been verified; AI responses are not professional financial advice.

## Installation and updates

1. Before updating, **export transactions to JSON** from Settings and save the file somewhere safe. A transaction export is not a complete backup of all settings, budgets, or keys.
2. Download the APK from the links above. If Android asks, allow installation from this source for your browser or file manager.
3. For **0.4.1 to 0.4.2, install as an update; do not uninstall**. The package remains `com.example.autospending`, uses the same original signing key, and keeps Room database schema version **2** (which is different from the Room library version). Uninstalling or clearing data can remove the ledger.
4. Enable **Notification access** for Expense so it can read transaction notifications. This is separate from permission to display notifications for budget alerts.
5. On Android 13+ or devices that block access for sideloaded apps, open **Settings → Apps → Expense → three-dot menu → Allow restricted settings**, then enable notification access. Names and menus may differ by manufacturer. Do this only for APKs from a source you trust.
6. If background recording stops, check the app's battery and OEM guidance and the device's battery restriction settings.

If Android rejects the update because the certificate differs, do not uninstall to force installation. Check the APK source first. A build signed with a different key cannot replace the official APK.

The source `applicationId` and namespace remain **`com.example.autospending`** rather than being changed to a new branding name. This released identity preserves the installation relationship with ledger data, the widget, FileProvider, and notification listener components and grants. Android component class and package names are also retained. The namespace is not merely an app label.

## Privacy

The public APK contains no user transactions, private database or backups, or bundled API key. Ledger data is stored locally, but this does not mean that the database is encrypted. When you use AI Q&A, your question **and a monthly ledger summary containing transaction rows** are sent to Google. Read [privacy and permissions](docs/PRIVACY.md) before enabling it.

## Source and documentation

- [Build from source, SDK, and signing](docs/BUILD-FROM-SOURCE.md)
- [Privacy](docs/PRIVACY.md)
- [Security policy and private reporting](SECURITY.md)
- [Changelog](CHANGELOG.md)
- [Source provenance and recovery](docs/RECOVERY-NOTES.md)
- [MIT license](LICENSE) · [Third-party licenses](docs/THIRD-PARTY-NOTICES.md)

The MIT license applies to the original Expense source, not to relicensing dependencies. The public release adds privacy sanitization for publication; the source does not promise bit-for-bit reconstruction of the older APK.

## CI and contributions

[Android CI](https://github.com/faraajs/expense/actions/workflows/android.yml) runs actual unit tests, a debug APK build, and Android Lint on pushes to `main`, pull requests, or manual triggers. Reports and the debug APK are available as run artifacts. The APK is **not an official release** and cannot update the official APK with a different certificate. The badge shows the status of a related run, not proof that every feature has been tested.

[CodeQL](https://github.com/faraajs/expense/actions/workflows/codeql.yml) analyzes Java/Kotlin through a tracked debug build. Dependabot proposes weekly Gradle and GitHub Actions updates without auto-merge. CI does not run device or emulator tests, read real bank notifications, verify updates that preserve data or grants, test the widget, or test Gemini with a valid key. Distinguish historical manual evidence for 0.4.2 from CI results on the current commit; see [verification scope and commands](docs/BUILD-FROM-SOURCE.md#ci-and-manual-verification).

Use the issue and PR templates with **synthetic data only**, not real bank notifications, private ledgers or exports, or secrets. Report vulnerabilities [privately](SECURITY.md).
