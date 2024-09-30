package com.expert.myalquran.data.source.local.room

import android.content.Context
import androidx.room.Database
import androidx.room.RoomDatabase
import com.expert.myalquran.domain.surah.model.response.surahResponse.detailsurah.DetailResponse
import com.expert.myalquran.domain.surah.model.response.surahResponse.surah.SurahResponse

@Database(entities = [SurahResponse.DataItem::class], version = 1)
abstract class SurahDatabase : RoomDatabase() {
    abstract fun getAyatDao(): AyatDao



}