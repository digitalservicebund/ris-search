# RIS Search

| Frontend                                                                                                                                                                                  | Backend                                                                                                                                                                                | Deployment                                                                                                                                                                              |
| ----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- | -------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- | --------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| [![Frontend](https://github.com/digitalservicebund/ris-search/actions/workflows/frontend.yml/badge.svg)](https://github.com/digitalservicebund/ris-search/actions/workflows/frontend.yml) | [![Backend](https://github.com/digitalservicebund/ris-search/actions/workflows/backend.yml/badge.svg)](https://github.com/digitalservicebund/ris-search/actions/workflows/backend.yml) | [![Deployment](https://github.com/digitalservicebund/ris-search/actions/workflows/deploy.yml/badge.svg)](https://github.com/digitalservicebund/ris-search/actions/workflows/deploy.yml) |

This is the repository for the public portal of NeuRIS. You can learn more about NeuRIS on our [website](https://digitalservice.bund.de/en/projects/new-legal-information-system).

## Quickstart

Clone this repo:

```sh
git clone git@github.com:digitalservicebund/ris-search.git
```

If you're already familiar with our stack and the project, here is a list of the most important commands for running frequent tasks. You will find [more detailed instructions below](#prerequisites).

### Running backend + frontend separately

```sh
# Run Docker containers (working dir: project root)
docker compose up -d

# Run backend (working dir: ./backend)
# Include `e2e` profile if you want test data for E2E tests
./gradlew bootRun --args='--spring.profiles.active=default,e2e'

# Install frontend dependencies and run frontend (working dir: ./frontend)
pnpm install
pnpm dev
```

You will find the following services at these addresses:

- Frontend at <http://localhost:3000>
- OpenSearch at <http://localhost:9200>
- OpenSearch Dashboards at <http://localhost:5601>
- Backend at <http://localhost:8090>
- Swagger API documentation at <http://localhost:8090/swagger-ui/index.html>

### Testing

Backend:

```sh
./gradlew test              # Unit tests
./gradlew integrationTest   # Integration tests
```

Frontend:

```sh
pnpm test         # Unit tests (once)
pnpm test:watch   # Unit tests (watch mode)
```

E2E tests (backend and frontend must be [running separately](#running-backend--frontend-separately)):

```sh
pnpm exec playwright test                     # E2E tests
pnpm exec playwright test --project chromium  # E2E tests for Chromium only
pnpm exec playwright test --ui                # Opens the Playwright UI
```

### Code style & quality

Backend:

```sh
./gradlew spotlessApply   # Format code
```

Frontend:

```sh
pnpm style:fix  # Check code conventions + formatting, attempt to fix
pnpm typecheck  # Check TypeScript validity
```

### Building

Backend:

```sh
./gradlew build
```

Frontend:

```sh
pnpm build

# Optionally, preview the build output (requires a running backend):
pnpm nuxt preview
```

## Navigating the repository

This is a mono-repository containing:

| Directory                 | Description                                                                     |
| ------------------------- | ------------------------------------------------------------------------------- |
| [`backend`](./backend/)   | The backend service (Java 21 + Spring Boot)                                     |
| [`doc`](./doc/)           | Additional documentation, including [Architecture Decision Records](./doc/adr/) |
| [`frontend`](./frontend/) | A browser-based interface for users (TypeScript + Vue + Nuxt + Tailwind)        |
| [`scripts`](./scripts/)   | Utility scripts                                                                 |

## Prerequisites

To build and run the application, you'll need:

- Docker, for services like OpenSearch
- A Java 21-compatible JDK
- Node.js and pnpm (you'll find the exact versions [here](./frontend/package.json))

If you would like to make changes to the application, you'll also need:

- [`jq`](https://jqlang.org/), for parsing license data
- [`lefthook`](https://lefthook.dev/), for running Git hooks
- (optional) [`adr-tools`](https://github.com/npryce/adr-tools), for scaffolding new ADRs
- (optional) [`nvm`](https://github.com/nvm-sh/nvm), for managing Node versions

If you use [Homebrew](https://brew.sh/), you can install all of them like this:

```sh
brew install openjdk@21 lefthook nvm pnpm adr-tools
brew install --cask docker # or `brew install docker` if you don't want the desktop app
```

Once you installed the prerequisites, make sure to initialize Git hooks. This will ensure any code you commit follows our coding standards, is properly formatted, and has a commit message adhering to our conventions:

```sh
lefthook install
```

Finally, there are some environment variables that need to be set locally. As a starting point, copy the `frontend/.env.example file, and rename it to `.env`.

## Learn more

You will find more information about each module in the respective folders. If you're getting started, the READMEs of the [backend](./backend/README.md) and [frontend](./frontend/README.md) will be the most relevant resources.

Additional guides:

- [Container image](./doc/readme/container-image.md)
- [Vulnerability scanning](./doc/readme/vulnerability-scan.md)
- [Dump case law data to OpenSearch](./doc/readme/dump-caselaw-to-opensearch.md)
- [OpenSearch index swap](./doc/readme/opensearch-index-swap.md)
- [API keys](./doc/readme/api-keys.md)

## License checking

When installing dependencies, make sure they are licensed under one of the [allowed licenses](./allowed-licenses.json). This will be checked in the pipeline for both frontend and backend dependencies. The pipeline will fail if licenses not included in the list are used by any dependency.

## Contributing

If you would like to contribute, check out [`CONTRIBUTING.md`](./CONTRIBUTING.md). Please also consider our [Code of Conduct](./CODE_OF_CONDUCT.md).
