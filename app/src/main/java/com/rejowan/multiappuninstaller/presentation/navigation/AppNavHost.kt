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

package com.rejowan.multiappuninstaller.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.rejowan.multiappuninstaller.feature.components.OnboardingScreen
import com.rejowan.multiappuninstaller.presentation.home.HomeScreen

@Composable
fun AppNavHost(
    navController: NavHostController,
    showOnboarding: Boolean,
    onOnboardingComplete: () -> Unit
) {
    val startDestination: Route = if (showOnboarding) Route.Onboarding else Route.Home

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable<Route.Onboarding> {
            OnboardingScreen(
                onComplete = {
                    onOnboardingComplete()
                    navController.navigate(Route.Home) {
                        popUpTo<Route.Onboarding> { inclusive = true }
                    }
                }
            )
        }

        composable<Route.Home> {
            HomeScreen()
        }
    }
}
