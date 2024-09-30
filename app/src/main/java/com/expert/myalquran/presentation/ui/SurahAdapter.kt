package com.expert.myalquran.presentation.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.expert.myalquran.R
import com.expert.myalquran.domain.surah.model.response.surahResponse.detailsurah.DetailResponse
import com.expert.myalquran.domain.surah.model.response.surahResponse.surah.SurahResponse
import com.expert.myalquran.databinding.ListItemBinding

class SurahAdapter : RecyclerView.Adapter<SurahAdapter.SurahViewHolder>() {

    private lateinit var onItemClickCallback: OnItemClickCallback


    fun setOnItemClickCallback(onItemClickCallback: OnItemClickCallback) {
        this.onItemClickCallback = onItemClickCallback
    }

    inner class SurahViewHolder(val binding: ListItemBinding) :
        RecyclerView.ViewHolder(binding.root)

    private val diffCallback = object : DiffUtil.ItemCallback<SurahResponse.DataItem>(){
        override fun areItemsTheSame(
            oldItem: SurahResponse.DataItem, newItem: SurahResponse.DataItem
        ): Boolean {
            return oldItem.nomor == newItem.nomor
        }

        override fun areContentsTheSame(
            oldItem: SurahResponse.DataItem, newItem: SurahResponse.DataItem
        ): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this, diffCallback)
    var surah: List<SurahResponse.DataItem>
        get() = differ.currentList
        set(value) {
            differ.submitList(value)
        }





    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SurahViewHolder {
        val binding =
            ListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SurahViewHolder(binding)
    }

    override fun getItemCount(): Int = surah.size

    override fun onBindViewHolder(holder: SurahViewHolder, position: Int) {
        holder.binding.apply {
            val surah = surah[position]
            judulArab.text = surah.nama
            judulLatin.text = surah.namaLatin
            jumlahAyat.text = holder.itemView.context.getString(R.string.ayat, surah.jumlahAyat)
            judulArabBig.text = surah.nama
            Cardview.setOnClickListener{
                onItemClickCallback.onItemClicked(surah)
            }
        }

    }

    interface OnItemClickCallback {
        fun onItemClicked(data: SurahResponse.DataItem)
    }
}
