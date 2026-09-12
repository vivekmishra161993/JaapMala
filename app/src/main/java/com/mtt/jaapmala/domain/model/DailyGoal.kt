package com.mtt.jaapmala.domain.model

data class DailyGoal(
    val id: Int,
    val jaapId: Int,
    val targetMalas: Int,
    val startDate: String,
    val endDate: String?,
    val isActive: Boolean
)