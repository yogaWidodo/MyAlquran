package com.expert.myalquran.domain.surah_usecase

import com.expert.myalquran.core.utils.DataStatus
import com.expert.myalquran.domain.model.response.surah.SurahResponse
import com.expert.myalquran.domain.repository.ISurahRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn


class GetSurahUseCase(private val repository: ISurahRepository) {

    suspend operator fun invoke(): Flow<DataStatus<List<SurahResponse.DataItem>>> = flow {
        emit(DataStatus.loading())
        val result = repository.getSurah()
        when (result.code()) {
            200 -> emit(DataStatus.success(result.body()?.data))
            400 -> emit(DataStatus.error(result.message()))
            500 -> emit(DataStatus.error(result.message()))
        }
    }
        .catch { emit(DataStatus.error(it.message.toString())) }
        .flowOn(Dispatchers.IO)

}