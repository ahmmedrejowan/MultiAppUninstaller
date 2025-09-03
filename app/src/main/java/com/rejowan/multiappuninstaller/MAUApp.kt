package com.rejowan.multiappuninstaller

import android.app.Application
import com.rejowan.multiappuninstaller.di.mainModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import timber.log.Timber

class MAUApp : Application() {


    override fun onCreate() {
        super.onCreate()

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