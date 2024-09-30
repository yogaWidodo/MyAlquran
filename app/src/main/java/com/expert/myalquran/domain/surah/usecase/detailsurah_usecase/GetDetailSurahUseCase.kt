package com.expert.myalquran.domain.surah.usecase.detailsurah_usecase

import com.expert.myalquran.core.utils.DataStatus
import com.expert.myalquran.domain.surah.model.response.surahResponse.detailsurah.DetailResponse
import com.expert.myalquran.domain.surah.repository.ISurahRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

class GetDetailSurahUseCase(private val repository: ISurahRepository) {

    suspend operator fun invoke(nomor: Int): Flow<DataStatus<List<DetailResponse.Data.AyatItem>>> = flow {
        emit(DataStatus.loading())
        val result = repository.getDetailSurah(nomor)
        when (result.code()) {
            200 -> emit(DataStatus.success(result.body()?.data?.ayat))
            400 -> emit(DataStatus.error(result.message()))
            500 -> emit(DataStatus.error(result.message()))
        }
    }
        .catch { emit(DataStatus.error(it.message.toString())) }
        .flowOn(Dispatchers.IO)

}