# Known Issues

This document lists known issues and limitations in Multi App Uninstaller, along with workarounds where available.

## Batch Uninstall

### User must confirm each uninstall individually
**Issue:** Android requires user confirmation for each app uninstall. There is no way to silently uninstall apps without root access.

**Workaround:** Confirm each prompt as it appears. The app queues them sequentially for you.

**Status:** Android platform limitation

---

### Back button during batch uninstall marks remaining as failed
**Issue:** Pressing the back button while a batch uninstall is in progress will cancel the remaining queue and mark those apps as failed.

**Workaround:** Let the batch process complete, or use the cancel button for a cleaner exit.

**Status:** By design

---

## App List

### System apps are not shown
**Issue:** The app only lists user-installed apps. System/pre-installed apps are intentionally excluded.

**Workaround:** Use ADB or a root-enabled tool to manage system apps.

**Status:** By design (safety measure)

---

### App size may not be fully accurate
**Issue:** The displayed app size only reflects the APK size, not the total storage including data and cache.

**Workaround:** Check the system App Info page for complete storage usage.

**Status:** Known limitation

---

## Permissions

### QUERY_ALL_PACKAGES permission on Android 11+
**Issue:** On Android 11 and above, the app needs the QUERY_ALL_PACKAGES permission to list all installed apps. Some app stores may flag this.

**Workaround:** The permission is declared in the manifest and should work automatically.

**Status:** Android platform requirement

---

## Performance

### Initial app load may take a moment
**Issue:** Loading the full list of installed apps with icons and metadata can take a few seconds on devices with many apps.

**Workaround:** A loading indicator is shown. Subsequent interactions are fast.

**Status:** Normal behavior

---

## Google Fonts

### Ubuntu font may not load on some devices
**Issue:** The app uses Google Fonts (Ubuntu) via Play Services. On devices without Play Services, the font may fall back to system default.

**Workaround:** The app remains fully functional with the fallback font.

**Status:** Known limitation

---

## Reporting New Issues

If you encounter an issue not listed here, please report it:

1. **Check existing issues:** [GitHub Issues](https://github.com/ahmmedrejowan/MultiAppUninstaller/issues)
2. **Create a new issue** with:
   - Device model and Android version
   - App version
   - Steps to reproduce
   - Expected vs actual behavior
   - Screenshots if applicable

---

## Fixed Issues

Issues that have been fixed in recent releases:

| Issue | Fixed In | Description |
|-------|----------|-------------|
| Home screen flash before onboarding | v1.0.0 | Navigation gated by splash + type-safe routes |
| Keyboard not dismissing on tap outside | v1.0.0 | Added tap/back/Done focus clear |
| - | v0.2.0 | Material 3 redesign |

---

*Last updated: 2026-04-08*
