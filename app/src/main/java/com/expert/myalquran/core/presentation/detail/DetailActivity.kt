package com.expert.myalquran.core.presentation.detail

import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.expert.myalquran.R
import com.expert.myalquran.core.ui.DetailSurahAdapter
import com.expert.myalquran.core.utils.DataStatus
import com.expert.myalquran.databinding.ActivityDetailBinding
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject

class DetailActivity : AppCompatActivity() {

    private val detailSurahAdapter: DetailSurahAdapter by lazy { DetailSurahAdapter() }
    private lateinit var binding: ActivityDetailBinding
    private val viewModel by inject<DetailViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        appCompact()
        setupRecyclerView()
        val arab = intent.getStringExtra(EXTRA_ARAB).toString()
        val latin = intent.getStringExtra(EXTRA_LATIN).toString()
        val ayat = intent.getIntExtra(EXTRA_AYAT, 0)
        val arti = intent.getStringExtra(EXTRA_ARTI).toString()
        val tempat = intent.getStringExtra(EXTRA_TEMPAT).toString()
        val nomor = intent.getIntExtra(EXTRA_NOMOR, 1)
        headerAyat(arab, latin, ayat, arti, tempat)
        getDetailSurah(nomor)
        binding.swipeRefreshLayout.setOnRefreshListener {
            getDetailSurah(nomor)
        }


    }


    private fun headerAyat(arab: String, latin: String, ayat: Int, arti: String, tempat: String) {
        binding.judulLatin.text = latin
        binding.jumlahAyat.text = getString(R.string.ayat, ayat)
        binding.arti.text = arti
        binding.tempatTurun.text = tempat
        binding.judulArab.text = arab
    }

    private fun appCompact() {
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun getDetailSurah(nomor: Int) {
        lifecycleScope.launch {
            viewModel.getDetailSurah(nomor)
            viewModel.detailSurahList.observe(this@DetailActivity) {
                when (it.status) {
                    DataStatus.Status.LOADING -> {
                        binding.swipeRefreshLayout.isRefreshing = true
                    }

                    DataStatus.Status.SUCCESS -> {
                        binding.swipeRefreshLayout.isRefreshing = false
                        detailSurahAdapter.differ.submitList(it.data)
                    }

                    DataStatus.Status.ERROR -> {
                        binding.swipeRefreshLayout.isRefreshing = false
                        Log.e("ErrorSurah", it.message.toString())
                    }
                }
            }
        }
    }

    private fun setupRecyclerView() = binding.recyclerView.apply {
        layoutManager = LinearLayoutManager(this@DetailActivity)
        adapter = detailSurahAdapter
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