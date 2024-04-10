package com.expert.myalquran.detail

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.expert.myalquran.R
import com.expert.myalquran.core.utils.DataStatus
import com.expert.myalquran.databinding.ActivityDetailBinding
import kotlinx.coroutines.launch
import org.jsoup.Jsoup
import org.koin.android.ext.android.inject

class DetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        appCompact()
        val arab = intent.getStringExtra(EXTRA_ARAB).toString()
        val latin = intent.getStringExtra(EXTRA_LATIN).toString()
        val ayat = intent.getIntExtra(EXTRA_AYAT,0)
        val arti = intent.getStringExtra(EXTRA_ARTI).toString()
        val desc = intent.getStringExtra(EXTRA_DESC).toString()
        val tempat = intent.getStringExtra(EXTRA_TEMPAT).toString()


        headerAyat(arab, latin, ayat, arti, desc, tempat)



    }



    private fun headerAyat(arab: String, latin: String, ayat: Int,arti: String, desc: String, tempat: String) {
        binding.judulArab.text = arab
        binding.judulLatin.text = latin
        binding.jumlahAyat.text = getString(R.string.ayat, ayat)
        binding.arti.text = arti
        binding.tempatTurun.text = tempat
    }

    private fun appCompact() {
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun cleanHTML(text: String): String {
    return Jsoup.parse(text).text()
    }

    companion object {
        const val EXTRA_ARAB = "extra_surah"
        const val EXTRA_LATIN = "extra_latin"
        const val EXTRA_AYAT = "extra_ayat"
        const val EXTRA_NOMOR = "extra_nomor"
        const val EXTRA_ARTI = "extra_arti"
        const val EXTRA_DESC = "extra_desc"
        const val EXTRA_TEMPAT = "extra_tempat"
    }
}