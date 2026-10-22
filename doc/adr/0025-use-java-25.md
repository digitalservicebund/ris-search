# 25. Use Java 25

Date: 2026-08-13

## Status

Accepted

## Context

JDK 25, released in September 2025, is the next Long-Term Support (LTS) version after JDK 21, with support
expected until 2033 at least. As with previous LTS upgrades, staying on a current LTS release keeps us eligible
for ongoing security patches and lets us pick up accumulated language, API, and JVM enhancements.

Gradle recommends strictly cross-compiling against a target release using the `options.release` compiler flag
rather than only setting the toolchain language version, to avoid accidentally depending on APIs not present in
the target release (see [Gradle toolchains: the release flag](https://docs.gradle.org/current/userguide/toolchains.html#sec:release-flag-toolchain)).

## Decision

We will use JDK 25 for our backend module, setting both the Gradle toolchain and `compileJava`'s `options.release`
to 25.

## Consequences

- **Feature Enhancements:** JDK 25 brings further language, API, and JVM improvements accumulated since JDK 21.
- **Performance Improvements:** As an LTS release, JDK 25 includes JVM optimizations that can improve runtime
  performance and resource utilization.
- **Security Updates:** Staying on a current LTS version ensures continued security patches.
- **Required Updates:** Dependencies such as JaCoCo needed to be upgraded to a version compatible with JDK 25.
