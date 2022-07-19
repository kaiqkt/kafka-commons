import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.7.10"
    id("maven-publish")
    id("io.gitlab.arturbosch.detekt") version "1.19.0"
}

group = "com.augenda"
version = "1.0.0"

repositories {
    mavenCentral()
}

dependencies {
    implementation("com.fasterxml.jackson.core:jackson-annotations:2.13.3")
    implementation("com.fasterxml.jackson.core:jackson-databind:2.13.3")

    implementation("org.apache.kafka:kafka-clients:3.1.0")

    implementation("org.slf4j:slf4j-api:1.7.36")
    implementation("org.slf4j:slf4j-simple:1.7.36")

    implementation ("io.azam.ulidj:ulidj:1.0.1")

    testImplementation(kotlin("test"))
}

tasks.jar {
    enabled = true
}

tasks.test {
    useJUnitPlatform()
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}

detekt {
    source = files("src/main/java", "src/main/kotlin")
    config = files("detekt/detekt.yml")
}

publishing {
    repositories {
        maven {
            name = "GitHubPackages"
            url = uri("https://maven.pkg.github.com/OWNER/REPOSITORY")
            credentials {
                username = project.findProperty("gpr.user") as String?
                password = project.findProperty("gpr.key") as String?
            }
        }
    }
    publications { publications {
        create<MavenPublication>("maven") {
            groupId = "com.augenda"
            artifactId = "kafka-commons"
            version = "1.0.0"

            from(components["java"])
        }
    }
    }
}