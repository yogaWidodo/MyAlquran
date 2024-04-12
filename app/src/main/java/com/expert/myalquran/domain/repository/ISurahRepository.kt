package com.expert.myalquran.domain.repository

import androidx.lifecycle.LiveData
import com.expert.myalquran.data.source.local.room.AyatDao
import com.expert.myalquran.domain.model.response.detailsurah.DetailResponse
import com.expert.myalquran.domain.model.response.surah.SurahResponse
import retrofit2.Response

interface ISurahRepository {

    suspend fun getSurah():Response<SurahResponse>

    suspend fun getDetailSurah(nomor: Int):Response<DetailResponse>

    fun getAllAyat(): LiveData<List<SurahResponse.DataItem>>
   suspend fun delete(ayat: SurahResponse.DataItem)
    suspend fun saveToDatabase(ayat: SurahResponse.DataItem)
}