# Sprint Change Proposal — macgyver Planning Reconciliation

**Date:** 2026-08-30  
**Mode:** Batch  
**Change classification:** Moderate — backlog and planning-artifact reconciliation; no hardware-milestone implementation change.

## 1. Issue Summary

### Trigger

Story 1.1, **Create the Upstream-Traceable maria Repository**, exposed a conflict during review: the current repository and product are `macgyver`, while active BMad configuration, the sprint tracker, the epic document, and the original Story 1.1 contract still present `maria` as the active direction.

### Problem statement

The rebrand deliberately preserves the VisionClaw import and maria-era baseline as historical provenance. The active planning layer, however, still mixes historical facts with current identity requirements. This leaves Story 1.1 `in-progress` even though its repository-provenance evidence is locally verified, and prevents the planned DAT 0.9 work from proceeding through an internally consistent workflow.

### Evidence

- Current local `origin` is `https://github.com/rubensousa-uw/macgyver.git`; `upstream` remains VisionClaw.
- `main` and `upstream/main` resolve to imported VisionClaw SHA `fbc72a2`; `baseline` and `baseline-visionclaw` resolve to `1ce978d`.
- `docs/rebrand-transition.md` explicitly preserves maria-era baseline evidence and declares `macgyver` current.
- The sprint tracker still names project `maria`, points to `/home/hermes/Projects/maria`, and keeps Story 1.1 in progress.

## 2. Checklist Results

| Checklist area | Result | Finding |
| --- | --- | --- |
| 1. Trigger and evidence | Done | Strategic identity change with concrete local Git and transition-document evidence. |
| 2. Epic impact | Done | Epic 1 needs a current-identity annotation and Story 1.1 closure; Epics 2–4 remain technically valid. |
| 3. PRD impact | Done | A new internal PRD now supplies the required active identity and provenance rules. |
| 3. Architecture impact | Done | No adapter, reducer, state-machine, lifecycle, privacy, or hardware-acceptance rule changes. Add an identity/provenance note only. |
| 3. UX impact | N/A | No standalone UX contract exists; the developer diagnostics and live preview requirements are unchanged. |
| 3. Secondary artifacts | Action-needed | BMad config, sprint status, and Story 1.1 need reconciliation. Remote ownership, external registrations, and a successful renamed build remain separately open. |
| 4. Direct adjustment | Viable | Moderate effort, low technical risk; preserves scope and momentum. |
| 4. Rollback | Not viable | Reverting the rebrand or baseline would destroy valid provenance and create avoidable disruption. |
| 4. MVP review | Not viable | The milestone remains achievable; no scope reduction is needed. |

## 3. Recommended Approach

Adopt **Option 1: Direct Adjustment**.

Treat `macgyver` as current planning identity, retain Maria/VisionClaw material only where it records historical provenance, and close Story 1.1 as historical import evidence after the reconciliation is applied. Do not change the hardware architecture, device acceptance matrix, implementation sequence, or the physical-verification gate.

**Effort:** Moderate documentation/tracking change.  
**Risk:** Low for code and architecture; medium for process accuracy if historical labels are mechanically replaced rather than explicitly preserved.  
**Timeline impact:** One planning-maintenance pass before Story 2.1; no new feature implementation.

## 4. Detailed Change Proposals

### 4.1 Add the active planning PRD

**Artifact:** `_bmad-output/planning-artifacts/prds/prd-maria-2026-08-30/prd.md`

**OLD:** No PRD exists; Correct Course cannot run because the active planning direction is implicit in a SPEC and transition note.

**NEW:** Finalized internal PRD, **macgyver Planning Reconciliation and Adventurer Camera Milestone**, establishes:

- `macgyver` as current identity;
- `maria`, VisionClaw, `baseline`, and `baseline-visionclaw` as historical provenance;
- no relaxation of first-usable-frame, privacy, provider-neutrality, memory-safety, or physical-acceptance gates; and
- Story 1.1 reconciliation as the required planning closure before implementation resumes.

**Rationale:** Supplies the mandatory product-level basis for Correct Course without replacing the canonical hardware SPEC.

### 4.2 Reconcile BMad project metadata and sprint tracker

**Artifacts:** `_bmad/bmm/config.yaml`; `_bmad-output/implementation-artifacts/sprint-status.yaml`

**OLD:** Both identify project `maria`; the tracker references `/home/hermes/Projects/maria` and keeps Story 1.1 in progress.

**NEW:**

- Set active BMad project name and tracker project to `macgyver`.
- Set `story_location` to `/home/hermes/Projects/macgyver/_bmad-output/implementation-artifacts`.
- Preserve historical Story 1.1 identifier/text, but mark it `done` with a reconciliation note pointing to this proposal and `docs/rebrand-transition.md`.
- Keep Story 1.2 `done`, Epic 1 `done`, and Epics 2–4 backlog. Update `last_updated`.

**Rationale:** The tracker becomes an accurate operational control surface without changing historical evidence.

### 4.3 Annotate, rather than rewrite, the epic plan and Story 1.1

**Artifacts:** `_bmad-output/planning-artifacts/epics.md`; `_bmad-output/implementation-artifacts/spec-1-1-create-the-upstream-traceable-maria-repository.md`; `_bmad-output/implementation-artifacts/epic-1-context.md`

**OLD:** The documents speak as if `maria` is current product identity and leave Story 1.1 open.

**NEW:** Add a concise top-level **Current identity and historical provenance** notice:

> `macgyver` is the current product and repository identity. The references to `maria`, VisionClaw, `fbc72a2`, `baseline`, and `baseline-visionclaw` in Epic 1 and Story 1.1 record historical import/baseline provenance and are not current naming requirements.

Update only current-state status/summary sections to show Story 1.1 and Epic 1 complete after this proposal is applied. Do not alter frozen historical acceptance language, recorded commands, or `docs/baseline.md`.

**Rationale:** Makes the temporal boundary explicit while preserving evidence integrity.

### 4.4 Add architecture identity boundary

**Artifact:** `_bmad-output/planning-artifacts/architecture/architecture-maria-2026-08-27/ARCHITECTURE-SPINE.md`

**OLD:** The architecture is named `maria` and includes deferred identity/owner language inherited from pre-rebrand planning.

**NEW:** Add an identity/provenance note at the beginning and replace only deferred active-identity wording:

- Current implementation identity is `macgyver`.
- VisionClaw/maria source and baseline references remain historical evidence.
- The existing `upstream` remote remains provenance; configured `origin` is current repository configuration pending remote verification.

**Rationale:** Prevents stale planning language from being misread as an architectural constraint. No architecture invariant, package boundary, lifecycle rule, or technology decision changes.

### 4.5 Record unresolved external operations separately

**Artifact:** `docs/rebrand-transition.md` (retain as source of truth)

**OLD:** The transition note already records remote verification, signing, OAuth/deep-link, Meta registration, data migration, and compiled-build follow-ups.

**NEW:** No scope expansion; retain the note and reference it from the adjusted planning artifacts.

**Rationale:** Keeps external coordination separate from the planning reconciliation and avoids falsely closing unverified operations.

## 5. Impact Analysis

| Area | Impact |
| --- | --- |
| Epic 1 | Becomes complete after reconciliation; baseline evidence remains historical. |
| Epics 2–4 | No requirement, order, or acceptance changes; remain blocked only until the planning state is consistent. |
| PRD | New PRD is the active identity contract; canonical SPEC remains the detailed hardware contract. |
| Architecture | Documentation annotation only; all AD-1 through AD-12 invariants remain unchanged. |
| UX | No change. |
| Code/deployment | No code, hosted infrastructure, Fly/MCP app, or credential migration action authorized by this proposal. |
| Hardware validation | No claim change: it remains unverified until physical acceptance passes. |

## 6. Implementation Handoff

**Scope:** Moderate.

- **Product Owner / Developer:** apply the planning, story-status, and tracker changes exactly as proposed; preserve frozen historical evidence.
- **Developer:** run `git diff --check` and targeted static checks on changed planning files; do not run a heavy Gradle build for this proposal.
- **Maintainer (external follow-up):** verify remote GitHub ownership/reachability, configure signing/OAuth/deep-link/Meta registration, and obtain a completed renamed Android build on a suitable environment before physical testing.

## 7. Success Criteria

- Active BMad metadata and sprint tracking use `macgyver` and the current workspace path.
- Story 1.1 and Epic 1 are closed with an explicit provenance/reconciliation annotation.
- The canonical hardware requirements and physical gate remain unchanged.
- No historical baseline command, tag, SHA, or `docs/baseline.md` content is rewritten.
- `docs/rebrand-transition.md` remains the source of truth for unverified external operations.

## 8. Approval Gate

Approve this proposal to authorize the planning-artifact updates described in §4. It does not authorize external GitHub, signing, OAuth, Meta registration, deployment, hardware, or code changes.

## 9. Approval and Handoff

**Approved:** 2026-08-30 by Hermes.  
**Applied scope:** Planning metadata, sprint tracking, and provenance annotations only.  
**Handoff:** Product Owner / Developer maintain the reconciled planning artifacts; external-operation owners retain the follow-ups documented in `docs/rebrand-transition.md`.
