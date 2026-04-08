# Security Policy

## Supported Versions

| Version | Supported          |
| ------- | ------------------ |
| 0.x.x   | :white_check_mark: |

## Reporting a Vulnerability

If you discover a security vulnerability in Multi App Uninstaller, please report it responsibly:

1. **Do NOT** open a public issue
2. Email the maintainer directly or use GitHub's private vulnerability reporting
3. Include:
   - Description of the vulnerability
   - Steps to reproduce
   - Potential impact
   - Suggested fix (if any)

## Security Measures

Multi App Uninstaller implements the following security measures:

- **No network access** — the app operates entirely offline
- **No data collection** — no analytics, tracking, or telemetry
- **No third-party SDKs** that collect user data
- **Minimal permissions** — only queries installed packages
- **Local storage only** — preferences stored via DataStore on device
- **ProGuard/R8** obfuscation in release builds
- **Open source** — fully auditable under GPL-3.0

## Response Timeline

- **Acknowledgment**: Within a week
- **Initial Assessment**: Within 2 weeks
- **Fix Timeline**: Depends on severity and availability
  - Critical: As soon as possible
  - Others: Next release

## Scope

The following are in scope for security reports:

- Data leakage
- Privilege escalation
- Unauthorized app uninstallation
- Denial of service

Out of scope:

- Issues requiring physical device access
- Social engineering attacks
- Issues in third-party libraries (report to respective maintainers)
