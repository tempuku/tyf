plugins {
    kotlin("jvm") version "1.9.21"
    kotlin("plugin.serialization") version "1.9.22"
    application
}

group = "org.home"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.0")
    testImplementation("org.jetbrains.kotlin:kotlin-test:1.8.10")
    implementation("com.itextpdf:itext-core:8.0.2")
    implementation("org.slf4j:slf4j-simple:1.7.21")
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(8)
}

application {
    mainClass.set("MainKt")
}