# RIS Search

[![Frontend](https://github.com/digitalservicebund/ris-search/actions/workflows/frontend.yml/badge.svg)](https://github.com/digitalservicebund/ris-search/actions/workflows/frontend.yml)
[![Backend](https://github.com/digitalservicebund/ris-search/actions/workflows/backend.yml/badge.svg)](https://github.com/digitalservicebund/ris-search/actions/workflows/backend.yml)
[![End-to-end tests](https://github.com/digitalservicebund/ris-search/actions/workflows/pipeline-e2e.yml/badge.svg)](https://github.com/digitalservicebund/ris-search/actions/workflows/pipeline-e2e.yml)
[![Deployment](https://github.com/digitalservicebund/ris-search/actions/workflows/deploy.yml/badge.svg)](https://github.com/digitalservicebund/ris-search/actions/workflows/deploy.yml)

These instructions are written assuming development takes place on macOS.

## Prerequisites

### Java
This project uses Java 21 for the backend. Check your java version with
```bash
java --version
```
If java is not installed, install it
```bash
brew install openjdk@21
```
and check the version again. If the version returned is not 21, you may need to change your version
```bash
sudo ln -sfn /opt/homebrew/opt/openjdk@21/libexec/openjdk.jdk /Library/Java/JavaVirtualMachines/openjdk-21.jdk
echo 'export PATH="/opt/homebrew/opt/openjdk@21/bin:$PATH"' >> ~/.zshrc
```
Close your shell, open a new one and check the version again.

If you get an error message, try running
```bash
brew info java
```
and follow the steps provided.

### Docker
This project uses Docker to run some containers locally. These include
* OpenSearch
* OpenSearch Dashboards
* Keycloak
* Application containers (backend and frontend)

Check Docker is installed by running
```bash
docker --version
```

If docker is not installed, run this to install it.

```bash
brew install --cask docker
```

Make sure to open docker once to complete the installation and start the docker daemon.

### Node

For the provided Git hooks you will need:

```bash
brew install lefthook node
```

## Getting started

**Clone Repository:**

```bash
git clone git@github.com:digitalservicebund/ris-search.git
cd ris-search
```

**Run initialization script:**

```bash
./run.sh init
```

This will replace placeholders in the application template and install a couple of Git hooks.

**Starting Docker**

Run
```bash
docker-compose up
```
to start docker.

You will find the following services served at the following addresses:
- OpenSearch running at [localhost:9200](http://localhost:9200/)
- OpenSearch Dashboards at [localhost:5601](http://localhost:5601/) by default.
- Backend running at [localhost:8090](http://localhost:8090/)
- Frontend running at [localhost:3001](http://localhost:3001/)
- Swagger API documentation for Backend endpoints
  at [localhost:8090/swagger-ui](http://localhost:8090/swagger-ui/index.html)
  or [localhost:3000/api/docs/index.html](http://localhost:3000/api/docs/index.html).


## Backend

### Build and run

If you don't want to use Docker for running the backend, you can build and run the application backend with:

```bash
cd backend
./gradlew bootRun
```

The API application will be available at [localhost:8090](http://localhost:8090).

### Test

The project has distinct unit and integration test sets.

**To run just the unit tests:**

```bash
./gradlew test
```

**To run the integration tests:**

```bash
./gradlew integrationTest
```

**Note:** Running integration tests requires passing unit tests (in Gradle terms: integration tests depend on unit
tests), so unit tests are going to be run first. In case there are failing unit tests we won't attempt to continue
running any integration tests.

**To run integration tests exclusively, without the unit test dependency:**

```bash
./gradlew integrationTest --exclude-task test
```

Denoting an integration test is accomplished by using a JUnit 5 tag annotation: `@Tag("integration")`.

Furthermore, there is another type of test worth mentioning. We're
using [ArchUnit](https://www.archunit.org/getting-started)
for ensuring certain architectural characteristics, for instance making sure that there are no cyclic dependencies.


## Frontend

See [./frontend/README.md](./frontend/README.md) for instructions to run the frontend.

## End-to-end tests

The end-to-end tests use Playwright and the test cases are located in the `./frontend/e2e` directory.

### Setup

1. Install the required browser dependencies:

   ```bash
   npx playwright install --with-deps chromium firefox webkit
   ```
2. Run the OpenSearch container (required for indexed data):

   ```bash
   docker compose -f docker-compose.yml up -d opensearch
   ```
3. Run the backend from the `backend` folder:

   ```bash
   ./gradlew bootRun
   ```

   **Note:** First export the following environment variables:

  * `SPRING_PROFILES_ACTIVE=e2e,default`
  * `OPENSEARCH_HOST=localhost`
  * `THC_PORT=8090`
4. Run the frontend from the `frontend` folder:

   ```bash
   pnpm dev
   ```

   **Note:** Copy the variables from `.env.example` into a `.env` file and configure them correctly.

### Running Playwright Tests

Once setup is complete, you may run the end-to-end tests:

```bash
pnpm run e2e
```

or to open the UI:

```bash
pnpm run e2e -- --ui
```

Alternatively, you can run directly with Playwright:

```bash
npx playwright test
```

If using the VS Code Playwright extension, select the “setup” project. Otherwise, the authentication flow may not be executed before the tests run.

---

If you like, I can check the rest of the README.md file for consistency and format it all according to your project style.

## Content

- [Commands](./doc/readme/commands.md)
- [Container image](./doc/readme/container-image.md)
- [Vulnerability Scanning](./doc/readme/vulnerability-scan.md)
- [Dump caselaw data to OpenSearch](./doc/readme/dump-caselaw-to-opensearch.md)

## Architecture Decision Records

[Architecture decisions](https://cognitect.com/blog/2011/11/15/documenting-architecture-decisions)
are kept in the `doc/adr` directory. For adding new records install the [adr-tools](https://github.com/npryce/adr-tools) package:

```bash
brew install adr-tools
```

See https://github.com/npryce/adr-tools regarding usage.

## About the Repository

**Program Name:** NeuRIS (Neues Rechtsinformationsportal)
**Description:** An intuitive legal information system aimed at simplifying access to laws, regulations, and court decisions in Germany.

**Copyright (C) 2025 DigitalService GmbH des Bundes**

**Author's Contact Information**

DigitalService GmbH des Bundes
Prinzessinnenstraße 8-14,
10969 Berlin, Germany
Email: hallo@digitalservice.bund.de
Website: [https://digitalservice.bund.de](https://digitalservice.bund.de)

## Contributing

🇬🇧
Currently the repository has data that can not be publicly released and is private. We plan to remove the
repository history and make it public. Until then, everyone inside DigitalService is welcome to contribute to
the development of the _ris-search_. You can contribute by opening pull request, providing documentation or
answering questions or giving feedback. Please always follow the guidelines and our
[Code of Conduct](CODE_OF_CONDUCT.md).

🇩🇪
Derzeit enthält das Repository Daten, die nicht öffentlich zugänglich gemacht werden können, und ist daher
privat. Wir planen, die Repository-Historie zu entfernen und es öffentlich zugänglich zu machen. Bis dahin
sind alle Mitarbeitenden von DigitalService herzlich eingeladen, zur Entwicklung der ris-search beizutragen.
Du kannst beitragen, indem du Pull Requests erstellst, Dokumentation bereitstellst oder Fragen beantwortest
bzw. Feedback gibst.
Bitte befolge immer die Richtlinien und unseren [Verhaltenskodex](CODE_OF_CONDUCT_DE.md).

### Contributing code

🇬🇧
Open a pull request with your changes and it will be reviewed by someone from the team. When you submit a pull request,
you declare that you have the right to license your contribution to the DigitalService and the community.
By submitting the patch, you agree that your contributions are licensed under the MIT license.

Please make sure that your changes have been tested before submitting a pull request.

🇩🇪
Nach dem Erstellen eines Pull Requests wird dieser von einer Person aus dem Team überprüft. Wenn du einen Pull Request
einreichst, erklärst du dich damit einverstanden, deinen Beitrag an den DigitalService und die Community zu
lizenzieren. Durch das Einreichen des Patches erklärst du dich damit einverstanden, dass deine Beiträge unter der
MIT-Lizenz lizenziert sind.

Bitte stelle sicher, dass deine Änderungen getestet wurden, bevor du einen Pull Request sendest.
