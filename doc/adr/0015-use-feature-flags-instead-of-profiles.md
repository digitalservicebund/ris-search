# 27. Replace `NUXT_PUBLIC_PROFILE` with `PRIVATE_FEATURES_ENABLED`

Date: 2025-10-30

## Status

Accepted

## Context

The frontend currently relies on a string environment variable `NUXT_PUBLIC_PROFILE` with the values `internal`, `public`, or `prototype` to decide which features appear in which environment. In practice:

* All deployments use `public` except the prototype which uses `prototype`.
* An `internal` portal does not exist yet.
* The terminology mixes audience with environment which causes confusion during development, reviews, and deployments.
* Feature checks like `isPublicProfile()`, `isPrototypeProfile()`, and `isInternalProfile()` are spread across the codebase and CI.

This setup makes it harder than necessary to reason about feature availability, authentication, and e2e test paths.

## Problem Statement

We need a simpler and less ambiguous way to control visibility of private features and related behaviors such as authentication. The solution must:

* Remove the overloaded notion of profiles.
* Make intent explicit at call sites.
* Be easy to configure across deployments.
* Be testable in CI for both states.
* Avoid breaking existing deployments during migration.

## Decision

Introduce a single boolean flag `PRIVATE_FEATURES_ENABLED` and remove `NUXT_PUBLIC_PROFILE`.
Also, to treat the `NUXT_PUBLIC_AUTH_ENABLED` as a feature that is private.

### Semantics

* `PRIVATE_FEATURES_ENABLED=true` means private features are visible including authentication.
* `PRIVATE_FEATURES_ENABLED=false` means private features are hidden.
* The old helpers map as follows:

    * `isPublicProfile()` becomes `privateFeaturesEnabled()`
    * `isInternalProfile()` becomes `privateFeaturesEnabled()`
    * `isPrototypeProfile()` becomes `!privateFeaturesEnabled()`
    * `authEnabled` becomes `privateFeaturesEnabled()`

### Configuration

Set `PRIVATE_FEATURES_ENABLED` as an environment variable in all deployments:

* Prototype: `false`
* All other deployments: `true`

## Rollout Plan

1. Add the new flag to all deployments with values:

    * Prototype: `PRIVATE_FEATURES_ENABLED=false`
    * Others: `PRIVATE_FEATURES_ENABLED=true`
2. Deploy the new frontend version that reads `PRIVATE_FEATURES_ENABLED` and uses `privateFeaturesEnabled()` everywhere.
3. Remove `NUXT_PUBLIC_PROFILE` from all deployments and configuration files.

## Migration Steps

* Remove `NUXT_PUBLIC_PROFILE` from `.env*`, Helm values, Docker envs, and any runtime config.
* Delete helpers `isPublicProfile`, `isPrototypeProfile`, `isInternalProfile`.
* Replace imports and usages with `privateFeaturesEnabled()`.
* Remove separate “auth enabled” checks and wire auth to `privateFeaturesEnabled()`.
* Update documentation and onboarding references.

## Infrastructure

* Ensure the new flag is present in Kubernetes, Docker Compose, and any serverless or preview environments.
* Remove `NUXT_PUBLIC_PROFILE` from secrets managers, value files, and CD templates.

## Consequences

* Clearer intent at call sites and in reviews.
* Reduced configuration surface and fewer invalid states.
* A single toggle controls both private UI and authentication which reduces drift.
* CI can exhaustively test both public-like and private-like behavior by flipping one flag.
* Minor one-time refactor cost to remove old helpers and env usage.
