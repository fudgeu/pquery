plugins {
    id("maven-publish")
    kotlin("jvm") version "2.0.0"
}

group = "dev.fudgeu"
version = "0.1.1b-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))
    implementation("com.google.code.gson:gson:2.11.0")
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(17)
}

publishing {
    publications {
        create<MavenPublication>("maven") { from(components["java"]) }
    }
}
