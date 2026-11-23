# nCrypt AI — Rebrand and nCryptd Integration

This repository is a GPL-3.0 fork of [Agora](https://github.com/newo-ether/Agora).
It preserves the upstream Android implementation while establishing the nCrypt AI
product identity. This is an independent community fork and is not affiliated with
or endorsed by the Agora maintainers.

## Product direction

- Android application: **nCrypt AI**
- Repository: **nCrypt** (rename to `nCrypt-AI` when repository administration is performed)
- Upstream: `newo-ether/Agora`
- Existing workspace: `nCryptdOfficial/nCryptd`
- Architecture: Android client + nCryptd workspace services; avoid duplicating
  nCryptd task/project ownership in the Android client.

## Rebrand principles

1. Preserve upstream license and all third-party notices.
2. Clearly mark modified files and retain upstream attribution.
3. Do not reuse Agora's name, logo, screenshots, website, or imply endorsement.
4. Keep the Android package identity separate from upstream so both apps can coexist.
5. Rebrand all locale resources, launcher assets, notifications, shortcuts, deep links,
   provider labels, and documentation consistently.
6. Keep API credentials on-device only when connecting directly to BYOK providers;
   any nCryptd service credential must use an authenticated backend and secure storage.

## Integration architecture

```text
nCrypt AI (Android / Kotlin / Compose)
  |-- Agora-derived chat, provider, local model, MCP and agent runtime
  |-- nCryptd connector (typed API client; OAuth/session token)
  |-- Local-first conversation and settings storage
  |
  +--> nCryptd backend
         |-- Authentication and authorization
         |-- Workspace / project / task APIs
         |-- Membership and permissions
         |-- Audit events and integration registry
```

nCryptd remains the source of truth for workspace projects, tasks, memberships and
shared activity. nCrypt AI owns device-local chat UX, provider configuration and
agent execution. Cross-system actions must be explicit, scoped, auditable and
permission-checked.

## Integration milestones

### M0 — Foundation
- Establish branded application label and repository documentation.
- Preserve upstream source history and license.
- Build a reproducible Android CI workflow before changing package namespaces.

### M1 — Identity and visual system
- Choose final application ID and update Gradle namespace, manifest references,
  deep links, FileProvider authorities, tests, Room schema paths and documentation.
- Replace launcher/splash assets with original nCrypt artwork.
- Update all translated app-name and assistant-name strings.

### M2 — nCryptd connector
- Define a versioned API contract for sign-in, workspace discovery, project/task
  listing, task updates and activity events.
- Implement an isolated Kotlin service/repository layer; do not put network calls
  in Compose screens.
- Use short-lived session tokens, TLS, scoped authorization and explicit logout.
- Never embed server secrets or OpenAI API keys in the APK.

### M3 — Collaborative agents
- Allow an agent to receive an explicitly selected nCryptd project/task context.
- Return proposed changes as previews; require confirmation for writes.
- Record actor, agent, operation, target and outcome in nCryptd audit events.

### M4 — Android test release
- Build debug and release variants in CI.
- Run unit tests, lint, dependency/license checks and install smoke tests.
- Publish APKs as GitHub Actions artifacts; do not publish a release until signing,
  privacy disclosures and GPL corresponding-source requirements are satisfied.

## Compatibility and safety

The upstream project currently uses Android namespace/application ID
`com.newoether.agora`. Changing it is a migration, not a cosmetic rename.
Do not perform broad text replacement: update package declarations and paths
consistently, regenerate Room schema locations where required, and run the full
test/build matrix. Keep the original upstream remote available for future merges.

## Current status

This document records the agreed direction and staged plan. It does not claim that
the package ID, launcher artwork, backend integration, or APK build has already
been changed or completed.
