package com.rejowan.multiappuninstaller.data

import kotlinx.coroutines.flow.first

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "app_preferences")

class DataStoreHelper(private val context: Context) {

    private val preferencesKey = booleanPreferencesKey("is_first_launch")

    suspend fun isFirstLaunch(): Boolean {
        val preferences = context.dataStore.data.first()
        return preferences[preferencesKey] ?: true
    }

    suspend fun setFirstLaunchDone() {
        context.dataStore.edit { preferences ->
            preferences[preferencesKey] = false
        }
    }
}
