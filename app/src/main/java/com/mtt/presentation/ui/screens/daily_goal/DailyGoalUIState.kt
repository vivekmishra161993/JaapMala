package com.mtt.presentation.ui.screens.daily_goal

import com.mtt.jaapmala.domain.model.DailyGoalDay

data class DailyGoalUiState(
    val isLoading: Boolean = false,
    val goals: List<DailyGoalItemUiModel> = emptyList(),
    val selectedGoalHistory: List<DailyGoalDay> = emptyList(),
    val error: String? = null
)