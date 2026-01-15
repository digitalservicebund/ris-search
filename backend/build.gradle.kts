import com.adarshr.gradle.testlogger.theme.ThemeType
import com.diffplug.spotless.FormatterFunc
import com.github.jk1.license.filter.DependencyFilter
import com.github.jk1.license.filter.LicenseBundleNormalizer
import java.io.Serializable

buildscript { repositories { mavenCentral() } }

plugins {
    jacoco
    java
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

java.sourceCompatibility = JavaVersion.VERSION_21
java.targetCompatibility = JavaVersion.VERSION_21

configurations {
    compileOnly { extendsFrom(annotationProcessor.get()) }
}

repositories {
    mavenCentral()
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
        property("sonar.coverage.exclusions", "**/config/**, **/e2e/**, **/CustomErrorController.java, **/RestClientConfigStackit.java")
    }
}

val xjc by configurations.creating

dependencies {
    xjc(libs.jaxb.moxy.xjc)

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

    // CVE-2022-1471
    implementation(libs.snakeyaml)

    // CVE-2024-29371 upgrade kubernetes-client to 5.0.0 after boot 4.0.0 upgrade
    implementation(libs.jose4j)

    implementation(libs.micrometer.registry.prometheus)
    implementation(libs.sentry.spring.boot.starter.jakarta)
    implementation(libs.sentry.logback)
    implementation(libs.posthog)
    implementation(libs.commons.text)

    implementation(libs.json)
    implementation(libs.commons.csv)

    implementation(libs.saxon.he)
    implementation(libs.jaxb.moxy)
    implementation(libs.pebble)
    implementation(libs.streamex)

    // CVE-2024-7254
    implementation(libs.protobuf)

    compileOnly(libs.lombok)
    annotationProcessor(libs.lombok)

    developmentOnly(libs.spring.boot.devtools)

    testImplementation(libs.spring.boot.starter.test)
    testImplementation(libs.spring.security.test)
    testImplementation(libs.archunit.junit5)
    testImplementation(libs.mockito.junit.jupiter)
    testImplementation(libs.spring.boot.starter.webmvc.test)

    testImplementation(libs.testcontainers.junit.jupiter)
    testImplementation(libs.opensearch.testcontainers)
    testImplementation(libs.testcontainers.postgresql)
    testImplementation(libs.restassured)
}

val generatedPath = "build/generated/**"
spotless {
    java {
        targetExclude(generatedPath)
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

tasks.named<Checkstyle>("checkstyleMain") {
    source("src")
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
    register("generate-nlex-wsdl", JavaExec::class) {
        doFirst {
            mkdir("$buildDir/generated/nlex")
        }
        enabled = true
        classpath(configurations["xjc"])
        mainClass = "org.eclipse.persistence.jaxb.xjc.MOXyXJC"
        args = listOf("src/main/resources/WEB_INF/nlex/simple_template.wsdl", "-wsdl", "-d", "$buildDir/generated/nlex", "-p", "nlex")
    }

    compileJava {
        dependsOn("generate-nlex-wsdl")
        options.compilerArgs.addAll(arrayOf())
    }

    jar {
        enabled = false
    }

    bootBuildImage {
        val containerRegistry = System.getenv("CONTAINER_REGISTRY") ?: "ghcr.io"
        val containerImageTag = System.getenv("CONTAINER_IMAGE_TAG")

        imageName.set(containerImageTag)
        builder.set("paketobuildpacks/builder-jammy-tiny")
        publish.set(false)
        docker {
            publishRegistry {
                username.set(System.getenv("CONTAINER_REGISTRY_USER") ?: "")
                password.set(System.getenv("CONTAINER_REGISTRY_PASSWORD") ?: "")
                url.set("https://$containerRegistry")
            }
        }
        environment.set(mapOf("BP_HEALTH_CHECKER_ENABLED" to "true"))
        buildpacks.set(
            listOf(
                "urn:cnb:builder:paketo-buildpacks/java",
                "docker.io/paketobuildpacks/health-checker:latest",
            ),
        )
    }

    test {
        useJUnitPlatform {
            excludeTags("integration", "data")
        }
    }

    register<Test>("dataTest") {
        description = "Runs the data tests."
        group = "verification"
        useJUnitPlatform {
            includeTags("data")
        }
        mustRunAfter(check)
    }

    register<Test>("integrationTest") {
        description = "Runs the integration tests."
        group = "verification"
        useJUnitPlatform {
            includeTags("integration")
        }
        mustRunAfter(check)
        finalizedBy("jacocoTestReport")
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

java.sourceSets["main"].java {
    srcDirs("build/generated/nlex")
}
