package com.mtt.presentation.ui.screens.goals

import com.mtt.jaapmala.data.local.entity.GoalStatus

data class GoalUiModel(
    val name: String,
    val current: Int,
    val target: Int,
    val endDate: Long?,
    val jaapName: String?,
    val id : Int = 0,
    val jaapId:Int =0,
    val status: GoalStatus
) {
    val progress: Float
        get() = if (target == 0) 0f else (current.toFloat() / target).coerceIn(0f, 1f)
}