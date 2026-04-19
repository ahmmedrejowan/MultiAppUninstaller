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

package com.rejowan.multiappuninstaller.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

private val Context.updateDataStore: DataStore<Preferences> by preferencesDataStore(name = "update_preferences")

class UpdatePrefHelper(private val context: Context) {

    private val intervalKey = stringPreferencesKey("update_check_interval")
    private val lastCheckTimeKey = longPreferencesKey("update_last_check_time")
    private val skippedVersionsKey = stringSetPreferencesKey("update_skipped_versions")

    fun getUpdateCheckInterval(): Flow<UpdateCheckInterval> {
        return context.updateDataStore.data.map { prefs ->
            val stored = prefs[intervalKey]
            stored?.let { name ->
                runCatching { UpdateCheckInterval.valueOf(name) }.getOrNull()
            } ?: UpdateCheckInterval.WEEKLY
        }
    }

    suspend fun setUpdateCheckInterval(interval: UpdateCheckInterval) {
        context.updateDataStore.edit { prefs ->
            prefs[intervalKey] = interval.name
        }
    }

    suspend fun getLastCheckTime(): Long {
        return context.updateDataStore.data.first()[lastCheckTimeKey] ?: 0L
    }

    suspend fun setLastCheckTime(time: Long) {
        context.updateDataStore.edit { prefs ->
            prefs[lastCheckTimeKey] = time
        }
    }

    suspend fun shouldSkipVersion(version: String): Boolean {
        val skipped = context.updateDataStore.data.first()[skippedVersionsKey] ?: emptySet()
        return version in skipped
    }

    suspend fun skipVersion(version: String) {
        context.updateDataStore.edit { prefs ->
            val current = prefs[skippedVersionsKey] ?: emptySet()
            prefs[skippedVersionsKey] = current + version
        }
    }

    suspend fun clearSkippedVersions() {
        context.updateDataStore.edit { prefs ->
            prefs.remove(skippedVersionsKey)
        }
    }
}
