package com.expert.myalquran.core.data.source.remote.response.surah


data class SurahResponse(
    val code: Int,
    val data: List<DataItem>,
    val message: String
) {
    data class DataItem(
        val jumlahAyat: Int,
        val nama: String,
        val tempatTurun: String,
        val arti: String,
        val deskripsi: String,
        val nomor: Int,
        val namaLatin: String,
    )
}








