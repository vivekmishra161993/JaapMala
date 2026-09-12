package com.mtt.jaapmala.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.mtt.jaapmala.data.local.entity.JaapHistoryEntity
import com.mtt.jaapmala.domain.model.JaapHistory
import kotlinx.coroutines.flow.Flow

@Dao
interface JaapHistoryDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(history: JaapHistoryEntity)

    @Query("SELECT * FROM jaap_history WHERE jaapId = :jaapId ORDER BY date DESC")
    fun getHistoryForJaap(jaapId: Int): Flow<List<JaapHistory>>

    @Query("""
    SELECT * FROM jaap_history
    WHERE jaapId = :jaapId
    AND date = :date
    LIMIT 1
""")
    fun getHistoryForDate(
        jaapId: Int,
        date: String
    ): Flow<JaapHistory?>

    @Query("""
    SELECT * FROM jaap_history
    WHERE jaapId = :jaapId
    AND date BETWEEN :startDate AND :endDate
    ORDER BY date DESC
""")
    fun getHistoryBetweenDates(
        jaapId: Int,
        startDate: String,
        endDate: String
    ): Flow<List<JaapHistoryEntity>>
}