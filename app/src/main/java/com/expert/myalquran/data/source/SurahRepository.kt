package com.expert.myalquran.data.source

import androidx.lifecycle.LiveData
import com.expert.myalquran.data.source.remote.network.ApiInterface
import com.expert.myalquran.core.utils.DataStatus
import com.expert.myalquran.data.source.local.room.AyatDao
import com.expert.myalquran.data.source.local.room.SurahDatabase
import com.expert.myalquran.domain.surah.model.response.surahResponse.detailsurah.DetailResponse
import com.expert.myalquran.domain.surah.model.response.surahResponse.surah.SurahResponse
import com.expert.myalquran.domain.surah.repository.ISurahRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import retrofit2.Response


class SurahRepository(private val myApi: ApiInterface, private val dao: AyatDao) :
    ISurahRepository {



    override suspend fun getSurah(): Response<SurahResponse> {
        return myApi.getSurat()
    }

    override suspend fun getDetailSurah(nomor: Int): Response<DetailResponse> {
        return myApi.getDetailSurat(nomor)
    }

    override fun getAllAyat(): LiveData<List<SurahResponse.DataItem>> {
        return dao.getAllAyat()
    }

    override suspend fun delete(ayat: SurahResponse.DataItem) {
        dao.delete(ayat)
    }

    override suspend fun saveToDatabase(ayat: SurahResponse.DataItem) {
        dao.upsert(ayat)
    }

}







