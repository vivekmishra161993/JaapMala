package com.mtt.presentation.ui.screens.add_goal

data class AddGoalUIState(
    val selectedJaapId: Int = 0,
    val selectedJaapName: String = "",
    val goalName: String = "",
    val targetMalas: String = "",
    val endDate: Long? = null,

    val jaapError: String? = null,
    val nameError: String? = null,
    val targetMalasError: String? = null,
    val dateError: String? = null,

    val isFormValid: Boolean = false
)
