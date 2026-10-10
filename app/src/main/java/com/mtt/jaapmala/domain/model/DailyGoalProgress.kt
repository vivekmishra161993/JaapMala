package com.mtt.jaapmala.domain.model

data class DailyGoalProgress(
    val goalId: Int,
    val jaapId: Int,
    val targetMalas: Int,
    val completedMalas: Int,
    val remainingMalas: Int,
    val progress: Float,
    val status: DailyGoalDayStatus
)