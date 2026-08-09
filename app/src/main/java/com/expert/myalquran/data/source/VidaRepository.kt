package com.expert.myalquran.data.source

import com.expert.myalquran.data.source.remote.network.VidaApiInterface
import com.expert.myalquran.domain.vida.model.LivenessDecisionRequest
import com.expert.myalquran.domain.vida.model.LivenessDecisionResponse
import com.expert.myalquran.domain.vida.repository.IVidaRepository
import retrofit2.Response

class VidaRepository(
    private val api: VidaApiInterface
) : IVidaRepository {

    override suspend fun submitLivenessDecision(
        request: LivenessDecisionRequest
    ): Response<LivenessDecisionResponse> = api.submitLivenessDecision(request)
}
