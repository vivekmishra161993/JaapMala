package com.mtt.presentation.ui.screens.daily_goal

import com.mtt.jaapmala.domain.model.DailyGoalDayStatus

data class DailyGoalItemUiModel(
    val goalId: Int,
    val jaapId: Int,
    val jaapName: String,
    val targetMalas: Int,
    val completedMalas: Int,
    val remainingMalas: Int,
    val progress: Float,
    val status: DailyGoalDayStatus
)