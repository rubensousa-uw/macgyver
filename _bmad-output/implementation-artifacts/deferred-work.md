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
