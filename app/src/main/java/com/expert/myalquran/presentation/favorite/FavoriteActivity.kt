package com.expert.myalquran.presentation.favorite

import android.app.ProgressDialog.show
import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.accessibility.AccessibilityEventCompat.setAction
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.expert.myalquran.R
import com.expert.myalquran.databinding.ActivityFavoriteBinding
import com.expert.myalquran.domain.surah.model.response.surahResponse.surah.SurahResponse
import com.expert.myalquran.presentation.detail.DetailActivity
import com.expert.myalquran.presentation.ui.FavoriteSurahAdapter
import com.google.android.material.snackbar.Snackbar
import org.koin.android.ext.android.inject

class FavoriteActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFavoriteBinding
    private val favoriteSurahAdapter: FavoriteSurahAdapter by lazy { FavoriteSurahAdapter() }
    private val viewModel by inject<FavoriteViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityFavoriteBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        setupRecyclerView()

        val itemTouchHelperCallback = object :ItemTouchHelper.SimpleCallback(
            ItemTouchHelper.UP or ItemTouchHelper.DOWN,
            ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
        ){
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return true
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                val surah = favoriteSurahAdapter.differ.currentList[position]
                viewModel.delete(surah)
                Snackbar.make(binding.root, "Delete Surah", Snackbar.LENGTH_LONG).apply {
                    setAction("Undo") {
                        viewModel.saveSurah(surah)
                    }
                    show()
                }
            }

        }

        ItemTouchHelper(itemTouchHelperCallback).apply {
            attachToRecyclerView(binding.rvFavorite)
        }

        favoriteSurahAdapter.setOnItemClickCallback(object : FavoriteSurahAdapter.OnItemClickCallback {
            override fun onItemClicked(data: SurahResponse.DataItem) {
                val intent = Intent(this@FavoriteActivity, DetailActivity::class.java)
                intent.putExtra(DetailActivity.EXTRA_DATA, data)
                startActivity(intent)
            }
        })


        viewModel.getAllSurah().observe(this) {
            favoriteSurahAdapter.differ.submitList(it)
        }

    }


    private fun setupRecyclerView() = binding.rvFavorite.apply {
        layoutManager = LinearLayoutManager(this@FavoriteActivity)
        adapter = favoriteSurahAdapter
    }
}