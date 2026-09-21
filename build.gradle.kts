/*
 * Nextcloud - Android Client
 *
 * SPDX-FileCopyrightText: 2025 Jimly Asshiddiqy <jimly.asshiddiqy@accenture.com>
 * SPDX-License-Identifier: AGPL-3.0-or-later
 */
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.kotlin.compose) apply false
    alias(libs.plugins.spotless) apply false
    alias(libs.plugins.ksp) apply false
    alias(libs.plugins.kotlin.serialization) apply false
    alias(libs.plugins.kotlin.parcelize) apply false
    alias(libs.plugins.spotbugs) apply false
    alias(libs.plugins.detekt) apply false
    // needed to make renovate run without shot, as shot requires Android SDK
    // https://github.com/pedrovgs/Shot/issues/300
    alias(libs.plugins.shot) apply false
    // :fairscan-imageprocessing is a plain JVM module (no AGP), which needs this applied
    // explicitly - declared here so Gradle resolves one canonical version across the build,
    // same as every other plugin above.
    alias(libs.plugins.kotlin.jvm) apply false
    alias(libs.plugins.aboutlibraries.android) apply false
}

tasks.register<Delete>("clean") {
    delete(rootProject.layout.buildDirectory)
}

// :opencv-minimal:android is reused unmodified from the FairScan submodule (see
// settings.gradle.kts). Two things about its own build.gradle.kts are worked around from here
// rather than by patching that file:
gradle.projectsEvaluated {
    val opencvMinimalAndroid = project(":opencv-minimal:android")

    // 1. Its buildOpenCVNative_<abi> Exec tasks reference top-level script properties
    //    (modulesToInclude, openCVVersion) directly from their doFirst action, which captures an
    //    implicit reference to the whole build script and isn't configuration-cache-serializable.
    opencvMinimalAndroid.tasks.withType<Exec>().configureEach {
        notCompatibleWithConfigurationCache(
            "Shells out to prepare-opencv.sh/build-native.sh, whose Gradle wiring references " +
                "top-level script properties from task actions - see this build.gradle.kts."
        )
    }

    // 2. Its defaultConfig.consumerProguardFiles("consumer-rules.pro") resolves relative to this
    //    module's own directory, but the file actually lives one level up, in the opencv-minimal/
    //    parent directory - looks like an upstream bug (missing "../"), and release builds hard-fail
    //    on the missing file. Reconfiguring the DSL after the fact didn't take effect (AGP's newer
    //    DSL and its internal variant model apparently don't share the same backing list here), and
    //    wiring a Copy task into preBuild didn't get scheduled either (mergeReleaseConsumerProguardFiles
    //    doesn't appear to depend on preBuild in AGP's own task graph). Copied eagerly here instead,
    //    during configuration - a trivial, idempotent text-file copy, so running it unconditionally on
    //    every invocation costs nothing worth optimizing. Leaves a small, easily-cleaned untracked file
    //    in the submodule's own (already gitignored-for-generated-content) directory, not a change to
    //    any of its tracked files.
    opencvMinimalAndroid.file("../consumer-rules.pro").copyTo(
        opencvMinimalAndroid.file("consumer-rules.pro"),
        overwrite = true
    )
}

tasks.register<Copy>("installGitHooks") {
    description = "Install git hooks"

    val sourceFolder = "${rootProject.projectDir}/scripts/hooks"
    val destFolder = "${rootProject.projectDir}/.git/hooks"

    from(sourceFolder) { include("*") }
    into(destFolder)
    eachFile { println("${sourceFolder}/${file.path} -> ${destFolder}/${file.path}") }
}
