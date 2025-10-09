# Smart Lens

[![Android CI](https://github.com/YOUR_GITHUB_USER/YOUR_REPO/actions/workflows/android-ci.yml/badge.svg)](https://github.com/YOUR_GITHUB_USER/YOUR_REPO/actions/workflows/android-ci.yml) ![Kotlin](https://img.shields.io/badge/Kotlin-2.0.21-7F52FF?logo=kotlin) ![AGP](https://img.shields.io/badge/AGP-8.10.0-3DDC84?logo=android) ![SDK](https://img.shields.io/badge/SDK-35-brightgreen)

An Android app scaffold built with Kotlin and Jetpack Compose that integrates CameraX and ML Kit Text Recognition for on-device OCR. The project is configured with a Gradle Kotlin DSL build, a centralized version catalog, and a single :app module.

Note: The repository currently contains the Android project configuration, resources, and dependencies wired for CameraX and ML Kit. Feature implementation (camera preview, OCR pipeline, UI screens) can be added on top of this scaffold.


## Table of contents
- Overview
- AI overview
- How AI is applied
- OCR inference pipeline
- Model and language support
- Performance and latency
- Privacy and data handling
- Use cases and impact
- Tech stack
- Requirements
- Quickstart
- Build and run
- Testing and lint
- Project structure
- Configuration
- Permissions
- Screenshots
- Runtime permission example (Compose)
- Continuous Integration
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

## AI overview
Smart Lens uses on-device AI (ML Kit Text Recognition) to convert pixels from the live camera feed into structured text in real time. Running inference entirely on-device means:
- Low latency: immediate feedback suitable for live overlays and guidance
- Private by default: frames never leave the device for OCR
- Offline ready: recognition works without network connectivity

## How AI is applied
- Live OCR on camera frames: continuous recognition while the camera preview is active
- Structured output: recognized text is returned as blocks/lines/elements for downstream UI or logic
- Compose-friendly state: results can be exposed as immutable state to drive UI updates (e.g., overlays, copy/share)

Common use cases this enables:
- Reading signs, labels, and documents in-context
- Digitizing receipts/notes for search and sharing
- Accessibility enhancements (e.g., larger text, potential TTS in future)

## OCR inference pipeline
1) Capture frames with CameraX ImageAnalysis
2) Convert to an ML Kit InputImage with the correct rotation
3) Run Text Recognition on a background thread
4) Emit results to the UI layer for rendering

Example analyzer setup with ML Kit:

```kotlin
class OcrAnalyzer(
    private val onResult: (com.google.mlkit.vision.text.Text) -> Unit
) : ImageAnalysis.Analyzer {

    private val recognizer = com.google.mlkit.vision.text.TextRecognition.getClient(
        com.google.mlkit.vision.text.latin.TextRecognizerOptions.DEFAULT_OPTIONS
    )

    override fun analyze(imageProxy: ImageProxy) {
        val mediaImage = imageProxy.image
        if (mediaImage == null) {
            imageProxy.close()
            return
        }
        val rotation = imageProxy.imageInfo.rotationDegrees
        val input = com.google.mlkit.vision.common.InputImage.fromMediaImage(mediaImage, rotation)

        recognizer.process(input)
            .addOnSuccessListener { text -> onResult(text) }
            .addOnCompleteListener { imageProxy.close() }
    }
}
```

And a performance-friendly ImageAnalysis configuration:

```kotlin
val analysis = ImageAnalysis.Builder()
    .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
    .build()
analysis.setAnalyzer(executor, OcrAnalyzer { text ->
    // Update state with recognized text here
})
```

## Model and language support
This project uses ML Kit's on-device Latin text recognition model. It supports many Latin-based languages. For additional scripts (Chinese, Devanagari, Japanese, Korean), ML Kit provides separate artifacts/packages. If you need those, add the corresponding dependencies and switch the recognizer options accordingly.

Accuracy tips:
- Prefer good lighting and focus; avoid motion blur
- Align/deskew documents when possible
- Consider a region-of-interest crop if only part of the frame is relevant

## Performance and latency
- Use STRATEGY_KEEP_ONLY_LATEST to avoid analyzer backlog
- Keep analysis resolution reasonable (balance detail vs. speed)
- Run inference on a background executor; avoid heavy work on the UI thread
- Reuse recognizer client; avoid recreating it per frame
- Optionally subsample frames (e.g., process every Nth) if UI remains responsive

## Privacy and data handling
- OCR is performed fully on-device; no frames are uploaded by default
- Camera permission is required and requested at runtime
- If you later add cloud features (translation, sync), gate them behind explicit user actions and clear settings

## Use cases and impact
- Quick capture of text from the physical world for copying, sharing, or search
- Assistive scenarios for users with low vision or when reading small text
- Productivity boosts when digitizing notes, whiteboards, and receipts

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
- JDK 17 (required by Android Gradle Plugin 8.x)
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


## Screenshots
Place screenshots in `docs/screenshots/` and reference them below. Example (replace with your own image):

![Main screen placeholder](docs/screenshots/main.png)


## Runtime permission example (Compose)
Request the Camera permission at runtime from a Composable and proceed only when granted:

```kotlin
@Composable
fun CameraPermissionGate(onGranted: () -> Unit, onDenied: () -> Unit = {}) {
    val context = LocalContext.current
    val permission = android.Manifest.permission.CAMERA
    var granted by remember { mutableStateOf(
        ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED
    ) }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        granted = isGranted
        if (isGranted) onGranted() else onDenied()
    }

    LaunchedEffect(Unit) {
        if (!granted) launcher.launch(permission) else onGranted()
    }

    if (!granted) {
        Text("Camera permission required to continue")
    }
}
```

Tip: Wrap your CameraX preview setup inside `onGranted { ... }` to start the camera only after permission is granted.


## Continuous Integration
A basic GitHub Actions workflow is included at `.github/workflows/android-ci.yml` that builds, runs unit tests, and lints on pushes and pull requests.

To enable the badge at the top of this README, replace `YOUR_GITHUB_USER/YOUR_REPO` with your actual GitHub org/user and repo name.


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
- Java version: ensure you’re using JDK 17 when building locally and in CI


## License
No license is specified in this repository. If you intend to open-source, add a LICENSE file (e.g., MIT, Apache-2.0).
