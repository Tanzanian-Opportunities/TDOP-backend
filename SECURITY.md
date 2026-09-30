# Security Policy

Repository: `TDOP-backend` (backend API). Full policy: [TDOP-docs/SECURITY.md](https://github.com/Tanzanian-Opportunities/TDOP-docs/blob/develop/SECURITY.md).

## Supported versions

| Version | Supported          |
| ------- | ------------------ |
| develop | :white_check_mark: |
| main    | :white_check_mark: |

Only the latest `develop` and `main` commits are maintained; there are no versioned releases yet.

## How to report a vulnerability

Open a private security advisory for this repository:

https://github.com/Tanzanian-Opportunities/TDOP-backend/security/advisories/new

If you cannot open an advisory, contact the repository maintainer (`felix202422`) privately.
**DO NOT publicly disclose an unpatched vulnerability.**

## Token hygiene

- Never commit secrets: configuration lives in `.env` (git-ignored); `.env.example` carries safe placeholders only.
- Tokens are stored in the Windows Credential Manager (retrieved by `git credential fill`) or in GitHub Actions secrets - never in files.
- Give every token the minimum scope it needs; rotate immediately if one is committed (revoke in GitHub Settings - Developer settings - Personal access tokens).
- Review Dependabot alerts and secret scanning results for this repository regularly.