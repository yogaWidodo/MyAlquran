package com.expert.myalquran.domain.surah.model.response.surahResponse.detailsurah


data class DetailResponse(
    val code: Int,
    val data: Data,
    val message: String
){
	data class Data(
        val jumlahAyat: Int,
        val nama: String,
        val audioFull: AudioFull,
        val suratSebelumnya: Any,
        val tempatTurun: String,
        val ayat: List<AyatItem>,
        val arti: String,
        val deskripsi: String,
        val suratSelanjutnya: SuratSelanjutnya,
        val nomor: Int,
        val namaLatin: String
	){

		data class AyatItem(
            val teksArab: String,
            val teksLatin: String,
            val nomorAyat: Int,
            val audio: Audio,
            val teksIndonesia: String
		)
	}
}

data class Audio(
	val jsonMember01: String,
	val jsonMember02: String,
	val jsonMember03: String,
	val jsonMember04: String,
	val jsonMember05: String
)

data class SuratSebelumnya(
	val jumlahAyat: Int,
	val nama: String,
	val nomor: Int,
	val namaLatin: String
)

data class SuratSelanjutnya(
	val jumlahAyat: Int,
	val nama: String,
	val nomor: Int,
	val namaLatin: String
)



data class AudioFull(
	val jsonMember01: String,
	val jsonMember02: String,
	val jsonMember03: String,
	val jsonMember04: String,
	val jsonMember05: String
)

