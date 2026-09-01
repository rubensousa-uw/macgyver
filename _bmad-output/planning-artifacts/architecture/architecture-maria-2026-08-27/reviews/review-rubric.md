# Rubric-Walker Review — Architecture Spine

- **Target:** `ARCHITECTURE-SPINE.md`
- **Lens:** Complete BMad good-spine checklist
- **Date:** 2026-08-27
- **Verdict:** **REVISION REQUIRED** — the spine is strong and substantially covers the canonical SPEC, but five unresolved seams can still make independently built units incompatible. Keep it `draft` until the high findings are resolved or converted into explicit, adoption-blocking open questions.

## Gate Summary

| Checklist item | Result | Judgment |
| --- | --- | --- |
| Fixes the real divergence points for the level below and misses none | **Fail** | Hardware-handle ownership, frame representation, explicit stop/failure transitions, and Android background behavior remain ambiguous or contradictory. |
| Every AD Rule is enforceable and prevents its stated divergence | **Fail** | Most rules are enforceable, but AD-2 conflicts with AD-1; AD-7 uses unbounded language; AD-11 lacks a verification mechanism. |
| Nothing under Deferred could let two units diverge | **Fail** | Four load-bearing `[ASSUMPTION]` decisions remain deferred until the same readiness boundary the spine is meant to enable, and recovery timing lacks even provisional bounds. |
| Named technology is verified-current | **Pass** | Stack entries are pinned, sources are commit-specific, and DAT 0.9.0 is the current published 0.9.x package in the cited official repository/package source. |
| Ratifies rather than contradicts the brownfield codebase | **Conditional** | The upstream seed was inspected and preservation is now explicit, but the locally imported baseline does not yet exist; the spine correctly acknowledges that ratification is pending. |
| Covers the driving SPEC capabilities | **Fail** | CAP-1, CAP-2, CAP-3, CAP-5, CAP-6, and CAP-8 are covered well; CAP-4/CAP-7 remain incomplete because the binding graph cannot express required stop and several failure paths. |
| Does not weaken an inherited parent spine | **N/A** | No parent spine is declared. |
| Every owned dimension is decided, deferred, or open | **Fail** | Deployment/backend scope and local build operations are covered, but the Android foreground/background execution policy and evidence retention/export policy are not decided or explicitly deferred. |

## High Findings

### H1 — Hardware-handle ownership contradicts the port boundary

- **Evidence:** AD-1 says only the Meta adapter package may import DAT and that runtime/domain contracts contain no DAT types. AD-2 then says the process-scoped `WearableRuntime` is the sole owner of `DeviceSession`, `Camera`, `Stream`, and SDK collectors.
- **Why it matters:** A runtime implementation can reasonably retain DAT handles to satisfy AD-2, while an adapter implementation can reasonably retain them to satisfy AD-1. Those are incompatible ownership models and undermine the named Ports-and-Adapters paradigm.
- **Disposition:** **Autofix.** State that `WearableRuntime` is the sole lifecycle **coordinator** through `WearableAdapter`, while `MetaDatWearableAdapter` exclusively owns DAT handles and SDK collectors. The runtime owns only provider-neutral desired intent, snapshot, recovery policy, and telemetry.

### H2 — The binding transition graph cannot model required commands and failures

- **Evidence:** AD-3 allows only edges in the canonical graph. That graph has no path for an explicit user stop while the device remains connected, even though AD-7 defines user stop and physical acceptance requires repeated start/stop. It also lacks `CAMERA_STARTING -> STREAM_FAILED` and session-start failure edges although `STREAM_FAILED` covers camera/stream failures.
- **Why it matters:** The reducer, command handler, and tests cannot all comply: they must either invent forbidden edges, misuse `DISCONNECTED` for a still-connected device, or omit required stop/start and startup-failure behavior.
- **Disposition:** **Discuss/upstream correction.** Amend the canonical SPEC companion and then the spine with explicit stop/teardown destinations and failure edges. Do not silently invent a spine-only graph that diverges from the canonical contract.

### H3 — The frame seam is not interoperable enough for separate adapter and preview units

- **Evidence:** AD-5 requires copied bytes plus dimensions, format, timestamp, and session identity, but does not fix an allowed frame representation, stride/orientation metadata, timestamp semantics, or which side decodes/transforms DAT raw or compressed frames. The Deferred section addresses future AI encoding only, not the preview required now.
- **Why it matters:** A Meta adapter can emit raw YUV, RGB, bitmap-backed bytes, or encoded frames while a preview unit makes a different assumption. Both can satisfy the prose and still fail to interoperate.
- **Disposition:** **Discuss, then autofix.** Either normalize at the adapter boundary to one app-owned preview format, or define a closed provider-neutral frame union with complete plane/stride/rotation/codec metadata and assign decode/rotation ownership. Keep future AI encoding deferred separately.

### H4 — The four core assumptions are deferred across the readiness boundary they control

- **Evidence:** AD-2, AD-3, AD-5, and AD-7 are load-bearing ownership, state, frame, and recovery decisions marked `[ASSUMPTION]`. Deferred says to ratify them before epics are implementation-ready.
- **Why it matters:** These decisions are precisely what epics and stories need to partition work coherently. If ratification changes them after decomposition, the story boundaries and acceptance tests can become invalid.
- **Disposition:** **Open item/blocker.** Ratify against the pinned upstream source before finalizing the spine, or obtain explicit user adoption of each assumption and change the later local-code check to a compatibility audit with an escalation rule for discovered conflicts.

### H5 — Android background/lock behavior is implied but not decided

- **Evidence:** AD-2 introduces `StreamingService` for “foreground liveness,” while the state mapping treats Android lifecycle as a recoverable pause. The acceptance matrix requires background/foreground and lock/unlock recovery, but no rule says whether streaming must continue under a camera foreground service or pause/teardown and recreate on return.
- **Why it matters:** Service, runtime, and UI units can choose incompatible lifetime policies, permissions, notifications, and recovery expectations. This is the main missing operational/environmental decision at feature altitude.
- **Disposition:** **Discuss.** Choose one policy for this milestone and bind service ownership, foreground-service type/notification behavior, lifecycle event mapping, and expected acceptance transitions. If hardware evidence must decide, make it an explicit pre-implementation open question rather than leaving both models live.

## Medium Findings

### M1 — Recovery timing and cross-session correlation are under-specified

- **Evidence:** AD-7 says retry timing is “bounded and hardware-tuned,” while Deferred postpones delays and retry limits until physical evidence. AD-8 defines reconnect latency across a disconnect and the first frame of a newly created session, but telemetry and `session_id` are per session attempt.
- **Risk:** Runtime and telemetry units may disagree on attempt limit/backoff and on whether reconnect latency belongs to the old session, new session, or a recovery operation.
- **Disposition:** **Defer precisely.** Seed provisional bounds/configuration for physical testing and define a recovery/correlation identifier or explicit ownership of cross-session metrics. Revisit values after the first physical run without changing the correlation semantics.

### M2 — Transition history and acceptance evidence have schemas but no retention/export owner

- **Evidence:** AD-9 exposes transition history and the evidence table defines complete records, but no decision states whether history is an in-memory ring, structured log stream, file export, or persisted record; no size/retention boundary exists.
- **Risk:** Diagnostics may show only live transitions while the acceptance harness expects durable evidence, or an implementation may retain unbounded sensitive operational history.
- **Disposition:** **Autofix or defer.** Assign a bounded in-memory diagnostic history and a deliberate, user-triggered redacted acceptance export, or explicitly bind another retention model. State that private frame/audio payloads are never retained.

### M3 — Registration, permission, and eligibility gates are observable but not command preconditions

- **Evidence:** The structural diagram names registration and permissions, and diagnostics owns registration state, but no AD defines when a session/camera command is permitted or how a permission/registration failure maps into domain state and recovery.
- **Risk:** UI and adapter units can race session creation before registration/camera/Bluetooth permission readiness or classify the same failure differently.
- **Disposition:** **Autofix.** Add provider-neutral command guards: select only a connected compatible camera-capable device, require registration and required Android/DAT permissions before session/camera start, and map failed guards through the existing typed error layers without false readiness.

### M4 — Typed DAT error retention needs a boundary-safe formulation

- **Evidence:** AD-9 says to retain the typed DAT cause for diagnosis, while AD-1 forbids DAT types outside the Meta adapter and `WearableFailure` exposes only provider-neutral fields.
- **Risk:** One unit may leak SDK exception/error objects across the port; another may discard useful error codes entirely.
- **Disposition:** **Autofix.** Keep the typed SDK cause adapter-internal, map stable SDK type/code into provider-neutral `causeType`/`code`, and expose only the redacted mapped failure beyond the adapter.

### M5 — Brownfield preservation is stated but not yet verifiable

- **Evidence:** AD-11 requires preserving compatible behavior but defines neither the inventory/evidence location nor directed regression checks for phone fallback, throttling, audio, Gemini Live, OpenClaw, reconnect, WebRTC, settings, and session state.
- **Risk:** “Preserved” can be asserted without proving that the untouched or adapted paths still compile and behave as before.
- **Disposition:** **Autofix.** Make `docs/baseline.md` own the inspected integration inventory and require directed baseline/migration checks for every touched preserved seam; untouched seams should be protected by a diff/inventory review.

## Low Findings

None. The remaining observations are consequences of the findings above rather than independent defects.

## Strong Points

- The paradigm is named and the dependency direction is unusually clear.
- DAT 0.9.0 and the upstream baseline are pinned rather than left floating.
- First-frame readiness, state serialization, backpressure, monotonic telemetry, privacy, host memory safety, and physical-only hardware claims are expressed as concrete invariants.
- The capability map, diagnostic ownership table, and canonical state evidence make most of the SPEC traceable without bloating the structural seed.
- Deployment and infrastructure scope is explicit: on-device Android only, with no milestone backend, cloud video path, realtime provider, gateway, operator, or tool deployment.

## Gate Decision

Do not mark the spine `final` yet. Resolve H1 directly, correct H2 in the canonical SPEC package and spine together, decide H3 and H5, and close or explicitly adopt H4. The medium findings can be fixed in the same pass or preserved as precise Deferred/open items with owners and revisit conditions.
