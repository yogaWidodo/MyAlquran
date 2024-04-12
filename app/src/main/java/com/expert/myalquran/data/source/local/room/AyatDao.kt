package com.expert.myalquran.data.source.local.room

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.expert.myalquran.domain.model.response.surah.SurahResponse

@Dao
interface AyatDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(ayat: SurahResponse.DataItem): Long

    @Delete
    suspend fun delete(ayat: SurahResponse.DataItem)

    @Query("SELECT * FROM surah")
    fun getAllAyat(): LiveData<List<SurahResponse.DataItem>>
}