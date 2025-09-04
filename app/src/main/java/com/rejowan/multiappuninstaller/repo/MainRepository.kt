package com.rejowan.multiappuninstaller.repo

import android.content.pm.PackageInfo
import kotlinx.coroutines.flow.StateFlow

interface MainRepository {

    suspend fun getAppList(): List<PackageInfo>


}