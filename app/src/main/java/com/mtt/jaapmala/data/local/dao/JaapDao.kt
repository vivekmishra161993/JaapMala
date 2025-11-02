package com.mtt.jaapmala.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.mtt.jaapmala.data.local.entity.JaapEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface JaapDao {
    @Query("SELECT * from jaaps")
    fun getAllMantras(): Flow<List<JaapEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMantra(jaapEntity: JaapEntity): Long

    @Update
    suspend fun updateJaap(jaap: JaapEntity)

    @Query("SELECT * from jaaps WHERE id = :id")
    fun getMantra(id: Int): Flow<JaapEntity>

    @Query("SELECT * FROM jaaps")
    suspend fun getAllMantrasOnce(): List<JaapEntity>
    @Delete
    suspend fun deleteJaap(jaap: JaapEntity)

    @Query("UPDATE jaaps SET name = :newName WHERE id = :jaapId")
    suspend fun updateJaapName(jaapId: Int, newName: String)
}