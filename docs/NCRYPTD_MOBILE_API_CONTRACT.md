# nCryptd mobile integration contract

Status: **client contract scaffold; Android transport/auth adapter not yet implemented**.

This contract maps nCrypt AI to the backend integration already used by the
nCryptd web workspace. It does not claim the backend has been independently
audited or that mobile access is ready for production.

## What exists today

The inspected nCryptd frontend contains:
- Supabase email/password sign-in and account creation through Supabase Auth.
- Session restoration and auth-state subscription.
- Workspace lookup through `workspace_members` joined to `workspaces`.
- Project and issue reads/writes through the `projects` and `issues` tables.
- Member listing through `workspace_members` and `profiles`.
- A workspace-member RPC named `add_workspace_member`.
- Realtime subscriptions to project and issue changes.

The frontend requires `VITE_SUPABASE_URL` and
`VITE_SUPABASE_PUBLISHABLE_KEY`. The repository does not include a database
migration/schema or RLS policy definitions, so table constraints, policies,
triggers, and RPC security still need to be verified against the deployed
Supabase project. The standalone local tasks/notes in the dashboard remain
browser-local; team projects/issues use Supabase.

## Ownership boundary

- nCryptd/Supabase owns accounts, sessions, workspace membership, roles,
  projects, issues (the shared task/board items), profiles, and workspace
  authorization.
- nCrypt AI owns conversations, provider credentials, model execution, tool/MCP
  runtime, and device-local preferences.
- Android may cache nCryptd data for offline viewing, but the server remains the
  source of truth. Cache writes must never silently override newer server state.
- Do not mirror nCryptd project/task CRUD into the Android Room database as a
  second authoritative store.

## Recommended mobile connection

Use the same Supabase project as nCryptd, with:
- Supabase Auth for sign-in, refresh, and sign-out.
- The public/publishable project key as the client API key.
- The signed-in user's access token as the bearer token for PostgREST and
  Realtime requests.
- Existing Postgres Row Level Security policies as the authorization boundary.

Never embed a Supabase service-role key, database password, or privileged server
secret in the APK. The publishable key is not a substitute for RLS: mobile
access must remain denied unless the deployed policies explicitly authorize the
authenticated user.

Before enabling the integration, verify that RLS is enabled and correctly
configured on every exposed table, and that RPC execution is restricted to
appropriate roles. Test cross-workspace IDOR attempts, revoked memberships,
expired tokens, and role changes.

## Supabase resources used by the web client

| Resource | Existing web-client use |
|---|---|
| `/auth/v1/token?grant_type=password` | Email/password sign-in (Supabase Auth SDK) |
| `/auth/v1/user` | Current authenticated user |
| `/rest/v1/workspace_members` | Membership and role lookup |
| `/rest/v1/workspaces` | Workspace relationship lookup |
| `/rest/v1/projects` | Project read/create/update/delete |
| `/rest/v1/issues` | Board item read/create/update |
| `/rest/v1/profiles` | Member profile lookup |
| `/rest/v1/rpc/add_workspace_member` | Existing-account workspace member addition |

The exact query shapes, foreign-key relationship names, and RLS behavior must
be confirmed from the deployed schema before implementing the Android adapter.
Do not guess relationship aliases from the frontend's inferred TypeScript types.

## Android contract scaffold

`NcryptdWorkspaceContract.kt` defines transport-neutral models and an API
interface. `NcryptdSupabaseResources` records the known resource paths. This
is intentionally not a fake working client: a production adapter still needs
secure token persistence/refresh, TLS-only base URL validation, bounded response
bodies, timeouts, cancellation, structured errors, and realtime lifecycle
management.

The initial read slice should load:
1. Current user.
2. Workspaces and the user's membership role.
3. Projects for an authorized workspace.
4. Issues for an authorized project.

Start read-only. Add mutations only after RLS and audit expectations are
validated. Keep destructive member/project actions out of the first mobile
release.

## Rollout checklist

1. Obtain the Supabase project URL and publishable key from the owner-managed
   deployment configuration (not from source code or chat).
2. Inspect deployed schema, foreign keys, RLS policies, grants, and RPC security.
3. Add Android Auth with secure token storage and refresh handling.
4. Implement PostgREST reads with the user's access token and strict response
   models.
5. Add realtime subscriptions with explicit reconnect and cleanup behavior.
6. Add read-only workspace/project/issue UI.
7. Run authorization and offline/cache conflict tests before enabling writes.
