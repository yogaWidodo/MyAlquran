package com.expert.myalquran.domain.deleteayat_usecase

import com.expert.myalquran.data.source.local.room.SurahDatabase
import com.expert.myalquran.domain.model.response.surah.SurahResponse
import com.expert.myalquran.domain.repository.ISurahRepository

class GetDeleteAyatUseCase(private val repository: ISurahRepository){
    suspend fun delete(ayat: SurahResponse.DataItem) = repository.delete(ayat)
}