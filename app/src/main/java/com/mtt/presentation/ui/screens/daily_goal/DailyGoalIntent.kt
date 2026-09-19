package com.mtt.presentation.ui.screens.daily_goal

sealed interface DailyGoalIntent {
    data class LoadGoals(
        val jaapId: Int? = null
    ) : DailyGoalIntent

    data class CreateGoal(
        val jaapId: Int,
        val targetMalas: Int
    ) : DailyGoalIntent

    data class DeactivateGoal(
        val goalId: Int
    ) : DailyGoalIntent

    data class LoadHistory(
        val goalId: Int
    ) : DailyGoalIntent

    data object ClearError : DailyGoalIntent
}