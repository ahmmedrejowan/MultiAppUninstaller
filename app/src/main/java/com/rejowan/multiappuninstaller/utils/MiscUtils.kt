/*
 * Multi App Uninstaller
 * Copyright (C) 2025 K M Rejowan Ahmmed
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.rejowan.multiappuninstaller.utils

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.os.Build
import android.widget.Toast
import timber.log.Timber

class MiscUtils {


    fun mailIntent(context: Context) {
        val packageInfo = try {
            context.packageManager.getPackageInfo(context.packageName, 0)
        } catch (e: Exception) {
            Timber.tag("SettingsScreen").e("Failed to get package info: ${e.message}")
            null
        }
        val appPackageName = context.packageName
        val appVersionName = packageInfo?.versionName ?: "Unknown"
        val appVersionCode = packageInfo?.let {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                it.longVersionCode.toString()
            } else {
                @Suppress("DEPRECATION") it.versionCode.toString()
            }
        } ?: "Unknown"

        val androidVersion = Build.VERSION.RELEASE
        val deviceModel = Build.MODEL
        val deviceBrand = Build.BRAND

        val emailBody = """
                Please provide your feedback or report any issues below.
                
                App Metadata:
                - Package Name: $appPackageName
                - Version Name: $appVersionName
                - Version Code: $appVersionCode
                
                Device Metadata:
                - Android Version: $androidVersion
                - Device Model: $deviceModel
                - Device Brand: $deviceBrand
                
                ---
                Your Feedback:
            """.trimIndent()

        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "message/rfc822"
            putExtra(Intent.EXTRA_EMAIL, arrayOf("kmrejowan@gmail.com"))
            putExtra(Intent.EXTRA_SUBJECT, "Feedback for Multi App Uninstaller")
            putExtra(Intent.EXTRA_TEXT, emailBody)
        }

        try {
            context.startActivity(Intent.createChooser(intent, "Send Email"))
        } catch (e: ActivityNotFoundException) {
            Timber.tag("SettingsScreen").e("No email app found: ${e.message}")
            Toast.makeText(context, "No email app found", Toast.LENGTH_SHORT).show()
        }
    }

}