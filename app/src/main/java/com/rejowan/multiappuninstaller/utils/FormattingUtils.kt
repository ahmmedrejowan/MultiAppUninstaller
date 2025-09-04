package com.rejowan.multiappuninstaller.utils

import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.os.Parcelable
import kotlinx.parcelize.Parcelize


// --- Sorting core (put in a separate file if you want) ---
enum class SortKey { NAME, SIZE, INSTALLED, UPDATED }
enum class SortOrder { ASC, DESC }

@Parcelize
data class SortConfig(
    val key: SortKey = SortKey.NAME, val order: SortOrder = SortOrder.ASC
) : Parcelable

fun PackageInfo.appLabel(pm: PackageManager): String = applicationInfo?.loadLabel(pm)?.toString().orEmpty()

fun PackageInfo.apkSizeBytes(): Long = applicationInfo?.sourceDir?.let { java.io.File(it).length() } ?: 0L

@Suppress("DEPRECATION")
fun PackageInfo.lastUpdate(): Long = lastUpdateTime // already Long millis

fun compareMaybeDesc(result: Int, order: SortOrder): Int = if (order == SortOrder.DESC) -result else result

fun sortApps(
    apps: List<android.content.pm.PackageInfo>, pm: PackageManager, config: SortConfig
): List<android.content.pm.PackageInfo> {
    if (apps.isEmpty()) return apps
    return apps.sortedWith { a, b ->
        val r = when (config.key) {
            SortKey.NAME -> a.appLabel(pm).lowercase().compareTo(b.appLabel(pm).lowercase())
            SortKey.SIZE -> a.apkSizeBytes().compareTo(b.apkSizeBytes())
            SortKey.INSTALLED -> a.firstInstallTime.compareTo(b.firstInstallTime)
            SortKey.UPDATED -> a.lastUpdate().compareTo(b.lastUpdate())
        }
        compareMaybeDesc(r, config.order)
    }
}

