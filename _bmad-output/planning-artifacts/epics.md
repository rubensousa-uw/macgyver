---
stepsCompleted:
  - step-01-validate-prerequisites
  - step-02-design-epics
  - step-03-create-stories
  - step-04-final-validation
inputDocuments:
  - _bmad-output/specs/spec-meta-adventurer-camera-milestone/SPEC.md
  - _bmad-output/specs/spec-meta-adventurer-camera-milestone/acceptance.md
  - _bmad-output/specs/spec-meta-adventurer-camera-milestone/architecture-diagrams.md
  - _bmad-output/specs/spec-meta-adventurer-camera-milestone/migration-and-delivery.md
  - _bmad-output/specs/spec-meta-adventurer-camera-milestone/wearable-state-and-diagnostics.md
  - _bmad-output/planning-artifacts/architecture/architecture-maria-2026-08-27/ARCHITECTURE-SPINE.md
---

# maria - Epic Breakdown

## Overview

This document provides the complete epic and story breakdown for maria, decomposing the requirements from the canonical SPEC and companions, Architecture requirements, and the explicitly accepted absence of a separate UX design contract into implementable stories.

> **Current identity and historical provenance (2026-08-30):** `macgyver` is the current product and repository identity. References to `maria`, VisionClaw, `fbc72a2`, `baseline`, and `baseline-visionclaw` in Epic 1 and Story 1.1 record historical import/baseline provenance; they are not current naming requirements. Story 1.1 and Epic 1 are complete after the approved Sprint Change Proposal `sprint-change-proposal-2026-08-30.md`.

## Requirements Inventory

### Functional Requirements

FR1: A developer can create the personal GitHub repository `maria` from `Intent-Lab/VisionClaw`, retain upstream traceability, and use `samples/CameraAccessAndroid` as the Android application base.

FR2: A developer can build the unmodified VisionClaw Android baseline and record the source commit, DAT, AGP, Kotlin, SDK levels, warnings, known problems, build result, and relevant Android files in `docs/baseline.md`.

FR3: A developer can migrate the baseline to the pinned stable DAT 0.9.x release and record every breaking or behaviorally relevant baseline-to-target changelog item and its code decision in `docs/dat-migration.md`.

FR4: The application can discover, accept, and select Meta Adventurer as `DeviceType.META_GLASSES` without requiring or assuming `RAYBAN_META`.

FR5: The application can expose the selected device identifier, name, type, capabilities, compatibility, link state, battery state, and thermal state when available.

FR6: The application can initiate registration through Meta AI, expose `Wearables.registrationState` and `Wearables.registrationErrorStream`, and prevent camera-session startup until registration reaches `REGISTERED`.

FR7: The application can create and start a DAT device session, attach and start a camera stream, and stop and detach the camera before stopping and discarding the session.

FR8: The application can represent and observe `NO_DEVICE`, `DEVICE_CONNECTED`, `SESSION_STARTING`, `SESSION_READY`, `CAMERA_STARTING`, `STREAM_WAITING`, `FIRST_FRAME_RECEIVED`, `STREAM_ACTIVE`, `STREAM_PAUSED`, `STREAM_FAILED`, and `DISCONNECTED` using only the canonical transition graph.

FR9: The application reports vision readiness only after a callback carries usable image data; SDK stream-started states and codec-configuration-only packets cannot satisfy readiness.

FR10: A tester can view real Meta Adventurer camera frames in an on-device live preview.

FR11: The application can measure stream startup latency, first-frame latency, reconnect latency, rolling incoming FPS, total received frames, and total forwarded frames for each session.

FR12: The application can expose one developer diagnostics screen containing every device, DAT registration, session, camera, stream, frame, telemetry, audio-route, battery, thermal, future-provider placeholder, gateway placeholder, tool-call placeholder, and current-error field defined by the diagnostics contract.

FR13: Diagnostics for future realtime, Hermes, tool-call, and unwired audio integrations display truthful `NOT_CONFIGURED` or `NOT_AVAILABLE` values without implementing those integrations.

FR14: The application can produce structured session records containing session ID, device type, DAT state, camera state, first-frame latency, received and forwarded frame counts, disconnect reason, and errors classified by layer.

FR15: The application can classify failures as `DEVICE`, `DAT`, `CAMERA`, `NETWORK`, or `AUTH`, retain the typed DAT cause for diagnosis, and avoid reporting hardware or camera faults as generic AI failures.

FR16: A user can explicitly start and stop streaming; an explicit stop clears desired streaming intent and prevents automatic resurrection.

FR17: App background/foreground and phone lock/unlock preserve desired streaming intent, while process death resets intent and does not automatically restart the camera.

FR18: The application can recover from device disconnect/reconnect, glasses power cycling, app background/foreground, phone lock/unlock, Wi-Fi/mobile-data changes, Meta AI open/closed states, and repeated camera start/stop.

FR19: Recovery can recreate failed sessions from `SESSION_STARTING` and retry camera or stream startup from `CAMERA_STARTING` only while the current session remains usable and desired streaming remains true.

FR20: A tester can record the fixed physical environment and, for every acceptance case, capture transitions, first-frame result, latency, recovery result, and any layer-specific error.

FR21: Physical acceptance requires explicit `META_GLASSES` detection, DAT 0.9.x camera startup, real preview frames, `FIRST_FRAME_RECEIVED` before readiness, consistent diagnostics and telemetry, and clean disconnect/reconnect recovery.

FR22: The application keeps forwarded-frame count at zero and sends no frame to an AI layer during this milestone.

FR23: Delivery can distinguish baseline-known-good, compiled/ready-for-physical-validation, and physically verified states; `adventurer-camera-working` is created only after every physical gate passes.

FR24: Compatible VisionClaw phone-camera fallback, frame throttling, audio, Gemini Live, OpenClaw routing, reconnect, WebRTC, settings, preview, and session-state behavior remains available unless a documented DAT incompatibility requires a focused adaptation.

### NonFunctional Requirements

NFR1: Meta DAT types and lifecycle calls are isolated to the Meta adapter; `WearableAdapter`, runtime, diagnostics, future realtime providers, operators, and tools remain provider-neutral and independently evolvable.

NFR2: One process-scoped runtime coordinates commands, reduction, recovery, and telemetry, while the Meta adapter exclusively owns DAT device, session, camera, stream, and collector handles.

NFR3: One serialized reducer is the sole writer of immutable canonical state; commands, SDK callbacks, transitions, and frames are generation-fenced so retired lifecycle events cannot mutate current state.

NFR4: Lifecycle teardown is serialized, idempotent, safe to repeat, cancels each collector once, and never reuses a terminally stopped session.

NFR5: Frame handling has bounded memory and backpressure: app-owned bytes leave the DAT callback through a latest-only bounded channel, and slow consumers cannot block the SDK collector.

NFR6: I420-to-display conversion runs off the main thread, frame-rate traffic does not cause unbounded Compose recomposition, and FPS presentation updates at most once per second.

NFR7: Ordering, latency, and FPS calculations use Android monotonic elapsed time; wall time is presentation-only.

NFR8: Recovery uses exactly one cancellable controller, bounded hardware-tuned retry timing, and no busy loop.

NFR9: Logs, diagnostics, configuration, and test fixtures never expose API/OAuth secrets, access tokens, DAT credentials, or raw private audio; DAT analytics and crash reporting are disabled by default.

NFR10: Every current failure exposes a typed layer and code with a redacted message; production behavior never branches on localized human-readable DAT descriptions.

NFR11: The latest 256 reducer-accepted transitions remain available for diagnosis without unbounded history growth.

NFR12: Compilation, emulator tests, MockDeviceKit, and phone-camera fallback cannot be represented as physical Meta Adventurer verification.

NFR13: Broad AGP, Kotlin, Compose, Android SDK, or unrelated dependency modernization is prohibited unless baseline compatibility forces a separately documented focused change.

NFR14: On the 8 GiB no-swap host, `hindsight.service` remains running; authorized memory-intensive Gradle work uses `/home/hermes/.local/bin/codex-memory-run`, with one heavy command and one worker at a time and no unscoped fallback.

### Additional Requirements

- AR1: Treat VisionClaw upstream commit `fbc72a2` and `samples/CameraAccessAndroid` as the starter seed; ratify the seed against the exact imported commit before stories become implementation-ready.
- AR2: Preserve the baseline stack unless compatibility proves otherwise: DAT 0.4.0, AGP 8.6.0, Gradle 8.14.1, Kotlin 2.1.20, Compose BOM 2024.04.01, compile SDK 35, target SDK 34, and min SDK 31.
- AR3: Test DAT 0.9.0 on the unchanged baseline toolchain with a minimal compatibility `assembleDebug` before pinning the combined stack.
- AR4: Create provider-neutral `WearableAdapter`, `WearableRuntime`, `WearableModels`, `WearableTelemetry`, and preview-renderer seams; place DAT-specific work in `wearables/meta` and diagnostics projections in `diagnostics`.
- AR5: Supply the process-scoped runtime through a small application container without adding a dependency-injection framework unless implementation evidence forces a separate decision.
- AR6: Publish canonical state through `StateFlow`, accepted transitions through a live transition flow, and the latest 256 transitions through reducer-owned bounded history.
- AR7: Attach one opaque session ID and one monotonic generation ID to every transition, frame envelope, metric, and structured record.
- AR8: Copy each usable DAT callback into an app-owned contiguous I420 envelope containing session ID, generation ID, sequence, monotonic timestamp, width, height, rotation, format, and Y/U/V bytes.
- AR9: Request `VideoQuality.MEDIUM` at 24 FPS with compression disabled; publish frames through a latest-only bounded channel and count each usable frame before conflation.
- AR10: Follow the DAT 0.9 lifecycle `Wearables.createSession` → start `DeviceSession` → `DeviceSession.addCamera` → `Camera.stream.start`.
- AR11: Teardown must establish ordered postconditions: terminate stream/camera, detach the camera capability, cancel collectors once, stop the session, and discard it; `docs/dat-migration.md` records the verified exact calls.
- AR12: Keep `desiredStreaming` separate from observed state and target recovery at the failed layer without spawning competing retry loops.
- AR13: Define telemetry per session: request-to-SDK-streaming startup latency, request-to-first-usable-frame latency, disconnect-to-first-usable-recovered-frame latency, usable frames over a rolling five-second FPS window, and monotonic counters.
- AR14: Model failures as `WearableFailure(layer, code, recoverability, redactedMessage, causeType)` and keep one current error plus bounded transition evidence.
- AR15: Set `com.meta.wearable.mwdat.ANALYTICS_OPT_OUT=true` and `com.meta.wearable.mwdat.CRASH_REPORTING_OPT_OUT=true`; any future opt-in requires an explicit privacy decision.
- AR16: Store GitHub Packages and DAT credentials only in environment variables or ignored `local.properties`; no secret enters source control.
- AR17: Verify reducer and telemetry behavior with pure tests, DAT integration with DAT 0.9 MockDeviceKit instrumentation, and hardware behavior with the physical Adventurer matrix.
- AR18: Preserve branch progression `main` → `baseline` → `feat/dat-0.9` → `feat/adventurer`, the baseline commit intent `chore: establish working Android baseline`, and final commit intent `feat: support Meta Adventurer with DAT 0.9`.
- AR19: Execute physical acceptance on Meta Adventurer Standard 49 mm firmware v127.1 paired with Samsung Galaxy Z Fold7, Android 16, and Meta AI 286.1.0.17.162; record any version change at test time.
- AR20: Confirm Developer Mode for the linked Adventurer and record `Wearables.registrationState`; the camera-session gates require `REGISTERED`.
- AR21: Treat the current canonical SPEC decisions for personal GitHub ownership and the fixed phone/Meta AI environment as superseding the corresponding stale unresolved items in the architecture's Deferred section.
- AR22: There is no milestone backend, cloud video path, realtime provider, gateway, operator, tool deployment, production Bluetooth audio, or AI frame forwarding.

### UX Design Requirements

No separate UX design contract was supplied. The milestone UI is limited to the developer diagnostics screen and live preview; their actionable fields, states, truthful placeholders, error behavior, and performance constraints are captured in FR10-FR15 and the Additional Requirements.

### FR Coverage Map

FR1: Epic 1 - Create the personal `maria` repository from VisionClaw with upstream traceability.
FR2: Epic 1 - Build and document the unchanged Android baseline.
FR3: Epic 2 - Migrate deliberately to the pinned DAT 0.9.x release and document the changelog decisions.
FR4: Epic 2 - Discover and select Adventurer explicitly as `META_GLASSES`.
FR5: Epic 2 - Expose selected-device identity, capability, compatibility, link, battery, and thermal data.
FR6: Epic 2 - Run Meta AI registration and gate camera startup on `REGISTERED`.
FR7: Epic 2 - Operate the supported DAT device-session and camera lifecycle.
FR8: Epic 2 - Represent and observe the canonical hardware state graph.
FR9: Epic 2 - Establish vision readiness only after a usable image frame.
FR10: Epic 2 - Display real Adventurer frames in an on-device preview.
FR11: Epic 3 - Measure session latencies, FPS, and frame counters.
FR12: Epic 3 - Expose the complete developer diagnostics surface.
FR13: Epic 3 - Render truthful placeholders for future integrations.
FR14: Epic 3 - Produce structured session evidence.
FR15: Epic 3 - Classify and expose typed layer-specific failures.
FR16: Epic 2 - Support explicit start and stop intent without unwanted resurrection.
FR17: Epic 2 - Preserve intent across Android lifecycle changes and reset it after process death.
FR18: Epic 3 - Recover across the required device, app, phone, network, Meta AI, and repeated-use scenarios.
FR19: Epic 3 - Restart recovery at the correct failed lifecycle layer.
FR20: Epic 4 - Record the fixed physical environment and per-case evidence.
FR21: Epic 4 - Enforce every physical camera acceptance gate.
FR22: Epic 2 - Keep AI frame forwarding disabled and its counter at zero.
FR23: Epic 4 - Distinguish baseline, compiled, and physically verified delivery states and gate the working tag.
FR24: Epic 2 - Preserve compatible VisionClaw behavior during the migration.

## Epic List

### Epic 1: Known-Good maria Android Baseline
A developer has a personal, upstream-traceable `maria` repository with the unchanged VisionClaw Android application compiling and its baseline fully documented before migration work begins.
**FRs covered:** FR1, FR2

### Epic 2: First Trustworthy Adventurer Camera Feed
A developer can register and connect Meta Adventurer, operate the supported DAT 0.9 lifecycle, observe truthful state, and see real camera frames on the phone without false readiness or regressions to compatible VisionClaw behavior.
**FRs covered:** FR3, FR4, FR5, FR6, FR7, FR8, FR9, FR10, FR16, FR17, FR22, FR24

### Epic 3: Diagnosable and Recoverable Wearable Operation
A developer can measure, diagnose, and recover camera operation through canonical telemetry, typed failures, structured evidence, and controlled lifecycle recovery.
**FRs covered:** FR11, FR12, FR13, FR14, FR15, FR18, FR19

### Epic 4: Physical Adventurer Acceptance and Release Evidence
A physical-device tester can execute the fixed Adventurer acceptance matrix, capture trustworthy evidence, and distinguish compiled readiness from verified hardware support.
**FRs covered:** FR20, FR21, FR23

## Epic 1: Known-Good maria Android Baseline

A developer has a personal, upstream-traceable `maria` repository with the unchanged VisionClaw Android application compiling and its baseline fully documented before migration work begins.

### Story 1.1: Create the Upstream-Traceable maria Repository

As a developer,
I want a personal `maria` repository derived from VisionClaw with explicit upstream traceability,
So that milestone work starts from a controlled and reproducible source.

**Acceptance Criteria:**

**Given** the user's authenticated personal GitHub account and the available `Intent-Lab/VisionClaw` repository
**When** the `maria` repository is created
**Then** it is owned by the authenticated personal account and named exactly `maria`
**And** its default branch contains the selected VisionClaw source without milestone implementation changes.

**Given** the imported repository
**When** its Git remotes and source layout are inspected
**Then** upstream traceability points to `https://github.com/Intent-Lab/VisionClaw`
**And** `samples/CameraAccessAndroid` exists and is selected as the Android application base.

**Given** architecture seed `fbc72a2`
**When** the imported VisionClaw commit is recorded
**Then** the exact source SHA is documented
**And** if it differs materially from `fbc72a2`, implementation readiness stops until the architecture seed is ratified against that commit.

**Given** repository configuration and dependency access
**When** local or CI credentials are configured
**Then** GitHub Packages tokens and DAT credentials exist only in environment variables or ignored local files
**And** no secret is committed, logged, or placed in a test fixture.

### Story 1.2: Establish and Document the Known-Good Android Baseline

As a developer,
I want the unchanged VisionClaw Android application compiled and its environment documented,
So that DAT migration failures can be distinguished from pre-existing baseline problems.

**Acceptance Criteria:**

**Given** Story 1.1's imported VisionClaw source and selected source SHA
**When** the Android project configuration is inspected before editing
**Then** the DAT, AGP, Gradle, Kotlin, Compose, `minSdk`, `targetSdk`, and `compileSdk` versions are recorded
**And** no DAT or unrelated dependency upgrade is introduced.

**Given** this 8 GiB no-swap host
**When** the unchanged Android baseline is built
**Then** the memory-intensive Gradle invocation runs through `/home/hermes/.local/bin/codex-memory-run` with at most one worker
**And** `hindsight.service` remains running with no unscoped heavy-command fallback.

**Given** the baseline build result
**When** `docs/baseline.md` is produced
**Then** it records the VisionClaw commit, dependency and SDK versions, build result, warnings, known problems, and relevant Android files
**And** it identifies the existing camera lifecycle, phone-camera fallback, frame throttling, audio, Gemini Live, OpenClaw, reconnect, WebRTC, settings, preview, and session-state integration surfaces.

**Given** an unchanged build failure
**When** a correction is required
**Then** only a build-blocking baseline fix is permitted
**And** the exact failure, focused fix, and resulting difference from upstream are documented before rebuilding.

**Given** a successful known-good baseline
**When** the baseline is finalized
**Then** the commit uses the intent `chore: establish working Android baseline`
**And** the `baseline` branch and `baseline-visionclaw` tag are created only after the documented build succeeds.

**Given** a failure that would require broad AGP, Kotlin, Compose, SDK, or dependency modernization
**When** the baseline cannot be established through a focused blocker fix
**Then** work stops with the incompatibility documented
**And** DAT migration does not begin until a separate scope decision is made.

## Epic 2: First Trustworthy Adventurer Camera Feed

A developer can register and connect Meta Adventurer, operate the supported DAT 0.9 lifecycle, observe truthful state, and see real camera frames on the phone without false readiness or regressions to compatible VisionClaw behavior.

### Story 2.1: Migrate and Pin the DAT 0.9 Baseline

As a developer,
I want the known-good Android baseline migrated deliberately to DAT 0.9.0,
So that Adventurer work uses a verified SDK/toolchain combination with an auditable migration record.

**Acceptance Criteria:**

**Given** the successful Story 1.2 baseline and its recorded DAT 0.4.0 environment
**When** migration work begins
**Then** branch `feat/dat-0.9` is created from the known-good baseline
**And** no work is performed directly on the protected baseline branch.

**Given** the baseline and target DAT versions
**When** the official changelog from 0.4.0 through 0.9.0 is reviewed
**Then** `docs/dat-migration.md` lists every breaking or behaviorally relevant change
**And** each applicable change has an explicit migration decision, including the DAT 0.8 introduction of `DeviceType.META_GLASSES`.

**Given** the unchanged baseline build toolchain
**When** DAT dependencies are updated
**Then** `mwdat-core`, `mwdat-camera`, and test-only `mwdat-mockdevice` are pinned to 0.9.0
**And** AGP, Gradle, Kotlin, Compose, and Android SDK levels remain unchanged unless a compatibility failure requires a separately documented decision.

**Given** DAT 0.9.0's consolidated lifecycle
**When** the existing DAT 0.4 call sites are adapted sufficiently for compatibility compilation
**Then** the code uses the supported equivalents of `Wearables.createSession`, session start, `DeviceSession.addCamera`, and `Camera.stream.start`
**And** the migration record identifies the verified stop, camera-detach, and session-stop APIs without retaining the obsolete `startStreamSession` model.

**Given** DAT build and runtime configuration
**When** manifest metadata and package access are configured
**Then** GitHub Packages and DAT credentials remain in environment variables or ignored local properties
**And** analytics and crash reporting are opted out by default without exposing credentials in source or logs.

**Given** the migrated dependency and compatibility code
**When** the minimal `assembleDebug` compatibility gate runs
**Then** it executes through `/home/hermes/.local/bin/codex-memory-run` with one worker
**And** success pins the combined DAT 0.9.0/baseline-toolchain stack as ready for Adventurer implementation.

**Given** a compatibility failure requiring broad toolchain modernization
**When** a focused migration cannot compile
**Then** the exact incompatibility is recorded and the story stops
**And** no unrelated dependency upgrade is silently introduced.

### Story 2.2: Register and Select Meta Adventurer

As a developer,
I want `maria` to register through Meta AI and select Meta Adventurer explicitly,
So that camera work targets the intended device without Ray-Ban-specific assumptions.

**Acceptance Criteria:**

**Given** DAT 0.9.0 is initialized and the linked Adventurer has Developer Mode enabled
**When** the user starts registration from `maria`
**Then** the application invokes the supported Meta AI registration flow
**And** exposes provider-neutral registration states derived directly from `Wearables.registrationState`.

**Given** registration is in progress or fails
**When** DAT emits a registration state or `registrationErrorStream` event
**Then** the current state and typed failure are observable by the application
**And** the failure is classified as `AUTH`, `DAT`, or `DEVICE` according to its actual layer.

**Given** registration has not reached `REGISTERED`
**When** the user requests camera streaming
**Then** session creation is rejected with a truthful registration precondition failure
**And** no DAT device session or camera handle is created.

**Given** registration reaches `REGISTERED` and DAT publishes linked devices
**When** `maria` evaluates eligible devices
**Then** `DeviceType.META_GLASSES` is accepted explicitly
**And** selection never requires or assumes `RAYBAN_META`.

**Given** one connected, compatible, camera-capable Meta Adventurer
**When** device selection completes
**Then** its identifier is selected for the upcoming session
**And** its name, type, capabilities, compatibility, link state, battery, and thermal state are exposed when DAT provides them.

**Given** multiple or ineligible devices
**When** automatic selection runs for this single-device milestone
**Then** exactly one eligible device is selected deterministically and its identifier remains fixed for the session lifetime
**And** the absence of an eligible device produces a typed `DEVICE` failure rather than selecting an incompatible device.

**Given** the provider boundary
**When** registration and device information cross into application code
**Then** only the Meta adapter imports `com.meta.wearable.dat.*`
**And** all outward registration, device, and error models contain no DAT types.

### Story 2.3: Operate the Canonical Wearable Camera Lifecycle

As a developer,
I want one provider-neutral runtime to control Adventurer camera start, stop, and observable state,
So that hardware ownership and readiness transitions remain deterministic across Android lifecycle events.

**Acceptance Criteria:**

**Given** the application process starts
**When** the wearable subsystem is created
**Then** a small application container supplies one process-scoped `WearableRuntime`
**And** Activities, ViewModels, and `StreamingService` observe or command that runtime without owning DAT handles.

**Given** the Meta adapter is active
**When** DAT creates device, session, camera, stream, or collector objects
**Then** `MetaDatWearableAdapter` owns those objects exclusively
**And** the provider-neutral runtime and UI contain no DAT types.

**Given** a registered, eligible selected Adventurer in `DEVICE_CONNECTED`
**When** the user requests streaming
**Then** `desiredStreaming` becomes true and the runtime follows `SESSION_STARTING` → `SESSION_READY` → `CAMERA_STARTING` → `STREAM_WAITING`
**And** the Meta adapter follows create/start session → add camera → start stream without skipping a lifecycle gate.

**Given** commands and DAT callbacks for an active lifecycle
**When** they enter the runtime
**Then** one serialized reducer is the sole writer of immutable `WearableSnapshot` state
**And** every command, callback, transition, and later frame carries the active monotonic generation ID.

**Given** an event from a retired lifecycle generation
**When** the reducer receives it
**Then** the event is discarded without changing current state
**And** the active session cannot be corrupted by a late callback.

**Given** any supported active or failed state
**When** the user explicitly stops streaming
**Then** `desiredStreaming` becomes false and the canonical user-stop transition returns to `DEVICE_CONNECTED`
**And** teardown terminates stream/camera, detaches the camera, cancels collectors once, stops the session, and discards it in the documented order.

**Given** teardown is requested more than once or after partial startup
**When** cleanup executes
**Then** every cleanup operation is idempotent
**And** no terminal session is reused.

**Given** the app backgrounds, foregrounds, or the phone locks and unlocks while streaming is desired
**When** Activities are recreated
**Then** `desiredStreaming` remains true, foreground liveness is maintained by `StreamingService`, and the recreated UI reattaches to runtime flows
**And** no competing session or reducer is created.

**Given** the Android process dies
**When** the application starts again
**Then** desired streaming intent resets to false
**And** the camera does not restart automatically without a new user command.

**Given** canonical reducer tests
**When** all startup, user-stop, pause/resume, failure, retry-target, disconnect, and cleanup edges are exercised
**Then** only transitions declared in the canonical state graph are accepted
**And** the latest 256 accepted transitions are retained without unbounded growth.

### Story 2.4: Display the First Usable Adventurer Frame

As a physical-device tester,
I want real Adventurer camera frames displayed in `maria`,
So that I can distinguish a genuinely usable vision pipeline from an SDK stream-start signal.

**Acceptance Criteria:**

**Given** a session reaches `STREAM_WAITING`
**When** DAT reports that streaming has started but no usable image callback has arrived
**Then** the runtime remains not ready
**And** neither the preview nor any other consumer treats the stream-start state as vision readiness.

**Given** camera streaming is configured
**When** the Meta adapter starts the DAT camera
**Then** it requests `VideoQuality.MEDIUM` at 24 FPS with compression disabled
**And** the configuration preserves the VisionClaw raw-preview seam.

**Given** a DAT callback carrying usable image data
**When** the adapter handles the callback
**Then** it copies the contiguous I420 bytes into app-owned memory before returning
**And** the resulting provider-neutral envelope contains session ID, generation ID, sequence, monotonic timestamp, width, height, rotation, `I420` format, and bytes ordered Y then U then V.

**Given** a codec-configuration-only packet or invalid image payload
**When** it reaches the adapter
**Then** it does not qualify as a usable frame
**And** it cannot trigger `FIRST_FRAME_RECEIVED`, readiness, preview output, or received-frame counting.

**Given** the first usable app-owned frame
**When** the reducer accepts it for the active generation
**Then** it emits observable `FIRST_FRAME_RECEIVED` followed by `STREAM_ACTIVE`
**And** sticky first-frame state remains available even if the current state advances immediately to `STREAM_ACTIVE`.

**Given** usable frames arrive faster than a consumer can render them
**When** frames cross the provider boundary
**Then** they use a latest-only bounded channel and every usable received frame is counted before conflation
**And** neither memory use nor SDK collection becomes unbounded or blocked.

**Given** an app-owned I420 frame
**When** the preview displays it
**Then** I420-to-display conversion happens outside the Meta adapter and off the main thread
**And** Compose is not recomposed independently for every incoming frame.

**Given** no AI frame consumer exists in this milestone
**When** frames are received and displayed
**Then** no frame is forwarded to an AI layer
**And** the forwarded-frame count remains exactly zero.

**Given** automated frame and reducer tests
**When** SDK-started, codec-only, first-usable-frame, later-frame, stale-generation, and slow-consumer cases run
**Then** readiness and preview occur only for accepted usable frames
**And** stale or invalid frames cannot alter active state.

### Story 2.5: Preserve Compatible VisionClaw Behavior

As a developer,
I want the Adventurer migration to preserve compatible VisionClaw behavior,
So that the camera milestone adds the new hardware path without becoming an unrelated rewrite.

**Acceptance Criteria:**

**Given** the integration-surface inventory from `docs/baseline.md`
**When** the DAT migration and wearable runtime changes are reviewed
**Then** every listed phone-camera fallback, frame-throttling, audio, Gemini Live, OpenClaw, reconnect, WebRTC, settings, preview, and session-state surface is classified as preserved, deliberately adapted, or blocked by a documented DAT incompatibility
**And** no surface disappears silently.

**Given** the provider-neutral runtime and Meta adapter
**When** DAT lifecycle ownership is extracted from the legacy ViewModel
**Then** only DAT-bound coordination moves into the new wearable subsystem
**And** unrelated preview, settings, audio, provider, routing, or service behavior is not moved into the Meta adapter.

**Given** the existing phone-camera fallback
**When** no eligible wearable is selected or the user chooses the phone path
**Then** the compatible fallback remains usable with its baseline behavior
**And** its readiness is not falsely attributed to Meta Adventurer acceptance.

**Given** existing Gemini, OpenClaw, WebRTC, audio, and routing paths
**When** the Adventurer camera path runs
**Then** those existing paths remain compilable and retain their compatible baseline behavior
**And** the new wearable frame channel is not connected to an AI, realtime, operator, gateway, or tool consumer.

**Given** a DAT incompatibility forces a focused adaptation
**When** compatible baseline behavior cannot be retained exactly
**Then** the affected behavior, reason, smallest viable adaptation, and verification result are recorded in `docs/dat-migration.md`
**And** the change does not authorize broad refactoring or deferred integrations.

**Given** the completed Epic 2 implementation
**When** directed unit, reducer, frame, and compatibility checks run
**Then** all checks pass and `assembleDebug` succeeds through the memory-scoped runner
**And** the result is described only as compiled and ready for physical validation.

**Given** Epic 2 completion
**When** its changes are reviewed
**Then** FR3–FR10, FR16–FR17, FR22, and FR24 have direct code or test evidence
**And** no result creates the `adventurer-camera-working` tag.

## Epic 3: Diagnosable and Recoverable Wearable Operation

A developer can measure, diagnose, and recover camera operation through canonical telemetry, typed failures, structured evidence, and controlled lifecycle recovery.

### Story 3.1: Measure Canonical Wearable Session Telemetry

As a developer,
I want canonical per-session camera telemetry,
So that startup, readiness, throughput, and reconnect behavior can be measured consistently.

**Acceptance Criteria:**

**Given** a new streaming request
**When** the runtime creates a session attempt
**Then** it assigns one opaque session ID and initializes session-scoped telemetry
**And** every metric update is generation-fenced to the active lifecycle.

**Given** a stream-start request and the corresponding DAT streaming state
**When** startup latency is calculated
**Then** it measures request-to-SDK-streaming time using Android monotonic elapsed time
**And** wall-clock time is not used for ordering or latency.

**Given** a stream-start request and the first accepted usable image
**When** first-frame latency is calculated
**Then** it measures request-to-first-usable-frame time
**And** SDK-started or codec-only events cannot complete the measurement.

**Given** an unexpected disconnect followed by a recovered usable frame
**When** reconnect latency is calculated
**Then** it measures disconnect-to-first-usable-recovered-frame time
**And** it is associated with the recovery attempt and active session evidence.

**Given** usable frames arrive
**When** incoming FPS is calculated
**Then** it uses accepted usable frames over a rolling five-second window
**And** the value exposed to observers updates at most once per second.

**Given** usable frames are received and the latest-only channel conflates some frames
**When** counters update
**Then** the received-frame counter increments before conflation and remains monotonic
**And** the forwarded-frame counter remains exactly zero while no AI consumer exists.

**Given** a new session attempt begins
**When** telemetry is reset
**Then** session-scoped latencies, FPS windows, and counters cannot leak from the previous session
**And** historical transition evidence retains the correct original session ID.

**Given** stale-generation, out-of-order, or duplicate events
**When** telemetry receives them
**Then** active metrics are not corrupted or decremented
**And** invalid duration samples are rejected rather than displayed.

**Given** telemetry unit tests with a controllable monotonic clock
**When** startup, first-frame, reconnect, rolling-window, conflation, reset, and stale-event scenarios run
**Then** all metric definitions produce deterministic expected values
**And** no test depends on wall-clock timing.

### Story 3.2: Classify Failures and Record Structured Session Evidence

As a developer,
I want wearable failures classified by their actual layer and recorded safely,
So that hardware, DAT, camera, network, and authentication problems can be diagnosed without leaking private data.

**Acceptance Criteria:**

**Given** a provider or runtime failure
**When** it crosses the wearable boundary
**Then** it is represented as `WearableFailure(layer, code, recoverability, redactedMessage, causeType)`
**And** its layer is exactly one of `DEVICE`, `DAT`, `CAMERA`, `NETWORK`, or `AUTH`.

**Given** a typed DAT failure
**When** the Meta adapter maps it into the domain
**Then** the typed cause and stable code remain available for diagnosis
**And** production behavior never branches on a localized or human-readable DAT message.

**Given** a registration, device-selection, session-start, camera-start, stream, or disconnect failure
**When** the failure is surfaced
**Then** it is assigned to the narrowest responsible layer
**And** a hardware or camera problem is never presented as a generic AI failure.

**Given** a current session and its accepted lifecycle events
**When** structured evidence is emitted
**Then** the record contains session ID, generation ID, device type, DAT state, camera state, first-frame latency, received and forwarded frame counts, disconnect reason, and errors by layer
**And** transition entries contain the transition, monotonic timestamp, typed error code, and redacted message.

**Given** multiple failures or transitions occur
**When** diagnostic history is retained
**Then** one current failure is exposed with the latest 256 accepted transitions
**And** the bounded history cannot grow without limit.

**Given** logs, diagnostics, or test fixtures are produced
**When** their contents are inspected
**Then** API/OAuth secrets, access tokens, GitHub Packages credentials, DAT credentials, and raw private audio are absent
**And** any safe message shown to a developer is redacted before leaving its owning layer.

**Given** DAT analytics and crash reporting configuration
**When** the application is built
**Then** both remain opted out by default
**And** enabling either requires a later explicit privacy decision outside this milestone.

**Given** failure-mapping and evidence tests
**When** representative errors from every layer, unknown typed causes, secret-bearing messages, repeated failures, and history overflow are exercised
**Then** classification, redaction, current-error selection, and history bounds are deterministic
**And** no sensitive literal appears in test output.

### Story 3.3: Show the Canonical Developer Diagnostics Screen

As a developer,
I want one in-app diagnostics screen backed by canonical runtime state,
So that I can understand the complete wearable pipeline without relying solely on Logcat.

**Acceptance Criteria:**

**Given** the diagnostics screen is opened
**When** no eligible device or integration is configured
**Then** every required field remains visible with a truthful state such as `NO_DEVICE`, `NOT_CONFIGURED`, or `NOT_AVAILABLE`
**And** no placeholder implies that an out-of-scope integration is implemented.

**Given** runtime state and device metadata are available
**When** diagnostics renders
**Then** it shows active device, device type, device ID, capabilities, DAT registration, device-session, camera, and stream states
**And** battery, thermal, Bluetooth input route, and Bluetooth output route are shown when available.

**Given** frame and telemetry state are available
**When** diagnostics updates
**Then** it shows first-frame received, last-frame timestamp, incoming FPS, total received frames, frames sent to AI, stream-startup latency, first-frame latency, and reconnect latency
**And** frames sent to AI remains zero during this milestone.

**Given** future integrations remain outside scope
**When** diagnostics renders their fields
**Then** realtime provider, realtime connection, Hermes gateway, and last tool call show `NOT_CONFIGURED` or `NOT_AVAILABLE`
**And** the screen does not initialize or depend on those layers.

**Given** registration state changes
**When** `Wearables.registrationState` or `registrationErrorStream` is mapped by the Meta adapter
**Then** the diagnostics registration field updates from the provider-neutral runtime projection
**And** the camera-start action remains unavailable until the state reaches `REGISTERED`.

**Given** lifecycle transitions and failures occur
**When** diagnostics renders them
**Then** it shows the current typed and redacted error plus access to the latest 256 accepted transitions
**And** each transition can be correlated by session ID, generation ID, monotonic time, disconnect reason, layer, and code.

**Given** the diagnostics implementation
**When** its state ownership is reviewed
**Then** `DiagnosticsViewModel` is a read-only projection of `WearableRuntime`, `WearableSnapshot`, transitions, and telemetry
**And** neither the ViewModel nor Compose UI maintains competing hardware, readiness, counter, or error state.

**Given** frames arrive at camera rate
**When** the diagnostics screen is visible
**Then** scalar telemetry presentation updates at its bounded cadence rather than once per frame
**And** UI rendering does not block frame collection or I420 conversion.

**Given** Compose and ViewModel tests
**When** absent, registering, connected, waiting, first-frame, active, paused, failed, disconnected, and placeholder scenarios are rendered
**Then** every required field displays the canonical expected value
**And** sensitive values are never displayed.

### Story 3.4: Recover Wearable Operation from the Correct Lifecycle Layer

As a physical-device tester,
I want camera operation to recover predictably after common disruptions,
So that an interrupted Adventurer session can resume without duplicate resources or unwanted restarts.

**Acceptance Criteria:**

**Given** desired streaming is true and the active device disconnects unexpectedly
**When** the disconnect is observed
**Then** the runtime retires the active generation, emits `DISCONNECTED`, performs complete idempotent teardown, and converges to `NO_DEVICE`
**And** exactly one cancellable recovery controller waits for an eligible connected device.

**Given** an eligible Adventurer reconnects while desired streaming remains true
**When** recovery begins
**Then** a new generation and session attempt are created from `SESSION_STARTING`
**And** readiness remains false until a new usable frame produces `FIRST_FRAME_RECEIVED`.

**Given** session creation or session start fails
**When** the failure is recoverable and desired streaming remains true
**Then** recovery recreates the lifecycle from `SESSION_STARTING`
**And** no terminal session or retired collector is reused.

**Given** camera attachment or stream startup fails while the current session remains usable
**When** the failure is recoverable
**Then** recovery restarts from `CAMERA_STARTING`
**And** it does not recreate a usable session unnecessarily.

**Given** the current session becomes unusable during a camera or stream failure
**When** recovery classifies the failure
**Then** it escalates to complete session recreation
**And** the transition history records the failed layer and selected recovery target.

**Given** recovery attempts continue
**When** retry timing is applied
**Then** delays and limits are bounded and hardware-tunable
**And** there is no busy loop, competing controller, or duplicate DAT handle.

**Given** the user explicitly stops while recovery is pending
**When** the stop command is reduced
**Then** desired streaming becomes false, pending recovery is cancelled, and state returns to `DEVICE_CONNECTED` or `NO_DEVICE` according to device availability
**And** later device events cannot restart the camera automatically.

**Given** app background/foreground, phone lock/unlock, Wi-Fi/mobile-data changes, Meta AI open/closed states, glasses power cycling, or repeated camera start/stop
**When** each scenario is exercised
**Then** the runtime either resumes cleanly according to desired intent or exposes a typed layer-specific failure
**And** state, telemetry, preview, and structured evidence remain internally consistent.

**Given** process death occurs
**When** the application process is recreated
**Then** desired streaming is false and no recovery controller restarts the camera
**And** the user must issue a new start command.

**Given** pure reducer tests and DAT 0.9 MockDeviceKit instrumentation
**When** disconnect, reconnect, layer-targeted failure, cancellation, repeated cleanup, stale generation, and lifecycle scenarios run
**Then** only one active lifecycle and one recovery controller exist at any time
**And** all canonical recovery transitions and telemetry outcomes are verified.

## Epic 4: Physical Adventurer Acceptance and Release Evidence

A physical-device tester can execute the fixed Adventurer acceptance matrix, capture trustworthy evidence, and distinguish compiled readiness from verified hardware support.

### Story 4.1: Install maria and Prepare the Physical Test Environment

As a physical-device tester,
I want the compiled `maria` build installed on the designated phone with DAT registration ready,
So that the Adventurer acceptance matrix starts from a recorded and reproducible environment.

**Acceptance Criteria:**

**Given** Epics 1–3 compile successfully
**When** the physical-test APK is produced
**Then** `assembleDebug` runs through `/home/hermes/.local/bin/codex-memory-run` with one worker
**And** the APK is traceable to its exact app commit and debug build variant.

**Given** the Samsung Galaxy Z Fold7 running Android 16
**When** the phone is prepared for development
**Then** Developer Options and USB debugging are enabled, the host recognizes the authorized device, and the tester accepts the host authorization prompt
**And** no unrelated developer setting is required.

**Given** the authorized Z Fold7 is connected
**When** the debug APK is installed through Android Studio or ADB
**Then** `maria` launches successfully on the physical phone
**And** the installed package corresponds to the recorded commit and build variant.

**Given** Meta Adventurer Standard 49 mm firmware v127.1 and Meta AI 286.1.0.17.162
**When** the wearable environment is prepared
**Then** the glasses are paired, powered, connected, and visible to Meta AI
**And** Developer Mode is enabled for the linked Adventurer.

**Given** `maria` is installed and DAT is initialized
**When** the tester starts registration
**Then** the Meta AI registration flow completes and diagnostics reports `Wearables.registrationState` as `REGISTERED`
**And** registration errors remain visible and block camera-session testing if registration does not complete.

**Given** registration is complete
**When** camera permission is requested through the supported Meta AI flow
**Then** DAT reports camera permission as granted
**And** session testing does not begin while permission is denied or unavailable.

**Given** the environment is ready
**When** the acceptance record is initialized
**Then** it contains phone model, Android version, Meta AI version, glasses model and firmware, DAT version, registration state, Developer Mode state, camera-permission state, app commit, build variant, and network conditions
**And** any value differing from the planned environment is recorded explicitly.

**Given** installation, pairing, registration, or permission preparation fails
**When** the failure is recorded
**Then** it is classified at the narrowest responsible layer with its redacted evidence
**And** the physical camera matrix remains blocked without claiming Adventurer support.

### Story 4.2: Execute the Physical Adventurer Camera Acceptance Matrix

As a physical-device tester,
I want to execute every required hardware scenario with recorded evidence,
So that Adventurer camera behavior and recovery are proven on the designated physical setup.

**Acceptance Criteria:**

**Given** Story 4.1's recorded environment and `REGISTERED` state
**When** the tester connects the glasses in `maria`
**Then** diagnostics identifies them explicitly as `DeviceType.META_GLASSES`
**And** records the selected device identity, capabilities, health, registration, and session state.

**Given** the connected Adventurer
**When** the tester starts the camera
**Then** the lifecycle follows the canonical startup states and reaches `STREAM_WAITING` before readiness
**And** a real preview frame causes observable `FIRST_FRAME_RECEIVED` before `STREAM_ACTIVE`.

**Given** real frames are streaming
**When** the preview and diagnostics are observed
**Then** the preview updates from Adventurer frames and startup latency, first-frame latency, incoming FPS, received-frame count, and last-frame timestamp update consistently
**And** forwarded-frame count remains zero.

**Given** an active camera session
**When** the glasses disconnect and reconnect
**Then** the recorded state passes through `DISCONNECTED`, cleanup, and a new generation before returning to a first usable recovered frame
**And** reconnect latency and recovery evidence are captured.

**Given** an active or desired camera session
**When** the glasses are powered off and back on
**Then** the runtime cleans up and recovers according to desired intent without duplicate resources
**And** every transition, latency, result, and layer-specific failure is recorded.

**Given** an active stream
**When** the app backgrounds and foregrounds and the phone locks and unlocks
**Then** desired streaming intent is preserved and the UI reattaches to the process-scoped runtime
**And** no second session, adapter, reducer, or recovery controller is created.

**Given** an active or recoverable session
**When** network connectivity changes from Wi-Fi to mobile data
**Then** the camera pipeline remains consistent or reports the narrowest applicable typed failure
**And** no generic AI error or false readiness is shown.

**Given** the required Meta AI interaction scenarios
**When** camera testing is repeated with Meta AI open and closed
**Then** each result is recorded independently
**And** any interruption is classified and recovered without stale-generation events altering current state.

**Given** the registered device
**When** the tester starts and stops the camera repeatedly
**Then** each start creates at most one active generation and each stop performs complete idempotent teardown
**And** no stopped session, collector, camera, or pending retry survives into the next cycle.

**Given** every physical scenario
**When** its evidence is captured
**Then** the record contains state transitions, first-frame result, relevant latency, recovery result, and any layer-specific error
**And** diagnostics, preview, telemetry, transition history, and structured records agree.

**Given** mocks, emulator output, compilation, or phone-camera fallback
**When** acceptance evidence is reviewed
**Then** none can substitute for a required Meta Adventurer result
**And** a missing or failed hardware scenario remains explicitly unaccepted.

### Story 4.3: Gate the Hardware-Verified Release from Physical Evidence

As a developer and physical-device tester,
I want the milestone release state determined strictly from recorded hardware evidence,
So that `maria` never claims Adventurer support before the camera pipeline is genuinely verified.

**Acceptance Criteria:**

**Given** the completed physical acceptance record
**When** the release verdict is evaluated
**Then** every required scenario has an explicit pass or fail result with traceable environment and session evidence
**And** missing, ambiguous, mock-only, emulator-only, fallback, or compilation-only evidence is treated as a failure to satisfy the physical gate.

**Given** the acceptance gates
**When** results are checked
**Then** approval requires `META_GLASSES` detection, DAT 0.9 camera startup, real preview frames, `FIRST_FRAME_RECEIVED` before readiness, internally consistent diagnostics and telemetry, and clean disconnect/reconnect recovery
**And** no individual gate can be waived silently.

**Given** one or more physical gates fail
**When** the milestone status is reported
**Then** the report names the exact failed scenario, observed states, relevant latency, recovery result, and layer-specific error
**And** the status remains “physical validation failed” or “compiled and ready for physical validation,” as supported by the evidence.

**Given** missing hardware or an incomplete acceptance run
**When** delivery status is produced
**Then** it states only “compiled and ready for physical validation”
**And** it does not describe Adventurer camera support as working.

**Given** all physical gates pass on the recorded environment
**When** the implementation is finalized
**Then** the completed milestone commit uses the intent `feat: support Meta Adventurer with DAT 0.9`
**And** the tested app commit, DAT version, phone, Android, Meta AI, glasses firmware, and acceptance evidence remain mutually traceable.

**Given** all physical gates pass
**When** release tagging is authorized
**Then** `adventurer-camera-working` points to the exact tested implementation commit
**And** the tag is never created before the physical verdict passes.

**Given** repository history is reviewed
**When** the milestone is complete
**Then** it preserves the `main` → `baseline` → `feat/dat-0.9` → `feat/adventurer` progression and upstream traceability
**And** known-good branches and tags contain no undocumented experimental work.

**Given** the final release report
**When** it is shared with downstream work
**Then** it clearly distinguishes baseline-known-good, compiled-ready, physical-failed, and hardware-verified states
**And** future realtime, operator, tool, audio, or AI-forwarding work remains outside the verified claim.
