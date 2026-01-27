# Backend

Java 21 + Spring Boot REST API.

See the [main README](../README.md) for prerequisites.

## Running

(working dir: `backend/`)

```bash
./gradlew bootRun
```

The API will be available at [localhost:8090](http://localhost:8090).

Swagger API documentation is available at [localhost:8090/swagger-ui](http://localhost:8090/swagger-ui/index.html).

## Testing

### Unit Tests

```bash
./gradlew test
```

Includes [ArchUnit](https://www.archunit.org/getting-started) tests for ensuring architectural characteristics (e.g. no cyclic dependencies).

### Integration Tests

```bash
./gradlew integrationTest
```

**Note:** Running integration tests requires passing unit tests first (in Gradle terms: integration tests depend on unit tests).

To run integration tests exclusively, without the unit test dependency:

```bash
./gradlew integrationTest --exclude-task test
```

Integration tests are denoted using a JUnit 5 tag annotation: `@Tag("integration")`.

## Code Quality

### Formatting

Check formatting:

```bash
./gradlew spotlessCheck
```

Auto-format:

```bash
./gradlew spotlessApply
```

### License Check

```bash
./gradlew checkLicense
```

## Data Import

Re-import local norms data:

```bash
curl http://localhost:8090/internal/import/norms/changelog --json '{"change_all": true}'
```
