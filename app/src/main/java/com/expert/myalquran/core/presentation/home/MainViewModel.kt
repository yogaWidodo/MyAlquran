package com.expert.myalquran.core.presentation.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.expert.myalquran.core.data.source.SurahRepository
import com.expert.myalquran.core.data.source.remote.response.surah.SurahResponse
import com.expert.myalquran.core.utils.DataStatus
import kotlinx.coroutines.launch


class MainViewModel(private val repository: SurahRepository) : ViewModel() {
    private val _surahList = MutableLiveData<DataStatus<List<SurahResponse.DataItem>>>()
    val surahList: LiveData<DataStatus<List<SurahResponse.DataItem>>>
        get() = _surahList

    fun getSurah() = viewModelScope.launch {
        repository.getSurah().collect{
            _surahList.value = it
        }
    }

}