package com.mtt.presentation.ui.screens.practice_insights

sealed interface PracticeInsightsIntent {
    data object GenerateAiInsight : PracticeInsightsIntent
    data object RetryAiInsight : PracticeInsightsIntent
}