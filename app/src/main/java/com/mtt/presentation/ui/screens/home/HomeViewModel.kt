package com.mtt.presentation.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mtt.jaapmala.data.local.db.DatabaseManager
import com.mtt.jaapmala.data.local.entity.JaapEntity
import com.mtt.jaapmala.data.model.MantraDto
import com.mtt.jaapmala.domain.usecase.DeleteJaapUseCase
import com.mtt.jaapmala.domain.usecase.GetMantrasUseCase
import com.mtt.jaapmala.domain.usecase.InsertMantraUseCase
import com.mtt.jaapmala.domain.usecase.ResetTodayCountsUseCase
import com.mtt.jaapmala.util.toMantraDto
import com.mtt.presentation.ui.screens.app_bar.TopBarAction
import com.mtt.presentation.ui.screens.app_bar.TopBarState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getMantrasUseCase: GetMantrasUseCase,
    private val insertMantraUseCase: InsertMantraUseCase,
    private val resetTodayCountUseCase: ResetTodayCountsUseCase,
    private val deleteJaapUseCase: DeleteJaapUseCase,
    val databaseManager: DatabaseManager
) : ViewModel() {
    private val refreshTrigger = MutableStateFlow(Unit)

    // Expose mantras as StateFlow by collecting from the use case Flow,
    // converting JaapEntities to DTOs
    @OptIn(ExperimentalCoroutinesApi::class)
    val mantras: StateFlow<List<MantraDto>> = refreshTrigger
        .flatMapLatest { getMantrasUseCase() }
        .map { list -> list.map { it.toMantraDto() } }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    private val _topBarState = MutableStateFlow<TopBarState>(
        TopBarState.HomeTopBar()
    )
    val topBarState: StateFlow<TopBarState> = _topBarState

    // One-time UI events
    private val _topBarEvent = MutableSharedFlow<TopBarAction>()
    val topBarEvent = _topBarEvent.asSharedFlow()

    init {
        resetDailyCount()
    }

    private fun resetDailyCount() {
        viewModelScope.launch {
            resetTodayCountUseCase()
        }
    }

    fun addMantra(name: String, size: Int) {
        viewModelScope.launch {
            insertMantraUseCase(name, getTodayDate(), size)
            // No need to update _mantras manually; Flow emits updates
        }
    }

    private fun getTodayDate(): String {
        val today = LocalDate.now()
        val formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy")
        return today.format(formatter)
    }

    fun deleteJaap(jaap: JaapEntity) {
        viewModelScope.launch {
            deleteJaapUseCase(jaap)
        }
    }

    fun onTopBarAction(action: TopBarAction) {
        viewModelScope.launch {
            when (action) {
                is TopBarAction.Backup -> _topBarEvent.emit(TopBarAction.Backup)
                is TopBarAction.Restore -> _topBarEvent.emit(TopBarAction.Restore)
                else -> {}
            }
        }
    }

    fun refreshData() {
        refreshTrigger.value = Unit
    }
}
