# Android Restrictions & Sound Control Guide

This document outlines Android API security restrictions across Android 12 (API 31), Android 13 (API 33), Android 14 (API 34), and Android 15 (API 35+).

---

## 1. Do Not Disturb (Notification Policy Access)

### Problem
Starting with Android 6.0 (API 23) and strictly enforced on Android 10+:
Calling `AudioManager.setRingerMode(RINGER_MODE_SILENT)` without `android.permission.ACCESS_NOTIFICATION_POLICY` throws:
```
java.lang.SecurityException: Not allowed to change Do Not Disturb state
```

### Solution Implemented
`SoundController.kt` checks:
```kotlin
notificationManager.isNotificationPolicyAccessGranted
```
- **If Granted**: Applies `RINGER_MODE_SILENT` and optional DND priority filter.
- **If Denied**: Automatically and gracefully falls back to `RINGER_MODE_VIBRATE` (which does not require DND access on modern Android). The app never crashes.
- The UI presents a prompt guiding the user to open Android Settings:
  ```kotlin
  Intent(Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS)
  ```

---

## 2. Background Location (`ACCESS_BACKGROUND_LOCATION`)

### Problem
Since Android 10 (API 29) and Android 11 (API 30):
Apps cannot request foreground location and background location simultaneously. Background location requires a separate, progressive request flow.

### Solution Implemented
- The onboarding wizard requests `ACCESS_FINE_LOCATION` first.
- Only after fine location is granted does the app present the rationale for background geofencing and launch the dedicated background location request or App Settings page.

---

## 3. Android 13+ Notification Permission (`POST_NOTIFICATIONS`)

### Problem
On Android 13+ (API 33+), runtime permission `POST_NOTIFICATIONS` is required to display notifications.

### Solution Implemented
- Declared in `AndroidManifest.xml`.
- Dedicated onboarding step prompts the user before posting entry/exit notifications.

---

## 4. Aggressive Manufacturer Battery Optimization

### Problem
Manufacturers (Xiaomi, Samsung, Oppo, Vivo, OnePlus) terminate background processes aggressively.

### Solution Implemented
- Handled via native hardware Google Play Services Geofencing (`GeofencingClient`), which operates within Google Play Services and does not require an ongoing foreground service or continuous GPS polling.
- `BootReceiver` listens for `BOOT_COMPLETED` and `MY_PACKAGE_REPLACED` to immediately re-register geofences after phone reboots.
- Built-in battery optimization exemption prompt (`ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS`).
