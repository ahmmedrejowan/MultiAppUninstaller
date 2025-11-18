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
