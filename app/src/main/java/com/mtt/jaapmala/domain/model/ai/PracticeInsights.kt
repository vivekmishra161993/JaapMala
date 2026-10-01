package com.mtt.jaapmala.domain.model.ai

data class PracticeInsights(
    val periodDays: Int,
    val totalMalas: Int,
    val averageMalasPerDay: Float,
    val activeDays: Int,
    val currentStreak: Int,
    val longestStreak: Int,
    val trend: PracticeTrend
)

enum class PracticeTrend {
    IMPROVING,
    STABLE,
    DECLINING
}