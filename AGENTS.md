<!-- bmad:context -->
<!-- Verified 2026-09-03 against a3d6076bc4f19f99129fc98a2ae2d2861f22acb1. Managed by bmad-project-context; edits inside this block are replaced on refresh. Keep anything you want preserved outside the markers. -->

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

- On this macOS host, run Android Gradle from `samples/CameraAccessAndroid` with `JAVA_HOME=/usr/local/opt/openjdk@17`, `ANDROID_HOME=/Users/rubensousa/Library/Android/sdk`, `--no-daemon --no-parallel --max-workers=1`, a bounded `-Dorg.gradle.jvmargs="-Xmx2g -Dfile.encoding=UTF-8"`, and `-Pkotlin.compiler.execution.strategy=in-process`. Before connected tests, boot `macgyver_api35_x86_64` and confirm `adb devices -l` reports it as `device`.

## Conventions that differ from defaults

- Treat the first usable camera frame, not a started stream state, as the vision-readiness boundary.
- Handle `DeviceType.META_GLASSES` explicitly; never require `RAYBAN_META`.
- Use `io.github.rubensousa.macgyver` for current Android/iOS identity, `macgyver` for deep links, and `macgyver_settings` for Android preferences.

<!-- /bmad:context -->
