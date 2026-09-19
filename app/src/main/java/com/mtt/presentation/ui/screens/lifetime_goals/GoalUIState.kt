package com.mtt.presentation.ui.screens.lifetime_goals

sealed class GoalUIState {
    object Loading : GoalUIState()
    data class Success(val goals: List<GoalUiModel>) : GoalUIState()
    object Empty : GoalUIState()
}