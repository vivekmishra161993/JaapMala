package com.mtt.presentation.ui.screens.practice_insights

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mtt.jaapmala.domain.usecase.ai.GenerateAiPracticeInsightUseCase
import com.mtt.jaapmala.domain.usecase.ai.GetPracticeInsightsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.coroutines.cancellation.CancellationException

@HiltViewModel
class PracticeInsightsViewModel @Inject constructor(
    private val getPracticeInsightsUseCase: GetPracticeInsightsUseCase,
    private val generateAiPracticeInsightUseCase: GenerateAiPracticeInsightUseCase
) : ViewModel() {

    private val _uiState =
        MutableStateFlow(PracticeInsightsUiState())

    val uiState: StateFlow<PracticeInsightsUiState> =
        _uiState.asStateFlow()

    private var insightsJob: Job? = null
    private var aiInsightJob: Job? = null

    /**
     * Returns the insights
     *
     * @param jaapId the jaap id
     */
    fun loadInsights(jaapId: Int) {

        insightsJob?.cancel()

        insightsJob = getPracticeInsightsUseCase(
            jaapId = jaapId
        )
            .onStart {
                _uiState.update {
                    it.copy(
                        isLoading = true,
                        error = null
                    )
                }
            }
            .onEach { insights ->
                _uiState.update {
                    it.copy(
                        insights = insights,
                        isLoading = false,
                        error = null
                    )
                }
            }
            .catch { throwable ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = throwable.message
                            ?: "Unable to load practice insights"
                    )
                }
            }
            .launchIn(viewModelScope)
    }

    /**
     * On intent
     *
     * @param intent
     */
    fun onIntent(intent: PracticeInsightsIntent) {
        when (intent) {
            PracticeInsightsIntent.GenerateAiInsight,
            PracticeInsightsIntent.RetryAiInsight -> {
                generateAiInsight()
            }
        }
    }

    /**
     * Creates a new AI insight
     *
     * @throws exception
     */
    private fun generateAiInsight() {
        val insights = _uiState.value.insights ?: return

        aiInsightJob?.cancel()

        aiInsightJob = viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isAiInsightLoading = true,
                    aiInsightError = null
                )
            }

            try {
                val result =
                    generateAiPracticeInsightUseCase(insights)

                _uiState.update {
                    it.copy(
                        aiInsight = result,
                        isAiInsightLoading = false,
                        aiInsightError = null
                    )
                }
            } catch (exception: CancellationException) {
                throw exception
            } catch (exception: Exception) {
                _uiState.update {
                    it.copy(
                        isAiInsightLoading = false,
                        aiInsightError =
                            "Unable to generate insights right now."
                    )
                }
            }
        }
    }
}