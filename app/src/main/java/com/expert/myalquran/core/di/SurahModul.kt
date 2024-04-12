package com.expert.myalquran.core.di

import android.content.Context
import androidx.room.Room
import com.expert.myalquran.data.source.SurahRepository
import com.expert.myalquran.data.source.local.room.AyatDao
import com.expert.myalquran.data.source.local.room.SurahDatabase
import com.expert.myalquran.data.source.remote.network.ApiInterface
import com.expert.myalquran.domain.deleteayat_usecase.GetDeleteAyatUseCase
import com.expert.myalquran.domain.detailsurah_usecase.GetDetailSurahUseCase
import com.expert.myalquran.domain.getayat_usecase.GetAyatUseCase
import com.expert.myalquran.domain.repository.ISurahRepository
import com.expert.myalquran.domain.saveayat_usecase.GetSaveAyatUseCase
import com.expert.myalquran.domain.surah_usecase.GetSurahUseCase
import com.expert.myalquran.presentation.detail.DetailViewModel
import com.expert.myalquran.presentation.favorite.FavoriteViewModel
import com.expert.myalquran.presentation.home.MainViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val surahModule = module {
    fun provideSurahDatabase(context: Context) =
        Room.databaseBuilder(
            context, SurahDatabase::class.java, "surah_db"
        ).build()

    fun provideDao(database: SurahDatabase): AyatDao = database.getAyatDao()

    single { provideSurahDatabase(androidContext()) }
    single { provideDao(get()) }
    fun provideSurahRepository(apiInterface: ApiInterface, ayatDao: AyatDao): ISurahRepository {
        return SurahRepository(apiInterface, ayatDao)
    }
    single { provideSurahRepository(get(), get()) }
    single { SurahRepository(get(), get()) }
    single { GetSurahUseCase(get()) }
    single { GetDetailSurahUseCase(get()) }
    single { GetSaveAyatUseCase(get()) }
    single{GetAyatUseCase(get())}
    single{GetDeleteAyatUseCase(get())}
    viewModel { FavoriteViewModel(get(), get(), get()) }
    viewModel { MainViewModel(get()) }
    viewModel { DetailViewModel(get(), get()) }
}