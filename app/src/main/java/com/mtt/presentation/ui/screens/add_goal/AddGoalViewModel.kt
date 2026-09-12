package com.mtt.presentation.ui.screens.add_goal

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mtt.jaapmala.data.local.entity.GoalEntity
import com.mtt.jaapmala.data.local.entity.GoalStatus
import com.mtt.jaapmala.domain.usecase.goals.AddGoalUseCase
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
class AddGoalViewModel @Inject constructor(
    private val getMantrasUseCase: GetMantrasUseCase,
    private val addGoalUseCase: AddGoalUseCase,
) : ViewModel() {
    val mantra = getMantrasUseCase()
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())
    private val _uiState = MutableStateFlow(AddGoalUIState())
    val uiState = _uiState.asStateFlow()
    fun addGoal(goal: GoalEntity) = viewModelScope.launch {
        addGoalUseCase(goal)
    }
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

    fun onGoalNameChange(value: String) {
        updateState { copy(goalName = value, nameError = null) }
        validateForm()
    }

    fun onTargetMalasChange(value: String) {
        updateState { copy(targetMalas = value, targetMalasError = null) }
        validateForm()
    }

    fun onDateSelected(date: Long?) {
        updateState { copy(endDate = date, dateError = null) }
        validateForm()
    }

    /* ---------- Validation ---------- */

    private fun validateForm() {
        val state = _uiState.value

        val malas = state.targetMalas.toIntOrNull()

        _uiState.update {
            it.copy(
                jaapError = if (state.selectedJaapId == 0) "Please select a Jaap" else null,
                nameError = if (state.goalName.isBlank()) "Goal name cannot be empty" else null,
                targetMalasError =
                    if (malas == null || malas <= 0) "Enter a valid number" else null,
                dateError = if (state.endDate == null) "Please select end date" else null,
                isFormValid =
                    state.selectedJaapId != 0 &&
                            state.goalName.isNotBlank() &&
                            malas != null && malas > 0 &&
                            state.endDate != null
            )
        }
    }
    private inline fun updateState(block: AddGoalUIState.() -> AddGoalUIState) {
        _uiState.update(block)
    }
    fun submitGoal(onSuccess: () -> Unit) {
        validateForm()
        if (!_uiState.value.isFormValid) return

        val state = _uiState.value

        viewModelScope.launch {
            addGoal(
                GoalEntity(
                    jaapId = state.selectedJaapId,
                    name = state.goalName.trim(),
                    targetMalas = state.targetMalas.toInt(),
                    endDate = state.endDate,
                    status = GoalStatus.ACTIVE
                )
            )
            onSuccess()
        }
    }
}
