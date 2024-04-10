package com.expert.myalquran.core.presentation.home

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.SearchView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.expert.myalquran.R
import com.expert.myalquran.core.data.source.remote.response.surah.SurahResponse
import com.expert.myalquran.core.ui.SurahAdapter
import com.expert.myalquran.core.utils.DataStatus
import com.expert.myalquran.databinding.ActivityMainBinding
import com.expert.myalquran.core.presentation.detail.DetailActivity
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject

class MainActivity : AppCompatActivity() {

    private val surahAdapter: SurahAdapter by lazy { SurahAdapter() }
    private lateinit var binding: ActivityMainBinding
    private val viewModel by inject<MainViewModel>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        appCompact()
        setupRecyclerView()
        surahAdapter.setOnItemClickCallback(object : SurahAdapter.OnItemClickCallback {
            override fun onItemClicked(data: SurahResponse.DataItem) {
                val intent = Intent(this@MainActivity, DetailActivity::class.java)
                intent.putExtra(DetailActivity.EXTRA_ARAB, data.nama)
                intent.putExtra(DetailActivity.EXTRA_LATIN, data.namaLatin)
                intent.putExtra(DetailActivity.EXTRA_AYAT, data.jumlahAyat)
                intent.putExtra(DetailActivity.EXTRA_NOMOR, data.nomor)
                intent.putExtra(DetailActivity.EXTRA_ARTI, data.arti)
                intent.putExtra(DetailActivity.EXTRA_DESC, data.deskripsi)
                intent.putExtra(DetailActivity.EXTRA_TEMPAT, data.tempatTurun)
                startActivity(intent)
            }
        })
        getSurah()
        binding.swipeRefreshLayout.setOnRefreshListener {
            getSurah()
        }

    }

    private fun getSurah() {
        lifecycleScope.launch {
            viewModel.getSurah()
            viewModel.surahList.observe(this@MainActivity) {
                when (it.status) {
                    DataStatus.Status.LOADING -> {
                        binding.swipeRefreshLayout.isRefreshing = true
                    }

                    DataStatus.Status.SUCCESS -> {
                        surahAdapter.differ.submitList(it.data)
                        binding.swipeRefreshLayout.isRefreshing = false
                    }

                    DataStatus.Status.ERROR -> {
                        binding.swipeRefreshLayout.isRefreshing = false
                        Toast.makeText(
                            this@MainActivity,
                            "There is something wrong!",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }
    }


    private fun appCompact() {
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun setupRecyclerView() = binding.recyclerView.apply {
        layoutManager = LinearLayoutManager(this@MainActivity)
        adapter = surahAdapter
    }
}