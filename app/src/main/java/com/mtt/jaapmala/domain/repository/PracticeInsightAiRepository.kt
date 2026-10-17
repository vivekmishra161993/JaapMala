package com.mtt.jaapmala.domain.repository

import com.mtt.jaapmala.domain.model.ai.PracticeInsights

interface PracticeInsightAiRepository {

    suspend fun generateInsight(
        insights: PracticeInsights
    ): String
}