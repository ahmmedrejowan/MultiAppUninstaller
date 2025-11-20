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
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import timber.log.Timber

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "theme_mode_preferences")

class ThemePrefHelper(private val context: Context) {

    private val themeModeKey = stringPreferencesKey("theme_preference")
    private val dynamicColorKey = stringPreferencesKey("dynamic_color_preference")

    suspend fun saveTheme(theme: String) {
        context.dataStore.edit { preferences ->
            preferences[themeModeKey] = theme
        }
    }

    fun getTheme(): Flow<String> {
        return context.dataStore.data.map { preferences ->
            preferences[themeModeKey] ?: "System Default"
        }
    }

    suspend fun setDefaultThemeIfNotSet() {
        context.dataStore.edit { preferences ->
            Timber.tag("ThemePrefHelper").e("setDefaultThemeIfNotSet: Current value: ${preferences[themeModeKey]}")
            if (preferences[themeModeKey] == null) {
                preferences[themeModeKey] = "System Default"
            }
        }
    }


    suspend fun saveDynamicColorPreference(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[dynamicColorKey] = if (enabled) "true" else "false"
        }
    }

    fun isDynamicColorEnabled(): Flow<Boolean> {
        return context.dataStore.data.map { preferences ->
            preferences[dynamicColorKey]?.toBoolean() ?: false
        }
    }


}
