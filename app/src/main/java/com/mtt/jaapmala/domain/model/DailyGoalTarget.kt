package com.mtt.jaapmala.domain.model

data class DailyGoalTarget(
    val id: Int,
    val dailyGoalId: Int,
    val targetMalas: Int,
    val effectiveFrom: String,
    val effectiveTo: String?
)