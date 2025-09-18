package com.rejowan.multiappuninstaller

import android.app.Application
import android.util.Log
import androidx.appcompat.app.AppCompatDelegate
import com.rejowan.multiappuninstaller.data.FirstLaunchHelper
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