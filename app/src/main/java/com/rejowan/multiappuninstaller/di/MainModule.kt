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

package com.rejowan.multiappuninstaller.di

import com.rejowan.multiappuninstaller.data.FirstLaunchHelper
import com.rejowan.multiappuninstaller.data.ThemePrefHelper
import com.rejowan.multiappuninstaller.presentation.home.HomeViewModel
import com.rejowan.multiappuninstaller.repo.MainRepository
import com.rejowan.multiappuninstaller.repoImpl.MainRepositoryImpl
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module


val mainModule = module {
    single { ThemePrefHelper(get()) }
    single { FirstLaunchHelper(get()) }
    single<MainRepository> { MainRepositoryImpl(get(), get(), get()) }

    // ViewModels
    viewModel { HomeViewModel(get()) }
    viewModel { com.rejowan.multiappuninstaller.presentation.settings.SettingsViewModel(get()) }
}
