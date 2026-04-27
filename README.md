# Elite Coach - Native Android

## Pure Native Android Fitness Tracking Application

This is a **100% Native Android** implementation of the Elite Coach fitness tracking app. Built with:

- **Kotlin**
- **XML Layouts**
- **ConstraintLayout & Material Design**
- **AdMob Integration**
- **Local Data Persistence (SharedPreferences + Gson)**

### ✅ No WebView • No React • No Capacitor • No Hybrid Frameworks

---

## Features

- **Onboarding Flow** - Complete user profile setup with body type calculation
- **Workout Tracking** - Daily exercise checklists with completion tracking
- **Streak Counter** - Track consecutive days of completion
- **Diet Plans** - Weekly meal plans based on user preferences
- **Badges/Achievements** - Unlock achievements for milestone completions
- **Local-First** - All data stored locally, no server required
- **AdMob Banner Ads** - Monetization via Google AdMob
- **Privacy Policy** - Compliant privacy policy screen

---

## Project Structure

```
EliteCoachAndroid/
├── app/
│   ├── src/main/
│   │   ├── java/com/elitecoach/app/
│   │   │   ├── MainActivity.kt
│   │   │   ├── OnboardingActivity.kt
│   │   │   ├── PrivacyPolicyActivity.kt
│   │   │   ├── data/
│   │   │   │   ├── UserProfile.kt
│   │   │   │   ├── DailyLog.kt
│   │   │   │   ├── WorkoutPlan.kt
│   │   │   │   ├── DietPlan.kt
│   │   │   │   └── PreferencesManager.kt
│   │   │   └── engine/
│   │   │       └── FitnessEngine.kt
│   │   ├── res/
│   │   │   ├── layout/
│   │   │   ├── values/
│   │   │   └── menu/
│   │   └── AndroidManifest.xml
│   └── build.gradle.kts
└── build.gradle.kts
```

---



## Build Instructions

### Prerequisites

1. **Android Studio** (Arctic Fox or later)
2. **JDK 17**
3. **Android SDK** (API 34)

### Steps

1. **Open Project in Android Studio**

   ```bash
   File > Open > Select "EliteCoachAndroid" folder
   ```

2. **Sync Gradle**

   ```bash
   File > Sync Project with Gradle Files
   ```

3. **Build APK**

   ```bash
   Build > Build Bundle(s) / APK(s) > Build APK(s)
   ```

   Or via command line:

   ```bash
   ./gradlew assembleDebug
   ```

4. **Install on Device**

   ```bash
   adb install -r app/build/outputs/apk/debug/app-debug.apk
   ```

---

## Technical Specifications

- **Min SDK**: 21 (Android 5.0 Lollipop)
- **Target SDK**: 34 (Android 14)
- **Language**: Kotlin
- **Build Tool**: Gradle (Kotlin DSL)
- **Dependencies**:
  - AndroidX Core KTX
  - AppCompat
  - Material Design Components
  - ConstraintLayout
  - RecyclerView
  - Google Play Services Ads (AdMob)
  - Gson (JSON serialization)

---

## Key Files

### Activities

- `MainActivity.kt` - Main dashboard with workout tracking
- `OnboardingActivity.kt` - User profile setup wizard
- `PrivacyPolicyActivity.kt` - Privacy policy display

### Business Logic

- `FitnessEngine.kt` - Core calculations (body type, streaks, badges, workout/diet generation)
- `PreferencesManager.kt` - Local data persistence

### Data Models

- `UserProfile.kt` - User information and progress
- `DailyLog.kt` - Daily workout completion tracking
- `WorkoutPlan.kt` - Exercise plans
- `DietPlan.kt` - Meal plans

---

## Verification Checklist

- [ ] Project builds successfully
- [ ] No compilation errors
- [ ] App launches to onboarding
- [ ] Onboarding form validation works
- [ ] Profile creation successful
- [ ] Dashboard loads with correct data
- [ ] Checkboxes toggle properly
- [ ] Steps input functional
- [ ] Completion score updates
- [ ] Save & Lock button works
- [ ] Streak increments correctly
- [ ] AdMob banner displays
- [ ] Privacy policy accessible
- [ ] Reset functionality works
- [ ] Data persists across app restarts

---

## Notes

- **No External APIs**: All data is stored locally using SharedPreferences
- **AdMob Ads**: Banner ads load at the bottom of the main screen
- **Dark Theme**: App uses a dark theme matching the original web app aesthetic
- **Portrait Only**: App is locked to portrait orientation

---

## License

All rights reserved. This is a conversion of the Elite Coach web app to native Android.
