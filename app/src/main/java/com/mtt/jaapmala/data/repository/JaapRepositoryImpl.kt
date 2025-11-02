package com.mtt.jaapmala.data.repository

import com.mtt.jaapmala.data.local.dao.JaapDao
import com.mtt.jaapmala.data.local.entity.JaapEntity
import com.mtt.jaapmala.data.model.MantraDto
import com.mtt.jaapmala.domain.repository.JaapRepository
import com.mtt.jaapmala.util.toMantraDto
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate
import javax.inject.Inject

class JaapRepositoryImpl @Inject constructor(private val dao: JaapDao) : JaapRepository {
    override fun getAllMantras(): Flow<List<JaapEntity>> {
        return dao.getAllMantras()
    }

    override suspend fun insertMantra(name: String, date: String,size:Int): MantraDto {
        val entity = JaapEntity(
            name = name,
            date = date,
            todayCount = 0,
            todayMalaCount = 0,
            lifetimeCount = 0,
            lifetimeMalaCount = 0,
            malaSize = size
        )
        val id = dao.insertMantra(entity).toInt()
        val mantraDto = entity.toMantraDto()
        mantraDto.id = id
        return mantraDto
    }

    override suspend fun getMantra(id: Int): Flow<JaapEntity> {
        return dao.getMantra(id)
    }

    override suspend fun updateJaap(jaapEntity: JaapEntity) {
        dao.updateJaap(jaapEntity)
    }

    override suspend fun resetTodayCountsIfNeeded() {
        val today = LocalDate.now().toString() // Get today's date in string format
        val jaaps = dao.getAllMantrasOnce() // Fetch all mantras from the database
        jaaps.forEach { jaap ->
            // Only reset if the stored date is not today
            if (jaap.date != today) {
                // If it's a different day, reset todayCount and todayMalaCount, and update the date
                val updated = jaap.copy(todayCount = 0, todayMalaCount = 0, date = today)
                dao.updateJaap(updated) // Update the entity in the database
            }
        }

    }
    override suspend fun deleteJaap(jaap: JaapEntity) {
        dao.deleteJaap(jaap)
    }
    override suspend fun updateJaapName(jaapId: Int, newName: String) {
        dao.updateJaapName(jaapId, newName)
    }

}