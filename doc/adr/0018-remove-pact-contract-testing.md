# 18. Removing Pact Contract Testing in Favor of Integration & E2E Tests

**Date:** 2025-12-05
**Status:** Accepted
**Supersedes:** ADR-007 “Using Pact Library for Contract Testing”

---

## **Context**

In September 2024, we introduced Pact contract testing (see ADR-007) to ensure compatibility between the backend API and the frontend client. Pact enabled the frontend to act as a consumer generating contract specifications, while the backend verified the expectations as a provider.

Over time, some issues were reported by the development team regarding Pact Testing:

* Maintaining Pact tests in both frontend and backend created friction and slowed down development.
* There was no time dedicated to expand on the initial test that was made for simple search and remained until this date 1 test.
* Some developers saw Pact as a testing strategy that introduced complexity into our workflow (e.g., git hooks, file generation, Pact verification tests) without proportional benefit.
* For end-to-end validation of real frontend/API interactions, our E2E Playwright tests is decided to become the primary, reliable source of truth for frontend-backend interaction.

After a team discussion in the Dev Lean Coffee session, we concluded that Pact no longer provides value relative to the maintenance cost.

---

## **Decision Drivers**

* **Reduced Complexity:** Remove the operational overhead of generating, storing, and verifying Pact files.
* **Stronger Guarantees:** Integration tests cover backend behavior holistically. E2E tests are planned to validate the real interaction between frontend and backend without the need for strict type checking.
* **Developer Velocity:** Simplify testing pipelines, decrease friction in modifying API endpoints, and reduce brittle tests.

---

## **Decision**

We will **remove Pact** and all associated contract testing workflows from both the frontend and backend.

Instead:

### **1. Backend**

* Rely on **integration tests** (MockMvc, full controller tests, database-backed when needed) for API correctness.
* No longer consume Pact files.

### **2. Frontend**

* No Pact consumer tests.
* Rely on:
  * **Mocked frontend unit tests**
  * **End-to-end tests** to validate the impact of the communication with the API

### **3. Cross-Service Interaction**

* Compatibility between frontend and backend will be validated through **E2E tests only**, not via contract files.

### **4. Tooling**

* Remove lefthook Pact generator.
* Remove Pact-related packages, scripts, and configuration from both repositories.
* Remove Pact verification tests from backend.

---

## **Consequences**

### **Positive**

* Reduced cognitive and operational load on the team.
* Simpler CI pipeline (no Pact generation, no Pact verification tasks).
* More flexible refactoring of API endpoints without updating Pact specs.

### **Negative / Trade-offs**

* Loss of early-stage frontend-driven contract enforcement.
* Type-level mismatches between frontend and backend would be picked through e2e tests if they produced visible bugs.

---

## **Alternatives Considered**

1. Keep Pact but automate it more — rejected due to maintenance time > value.
2. Keep Pact only for critical endpoints — rejected because partial usage still introduces complexity and inconsistency.

---

## **Outcome**

ADR-007 is now deprecated.
Pact is fully removed from the codebase and workflow.

API correctness is guaranteed through backend integration tests,
and frontend/backend interaction correctness through Playwright E2E tests.