# Current-Technology and Reality-Check Review

- **Target:** `ARCHITECTURE-SPINE.md`
- **Date checked:** 2026-08-27
- **Evidence policy:** Primary official sources and the exact upstream repository commits named by the spine.
- **Verdict:** **REVISION REQUIRED** — the pinned versions and core DAT 0.9 API names are correct, but the spine still commits to an unproven combined toolchain, a teardown recipe that is stronger than the official 0.9 sample, and a brownfield ownership model that does not match VisionClaw. Live privacy and background-service defaults also need explicit decisions.

## Sources Inspected

- VisionClaw `main`/HEAD resolved to `fbc72a25686016d015de1099817f73c2bddbdea5`; inspected `samples/CameraAccessAndroid` build files, manifest, `StreamViewModel`, `WearablesViewModel`, and `StreamingService`: [VisionClaw pinned sample](https://github.com/Intent-Lab/VisionClaw/tree/fbc72a25686016d015de1099817f73c2bddbdea5/samples/CameraAccessAndroid).
- Meta DAT Android `main`/HEAD resolved to `81dfb51b9be26de5cd262bb1dcbb4b8d0d6bd2bc`; inspected changelog, README, official CameraAccess sample, and bundled official DAT guidance: [DAT 0.9 pinned repository](https://github.com/facebook/meta-wearables-dat-android/tree/81dfb51b9be26de5cd262bb1dcbb4b8d0d6bd2bc).
- Official package registry reports `mwdat-camera` 0.9.0 as the latest 0.9.x package, published 2026-08-03: [official GitHub package](https://github.com/facebook/meta-wearables-dat-android/packages/2759309).
- Android foreground-service requirements: [launch rules](https://developer.android.com/develop/background-work/services/fgs/launch) and [`connectedDevice` service type](https://developer.android.com/develop/background-work/services/fgs/service-types).
- Kotlin coroutine semantics: [`StateFlow`](https://kotlinlang.org/api/kotlinx.coroutines/kotlinx-coroutines-core/kotlinx.coroutines.flow/-state-flow/) and [`SharedFlow`](https://kotlinlang.org/api/kotlinx.coroutines/kotlinx-coroutines-core/kotlinx.coroutines.flow/-shared-flow/).
- Android interval-clock semantics: [`SystemClock`](https://developer.android.com/reference/android/os/SystemClock.html).

## Finding Summary

| ID | Severity | Reality-check result | Required disposition |
| --- | --- | --- | --- |
| CT-1 | High | Brownfield ownership/state model does not match VisionClaw | Fix architecture and migration seam |
| CT-2 | High | DAT 0.9 + baseline toolchain combination is unproven | Compile compatibility spike before claiming stack |
| CT-3 | High | Mandatory stop+remove teardown is not the official sample behavior | Correct to verified semantic teardown |
| CT-4 | High | Frame codec/preview pipeline is undecided across a real raw→HEVC fork | Pick and bind one milestone path |
| CT-5 | High | DAT analytics and crash reporting are enabled by default | Explicit opt-out or approved policy |
| CT-6 | High | Baseline foreground-service stop path preserves a known race fixed upstream | Adopt focused current-sample fix |
| CT-7 | Medium | DAT initialization/permission/registration order is not bound | Add lifecycle preconditions |
| CT-8 | Medium | Build-package and runtime DAT credentials are conflated | Separate credential channels |
| CT-9 | Medium | Battery availability is not proven as a stable public field | Treat as capability-dependent |

## High Findings

### CT-1 — The committed ownership model is not a ratification of VisionClaw

**Evidence.** In pinned VisionClaw, `StreamViewModel` owns the DAT `StreamSession`, frame/state collector jobs, phone-camera fallback, and WebRTC forwarding. `WearablesViewModel` separately owns device selection, registration, permissions, and device metadata. `StreamingService` owns only the foreground notification and wake lock. There is no process-scoped application container, `WearableRuntime`, single reducer, or adapter boundary. The official DAT 0.9 sample likewise keeps `DeviceSession`, `Camera`, and `Stream` in a screen-scoped `CameraViewModel`.

AD-2 and AD-3 therefore describe a deliberate replacement architecture, not a convention read from the brownfield source. AD-11 simultaneously says new code may replace only the Meta adapter and diagnostics seams. Those statements cannot both describe the actual change. AD-2 also says `WearableRuntime` owns DAT handles while AD-1 says only `MetaDatWearableAdapter` may import DAT types.

**Concrete fix.** Make the provider-neutral runtime the sole lifecycle **coordinator**, with `MetaDatWearableAdapter` exclusively owning `DeviceSession`, `Camera`, `Stream`, and DAT collectors. Add an explicit migration map from `WearablesViewModel`/`StreamViewModel` responsibilities into the new seams, including which existing phone-camera, WebRTC, settings, and UI paths remain in place. Reword AD-11 to permit that named, bounded state-ownership migration instead of claiming only adapter/diagnostics replacement.

### CT-2 — The combined target stack has not been shown compatible

**Evidence.** The spine accurately records the VisionClaw baseline: DAT 0.4.0, AGP 8.6.0, Kotlin 2.1.20, Compose BOM 2024.04.01, compile SDK 35, target SDK 34, min SDK 31, Gradle 8.14.1, and JVM target 1.8. The official DAT 0.9 CameraAccess sample at the pinned current commit uses DAT 0.9.0 with AGP 8.11.1, Kotlin 2.2.21, Compose BOM 2026.05.01, compile/target SDK 36, and Java/JVM 17.

The spine intentionally prefers the old host stack, which is reasonable for migration isolation, but no inspected source proves that DAT 0.9.0 compiles and runs with that older AGP/Kotlin/JVM combination. Listing both baseline and target in one Stack table can be misread as a verified combination.

**Concrete fix.** Label the combined stack as `candidate, compile verification required`. Make the first migration check a minimal DAT 0.9 dependency-resolution/compile spike on the unchanged baseline toolchain. If it fails, adopt only the minimum official-sample toolchain changes, record each in `docs/dat-migration.md`, and then pin the proven combination in the spine.

### CT-3 — AD-6 overstates the verified DAT 0.9 teardown recipe

**Evidence.** DAT 0.9.0 does verify `Wearables.createSession` → `DeviceSession.start` → `DeviceSession.addCamera` → `Camera.stream.start`, `Camera.stop()`/`close()`, and `DeviceSession.removeCamera()`. However, the official 0.9 CameraAccess sample tears down an active camera with `Camera.close()`/`Camera.stop()` and then clears its references; it does not blindly call both stop and `removeCamera()`. Official guidance describes `removeCamera()` as the operation to use when removing the capability before re-adding, not as an unconditional second call after `Camera.stop()`.

The spine's mandatory “stop camera, remove camera, then stop session” sequence is therefore not established by the official sample and may turn a successful idempotent stop into a redundant error path.

**Concrete fix.** Bind semantic postconditions instead: the stream is stopped, the camera capability is detached, and the session is stopped, with every operation idempotent. Use the exact 0.9 API sequence proven by a directed test; call `removeCamera()` only when the attached-capability state requires it. Because the canonical companion currently mandates both operations, update that source and the spine together if the test confirms the official sample's stop-only detach behavior.

### CT-4 — The migration crosses a real raw-I420 versus compressed-HEVC fork without choosing one

**Evidence.** Pinned VisionClaw 0.4 requests the default/raw stream and converts copied I420 bytes to NV21, JPEG, then `Bitmap`; it forwards that bitmap to existing WebRTC. The official DAT 0.9 CameraAccess sample explicitly sets `compressVideo = true`, consumes HEVC, uses `VideoFrame.isCodecConfig`, `isCompressed`, and `presentationTimeUs`, and decodes directly to a `Surface` so preview/recording can continue in the background.

AD-4 correctly excludes codec-configuration-only packets from first-frame readiness, and AD-5 correctly requires app-owned bytes. But `format` remains open, while preview is current scope and AI encoding alone is deferred. An adapter emitting HEVC and a preview expecting raw pixels would both satisfy the written rule.

**Concrete fix.** Pick one milestone representation after the compatibility spike. Either preserve raw I420 and bind plane order/stride/orientation plus the existing preview conversion, or adopt compressed HEVC and bind codec-config handling, presentation timestamps, decoder/surface ownership, and what is exposed through `WearableAdapter`. Keep future AI encoding separate from the chosen preview format.

### CT-5 — DAT's live telemetry defaults contradict a privacy-safe-by-default posture unless decided

**Evidence.** The official DAT 0.9 README states that analytics are enabled when `com.meta.wearable.mwdat.ANALYTICS_OPT_OUT` is absent or false, and SDK crash reporting is enabled when `com.meta.wearable.mwdat.CRASH_REPORTING_OPT_OUT` is absent or false. Pinned VisionClaw's manifest sets neither key. The spine governs app logs but says nothing about SDK-managed collection.

**Concrete fix.** For this private wearable-assistant milestone, add both manifest metadata keys with value `true` unless the user explicitly approves Meta analytics/crash collection after reviewing the developer terms. Record the decision in the privacy/configuration convention and verify the generated manifest in the baseline/migration evidence.

### CT-6 — “Preserve compatible behavior” would preserve a foreground-service race already fixed upstream

**Evidence.** VisionClaw's `StreamingService.stop()` calls `stopService()` directly. The current official DAT 0.9 sample routes stop through a foreground-service STOP action, calls `startForeground()` first, and only then stops itself; its source documents that this avoids `ForegroundServiceDidNotStartInTimeException` when stop races a pending start. The official sample also uses `connectedDevice`, the corresponding manifest permission, a notification, and a wake lock, which otherwise match VisionClaw and Android's current foreground-service requirements.

**Concrete fix.** Treat the STOP-action protocol as a focused compatibility adaptation under AD-11, not an unrelated rewrite. Preserve the existing service boundary and `connectedDevice` type, but adopt and test the current start/stop handshake, including repeated start/stop, background/foreground, and lock/unlock cases.

## Medium Findings

### CT-7 — Initialization, registration, and permission preconditions are visible but not bound

The official DAT guidance requires Android runtime permissions before `Wearables.initialize(context)`, initialization before any SDK API, registration observation, camera-permission checking through `Wearables.checkPermissionStatus`, and a started `DeviceSession` before `addCamera`. VisionClaw currently spreads those responsibilities across `MainActivity`, `WearablesInit`, `WearablesViewModel`, and `StreamViewModel`.

**Concrete fix.** Add one provider-neutral startup contract and assign each gate to the adapter/runtime: required Android permission → initialize once → observe registration/device eligibility → camera permission → create/start session → add/start camera. Map each failed gate to the existing typed failure layers and diagnostics fields.

### CT-8 — Two different credential channels need distinct rules

The official DAT repository uses `GITHUB_TOKEN` or `github_token` in ignored `local.properties` for GitHub Packages. Runtime DAT registration uses manifest placeholders for `mwdat_application_id` and `mwdat_client_token`. The spine's single “DAT package credentials” phrase can cause build credentials and runtime app credentials to be wired or redacted inconsistently.

**Concrete fix.** Name both channels: package-read token only in `GITHUB_TOKEN`/ignored `local.properties`; runtime application ID/client token through non-checked-in manifest placeholders or generated resources. Redact both, and verify neither appears in the merged manifest artifact committed to evidence.

### CT-9 — Battery should remain capability-dependent

The official 0.9 repository clearly documents `Wearables.getDeviceState(id)` and thermal state. Public source evidence for the exact stable battery field is less explicit than for thermal, while the canonical SPEC already qualifies health fields with “when exposed by DAT.”

**Concrete fix.** Keep battery in the provider-neutral snapshot as optional/capability-reported and render `NOT_AVAILABLE` when the selected DAT/device combination does not expose it. Do not block the diagnostics screen or fabricate a value.

## Decisions Confirmed by Current Sources

- **Versions:** VisionClaw `fbc72a2` and Meta DAT Android `81dfb51` are still the respective upstream `main` HEADs on 2026-08-27. DAT 0.9.0 is the latest published 0.9.x package.
- **DAT lifecycle names:** `Wearables.createSession`, `DeviceSession.start`, `DeviceSession.addCamera`, `Camera.stream`, `Stream.start`, `Camera.stop`/`close`, and `DeviceSession.removeCamera` exist in current 0.9 guidance/source.
- **Adventurer type:** `DeviceType.META_GLASSES` and MockDeviceKit's `GlassesModel.META_GLASSES` were added in DAT 0.8 and remain in the 0.9 changelog.
- **First-frame filter:** `VideoFrame.isCodecConfig` is a real current API; codec-config-only packets must not establish usable-frame readiness.
- **Typed observation:** current DAT exposes device-session state/errors and stream state/error flows; camera state is part of the 0.9 consolidated camera capability.
- **Coroutines:** `StateFlow` is suitable for current truth because it is equality-conflated; a separately configured `SharedFlow`/event stream is appropriate when transitions must not be conflated.
- **Timing:** Android `elapsedRealtime()`/`elapsedRealtimeNanos()` are monotonic and include deep sleep, so they are suitable for startup/reconnect latency across lock/sleep events.
- **Foreground service type:** `connectedDevice` is the correct Android type for interaction with Bluetooth glasses when its manifest permission and a granted `BLUETOOTH_CONNECT` prerequisite are present. Both pinned samples already declare the relevant permissions.
- **Structural anchors:** `samples/CameraAccessAndroid`, the `wearables` and `stream` packages, `StreamingService`, phone-camera fallback, WebRTC/LiveKit, settings, and existing state ViewModels all exist at the pinned VisionClaw commit.
- **Mock testing:** DAT 0.9 MockDeviceKit supports registration, permissions, device availability, and camera media simulation, so the proposed instrumentation layer is real. It still cannot replace physical Adventurer acceptance.

## Gate Decision

Keep the spine in `draft`. Correct CT-1 and CT-3, turn CT-2 into an explicit pre-final compatibility gate, choose the CT-4 frame path, and decide CT-5 privacy defaults. CT-6 should be included in the focused migration rather than preserved unchanged. The remaining medium findings can be incorporated into the same revision without changing the overall paradigm.
