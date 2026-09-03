---
title: 'Migrate and Pin the DAT 0.9 Baseline'
type: 'feature'
created: '2026-08-30'
status: 'in-review'
review_loop_iteration: 0
baseline_commit: '4706c6aaae9d7f0daeb5bb9740656c2b74946b3e'
context:
  - '{project-root}/AGENTS.md'
  - '{project-root}/_bmad-output/implementation-artifacts/epic-2-context.md'
  - '{project-root}/_bmad-output/specs/spec-meta-adventurer-camera-milestone/migration-and-delivery.md'
---

<frozen-after-approval reason="human-owned intent — do not modify unless human renegotiates">

## Intent

**Problem:** The Android baseline is pinned to DAT 0.4.0 and owns camera streaming through the removed `Wearables.startStreamSession` / `StreamSession` model. It cannot provide the DAT 0.9 camera foundation required for Meta Adventurer without an auditable API migration.

**Approach:** Pin all DAT modules to 0.9.0 on the existing baseline toolchain, document every applicable 0.4.0-to-0.9.0 change, and replace only the legacy session/stream ownership paths with the verified 0.9 `DeviceSession` → `Camera` → `Camera.stream` lifecycle. Establish compilation and MockDeviceKit readiness only; physical Adventurer support remains unverified.

## Boundaries & Constraints

**Always:** Preserve AGP 8.6.0, Gradle 8.14.1, Kotlin 2.1.20, Compose BOM 2024.04.01, SDK 35/34/31, the phone-camera path, and existing product behavior unless DAT incompatibility forces a documented focused adaptation. Pin `mwdat-core`, `mwdat-camera`, and `mwdat-mockdevice` to exactly 0.9.0. Record official changelog decisions and exact verified APIs in `docs/dat-migration.md`. Keep DAT behind the existing Android capture boundary; use typed DAT results/errors and provider-neutral mapping seams rather than parsing error descriptions. Set analytics and crash-reporting opt-outs; keep credentials in environment or ignored local configuration. Run any Gradle work only through the memory runner with one worker and JDK 17.

**Ask First:** DAT 0.9 does not support the required uncompressed I420 frame path; the verified 0.9 artifact/API differs materially from the official migration evidence; a toolchain/dependency upgrade beyond DAT is needed; or a change would alter phone-camera, LiveKit, gateway, realtime, operator, tool, or physical-hardware scope.

**Never:** Retain `Wearables.startStreamSession`, `StreamSession`, `DeviceSession.addStream`, or `removeStream` in active Android camera code; silently treat compressed or codec-configuration frames as I420; commit package tokens or DAT credentials; claim a build, mock, or phone-camera result proves Adventurer hardware; or tag hardware support.

## I/O & Edge-Case Matrix

| Scenario | Input / State | Expected Output / Behavior | Error Handling |
|----------|--------------|---------------------------|----------------|
| DAT compatibility migration | Authenticated DAT 0.9.0 artifacts and unchanged baseline toolchain | Catalog resolves 0.9.0; lifecycle uses created/started `DeviceSession`, attached `Camera`, then `Camera.stream.start()` | Stop before source migration if exact artifact/API evidence disagrees |
| Legacy lifecycle removal | Existing stream and LiveKit glasses paths | No active call to `startStreamSession` or direct stream attachment remains | Compile failures identify each signature/state adaptation; do not add compatibility shims for removed APIs |
| Unsupported raw frames | DAT reports compressed or codec-configuration-only frames | Frame is rejected from raw-I420 path and no readiness/counter claim occurs | Halt for a representation decision instead of decoding or forwarding by assumption |
| Repeated shutdown | Active, partial, or failed camera setup | Collectors cancel once; stream/camera stops, camera detaches, then session stops/discards | Teardown is idempotent and typed failures remain attributable |

</frozen-after-approval>

## Code Map

- `samples/CameraAccessAndroid/gradle/libs.versions.toml` -- shared DAT version alias currently `0.4.0`; pins core, camera, and MockDeviceKit together.
- `samples/CameraAccessAndroid/app/build.gradle.kts` -- currently places all DAT modules in `implementation`; determine the least invasive valid MockDeviceKit test scope after existing usage inspection.
- `samples/CameraAccessAndroid/settings.gradle.kts` -- GitHub Packages repository and environment/ignored-local token lookup; preserve without recording a token.
- `samples/CameraAccessAndroid/app/src/main/AndroidManifest.xml` -- add DAT analytics/crash-reporting opt-out metadata; retain current application identity and foreground-service declaration.
- `samples/CameraAccessAndroid/app/src/main/java/io/github/rubensousa/macgyver/stream/StreamViewModel.kt` -- legacy `StreamSession` owner, video collection, photo capture, and teardown; migrate to the DAT 0.9 camera lifecycle only.
- `samples/CameraAccessAndroid/app/src/main/java/io/github/rubensousa/macgyver/livekit/LiveKitSessionViewModel.kt` -- second legacy glasses lifecycle feeding LiveKit; apply the same lifecycle removal without redesigning LiveKit.
- `samples/CameraAccessAndroid/app/src/main/java/io/github/rubensousa/macgyver/stream/StreamUiState.kt` and `ui/StreamScreen.kt` -- adapt removed stream-session state types only as required by 0.9 compilation.
- `samples/CameraAccessAndroid/app/src/main/java/io/github/rubensousa/macgyver/wearables/WearablesInit.kt` and `wearables/WearablesViewModel.kt` -- registration/device APIs to recompile against 0.9, but explicit `META_GLASSES` selection belongs to Story 2.2.
- `docs/dat-migration.md` -- new changelog-to-code-decision record and compilation evidence.

## Tasks & Acceptance

**Execution:**
- [x] `gradle/libs.versions.toml` and `app/build.gradle.kts` -- pin DAT modules to 0.9.0 and set MockDeviceKit scope only where existing tests support it -- establish one auditable dependency baseline.
- [x] `docs/dat-migration.md` -- map official 0.5–0.9 changes to code decisions, including Device/LinkState, typed results, registration errors, explicit session state, `VideoFrame` codec flags, `META_GLASSES`, Camera consolidation, DAM, and privacy metadata -- prevent undocumented behavioral drift.
- [x] `AndroidManifest.xml` -- declare `com.meta.wearable.mwdat.ANALYTICS_OPT_OUT=true` and `com.meta.wearable.mwdat.CRASH_REPORTING_OPT_OUT=true` -- preserve the milestone privacy default.
- [x] `stream/StreamViewModel.kt`, `livekit/LiveKitSessionViewModel.kt`, and affected stream UI state -- replace both obsolete lifecycle paths with the verified 0.9 create/start session, `addCamera`, `Camera.stream.start`, and ordered idempotent teardown sequence -- remove API-incompatible ownership without a broad runtime rewrite.
- [x] Android test/MockDeviceKit surface -- add or adapt focused lifecycle coverage for create/start, camera attachment, stream start, rejected non-raw frames, and repeated teardown -- verify SDK integration without hardware claims.
- [x] `docs/dat-migration.md` -- record memory-scoped directed checks and `assembleDebug` result, including any inconclusive host-capacity outcome -- distinguish compilation readiness from physical acceptance.

**Acceptance Criteria:**
- Given the baseline toolchain and authenticated package access, when the catalog resolves, then every DAT module uses exactly 0.9.0 and no unrelated toolchain version changes.
- Given the migrated Android camera paths, when source is inspected or compiled, then no active legacy session/stream factory remains and each path reaches stream startup through `DeviceSession.addCamera(...).stream`.
- Given DAT 0.9 frame delivery, when a frame is compressed, codec configuration, or not verified as contiguous raw I420, then it cannot satisfy readiness or enter the raw frame path.
- Given normal or failed startup, when teardown runs repeatedly, then it safely cancels collectors, stops/detaches camera capability, and stops/discards the session without a second lifecycle owner.
- Given the manifest and migration record, when privacy and migration decisions are reviewed, then analytics/crash reporting are opted out, credentials are absent, and each relevant changelog change has a code decision.
- Given the directed MockDeviceKit and memory-scoped build checks, when they complete, then the result is recorded as compilation/SDK readiness only and never as physical Meta Adventurer verification.

## Spec Change Log

## Design Notes

DAT 0.9 consolidates camera ownership: `DeviceSession.addStream` was removed in favor of `DeviceSession.addCamera`, whose `Camera.stream` child is started separately and whose camera is detached with `removeCamera()`. This story keeps that ownership localized in the two existing legacy paths. It deliberately does not implement the provider-neutral runtime, deterministic Adventurer selection, first-frame reducer, or preview redesign; those belong to Stories 2.2–2.5.

## Verification

**Commands:**
- `rg -n 'startStreamSession|StreamSession|addStream|removeStream' samples/CameraAccessAndroid/app/src/main/java` -- expected: no active legacy DAT lifecycle usage after migration.
- `rg -n 'mwdat|ANALYTICS_OPT_OUT|CRASH_REPORTING_OPT_OUT' samples/CameraAccessAndroid/{gradle/libs.versions.toml,app/build.gradle.kts,app/src/main/AndroidManifest.xml}` -- expected: DAT 0.9.0 and both opt-outs.
- `JAVA_HOME=/tmp/maria-toolchains/jdk-17 /home/hermes/.local/bin/codex-memory-run ./gradlew --no-daemon --no-parallel --max-workers=1 -Dorg.gradle.java.home=/tmp/maria-toolchains/jdk-17 :app:assembleDebug` from `samples/CameraAccessAndroid` -- expected: one-worker memory-scoped result recorded in `docs/dat-migration.md`.

**Manual checks (if no CLI):**
- Compare every `docs/dat-migration.md` decision with the official DAT 0.5–0.9 changelog and confirm no physical-adventurer claim appears.

## Suggested Review Order

**Lifecycle ownership and teardown**

- Follow the primary migrated DAT session from creation through camera stream startup.
  [`StreamViewModel.kt:122`](../../samples/CameraAccessAndroid/app/src/main/java/io/github/rubensousa/macgyver/stream/StreamViewModel.kt#L122)

- Compare the LiveKit glasses owner’s equivalent session and camera lifecycle.
  [`LiveKitSessionViewModel.kt:663`](../../samples/CameraAccessAndroid/app/src/main/java/io/github/rubensousa/macgyver/livekit/LiveKitSessionViewModel.kt#L663)

- Verify generation fencing and ordered cleanup for repeated or late teardown.
  [`StreamViewModel.kt:303`](../../samples/CameraAccessAndroid/app/src/main/java/io/github/rubensousa/macgyver/stream/StreamViewModel.kt#L303)

- Confirm the second owner isolates camera, detach, and session-stop failures.
  [`LiveKitSessionViewModel.kt:905`](../../samples/CameraAccessAndroid/app/src/main/java/io/github/rubensousa/macgyver/livekit/LiveKitSessionViewModel.kt#L905)

**Raw-frame admission**

- Inspect the shared exact-size, even-dimension I420 gate used by both consumers.
  [`StreamViewModel.kt:63`](../../samples/CameraAccessAndroid/app/src/main/java/io/github/rubensousa/macgyver/stream/StreamViewModel.kt#L63)

- Confirm the LiveKit bridge cannot receive compressed, codec-only, or malformed payloads.
  [`GlassesVideoCapturer.kt:54`](../../samples/CameraAccessAndroid/app/src/main/java/io/github/rubensousa/macgyver/livekit/GlassesVideoCapturer.kt#L54)

**Evidence and supporting coverage**

- Review first-frame readiness, photo cleanup, and idempotent test teardown.
  [`InstrumentationTest.kt:125`](../../samples/CameraAccessAndroid/app/src/androidTest/java/io/github/rubensousa/macgyver/InstrumentationTest.kt#L125)

- Check the official version decisions and the explicit current verification limitation.
  [`dat-migration.md:7`](../../docs/dat-migration.md#L7)

- Confirm the sprint remains reviewable while fresh checkout verification is deferred.
  [`sprint-status.yaml:45`](sprint-status.yaml#L45)

### Review Findings

- [x] [Review][Defer] Story closure waits for current-checkout verification [spec frontmatter] — deferred, pre-existing; keep Story 2.1 open until the prescribed build and connected-test run is available for this checkout.
- [x] [Review][Defer] MockDeviceKit scope remains an intentional implementation exception [app/build.gradle.kts:76] — deferred, pre-existing; existing main-source usage is documented, and relocation to a test/debug source set remains future cleanup.
- [x] [Review][Patch] Session startup can race with generation retirement [StreamViewModel.kt:129; LiveKitSessionViewModel.kt:673] — resolved with generation claims and serialized DAT start operations.
- [x] [Review][Patch] Stale callbacks can tear down a newer generation after a check-then-stop race [StreamViewModel.kt:151; LiveKitSessionViewModel.kt:698] — resolved with generation-aware stop operations.
- [x] [Review][Patch] Teardown releases lifecycle state before cancelling and clearing all shared handles [StreamViewModel.kt:303; LiveKitSessionViewModel.kt:905] — resolved by snapshotting and clearing owned handles under the lifecycle lock before cleanup.
- [x] [Review][Patch] External SDK and bridge calls run while lifecycle locks are held [StreamViewModel.kt:202; LiveKitSessionViewModel.kt:746] — resolved with separate serialized DAT and frame-delivery operation locks.
- [x] [Review][Patch] Synchronous DAT exceptions can escape camera and stream setup [StreamViewModel.kt:193; LiveKitSessionViewModel.kt:253; LiveKitSessionViewModel.kt:743; LiveKitSessionViewModel.kt:852] — resolved with guarded session, camera, and stream start/setup calls.
- [x] [Review][Patch] Normal DAT flow completion does not release the active lifecycle [StreamViewModel.kt:209; LiveKitSessionViewModel.kt:760] — resolved with normal-completion cleanup for session and camera flows.
- [x] [Review][Patch] Camera-detach DatResult failures are ignored during ordered teardown [StreamViewModel.kt:353; LiveKitSessionViewModel.kt:947] — resolved by observing typed detach failures while continuing ordered cleanup.
- [x] [Review][Defer] LiveKit glasses lifecycle lacks direct integration coverage [LiveKitSessionViewModel.kt:663] — deferred, pre-existing
- [x] [Review][Defer] Raw-frame consumer rejection is covered only through the shared predicate [GlassesVideoCapturer.kt:54] — deferred, pre-existing
- [x] [Review][Defer] Generation fencing has no deterministic late-callback test [StreamViewModel.kt:122; LiveKitSessionViewModel.kt:663] — deferred, pre-existing
- [x] [Review][Defer] Teardown tests do not observe ordered DAT cleanup or wait for asynchronous release [StreamViewModel.kt:303; InstrumentationTest.kt:144] — deferred, pre-existing
- [x] [Review][Defer] Photo results from retired generations have no test coverage [StreamViewModel.kt:397] — deferred, pre-existing
- [x] [Review][Defer] Session timeout, setup failure, and foreground-service failure paths lack focused tests [StreamViewModel.kt:170; LiveKitSessionViewModel.kt:720] — deferred, pre-existing
- [x] [Review][Defer] Practical frame-size limits need hardware-tuned validation before tightening the raw gate [StreamViewModel.kt:84] — deferred, pre-existing
