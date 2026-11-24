# 29. No API versioning until go-live

Date: 2026-11-24

## Status

Proposed

## Context

The API is not yet officially released. Until go-live, its responses will
continue to change: field names are being translated into German, and fields
the portal does not need are being removed.

So far, we have not decided how to version these changes.

Versioning requires more than a version number in the path. It needs clear
rules for:

- what counts as a breaking change,
- how long older versions remain supported,
- how API users are informed about changes.

These rules also have to be implemented and maintained. Before go-live, this
effort brings no benefit, because the API is not yet in use and its responses
are still changing.

## Decision

We will not introduce API versioning before go-live.

- Until go-live, we change endpoints and responses directly, without
  maintaining versions.
- At go-live, the API reflects the data published at that point: the
  endpoints, field names and fields that are public then.
- We keep the `/v1` prefix in the paths. It does not imply a versioning
  scheme, but it lets us introduce one later without changing the existing
  paths.
- Versioning must be in place before any response is changed after go-live.

## Consequences

- Until go-live, we can rename and remove fields without maintaining older
  versions.
- Until go-live, API users must expect breaking changes. The API documentation
  states that the API is not yet stable.
- After go-live, any change to a response is a breaking change for API users.
  Such changes require versioning to be introduced first.
- Published `ris:` IRIs (ADR 27) follow the same rule: once published at
  go-live, they do not change.
