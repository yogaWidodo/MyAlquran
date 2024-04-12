package com.expert.myalquran.presentation.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.expert.myalquran.domain.model.response.surah.SurahResponse
import com.expert.myalquran.core.utils.DataStatus
import com.expert.myalquran.domain.surah_usecase.GetSurahUseCase
import kotlinx.coroutines.launch
import retrofit2.Response


class MainViewModel(private val getUseCase: GetSurahUseCase) : ViewModel() {
    private val _surahList = MutableLiveData<DataStatus<List<SurahResponse.DataItem>>>()
    val surahList: LiveData<DataStatus<List<SurahResponse.DataItem>>>
        get() = _surahList

    private val _filteredSurah = MutableLiveData<DataStatus<List<SurahResponse.DataItem>>>()
    val filteredSurah: LiveData<DataStatus<List<SurahResponse.DataItem>>>
        get() = _filteredSurah




    init {
        getSurah()
    }

    private fun getSurah() = viewModelScope.launch {
        getUseCase().collect {
            _surahList.value = it
        }
    }


        fun filterSurah(query: String) {
            val listSurah = _surahList.value?.data
            if (listSurah != null) {
                val filteredSurah = listSurah.filter {
                    it.nama.contains(query, ignoreCase = true) ||
                            it.namaLatin.contains(query, ignoreCase = true) ||
                            it.nomor.toString().contains(query, ignoreCase = true)
                }
                _filteredSurah.value = DataStatus.success(filteredSurah)
            }

        }

}