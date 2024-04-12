package com.expert.myalquran.domain.getayat_usecase

import com.expert.myalquran.domain.repository.ISurahRepository

class GetAyatUseCase(private val repository: ISurahRepository) {
    fun getAllAyat() = repository.getAllAyat()
}