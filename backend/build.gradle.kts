import com.adarshr.gradle.testlogger.theme.ThemeType
import com.github.jk1.license.filter.DependencyFilter
import com.github.jk1.license.filter.LicenseBundleNormalizer

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
    maven {
        setUrl("https://jitpack.io")
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
        property("sonar.coverage.exclusions", "**/config/**, ,**/e2e/**, **/CustomErrorController.java")
    }
}

val xjc by configurations.creating

dependencies {
    xjc(libs.jaxb.moxy.xjc)

    implementation(libs.spring.actuator)
    implementation(libs.spring.validation)
    implementation(libs.spring.web)
    implementation(libs.spring.security)
    implementation(libs.spring.oauth2)
    implementation(libs.spring.data.jpa)
    implementation(libs.jts.core)
    implementation(libs.spring.boot.starter.webservices)

    implementation(libs.spring.kubernetes.client)

    implementation(libs.spring.data.opensearch) {
        exclude(group = "org.opensearch.client", module = "opensearch-rest-client-sniffer")
    }

    implementation(libs.spring.security.oauth2.jose)

    implementation(libs.spring.data.elasticsearch)
    implementation(libs.springdoc.openapi.starter.webmvc.ui)
    implementation(libs.jsoup)
    implementation(libs.amazon.aws.sdk.s3)

    // CVE-2022-1471
    implementation(libs.snakeyaml)

    implementation(libs.micrometer.registry.prometheus)
    implementation(libs.sentry.spring.boot.starter.jakarta)
    implementation(libs.sentry.logback)
    implementation(libs.posthog)
    implementation(libs.commons.text)

    implementation(libs.lucene.queryparser)
    implementation(libs.lucene.core)
    implementation(libs.logbook.spring.boot.starter)
    implementation(libs.json)
    implementation(libs.xml.parser)
    implementation(libs.commons.csv)

    implementation(libs.jackson.jsonld)
    implementation(libs.saxon.he)
    implementation(libs.jaxb.moxy)
    implementation(libs.pebble)
    implementation(libs.streamex)

    compileOnly(libs.lombok)
    annotationProcessor(libs.lombok)

    developmentOnly(libs.spring.boot.devtools)

    testImplementation(libs.spring.boot.starter.test)
    testImplementation(libs.spring.security.test)
    testImplementation(libs.archunit.junit5)
    testImplementation(libs.mockito.junit.jupiter)
    testImplementation(libs.spring.addons.oauth2.test)

    testImplementation(libs.testcontainers.junit.jupiter)
    testImplementation(libs.opensearch.testcontainers)
    testImplementation(libs.testcontainers.postgresql)
    testImplementation(libs.pact)
    testImplementation(libs.restassured)
}

val pactPath = "pacts/**/*.json"
val generatedPath = "build/generated/**"
spotless {
    java {
        targetExclude(generatedPath)
        removeUnusedImports()
        googleJavaFormat()
        custom("Refuse wildcard imports") {
            // Wildcard imports can't be resolved by spotless itself.
            // This will require the developer themselves to adhere to best practices.
            if (it.contains("\nimport .*\\*;".toRegex())) {
                throw AssertionError("Do not use wildcard imports. 'spotlessApply' cannot resolve this issue.")
            }
            it
        }
        targetExclude(pactPath, generatedPath)
    }
    kotlin {
        ktfmt()
        targetExclude(pactPath, generatedPath)
    }
    kotlinGradle {
        ktlint()
        targetExclude(pactPath, generatedPath)
    }

    format("misc") {
        target("**/*.json", "**/*.md", "**/*.properties", "**/*.sh", "**/*.yml")
        targetExclude("frontend/**", "pacts/**/*.json", generatedPath)
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
        val containerImageName = System.getenv("CONTAINER_IMAGE_NAME") ?: "digitalservicebund/${project.name}"
        val containerImageVersion = System.getenv("CONTAINER_IMAGE_VERSION") ?: "latest"

        imageName.set("$containerRegistry/$containerImageName:$containerImageVersion")
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
                fileTree(project.layout.buildDirectory.asFile.get().absolutePath) {
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
