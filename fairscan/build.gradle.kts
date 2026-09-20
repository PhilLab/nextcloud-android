/*
 * Nextcloud - Android Client
 *
 * SPDX-FileCopyrightText: 2026 Nextcloud GmbH and Nextcloud contributors
 * SPDX-License-Identifier: AGPL-3.0-or-later
 */

// Wraps FairScan's own :app module (github.com/pynicolas/FairScan, GPL-3.0-or-later) as a
// Nextcloud-owned Android library, since FairScan applies the com.android.application plugin
// itself and an APK can only have one application identity. FairScan's own app/build.gradle.kts
// can't be reused directly for the same reason as :fairscan-imageprocessing's (its own version
// catalog isn't visible here), plus it applies the wrong plugin for a library module.

plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.aboutlibraries.android)
}

val fairscanAppDir = "${rootDir}/submodules/fairscan/app"

// FairScan's own FairScanApp (its Application subclass) is `final`, but Nextcloud's own MainApp
// needs to extend it - see MainApp for why. Rather than mutate the submodule's checked-out files
// (which would leave it permanently "dirty" and fight every submodule update), a curated copy of
// the source tree is assembled here: everything from the submodule except FairScanApp.kt, plus
// src/main/java-overrides/.../FairScanApp.kt - a Nextcloud-maintained copy identical to upstream's
// except for the `open` modifier and lazy appContainer. Diff that file against upstream after
// bumping the submodule pin.
val fairscanSrcDir = layout.buildDirectory.dir("generated/fairscanSrc/java")

val syncFairscanSrc = tasks.register<Sync>("syncFairscanSrc") {
    into(fairscanSrcDir)
    from("$fairscanAppDir/src/main/java") {
        exclude("org/fairscan/app/FairScanApp.kt")
    }
    from("src/main/java-overrides")
}

android {
    namespace = "org.fairscan.app"
    compileSdk = 37

    defaultConfig {
        minSdk = 26
        // FairScan's own code reads BuildConfig.VERSION_NAME (e.g. as a PDF document-info
        // "creator" field) - library modules don't get one from AGP the way applications do,
        // so it's supplied here manually. Keep this in sync with the submodule's pinned tag.
        buildConfigField("String", "VERSION_NAME", "\"2.2.0\"")
    }

    sourceSets["main"].apply {
        java.srcDir(fairscanSrcDir)
        res.srcDir("$fairscanAppDir/src/main/res")
        assets.srcDir("$fairscanAppDir/src/main/assets")
        assets.srcDir(layout.buildDirectory.dir("generated/assets"))
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlin {
        compilerOptions {
            jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_11)
        }
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
}

// Downloads the document-segmentation TFLite model FairScan's own build fetches at build time
// (see submodules/fairscan/app/download-tflite.gradle.kts) - not vendored into the submodule
// itself, so it's reproduced here rather than referenced, to avoid depending on FairScan's own
// build script (which uses its own version catalog).
val tfliteModelVersion = "v1.2.0"
val tfliteModelFileName = "fairscan-segmentation-model.tflite"
val tfliteModelUrl =
    "https://github.com/pynicolas/fairscan-segmentation-model/releases/download/$tfliteModelVersion/$tfliteModelFileName"
val downloadedTfliteModelPath = layout.buildDirectory.file("downloads/$tfliteModelFileName")
val generatedAssetsDir = layout.buildDirectory.dir("generated/assets")

val downloadTFLiteModel = tasks.register("downloadTFLiteModel") {
    val outputFile = downloadedTfliteModelPath.get().asFile
    outputs.file(outputFile)

    doLast {
        if (!outputFile.exists()) {
            outputFile.parentFile.mkdirs()
            java.net.URL(tfliteModelUrl).openStream().use { input ->
                outputFile.outputStream().use { output -> input.copyTo(output) }
            }
        }
    }
}

val copyTFLiteToAssets = tasks.register<Copy>("copyTFLiteToAssets") {
    dependsOn(downloadTFLiteModel)
    from(downloadedTfliteModelPath)
    into(generatedAssetsDir)
}

tasks.named("preBuild") {
    dependsOn(copyTFLiteToAssets, syncFairscanSrc)
}

aboutLibraries {
    library {
        duplicationMode = com.mikepenz.aboutlibraries.plugin.DuplicateMode.MERGE
        duplicationRule = com.mikepenz.aboutlibraries.plugin.DuplicateRule.SIMPLE
    }
}

dependencies {
    implementation(project(":fairscan-imageprocessing")) {
        exclude(group = "org.openpnp", module = "opencv")
    }

    implementation(libs.compose.activity)
    implementation(platform(libs.compose.bom))
    implementation(libs.compose.ui)
    implementation(libs.compose.ui.graphics)
    implementation(libs.compose.ui.tooling.preview)
    implementation(libs.compose.material3)
    implementation(libs.material.icons.extended)
    implementation(libs.lifecycle.runtime.ktx)
    implementation(libs.lifecycle.viewmodel.compose)
    implementation(libs.lifecycle.runtime.compose)
    implementation(libs.camera.core)
    implementation(libs.camera.camera2)
    implementation(libs.camera.lifecycle)
    implementation(libs.camera.view)
    implementation(libs.datastore.preferences)
    implementation(libs.documentfile)
    implementation(libs.litert.fdroid)
    implementation(libs.litert.support) {
        exclude(group = "com.google.ai.edge.litert", module = "litert")
    }
    implementation(libs.litert.metadata) {
        exclude(group = "com.google.ai.edge.litert", module = "litert")
    }
    implementation(libs.opencv.android)
    implementation(libs.pdfbox.android) {
        exclude("org.bouncycastle")
    }
    implementation(libs.zoomable)
    implementation(libs.reorderable)
    implementation(libs.aboutlibraries.compose.m3)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.tesseract4android)

    debugImplementation(libs.compose.ui.tooling)
}
