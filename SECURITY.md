# Security policy

## Supported versions

Security fixes target only the [latest stable release](https://github.com/faraajs/expense/releases/latest). Older versions do not receive promised backports. Before updating, export transactions to a safe location and install the official APK as an update. Do not uninstall or clear ledger data.

## Private vulnerability reporting

Use [GitHub Private Vulnerability Reporting](https://github.com/faraajs/expense/security/advisories/new) for vulnerabilities, data leaks, or security issues. Do not open a public issue or PR with exploit details or sensitive data. If private reporting is not yet available, do not publish sensitive details. Wait until a maintainer enables it.

Include the app version, Android version, impact, minimal reproduction steps, and a synthetic example when needed. **Do not send real bank notifications, ledger data, databases, private transaction exports, account numbers, API keys, keystores, or passwords** through public issues, screenshots, logs, or private reports. Redaction is not a substitute for synthetic examples when financial data could be reconstructed.

Maintainers assess reports and coordinate fixes and disclosure as availability allows. There is no response SLA and no guarantee that all vulnerabilities have been found. Ordinary bugs with synthetic data can be reported through the issue template.

## Warranty limits

CI, Android Lint, Dependabot, and CodeQL support review but are not a complete security audit. CodeQL uses a tracked debug build to analyze Java/Kotlin. Results do not cover all runtime behavior, dependencies, or release configuration. See [CI scope and manual verification](docs/BUILD-FROM-SOURCE.md#ci-and-manual-verification). The ledger is stored locally, but this does not mean that the database is encrypted. [Privacy and permissions](docs/PRIVACY.md) explain notification access and optional data transmission to Gemini.
