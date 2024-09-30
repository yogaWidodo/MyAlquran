package com.expert.myalquran.domain.surah.usecase.getayat_usecase

import com.expert.myalquran.domain.surah.repository.ISurahRepository

class GetAyatUseCase(private val repository: ISurahRepository) {
    fun getAllAyat() = repository.getAllAyat()
}