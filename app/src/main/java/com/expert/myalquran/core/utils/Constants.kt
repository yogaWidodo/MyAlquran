package com.expert.myalquran.core.utils

object Constants {
    const val BASE_URL = "https://equran.id/api/v2/"
    const val BASE_URL_NEWS = "https://newsapi.org/v2/"

    /**
     * Stub backend latihan VIDA (tools/liveness-stub-server.py).
     *
     * Kalau IP mesinmu berubah, ubah di sini DAN di
     * res/xml/network_security_config.xml - kalau hanya salah satu, koneksinya
     * ditolak Android dengan pesan cleartext yang membingungkan.
     *
     * Dari emulator Android, host loopback-nya http://10.0.2.2:8090/
     */
    const val BASE_URL_VIDA = "http://192.168.1.151:8085/"
}