package com.mtt.presentation.ui.screens.goals

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mtt.jaapmala.data.local.entity.GoalEntity
import com.mtt.jaapmala.domain.usecase.CheckGoalStatusUseCase
import com.mtt.jaapmala.domain.usecase.DeleteGoalUseCase
import com.mtt.jaapmala.domain.usecase.GetGoalsUseCase
import com.mtt.jaapmala.domain.usecase.UpdateGoalProgressUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GoalsViewModel @Inject constructor(
    private val getGoalsUseCase: GetGoalsUseCase,
    private val deleteGoalUseCase: DeleteGoalUseCase,
    private val updateGoalProgressUseCase: UpdateGoalProgressUseCase,
    private val checkGoalStatusUseCase: CheckGoalStatusUseCase
) : ViewModel() {
    private val refreshTrigger = MutableStateFlow(Unit)

    init {
        viewModelScope.launch {
            checkGoalStatusUseCase.invoke()
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    val uiState: StateFlow<GoalUIState> = refreshTrigger
        .flatMapLatest {
            flow {
                emit(GoalUIState.Loading)
                delay(150) // small delay to show shimmer
                emitAll(getGoalsUseCase().map { list ->
                    when {
                        list.isEmpty() -> GoalUIState.Empty
                        else -> GoalUIState.Success(list)
                    }
                })
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = GoalUIState.Loading
        )
    val goals = getGoalsUseCase()
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            emptyList()
        )

    fun deleteGoal(goal: GoalEntity) = viewModelScope.launch {
        deleteGoalUseCase(goal)
    }

    fun updateGoalProgress(jaapId: Int) = viewModelScope.launch {
        updateGoalProgressUseCase(jaapId)
    }
}

