package com.mtt.jaapmala.domain.usecase.ai

import com.mtt.jaapmala.domain.model.ai.PracticeInsights
import com.mtt.jaapmala.domain.repository.PracticeInsightAiRepository
import javax.inject.Inject

class GenerateAiPracticeInsightUseCase @Inject constructor(
    private val repository: PracticeInsightAiRepository
) {
    suspend operator fun invoke(
        insights: PracticeInsights
    ): String {
        return repository.generateInsight(insights)
    }
}