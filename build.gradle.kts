import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.6.21"
    id("maven-publish")
    id("io.gitlab.arturbosch.detekt") version "1.19.0"
}

group = "com.augenda.commons"
version = "1.0.1"
java.sourceCompatibility = JavaVersion.VERSION_1_8

repositories {
    mavenCentral()
}

dependencies {
    compileOnly("com.fasterxml.jackson.core:jackson-annotations:2.13.3")
    compileOnly("com.fasterxml.jackson.core:jackson-databind:2.13.3")

    compileOnly("org.apache.kafka:kafka-clients:3.1.0")

    compileOnly("org.slf4j:slf4j-api:1.7.36")
    compileOnly("org.slf4j:slf4j-simple:1.7.36")

    compileOnly("io.azam.ulidj:ulidj:1.0.1")

}

tasks.jar {
    enabled = true
}

java {
    withSourcesJar()
}

tasks.test {
    useJUnitPlatform()
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict")
        jvmTarget = "1.8"
    }
}

detekt {
    source = files("src/main/java", "src/main/kotlin")
    config = files("detekt/detekt.yml")
}

publishing {
    repositories {
        maven {
            name = "GitHubPackages"
            url = uri("https://maven.pkg.github.com/kaiqkt/kafka-commons")
            credentials {
                username = project.findProperty("gpr.user") as String?
                password = project.findProperty("gpr.key") as String?
            }
        }
    }
    publications {
        publications {
            create<MavenPublication>("maven") {
                from(components["java"])
            }
        }
    }
}