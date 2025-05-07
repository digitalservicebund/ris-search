# 13. Replace Talisman with GitHub Secrets Check

Date: 2025-05-07

## Status

Decided

## Context

The repository previously used **Talisman** as a tool to check for secrets before pushing code. While Talisman served its purpose, it has been a source of frustration for the team due to the following reasons:

- Developers needed to adjust patterns in a configuration file and push it to the repository to ignore certain files or patterns.
- Talisman was attached to **lefthook**, adding complexity to the development workflow.
- The overall experience was not seamless for developers.

Now that the repository is public, we can leverage **GitHub's built-in secrets push prevention** feature, which is free and provides an out-of-the-box solution to prevent secrets from being pushed to the repository.

## Problem Statement

We need a mechanism to:

- Prevent secrets from being pushed to the repository.
- Simplify the developer experience by reducing the need for additional tools or configuration files.
- Ensure that exceptions or overrides are handled securely.

## Decision

We will replace **Talisman** with **GitHub Secrets Push Prevention**.

### Key Changes

1. **Enable GitHub Secrets Push Prevention**:
   - This feature is enabled in the repository settings and prevents secrets from being pushed to the repository.

2. **Define Bypass Patterns**:
   - Specific patterns to bypass the secrets check can be defined directly in the GitHub repository settings, eliminating the need for additional configuration files in the repository.

3. **Override Permissions**:
   - To handle cases where pushing without restrictions is necessary, we have:
     - Made all members of the **Portal Team** maintainers.
     - Restricted pushing without checks to **admin accounts only**.

### Advantages

- **Improved Developer Experience**:
  - No need to maintain additional configuration files or update lefthook.
  - Seamless integration with GitHub's native features.

- **Enhanced Security**:
  - Admin-only overrides provide stricter control over sensitive operations.

### Downsides

- A team or role must be defined to handle overrides or unrestricted pushes. This requires careful management to ensure security.

## Consequences

- Simplifies the workflow for developers.
- Reduces the operational overhead of maintaining Talisman and its configurations.
- Provides a more secure and streamlined approach to preventing secrets from being pushed to the repository.