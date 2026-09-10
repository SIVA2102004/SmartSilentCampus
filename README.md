# Smart Silent Campus

**"Your phone knows when to stay silent."**

Production-grade Android application for automated, location-based sound profile management inside college campuses, classrooms, laboratories, libraries, and examination halls using Google Geofencing APIs and Clean Architecture.

---

## Key Features

1. **Intelligent Geofencing**:
   - Zero continuous GPS polling in the background. Uses Android's native `GeofencingClient` with hardware low-power geofencing.
   - Configurable zones (50m, 100m, 150m, 200m, 300m, custom).
   - Dwell detection with loitering delay (15 seconds) to eliminate edge bouncing.

2. **Previous Sound State Capture & Exact Restoration**:
   - Snapshots exact audio volumes (Ring, Notification, Media, Alarm, System) and ringer mode before entering the first silent zone.
   - Upon exiting all registered zones, the original audio state is restored without assuming arbitrary defaults.

3. **Multi-Zone Overlap & Priority Resolution**:
   - Strict hierarchical priority engine:
     `Exam Hall (100) > Classroom (80) > Laboratory (70) > Library (60) > Seminar Hall (50) > College Campus (30) > Hostel (20) > Custom (10)`
   - Leaving a child zone (e.g., Classroom) while remaining in a parent zone (e.g., Campus) seamlessly maintains the campus profile without premature normal mode restoration.

4. **Android Restriction Compliance**:
   - Full support for Android 12, 13, 14, and 15+.
   - Do Not Disturb / Notification Policy Access detection with graceful fallback to Vibrate mode if DND is restricted.
   - Battery optimization guidance screen and background location permission onboarding.

5. **Built-In Developer / User Test Mode**:
   - Simulate entering and exiting zones with real-time audio state validation without physical travel.

---

## Tech Stack

- **Language**: Kotlin 2.0+
- **UI Toolkit**: Jetpack Compose with Material 3
- **Architecture**: Clean Architecture + MVVM + Repository Pattern
- **Dependency Injection**: Hilt
- **Local Persistence**: Room Database + DataStore Preferences
- **Location Services**: Google Play Services Geofencing API & Fused Location Provider
- **Asynchronous Execution**: Kotlin Coroutines & StateFlow

---

## Project Structure

```
app/src/main/java/com/smartsilentcampus/
├── SmartSilentApplication.kt
├── boot/
│   └── BootReceiver.kt
├── data/
│   ├── local/
│   │   ├── dao/
│   │   ├── database/
│   │   └── entity/
│   └── repository/
├── di/
│   └── AppModules.kt
├── domain/
│   ├── model/
│   ├── repository/
│   └── usecase/
│       └── AutomationEngine.kt
├── geofence/
│   ├── GeofenceBroadcastReceiver.kt
│   └── GeofenceManager.kt
├── notification/
│   └── SmartSilentNotificationManager.kt
├── permissions/
│   └── PermissionManager.kt
├── presentation/
│   ├── MainActivity.kt
│   ├── components/
│   ├── history/
│   ├── home/
│   ├── locations/
│   ├── navigation/
│   ├── onboarding/
│   ├── profiles/
│   ├── settings/
│   ├── testmode/
│   └── theme/
└── sound/
    └── SoundController.kt
```

---

## How to Build & Run

### Prerequisites
- Android Studio Ladybug / Meerkat or later
- JDK 17 or higher
- Android SDK Platform 35 (Android 15)

### Steps
1. Clone or open `SmartSilentCampus` in Android Studio.
2. Ensure `local.properties` points to your Android SDK:
   ```properties
   sdk.dir=C:\\Users\\Dell\\AppData\\Local\\Android\\Sdk
   ```
3. Set your Google Maps API Key in `gradle.properties` or `local.properties`:
   ```properties
   MAPS_API_KEY=AIzaSy...
   ```
4. Build APK:
   ```bash
   ./gradlew assembleDebug
   ```
5. Run unit tests:
   ```bash
   ./gradlew test
   ```
