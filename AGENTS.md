<!-- bmad:context -->
<!-- Verified 2026-08-29 against 1ce978de6a5a5254340331b46ba7fc06e7e59925. Managed by bmad-project-context; edits inside this block are replaced on refresh. Keep anything you want preserved outside the markers. -->

## macgyver

Android and iOS wearable-assistant project derived from VisionClaw and using Meta Wearables DAT. The canonical first-hardware-milestone contract is `_bmad-output/specs/spec-meta-adventurer-camera-milestone/SPEC.md`; historical baseline provenance is recorded in `docs/baseline.md` and the identity transition in `docs/rebrand-transition.md`.

## Policy

- Keep `WearableAdapter`, `RealtimeProvider`, `Operator`, and `Tools` independent; keep Meta DAT behind `WearableAdapter`.
- Never claim or tag Adventurer camera support as working until physical acceptance passes; compilation is only readiness for physical validation.
- Never log API/OAuth secrets, access tokens, or raw private audio.
- Preserve `baseline`, `baseline-visionclaw`, and the VisionClaw upstream remote as historical provenance; do not rewrite maria-era evidence as macgyver-era evidence.

## Where things are

- For hardware-milestone work, read `_bmad-output/specs/spec-meta-adventurer-camera-milestone/SPEC.md` and every declared companion.
- Rebrand constraints and outstanding signing, OAuth, deep-link, and Meta-registration work are in `docs/rebrand-transition.md`.

## Running and verifying

- Run any memory-intensive Gradle command only through `/home/hermes/.local/bin/codex-memory-run`, with one worker; set `JAVA_HOME=/tmp/maria-toolchains/jdk-17` and keep `hindsight.service` running.

## Conventions that differ from defaults

- Treat the first usable camera frame, not a started stream state, as the vision-readiness boundary.
- Handle `DeviceType.META_GLASSES` explicitly; never require `RAYBAN_META`.
- Use `io.github.rubensousa.macgyver` for current Android/iOS identity, `macgyver` for deep links, and `macgyver_settings` for Android preferences.

<!-- /bmad:context -->
