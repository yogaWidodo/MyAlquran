package com.expert.myalquran.core.di.divida

import com.expert.myalquran.core.utils.Constants.BASE_URL_VIDA
import com.expert.myalquran.data.source.VidaRepository
import com.expert.myalquran.data.source.remote.network.VidaApiInterface
import com.expert.myalquran.domain.vida.repository.IVidaRepository
import com.expert.myalquran.domain.vida.usecase.SubmitLivenessDecisionUseCase
import com.expert.myalquran.presentation.vida.VidaViewModel
import com.google.gson.Gson
import okhttp3.OkHttpClient
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * Instance Retrofit terpisah karena base URL-nya beda dari API surah.
 * Gson dan OkHttpClient-nya menumpang singleton yang sudah ada di
 * apiSurahModule, jadi tidak ada koneksi pool yang terduplikasi.
 */
fun provideVidaApi(gson: Gson, client: OkHttpClient): VidaApiInterface =
    Retrofit.Builder()
        .baseUrl(BASE_URL_VIDA)
        .client(client)
        .addConverterFactory(GsonConverterFactory.create(gson))
        .build()
        .create(VidaApiInterface::class.java)

val vidaModule = module {
    single { provideVidaApi(get(), get()) }
    single<IVidaRepository> { VidaRepository(get()) }
    single { SubmitLivenessDecisionUseCase(get()) }
    viewModel { VidaViewModel(get()) }
}
