package com.expert.myalquran.core.di

import com.expert.myalquran.data.source.remote.network.ApiInterface
import com.expert.myalquran.core.utils.Constants.BASE_URL
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.android.BuildConfig
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

fun provideGson(): Gson = GsonBuilder().setLenient().create()

fun provideOkHttpClient() = if (BuildConfig.DEBUG) {
    val loggingInterceptor = HttpLoggingInterceptor()
    loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY)
    OkHttpClient
        .Builder()
        .addInterceptor(loggingInterceptor)
        .build()
} else {
    OkHttpClient
        .Builder()
        .build()
}

fun provideRetrofit(gson: Gson, client: OkHttpClient): ApiInterface =
    Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(client)
        .addConverterFactory(GsonConverterFactory.create(gson))
        .build()
        .create(ApiInterface::class.java)

val apiModule = module {
    single { provideGson() }
    single { provideOkHttpClient() }
    single { provideRetrofit(get(), get()) }
}