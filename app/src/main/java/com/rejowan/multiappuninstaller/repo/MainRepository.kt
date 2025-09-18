package com.rejowan.multiappuninstaller.repo

import android.content.pm.PackageInfo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

interface MainRepository {

    suspend fun getAppList(): List<PackageInfo>

    suspend fun isFirstLaunch(): Boolean

    suspend fun setFirstLaunchDone()

    suspend fun saveTheme(theme: String)

    fun getTheme(): Flow<String>

    suspend fun setDefaultThemeIfNotSet()

    suspend fun saveDynamicColorPreference(enabled: Boolean)

    fun isDynamicColorEnabled(): Flow<Boolean>


}