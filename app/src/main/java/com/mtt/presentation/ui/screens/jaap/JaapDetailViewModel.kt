package com.mtt.presentation.ui.screens.jaap

import android.content.Context
import android.content.Intent
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mtt.jaapmala.R
import com.mtt.jaapmala.data.SoundManager
import com.mtt.jaapmala.data.local.entity.JaapEntity
import com.mtt.jaapmala.data.local.entity.JaapHistoryEntity
import com.mtt.jaapmala.domain.usecase.GetJaapHistoryUseCase
import com.mtt.jaapmala.domain.usecase.GetMantraUseCase
import com.mtt.jaapmala.domain.usecase.GetMeditationSoundEnabledUseCase
import com.mtt.jaapmala.domain.usecase.SaveJaapHistoryUseCase
import com.mtt.jaapmala.domain.usecase.ShouldTriggerHapticUseCase
import com.mtt.jaapmala.domain.usecase.UpdateGoalProgressUseCase
import com.mtt.jaapmala.domain.usecase.UpdateJaapManuallyUseCase
import com.mtt.jaapmala.domain.usecase.UpdateJaapUseCase
import com.mtt.presentation.ui.screens.app_bar.TopBarAction
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class JaapDetailViewModel @Inject constructor(
    private val getMantraUseCase: GetMantraUseCase,
    private val updateJaapUseCase: UpdateJaapUseCase,
    private val updateJaapManuallyUseCase: UpdateJaapManuallyUseCase,
    private val saveJaapHistoryUseCase: SaveJaapHistoryUseCase,
    private val getJaapHistoryUseCase: GetJaapHistoryUseCase,
    private val getMeditationSoundEnabledUseCase: GetMeditationSoundEnabledUseCase,
    private val meditationSoundManager: SoundManager,
    private val updateGoalProgressUseCase: UpdateGoalProgressUseCase,
    private val shouldTriggerHapticUseCase: ShouldTriggerHapticUseCase
) : ViewModel() {

    private val _mantra = MutableStateFlow<JaapEntity?>(null)
    val mantra: StateFlow<JaapEntity?> = _mantra

    private val _uiEvent = MutableSharedFlow<JaapUIEvent>()
    val uiEvent = _uiEvent.asSharedFlow()
    private val _updateStatus = MutableStateFlow<Boolean?>(null)
    val updateStatus = _updateStatus.asStateFlow()

    private val _showManualEntryDialog = MutableStateFlow(false)
    val showManualEntryDialog: StateFlow<Boolean> = _showManualEntryDialog

    private val _history = MutableStateFlow<List<JaapHistoryEntity>>(emptyList())
    val history: StateFlow<List<JaapHistoryEntity>> = _history

    private val _topBarEvent = MutableSharedFlow<TopBarAction>()
    val topBarEvent = _topBarEvent.asSharedFlow()
    val meditationSoundEnabled = getMeditationSoundEnabledUseCase()
        .stateIn(viewModelScope, SharingStarted.Lazily, false)



    fun getMantra(id: Int) {
        viewModelScope.launch {
            getMantraUseCase(id).collect { entity ->
                // Directly set the mantra, initializing counts on load if necessary
                _mantra.value = entity.copy(
                    count = entity.count.takeIf { it >= 0 } ?: 0, // Ensure count is never negative
                    sessionCount = entity.sessionCount.takeIf { it >= 0 } ?: 0,
                    sessionMalaCount = entity.sessionMalaCount.takeIf { it >= 0 } ?: 0
                )
            }
        }
    }

    fun increaseCount() {
        _mantra.value?.let { current ->
            var newCount = current.count + 1
            var newMalaCount = current.todayMalaCount
            var newLifetimeMalaCount = current.lifetimeMalaCount
            var newSessionMalaCount = current.sessionMalaCount

            viewModelScope.launch {
                shouldTriggerHapticUseCase
                    .shouldTrigger(newCount)
                    .first()
                    .takeIf { it }
                    ?.let { _uiEvent.emit(JaapUIEvent.TriggerHaptic) }
            }
            if (newCount % current.malaSize == 0) {
                newCount = 0
                newMalaCount += 1
                newLifetimeMalaCount += 1
                newSessionMalaCount += 1
                //play bell
                meditationSoundManager.triggerMalaCompletionFeedback(R.raw.bell)
                viewModelScope.launch {
                   updateGoalProgressUseCase(current.id,  malaIncrement = 1)
                }
            }

            val updated = current.copy(
                count = newCount,
                todayCount = current.todayCount + 1,
                lifetimeCount = current.lifetimeCount + 1,
                sessionCount = current.sessionCount + 1,
                todayMalaCount = newMalaCount,
                lifetimeMalaCount = newLifetimeMalaCount,
                sessionMalaCount = newSessionMalaCount,
            )
            _mantra.value = updated

            // Save to DB immediately
            viewModelScope.launch {
                updateJaapUseCase(updated)
            }

        }
    }
    fun decreaseCount() {
        _mantra.value?.let { current ->
            if (current.count > 0) {
                val updated = current.copy(
                    count = current.count - 1,
                    todayCount = maxOf(current.todayCount - 1, 0),
                    lifetimeCount = maxOf(current.lifetimeCount - 1, 0),
                    sessionCount = maxOf(current.sessionCount - 1, 0)
                )
                _mantra.value = updated
                // Save to DB immediately
                viewModelScope.launch {
                    updateJaapUseCase(updated)
                }
            }
        }
    }
    fun updateJaapCountManually(jaapId: Int, addedCount: Int) {
        viewModelScope.launch {
            try {
                updateJaapManuallyUseCase.invoke(jaapId, addedCount)
                updateGoalProgressUseCase.invoke(jaapId,  malaIncrement = (addedCount/_mantra.value!!.malaSize))

                _updateStatus.value = true
            } catch (e: Exception) {
                _updateStatus.value = false
            }
        }
    }
    fun onTopBarAction(action: TopBarAction,context: Context) {
        viewModelScope.launch {
            when (action) {
                is TopBarAction.IncrementCount -> { _showManualEntryDialog.value = true }
                is TopBarAction.History -> { _topBarEvent.emit(TopBarAction.History)}
                is TopBarAction.Share -> {shareProgress(context )}
                else -> {}
            }
        }
    }
    fun dismissManualEntryDialog() {
        _showManualEntryDialog.value = false
    }
    fun saveHistoryForToday() {
        viewModelScope.launch {
            val mantra = _mantra.value ?: return@launch
            val today = LocalDate.now().toString()

            saveJaapHistoryUseCase(
                jaapId = mantra.id,
                date = today,
                count = mantra.todayCount,
                malaCount = mantra.todayMalaCount
            )
        }
    }
    fun getHistory(id: Int) {
        viewModelScope.launch {
            getJaapHistoryUseCase(id).collect { list ->
                _history.value = list
            }
        }
    }
    fun shareProgress(context: Context) {
        val appLink = "https://play.google.com/store/apps/details?id=com.mtt.jaapmala"
        val shareText = """
            I am practicing "${_mantra.value?.name}" using Jaap Mala app!
            Today's count: ${_mantra.value?.todayCount}
            Lifetime count: ${_mantra.value?.lifetimeCount}
            Download the app here: $appLink
        """.trimIndent()
        val sendIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, shareText)
            type = "text/plain"
        }
        val shareIntent = Intent.createChooser(sendIntent, "Share your Jaap progress")
        context.startActivity(shareIntent)
    }
    fun onAppPaused() {
        meditationSoundManager.onAppBackgrounded()
    }

    fun onAppResumed() {
        meditationSoundManager.onAppForegrounded()
    }

    override fun onCleared() {
        super.onCleared()
        meditationSoundManager.releaseAll()
    }
    fun startMeditationSound() {
        if (meditationSoundEnabled.value) {
            meditationSoundManager.playMeditationSound(R.raw.sound)
        }
    }
    fun stopMeditationSound() {
        meditationSoundManager.stopMeditationSound()
    }
}
