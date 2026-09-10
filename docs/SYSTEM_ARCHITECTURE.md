# SMART SILENT CAMPUS — SYSTEM ARCHITECTURE & ENGINEERING REPORT

## 1. System Architecture

The application follows Google's recommended Clean Architecture with unidirectional data flow (UDF).

```mermaid
graph TD
    UI[Jetpack Compose UI Screens] --> VM[ViewModels]
    VM --> UC[AutomationEngine Use Case]
    VM --> REPO[Repositories]
    
    GEO[Google Play Services Geofencing] --> RCV[GeofenceBroadcastReceiver]
    RCV --> UC
    
    BOOT[Device Boot / Package Update] --> BR[BootReceiver]
    BR --> GM[GeofenceManager]
    
    UC --> SC[SoundController - Audio & DND Policy]
    UC --> NM[SmartSilentNotificationManager]
    UC --> REPO
    
    REPO --> ROOM[(Room Database)]
    REPO --> DS[(DataStore Preferences)]
    
    SC --> OS_AUDIO[Android AudioManager]
    SC --> OS_DND[Android NotificationPolicy]
```

---

## 2. Geofence Transition & State Restoration Sequence

```mermaid
sequenceDiagram
    autonumber
    actor User as Student
    participant OS as Android Geofencing API
    participant RCV as GeofenceBroadcastReceiver
    participant ENG as AutomationEngine
    participant SC as SoundController
    participant DB as Room Database
    participant NM as NotificationManager

    Note over User, OS: User walks into Classroom geofence
    OS->>RCV: Broadcast Intent (GEOFENCE_TRANSITION_ENTER)
    RCV->>ENG: handleEnter(locationId)
    ENG->>DB: Query current ActiveZone count
    alt First Zone Entered
        ENG->>SC: captureCurrentSoundState()
        SC-->>ENG: PreviousSoundState(ring=80%, mode=NORMAL)
        ENG->>DB: saveState(PreviousSoundState)
    end
    ENG->>DB: addActiveZone(locationId, priority=80)
    ENG->>DB: getHighestPriorityActiveZone()
    DB-->>ENG: Highest Zone Profile (Silent)
    ENG->>SC: applySoundProfile(Silent)
    SC-->>ENG: Success (Audio mode set to SILENT / VIBRATE)
    ENG->>DB: addHistory(ENTER event)
    ENG->>NM: showSilentActivatedNotification()
    
    Note over User, OS: User walks out of Classroom geofence
    OS->>RCV: Broadcast Intent (GEOFENCE_TRANSITION_EXIT)
    RCV->>ENG: handleExit(locationId)
    ENG->>DB: removeActiveZone(locationId)
    ENG->>DB: getHighestPriorityActiveZone()
    alt Remaining Active Zones Exist (e.g., Campus Zone)
        ENG->>SC: applySoundProfile(Campus Profile)
        ENG->>DB: recordHistory(Maintained Campus Profile)
    else No Active Zones Left
        ENG->>DB: getSavedState()
        DB-->>ENG: PreviousSoundState
        ENG->>SC: restorePreviousSoundState(state)
        ENG->>DB: clearState()
        ENG->>NM: showSoundRestoredNotification()
    end
```

---

## 3. Database Entity Relationship Diagram (ERD)

```mermaid
erDiagram
    LOCATION ||--o{ ACTIVE_ZONE : "tracks current presence"
    LOCATION }|--|| SOUND_PROFILE : "assigns"
    LOCATION ||--o{ AUTOMATION_HISTORY : "logs events"
    PREVIOUS_SOUND_STATE ||--o| LOCATION : "captured before entry"

    LOCATION {
        string id PK
        string name
        string type
        double latitude
        double longitude
        float radiusMeters
        int priority
        string profileId FK
        boolean isEnabled
        long createdAt
        long updatedAt
    }

    SOUND_PROFILE {
        string id PK
        string name
        string ringerMode
        int ringVolumePercent
        int notificationVolumePercent
        int mediaVolumePercent
        int alarmVolumePercent
        int systemVolumePercent
        boolean isVibrationEnabled
        boolean isDndEnabled
        boolean isDefault
    }

    PREVIOUS_SOUND_STATE {
        long id PK
        int ringerMode
        int ringVolume
        int notificationVolume
        int mediaVolume
        int alarmVolume
        int systemVolume
        int vibrationSetting
        long capturedAt
        boolean isActive
    }

    AUTOMATION_HISTORY {
        long id PK
        string locationId FK
        string locationName
        string eventType
        long timestamp
        string appliedProfileName
        string previousProfileName
        boolean success
        string errorMessage
    }

    ACTIVE_ZONE {
        string locationId PK
        long enteredAt
        int priority
        string profileId
    }
```

---

## 4. Multi-Zone Priority Engine

When a student moves between nested zones (e.g., College Campus $\rightarrow$ Library $\rightarrow$ Exam Hall), the engine dynamically resolves priority:

| Zone Type | Priority Weight | Typical Default Behavior |
| :--- | :--- | :--- |
| **Examination Hall** | `100` | Strict Silence / DND, No Vibration, 0% Media |
| **Classroom** | `80` | Silent Mode, Vibrate Off |
| **Laboratory** | `70` | Silent Mode, Vibrate Off |
| **Library** | `60` | Silent Mode, Vibrate On, Media 20% |
| **Seminar Hall** | `50` | Silent Mode |
| **College Campus** | `30` | Vibrate Only / Campus Silent |
| **Hostel** | `20` | Normal Mode |
| **Custom Zone** | `10` | User Configured |
