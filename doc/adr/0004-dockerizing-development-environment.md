# 4. Dockerized the Backend and Frontend Development Environment

Date: 2024-07-23

## Status

Accepted

## Context

Our project consists of multiple components (opensearch, keykloak, frontend internal, frontend public, backend, two postgress instances). All components are so far dockerized except of our Java-based backend and our two Vue.js/Nuxt-based frontends. Each of these components has its own set of dependencies and environment requirements. Setting up a local development environment that closely mirrors the production environment can be time-consuming and prone to errors. This complexity can lead to the "it works on my machine" problem, where code runs fine in one environment but fails in another. There's also more effort done in spinning up each server separately from where it's located on the repo.

## Decision

We have decided to containerize both the backend and frontend applications using Docker. This approach involves creating Docker images for each component and managing them through docker-compose to streamline the development setup process.

## Rationale
1. **Consistency**: Docker ensures that the development environment is consistent across all team members' machines, as well as between development, staging, and production environments. This reduces the chances of encountering environment-specific bugs.
2. **Ease of Setup**: New team members can get started with the project by running a few Docker commands, without the need to install and configure multiple dependencies manually.
3. **Isolation**: Docker containers are isolated from each other and the host system, reducing the risk of conflicting dependencies and making it easier to manage different versions of services (e.g., databases, search engines).
4. **Replicability**: Docker images can be shared and replicated easily, ensuring that all team members and CI/CD pipelines use the exact same environment setup.

## Consequences

All developers must have Docker and docker-compose installed on their machines.
The development team needs to maintain the Dockerfiles and docker-compose.yml configuration, ensuring they are updated with any changes in the project's dependencies or environment requirements.

Developers using IDEs need to configure them to work with Docker containers, which might involve additional setup steps.
