package com.mtt.presentation.ui.screens.daily_goal

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mtt.jaapmala.domain.usecase.dailygoal.CreateDailyGoalUseCase
import com.mtt.jaapmala.domain.usecase.dailygoal.DeactivateDailyGoalUseCase
import com.mtt.jaapmala.domain.usecase.dailygoal.GetActiveDailyGoalsUseCase
import com.mtt.jaapmala.domain.usecase.dailygoal.GetDailyGoalHistoryUseCase
import com.mtt.jaapmala.domain.usecase.dailygoal.GetDailyGoalProgressUseCase
import com.mtt.jaapmala.domain.usecase.jaap.GetMantrasUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class DailyGoalViewModel @Inject constructor(
    private val getDailyActiveDailyGoalsUseCase: GetActiveDailyGoalsUseCase,
    private val createDailyGoalUseCase: CreateDailyGoalUseCase,
    private val deactivateDailyGoalUseCase: DeactivateDailyGoalUseCase,
    private val getDailyGoalHistoryUseCase: GetDailyGoalHistoryUseCase,
    private val getDailyGoalProgressUseCase: GetDailyGoalProgressUseCase,
    private val getMantrasUseCase: GetMantrasUseCase
) : ViewModel() {
    private val _uiState = MutableStateFlow(DailyGoalUiState())
    val uiState: StateFlow<DailyGoalUiState> = _uiState.asStateFlow()
    private val _effects = MutableSharedFlow<DailyGoalEffect>()
    val effects: SharedFlow<DailyGoalEffect> = _effects
    val mantra = getMantrasUseCase().stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    private var goalsJob: Job? = null
    private var historyJob: Job? = null

    fun onIntent(intent: DailyGoalIntent) {
        when (intent) {
            DailyGoalIntent.ClearError -> {
                _uiState.update { it.copy(error = null) }
            }

            is DailyGoalIntent.CreateGoal -> createGoal(intent)
            is DailyGoalIntent.DeactivateGoal -> deactivateGoal(intent.goalId)
            is DailyGoalIntent.LoadGoals -> loadGoals()
            is DailyGoalIntent.LoadHistory -> loadHistory(intent.goalId)
        }
    }

    private fun loadHistory(goalId: Int) {
        historyJob?.cancel()
        historyJob = getDailyGoalHistoryUseCase(goalId)
            .onEach { history ->
                _uiState.update {
                    it.copy(
                        selectedGoalHistory = history,
                        error = null
                    )
                }
            }.catch { throwable ->
                _uiState.update {
                    it.copy(error = throwable.message ?: "Unable to load goal history")
                }
            }.launchIn(viewModelScope)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun loadGoals() {
        goalsJob?.cancel()
        goalsJob = getDailyActiveDailyGoalsUseCase()
            .flatMapLatest { goals ->
                if (goals.isEmpty()) {
                    flowOf(emptyList<DailyGoalItemUiModel>())
                } else {
                    val today = LocalDate.now().toString()
                    val mantras = mantra.value
                    combine(
                        goals.map { goal ->
                            getDailyGoalProgressUseCase(
                                goal = goal,
                                date = today
                            )
                        }
                    ) { progessList ->
                        goals.mapIndexed { index, goal ->
                            val progress = progessList[index]
                            val jaapName = mantras
                                .firstOrNull { it.id == goal.jaapId }
                                ?.name
                                ?: "Unknown Jaap"
                            DailyGoalItemUiModel(
                                goalId = goal.id,
                                jaapId = goal.jaapId,
                                jaapName = jaapName,
                                targetMalas = progress.targetMalas,
                                completedMalas = progress.completedMalas,
                                remainingMalas = progress.remainingMalas,
                                progress = progress.progress,
                                status = progress.status
                            )
                        }
                    }
                }.onEach { items: List<DailyGoalItemUiModel> ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            goals = items,
                            error = null
                        )
                    }
                }
            }.catch { throwable ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = throwable.message ?: "Unable to load daily goals"
                    )
                }
            }.launchIn(viewModelScope)
    }

    private fun deactivateGoal(goalId: Int) {
        viewModelScope.launch {
            try {
                deactivateDailyGoalUseCase(goalId)
                _effects.emit(DailyGoalEffect.GoalDeactivated)
            } catch (throwable: Throwable) {
                _effects.emit(
                    DailyGoalEffect.ShowError(
                        throwable.message ?: "Unable to deactivate daily goal"
                    )
                )
            }
        }
    }

    private fun createGoal(intent: DailyGoalIntent.CreateGoal) {
        viewModelScope.launch {
            try {
                createDailyGoalUseCase(
                    jaapId = intent.jaapId,
                    targetMalas = intent.targetMalas
                )

                _effects.emit(DailyGoalEffect.GoalCreated)

            } catch (throwable: Throwable) {
                _effects.emit(
                    DailyGoalEffect.ShowError(
                        throwable.message ?: "Unable to create daily goal"
                    )
                )
            }
        }
    }
}