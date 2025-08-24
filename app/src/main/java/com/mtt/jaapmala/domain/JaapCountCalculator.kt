package com.mtt.jaapmala.domain

import com.mtt.jaapmala.data.local.entity.JaapEntity
import javax.inject.Inject

class JaapCountCalculator @Inject constructor() {
    fun calculateNewCounts(jaap: JaapEntity, addedCount: Int): JaapEntity {
        // Update total count
        val newTotalCount = jaap.todayCount + addedCount

        // Calculate the number of full malas completed
        val newMalaCount = addedCount / jaap.malaSize

        // Track UI count that resets on mala completion
        val newUiCount = if (newTotalCount >= jaap.malaSize) {
            newTotalCount % jaap.malaSize // Reset the UI count after each mala is completed
        } else {
            newTotalCount
        }

        // Update lifetime counts
        val newLifeTimeCount = jaap.lifetimeCount + addedCount
        val newLifeTimeMalaCount = jaap.lifetimeMalaCount + newMalaCount

        return jaap.copy(
            todayCount = newTotalCount,
            todayMalaCount = newTotalCount / jaap.malaSize,  // Number of full malas completed today
            lifetimeCount = newLifeTimeCount,
            lifetimeMalaCount = newLifeTimeMalaCount,
            count = newUiCount  // Remaining count for the current mala
        )
    }
}

