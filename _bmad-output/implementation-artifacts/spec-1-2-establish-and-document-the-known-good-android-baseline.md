---
title: 'Establish and Document the Known-Good Android Baseline'
type: 'chore'
created: '2026-08-28'
status: 'done'
review_loop_iteration: 0
baseline_commit: 'fbc72a25686016d015de1099817f73c2bddbdea5'
context:
  - '{project-root}/_bmad-output/implementation-artifacts/epic-1-context.md'
---

<frozen-after-approval reason="human-owned intent — do not modify unless human renegotiates">

## Intent

**Problem:** The imported VisionClaw revision has not been built on this host, so later DAT migration failures cannot be separated from upstream, toolchain, credential, or environment failures.

**Approach:** Inventory the exact unmodified Android baseline, prepare only ignored/local toolchain inputs, run the baseline build inside the mandated memory scope, and record reproducible evidence in `docs/baseline.md`. On success, preserve the result on a local `baseline` branch and `baseline-visionclaw` tag without claiming hardware verification.

## Boundaries & Constraints

**Always:** Start from tracked source exactly equal to `fbc72a25686016d015de1099817f73c2bddbdea5`; keep `hindsight.service` running; execute Gradle only through `/home/hermes/.local/bin/codex-memory-run`, one heavy command and one worker at a time; expose JDK 17 at `/tmp/maria-toolchains/jdk-17` and Android SDK 35 at `/tmp/maria-toolchains/android-sdk`; override the upstream macOS JBR path and 2 GiB heap without modifying tracked build files; keep GitHub Packages credentials and `Secrets.kt` ignored and out of logs; document every observed warning, known problem, integration surface, command, result, and artifact path.

**Ask First:** Any fix that changes tracked Android source or build configuration before a successful unchanged build; any broad AGP, Gradle, Kotlin, Compose, SDK, DAT, or unrelated dependency modernization; any need to stop or reconfigure `hindsight.service`; any system-wide installation requiring administrator privileges.

**Never:** Run Gradle outside the memory runner; expose tokens; commit `local.properties`, `Secrets.kt`, SDK/JDK binaries, caches, or build outputs; silently treat a failed build as known-good; create the baseline branch/tag before success; describe compilation, mocks, emulator output, or phone fallback as Meta Adventurer hardware validation.

## I/O & Edge-Case Matrix

| Scenario | Input / State | Expected Output / Behavior | Error Handling |
|----------|--------------|---------------------------|----------------|
| Unchanged baseline succeeds | Exact upstream source plus valid local JDK, SDK, credential, and `Secrets.kt` | `:app:assembleDebug` succeeds in memory scope and the APK/result are documented | Create the local baseline commit, branch, and tag only after evidence is complete |
| Missing local prerequisite | JDK, SDK, package credential, or ignored secrets file absent | Install or create only local/ignored inputs, then retry the unchanged source | Record the prerequisite and never place its secret value in documentation or output |
| Focused build blocker | Unchanged source fails for one narrow, attributable defect | Record full failure and propose the smallest tracked correction | Obtain approval before changing tracked source; rebuild and document divergence |
| Broad incompatibility | Success would require toolchain/dependency modernization | Baseline remains unproven and DAT migration does not begin | Stop and request a separate scope decision with exact evidence |

</frozen-after-approval>

## Code Map

- `gradle/wrapper/gradle-wrapper.properties:3` -- pins Gradle 8.14.1.
- `gradle/libs.versions.toml:1` -- canonical AGP, Kotlin, Compose BOM, DAT 0.4.0, runtime, and test dependency pins.
- `samples/CameraAccessAndroid/app/build.gradle.kts:14` -- Android SDK levels, ABI, Java/Kotlin targets, dependencies, and build variants.
- `samples/CameraAccessAndroid/gradle.properties:9` -- upstream heap and macOS JBR settings that require command-line overrides on this host.
- `samples/CameraAccessAndroid/settings.gradle.kts:40` -- GitHub Packages repository and token/local-property lookup.
- `samples/CameraAccessAndroid/app/src/main/java/com/meta/wearable/dat/externalsampleapps/cameraaccess/Secrets.kt.example:1` -- template for the ignored local secrets file.
- `samples/CameraAccessAndroid/app/src/main/java/com/meta/wearable/dat/externalsampleapps/cameraaccess/livekit/LiveKitSessionViewModel.kt:66` -- current LiveKit/DAT session, preview, frame, reconnect, and gateway flow.
- `samples/CameraAccessAndroid/app/src/main/java/com/meta/wearable/dat/externalsampleapps/cameraaccess/wearables/WearablesViewModel.kt:41` -- registration, permission, device-selection, and wearable state.
- `samples/CameraAccessAndroid/app/src/main/java/com/meta/wearable/dat/externalsampleapps/cameraaccess/stream/StreamingService.kt:19` -- foreground-service and wake-lock lifecycle.
- `samples/CameraAccessAndroid/app/src/androidTest/java/com/meta/wearable/dat/externalsampleapps/cameraaccess/InstrumentationTest.kt:67` -- sole existing test suite, whose routing/permission assumptions require documentation.
- `README.md:261` -- upstream behavioral/file-map claims that diverge from the imported Android implementation.

## Tasks & Acceptance

**Execution:**
- [x] `docs/baseline.md` -- record provenance, full version inventory, host prerequisites, credential locations, integration map, documentation/test mismatches, and source-divergence table -- make the baseline reproducible and auditable.
- [x] Ignored local build inputs -- prepare JDK 17, Android SDK 35, empty-safe `Secrets.kt`, and package authentication without touching tracked source -- satisfy build prerequisites safely.
- [x] `samples/CameraAccessAndroid` -- verify `hindsight.service`, then execute memory-scoped `:app:assembleDebug` with one worker and Linux-safe JDK/JVM overrides -- prove or disprove the unchanged baseline.
- [x] `docs/baseline.md` -- append the timestamped command, result, warnings, APK path/hash, known problems, and explicit non-hardware disclaimer -- preserve authoritative evidence.
- [x] Git baseline refs -- after success, create local branch `baseline`, commit `docs/baseline.md` plus only the approved extended-icons build-blocker fix with `chore: establish working Android baseline`, and tag `baseline-visionclaw` -- establish the migration starting point.

**Acceptance Criteria:**
- Given exact upstream source and local-only prerequisites, when the baseline build runs, then it executes through the memory runner with at most one worker and no tracked dependency upgrade.
- Given the build result, when `docs/baseline.md` is reviewed, then it records the source SHA, DAT/AGP/Gradle/Kotlin/Compose/SDK versions, exact command, result, warnings, known problems, relevant Android files, and current integration surfaces.
- Given a successful unchanged build, when repository history is inspected, then `baseline` and `baseline-visionclaw` point to the documented baseline commit and the commit message is exactly `chore: establish working Android baseline`.
- Given any report or ref created by this story, when its claims are inspected, then it distinguishes software baseline success from physical Meta Adventurer validation.

## Spec Change Log

- 2026-08-28 — Repeated memory-scoped builds isolated D8 heap exhaustion while transforming the 87.5 MiB `material-icons-extended` runtime. The user explicitly authorized replacing that dependency with material-icons core plus locally vendored definitions for only the seven extended icons used by the app. The baseline commit may include only this focused correction and its documentation; keep all other Android behavior, dependency versions, toolchain pins, and upstream source unchanged.
- 2026-08-28 — The approved correction compiled successfully, but post-correction `mergeExtDexDebug` attempts still exhausted bounded heap/metaspace or entered sustained low-progress `MemoryHigh` behavior inside the mandatory 768 MiB scope. Only valid partial DEX output exists; no APK was produced. The software baseline remains unproven, the story remains `in-progress`, and the baseline branch/commit/tag task remains unchecked. This result makes no physical Meta Adventurer validation claim.
- 2026-08-28 — After the user stopped the remote-control-only `codex-rc.service`, the host reported 3.5 GiB available RAM and 1.5 GiB free swap. The user authorized continuation. The next build may add an opt-in override to the existing memory runner while preserving its defaults, then run one worker with `MemoryHigh=1G`, `MemoryMax=1.5G`, and swap disabled. This authorization changes only the local execution envelope; all source, verification, ref, and hardware-claim boundaries remain unchanged.
- 2026-08-28 — The user subsequently authorized raising only the opt-in baseline profile's `MemoryHigh` to 1.4 GiB while retaining `MemoryMax=1.5G`, `MemorySwapMax=0`, one Gradle worker, the pinned baseline toolchain, and the runner's original default profile. This remains a local execution-envelope change only; AGP and all other tracked dependency versions remain unchanged.
- 2026-08-28 — The expanded profile with `MemoryHigh=1400M`, `MemoryMax=1536M`, swap disabled, one worker, and a 896 MiB Gradle heap completed `:app:assembleDebug` in 1 minute 48 seconds. The 78,830,897-byte APK has SHA-256 `519adc557a8476a63ee52eeb7a94d84d9d2cc53c126cd1f10447283461827d30`. Local branch `baseline` and tag `baseline-visionclaw` both resolve to `1ce978de6a5a5254340331b46ba7fc06e7e59925`, whose subject is exactly `chore: establish working Android baseline`. This proves only the software baseline; physical Meta Adventurer acceptance remains separate.

## Design Notes

Prefer temporary or user-local toolchains and command-line overrides so the first build remains an observation of upstream source. The Android app currently routes camera/audio/session behavior primarily through LiveKit while retaining legacy raw WebRTC and stream classes; the baseline document must describe observed code rather than repeat stale README claims.

## Verification

**Commands:**
- `git diff --name-only fbc72a25686016d015de1099817f73c2bddbdea5 -- samples/CameraAccessAndroid` -- expected: only the approved extended-icons dependency replacement, seven vendored icon definitions, and their import updates.
- `CODEX_MEMORY_RUN_PROFILE=maria-baseline-expanded ANDROID_HOME=/tmp/maria-toolchains/android-sdk ANDROID_SDK_ROOT=/tmp/maria-toolchains/android-sdk /home/hermes/.local/bin/codex-memory-run ./gradlew --no-daemon --no-parallel --max-workers=1 -Dorg.gradle.jvmargs="-Xmx896m -Dfile.encoding=UTF-8" -Dorg.gradle.java.home=/tmp/maria-toolchains/jdk-17 -Pkotlin.compiler.execution.strategy=in-process :app:assembleDebug` from `samples/CameraAccessAndroid` -- expected: `BUILD SUCCESSFUL`.
- `sha256sum samples/CameraAccessAndroid/app/build/outputs/apk/debug/app-debug.apk` -- expected: a recorded non-empty APK digest.
- `git show -s --format='%s' baseline && git rev-parse baseline baseline-visionclaw` -- expected: exact commit intent and identical refs.
- `git status --short` -- expected: no tracked or sensitive local input left pending.

**Manual checks (if no CLI):**
- Confirm `docs/baseline.md` includes every required field, accurately identifies stale documentation/tests, and makes no hardware-working claim.
