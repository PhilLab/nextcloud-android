/*
 * Nextcloud - Android Client
 *
 * SPDX-FileCopyrightText: 2026 Nextcloud GmbH and Nextcloud contributors
 * SPDX-License-Identifier: AGPL-3.0-or-later
 */
package com.nextcloud.client.documentscan

import android.content.Context
import android.content.Intent
import org.fairscan.app.MainActivity

/**
 * Launches the embedded FairScan (see :fairscan/build.gradle.kts) explicitly, rather than through
 * the org.fairscan.app.action.SCAN_TO_PDF implicit intent FairScan documents for a separately
 * installed app - MainActivity is compiled into this APK now, so no package resolution is needed.
 * The action is still set on the explicit intent: it's the same string FairScan's own
 * MainActivity.resolveLaunchMode() already switches on, so no FairScan code change is needed to
 * recognize it.
 */
class FairScanDocumentScanLauncher : DocumentScanLauncher {
    override fun scanIntent(context: Context): Intent = Intent(context, MainActivity::class.java).apply {
        action = SCAN_TO_PDF_ACTION
    }

    private companion object {
        const val SCAN_TO_PDF_ACTION = "org.fairscan.app.action.SCAN_TO_PDF"
    }
}
