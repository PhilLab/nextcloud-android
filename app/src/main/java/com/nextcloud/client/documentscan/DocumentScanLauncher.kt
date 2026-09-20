/*
 * Nextcloud - Android Client
 *
 * SPDX-FileCopyrightText: 2026 Nextcloud GmbH and Nextcloud contributors
 * SPDX-License-Identifier: AGPL-3.0-or-later
 */
package com.nextcloud.client.documentscan

import android.content.Context
import android.content.Intent

/**
 * Builds the intent that starts document scanning, called by OCFileListFragment.
 *
 * Kept as a narrow seam rather than constructing the intent inline at the call site: everything
 * FairScan-specific (which activity, which action) stays behind this one interface, so if a real
 * FairScan library dependency ever replaces the :fairscan wrapper module, only this interface's
 * implementation needs to change.
 */
interface DocumentScanLauncher {
    fun scanIntent(context: Context): Intent
}
