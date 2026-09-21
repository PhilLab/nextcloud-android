/*
 * Nextcloud - Android Client
 *
 * SPDX-FileCopyrightText: 2026 Alper Ozturk <alper.ozturk@nextcloud.com>
 * SPDX-FileCopyrightText: 2025 Jimly Asshiddiqy <jimly.asshiddiqy@accenture.com>
 * SPDX-License-Identifier: AGPL-3.0-or-later
 */
rootProject.name = "Nextcloud"

pluginManagement {
    resolutionStrategy.eachPlugin {
        if (requested.id.id == "shot") useModule("com.karumi:shot:${requested.version}")
    }

    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        gradlePluginPortal()
        mavenCentral()
    }
}

@Suppress("UnstableApiUsage")
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        maven("https://jitpack.io")
    }
}

/*
Needed for local android library
includeBuild("../android-library") {
    dependencySubstitution {
        substitute(module("com.github.nextcloud:android-library"))
            .using(project(":library"))
    }
}
*/


/*
Needed for local android common library

includeBuild("../android-common") {
    dependencySubstitution {
        substitute(module("com.github.nextcloud.android-common:core"))
            .using(project(":core"))

        substitute(module("com.github.nextcloud.android-common:ui"))
            .using(project(":ui"))
    }
}
*/

include(":app", ":fairscan", ":fairscan-imageprocessing", ":opencv-minimal", ":opencv-minimal:android", ":opencv-minimal:java")

// These reuse FairScan's own build.gradle.kts files entirely unmodified (their only external
// references - the jetbrains-kotlin-jvm plugin and assertj - are already satisfied by this repo's
// own version catalog), rather than maintaining parallel Nextcloud-owned copies like :fairscan and
// :fairscan-imageprocessing's other dependencies need. :opencv-minimal itself is an empty parent
// module, included only so its subprojects' default directory resolution matches FairScan's own
// physical layout. See submodules/fairscan/opencv-minimal/README.md for what this actually builds
// and why.
project(":fairscan-imageprocessing").projectDir = file("submodules/fairscan/imageprocessing")
project(":opencv-minimal").projectDir = file("submodules/fairscan/opencv-minimal")
project(":opencv-minimal:android").projectDir = file("submodules/fairscan/opencv-minimal/android")
project(":opencv-minimal:java").projectDir = file("submodules/fairscan/opencv-minimal/java")
