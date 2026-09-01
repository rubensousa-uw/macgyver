# Epic 2 Context: First Trustworthy Adventurer Camera Feed

<!-- Compiled from planning artifacts. Edit freely. Regenerate with compile-epic-context if planning docs change. -->

## Goal

Enable macgyver to register and select a Meta Adventurer, operate its DAT 0.9 camera lifecycle through one truthful provider-neutral runtime, and render real camera frames on the phone. This establishes a compilable, physically testable camera path while preserving compatible VisionClaw behavior; it must never represent SDK stream state, phone-camera behavior, or compilation as verified Adventurer support.

## Stories

- Story 2.1: Migrate and Pin the DAT 0.9 Baseline
- Story 2.2: Register and Select Meta Adventurer
- Story 2.3: Operate the Canonical Wearable Camera Lifecycle
- Story 2.4: Display the First Usable Adventurer Frame
- Story 2.5: Preserve Compatible VisionClaw Behavior

## Requirements & Constraints

- Pin `mwdat-core`, `mwdat-camera`, and test-only `mwdat-mockdevice` to DAT 0.9.0. Retain the baseline Android toolchain unless a focused compatibility failure requires a separately recorded change; record behaviorally relevant migration decisions and the verified DAT 0.9 lifecycle APIs in `docs/dat-migration.md`.
- Registration must use the supported Meta AI flow and project state and failures into provider-neutral models. Do not create a device session or camera handle until registration reaches `REGISTERED`.
- Explicitly accept `DeviceType.META_GLASSES`; do not require or assume `RAYBAN_META`. Select one connected, compatible, camera-capable device deterministically, fix its identifier for that session, and fail truthfully at the device layer when none is eligible.
- Camera readiness begins only when an accepted callback contains usable image data. DAT stream-start signals and codec-configuration-only or invalid packets must not trigger readiness, preview output, or received-frame counts. The first accepted frame must make both `FIRST_FRAME_RECEIVED` and `STREAM_ACTIVE` observable.
- Configure the wearable camera for medium quality, 24 FPS, and no compression. Copy usable contiguous I420 data into app-owned memory before returning from its callback; include session and generation IDs, sequence, monotonic timestamp, dimensions, rotation, I420 format, and Y/U/V-ordered bytes.
- Use bounded latest-only frame delivery; count every usable received frame before conflation. Convert I420 for preview outside the Meta adapter and off the main thread, without per-frame Compose recomposition. Do not forward frames to AI, realtime, operator, gateway, or tool consumers; forwarded-frame count remains zero.
- Preserve the compatible phone-camera fallback and existing preview, frame-throttling, audio, Gemini Live, OpenClaw, reconnect, WebRTC, settings, routing, and session-state behavior. Classify each relevant baseline surface as preserved, deliberately adapted, or blocked by a documented DAT incompatibility; avoid broad refactoring.
- DAT credentials and package tokens stay in environment variables or ignored local configuration. Analytics and crash reporting are opted out by default. Do not log or commit credentials, API/OAuth secrets, access tokens, or raw private audio.
- Directed migration, reducer, frame, and compatibility checks plus a memory-scoped one-worker `assembleDebug` are required for compilation readiness. The result is only “compiled and ready for physical validation”; do not create or claim an `adventurer-camera-working` result before physical acceptance.

## Technical Decisions

- Use Ports and Adapters with unidirectional state flow. `WearableAdapter` is provider-neutral; only `MetaDatWearableAdapter` may import `com.meta.wearable.dat.*` and own DAT device/session/camera/stream handles and SDK collectors. Keep `WearableAdapter`, `RealtimeProvider`, `Operator`, and `Tools` independent.
- Supply one process-scoped `WearableRuntime` from an application container. Activities, ViewModels, and `StreamingService` issue commands or observe flows only; the service supplies foreground liveness and owns no DAT handles. Extract only DAT-bound coordination from legacy lifecycle ownership.
- Route commands and adapter callbacks through one serialized reducer, the sole writer of immutable `WearableSnapshot`. Fence every command, callback, transition, and frame with a monotonic generation ID; discard events from retired generations. Retain only the latest 256 accepted transitions.
- Follow the DAT 0.9 lifecycle explicitly: create session, start device session, add camera, then start the camera stream. Map startup to `SESSION_STARTING`, `SESSION_READY`, `CAMERA_STARTING`, and `STREAM_WAITING`; do not retain the obsolete `startStreamSession` model.
- Maintain `desiredStreaming` separately from observed state. User stop clears it and returns safely to `DEVICE_CONNECTED`; teardown is serialized and idempotent: terminate stream/camera, detach the camera, cancel collectors once, then stop and discard the session. Never reuse a terminal session.
- Preserve desired intent through Activity recreation, background/foreground, and lock/unlock while foreground liveness is needed; reset intent to false after process death so the camera never restarts automatically.
- Represent failures as typed, redacted domain values classified at their narrowest responsible layer (`DEVICE`, `DAT`, `CAMERA`, `NETWORK`, or `AUTH`), never by parsing localized error text.

## Cross-Story Dependencies

- Story 2.1 establishes the pinned compatibility baseline needed by registration and lifecycle work. Story 2.2 supplies the registered, selected Adventurer precondition for Story 2.3. Story 2.3 supplies generation-fenced lifecycle state for Story 2.4. Story 2.5 constrains every Epic 2 change against the recorded VisionClaw baseline.
- Epic 2 depends on the known-good baseline from Epic 1. Its compilation evidence is an input to Epic 3 diagnostics/recovery and Epic 4 physical acceptance, but it is not physical acceptance itself.
