# Android instrumentation handoff — iMac

## Purpose

Finish the focused verification for the DAT 0.9 migration on a Mac that can accelerate an Android Virtual Device. This is a MockDeviceKit and UI-instrumentation check only. It must not be represented as validation of a physical Meta Adventurer camera.

## Current repository state

- Working branch: `feat/dat-0.9`, based on `4706c6a`; carry the current uncommitted migration changes to the Mac before testing.
- DAT artifacts are pinned together at `0.9.0` in `samples/CameraAccessAndroid/gradle/libs.versions.toml`.
- The migration replaces the obsolete stream-session ownership with DAT 0.9 `Wearables.createSession` → `DeviceSession.start` → `addCamera` → `Camera.stream.start` lifecycle in the stream owners.
- `InstrumentationTest` contains four current-route tests. Its rule chain grants runtime permissions first, then prepares the glasses capture-source preference and MockDeviceKit before `MainActivity` launches; `BLUETOOTH_CONNECT` must exist before MockDeviceKit initializes DAT. The camera cases assert the first usable mock frame, photo capture, idempotent teardown, and a shared raw-I420 gate that rejects compressed, codec-configuration, and malformed frames. The foreground service is deliberately started only after the DAT stream is `STREAMING` and is promoted from `onCreate`, addressing the previous Android 15 service-start deadline failure.
- The HomeScreen test alone writes an inoffensive `.invalid` gateway URL and `instrumentation-token` into app preferences. This bypasses the normal access-code gate for that UI route; it is not a real credential, is cleared during teardown, and must never be copied into production configuration. Camera lifecycle tests remain behind the gate and drive `StreamViewModel` directly, so no automatic LiveKit call competes for the mock device.
- The debug variant packages both `arm64-v8a` and `x86_64` native libraries for emulator testing. The release variant remains arm64-only.
- `:app:assembleDebug` already passed on the Linux host. The connected suite is not passing or failing: it is **inconclusive** because that host has no nested KVM and its software-emulated guest became unresponsive.

See [DAT migration record](../dat-migration.md) for the complete boundary and the prior verification evidence.

## Mac setup

Install Android Studio (or the Android command-line SDK) and a JDK 17. Create and boot one API-35 AVD before running Gradle:

- Apple Silicon: API 35 `arm64-v8a` system image.
- Intel Mac: API 35 `x86_64` system image.
- An AOSP or Android Test Device image is sufficient; Google Play is unnecessary.

Use `adb devices -l` to confirm the AVD is in `device` state. Do not run the test while it is `offline` or still booting.

The DAT dependencies are hosted in GitHub Packages. Authenticate GitHub CLI with an account that has access to `facebook/meta-wearables-dat-android` and package-read access, then source the token only for the Gradle process. Do not commit `local.properties`, an access token, or command output containing a token.

`Secrets.kt` is intentionally local and ignored by Git. Before compiling a fresh clone, create it from the checked-in example and leave `gatewayToken` empty; instrumentation does not need a real gateway identity:

```bash
cp app/src/main/java/io/github/rubensousa/macgyver/Secrets.kt.example \
  app/src/main/java/io/github/rubensousa/macgyver/Secrets.kt
```

## Verification command

From the repository root on macOS:

```bash
export JAVA_HOME="$(/usr/libexec/java_home -v 17)"
export ANDROID_HOME="$HOME/Library/Android/sdk"
export PATH="$ANDROID_HOME/platform-tools:$PATH"
export GITHUB_TOKEN="$(gh auth token)"

adb devices -l
cd samples/CameraAccessAndroid
cp app/src/main/java/io/github/rubensousa/macgyver/Secrets.kt.example \
  app/src/main/java/io/github/rubensousa/macgyver/Secrets.kt
./gradlew :app:connectedDebugAndroidTest --no-daemon --max-workers=1 --console=plain \
  -Dddmlib.getprop.timeout.sec=60 \
  -Dorg.gradle.jvmargs=-Xmx2g \
  -Dkotlin.daemon.jvm.options=-Xmx1g
```

The timeout override is harmless on an accelerated Mac and avoids flaky device-property discovery. A 64 GiB iMac has sufficient capacity for this command and an AVD; run one Gradle worker as requested.

## Completion evidence

The handoff is complete only when all of the following are collected:

1. `:app:connectedDebugAndroidTest` ends with `BUILD SUCCESSFUL`.
2. The connected-test report at `samples/CameraAccessAndroid/app/build/reports/androidTests/connected/debug/index.html` shows all four instrumentation tests passing.
3. `git diff --check` passes.
4. The result is added to `docs/dat-migration.md` as MockDeviceKit/SDK evidence only. Do not say that Adventurer hardware or a physical camera was verified.

If the command fails, return the complete error tail, the connected-test HTML report, `adb devices -l`, the Mac architecture, and the emulator API/image/ABI. Do not change production source merely to make the test pass without first identifying the failure.
