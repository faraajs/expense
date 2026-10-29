# Privacy and permissions

[Back to README](../README.md)

## Local data

Expense stores transactions, budgets, recurring expenses, categories, and notification audit logs in a local Room database. Transaction and log contents may include amounts, times, merchants, source apps, titles, and notification text. The ledger database does not use dedicated database encryption. Its protection follows the Android device sandbox and device security. The public APK contains no user transactions, private databases or backups, or bundled API key.

Notification access gives the app the ability to read device notifications. The parser recognizes only supported sources and formats, but the Android permission itself is broad. Do not enable it if you are uncomfortable with that access. Local audit logs may contain sensitive text. A worker is scheduled daily to delete raw logs older than 21 days. Execution depends on Android scheduling and is not a guarantee of timely deletion. This retention policy does not delete ledger transactions or exported files.

Android automatic backup is disabled in the app configuration. Do not treat this as a replacement for manual backups or protection against a rooted or compromised device. JSON exports contain transaction data and may include sensitive metadata or text. The sharing target and selected storage location have their own policies. Store exports securely and do not upload them to public issues. A transaction export is not a complete backup of all data and settings.

## Optional Gemini: data leaving the device

The ledger and basic parser do not require internet access. When a user sends a question through AI Q&A, the app contacts Google's Generative Language API over HTTPS (`generativelanguage.googleapis.com`). The payload contains instructions or prompts, the user's question, and a **per-transaction** monthly ledger summary that includes dates, income or expense type, amount, merchant, and category. This summary is not merely an aggregate total and is not anonymous data.

Google receives the payload and API key for authentication. Google's use, storage, and processing of the data follow the [Gemini API terms](https://ai.google.dev/gemini-api/terms) and [Google Privacy Policy](https://policies.google.com/privacy), including terms that may differ by service or usage level. Do not send Google data that you do not want to share. Disabling notification access does not revoke data previously sent through AI.

Users must provide their own supported Google AI Studio key. Availability of the model, quota, and network connection determine success. No shared key or free AI access is promised. Direct AI use has not been verified. AI responses may be wrong and are not professional financial advice.

The key is stored through `EncryptedSharedPreferences` with Android Keystore. This does not encrypt the ledger. The key field is masked in the UI. The public launcher does not accept keys through intent extras. Do not share the key, and revoke or rotate it through the provider if it is exposed.

## Permissions and sideloading

- **Notification access:** reads notifications for transaction recording and can be revoked in Android settings.
- **POST_NOTIFICATIONS:** displays app notifications, such as budget alerts. This is separate from permission to read notifications.
- **INTERNET:** used by the optional Gemini feature.
- **REQUEST_IGNORE_BATTERY_OPTIMIZATIONS:** lets users configure a battery exemption for listener reliability.
- **RECEIVE_BOOT_COMPLETED:** helps restore the service after a restart or package replacement.

On Android 13+ devices that block sideloaded apps, open Settings → Apps → Expense → three-dot menu → Allow restricted settings, then enable Notification access. Menus differ by device. Grant access only to APKs you trust. Official links are in the [README](../README.md).

## Publication sanitization

The published source removes the inactive private history importer and private enrichment. Ingest Logcat output is limited to package status and source rather than transaction payloads; local audit logs remain for diagnostic functions. Room schema was not changed as part of this sanitization.

To stop sharing data with AI, do not use AI Q&A and remove the key through the app settings. To stop capture, revoke notification access. Delete separate export files when they are no longer needed. Clearing app data or uninstalling removes the local ledger but also risks data loss: **export before either action; do not uninstall when only updating the version**.
