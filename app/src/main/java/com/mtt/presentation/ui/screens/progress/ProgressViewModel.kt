package com.mtt.presentation.ui.screens.progress

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProgressViewModel @Inject constructor() : ViewModel() {
    private val _uiState = MutableStateFlow<ProgressUiState>(ProgressUiState.Loading)
    val uiState: StateFlow<ProgressUiState> = _uiState

    init {
        // Simulate data load
        viewModelScope.launch {
            delay(1000)
            _uiState.value = ProgressUiState.Success(
                ProgressData(
                    totalJaaps = 5,
                    totalCount = 12480,
                    totalMalas = 115,
                    perMantraProgress = listOf(
                        MantraProgress("Gayatri Mantra", 80),
                        MantraProgress("Mahamrityunjaya", 45),
                        MantraProgress("Hanuman Chalisa", 60)
                    )
                )
            )
        }
    }
}

sealed class ProgressUiState {
    object Loading : ProgressUiState()
    data class Success(val data: ProgressData) : ProgressUiState()
    object Empty : ProgressUiState()
}
