# 21. Generate TypeScript types from OpenAPI spec

Date: 2026-03-04

## Status

Accepted

## Context

The frontend communicates with the backend via a REST API documented by an OpenAPI 3.1.0 spec. Previously, TypeScript types for API response shapes were maintained manually in `frontend/src/types.ts`. This approach had several drawbacks:

- Types could drift out of sync with the actual backend schema
- Adding or renaming backend fields required manual updates in two places
- There was no automated way to detect when the API changed in a breaking way

The backend already generates an OpenAPI spec via SpringDoc. This means a machine-readable, up-to-date spec is always available.

## Decision

We will use [`openapi-typescript`](https://openapi-ts.dev/) to generate TypeScript types directly from the committed OpenAPI spec, replacing the manually maintained types.

The generated file (`frontend/src/types/api-generated.d.ts`) is checked into the repository and produced by running:

```
pnpm generate-api-types
```

A hand-authored module (`frontend/src/types/api.ts`) re-exports the generated schema types under their original names and defines frontend-only types (`DocumentKind`, `AnyDocument`) that have no equivalent in the OpenAPI spec. All application code imports from `~/types/api`.

The existing `useRisBackend<T>(...)` fetch pattern is kept unchanged.

A full client generator like `openapi-fetch` would also generate the fetching logic. We chose type generation only to keep the simplicity and easy Nuxt integration of our `useRisBackend` composable.

## Consequences

- TypeScript types for API responses are derived from a single source of truth (the OpenAPI spec)
- Schema drift between backend and frontend is surfaced as a type error after running type generation
