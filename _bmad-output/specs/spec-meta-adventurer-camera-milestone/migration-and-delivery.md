# Migration and Delivery Contract

## Starting point

- Fork `https://github.com/Intent-Lab/VisionClaw` into the user's authenticated personal GitHub account, name the project and repository `maria`, and retain upstream traceability.
- Use `samples/CameraAccessAndroid/` as the Android base.
- Preserve compatible implementations for Meta DAT device access, camera streaming, phone-camera fallback, frame throttling, audio handling, Gemini Live, OpenClaw routing, reconnect, WebRTC, settings, and session state.
- Inspect repository HEAD, camera lifecycle, Gemini Live, audio routing, and OpenClaw integration before editing.

## Baseline

Run the unchanged application build first:

```bash
./gradlew assembleDebug
```

Only fix issues that prevent the baseline from building. Create `docs/baseline.md` with:

```text
VisionClaw commit:
DAT version:
AGP version:
Kotlin version:
minSdk:
targetSdk:
compileSdk:

Build result:
Warnings:
Known problems:

Relevant Android files:
```

Use commit intent `chore: establish working Android baseline`. Create `baseline-visionclaw` only after the baseline is known-good.

## DAT 0.9.x migration

1. Determine the DAT version used by baseline HEAD.
2. Resolve the latest stable 0.9.x release from official Meta sources and pin it explicitly.
3. Preserve in the migration record that DAT 0.8 introduced `DeviceType.META_GLASSES`, then verify the selected 0.9.x release's exact API behavior.
4. Read the complete changelog across the full baseline-to-target version interval before editing.
5. Create `docs/dat-migration.md` listing every breaking or behaviorally relevant change and the corresponding code decision.
6. Deliberately migrate the consolidated camera lifecycle where required:

```text
DeviceSession
  -> addCamera(...)
  -> Camera
  -> Camera.stream
```

Teardown must use the supported equivalents of:

```text
Camera.stop()
DeviceSession.removeCamera()
```

7. Compile and run directed checks after migration.

## Branch and tag discipline

The relevant sequence is:

```text
main
  -> baseline
  -> feat/dat-0.9
  -> feat/adventurer
```

- Never experiment directly on a known-good branch.
- Use `feat: support Meta Adventurer with DAT 0.9` for the completed hardware milestone commit.
- Create `adventurer-camera-working` only after the physical acceptance criteria pass.
- If physical hardware has not been tested, report the state as compiled/ready for validation and do not create the working tag.

## Host build safety

This host has 8 GiB RAM and no swap. Keep `hindsight.service` running. Any authorized memory-intensive Gradle command must run as:

```bash
/home/hermes/.local/bin/codex-memory-run ./gradlew assembleDebug
```

Run at most one memory-intensive command at a time and use one worker where supported. Do not substitute an unscoped heavy command if the runner is blocked.
