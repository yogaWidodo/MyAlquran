package com.expert.myalquran.domain.vida.model

import com.google.gson.annotations.SerializedName

/**
 * Keputusan dari backend. `action` bernilai ALLOW / BLOCK / REVIEW.
 *
 * Semua field nullable karena datang dari jaringan - server bisa saja
 * mengirim JSON yang tidak lengkap, dan Gson tidak akan protes. Nullable di
 * sini memaksa UI menangani kemungkinan itu, bukan crash di runtime.
 */
data class LivenessDecisionResponse(
    @SerializedName("action")
    val action: String?,

    @SerializedName("reason")
    val reason: String?,

    @SerializedName("message")
    val message: String?
)
