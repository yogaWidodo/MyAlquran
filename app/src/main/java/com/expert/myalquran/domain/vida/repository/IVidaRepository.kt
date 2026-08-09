package com.expert.myalquran.domain.vida.repository

import com.expert.myalquran.domain.vida.model.LivenessDecisionRequest
import com.expert.myalquran.domain.vida.model.LivenessDecisionResponse
import retrofit2.Response

interface IVidaRepository {

    suspend fun submitLivenessDecision(
        request: LivenessDecisionRequest
    ): Response<LivenessDecisionResponse>
}
