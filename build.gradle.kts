import java.awt.image.BufferedImage

plugins {
    kotlin("jvm") version "1.9.21"
    kotlin("plugin.serialization") version "1.9.22"
    application
}

group = "org.home"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    google()
    maven {
        url = uri("https://repo.e-iceblue.com/nexus/content/groups/public/")
    }

}

dependencies {
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.0")
    testImplementation("io.kotest:kotest-runner-junit5:5.8.0")
    implementation("com.itextpdf:itext-core:8.0.2")
    implementation("org.ghost4j:ghost4j:1.0.1")
//    implementation("org.apache.pdfbox:pdfbox:2.0.0")
//    implementation("org.apache.pdfbox:pdfbox-tools:2.0.9")
    implementation("org.slf4j:slf4j-simple:1.7.21")
    implementation("com.sealwu:kscript-tools:1.0.2")
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