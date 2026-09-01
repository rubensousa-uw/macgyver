---
id: SPEC-meta-adventurer-camera-milestone
companions:
  - migration-and-delivery.md
  - wearable-state-and-diagnostics.md
  - acceptance.md
  - architecture-diagrams.md
sources: []
---

> **Canonical contract.** This SPEC and the files in `companions:` are the complete, preservation-validated contract for what to build, test, and validate. Source documents listed in frontmatter are for traceability — consult them only if you need narrative rationale or prose color this contract intentionally omits.

# maria — Meta Adventurer Camera Milestone

## Why

Establish the verified wearable hardware foundation for a personal Android AI assistant before introducing realtime models or operator integrations. The affected developer and physical-device tester need a reproducible VisionClaw baseline, a deliberate Meta DAT 0.9.x migration, and trustworthy proof that Meta Adventurer produces recoverable real camera frames; later layers must not be built on a false “stream started” signal.

## Capabilities

- **CAP-1**
  - **intent:** The developer can establish and document a reproducible, unmodified VisionClaw Android baseline.
  - **success:** `assembleDebug` completes and `docs/baseline.md` records the source commit, DAT, AGP, Kotlin, SDK levels, warnings, known problems, build result, and relevant Android files.
- **CAP-2**
  - **intent:** The Android application can use the latest stable Meta DAT release in the requested 0.9.x line through its supported camera lifecycle.
  - **success:** The application compiles with the pinned DAT version and `docs/dat-migration.md` maps every applicable changelog change to an explicit migration decision.
- **CAP-3**
  - **intent:** The application can accept and identify Meta Adventurer as `DeviceType.META_GLASSES` without Ray-Ban-specific assumptions.
  - **success:** A connected Adventurer is not rejected for differing from `RAYBAN_META`, and its identity, capabilities, health, registration, session, and camera lifecycle are visible in diagnostics.
- **CAP-4**
  - **intent:** The application can distinguish connection, session, stream startup, first-frame readiness, active streaming, pauses, failures, and disconnection.
  - **success:** The vision pipeline becomes ready only after a real frame arrives, and every required state and transition is observable.
- **CAP-5**
  - **intent:** A developer can diagnose the wearable pipeline in-app without relying solely on Logcat.
  - **success:** One diagnostics screen exposes all fields defined in `wearable-state-and-diagnostics.md`, with later integrations shown as explicit placeholders.
- **CAP-6**
  - **intent:** A tester can view the live wearable camera feed and quantify its operational behavior.
  - **success:** Live preview works and startup latency, first-frame latency, reconnect latency, effective FPS, received-frame count, and forwarded-frame count are measurable.
- **CAP-7**
  - **intent:** The hardware vision pipeline can recover from common device, application, and network lifecycle disruptions.
  - **success:** The physical test matrix in `acceptance.md` records clean recovery or a layer-specific failure for every scenario.
- **CAP-8**
  - **intent:** A physical-device tester can execute an exact Adventurer acceptance procedure and distinguish compiled support from hardware-verified support.
  - **success:** The instructions prove detection as `META_GLASSES`, camera startup, arrival of real frames, and reconnect recovery, and no hardware-working claim or tag is made before that evidence exists.

## Constraints

- The canonical project and repository name is lowercase `maria`.
- Create the VisionClaw fork as a personal GitHub repository named `maria`, owned by the user's authenticated personal account, while preserving upstream traceability.
- Start from `Intent-Lab/VisionClaw`, retaining `samples/CameraAccessAndroid` as the application base; preserve compatible working components rather than rewriting the project.
- Build and document repository HEAD before upgrading DAT; baseline fixes may address build blockers only.
- Read the complete official DAT changelog between the baseline and selected stable 0.9.x release before changing lifecycle code.
- Treat `FIRST_FRAME_RECEIVED`, not a started stream state, as the camera-readiness boundary.
- Keep the Meta DAT implementation behind the `WearableAdapter` boundary and independent of future realtime, operator, and tool layers.
- Use the delivery, observability, privacy, and host memory-safety rules in the companions.
- Do not describe or tag Adventurer camera support as working until it passes on physical Meta Adventurer Standard 49 mm hardware.

## Non-goals

- Adventurer microphone/speaker loopback or production Bluetooth audio routing.
- Realtime provider refactoring, Gemini regression work, OpenAI Realtime, gateway, Hermes, OAuth, Home Assistant, memory, coding delegation, or conversational modes.
- Battery benchmarking, continuous life recording, facial recognition, always-on cloud video, iOS, store distribution, custom Meta firmware, Meta AI replacement, or complex product UI.

## Success signal

On a real Meta Adventurer Standard 49 mm paired with a Samsung Galaxy Z Fold7 running Android 16 and Meta AI 286.1.0.17.162, the diagnostics screen identifies `META_GLASSES`, the preview displays real frames, readiness follows the first frame, measured telemetry updates, and the camera session recovers after disconnect/reconnect. Until that demonstration is recorded, success is limited to “compiled and ready for physical validation.”

## Assumptions

- The implementation will resolve and pin the latest stable DAT release within the requested 0.9.x line from official sources at execution time.
- The upstream repository still contains `samples/CameraAccessAndroid`; a material upstream restructure blocks implementation pending a scope decision.
- Diagnostics for future layers may display `NOT_CONFIGURED` without implementing those layers.

## Open Questions

- After `maria` starts registration through Meta AI, does `Wearables.registrationState` reach `REGISTERED`, and is Developer Mode enabled for the linked Adventurer?
