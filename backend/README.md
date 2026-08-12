# Backend

Java 21 + Spring Boot REST API.

## Building

The backend uses gradle, but before building you need some setup due to a dependency on a github module.
The module is public, but github requires authentication to download.
It can be any authenticated github user and doesn't have to be affiliated with this repository.
It's just github policy that modules can't be downloaded without an account.

#### Make a new public read only PAT (don't use a PAT you might already have with more privilege)
* Go to [this github page](https://github.com/settings/personal-access-tokens)
* Click Generate new token
* Give it a name and set expiration to 90 days (if you pick more than 90 it will be rejected later)
* Use Public repositories (should already be selected)
* Click Generate token
* Don't close the tab, you will need this value later
#### Setup 2 global gradle properties
* In shell run `touch ~/.gradle/gradle.properties`
* Append `global_gh_packages_user=YOUR_GITHUB_USERNAME` to the file
* Append `global_gh_packages_token=THE_PAT_FROM_EARLIER` to the file

Now when you build the project the github module should be correctly downloaded.

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

## Dependencies

Make sure to regenerate the `gradle.lockfile` file when changing dependencies with:
```bash
./gradlew dependencies --write-locks
```

## Code Quality

Check formatting:

```bash
./gradlew spotlessCheck
```

Auto-format:

```bash
./gradlew spotlessApply
```
