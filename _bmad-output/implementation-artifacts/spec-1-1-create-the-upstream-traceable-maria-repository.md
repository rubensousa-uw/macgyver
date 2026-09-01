---
title: 'Create the Upstream-Traceable maria Repository'
type: 'chore'
created: '2026-08-28'
status: 'done'
review_loop_iteration: 0
baseline_commit: 'NO_VCS'
context:
  - '{project-root}/_bmad-output/implementation-artifacts/epic-1-context.md'
---

<frozen-after-approval reason="human-owned intent — do not modify unless human renegotiates">

## Intent

**Problem:** The `maria` workspace contains planning artifacts but no usable Git history or Android source, so later baseline and DAT migration work cannot be traced to a reproducible VisionClaw revision.

**Approach:** Initialize the workspace from the exact current `Intent-Lab/VisionClaw` main revision, preserve that project unchanged as the tracked baseline source, and connect it to a personal GitHub fork named `maria` while retaining an explicit `upstream` remote.

## Boundaries & Constraints

**Always:** Use the authenticated personal GitHub account; name the fork exactly `maria`; record the full upstream SHA; keep `https://github.com/Intent-Lab/VisionClaw.git` as `upstream`; use `samples/CameraAccessAndroid` as the application base; keep existing local BMad planning artifacts intact and uncommitted while importing upstream; keep credentials only in GitHub CLI storage, environment variables, or ignored local files; verify the imported tracked tree has no source changes relative to upstream.

**Ask First:** Reauthenticate GitHub interactively when the configured token is rejected; reconcile rather than overwrite if an authenticated private repository named `maria` already exists; request explicit approval before replacing the empty read-only `.git` directory if the environment still prevents Git initialization.

**Never:** Expose tokens; force-push or delete an existing remote repository; import milestone implementation changes; silently choose a different upstream commit; commit `_bmad-output`, local credentials, or generated build state as part of the upstream baseline; claim that repository import proves an Android build or hardware support.

## I/O & Edge-Case Matrix

| Scenario | Input / State | Expected Output / Behavior | Error Handling |
|----------|--------------|---------------------------|----------------|
| Fresh personal fork | Valid GitHub authentication; no personal `maria` repository | Fork is created, `origin` targets the personal fork, `upstream` targets VisionClaw, and local `main` matches the recorded upstream SHA | Stop before changing remote state if any ownership or SHA check disagrees |
| Existing private repository | Authenticated account already owns `maria` | Existing repository is inspected and reused only when it is the intended VisionClaw fork | Do not overwrite, rename, delete, or force-push; require reconciliation |
| Invalid authentication | Stored GitHub token is rejected | No external repository mutation occurs | Run the supported interactive GitHub login flow and verify the account before retrying |
| Local metadata obstruction | Empty read-only `.git` blocks initialization | Replace only that verified-empty metadata directory, then initialize safely | Do not remove non-empty Git metadata or unrelated workspace files |

</frozen-after-approval>

## Correct Course Reconciliation (2026-08-30)

`macgyver` is the current product and repository identity. This Story's `maria`, VisionClaw, imported-SHA, and baseline references record historical repository-import provenance; they are not current naming requirements. The approved Sprint Change Proposal `sprint-change-proposal-2026-08-30.md` closes this Story on the verified local provenance evidence while preserving its frozen acceptance language and baseline evidence unchanged. Remote ownership/reachability, signing, OAuth/deep-link, Meta-registration, and renamed-build validation remain separate follow-ups in `docs/rebrand-transition.md`.

## Code Map

- `/home/hermes/Projects/maria/.git/` -- currently empty, read-only metadata directory; must be verified empty before controlled replacement and Git initialization.
- `/home/hermes/Projects/maria/AGENTS.md` -- machine-local project guidance that must remain present and uncommitted during the pristine upstream import.
- `/home/hermes/Projects/maria/_bmad/` -- local BMAD installation; preserve as local process infrastructure, not upstream source.
- `/home/hermes/Projects/maria/_bmad-output/` -- canonical planning/spec artifacts; preserve locally and exclude from the imported upstream comparison.
- `https://github.com/Intent-Lab/VisionClaw.git` -- canonical upstream; `main` currently resolves to `fbc72a25686016d015de1099817f73c2bddbdea5`.
- `{project-root}/samples/CameraAccessAndroid/` -- required Android application base after import.

## Tasks & Acceptance

**Execution:**
- [x] `.git/` -- verify the placeholder contains no repository data, replace it only if necessary, and initialize `main` -- establish functional local version control without destroying history.
- [x] Git remotes and tracked tree -- fetch `Intent-Lab/VisionClaw`, check out exact upstream `main`, and retain it as `upstream` -- establish a canonical, reproducible source baseline.
- [x] GitHub personal account -- authenticate safely, detect any existing `maria`, and create or reconcile the fork without overwriting remote work -- satisfy personal ownership and remote traceability.
- [x] `samples/CameraAccessAndroid/` -- verify the application base exists at the recorded revision -- gate Story 1.2 on the required source layout.
- [x] Local repository evidence -- verify branch, full SHA, remotes, clean tracked diff, and credential-ignore behavior -- prove the import meets the story contract.

**Acceptance Criteria:**
- Given the authenticated personal GitHub account, when setup completes, then it owns a repository named exactly `maria` whose source derives from `Intent-Lab/VisionClaw`.
- Given local `main`, when Git evidence is inspected, then `HEAD` is the recorded full upstream SHA, `upstream` points to VisionClaw, `origin` points to the personal fork, and no tracked milestone source change exists.
- Given the imported tree, when its layout is inspected, then `samples/CameraAccessAndroid` exists and is selected as the Android application base.
- Given local and CI credential configuration, when tracked files and logs are inspected, then no GitHub Packages token, DAT credential, or access token is present.

## Spec Change Log

## Design Notes

The upstream SHA currently matches the architecture seed exactly, so no seed ratification is required. Local BMAD artifacts predate Git initialization and are operational context rather than changes to the imported VisionClaw baseline; the clean-tree proof therefore compares tracked upstream content, not unrelated untracked planning files.

## Verification

**Commands:**
- `git rev-parse HEAD` -- expected: `fbc72a25686016d015de1099817f73c2bddbdea5`.
- `git remote get-url upstream && git remote get-url origin` -- expected: canonical VisionClaw upstream and authenticated personal `maria` fork.
- `git diff --quiet upstream/main...HEAD && git diff --quiet` -- expected: no tracked source divergence.
- `test -d samples/CameraAccessAndroid` -- expected: success.
- `git ls-remote --symref origin HEAD` -- expected: reachable personal fork with a default branch.

**Manual checks (if no CLI):**
- Confirm the GitHub repository owner is the authenticated personal account and no secret appears in tracked files or command output.

**Rebrand verification (2026-08-30):**
- `git diff --check` passed after the Android, documentation, and deployment-identity corrections.
- Earlier isolated `:app:compileDebugKotlin` runs reached the Kotlin compile task and generated a Gradle report with three pre-existing AGP deprecation warnings and no error diagnostic, but did not terminate within the review window. A later run used the authenticated GitHub CLI token only in the Gradle process and resolved all Meta DAT dependencies successfully. The 768 MiB scope was OOM-killed after 11m29s; rerunning with the existing 1.5 GiB expanded scope exposed and then validated the DAT 0.9 compatibility fixes: `DeviceSession.removeCamera()` was removed and `DeviceSelector.activeDeviceFlow()` replaced the retired parameterized selector call. `:app:compileDebugKotlin` completed successfully in 47s. No token was printed, stored, or committed.

## Suggested Review Order

**Repository provenance**

- Start with the approved intent and exact upstream/fork contract.
  [`spec-1-1-create-the-upstream-traceable-maria-repository.md:14`](./spec-1-1-create-the-upstream-traceable-maria-repository.md#L14)

- Confirm `origin` is the push target while `upstream` remains the comparison source.
  [`config:6`](../../.git/config#L6)

**Local-state safety**

- Review machine-local artifact and recursive credential exclusions.
  [`exclude:8`](../../.git/info/exclude#L8)

### Review Findings

- [x] [Review][Decision] Reconcile the obsolete Story 1.1 contract with the macgyver rebrand — The user chose macgyver as canonical. A Correct Course proposal is still required to supersede/update the stale Story 1.1 contract and sprint tracker; the installed Correct Course workflow cannot be completed without a PRD.
- [x] [Review][Decision] Define the hosted-service cutover and state-preservation strategy — The user chose to retain the existing deployment identities and URLs until an explicit stateful migration can preserve sessions, vaults, and OAuth credentials.
- [x] [Review][Decision] Preserve or amend baseline evidence deliberately — The user chose to restore the historical baseline document and keep later clarification outside it.
- [x] [Review][Patch] Align README clone URLs with the configured macgyver origin [README.md:66]
- [x] [Review][Patch] Restore the actual Android project directory in README setup and architecture references [README.md:118]
- [x] [Review][Patch] Correct the transition document’s completed-origin statement [docs/rebrand-transition.md:37]
- [x] [Review][Defer] Add automated gateway response-contract coverage for renamed model identifiers [gateway/src/server.ts:146] — deferred, no gateway test harness currently exists; add authenticated streaming and non-streaming assertions when test infrastructure is introduced.
