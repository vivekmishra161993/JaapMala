package com.mtt.jaapmala.domain.repository

import com.mtt.jaapmala.data.local.entity.JaapEntity
import com.mtt.jaapmala.data.model.MantraDto
import kotlinx.coroutines.flow.Flow

interface JaapRepository {
    fun getAllMantras(): Flow<List<JaapEntity>>
    suspend fun insertMantra(name: String, date: String,size:Int): MantraDto
    suspend fun getMantra(id: Int): Flow<JaapEntity>
    suspend fun updateJaap(jaapEntity: JaapEntity)
    suspend fun resetTodayCountsIfNeeded()
    suspend fun deleteJaap(jaap: JaapEntity)
    suspend fun updateJaapName(jaapId: Int, newName: String)

}