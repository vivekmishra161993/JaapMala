package com.mtt.presentation.ui.screens.daily_goal

sealed interface DailyGoalEffect {

    data object GoalCreated : DailyGoalEffect

    data object GoalDeactivated : DailyGoalEffect

    data class ShowError(
        val message: String
    ) : DailyGoalEffect
}