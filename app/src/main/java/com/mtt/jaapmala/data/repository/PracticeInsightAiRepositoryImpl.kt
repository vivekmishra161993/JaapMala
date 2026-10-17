package com.mtt.jaapmala.data.repository

import com.mtt.jaapmala.domain.model.ai.PracticeInsights
import com.mtt.jaapmala.domain.repository.PracticeInsightAiRepository
import javax.inject.Inject

class PracticeInsightAiRepositoryImpl @Inject constructor() : PracticeInsightAiRepository {
    override suspend fun generateInsight(insights: PracticeInsights): String {
        return "Keep up your consistent practice!"
    }
}
