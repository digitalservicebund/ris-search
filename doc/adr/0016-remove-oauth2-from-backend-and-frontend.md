# 16. Remove OAuth2 in Favor of Basic Auth for Staging

Date: 2025-11-04

## Status

Accepted

## Context

The portal currently integrates OAuth2 for authentication both in the backend and frontend for certain environments such as Staging and Local development.
While this setup supports more security for features that are not yet public, it introduces significant overhead for:

* Local development
* End-to-end testing
* Staging deployments
* Onboarding new contributors

The OAuth2 setup requires running a local Keycloak server, configuring client secrets, and maintaining middleware and proxy logic in both backend and frontend.
End-to-end tests also depend on programmatic login via OAuth2, adding complexity and fragility to the pipeline.
At this stage of the project, a simple and smooth developer workflow is more valuable on non-production environments, while 
Staging environment can be easily protected with basic auth.

## Decision

Remove OAuth2 authentication from both the backend and frontend.

* Remove the OAuth2 stack locally and in CI
* Reduce environment variables and setup complexity
* Keep staging protected with basic auth without exposing private functionality

### Replacement Strategy

* In development and staging, access will be controlled via **Basic Auth** at the ingress layer.
* When internal portal functionality is introduced (or production requires it), we can revert to OAuth2 using the recorded commit.

### Version Tag

After merging this PR into `main`, we create a git tag for reference:

```
remove-OpenAuth
```

This tag points to the version right before removing OAuth2, making it easy to restore the functionality if required later.

## Rollout Plan

1. Remove OAuth2 client and server code from backend and frontend.
2. Delete associated environment variables and secrets.
3. Remove Keycloak container and setup scripts from local dev tools.
4. Remove e2e login helpers and setup scripts.
5. Apply Basic Auth on staging ingress.
6. Tag the commit as `remove-OpenAuth`.

### Justification

The simplification speeds up development and testing, reduces infra complexity, and avoids unnecessary auth complexity for current project needs.

If and when the internal portal requires OAuth2 again, the `remove-OpenAuth` tag allows clean restoration.
