/*
 * Copyright 2025-2026 The FairScan authors
 *
 * This program is free software: you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for
 * more details.
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <https://www.gnu.org/licenses/>.
 *
 * Nextcloud-maintained stub of upstream's LibrariesScreen.kt (see :fairscan/build.gradle.kts).
 * Upstream's version calls com.mikepenz.aboutlibraries.ui.compose.android.rememberLibraries()/
 * LibraryDefaults.libraryTextStyles(), whose API changed between the AboutLibraries version
 * FairScan pins (13.2.1, incompatible with AGP 9.4's LibraryExtension detection) and the version
 * this module uses instead (15.2.0). Reachable only from Settings, which Nextcloud's launch mode
 * (EXTERNAL_SCAN_TO_PDF) always hides - see MainActivity.navigation()'s toSettingsScreen - so it's
 * simplified to keep the same public signature MainActivity.kt calls, without chasing the new API
 * for a screen nothing can navigate to. Diff against upstream after bumping the submodule pin.
 */
package org.fairscan.app.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import org.fairscan.app.R
import org.fairscan.app.ui.components.BackButton

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LibrariesScreen(onBack: () -> Unit) {
    BackHandler { onBack() }
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.libraries_open_source)) },
                navigationIcon = { BackButton(onClick = onBack) }
            )
        }
    ) { padding ->
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Intentionally empty - see file header.
        }
    }
}
