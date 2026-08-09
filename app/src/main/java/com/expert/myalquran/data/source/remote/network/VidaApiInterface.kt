package com.expert.myalquran.data.source.remote.network

import com.expert.myalquran.domain.vida.model.LivenessDecisionRequest
import com.expert.myalquran.domain.vida.model.LivenessDecisionResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

/**
 * Interface terpisah dari [ApiInterface] karena base URL-nya berbeda.
 * Instance Retrofit-nya juga terpisah, lihat core/di/divida/VidaModule.kt.
 */
interface VidaApiInterface {

    @POST("liveness/decision")
    suspend fun submitLivenessDecision(
        @Body body: LivenessDecisionRequest
    ): Response<LivenessDecisionResponse>
}
