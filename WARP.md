# WARP.md

This file provides guidance to WARP (warp.dev) when working with code in this repository.

Project overview
- Android app (Kotlin, Jetpack Compose) in a single module: :app
- Uses CameraX for camera integration and ML Kit Text Recognition for on-device OCR
- UI is built with Jetpack Compose (Material 3, Compose BOM)
- Gradle Kotlin DSL with a version catalog (gradle/libs.versions.toml)

Toolchain
- Android Gradle Plugin: 8.10.0
- Kotlin: 2.0.21 with Compose compiler plugin
- Compile/Target SDK: 35; Min SDK: 24
- JVM target: 11

Common commands
- List available tasks
```bash path=null start=null
./gradlew --no-daemon --warning-mode all tasks --all
```

- Build debug APK
```bash path=null start=null
./gradlew :app:assembleDebug
```

- Build release APK (unsigned unless signing is configured)
```bash path=null start=null
./gradlew :app:assembleRelease
```

- Install debug build to a connected device/emulator
```bash path=null start=null
./gradlew :app:installDebug
```

- Run unit tests (local JVM)
```bash path=null start=null
./gradlew :app:testDebugUnitTest
```

- Run a single unit test (by class or method)
```bash path=null start=null
./gradlew :app:testDebugUnitTest --tests 'fully.qualified.ClassName'
# Or a single method
./gradlew :app:testDebugUnitTest --tests 'fully.qualified.ClassName.methodName'
```

- Run instrumented Android tests (requires a running emulator or device)
```bash path=null start=null
./gradlew :app:connectedDebugAndroidTest
```

- Lint (Android Lint)
```bash path=null start=null
./gradlew :app:lintDebug
# All variants
./gradlew :app:lint
```

- Clean build outputs
```bash path=null start=null
./gradlew clean
```

Architecture and build structure
- Module layout
  - settings.gradle.kts includes only ":app", establishing a single-module structure
  - Root build.gradle.kts configures plugin aliases via the version catalog; plugins are applied in the app module

- Version catalog (gradle/libs.versions.toml)
  - Centralizes versions for AGP, Kotlin, CameraX, Compose BOM, AndroidX, ML Kit
  - Libraries used in :app include:
    - CameraX: camera-camera2, camera-lifecycle, camera-view
    - ML Kit: com.google.mlkit:text-recognition
    - Compose: BOM-managed UI, Material 3, tooling, testing
    - AndroidX core, lifecycle, activity-compose
    - Testing: junit (unit), androidx.test.ext:junit, espresso (instrumented)

- App module (app/build.gradle.kts)
  - compose = true in buildFeatures; Kotlin JVM target 11
  - DefaultConfig: applicationId com.vision.smartlens, testInstrumentationRunner androidx.test.runner.AndroidJUnitRunner
  - Release build uses default ProGuard optimize file and app/proguard-rules.pro; isMinifyEnabled = false by default

- Manifest (app/src/main/AndroidManifest.xml)
  - Declares CAMERA permission and camera.any feature
  - Main launcher Activity: com.vision.smartlens.MainActivity
  - App theme: Theme.SmartLens (inherits android:Theme.Material.Light.NoActionBar)

Android- and environment-specific notes
- Building from CLI requires a functional Android SDK and build-tools; device/emulator needed for connected tests
- Gradle properties enable AndroidX and non-transitive R classes; Kotlin style is "official"

Conventions and where to change things
- Dependencies and versions: edit gradle/libs.versions.toml
- Repositories and plugin management: settings.gradle.kts
- App configuration (SDKs, Compose enablement, ProGuard, test runner): app/build.gradle.kts
- Manifest-level permissions and entry points: app/src/main/AndroidManifest.xml

External project rules and docs
- No CLAUDE.md, Cursor rules, Copilot instructions, or README.md were found in this repository at the time of writing
