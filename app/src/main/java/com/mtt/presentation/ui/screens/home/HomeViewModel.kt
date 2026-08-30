package com.mtt.presentation.ui.screens.home

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mtt.jaapmala.data.local.db.DatabaseManager
import com.mtt.jaapmala.domain.repository.ChangelogRepository
import com.mtt.jaapmala.domain.usecase.DeleteJaapUseCase
import com.mtt.jaapmala.domain.usecase.GetMantrasUseCase
import com.mtt.jaapmala.domain.usecase.InsertMantraUseCase
import com.mtt.jaapmala.domain.usecase.ResetTodayCountsUseCase
import com.mtt.jaapmala.domain.usecase.UpdateJaapNameUseCase
import com.mtt.jaapmala.util.DateUtils
import com.mtt.jaapmala.util.toMantraDto
import com.mtt.presentation.ui.screens.app_bar.TopBarAction
import com.mtt.presentation.ui.screens.app_bar.TopBarState
import com.mtt.presentation.ui.screens.whats_new.AppVersionProvider
import com.mtt.presentation.ui.screens.whats_new.ChangeLogProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getMantrasUseCase: GetMantrasUseCase,
    private val insertMantraUseCase: InsertMantraUseCase,
    private val resetTodayCountUseCase: ResetTodayCountsUseCase,
    private val deleteJaapUseCase: DeleteJaapUseCase,
    val databaseManager: DatabaseManager,
    private val updateJaapNameUseCase: UpdateJaapNameUseCase,
    @ApplicationContext private val context: Context,
    private val changelogRepository: ChangelogRepository,
) : ViewModel() {
    private val refreshTrigger = MutableStateFlow(Unit)

    private val _state = MutableStateFlow(
        HomeState(
            topBarState = TopBarState("JaapMala"),
            mantraList = HomeUIState.Loading
        )
    )
    private val _effect = MutableSharedFlow<HomeEffect>()
    val state = _state.asStateFlow()
    val effect: SharedFlow<HomeEffect> = _effect.asSharedFlow()
    private var currentVersionCode = 10
    private var customActionHandler: ((TopBarAction, Context) -> Unit)? = null


    init {
        observeMantras()
        resetDailyCount()
        checkForWhatsNew()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun observeMantras() {
        viewModelScope.launch {
            refreshTrigger.flatMapLatest {
                flow {
                    emit(HomeUIState.Loading)
                    delay(150)
                    emitAll(getMantrasUseCase().map { list ->
                        when {
                            list.isEmpty() -> HomeUIState.Empty
                            else -> HomeUIState.Success(list.map { it.toMantraDto() })
                        }

                    })
                }
            }.collect { newListState ->
                _state.update { it.copy(mantraList = newListState) }
            }
        }
    }

    fun onIntent(intent: HomeIntent) {
        when (intent) {
            is HomeIntent.AddMantra -> {
                viewModelScope.launch {
                    insertMantraUseCase(intent.name, DateUtils.getTodayDate(), intent.size)
                }
            }

            is HomeIntent.DeleteMantra -> {
                viewModelScope.launch {
                    deleteJaapUseCase(intent.jaap)
                }
            }

            is HomeIntent.UpdateMantraName -> {
                viewModelScope.launch {
                    updateJaapNameUseCase(intent.jaapId, intent.newName)
                    refreshData()
                }
            }

            is HomeIntent.RefreshData -> {
                refreshData()
            }

            is HomeIntent.OnTopBarAction -> {
                handleTopBarAction(intent.action, intent.context)
            }

            is HomeIntent.OnBackPressed -> {
                _state.update { it.copy(showExitDialog = true) }
            }

            is HomeIntent.ConfirmExit -> {
                _state.update { it.copy(showExitDialog = false) }
                viewModelScope.launch {
                    _effect.emit(HomeEffect.CloseApp)
                }

            }

            is HomeIntent.DismissExitDialog -> {
                _state.update { it.copy(showExitDialog = false) }

            }

            is HomeIntent.OnWhatsNewDismissed -> {
                viewModelScope.launch {
                    changelogRepository.markVersionShown(currentVersionCode)
                    _state.update { it.copy(showWhatsNewDialog = false) }
                }
            }

            is HomeIntent.InitializeHome -> {
                _state.update { it ->
                    it.copy(
                        topBarState = it.topBarState.copy(
                            actions = listOf(
                                TopBarAction.Backup,
                                TopBarAction.Restore,
                                TopBarAction.Settings
                            )
                        )
                    )
                }
            }

            is HomeIntent.OnMantraClicked -> {
                viewModelScope.launch {
                    _effect.emit(HomeEffect.NavigateToJaapDetail(intent.jaapId))

                }
            }
            is HomeIntent.UpdateTopBar ->{
                _state.update { it.copy(topBarState = it.topBarState.copy(title = intent.title, actions = intent.actions)) }
            }


        }
    }

    private fun resetDailyCount() {
        viewModelScope.launch {
            resetTodayCountUseCase()
        }
    }

    private fun handleTopBarAction(action: TopBarAction, context: Context) {
        viewModelScope.launch {
            if (customActionHandler != null) {
                customActionHandler?.invoke(action, context)
                return@launch
            }
            when (action) {
                is TopBarAction.Backup -> _effect.emit(HomeEffect.NavigateToBackup)
                is TopBarAction.Restore -> _effect.emit(HomeEffect.NavigateToRestore)
                is TopBarAction.Settings -> _effect.emit(HomeEffect.NavigateToSettings)
                else -> {}
            }
        }
    }

    fun registerCustomActionHandler(handler: (TopBarAction, Context) -> Unit) {
        customActionHandler = handler
    }

    fun unregisterCustomActionHandler() {
        customActionHandler = null
    }

    private fun refreshData() {
        refreshTrigger.value = Unit
    }

    private fun checkForWhatsNew() {
        viewModelScope.launch {
            currentVersionCode = AppVersionProvider(context).getVersionCode(context)

            changelogRepository.lastShownVersion.collect { lastShown ->
                if (currentVersionCode > lastShown) {
                    val items = ChangeLogProvider.getChangesFor(currentVersionCode)

                    if (items.isNotEmpty()) {
                        _state.update { it.copy(changeLogs = items, showWhatsNewDialog = true) }
                    }
                }
            }
        }
    }

}
