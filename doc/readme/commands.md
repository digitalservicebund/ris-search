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
