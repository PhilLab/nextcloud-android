/*
 * Nextcloud - Android Client
 *
 * SPDX-FileCopyrightText: 2026 Nextcloud GmbH and Nextcloud contributors
 * SPDX-License-Identifier: AGPL-3.0-or-later
 */
package org.fairscan.app

import androidx.core.content.FileProvider

/**
 * Nextcloud addition, not present upstream (see :fairscan/build.gradle.kts). FairScan's own
 * manifest registers plain androidx.core.content.FileProvider directly; declaring that same class
 * a second time here, alongside :app's own FileProvider declaration, makes AGP's manifest merger
 * treat both <provider> entries as the same logical component (its merge key is android:name) and
 * reject the differing authorities. Subclassing gives this one a distinct android:name so both
 * coexist - FileProvider.getUriForFile() resolves purely by authority string at runtime, so
 * FairScan's own uriForFile() (which computes "${packageName}.fileprovider") keeps working
 * unmodified.
 */
class FairScanFileProvider : FileProvider()
