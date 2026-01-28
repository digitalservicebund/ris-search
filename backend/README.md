# Backend

Java 21 + Spring Boot REST API.

## Running

Make sure your system meets the prerequisites. Then, run the backend by running:

```bash
./gradlew bootRun
```

The API will be available at [localhost:8090](http://localhost:8090).

Swagger API documentation is available at [localhost:8090/swagger-ui](http://localhost:8090/swagger-ui/index.html).

## Testing

The project has distinct unit and integration test sets.

**To run just the unit tests:**

```bash
./gradlew test
```

**To run the integration tests:**

```bash
./gradlew integrationTest
```

**Note:** Running integration tests requires passing unit tests (in Gradle terms: integration tests
depend on unit tests), so unit tests are going to be run first. In case there are failing unit tests we won't attempt to continue running any integration tests.

**To run integration tests exclusively, without the unit test dependency:**

```bash
./gradlew integrationTest --exclude-task test
```

Denoting an integration test is accomplished by using a JUnit 5 tag annotation: `@Tag("integration")`.

Furthermore, there is another type of test worth mentioning. We're using [ArchUnit](https://www.archunit.org/getting-started) for ensuring certain architectural characteristics, for instance making sure that there are no cyclic dependencies.

## Code Quality

Check formatting:

```bash
./gradlew spotlessCheck
```

Auto-format:

```bash
./gradlew spotlessApply
```

## Data Import

Re-import local norms data:

```bash
curl http://localhost:8090/internal/import/norms/changelog --json '{"change_all": true}'
```
