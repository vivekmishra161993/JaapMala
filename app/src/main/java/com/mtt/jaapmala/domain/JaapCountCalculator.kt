package com.mtt.jaapmala.domain

import com.mtt.jaapmala.data.local.entity.JaapEntity
import javax.inject.Inject

class JaapCountCalculator @Inject constructor() {

    fun calculateNewCounts(
        jaap: JaapEntity,
        addedCount: Int
    ): JaapEntity {

        val previousTodayCount = jaap.todayCount
        val newTodayCount = previousTodayCount + addedCount

        // Full malas completed before and after this update
        val previousTodayMalas =
            previousTodayCount / jaap.malaSize

        val newTodayMalas =
            newTodayCount / jaap.malaSize

        // Malas completed because of this particular update
        val completedMalas =
            newTodayMalas - previousTodayMalas

        val newLifetimeCount =
            jaap.lifetimeCount + addedCount

        val newLifetimeMalaCount =
            jaap.lifetimeMalaCount + completedMalas

        // Remaining japs after the last completed mala
        val newUiCount =
            newTodayCount % jaap.malaSize

        return jaap.copy(
            todayCount = newTodayCount,
            todayMalaCount = newTodayMalas,
            lifetimeCount = newLifetimeCount,
            lifetimeMalaCount = newLifetimeMalaCount,
            count = newUiCount
        )
    }
}

