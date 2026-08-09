package com.expert.myalquran.presentation.vida

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.expert.myalquran.core.utils.DataStatus
import com.expert.myalquran.domain.vida.model.LivenessDecisionRequest
import com.expert.myalquran.domain.vida.model.LivenessDecisionResponse
import kotlinx.coroutines.launch
import com.expert.myalquran.domain.vida.usecase.SubmitLivenessDecisionUseCase

class VidaViewModel(
    private val submitLivenessDecision: SubmitLivenessDecisionUseCase
) : ViewModel() {

    private val _decision = MutableLiveData<DataStatus<LivenessDecisionResponse>>()
    val decision: LiveData<DataStatus<LivenessDecisionResponse>>
        get() = _decision

    /** Payload terakhir, supaya tombol coba lagi bisa mengirim ulang yang sama. */
    private var lastRequest: LivenessDecisionRequest? = null

    fun submit(request: LivenessDecisionRequest) {
        lastRequest = request
        dispatch(request)
    }

    fun retryLast() {
        lastRequest?.let { dispatch(it) }
    }

    private fun dispatch(request: LivenessDecisionRequest) = viewModelScope.launch {
        submitLivenessDecision(request).collect { _decision.value = it }
    }
}
