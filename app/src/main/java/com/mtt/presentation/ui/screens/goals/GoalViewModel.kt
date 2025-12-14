package com.mtt.presentation.ui.screens.goals

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mtt.jaapmala.data.local.entity.GoalEntity
import com.mtt.jaapmala.domain.usecase.AddGoalUseCase
import com.mtt.jaapmala.domain.usecase.DeleteGoalUseCase
import com.mtt.jaapmala.domain.usecase.GetGoalsUseCase
import com.mtt.jaapmala.domain.usecase.UpdateGoalProgressUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GoalsViewModel @Inject constructor(
    private val getGoalsUseCase: GetGoalsUseCase,
    private val addGoalUseCase: AddGoalUseCase,
    private val deleteGoalUseCase: DeleteGoalUseCase,
    private val updateGoalProgressUseCase: UpdateGoalProgressUseCase,
) : ViewModel() {
    val goals = getGoalsUseCase()
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            emptyList<GoalUiModel>()
        )


    fun addGoal(goal: GoalEntity) = viewModelScope.launch {
        addGoalUseCase(goal)
    }

    fun deleteGoal(goal: GoalEntity) = viewModelScope.launch {
        deleteGoalUseCase(goal)
    }

    fun updateGoalProgress(entity: GoalEntity) =
        viewModelScope.launch {
            updateGoalProgressUseCase(entity)
        }
}

