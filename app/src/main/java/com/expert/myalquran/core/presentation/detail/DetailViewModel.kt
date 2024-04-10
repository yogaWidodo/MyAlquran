package com.expert.myalquran.core.presentation.detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.expert.myalquran.core.data.source.SurahRepository
import com.expert.myalquran.core.data.source.remote.response.detailsurah.DetailResponse
import com.expert.myalquran.core.utils.DataStatus
import kotlinx.coroutines.launch

class DetailViewModel(private val repository: SurahRepository) : ViewModel() {
    private val _detailSurahList = MutableLiveData<DataStatus<List<DetailResponse.Data.AyatItem>>>()
    val detailSurahList: LiveData<DataStatus<List<DetailResponse.Data.AyatItem>>>
        get() = _detailSurahList

    fun getDetailSurah(nomor: Int) = viewModelScope.launch {
        repository.getDetailSurah(nomor).collect{
            _detailSurahList.value = it
        }
    }
}