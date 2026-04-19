# Changelog

All notable changes to Multi App Uninstaller will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]

---

## [1.0.0] - 2026-04-19

### Added
- Linky-style onboarding screen with animated pager
- Type-safe Jetpack Compose navigation
- Sort bottom sheet with categorized options
- Redesigned app details as bottom sheet with info cards
- AMOLED-optimized dark theme with accent colors
- App update checking via GitHub with configurable interval
- Persistent update preferences (interval, skipped versions, last-check time)
- Interval-gated auto-check at app start
- Shimmer loading state for app list
- CI and release GitHub Actions workflows
- Issue templates and contributing guidelines

### Changed
- Redesigned AppListItem with gradient icon box and selection border
- Redesigned SelectionBottomBar with Linky-style action buttons
- Search bar now pill-shaped with sort button integrated
- Empty states now have tinted circular icon containers
- Settings cards use surface container depth hierarchy
- Batch result dialog uses AccentColors (green/red)

### Fixed
- Home screen no longer flashes before onboarding on first launch
- Loading state now shows during app list query
- Keyboard dismisses and focus clears on tap outside, back press, or keyboard Done action

---

## [0.2.0] - 2025-11-21

### Changed
- Complete UI/UX redesign with Material 3
- Component-based architecture
- SegmentedButton for theme selection
- Modal bottom sheets for informational content
- Improved settings organization
- Updated to GPL-3.0 license
- Performance improvements and bug fixes

---

## [0.1.0] - 2024-09-19

### Added
- Initial release
- Batch app uninstall functionality
- Search and filter apps
- Sort by name, size, install date, and update date
- Material Design theme support
- Dynamic color support (Android 12+)

---

## Version History

| Version | Release Date | Highlights |
|---------|--------------|------------|
| 1.0.0 | 2026-04-19 | Linky-style redesign and update system |
| 0.2.0 | 2025-11-21 | Material 3 redesign |
| 0.1.0 | 2024-09-19 | Initial release |

---

[Unreleased]: https://github.com/ahmmedrejowan/MultiAppUninstaller/compare/v1.0.0...HEAD
[1.0.0]: https://github.com/ahmmedrejowan/MultiAppUninstaller/releases/tag/v1.0.0
[0.2.0]: https://github.com/ahmmedrejowan/MultiAppUninstaller/releases/tag/v0.2.0
[0.1.0]: https://github.com/ahmmedrejowan/MultiAppUninstaller/releases/tag/v0.1.0
