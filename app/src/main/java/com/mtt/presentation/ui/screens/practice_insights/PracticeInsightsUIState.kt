package com.mtt.presentation.ui.screens.practice_insights

import com.mtt.jaapmala.domain.model.ai.PracticeInsights

data class PracticeInsightsUiState(
    val insights: PracticeInsights? = null,
    val isLoading: Boolean = false,
    val error: String? = null,
    val aiInsight: String? = null,
    val isAiInsightLoading: Boolean = false,
    val aiInsightError: String? = null
)

