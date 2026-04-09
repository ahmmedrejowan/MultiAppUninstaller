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

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.lifecycleScope
import androidx.navigation.compose.rememberNavController
import com.rejowan.multiappuninstaller.data.FirstLaunchHelper
import com.rejowan.multiappuninstaller.presentation.navigation.AppNavHost
import com.rejowan.multiappuninstaller.ui.theme.MAUTheme
import kotlinx.coroutines.launch

class HomeActivity : ComponentActivity() {

    private var isReady by mutableStateOf(false)
    private var showOnboarding by mutableStateOf(false)

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        splashScreen.setKeepOnScreenCondition { !isReady }

        val firstLaunchHelper = FirstLaunchHelper(this)

        lifecycleScope.launch {
            showOnboarding = firstLaunchHelper.isFirstLaunch()
            isReady = true
        }

        setContent {
            MAUTheme {
                if (isReady) {
                    val navController = rememberNavController()
                    AppNavHost(
                        navController = navController,
                        showOnboarding = showOnboarding,
                        onOnboardingComplete = {
                            lifecycleScope.launch {
                                firstLaunchHelper.setFirstLaunchDone()
                            }
                        }
                    )
                }
            }
        }
    }
}
