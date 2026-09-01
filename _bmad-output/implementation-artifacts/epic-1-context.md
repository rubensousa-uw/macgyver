# Epic 1 Context: Known-Good maria Android Baseline

<!-- Compiled from planning artifacts. Edit freely. Regenerate with compile-epic-context if planning docs change. -->

> **Current identity and historical provenance (2026-08-30):** `macgyver` is the active product and repository identity. The `maria`, VisionClaw, imported-SHA, `baseline`, and `baseline-visionclaw` references in this Epic record historical provenance and do not impose current naming requirements.

## Goal

Establish a controlled, reproducible starting point for the milestone: a personal `maria` repository derived from VisionClaw, traceable to its exact upstream source, with the unchanged `samples/CameraAccessAndroid` application successfully compiled and its environment and existing integration surfaces documented. This baseline must make later DAT migration failures distinguishable from pre-existing issues and must not introduce milestone behavior or broad dependency modernization.

## Stories

- Story 1.1: Create the Upstream-Traceable maria Repository
- Story 1.2: Establish and Document the Known-Good Android Baseline

## Requirements & Constraints

- The repository must be named exactly `maria`, be owned by the authenticated personal GitHub account, and retain traceability to `https://github.com/Intent-Lab/VisionClaw`. Its default branch must initially contain the selected VisionClaw source without milestone implementation changes.
- Use `samples/CameraAccessAndroid` as the Android application base. Record the exact imported VisionClaw source SHA and compare it with seed `fbc72a2`; a material difference requires ratifying the architecture seed before implementation proceeds.
- Compile the unchanged Android baseline before any DAT lifecycle or wearable behavior change. Preserve the observed baseline stack unless compatibility proves otherwise: DAT 0.4.0, AGP 8.6.0, Gradle 8.14.1, Kotlin 2.1.20, Compose BOM 2024.04.01, compile SDK 35, target SDK 34, and min SDK 31.
- Produce `docs/baseline.md` containing the source commit, dependency and SDK versions, build result, warnings, known problems, relevant Android files, and the existing camera lifecycle, phone-camera fallback, frame throttling, audio, Gemini Live, OpenClaw routing, reconnect, WebRTC, settings, preview, and session-state integration surfaces.
- If the unchanged application does not build, allow only a focused build-blocking correction. Document the original failure, the exact correction, and the resulting divergence from upstream before rebuilding. If success would require broad AGP, Kotlin, Compose, SDK, or unrelated dependency modernization, document the incompatibility and stop before DAT migration.
- Keep GitHub Packages tokens and DAT credentials only in environment variables or ignored local configuration. Secrets, access tokens, DAT credentials, and raw private audio must not enter source control, logs, diagnostics, or test fixtures.
- Protect the 8 GiB no-swap development host: keep `hindsight.service` running and execute any authorized memory-intensive Gradle command only through `/home/hermes/.local/bin/codex-memory-run`, with one heavy command and at most one worker. Never retry the same heavy command outside that scope.
- Compilation establishes a known-good software baseline only; it is not physical Meta Adventurer verification and must not be presented as such.

## Technical Decisions

- Delivery is staged and brownfield-first: preserve and document VisionClaw before migration work so regressions and compatibility changes remain attributable.
- Maintain branch progression `main` → `baseline` → `feat/dat-0.9` → `feat/adventurer`; experimental work must not occur on a known-good branch.
- Finalize the baseline with commit intent `chore: establish working Android baseline`. Create the `baseline` branch and `baseline-visionclaw` tag only after the documented baseline build succeeds.
- Preserve compatible VisionClaw behavior and inspect its existing integration surfaces before later edits. This epic creates evidence about those surfaces; it does not redesign them.
- Keep the future architecture boundaries intact: Meta DAT remains behind `WearableAdapter`, while `WearableAdapter`, `RealtimeProvider`, `Operator`, and `Tools` remain independently evolvable. No future realtime, Hermes, tool, backend, cloud-video, production Bluetooth-audio, or AI frame-forwarding integration belongs in this epic.

## Cross-Story Dependencies

- Story 1.2 depends on Story 1.1 providing the imported source, exact SHA, upstream remote, and selected Android application base.
- Epic 2 must not begin DAT 0.9 migration until the baseline build and documentation succeed, or until any blocking incompatibility has been explicitly resolved through a separate scope decision.
- The exact imported SHA and the baseline evidence become the comparison point for the DAT migration record and all later compatibility decisions.
