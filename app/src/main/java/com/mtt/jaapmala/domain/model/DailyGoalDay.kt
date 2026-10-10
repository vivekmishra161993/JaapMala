package com.mtt.jaapmala.domain.model

enum class DailyGoalDayStatus {
    COMPLETED,
    PARTIAL,
    PENDING,
    MISSED
}

data class DailyGoalDay(
    val date: String,
    val targetMalas: Int,
    val completedMalas: Int,
    val progress: Float,
    val status: DailyGoalDayStatus
)