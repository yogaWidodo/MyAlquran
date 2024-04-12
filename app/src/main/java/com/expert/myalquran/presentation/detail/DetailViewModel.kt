package com.expert.myalquran.presentation.detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.expert.myalquran.domain.model.response.detailsurah.DetailResponse
import com.expert.myalquran.core.utils.DataStatus
import com.expert.myalquran.domain.detailsurah_usecase.GetDetailSurahUseCase
import com.expert.myalquran.domain.model.response.surah.SurahResponse
import com.expert.myalquran.domain.saveayat_usecase.GetSaveAyatUseCase
import kotlinx.coroutines.launch

class DetailViewModel(private val getUseCase: GetDetailSurahUseCase,private val getSaveAyatUseCase: GetSaveAyatUseCase) : ViewModel() {
    private val _detailSurahList = MutableLiveData<DataStatus<List<DetailResponse.Data.AyatItem>>>()
    val detailSurahList: LiveData<DataStatus<List<DetailResponse.Data.AyatItem>>>
        get() = _detailSurahList



    fun getDetailSurah(nomor: Int) = viewModelScope.launch {
        getUseCase(nomor).collect {
            _detailSurahList.value = it
        }
    }

    fun setFavorite (ayat: SurahResponse.DataItem) = viewModelScope.launch {
        getSaveAyatUseCase.upsert(ayat)
    }
}