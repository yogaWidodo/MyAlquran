package com.expert.myalquran.domain.vida.usecase

import com.expert.myalquran.core.utils.DataStatus
import com.expert.myalquran.domain.vida.model.LivenessDecisionRequest
import com.expert.myalquran.domain.vida.model.LivenessDecisionResponse
import com.expert.myalquran.domain.vida.repository.IVidaRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

/**
 * Tahap SUBMIT -> DECISION dari alur capture -> submit -> decision -> callback.
 *
 * Catatan beda dengan GetSurahUseCase yang sudah ada di repo ini: use case itu
 * hanya meng-emit untuk kode 200/400/500, sehingga respons 404/403/502 tidak
 * meng-emit apa pun dan UI menggantung di LOADING selamanya. Di sini pakai
 * `isSuccessful` + cabang else, jadi setiap kemungkinan pasti berujung SUCCESS
 * atau ERROR. Pola ini yang layak dibawa ke project kantor.
 */
class SubmitLivenessDecisionUseCase(private val repository: IVidaRepository) {

    suspend operator fun invoke(
        request: LivenessDecisionRequest
    ): Flow<DataStatus<LivenessDecisionResponse>> = flow {
        emit(DataStatus.loading())

        val result = repository.submitLivenessDecision(request)
        val body = result.body()

        if (result.isSuccessful && body != null) {
            emit(DataStatus.success(body))
        } else {
            val detail = result.message().ifBlank { "tidak ada pesan dari server" }
            emit(DataStatus.error("HTTP ${result.code()} - $detail"))
        }
    }
        // Gagal koneksi, timeout, DNS, JSON rusak - semuanya mendarat di sini.
        .catch { emit(DataStatus.error(it.message ?: it.javaClass.simpleName)) }
        .flowOn(Dispatchers.IO)
}
