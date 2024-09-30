package com.expert.myalquran.domain.surah.usecase.saveayat_usecase

import com.expert.myalquran.domain.surah.model.response.surahResponse.surah.SurahResponse
import com.expert.myalquran.domain.surah.repository.ISurahRepository

class GetSaveAyatUseCase(private val repository: ISurahRepository) {
    suspend fun upsert(ayat: SurahResponse.DataItem) = repository.saveToDatabase(ayat)
}