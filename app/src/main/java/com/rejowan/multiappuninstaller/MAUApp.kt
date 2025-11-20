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

package com.rejowan.multiappuninstaller

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import com.rejowan.multiappuninstaller.data.ThemePrefHelper
import com.rejowan.multiappuninstaller.di.mainModule
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import timber.log.Timber

class MAUApp : Application() {

    private val coroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
    private lateinit var themePrefHelper: ThemePrefHelper

    override fun onCreate() {
        super.onCreate()

        themePrefHelper = ThemePrefHelper(this)

        coroutineScope.launch {
            themePrefHelper.setDefaultThemeIfNotSet()
        }

        coroutineScope.launch {
            themePrefHelper.getTheme().collectLatest { theme ->
                Timber.tag("MAUApp").e("Theme: $theme")
                when (theme) {
                    "Light" -> {
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                        Timber.tag("MAUApp").e("Set mode: ${AppCompatDelegate.getDefaultNightMode()}")
                    }

                    "Dark" -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                    else -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
                }
            }
        }

        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }

        startKoin {
            androidContext(this@MAUApp)
            modules(
                listOf(
                    mainModule
                )
            )
        }

    }
}