package com.expert.myalquran.core.di

import com.expert.myalquran.core.data.source.SurahRepository
import com.expert.myalquran.core.presentation.detail.DetailViewModel
import com.expert.myalquran.core.presentation.home.MainViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val surahModule = module {
    factory { SurahRepository(get()) }
    viewModel { MainViewModel(get()) }
    viewModel{ DetailViewModel(get()) }
}