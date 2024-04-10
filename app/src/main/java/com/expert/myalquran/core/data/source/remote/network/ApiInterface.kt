package com.expert.myalquran.core.data.source.remote.network


import com.expert.myalquran.core.data.source.remote.response.detailsurah.DetailResponse
import com.expert.myalquran.core.data.source.remote.response.surah.SurahResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface ApiInterface {

    @GET("surat")
    suspend fun getSurat(): Response<SurahResponse>

    @GET("surat/{nomor}")
    suspend fun getDetailSurat(@Path("nomor") nomor: Int = 1): Response<DetailResponse>

}