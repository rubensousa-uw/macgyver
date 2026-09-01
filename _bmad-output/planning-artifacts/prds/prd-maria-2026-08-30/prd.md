---
title: macgyver Planning Reconciliation and Adventurer Camera Milestone
status: final
created: 2026-08-30
updated: 2026-08-30
---

# PRD: macgyver Planning Reconciliation and Adventurer Camera Milestone

## 0. Document Purpose

This internal PRD aligns the active product identity and planning workflow with `macgyver` while preserving the historical `maria` and VisionClaw baseline as auditable provenance. It is the product-level input required to close the obsolete Story 1.1 contract through Correct Course; it does not replace the canonical hardware contract in `_bmad-output/specs/spec-meta-adventurer-camera-milestone/`.

## 1. Vision

macgyver is a camera-first wearable assistant built from the VisionClaw baseline. Its first hardware milestone is a trustworthy Meta Adventurer camera feed: a developer can diagnose, operate, and physically validate a real wearable camera session without confusing software compilation, SDK state, or phone-camera behavior with hardware verification.

The active product and repository identity is `macgyver`. Historical material that truthfully records the `maria` baseline, the VisionClaw import, `baseline`, and `baseline-visionclaw` remains unchanged and is explicitly labeled as provenance rather than current product identity.

## 2. Target User

### 2.1 Jobs To Be Done

- As the developer and physical-device tester, I need a clear current product identity and an auditable source baseline so that later DAT migration and hardware evidence remain attributable.
- As the developer, I need live diagnostics and first-frame-based readiness so that I can distinguish a usable Adventurer camera feed from partial SDK lifecycle progress.
- As the maintainer, I need planning artifacts to separate current requirements from historical evidence so that rebranding does not invalidate baseline facts.

### 2.2 Non-Users (v1)

- Production end users requiring a public release, backend video pipeline, or realtime/agent integration.
- Users expecting migrated OAuth, deep-link, signing, or device-registration state from the prior application identity.

### 2.3 Key User Journeys

- **UJ-1. Hermes establishes the current planning baseline.** Hermes opens the `macgyver` repository, sees `macgyver` as the active identity, and can trace the preserved `maria` baseline back to VisionClaw without treating it as current product configuration.
- **UJ-2. Hermes validates the wearable milestone.** Using the registered Meta Adventurer and the developer diagnostics screen, Hermes starts streaming, observes `FIRST_FRAME_RECEIVED` before readiness, views real frames, and records physical acceptance evidence.

## 3. Glossary

- **Current identity** — the active product, repository, Android/iOS package, deep-link, and preference identity: `macgyver`.
- **Historical provenance** — immutable evidence from the VisionClaw import and `maria` baseline, including `baseline`, `baseline-visionclaw`, imported SHA `fbc72a2`, and `docs/baseline.md`.
- **Correct Course** — the controlled change-management process that reconciles active planning artifacts with an approved direction change.
- **First usable frame** — a camera callback carrying usable image data; the sole vision-readiness boundary.
- **Physical acceptance** — documented execution of the Adventurer hardware matrix; it is distinct from compile, emulator, MockDeviceKit, and phone-camera evidence.

## 4. Features

### 4.1 Planning and identity reconciliation

**Description:** The active planning surface uses `macgyver` as the current identity, while historical provenance remains truthful and unmodified. Realizes UJ-1.

#### FR-1: Current identity is explicit

The maintainer can identify `macgyver` as the canonical current identity in active project guidance, transition documentation, and current planning outputs.

**Consequences (testable):**

- Current Android/iOS identity is `io.github.rubensousa.macgyver`; the deep-link scheme is `macgyver`; Android preferences use `macgyver_settings`.
- Current repository documentation identifies the configured `origin` as `rubensousa-uw/macgyver` once remote verification is available.
- BMad configuration and sprint tracking identify the current project and workspace as `macgyver`; historical story names retain their provenance context.

#### FR-2: Historical provenance is preserved

The maintainer can inspect the historical baseline without it being rewritten as macgyver-era work.

**Consequences (testable):**

- `upstream` remains `https://github.com/Intent-Lab/VisionClaw.git`.
- `baseline`, `baseline-visionclaw`, `fbc72a2`, and `docs/baseline.md` remain auditable historical evidence.
- Rebrand documentation explains the distinction between current identity and historical provenance.

#### FR-3: Planning status is internally consistent

The maintainer can distinguish the completed software-baseline story from the historical repository-import story awaiting formal reconciliation.

**Consequences (testable):**

- Story 1.2 remains complete only as a software baseline, not a hardware validation.
- Story 1.1 is reconciled through an approved Sprint Change Proposal before the next implementation story begins.

### 4.2 Trustworthy Adventurer camera milestone

**Description:** The Android application provides a provider-neutral wearable runtime and Meta-specific adapter that make camera operation observable, recoverable, and physically testable. Realizes UJ-2.

#### FR-4: Adventurer is detected without Ray-Ban assumptions

The application accepts Meta Adventurer as `DeviceType.META_GLASSES` and exposes device, registration, session, camera, and stream state in diagnostics.

#### FR-5: Vision readiness follows a first usable frame

The application reaches vision-ready state only when a usable image frame arrives; a stream-started state or codec-only packet cannot satisfy readiness.

#### FR-6: Physical verification is gated by evidence

The application and delivery workflow distinguish baseline-known-good, compiled/ready-for-physical-validation, and physically verified states. No `adventurer-camera-working` claim or tag exists before every physical acceptance gate passes.

## 5. Cross-Cutting Requirements and Guardrails

- Keep `WearableAdapter`, `RealtimeProvider`, `Operator`, and `Tools` independent; Meta DAT stays behind `WearableAdapter`.
- Never log API/OAuth secrets, access tokens, DAT credentials, or raw private audio.
- Use a serialized reducer, bounded frame handling, monotonic timing, typed failures, and one cancellable recovery controller as defined by the canonical SPEC companions.
- Run memory-intensive Gradle work only via `/home/hermes/.local/bin/codex-memory-run`, one worker at a time, while `hindsight.service` remains running.
- Compilation, emulator tests, MockDeviceKit, and phone-camera behavior do not prove Adventurer hardware support.

## 6. MVP Scope

### 6.1 In Scope

- Formal planning reconciliation from obsolete `maria`-named active artifacts to current `macgyver` direction.
- Preservation and explicit labeling of historical VisionClaw and baseline evidence.
- The existing Epic 2–4 hardware-milestone path: DAT 0.9 migration, `META_GLASSES` selection, first-frame truth, diagnostics, recovery, and physical acceptance.

### 6.2 Out of Scope for MVP

- Rewriting historical `maria` evidence, baseline commands, tags, or imported provenance.
- Hosted-service migration, new Fly/MCP applications, session/vault/OAuth migration, or shutdown of existing deployments.
- iOS implementation changes, production Bluetooth audio, backend/cloud video, realtime-provider work, AI frame forwarding, or store release.

## 7. Success Metrics

- **SM-1:** An approved Sprint Change Proposal makes the Story 1.1 reconciliation and active planning status unambiguous. Validates FR-1–FR-3.
- **SM-2:** Physical acceptance records explicit `META_GLASSES` detection, real preview frames, `FIRST_FRAME_RECEIVED` before readiness, telemetry, and reconnect recovery. Validates FR-4–FR-6.
- **SM-C1:** Do not optimize documentation consistency by erasing historical provenance or claiming hardware verification before evidence exists.

## 8. Open Questions

1. When network/authentication are available, does the GitHub remote confirm the intended `rubensousa-uw/macgyver` ownership and default branch?
2. Which signing, OAuth, deep-link consumer, and Meta Wearables registration values must be created or selected before physical testing?
3. Does the current renamed Android checkout complete a memory-scoped compilation on CI or a sufficiently provisioned host?

## 9. Assumptions Index

- The current product identity is `macgyver`, while the `maria` baseline is historical provenance.
- The canonical hardware requirements remain those in `spec-meta-adventurer-camera-milestone` and its companions, except where this PRD explicitly supersedes stale active-identity wording.
