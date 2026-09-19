package com.mtt.presentation.ui.screens.add_daily_goals

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mtt.jaapmala.domain.usecase.dailygoal.CreateDailyGoalUseCase
import com.mtt.jaapmala.domain.usecase.jaap.GetMantrasUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddDailyGoalViewModel @Inject constructor(
    private val getMantrasUseCase: GetMantrasUseCase,
    private val createDailyGoalUseCase: CreateDailyGoalUseCase
) : ViewModel() {
    val mantra = getMantrasUseCase().stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())
    private val _uiState = MutableStateFlow(AddDailyGoalUIState())
    val uiState = _uiState.asStateFlow()

    fun onJaapSelected(id: Int, name: String) {
        updateState {
            copy(
                selectedJaapId = id,
                selectedJaapName = name,
                jaapError = null
            )
        }
        validateForm()
    }

    fun onTargetMalasChange(value: String) {
        updateState { copy(targetMalas = value, targetMalasError = null) }
        validateForm()
    } /* ---------- Validation ---------- */

    private fun validateForm() {
        val state = _uiState.value
        val malas = state.targetMalas.toIntOrNull()
        _uiState.update {
            it.copy(
                jaapError = if (state.selectedJaapId == 0) {
                    "Please select a Jaap"
                } else {
                    null
                }, targetMalasError = if (malas == null || malas <= 0) {
                    "Enter a valid number"
                } else {
                    null
                }, isFormValid = state.selectedJaapId != 0 && malas != null && malas > 0
            )
        }
    }

    private inline fun updateState(block: AddDailyGoalUIState.() -> AddDailyGoalUIState) {
        _uiState.update(block)
    }

    fun submitGoal(onSuccess: () -> Unit) {
        validateForm()
        if (!_uiState.value.isFormValid) return
        val state = _uiState.value
        viewModelScope.launch {
            try {
                createDailyGoalUseCase(
                    jaapId = state.selectedJaapId,
                    targetMalas = state.targetMalas.toInt()
                )
                onSuccess()
            } catch (throwable: Throwable) {
                _uiState.update {
                    it.copy(
                        error = throwable.message ?: "Unable to create daily goal"
                    )
                }
            }
        }
    }
}