# 5. Migrated Dependencies to a TOML File

Date: 2024-08-07

## Status

Accepted

## Context

Managing dependencies directly within the `build.gradle.kts` file can become cumbersome as the project grows. It can lead to duplication, inconsistencies, and difficulties in maintaining and updating dependencies. A centralized approach to managing dependency versions can improve maintainability and readability.

## Decision

We have decided to migrate the dependency versions to a `libs.versions.toml` file. This approach involves defining all dependency versions in a single TOML file and referencing them in the `build.gradle.kts` file.

## Rationale
1. **Centralized Management**: Having all dependency versions in one file makes it easier to manage and update dependencies.
2. **Consistency**: Ensures that the same version of a dependency is used throughout the project, reducing the risk of version conflicts.
3. **Readability**: Improves the readability of the `build.gradle.kts` file by separating version information from dependency declarations.
4. **Maintainability**: Simplifies the process of updating dependencies, as changes need to be made in only one place.

## Consequences

All developers must be familiar with the new `libs.versions.toml` file and how to reference dependencies from it.
The development team needs to ensure that the TOML file is kept up-to-date with any changes in the project's dependencies.
This approach also requires initial setup and configuration to migrate existing dependencies to the TOML file.
