# Commands

- Unit tests

```bash
./gradlew test
```

- Integration tests

```bash
./gradlew integrationTest --exclude-task test
```

- Unit tests and integration tests

```bash
./gradlew integrationTest
```

- Check formatting

```bash
./gradlew spotlessCheck
```

- Auto formatting

```bash
./gradlew spotlessApply
```

- Check licenses

```bash
./gradlew checkLicense
```

- Re-import local data (norms)

```bash
curl http://localhost:8090/internal/import/norms/changelog --json '{"change_all": true}
```