package com.expert.myalquran.core.data.source

import com.expert.myalquran.core.data.source.remote.network.ApiInterface
import com.expert.myalquran.core.utils.DataStatus
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn


class SurahRepository(private val myApi: ApiInterface) {


    suspend fun getSurah() = flow {
        emit(DataStatus.loading())
        val result = myApi.getSurat()
        when (result.code()) {
            200 -> emit(DataStatus.success(result.body()?.data))
            400 -> emit(DataStatus.error(result.message()))
            500 -> emit(DataStatus.error(result.message()))
        }
    }
        .catch { emit(DataStatus.error(it.message.toString())) }
        .flowOn(Dispatchers.IO)

}







