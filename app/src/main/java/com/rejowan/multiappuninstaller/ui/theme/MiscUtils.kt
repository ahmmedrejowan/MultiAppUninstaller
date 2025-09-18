package com.rejowan.multiappuninstaller.ui.theme

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.os.Build
import android.widget.Toast
import timber.log.Timber

class MiscUtils {


    fun mailIntent(context: Context){
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