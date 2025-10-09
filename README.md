# Smart Lens

An Android app scaffold built with Kotlin and Jetpack Compose that integrates CameraX and ML Kit Text Recognition for on-device OCR. The project is configured with a Gradle Kotlin DSL build, a centralized version catalog, and a single :app module.

Note: The repository currently contains the Android project configuration, resources, and dependencies wired for CameraX and ML Kit. Feature implementation (camera preview, OCR pipeline, UI screens) can be added on top of this scaffold.


## Table of contents
- Overview
- Tech stack
- Requirements
- Quickstart
- Build and run
- Testing and lint
- Project structure
- Configuration
- Permissions
- Architecture at a glance
- Roadmap / next steps
- Troubleshooting
- License


## Overview
- Package: `com.vision.smartlens`
- Min SDK: 24 (Android 7.0)
- Target/Compile SDK: 35
- Language: Kotlin (JVM target 11)
- UI: Jetpack Compose + Material 3 (managed via Compose BOM)
- Camera: CameraX (camera-camera2, lifecycle, view)
- OCR: ML Kit Text Recognition (on-device)

The app requests the camera permission and is set up to use a no-action-bar Material theme (`Theme.SmartLens`).


## Tech stack
- Android Gradle Plugin: 8.10.0
- Kotlin: 2.0.21 + Compose compiler plugin
- Jetpack Compose BOM: 2025.07.00
- CameraX: 1.4.2
- ML Kit Text Recognition: 16.0.1
- AndroidX: core-ktx, lifecycle-runtime-ktx, activity-compose
- Test: JUnit 4 (unit), AndroidX JUnit, Espresso (instrumented)

Dependency versions are managed via Gradle Version Catalog at `gradle/libs.versions.toml`.


## Requirements
- JDK 11
- Android SDK + Build-Tools for SDK 35 installed
- A device or emulator running Android 7.0+ for install/instrumentation tests
- Android Studio (latest) or CLI tools

Environment tips (CLI):
- Ensure `ANDROID_HOME` or `ANDROID_SDK_ROOT` is set and platform/Build-Tools for API 35 are installed.
- Accept Android SDK licenses if prompted: `yes | sdkmanager --licenses`


## Quickstart
Clone and build from the command line using the Gradle wrapper:

```bash
./gradlew --version
./gradlew :app:assembleDebug
```

Install the debug build to a connected device/emulator:

```bash
./gradlew :app:installDebug
```

Run the app from your launcher (it will request camera permission at first use).


## Build and run
- List tasks
```bash
./gradlew --no-daemon --warning-mode all tasks --all
```

- Assemble APKs
```bash
./gradlew :app:assembleDebug
./gradlew :app:assembleRelease   # unsigned unless signing is configured
```

- Install debug
```bash
./gradlew :app:installDebug
```

- Clean
```bash
./gradlew clean
```


## Testing and lint
- Unit tests (JVM):
```bash
./gradlew :app:testDebugUnitTest
```

- Run a single unit test (class or method):
```bash
./gradlew :app:testDebugUnitTest --tests 'fully.qualified.ClassName'
./gradlew :app:testDebugUnitTest --tests 'fully.qualified.ClassName.methodName'
```

- Instrumented tests (on device/emulator):
```bash
./gradlew :app:connectedDebugAndroidTest
```

- Android Lint:
```bash
./gradlew :app:lintDebug
# Or all variants
./gradlew :app:lint
```


## Project structure
- `settings.gradle.kts` — declares single module `:app`
- `build.gradle.kts` (root) — plugin aliases via the version catalog
- `gradle/libs.versions.toml` — centralized versions for AGP, Kotlin, Compose BOM, CameraX, ML Kit, AndroidX
- `app/build.gradle.kts` — Android + Kotlin configuration, Compose enabled, dependencies
- `app/src/main/AndroidManifest.xml` — camera permission/feature, `MainActivity` launcher entry
- `app/src/main/res/values/themes.xml` — `Theme.SmartLens` (Material Light, no action bar)


## Configuration
- Application ID: `com.vision.smartlens` (change in `app/build.gradle.kts`)
- SDK levels: `compileSdk = 35`, `targetSdk = 35`, `minSdk = 24`
- Kotlin options: `jvmTarget = "11"`
- Compose: enabled via `buildFeatures.compose = true`
- ProGuard/R8: release uses default optimize file + `app/proguard-rules.pro` (minify disabled by default)
- Dependency management: update versions in `gradle/libs.versions.toml`


## Permissions
The app requests camera access:
- Manifest: `android.permission.CAMERA`
- Feature: `android.hardware.camera.any`

Runtime permission requesting should be implemented in UI/Activity code before starting the CameraX preview.


## Architecture at a glance
This repository is a single-module Compose app scaffold intended to:
- Show a camera preview using CameraX (camera-camera2, lifecycle, view)
- Recognize text on-device with ML Kit’s Text Recognition API
- Display results within a Compose-driven UI (Material 3 theme)

Recommended layering when implementing features:
- UI (Compose): Camera preview surface, text overlay/indicators
- Camera layer: CameraX setup, lifecycle binding, frame acquisition
- OCR layer: ML Kit Text Recognition invocation on camera frames
- Model/state: simple data model representing detected blocks/lines/words


## Roadmap / next steps
- Implement camera preview (CameraX PreviewView or Compose interop)
- Wire up ML Kit Text Recognition to process frames
- Add UI to visualize recognized text and copy/share actions
- Request and handle camera runtime permission in Activity/Compose
- Add unit tests for parsing/formatting OCR results and view-model logic
- Add instrumented tests for permission and basic capture flows


## Troubleshooting
- SDK not found: Ensure Android SDK is installed and `ANDROID_HOME` or `ANDROID_SDK_ROOT` is set
- Build-tools mismatch: Install the required platform/build-tools for API 35 via SDK Manager
- Emulator camera: If preview is blank, verify emulator has a virtual camera or test on a physical device
- Gradle memory: adjust `org.gradle.jvmargs` in `gradle.properties` if you hit OOM during builds


## License
No license is specified in this repository. If you intend to open-source, add a LICENSE file (e.g., MIT, Apache-2.0).
