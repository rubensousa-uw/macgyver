# macgyver rebrand transition

On 2026-08-29, the current product identity changed from `maria` to
`macgyver`. The Android and iOS application identity is now
`io.github.rubensousa.macgyver`; the deep-link URI scheme is `macgyver`; and
Android settings intentionally use the new `macgyver_settings` preferences
file.

## Historical evidence remains historical

`baseline`, `baseline-visionclaw`, imported commit
`fbc72a25686016d015de1099817f73c2bddbdea5`, and `docs/baseline.md` predate
this transition. Their maria-era paths, commands, toolchain names, results,
and upstream provenance have not been rewritten. `upstream` remains
`https://github.com/Intent-Lab/VisionClaw.git` as the source provenance.

## Deliberate breaking change

The changed application and bundle identifier installs as a distinct app. It
does not read the old application preferences, own the old deep links, or
inherit prior OAuth callbacks, signing/provisioning, or Meta Wearables DAT
registration. Before shipping or testing on hardware, a maintainer must:

- register `io.github.rubensousa.macgyver` and the `macgyver` callback with
  every OAuth/deep-link consumer and Meta Wearables developer configuration;
- create or select matching Android/iOS signing and provisioning identities;
- decide whether any user data needs an explicit export/import migration; and
- preserve the current gateway/agent and MCP deployment identities until an
  explicit stateful migration can retain user sessions, vault records, and
  OAuth credentials.

No physical-device validation is implied by this rename. The Meta Adventurer
camera milestone remains only compiled and ready for physical validation until
its physical acceptance gates are recorded.

## Remaining external operations

The local checkout is `/home/hermes/Projects/macgyver` and its configured
`origin` is `https://github.com/rubensousa-uw/macgyver.git`. The remote
repository's ownership and reachability still need verification with valid
GitHub authentication and network access; local remote configuration alone is
not evidence that the remote rename completed.
