- source_spec: `/home/hermes/Projects/macgyver/_bmad-output/implementation-artifacts/spec-1-2-establish-and-document-the-known-good-android-baseline.md`
  summary: Replace the stale Android instrumentation suite with coverage for the current permissions, capture-source, LiveKit, gateway/session, and non-hardware mock flows before the physical-release gate.
  evidence: The retained test targets Ray-Ban-only pairing and legacy UI text, omits current runtime permissions, and does not exercise LiveKitStreamScreen or the gateway/session path.

## Deferred from: code review of spec-1-1-create-the-upstream-traceable-maria-repository (2026-08-29)

- Add automated gateway response-contract coverage for renamed model identifiers — no gateway test harness currently exists; add authenticated streaming and non-streaming assertions when test infrastructure is introduced.
