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

package com.rejowan.multiappuninstaller.repo

import android.content.pm.PackageInfo
import kotlinx.coroutines.flow.Flow

interface MainRepository {

    suspend fun getAppList(): List<PackageInfo>

    suspend fun saveTheme(theme: String)

    fun getTheme(): Flow<String>

    suspend fun setDefaultThemeIfNotSet()

    suspend fun saveDynamicColorPreference(enabled: Boolean)

    fun isDynamicColorEnabled(): Flow<Boolean>


}