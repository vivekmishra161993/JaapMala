package com.mtt.presentation.ui.screens.goals

data class GoalUiModel(
    val name: String,
    val current: Int,
    val target: Int
) {
    val progress: Float
        get() = if (target == 0) 0f else (current.toFloat() / target).coerceIn(0f, 1f)
}