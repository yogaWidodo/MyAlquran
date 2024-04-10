package com.expert.myalquran.core.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.expert.myalquran.R
import com.expert.myalquran.core.data.source.remote.response.detailsurah.DetailResponse
import com.expert.myalquran.core.data.source.remote.response.surah.SurahResponse
import com.expert.myalquran.databinding.ListItemBinding

class SurahAdapter : RecyclerView.Adapter<SurahAdapter.SurahViewHolder>() {

    private lateinit var onItemClickCallback: OnItemClickCallback
    private val surahList = ArrayList<SurahResponse.DataItem>()
    private val fullList = ArrayList<SurahResponse.DataItem>()

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

    fun updateList(newList: List<SurahResponse.DataItem>){
        surahList.clear()
        surahList.addAll(newList)
        fullList.clear()
        fullList.addAll(newList)
        notifyDataSetChanged()
    }

    fun filterList(search:String){
        surahList.clear()
        for (item in fullList){
            if (item.namaLatin.lowercase().contains(search.lowercase()) == true){
                surahList.add(item)
            }
        }
        notifyDataSetChanged()
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
            Cardview.setOnClickListener{
                onItemClickCallback.onItemClicked(surah)
            }
        }

    }

    interface OnItemClickCallback {
        fun onItemClicked(data: SurahResponse.DataItem)
    }
}
