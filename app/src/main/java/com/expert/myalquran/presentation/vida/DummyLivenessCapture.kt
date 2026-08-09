package com.expert.myalquran.presentation.vida

import com.expert.myalquran.domain.vida.model.LivenessDecisionRequest
import java.util.UUID
import kotlin.random.Random

/**
 * Tahap CAPTURE dari alur capture -> submit -> decision -> callback.
 *
 * Di project kantor, tahap ini diisi SDK VIDA yang membuka kamera dan
 * menghasilkan skor. Di sini skornya diacak, jadi tidak perlu kamera dan tidak
 * perlu AAR asli. Yang dilatih adalah alur dan penanganan state-nya, bukan
 * cara SDK menghitung skor.
 */
object DummyLivenessCapture {

    /**
     * @param forceServerError kalau true, userId diawali "fail" sehingga stub
     *   backend membalas HTTP 500. Dipakai untuk menguji jalur error.
     */
    fun capture(forceServerError: Boolean = false): LivenessDecisionRequest {
        val prefix = if (forceServerError) "fail" else "user"
        return LivenessDecisionRequest(
            userId = "$prefix-${Random.nextInt(1000, 9999)}",
            transactionId = UUID.randomUUID().toString(),
            livenessScore = Random.nextDouble(0.40, 0.99).round2(),
            manipulationScore = Random.nextDouble(0.00, 0.80).round2(),
            sessionId = UUID.randomUUID().toString(),
            source = "REHEARSAL"
        )
    }

    private fun Double.round2(): Double = Math.round(this * 100.0) / 100.0
}
