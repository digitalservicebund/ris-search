# 10. Run end-to-end tests in pipeline

Date: 2024-12-19

## Status

Accepted

## Context

We were running end-to-end tests involving the Postgres databases, OpenSearch cluster, Keycloak instance, backend, and
frontend against our staging environment. The end-to-end tests would happen after the code being tested had been
deployed
to that staging environment.

The main reason why that approach was chosen was the non-availability of seed data. Instead, the development environment
relied on a rather involved data ingestion flow that accessed the caselaw and norms database schemas.

Switching to a file-based data exchange format allowed us to implement an end-to-end testing pipeline where a set of 10
norms and case law documentation units each would be provided as test data.

## Decision

- A sample of production data is added to the repository, to be used in CI and when developing locally.
- The end-to-end tests will run in an ephemeral environment in the CI pipeline.
- All dependencies will be present in the repository.
- For speedy execution and efficiency, the end-to-end step will reuse the frontend and backend images built in previous
  steps, or from the main branch (if no change has been made to the underlying code).
- To not interfere with the standard development setup, a separate OpenSearch index / alias set is created when the "
  e2e" profile is active in Spring. A full re-import will happen on each service start.
- For the time being, the end-to-end profile list will be limited to just chromium. Other browser profiles might be
  added later, potentially being run less frequently (e.g., prior to a production deployment).
- The frontend and backend GitHub actions workflows will both call the end-to-end workflow. This might be de-duplicated
  later.

## Consequences

- This change will provide faster feedback, making the end-to-end tests more useful and preventing more issues from
  affecting the staging environment.
- Using a stable set of seed data will increase the stability of tests and allow for more specific assertions.
- Using a sample of live data guarantees that the tests assert actual desired application behavior.
- Due to licensing reasons, the data will need to be altered before the contents of the repository are made available to
  the public.

## Notes

- To set the `BRANCH_NAME` variable in GitHub actions, `${{ github.head_ref || github.ref_name }}` should be used
  instead of just `${{ github.ref_name }}`, because that one will differ between normal runs and runs triggered by pull
  requests.
- Relative paths in the dockerized backend are be resolved to `/workspace` instead of the `WORKDIR` set in the
  Dockerfile. To work around that, an absolute path is set in the compose file:
  `LOCAL_FILE_STORAGE=/app/backend/e2e-data`. This will override the relative path declared in `application-e2e.yaml`,
  still used when running the backend locally with the e2e profile.
