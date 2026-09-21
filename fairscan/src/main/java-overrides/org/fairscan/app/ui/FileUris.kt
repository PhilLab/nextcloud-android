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
 * Nextcloud-maintained copy of upstream's FileUris.kt (see :fairscan/build.gradle.kts). Upstream
 * computes the FileProvider authority as "${context.packageName}.fileprovider" - correct for a
 * standalone app, but here context.packageName is Nextcloud's own package
 * (com.nextcloud.client), and Nextcloud's own UriUploader.isSensitiveUri() rejects any returned
 * content:// URI whose string contains its own package name, as a guard against a malicious app
 * handing back a URI into Nextcloud's private storage. A fixed, non-package-derived authority
 * avoids that false positive without touching Nextcloud's own security check. Must match the
 * authority declared for org.fairscan.app.FairScanFileProvider in fairscan/src/main/AndroidManifest.xml.
 */
package org.fairscan.app.ui

import android.content.Context
import android.net.Uri
import androidx.core.content.FileProvider
import java.io.File

private const val FILE_PROVIDER_AUTHORITY = "org.fairscan.app.fileprovider"

fun uriForFile(context: Context, file: File): Uri = FileProvider.getUriForFile(context, FILE_PROVIDER_AUTHORITY, file)
