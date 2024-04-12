package com.expert.myalquran.domain.saveayat_usecase

import com.expert.myalquran.domain.model.response.surah.SurahResponse
import com.expert.myalquran.domain.repository.ISurahRepository

class GetSaveAyatUseCase(private val repository: ISurahRepository) {
    suspend fun upsert(ayat: SurahResponse.DataItem) = repository.saveToDatabase(ayat)
}