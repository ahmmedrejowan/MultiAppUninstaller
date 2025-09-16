package com.rejowan.multiappuninstaller.utils

import android.content.Context
import android.content.pm.PackageInfo
import android.graphics.drawable.Drawable
import java.io.File
import java.util.Locale

object ExtractUtils {

    fun getAppIcon(
        packageInfo: PackageInfo,
        context: Context
    ) : Drawable{
        return packageInfo.applicationInfo?.loadIcon(context.packageManager)
            ?: context.packageManager.defaultActivityIcon
    }

    fun getAppName(
        packageInfo: PackageInfo,
        context: Context
    ) : String {
        return packageInfo.applicationInfo?.loadLabel(context.packageManager)?.toString()
            ?: "Unknown"
    }


    fun getAppSize(
        packageInfo: PackageInfo
    ) : String {
        return packageInfo.applicationInfo?.sourceDir?.let { src ->
            val mb = File(src).length() / (1024f * 1024f)
            String.format(Locale.getDefault(), "%.2f MB", mb)
        } ?: "Unknown size"

    }

    fun getAppInstaller(
        packageInfo: PackageInfo,
        context: Context
    ) : String {
        return runCatching {
            context.packageManager.getInstallerPackageName(packageInfo.packageName)
        }.getOrNull() ?: "Unknown"
    }

}