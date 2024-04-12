package com.expert.myalquran.domain.model.response.surah

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize


@Parcelize
data class SurahResponse(
    val code: Int,
    val data: List<DataItem>,
    val message: String
) :Parcelable{
    @Parcelize
    @Entity(tableName = "surah")
    data class DataItem(
        val jumlahAyat: Int,
        val nama: String,
        val tempatTurun: String,
        val arti: String,
        val deskripsi: String,
        @PrimaryKey
        val nomor: Int,
        val namaLatin: String,
    ):Parcelable
}








