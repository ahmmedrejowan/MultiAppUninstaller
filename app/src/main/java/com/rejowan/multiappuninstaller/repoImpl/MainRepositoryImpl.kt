package com.rejowan.multiappuninstaller.repoImpl

import android.content.Context
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.os.Build
import com.rejowan.multiappuninstaller.data.DataStoreHelper
import com.rejowan.multiappuninstaller.repo.MainRepository

class MainRepositoryImpl(
    private val context: Context,
    private val dataStoreHelper: DataStoreHelper
) : MainRepository {


    override suspend fun getAppList(): List<PackageInfo> {
        try {
            val pm = context.packageManager
            val packageList: List<PackageInfo> = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                pm.getInstalledPackages(PackageManager.PackageInfoFlags.of(0))
            } else {
                @Suppress("DEPRECATION") pm.getInstalledPackages(0)
            }

            val userApps = packageList.filter {
                (it.applicationInfo?.flags?.and(android.content.pm.ApplicationInfo.FLAG_SYSTEM) ?: 0) == 0
            }.sortedBy { it.applicationInfo?.loadLabel(pm).toString().lowercase() }

            return userApps
        } catch (e: Exception) {
            e.printStackTrace()
            return emptyList()
        }
    }

    override suspend fun isFirstLaunch(): Boolean {
        return dataStoreHelper.isFirstLaunch()
    }

    override suspend fun setFirstLaunchDone() {
        dataStoreHelper.setFirstLaunchDone()
    }

}