import com.adarshr.gradle.testlogger.theme.ThemeType
import com.diffplug.spotless.FormatterFunc
import com.github.jk1.license.filter.DependencyFilter
import com.github.jk1.license.filter.LicenseBundleNormalizer
import java.io.Serializable

buildscript { repositories { mavenCentral() } }

plugins {
    jacoco
    java
    `jvm-test-suite`
    `java-test-fixtures`
    checkstyle
    alias(libs.plugins.spring.boot)
    alias(libs.plugins.spring.dependency.management)
    alias(libs.plugins.spotless)
    alias(libs.plugins.sonarqube)
    alias(libs.plugins.license.report)
    alias(libs.plugins.test.logger)
    alias(libs.plugins.node.gradle)
}

group = "de.bund.digitalservice"
version = "0.0.1-SNAPSHOT"

configurations {
    compileOnly { extendsFrom(annotationProcessor.get()) }
}

repositories {
    mavenCentral()
    maven {
        url = uri("https://maven.pkg.github.com/digitalservicebund/ris-html-transformation")
        credentials {
            username = System.getenv("GH_PACKAGES_REPOSITORY_USER") ?: project.findProperty("global_gh_packages_user") as String?
            password = System.getenv("GH_PACKAGES_REPOSITORY_TOKEN") ?: project.findProperty("global_gh_packages_token") as String?
        }
    }
    maven {
        url = uri("https://maven.pkg.github.com/digitalservicebund/ris-xml-schema")
        credentials {
            username = System.getenv("GH_PACKAGES_REPOSITORY_USER") ?: project.findProperty("global_gh_packages_user") as String?
            password = System.getenv("GH_PACKAGES_REPOSITORY_TOKEN") ?: project.findProperty("global_gh_packages_token") as String?
        }
    }
}

jacoco { toolVersion = libs.versions.jacoco.get() }

testlogger {
    theme = ThemeType.MOCHA
}

sonar {
    properties {
        property("sonar.projectKey", "digitalservicebund_ris-search-backend")
        property("sonar.organization", "digitalservicebund")
        property("sonar.host.url", "https://sonarcloud.io")
        property(
            "sonar.coverage.exclusions",
            "**/config/**, **/e2e/**, **/CustomErrorController.java, **/RestClientConfigStackit.java",
        )
        // RechtsprechungController intentionally duplicates CaseLawController's and
        // CaseLawSearchController's endpoint bodies (see class Javadoc) so both
        // /v1/case-law/** and /v1/rechtsprechung/** work in parallel while the frontend
        // migrates. Remove this exclusion once /v1/case-law/** is deleted and the duplication with
        // it.
        property(
            "sonar.cpd.exclusions",
            "**/controller/api/RechtsprechungController.java",
        )
    }
}

dependencies {
    implementation(libs.spring.actuator)
    implementation(libs.spring.validation)
    implementation(libs.spring.web)
    implementation(libs.spring.security)
    implementation(libs.spring.data.jpa)
    implementation(libs.jts.core)
    implementation(libs.spring.boot.starter.webservices)
    implementation(libs.spring.kubernetes.client)
    implementation(libs.spring.data.opensearch)
    implementation(libs.springdoc.openapi.starter.webmvc.ui)
    implementation(libs.jsoup)
    implementation(libs.amazon.aws.sdk.s3)

    implementation(libs.micrometer.registry.prometheus)
    implementation(libs.sentry.spring.boot.starter.jakarta)
    implementation(libs.sentry.logback)
    implementation(libs.posthog)
    implementation(libs.commons.text)

    implementation(libs.commons.csv)

    implementation(libs.saxon.he)
    implementation(libs.jaxb.moxy)
    implementation(libs.pebble)
    implementation(libs.streamex)

    // CVE-2026-5588
    implementation(platform(libs.bouncycastle.bom))

    // CVE-2026-65182
    implementation(libs.tomcat.embed.core)

    implementation(libs.ris.html.transformation)

    compileOnly(libs.lombok)
    annotationProcessor(libs.lombok)

    developmentOnly(libs.spring.boot.devtools)

    // Shared test fixtures (src/testFixtures) - XML/schema validators and norm XML builders
    // used by both the unit and integration suites.
    testFixturesCompileOnly(libs.lombok)
    testFixturesAnnotationProcessor(libs.lombok)
    testFixturesImplementation(sourceSets["main"].output)
    testFixturesImplementation(libs.jaxb.moxy)
    testFixturesImplementation(libs.ris.xml.schema)
    testFixturesImplementation(libs.commons.io)
}

dependencyLocking {
    lockAllConfigurations()
}

testing {
    suites {
        withType(JvmTestSuite::class).matching { it.name in listOf("test", "integrationTest") }.configureEach {
            useJUnitJupiter()
            dependencies {
                implementation(sourceSets["main"].output)
                implementation(testFixtures(project()))
                implementation(libs.spring.boot.starter.test)
                implementation(libs.spring.security.test)
                implementation(libs.spring.boot.starter.webmvc.test)
                implementation(libs.mockito.junit.jupiter)
            }
        }

        val test =
            named<JvmTestSuite>("test") {
                dependencies {
                    implementation(libs.archunit.junit5)
                }
            }

        register<JvmTestSuite>("integrationTest") {
            dependencies {
                implementation(libs.testcontainers.junit.jupiter)
                implementation(libs.opensearch.testcontainers)
                implementation(libs.testcontainers.postgresql)
                implementation(libs.restassured)
                implementation(libs.apicatalog.titanium.json)
                implementation(libs.glassfish.jakarta.json)
            }
            targets {
                all {
                    testTask.configure {
                        shouldRunAfter(test)
                        mustRunAfter(tasks.check)
                        finalizedBy("jacocoTestReport")
                    }
                }
            }
        }

        register<JvmTestSuite>("dataTest") {
            useJUnitJupiter()
            dependencies {
                implementation(sourceSets["main"].output)
                implementation(libs.spring.boot.starter.test)
                implementation(libs.restassured)
            }
            targets {
                all {
                    testTask.configure {
                        shouldRunAfter(test)
                        mustRunAfter(tasks.check)
                    }
                }
            }
        }
    }
}

spotless {
    java {
        removeUnusedImports()
        googleJavaFormat()
        // Wildcard imports can't be resolved by spotless itself.
        // This will require the developer themselves to adhere to best practices.
        custom(
            "Refuse wildcard imports",
            object : Serializable, FormatterFunc {
                override fun apply(input: String): String {
                    if (input.contains("*;\n")) {
                        throw GradleException("No wildcard imports allowed.")
                    }
                    return input
                }
            },
        )
    }
    kotlin {
        ktfmt()
    }
    kotlinGradle {
        ktlint("1.4.1")
    }
}

licenseReport {
// If there's a new dependency with a yet unknown license causing this task to fail
// the license(s) will be listed in build/reports/dependency-license/dependencies-without-allowed-license.json
    allowedLicensesFile = File("$projectDir/../allowed-licenses.json")
    filters = arrayOf<DependencyFilter>(LicenseBundleNormalizer())
}

project.tasks.sonar {
    dependsOn("jacocoTestReport")
}

tasks {
    compileJava {
        options.release.set(25)
        options.compilerArgs.addAll(arrayOf())
    }

    jar {
        enabled = false
    }

    jacocoTestReport {
        // Jacoco hooks into all tasks of type: Test automatically, but results for each of these
        // tasks are kept separately and are not combined out of the box.. we want to gather
        // coverage of our unit and integration tests as a single report!
        executionData.setFrom(
            files(
                fileTree(
                    project.layout.buildDirectory.asFile
                        .get()
                        .absolutePath,
                ) {
                    include("jacoco/*.exec")
                },
            ),
        )
        reports {
            xml.required.set(true)
            html.required.set(true)
        }

        dependsOn(getByName("test"))
        dependsOn(getByName("integrationTest"))
    }

    withType<Javadoc> {
        options {
            this as StandardJavadocDocletOptions
            addBooleanOption("Xdoclint:none", true)
            addStringOption("Xmaxwarns", "1")
        }
        include("**/*.java")
    }
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(25)
    }
    sourceSets["main"].java {
        srcDirs("build/generated/nlex")
    }
}

// `jar` is disabled above (this is a Spring Boot app using bootJar instead), which breaks the
// self-project dependency that `java-test-fixtures`/`jvm-test-suite` would otherwise set up
// automatically to give each suite main's own *third-party* dependencies (AWS SDK, Jackson,
// OpenSearch client, etc., which integration tests exercise directly; main's own classes are
// already handled above via `implementation(sourceSets["main"].output)`). Replicate, for every
// suite, what the `java` plugin already wires up for the built-in "test" configuration by
// convention:
configurations.named("testImplementation") { extendsFrom(configurations["implementation"]) }
configurations.named("testRuntimeOnly") { extendsFrom(configurations["runtimeOnly"]) }
configurations.named("testFixturesImplementation") { extendsFrom(configurations["implementation"]) }
configurations.named("testFixturesRuntimeOnly") { extendsFrom(configurations["runtimeOnly"]) }
configurations.named("integrationTestImplementation") { extendsFrom(configurations["implementation"]) }
configurations.named("integrationTestRuntimeOnly") { extendsFrom(configurations["runtimeOnly"]) }
configurations.named("dataTestImplementation") { extendsFrom(configurations["implementation"]) }
configurations.named("dataTestRuntimeOnly") { extendsFrom(configurations["runtimeOnly"]) }
