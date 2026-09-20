/*
 * Nextcloud - Android Client
 *
 * SPDX-FileCopyrightText: 2026 Nextcloud GmbH and Nextcloud contributors
 * SPDX-License-Identifier: AGPL-3.0-or-later
 */

// Wraps org.fairscan.app:imageprocessing (a plain JVM Kotlin module, no Android/UI code) from the
// FairScan submodule as a Nextcloud-owned Gradle module. FairScan's own build.gradle.kts for this
// module can't be reused directly: it resolves dependencies through FairScan's own version catalog,
// which isn't visible here - the default `libs` accessor always resolves against this repo's
// gradle/libs.versions.toml, regardless of a module's source location.
val fairscanImageProcessingDir = "${rootDir}/submodules/fairscan/imageprocessing"

plugins {
    id("java-library")
    alias(libs.plugins.kotlin.jvm)
}

java {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
}

kotlin {
    compilerOptions {
        jvmTarget = org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_11
    }
}

sourceSets {
    main {
        java.srcDir("$fairscanImageProcessingDir/src/main/java")
    }
    test {
        java.srcDir("$fairscanImageProcessingDir/src/test/java")
    }
}

dependencies {
    implementation(libs.opencv.java)

    testImplementation(kotlin("test"))
    testImplementation(libs.assertj)
}
