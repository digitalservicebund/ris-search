# 17. Remove Dependency of End-to-End Tests on Prebuilt Docker Images

Date: 2025-11-20

## Status

Accepted

## Context

Historically, our end-to-end (E2E) tests in the portal pipeline depended on backend and frontend **Docker images that were already built and pushed** to the container registry.
This created several issues:

* E2E tests were running **against images different** from what developers were using locally.
* The pipeline required **complex environment variables** to make backend–frontend communication possible inside GitHub Actions.
* The `docker-compose-ci.yml` setup contained multiple environment configs, and additional logic that does not reflect any real environment (neither local, nor staging, nor UAT).
* Debugging E2E failures locally was harder because the setup did not match the CI setup.
* Waiting for images to be pushed before tests could start introduced **unnecessary delays**.

During the Developer Exchange in the Portal Team, we discussed ways to simplify the pipeline, remove unnecessary indirection, and make E2E tests faster and more reliable.

## Decision

We will **build the backend and frontend images directly inside the pipeline** before running E2E tests.

This makes E2E tests run against a freshly compiled code (in dev mode) that reflect the current branch and bring the pipeline closer to the local development setup.

### Key Points

* The backend and frontend applications are now **compiled and run in dev mode inside the pipeline**, not as images pulled from the registry.
* E2E tests run **immediately after the push**, without waiting for upstream image builds.
* Local and CI environments are now aligned:
    * Locally, developers run backend and frontend directly.
    * In CI, we simulate these environments with minimal container setup, without extra proxy layers or special environment variable mappings.

### Consequences

#### Positive
1. **Speed**
    * E2E tests start much earlier since they no longer depend on external image builds.
    * Caching is applied to:
        * OpenSearch container image
        * Gradle
        * Yarn
          This significantly reduces repeated build times.

2. **Simplified Configuration**
    * No duplicate backend or frontend images in `docker-compose-ci.yml`.
    * No additional `.env` files injected into CI.
    * No artificial environment variable hacks to allow frontend-to-backend communication inside the GitHub runner.

3. **Better Reliability & Reproducibility**
    * The pipeline setup now mirrors how developers work locally.
    * Issues can be reproduced without needing special CI-only setups.
    * The pipeline simulates how images are built before they would be deployed.

4. **Cleaner Future Staging/UAT Testing**
    * We decouple E2E tests from staging image builds.
    * Later, we will introduce **scheduled smoke tests** that run against the real deployed versions on staging/UAT to ensure environment integrity.

#### Negative

* E2E tests no longer run on **exactly** the same images deployed to staging/UAT. To compensate, we will introduce scheduled smoke tests on staging to ensure that the deployed environment remains healthy.

## Future Work

**Introduce a scheduled smoke-test workflow**
 * Runs E2E-lite tests against staging and UAT.
 * Ensures that the deployed environment behaves correctly.
 * Helps catch environment-specific regressions early.