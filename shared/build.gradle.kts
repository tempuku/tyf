plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidLibrary)
    kotlin("plugin.serialization") version "1.9.22"
}

kotlin {
    androidTarget {
        compilations.all {
            kotlinOptions {
                jvmTarget = "1.8"
            }
        }
    }
    
    iosX64()
    iosArm64()
    iosSimulatorArm64()

    jvm{}
    
    sourceSets {
        commonMain.dependencies {
            implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.0")
            implementation("com.itextpdf:itext-core:8.0.2")
            implementation("com.tom-roush:pdfbox-android:2.0.27.0")
        }
        jvmMain.dependencies {
            implementation("org.apache.pdfbox:pdfbox:2.0.27")
            implementation("org.apache.pdfbox:pdfbox-tools:2.0.27")
        }
    }
}

android {
    namespace = "org.example.tyf.shared"
    compileSdk = libs.versions.android.compileSdk.get().toInt()
    defaultConfig {
        minSdk = libs.versions.android.minSdk.get().toInt()
    }
}
