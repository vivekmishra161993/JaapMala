package com.mtt.presentation.ui.screens.practice_insights

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
import javax.inject.Inject

@HiltViewModel
class PracticeInsightsViewModel @Inject constructor(
    private val getPracticeInsightsUseCase: GetPracticeInsightsUseCase
) : ViewModel() {

    private val _uiState =
        MutableStateFlow(PracticeInsightsUiState())

    val uiState: StateFlow<PracticeInsightsUiState> =
        _uiState.asStateFlow()

    private var insightsJob: Job? = null

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
}