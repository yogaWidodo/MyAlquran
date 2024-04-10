package com.expert.myalquran.core.data.source.remote.response

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
        val ayat:List<AyatItem>,
        val namaLatin: String,
    )
        data class AyatItem(
            val teksArab: String,
            val teksLatin: String,
            val nomorAyat: Int,
            val teksIndonesia: String
        )

}








