---
title: 'Rename maria to macgyver'
type: 'refactor'
created: '2026-08-29'
status: 'in-progress'
review_loop_iteration: 0
baseline_commit: '1ce978de6a5a5254340331b46ba7fc06e7e59925'
context:
  - '{project-root}/AGENTS.md'
  - '{project-root}/_bmad-output/specs/spec-meta-adventurer-camera-milestone/SPEC.md'
---

<frozen-after-approval reason="human-owned intent — do not modify unless human renegotiates">

## Intent

**Problem:** The project is currently named `maria`, while its repository, workspace, BMad metadata, mobile clients, gateway, and user-facing identities still inherit names from `maria`, VisionClaw, or CameraAccess. This makes the intended product identity unclear and leaves the remote, package identities, deep links, and documentation inconsistent.

**Approach:** Rebrand the current project to `macgyver` on a dedicated branch from the immutable `baseline` reference. Rename the GitHub repository and local workspace, establish the approved Android and iOS identity `io.github.rubensousa.macgyver`, migrate current source and product-facing identifiers, and record the transition without rewriting historical baseline evidence.

## Boundaries & Constraints

**Always:** Use `macgyver` as the current project and display name; use Android/iOS identifier `io.github.rubensousa.macgyver`, URI scheme `macgyver`, and Android preferences key `macgyver_settings`; create the work branch from `baseline`; preserve `upstream` as `https://github.com/Intent-Lab/VisionClaw.git`; keep `baseline`, `baseline-visionclaw`, imported SHA `fbc72a25686016d015de1099817f73c2bddbdea5`, and all prior build commands/results as auditable historical evidence; regenerate BMad managed/rendered context rather than hand-edit generated files; keep secrets and ignored local inputs out of source control and logs.

**Ask First:** A GitHub rename cannot be completed with the authenticated account; the target repository already exists or is owned by someone else; Android/iOS signing, provisioning, OAuth callback, deep-link consumer, Meta Wearables registration, or stored-user-data migration needs values or credentials not present locally; the workspace move would overwrite an existing `/home/hermes/Projects/macgyver` directory.

**Never:** Alter the baseline commit/tag or claim a renamed build verifies Meta Adventurer hardware; replace the upstream remote; silently retain a configured origin URL pointing to `rubensousa-uw/maria`; bulk-rewrite historical evidence to present it as originally produced by macgyver; rename `/tmp/maria-toolchains` or the machine-local `maria-baseline-expanded` runner profile in this repository change; run an unscoped Gradle build.

## I/O & Edge-Case Matrix

| Scenario | Input / State | Expected Output / Behavior | Error Handling |
| --- | --- | --- | --- |
| Normal rebrand | Clean `baseline`, available GitHub rename, absent target workspace | New branch and remote/workspace identify as `macgyver`; current products use the approved identities | Stop before external/local move if preflight disagrees |
| Historical baseline reference | Existing specs, build evidence, branch/tag, and toolchain paths containing `maria` | Evidence remains truthful and gains an explicit rebrand record | Do not mechanical-replace historical paths, commands, or provenance |
| Existing install or registration | Device has old package/preferences/deep links or provider registration is unknown | New package installs as a distinct app; required migration/re-registration is documented | Stop for unavailable signing/registration authority; do not pretend continuity |
| Target collision | GitHub repo or local `macgyver` path exists | No overwrite or remote reassignment occurs | Report the collision and request reconciliation |

</frozen-after-approval>

## Code Map

- `.git/config` and Git refs -- origin currently targets `https://github.com/rubensousa-uw/maria.git`; `upstream`, `baseline`, and `baseline-visionclaw` are read-only provenance anchors.
- `AGENTS.md:1` -- managed project-context block identifies `maria`; refresh through BMad project-context tooling after the rebrand.
- `_bmad/config.toml:13`, `_bmad/core/config.yaml:7`, `_bmad/bmm/config.yaml:13` -- current BMad project name; installer-managed files require their supported regeneration/override path.
- `docs/baseline.md:1` and `_bmad-output/` historical artifacts -- preserve baseline evidence; add a transition record instead of rewriting command paths, completed story identifiers, or imported provenance.
- `gateway/package.json:2`, `gateway/package-lock.json`, `gateway/README.md`, `gateway/src/`, `gateway/public/dashboard.html`, and deployment manifests -- gateway package and product-facing VisionClaw identifiers.
- `samples/CameraAccessAndroid/app/build.gradle.kts:16-22`, `app/src/main/AndroidManifest.xml:17-55`, `app/src/main/res/values/{strings,themes}.xml`, and the `com/meta/wearable/dat/externalsampleapps/cameraaccess` source/test tree -- Android namespace, application ID, display name, URI scheme, preferences, resources, imports, and package paths to migrate together.
- `samples/CameraAccess/CameraAccess.xcodeproj/project.pbxproj:253-521`, shared scheme, `CameraAccess/Info.plist:7-81`, entitlements, project/target/source/test paths -- iOS product, bundle identifiers, URI/OAuth schemes, signing references, and target names requiring coordinated Xcode updates.
- `_bmad/render/` -- generated local workflow paths containing the old workspace; recreate after the move rather than editing snapshots.

## Tasks & Acceptance

**Execution:**
- [ ] Git/GitHub and workspace -- preflight the `macgyver` target, create `chore/rename-macgyver` from `baseline`, rename `rubensousa-uw/maria` to `rubensousa-uw/macgyver`, update `origin`, then move the checkout without overwriting existing content -- make repository identity and local path coherent while preserving upstream and baseline refs.
- [x] BMad/project documentation -- regenerate/update current project metadata and `AGENTS.md`; add a concise rebrand transition record that maps current identity to historical `maria` evidence -- keep guidance and provenance accurate.
- [x] Gateway and shared documentation -- rename product/package references from VisionClaw/CameraAccess where they define macgyver's current public identity, updating lockfile consistently -- avoid stale service metadata and public labels.
- [x] Android client -- migrate the namespace and source/test directory tree to `io.github.rubensousa.macgyver`; update Gradle application ID, manifest label/scheme/theme/resource identifiers, preferences key, provider authority assumptions, imports, test/package declarations, and user-visible labels -- deliver one internally consistent Android identity.
- [x] iOS client -- rename project/target/scheme/product paths and bundle identifiers to macgyver, update Info.plist display, URI, OAuth and entitlements values, and document signing/DAT registration follow-up -- keep the Xcode model, deep links, and install identity consistent.
- [x] Verification documentation -- record package-identity breaking-change behavior and required user-data/deep-link/registration follow-up, without treating unavailable Apple/physical-device checks as passed -- ensure the migration is truthful and recoverable.

**Acceptance Criteria:**
- Given a clean baseline checkout, when the rebrand completes, then the active branch, GitHub origin, and local workspace identify `macgyver`, while `upstream`, `baseline`, and `baseline-visionclaw` retain their original provenance.
- Given Android and iOS source trees, when build metadata and product configuration are inspected, then both use `io.github.rubensousa.macgyver`, `macgyver` is the display/deep-link identity, and no stale current product identifier remains.
- Given historical baseline material, when it is inspected after the rebrand, then its maria-era commit, toolchain paths, commands, tag, and outcome remain accurate and the rebrand record explains their historical status.
- Given a renamed Android checkout, when the scoped baseline build is executed, then it uses the required memory runner, produces a build result without exposing secrets, and makes no physical-hardware claim.

## Spec Change Log

## Design Notes

The old Android and iOS package identities are inherited provider/sample identities, so this is a coordinated application-identity migration rather than a text replacement. A changed bundle/application ID intentionally creates a new installation identity; document, rather than fabricate, data migration, signing, OAuth/deep-link consumer, and Meta registration results. The baseline ref remains immutable evidence: the rebrand applies to current state and adds traceability from `macgyver` back to `maria`.

## Verification

**Commands:**
- `git status --short && git branch --show-current && git remote -v` -- expected: clean rebrand branch; origin uses `rubensousa-uw/macgyver`; upstream remains VisionClaw.
- `git show -s --format='%H %s' baseline && git rev-parse baseline-visionclaw` -- expected: unchanged baseline SHA and commit subject.
- `git grep -in 'maria\|visionclaw\|cameraaccess' -- ':!docs/baseline.md'` -- expected: only deliberately retained historical/provenance references documented by the transition record.
- `rg -n 'namespace|applicationId|android:scheme|PREFS_NAME|PRODUCT_BUNDLE_IDENTIFIER|CFBundleDisplayName' samples/CameraAccessAndroid samples/CameraAccess` -- expected: approved macgyver identifiers only.
- `CODEX_MEMORY_RUN_PROFILE=maria-baseline-expanded ANDROID_HOME=/tmp/maria-toolchains/android-sdk ANDROID_SDK_ROOT=/tmp/maria-toolchains/android-sdk /home/hermes/.local/bin/codex-memory-run ./gradlew --no-daemon --no-parallel --max-workers=1 -Dorg.gradle.jvmargs="-Xmx896m -Dfile.encoding=UTF-8" -Dorg.gradle.java.home=/tmp/maria-toolchains/jdk-17 -Pkotlin.compiler.execution.strategy=in-process :app:assembleDebug` from `samples/CameraAccessAndroid` -- expected: memory-scoped Android result; never run the equivalent command outside the runner.

**Manual checks (if no CLI):**
- Inspect the GitHub repository name and origin URL after the remote rename, the moved workspace without an overwritten target, Android manifest/package output and macgyver deep link, and the Xcode target/scheme/bundle signing configuration. Verify the wearable developer registration separately before relying on the renamed iOS/Android identity.
