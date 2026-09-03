- source_spec: `/home/hermes/Projects/macgyver/_bmad-output/implementation-artifacts/spec-1-2-establish-and-document-the-known-good-android-baseline.md`
  summary: Replace the stale Android instrumentation suite with coverage for the current permissions, capture-source, LiveKit, gateway/session, and non-hardware mock flows before the physical-release gate.
  evidence: The retained test targets Ray-Ban-only pairing and legacy UI text, omits current runtime permissions, and does not exercise LiveKitStreamScreen or the gateway/session path.

## Deferred from: code review of spec-1-1-create-the-upstream-traceable-maria-repository (2026-08-29)

- Add automated gateway response-contract coverage for renamed model identifiers — no gateway test harness currently exists; add authenticated streaming and non-streaming assertions when test infrastructure is introduced.

## Deferred from: code review of spec-2-1-migrate-and-pin-the-dat-0-9-baseline (2026-09-02)

- source_spec: `/Users/rubensousa/Documents/Projects/macgyver/_bmad-output/implementation-artifacts/spec-2-1-migrate-and-pin-the-dat-0-9-baseline.md`
  summary: Re-run the prescribed memory-scoped build and four-case connected MockDeviceKit suite against the current lifecycle-hardening checkout.
  evidence: This context lacks the required runner/JDK (`exit 127`), and the recorded iMac run predates the uncommitted hardening changes.
- source_spec: `/Users/rubensousa/Documents/Projects/macgyver/_bmad-output/implementation-artifacts/spec-2-1-migrate-and-pin-the-dat-0-9-baseline.md`
  summary: Add direct LiveKit glasses-path instrumentation with observable DAT fakes or spies for invalid-frame rejection and ordered teardown.
  evidence: The current shared gate test and StreamViewModel lifecycle cases cover the available seams; no LiveKit DAT injection harness exists yet.

## Deferred from: code review of spec-2-1-migrate-and-pin-the-dat-0-9-baseline (2026-09-03)

- source_spec: `/Users/rubensousa/Documents/Projects/macgyver/_bmad-output/implementation-artifacts/spec-2-1-migrate-and-pin-the-dat-0-9-baseline.md`
  summary: Re-run the prescribed current-checkout build and connected-test verification before closing Story 2.1.
  evidence: The available four-test pass belongs to an earlier commit (`79deee1`); the reviewed checkout (`ab82365`) could not be verified because the prescribed runner/toolchain is unavailable in this environment.
- source_spec: `/Users/rubensousa/Documents/Projects/macgyver/_bmad-output/implementation-artifacts/spec-2-1-migrate-and-pin-the-dat-0-9-baseline.md`
  summary: Add direct integration coverage for the LiveKit glasses DAT lifecycle, first-frame readiness, invalid-frame rejection, and teardown.
  evidence: The current Android suite exercises only StreamViewModel and the shared predicate; no LiveKit DAT injection harness exists.
- source_spec: `/Users/rubensousa/Documents/Projects/macgyver/_bmad-output/implementation-artifacts/spec-2-1-migrate-and-pin-the-dat-0-9-baseline.md`
  summary: Inject invalid compressed, codec-configuration, and malformed frames through each production raw-frame consumer.
  evidence: The current test calls the shared predicate directly but does not exercise StreamViewModel, LiveKitSessionViewModel, or GlassesVideoCapturer with invalid frames.
- source_spec: `/Users/rubensousa/Documents/Projects/macgyver/_bmad-output/implementation-artifacts/spec-2-1-migrate-and-pin-the-dat-0-9-baseline.md`
  summary: Add deterministic tests for generation fencing and late session, camera, frame, and photo callbacks.
  evidence: Existing tests stop after a successful frame and contain no deferred DAT session/camera or photo-result fakes.
- source_spec: `/Users/rubensousa/Documents/Projects/macgyver/_bmad-output/implementation-artifacts/spec-2-1-migrate-and-pin-the-dat-0-9-baseline.md`
  summary: Replace UI-reset teardown assertions with observable ordered cleanup checks and await asynchronous release.
  evidence: The current teardown test observes the default STOPPED UI state and does not spy on camera stop, removeCamera, or session stop.
- source_spec: `/Users/rubensousa/Documents/Projects/macgyver/_bmad-output/implementation-artifacts/spec-2-1-migrate-and-pin-the-dat-0-9-baseline.md`
  summary: Add a late photo-result test proving retired generations cannot update the current UI state.
  evidence: The current photo test completes before teardown and does not use a deferred capture result.
- source_spec: `/Users/rubensousa/Documents/Projects/macgyver/_bmad-output/implementation-artifacts/spec-2-1-migrate-and-pin-the-dat-0-9-baseline.md`
  summary: Add focused failure-path tests for session timeout, camera setup, stream start, detach, and foreground-service failures.
  evidence: No current test injects a hanging or failing DAT result/flow or a failed foreground-service start.
- source_spec: `/Users/rubensousa/Documents/Projects/macgyver/_bmad-output/implementation-artifacts/spec-2-1-migrate-and-pin-the-dat-0-9-baseline.md`
  summary: Determine a hardware-tuned practical maximum raw-frame payload before enforcing an additional size ceiling.
  evidence: The current gate prevents arithmetic overflow but accepts very large SDK-reported dimensions; the story does not define a safe device-specific ceiling.
- source_spec: `/Users/rubensousa/Documents/Projects/macgyver/_bmad-output/implementation-artifacts/spec-2-1-migrate-and-pin-the-dat-0-9-baseline.md`
  summary: Revisit moving MockDeviceKit-dependent main sources to a test/debug source set and making `mwdat-mockdevice` test-only.
  evidence: The current app keeps the dependency in `implementation` because `MockDeviceKitViewModel` and `WearablesViewModel` use it from main sources; the scoped exception is intentional for this story.

## Deferred review: code review of spec-2-1-migrate-and-pin-the-dat-0-9-baseline (2026-09-03 follow-up)

- source_spec: `/Users/rubensousa/Documents/Projects/macgyver/_bmad-output/implementation-artifacts/spec-2-1-migrate-and-pin-the-dat-0-9-baseline.md`
  summary: Fence the pre-existing phone-camera callbacks and repeated starts against late delivery and overwritten managers.
  evidence: `startPhoneCamera` replaces `phoneCameraManager` without stopping a prior manager, and its callback can update UI or WebRTC after `stopStream`; the migration changed only the DAT path and stream-state type here.
- source_spec: `/Users/rubensousa/Documents/Projects/macgyver/_bmad-output/implementation-artifacts/spec-2-1-migrate-and-pin-the-dat-0-9-baseline.md`
  summary: Recover the pre-existing Wearables monitoring collectors after SDK flow failures.
  evidence: The active-device, registration, device-list, and device-metadata collectors have no retry or gate reset when an SDK flow terminates exceptionally; this behavior predates the focused DAT lifecycle migration.
- source_spec: `/Users/rubensousa/Documents/Projects/macgyver/_bmad-output/implementation-artifacts/spec-2-1-migrate-and-pin-the-dat-0-9-baseline.md`
  summary: Renew or reacquire the pre-existing ten-minute foreground-service wake lock for longer sessions.
  evidence: The wake lock expires after ten minutes while the retained non-null handle prevents `acquireWakeLock` from acquiring a replacement; the migration only moved acquisition earlier in service startup.
