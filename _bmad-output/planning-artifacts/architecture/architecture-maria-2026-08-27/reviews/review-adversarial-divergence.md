# Adversarial Divergence Review — Architecture Spine

- **Target:** `ARCHITECTURE-SPINE.md`
- **Lens:** Independent units one level down, each obeying every stated AD
- **Date:** 2026-08-27
- **Verdict:** **REVISION REQUIRED** — the spine establishes a strong direction, but six seams still permit independently compliant units to build incompatible ownership, lifecycle, frame, event-history, recovery, or background-execution behavior.

## Method

For each seam, this review constructs two plausible implementation units. Both are required to obey the current AD text. A finding exists only when both implementations can honestly claim compliance yet cannot integrate without one being rewritten or the spine being reinterpreted.

## Finding Summary

| ID | Severity | Independent units | Divergence |
| --- | --- | --- | --- |
| DV-1 | High | Runtime vs Meta adapter | Both can claim ownership of DAT handles |
| DV-2 | High | Meta frame producer vs preview | Byte envelope and readiness semantics are incompatible |
| DV-3 | High | Command/reducer vs reducer tests | Required stop/startup-failure behavior has no legal graph edge |
| DV-4 | High | Recovery controller vs adapter collectors/telemetry | Retired-session events can corrupt a replacement session |
| DV-5 | High | Transition publisher vs diagnostics/acceptance | “Non-conflated” does not guarantee history or late-subscriber delivery |
| DV-6 | High | Streaming service vs runtime | Continue-in-background and pause/recreate are both compliant |
| DV-7 | Medium | Adapter/frame collector vs telemetry reducer | Frame counters and state can update in incompatible orders |
| DV-8 | Medium | DAT error mapper vs provider-neutral consumers | Typed SDK causes can leak or be discarded |
| DV-9 | Medium | Domain model vs diagnostics UI | Optional/unavailable field semantics remain incompatible |
| DV-10 | Medium | Runtime implementation vs test harness | Retry/time behavior has no deterministic policy contract |

## High Findings

### DV-1 — Runtime and adapter can both obey the spine while owning the same resources

**Unit A — `WearableRuntime`.** Implements AD-2 literally: stores `DeviceSession`, `Camera`, `Stream`, and collector jobs because it is the “sole owner.” It calls DAT lifecycle methods through thin adapter helpers.

**Unit B — `MetaDatWearableAdapter`.** Implements AD-1 literally: stores those DAT objects because only its package may import `com.meta.wearable.dat.*`; exposes only provider-neutral commands/events.

**Incompatibility.** Either the runtime imports/retains DAT types and violates the port boundary, or the adapter owns the handles and AD-2's ownership claim is false. A compromise where both retain references creates exactly the competing teardown authority AD-2 is meant to prevent.

**Concrete fix.** Split authority explicitly: `WearableRuntime` is the sole provider-neutral lifecycle **coordinator** and command/recovery owner; `MetaDatWearableAdapter` is the sole owner of DAT handles and SDK collectors. Define the port commands and adapter event outputs as the only interaction between them.

### DV-2 — A compliant frame producer and preview consumer need not understand each other

**Unit A — Meta frame producer.** Copies each non-config DAT callback into a frame envelope with `format = HEVC`, encoded bytes, encoded dimensions, and DAT presentation timestamp. It declares the first non-config packet usable and emits readiness.

**Unit B — Compose preview.** Implements off-main preview transformation but expects `format = I420` or NV21-compatible raw bytes, display rotation, plane/stride information, and an arrival timestamp. It declares readiness only after a bitmap/surface frame can render.

**Incompatibility.** Both satisfy AD-4/AD-5's “usable image bytes,” dimensions, format, timestamp, copying, and off-main transformation language. They disagree on the closed format set, timestamp clock, row/plane layout, rotation, codec-configuration/keyframe requirements, decoding owner, and the exact event that proves a frame is usable. The app can report `FIRST_FRAME_RECEIVED` while the preview cannot display anything.

**Concrete fix.** Choose one milestone contract: either normalize at the adapter to one app-owned raw format, or define a closed frame union with complete raw-plane/stride/rotation and encoded-codec/keyframe/timestamp metadata. Assign decode/render ownership and define first-frame readiness at one precise boundary that the preview and reducer can both observe.

### DV-3 — Required user-stop and startup-failure implementations cannot obey the binding graph

**Unit A — Command handler.** On explicit stop while glasses remain connected, clears `desiredStreaming`, stops/removes the camera, and wants to return to `SESSION_READY` or `DEVICE_CONNECTED` so the user can start again.

**Unit B — Reducer tests.** Enforces AD-3 literally and rejects every edge not in the canonical graph. The graph has no transition from `STREAM_ACTIVE`, `STREAM_WAITING`, or `STREAM_PAUSED` for explicit stop while still connected. It also has no `SESSION_STARTING -> STREAM_FAILED` or `CAMERA_STARTING -> STREAM_FAILED` edge for startup failure.

**Incompatibility.** The command handler must invent a forbidden edge, misuse `DISCONNECTED` even though the device is connected, or omit the required repeated start/stop behavior. The tests must either violate AD-3 or reject correct lifecycle behavior.

**Concrete fix.** Amend the canonical SPEC graph and spine together. Add explicit user-stop/teardown edges and destinations, plus failure edges from every asynchronous start phase. State whether stopping only the camera preserves a ready session or tears down to a connected-device state.

### DV-4 — Recovery has no generation fence between old and replacement sessions

**Unit A — Meta adapter collector.** On disconnect, emits `DISCONNECTED`; asynchronous DAT collectors then finish and emit terminal camera/stream/session states or errors while teardown completes.

**Unit B — Recovery controller.** After the device becomes eligible, creates a new session with a new `session_id` and begins reducing its startup events immediately. It accepts any adapter event that reaches the single reducer.

**Incompatibility.** Both comply with serialized teardown, a single recovery controller, one reducer, and per-session IDs. The spine does not say that events from a retired session/generation must be rejected. A late `STOPPED`, error, or `NO_DEVICE` event from the old collector can move the new session backward or reset its telemetry. Reconnect latency also spans two session IDs without a defined correlation owner.

**Concrete fix.** Introduce an opaque runtime generation/recovery ID. Every command, adapter event, frame, transition, and collector carries the generation that created it; the reducer discards stale generations after teardown commits. Define reconnect latency as a recovery-operation metric correlated from the old disconnect to the new generation's first usable frame.

### DV-5 — A non-conflated transition flow is not necessarily an observable history

**Unit A — Transition publisher.** Uses `MutableSharedFlow(replay = 0, extraBufferCapacity = 0)` and suspending `emit`. It never conflates transitions and therefore satisfies AD-3.

**Unit B — Diagnostics/acceptance.** Subscribes when its screen opens or when an acceptance export begins. It expects the “transition history” required by AD-9 and the evidence table, including the earlier `FIRST_FRAME_RECEIVED` event.

**Incompatibility.** The publisher is non-conflated but provides no replay or retention; all transitions emitted before subscription are absent. A different compliant publisher may keep an unbounded list, creating a memory/privacy problem. Neither diagnostics nor evidence has a stable contract.

**Concrete fix.** Separate live events from history. Keep a bounded, reducer-owned per-session ring buffer with a fixed capacity or retention rule; publish live transitions separately; make diagnostics and acceptance export read a canonical immutable history snapshot. Define overflow behavior and guarantee that terminal/first-frame transitions are retained.

### DV-6 — Service and runtime can choose incompatible background behavior

**Unit A — `StreamingService`.** Interprets “foreground liveness” as keeping the DAT stream and runtime alive through background and screen lock. It holds the wake-lock/notification until explicit user stop.

**Unit B — Runtime/lifecycle observer.** Interprets Android lifecycle as a recoverable pause: on background or lock it stops camera/session resources, emits `STREAM_PAUSED`, and recreates them when the app resumes.

**Incompatibility.** Both satisfy AD-2, the `STREAM_PAUSED` mapping, AD-7 recovery, and the physical recovery requirement. They disagree about whether frames continue, whether a new session ID is created, when reconnect/startup metrics restart, and who triggers service stop. Acceptance results and resource ownership will differ.

**Concrete fix.** Choose one milestone policy for background and lock. Bind the exact runtime commands, state transitions, session-ID behavior, foreground-service start/stop authority, wake-lock/notification lifetime, and expected recovery metrics for each lifecycle event.

## Medium Findings

### DV-7 — Frame counting and reducer state do not share an atomic event

**Unit A — Adapter/frame collector.** Increments received count locally before sending bytes to the latest-only frame channel, as AD-5 says.

**Unit B — Reducer/telemetry.** Owns canonical snapshot mutation and increments telemetry only when it reduces a `FrameReceived` event, as AD-3/AD-8 imply.

**Incompatibility.** Counts can double, disagree, or be observed in the wrong order relative to `FIRST_FRAME_RECEIVED`; one unit may drop an event while the other still counted its frame. `lastFrameTimestamp`, FPS window, and received total can therefore disagree across preview, diagnostics, and structured records.

**Concrete fix.** Define one immutable `UsableFrameReceived` event containing frame ID, generation/session ID, monotonic arrival timestamp, and frame metadata. The reducer atomically updates readiness, counters, last-frame time, and FPS input exactly once; frame bytes travel on the bounded frame channel keyed by the same frame ID.

### DV-8 — Error mapping permits both SDK leakage and diagnostic information loss

**Unit A — Meta mapper.** Retains the actual DAT error enum/object in `WearableFailure.cause` to obey AD-9's “retain the typed DAT cause.”

**Unit B — Runtime/diagnostics.** Rejects that object because AD-1 forbids DAT types outside the Meta package and expects only `WearableFailure(layer, code, recoverability, redactedMessage, causeType)`.

**Incompatibility.** One side leaks DAT through the port; the other necessarily discards the exact typed object. Independent teams can also map the same registration/permission/session error to different `AUTH`, `DAT`, or `DEVICE` layers.

**Concrete fix.** Keep the SDK object adapter-private. Define a closed provider-neutral error-code mapping table, including layer and recoverability for every DAT error family used by this milestone. Export stable SDK type/code strings only, never the SDK object.

### DV-9 — Required diagnostic fields have names but not common absence semantics

**Unit A — Domain model.** Represents battery, thermal, Bluetooth routes, capabilities, and future integration states as nullable primitives/enums.

**Unit B — Diagnostics UI.** Models every field as a non-null display value and distinguishes `NOT_CONFIGURED`, `NOT_AVAILABLE`, temporarily unknown/loading, denied permission, and stale/disconnected data.

**Incompatibility.** A null from Unit A has multiple meanings, so Unit B can display false values or retain stale device health across session replacement. The same problem applies to “one current error” after recovery.

**Concrete fix.** Define a provider-neutral availability wrapper or closed status enum (`Known(value)`, `Loading`, `NotConfigured`, `NotAvailable`, `PermissionDenied`, `Stale`) and reset rules per session/generation. Use it consistently in `WearableSnapshot` and diagnostics.

### DV-10 — Runtime and test harness can choose different retry clocks and outcomes

**Unit A — Recovery implementation.** Picks three attempts with exponential backoff based on monotonic real time.

**Unit B — Pure reducer tests.** Assumes indefinite eligibility waiting or immediate deterministic retries because AD-7 only says “bounded and hardware-tuned.”

**Incompatibility.** Both satisfy one controller and no busy loop, but tests cannot deterministically prove exhaustion, cancellation, or retry scheduling. Physical tuning can silently change reducer behavior and acceptance expectations.

**Concrete fix.** Inject a clock and retry policy through provider-neutral runtime configuration. Seed explicit provisional bounds for tests, define terminal/exhausted behavior, and permit physical tuning of values without changing the state-machine semantics.

## Pair Matrix by Requested Concern

| Concern | Covered by |
| --- | --- |
| Shared-data shapes | DV-2, DV-7, DV-8, DV-9 |
| Ownership | DV-1, DV-6 |
| State mutation | DV-3, DV-4, DV-7 |
| Lifecycle | DV-3, DV-4, DV-6 |
| Preview | DV-2 |
| Diagnostics/evidence | DV-5, DV-7, DV-9 |
| Tests | DV-3, DV-10 |
| Recovery | DV-4, DV-6, DV-10 |

## Gate Decision

Do not finalize the spine yet. Resolve DV-1 through DV-6 as architecture decisions rather than implementation details. DV-7 through DV-10 can be fixed in the same pass or moved to precise Deferred/open items only if they receive explicit owners and revisit conditions before stories for the affected seams are split.
