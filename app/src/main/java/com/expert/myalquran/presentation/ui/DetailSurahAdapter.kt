package com.expert.myalquran.presentation.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.expert.myalquran.domain.surah.model.response.surahResponse.detailsurah.DetailResponse
import com.expert.myalquran.databinding.ListItemDetailBinding

class DetailSurahAdapter : RecyclerView.Adapter<DetailSurahAdapter.MyViewHolder>() {

    inner class MyViewHolder(val binding: ListItemDetailBinding) :
        RecyclerView.ViewHolder(binding.root)


    private val diffCallback = object : DiffUtil.ItemCallback<DetailResponse.Data.AyatItem>() {
        override fun areItemsTheSame(
            oldItem: DetailResponse.Data.AyatItem,
            newItem: DetailResponse.Data.AyatItem
        ): Boolean {
            return oldItem.nomorAyat == newItem.nomorAyat
        }

        override fun areContentsTheSame(
            oldItem: DetailResponse.Data.AyatItem,
            newItem: DetailResponse.Data.AyatItem
        ): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this, diffCallback)
    private var detailSurah: List<DetailResponse.Data.AyatItem>
        get() = differ.currentList
        set(value) {
            differ.submitList(value)
        }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding =
            ListItemDetailBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding)
    }

    override fun getItemCount(): Int = detailSurah.size
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.binding.apply {
            val detailSurah = detailSurah[position]
            nomorAyat.text = detailSurah.nomorAyat.toString()
            teksArab.text = detailSurah.teksArab
            teksLatin.text = detailSurah.teksLatin
            teksIndonesia.text=detailSurah.teksIndonesia
        }
    }
}