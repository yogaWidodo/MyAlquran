package com.expert.myalquran.domain.vida.model

import com.google.gson.annotations.SerializedName

/**
 * Payload yang dikirim ke backend keputusan liveness.
 *
 * Nama field sengaja generik dan TIDAK sama dengan kontrak backend kantor -
 * repo ini repo latihan pribadi.
 */
data class LivenessDecisionRequest(
    @SerializedName("userId")
    val userId: String,

    @SerializedName("transactionId")
    val transactionId: String?,

    @SerializedName("livenessScore")
    val livenessScore: Double,

    @SerializedName("manipulationScore")
    val manipulationScore: Double,

    @SerializedName("sessionId")
    val sessionId: String?,

    @SerializedName("source")
    val source: String = "REHEARSAL"
)
