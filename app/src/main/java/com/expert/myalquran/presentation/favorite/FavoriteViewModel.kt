package com.expert.myalquran.presentation.favorite

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.expert.myalquran.domain.model.response.surah.SurahResponse
import com.expert.myalquran.domain.deleteayat_usecase.GetDeleteAyatUseCase
import com.expert.myalquran.domain.getayat_usecase.GetAyatUseCase
import com.expert.myalquran.domain.saveayat_usecase.GetSaveAyatUseCase
import kotlinx.coroutines.launch


class FavoriteViewModel(
    private val getUseCase: GetAyatUseCase,
    private val getDeleteAyatUseCase: GetDeleteAyatUseCase,
    private val getSaveAyatUseCase: GetSaveAyatUseCase
) : ViewModel() {
    fun getAllSurah(): LiveData<List<SurahResponse.DataItem>> {
        return getUseCase.getAllAyat()
    }

    fun delete(ayat: SurahResponse.DataItem) {
        viewModelScope.launch {
            getDeleteAyatUseCase.delete(ayat)
        }
    }

    fun saveSurah(surah: SurahResponse.DataItem) {
        viewModelScope.launch {
            getSaveAyatUseCase.upsert(surah)
        }
    }
}