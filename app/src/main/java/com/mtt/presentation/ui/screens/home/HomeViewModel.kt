package com.mtt.presentation.ui.screens.home

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mtt.jaapmala.data.local.db.DatabaseManager
import com.mtt.jaapmala.data.local.entity.JaapEntity
import com.mtt.jaapmala.domain.repository.ChangelogRepository
import com.mtt.jaapmala.domain.usecase.DeleteJaapUseCase
import com.mtt.jaapmala.domain.usecase.GetMantrasUseCase
import com.mtt.jaapmala.domain.usecase.InsertMantraUseCase
import com.mtt.jaapmala.domain.usecase.ResetTodayCountsUseCase
import com.mtt.jaapmala.domain.usecase.UpdateJaapNameUseCase
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
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
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
    val databaseManager: DatabaseManager,
    private val updateJaapNameUseCase: UpdateJaapNameUseCase,
    @ApplicationContext private val context: Context,
    private val changelogRepository: ChangelogRepository,
    ) : ViewModel() {
    private val refreshTrigger = MutableStateFlow(Unit)
    private val _showExitDialog = MutableStateFlow(false)
    val showExitDialog: StateFlow<Boolean> = _showExitDialog

    private val _uiState = MutableStateFlow<HomeUIState>(HomeUIState.Loading)
    private  var customActionHandler: ((TopBarAction, Context) -> Unit)? = null
    private val _showWhatsNew = MutableStateFlow(false)
    val showWhatsNew: StateFlow<Boolean> = _showWhatsNew

    private val _changelogItems = MutableStateFlow<List<String>>(emptyList())
    val changelogItems: StateFlow<List<String>> = _changelogItems

    private var currentVersionCode = 8

    // Expose mantras as StateFlow by collecting from the use case Flow,
    // converting JaapEntities to DTOs
    @OptIn(ExperimentalCoroutinesApi::class)
    val uiState: StateFlow<HomeUIState> = refreshTrigger
        .flatMapLatest {
            flow {
                emit(HomeUIState.Loading)
                delay(150) // small delay to show shimmer
                emitAll(getMantrasUseCase().map { list ->
                    when {
                        list.isEmpty() -> HomeUIState.Empty
                        else -> HomeUIState.Success(list.map { it.toMantraDto() })
                    }
                })
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = HomeUIState.Loading
        )
    private val _topBarState = MutableStateFlow(
        TopBarState("JaapMala")
    )
    val topBarState: StateFlow<TopBarState> = _topBarState

    // One-time UI events
    private val _topBarEvent = MutableSharedFlow<TopBarAction>()
    val topBarEvent = _topBarEvent.asSharedFlow()

    init {
        resetDailyCount()
        checkForWhatsNew()
    }

    private fun resetDailyCount() {
        viewModelScope.launch {
            resetTodayCountUseCase()
        }
    }
    fun setHomeScreenActions(){
        _topBarState.update { currentState ->
            currentState.copy(
                actions = listOf(
                    TopBarAction.Backup,
                    TopBarAction.Restore,
                    TopBarAction.Settings
                )
            )
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

    fun onTopBarAction(action: TopBarAction,context: Context) {
        viewModelScope.launch {
            if (customActionHandler!=null){
                customActionHandler?.invoke(action,context)
                return@launch
            }
            when (action) {
                is TopBarAction.Backup -> _topBarEvent.emit(TopBarAction.Backup)
                is TopBarAction.Restore -> _topBarEvent.emit(TopBarAction.Restore)
                is TopBarAction.Settings -> _topBarEvent.emit(TopBarAction.Settings)

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

    fun refreshData() {
        refreshTrigger.value = Unit
    }

    fun onBackPressed() {
        _showExitDialog.value = true
    }

    fun confirmExit() {
        _showExitDialog.value = false
        // Handle actual exit in Activity/Composable
    }

    fun dismissExitDialog() {
        _showExitDialog.value = false
    }
    fun updateMantraName(jaapId: Int, newName: String) {
        viewModelScope.launch {
            updateJaapNameUseCase(jaapId, newName)
            refreshData() // refresh the list after edit
        }
    }
    /**
     * Updates the TopBar actions with a new list.
     * To be called by other screens like JaapDetailScreen.
     */
    fun setTopBarActions(actions: List<TopBarAction>) {
        _topBarState.update { it.copy(actions = actions) }
    }
    private fun checkForWhatsNew() {
        viewModelScope.launch {
            currentVersionCode = AppVersionProvider(context).getVersionCode(context)

            changelogRepository.lastShownVersion.collect { lastShown ->
                if (currentVersionCode > lastShown) {
                    _changelogItems.value =
                        ChangeLogProvider.getChangesFor(currentVersionCode)

                    if (_changelogItems.value.isNotEmpty()) {
                        _showWhatsNew.value = true
                    }
                }
            }
        }
    }

    fun onWhatsNewDismissed() {
        viewModelScope.launch {
            changelogRepository.markVersionShown(currentVersionCode)
            _showWhatsNew.value = false
        }
    }
}
