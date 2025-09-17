package com.rejowan.multiappuninstaller

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.rejowan.multiappuninstaller.feature.module.home.HomeScreen
import com.rejowan.multiappuninstaller.ui.theme.MAUTheme

class HomeActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen()
        enableEdgeToEdge()
        setContent {
            MAUTheme {
                HomeScreen()
            }
        }
    }
}
