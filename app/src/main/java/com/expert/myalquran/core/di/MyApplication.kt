package com.expert.myalquran.core.di

import android.app.Application
import com.expert.myalquran.core.di.disurah.apiSurahModule
import com.expert.myalquran.core.di.disurah.surahModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

open class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@MyApplication)
            modules(apiSurahModule, surahModule)
        }
    }
}