# DAT 0.9.0 migration record

This records the Android move from DAT 0.4.0 to the explicitly pinned 0.9.0 release. It is compilation and MockDeviceKit readiness work only; it does not verify Meta Adventurer hardware.

## Official changelog decisions

| Version | Relevant change | Code decision |
| --- | --- | --- |
| 0.5 | `Device.linkState` replaces the boolean availability signal; photo capture returns typed `DatResult<PhotoData, CaptureError>`. | Existing device observation remains flow-based; photo capture retains `DatResult` success/failure handling. No error descriptions are parsed for control flow. |
| 0.6–0.7 | Typed registration/session errors, explicit session state, and DAM support were introduced. | Registration continues through `Wearables` and lifecycle errors remain SDK results; no DAM opt-out metadata is declared. |
| 0.8 | `DeviceType.META_GLASSES`, `VideoFrame.isCodecConfig`, explicit stream start and MockDeviceKit `pairGlasses` were added. | Adventurer selection is deferred to Story 2.2; camera paths reject codec configuration and compressed frames before the raw-I420 path; MockDeviceKit uses its 0.8 factory/services APIs. |
| 0.9 | `DeviceSession.addStream/removeStream` were removed for `addCamera/removeCamera`; `Camera.stream` is the child stream; crash opt-out was added; DAM is mandatory. | Both legacy stream owners create a session, start it, attach a camera, then start `Camera.stream`; teardown stops the camera, detaches it, cancels collectors, and stops/discards the session. Analytics and crash reporting are opted out in the manifest. |

## Verified API surface

The DAT 0.9.0 SDK source and CameraAccess sample were checked for `Wearables.createSession`, `DeviceSession.start`, `DeviceSession.addCamera`, `Camera.stream.start`, `Camera.stop`, `DeviceSession.removeCamera`, `StreamState`, `VideoFrame.isCompressed`, and `VideoFrame.isCodecConfig`.

All raw-I420 consumers reject compressed or codec-configuration frames and frames whose buffer size is not exactly the contiguous I420 payload size (`width * height * 3 / 2`). No stream-start state is treated as a usable camera frame.

## Directed verification

`rg -n 'startStreamSession|StreamSession|addStream|removeStream' samples/CameraAccessAndroid/app/src/main/java` found no active legacy lifecycle usage. The DAT catalog and privacy-metadata check confirmed the shared `0.9.0` pin and both opt-outs.

`InstrumentationTest.startThenStopStreaming` configures a MockDeviceKit camera feed, starts the migrated UI path, captures a photo, and invokes UI teardown. It was updated for the DAT 0.9 MockDeviceKit API. The production consumers separately reject compressed, codec-configuration, and malformed raw-frame buffers before forwarding or preview conversion.

On 2026-08-30, the required memory-scoped, one-worker `:app:assembleDebug` was run with JDK 17, `ANDROID_HOME=/tmp/maria-toolchains/android-sdk`, and the existing expanded runner profile (1.5 GiB maximum). It completed successfully in 2m58s after Kotlin's daemon fell back to in-process compilation. `git diff --check` also passed. The GitHub Packages token was sourced only from the authenticated GitHub CLI for the Gradle process; no token was printed, stored, or committed.

On 2026-08-30 an Android Emulator and API-35 AOSP/ATD x86_64 images were installed under `/home/hermes/.local/share/macgyver-android-sdk`. The host has no `/dev/kvm`; the AVD therefore runs in software (TCG) emulation. The normal AOSP image was too slow for ddmlib's default five-second property-fetch timeout. The API-35 AOSP ATD image reduced memory use but still needs a 60-second `ddmlib.getprop.timeout.sec` for this host. A debug-only `x86_64` ABI was added so the instrumentation APK can target the AVD without changing the arm64-only release artifact.

The focused `:app:connectedDebugAndroidTest` command was attempted against that ATD with one Gradle worker, the isolated 1.5 GiB build scope, and the extended ddmlib timeout. It reached the connected-test task after packaging the x86_64 debug APK, but the software-emulated guest stopped responding to ADB and produced no test result; the Gradle invocation was cancelled after several minutes and the AVD was stopped. The MockDeviceKit suite is consequently **inconclusive**, not passing. Compilation and APK packaging prove SDK/build readiness only; they do not prove MockDeviceKit behavior or physical Meta Adventurer support. No physical Adventurer claim or hardware-support tag was created.

The reproducible Mac handoff is in [Android instrumentation handoff](handoffs/android-instrumentation-mac.md); [the accompanying prompt](prompts/run-android-instrumentation-on-mac.md) is ready to paste into a coding session on that machine.

On the accelerated API-35 ATD iMac, dependency resolution, APK packaging, installation, and instrumentation startup were verified. The first executed test exposed an initialization-order defect: `WearablesViewModel` constructed `AutoDeviceSelector` before `Wearables.initialize`, causing `WearablesException` during ViewModel creation. `deviceSelector` is now lazy and passes through `WearablesInit.ensure` before construction. The directed local `:app:assembleDebug` check passed after that correction; the three-test MockDeviceKit suite must be rerun on the iMac before it can be recorded as passing.

The first rerun advanced past `WearablesViewModel` and exposed the equivalent eager `AutoDeviceSelector` in `LiveKitSessionViewModel`. `glassesSelector` now uses the same lazy, initialization-gated pattern. The local directed build passed after this correction as well; MockDeviceKit execution remains pending the next iMac rerun.
