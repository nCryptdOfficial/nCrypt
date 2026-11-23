# nCryptd mobile API contract

Status: **proposed contract; backend not implemented**.

This contract defines the boundary between the nCrypt AI Android client and the
existing nCryptd workspace product. It does not imply that the current nCryptd
deployment already exposes these endpoints.

## Source-of-truth boundary

- nCryptd owns users, sessions, workspace membership, roles, projects, tasks,
  comments, attachments, and server-side audit history.
- nCrypt AI owns local conversations, provider configuration, model execution,
  tool/MCP runtime, and device-local settings.
- The Android app may cache workspace responses for display, but must not treat
  that cache as authoritative or silently overwrite newer server state.
- Do not duplicate nCryptd project/task CRUD or membership policy in the Android
  database. Mutations should be added only after server authorization and audit
  behavior exist.

## Current backend reality

The inspected nCryptd repository currently describes itself as a local-first
React/Vite application. Its README states that tasks, notes, and preferences
are stored in the browser profile, while authentication, integrations, live
telemetry, and server-side audit logging are not connected. A Supabase client
helper exists, but its presence is not evidence that production tables, RLS
policies, or mobile API routes are deployed.

Therefore this document is an implementation target, not a claim of working
connectivity. The Android client contract is isolated under
`app/src/main/java/com/newoether/agora/ncryptd` and must remain disabled until
the server contract and authorization are deployed and tested.

## API conventions

Base URL: configured by the user/admin for the nCryptd deployment.

Prefix: `/api/mobile/v1`

Authentication:
- HTTPS only.
- `Authorization: Bearer <user access token>`.
- Token is issued for the signed-in user; never use a Supabase service-role key
  or other privileged server secret in the APK.
- Backend validates token signature/issuer/audience and checks workspace
  membership on every request.
- Return 401 for missing/expired sessions and 403 for insufficient membership.
- Never accept a client-supplied user ID as authorization evidence.

JSON:
- UTF-8, ISO-8601 UTC timestamps.
- Stable string IDs.
- Paginated collections use `items` and nullable `nextCursor`.
- Errors use `{ "code": "...", "message": "...", "requestId": "..." }`.
- Do not return secrets or fields the caller is not permitted to read.

## Read endpoints (initial integration slice)

| Method | Path | Purpose |
|---|---|---|
| GET | `/api/mobile/v1/me` | Current authenticated user |
| GET | `/api/mobile/v1/workspaces?cursor=` | Workspaces visible to the user |
| GET | `/api/mobile/v1/workspaces/{workspaceId}/projects?cursor=` | Projects in an authorized workspace |
| GET | `/api/mobile/v1/projects/{projectId}/tasks?cursor=` | Tasks in an authorized project |
| GET | `/api/mobile/v1/tasks/{taskId}` | One task after project/workspace authorization |

Initial DTOs are represented in `NcryptdWorkspaceContract.kt`. Route identifiers
must be URL-encoded by the eventual HTTP implementation.

## Required server-side authorization

1. Resolve the user from the verified bearer token.
2. Resolve the requested workspace/project/task from server-side records.
3. Verify active membership and role for the resource's workspace.
4. Apply row-level security or equivalent authorization in the database layer.
5. Emit an audit event for sensitive reads and all mutations according to the
   nCryptd audit policy.
6. Return only the fields required by the mobile client.

Never trust workspace IDs, role names, assignee IDs, or ownership claims supplied
by the Android app without checking them against server records.

## Rollout plan

1. Implement and test the server routes and database authorization in nCryptd.
2. Publish an OpenAPI document and contract tests from the backend.
3. Implement the Android HTTP adapter with TLS, bounded response bodies,
   timeouts, cancellation, structured errors, and token refresh.
4. Add a read-only workspace/project/task screen in nCrypt AI.
5. Add mutations only after audit logging, conflict handling, and permission
   checks are covered by integration tests.
6. Enable the feature through a remote/configurable feature flag after staging
   verification.

## Explicitly out of scope for the first slice

- Writing tasks or projects from Android.
- Uploading attachments.
- Managing members or roles.
- Using Supabase service credentials in the app.
- Synchronizing local AI conversations into nCryptd.
