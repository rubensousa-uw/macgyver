# Android instrumentation record — iMac

## Purpose

Preserve the completed iMac verification procedure and evidence for the DAT 0.9 migration. This is historical MockDeviceKit UI-instrumentation evidence only. It does not validate a physical Meta Adventurer camera.

## Verified state

- Verification ran from `feat/dat-0.9` at commit `cc4de1b8fe1fe08a60d4cd4fb6b3768096134edb`.
- DAT artifacts were pinned together at `0.9.0` in `samples/CameraAccessAndroid/gradle/libs.versions.toml`.
- Both stream owners used `Wearables.createSession` → `DeviceSession.start` → `addCamera` → `Camera.stream.start`.
- `InstrumentationTest` contained four cases covering the glasses route, first usable MockDeviceKit frame and photo capture, idempotent camera teardown, and rejection of compressed, codec-configuration, and malformed raw-I420 frames.
- The debug variant packaged `arm64-v8a` and `x86_64` native libraries for emulator testing; release remained arm64-only.

The full chronology and evidence boundaries are in the [DAT migration record](../dat-migration.md).

## Historical environment and command

The successful run used an accelerated API-35 x86_64 AVD on an Intel iMac with JDK 17. DAT dependencies were resolved from GitHub Packages using a process-local token that was not committed. `Secrets.kt` was created from its checked-in example with no real gateway identity.

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

## Recorded result

- On 2026-09-03, `:app:connectedDebugAndroidTest` ended with `BUILD SUCCESSFUL` on the API-35 `macgyver_api35_x86_64` AVD.
- The connected report recorded four tests, zero failures, and zero skips.
- `git diff --check` passed.
- A separately approved bounded, one-worker `:app:assembleDebug` run also completed successfully on the same Mac.
- These results establish DAT 0.9/MockDeviceKit SDK readiness only; they do not establish physical Adventurer support.
