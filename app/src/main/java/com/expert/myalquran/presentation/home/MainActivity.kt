package com.expert.myalquran.presentation.home

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
import com.expert.myalquran.domain.model.response.surah.SurahResponse
import com.expert.myalquran.presentation.ui.SurahAdapter
import com.expert.myalquran.core.utils.DataStatus
import com.expert.myalquran.databinding.ActivityMainBinding
import com.expert.myalquran.presentation.detail.DetailActivity
import com.expert.myalquran.presentation.favorite.FavoriteActivity
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
                intent.putExtra(DetailActivity.EXTRA_DATA, data)
                startActivity(intent)
            }
        })
        getSurah()
        binding.swipeRefreshLayout.setOnRefreshListener {
            getSurah()
        }
        binding.floatingActionButton.setOnClickListener {
            val intent = Intent(this@MainActivity, FavoriteActivity::class.java)
            startActivity(intent)
        }

        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                viewModel.filterSurah(query.toString())
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                viewModel.filterSurah(newText.toString())
                return true
            }
        })
        searchSurah()

    }

    private fun searchSurah() {
        viewModel.filteredSurah.observe(this@MainActivity) {
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

    private fun getSurah() {
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