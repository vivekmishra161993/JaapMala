package com.mtt.presentation.ui.screens.add_daily_goals

data class AddDailyGoalUIState(
    val selectedJaapId: Int = 0,
    val selectedJaapName: String = "",
    val targetMalas: String = "",
    val jaapError: String? = null,
    val targetMalasError: String? = null,
    val error: String? = null,
    val isFormValid: Boolean = false
)