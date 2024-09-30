package com.expert.myalquran.domain.surah.usecase.deleteayat_usecase

import com.expert.myalquran.domain.surah.model.response.surahResponse.surah.SurahResponse
import com.expert.myalquran.domain.surah.repository.ISurahRepository

class GetDeleteAyatUseCase(private val repository: ISurahRepository){
    suspend fun delete(ayat: SurahResponse.DataItem) = repository.delete(ayat)
}