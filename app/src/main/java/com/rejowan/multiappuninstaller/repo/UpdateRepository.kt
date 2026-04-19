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

import com.rejowan.multiappuninstaller.data.UpdateCheckInterval
import com.rejowan.multiappuninstaller.data.UpdateState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

interface UpdateRepository {

    val updateState: StateFlow<UpdateState>

    suspend fun runCheckIfNeeded(currentVersion: String)

    suspend fun runCheck(currentVersion: String)

    fun dismissUpdate()

    suspend fun getLastCheckTime(): Long

    suspend fun shouldSkipVersion(version: String): Boolean

    suspend fun skipVersion(version: String)

    suspend fun clearSkippedVersions()

    fun getUpdateCheckInterval(): Flow<UpdateCheckInterval>

    suspend fun setUpdateCheckInterval(interval: UpdateCheckInterval)
}
