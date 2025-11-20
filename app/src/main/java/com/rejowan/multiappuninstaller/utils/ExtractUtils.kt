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

@file:Suppress("DEPRECATION")

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
    ): Drawable {
        return packageInfo.applicationInfo?.loadIcon(context.packageManager)
            ?: context.packageManager.defaultActivityIcon
    }

    fun getAppName(
        packageInfo: PackageInfo,
        context: Context
    ): String {
        return packageInfo.applicationInfo?.loadLabel(context.packageManager)?.toString()
            ?: "Unknown"
    }


    fun getAppSize(
        packageInfo: PackageInfo
    ): String {
        return packageInfo.applicationInfo?.sourceDir?.let { src ->
            val mb = File(src).length() / (1024f * 1024f)
            String.format(Locale.getDefault(), "%.2f MB", mb)
        } ?: "Unknown size"

    }

    fun getAppInstaller(
        packageInfo: PackageInfo,
        context: Context
    ): String {
        return runCatching {
            context.packageManager.getInstallerPackageName(packageInfo.packageName)
        }.getOrNull() ?: "Unknown"
    }

}