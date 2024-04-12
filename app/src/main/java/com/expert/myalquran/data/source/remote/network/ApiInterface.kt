package com.expert.myalquran.data.source.remote.network


import com.expert.myalquran.domain.model.response.detailsurah.DetailResponse
import com.expert.myalquran.domain.model.response.surah.SurahResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface ApiInterface {

    @GET("surat")
    suspend fun getSurat(): Response<SurahResponse>

    @GET("surat/{nomor}")
    suspend fun getDetailSurat(@Path("nomor") nomor: Int = 1): Response<DetailResponse>

}