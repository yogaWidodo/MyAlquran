package com.expert.myalquran.core.data.source.remote.network

import com.expert.myalquran.core.data.source.remote.response.SurahResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface ApiInterface {

    @GET("surat")
    suspend fun getSurat(): Response<SurahResponse>


}