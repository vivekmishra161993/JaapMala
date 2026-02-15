package com.mtt.presentation.ui.screens.jaap

import android.content.Context
import android.content.Intent
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mtt.jaapmala.R
import com.mtt.jaapmala.data.SoundManager
import com.mtt.jaapmala.data.local.entity.JaapEntity
import com.mtt.jaapmala.data.local.entity.JaapHistoryEntity
import com.mtt.jaapmala.domain.usecase.EnsureTodayUseCase
import com.mtt.jaapmala.domain.usecase.GetHapticFeedbackUseCase
import com.mtt.jaapmala.domain.usecase.GetHapticFrequencyUseCase
import com.mtt.jaapmala.domain.usecase.GetJaapHistoryUseCase
import com.mtt.jaapmala.domain.usecase.GetMantraUseCase
import com.mtt.jaapmala.domain.usecase.GetMeditationSoundEnabledUseCase
import com.mtt.jaapmala.domain.usecase.GetSoundModeUseCase
import com.mtt.jaapmala.domain.usecase.SaveJaapHistoryUseCase
import com.mtt.jaapmala.domain.usecase.ShouldTriggerHapticUseCase
import com.mtt.jaapmala.domain.usecase.UpdateGoalProgressUseCase
import com.mtt.jaapmala.domain.usecase.UpdateJaapManuallyUseCase
import com.mtt.jaapmala.domain.usecase.UpdateJaapUseCase
import com.mtt.jaapmala.util.JaapSoundManager
import com.mtt.presentation.ui.screens.app_bar.TopBarAction
import com.mtt.presentation.ui.screens.settings.SoundMode
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
    private val shouldTriggerHapticUseCase: ShouldTriggerHapticUseCase,
    private val ensureTodayUseCase: EnsureTodayUseCase,
    private val getSoundModeUseCase: GetSoundModeUseCase,
    private val jaapSoundManager: JaapSoundManager,
    private val getHapticFrequencyUseCase: GetHapticFrequencyUseCase,
    private val getHapticFeedbackUseCase: GetHapticFeedbackUseCase
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
    private var currentSoundMode: SoundMode = SoundMode.MALA_COMPLETION
    var hapticFeedbackEnabled: Boolean = false

    val hapticFrequency = getHapticFrequencyUseCase()
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            1
        )

    init {
        viewModelScope.launch {
            currentSoundMode = getSoundModeUseCase().first()
            hapticFeedbackEnabled = getHapticFeedbackUseCase().first()

        }
    }
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

            viewModelScope.launch {

                val safeCurrent = ensureTodayUseCase(current)

                val nextCount = safeCurrent.count + 1
                val isMalaCompleted =
                    nextCount % safeCurrent.malaSize == 0

                var newCount = nextCount
                var newMalaCount = safeCurrent.todayMalaCount
                var newLifetimeMalaCount = safeCurrent.lifetimeMalaCount
                var newSessionMalaCount = safeCurrent.sessionMalaCount

                // -------------------------
                // HAPTIC (NO FLOW COLLECTION)
                // -------------------------
                if (hapticFeedbackEnabled) {
                    val frequency = hapticFrequency.value

                    if (frequency > 0 && nextCount % frequency == 0) {
                        _uiEvent.emit(JaapUIEvent.TriggerHaptic)
                    }
                }

                // -------------------------
                // MALA COMPLETION LOGIC
                // -------------------------
                if (isMalaCompleted) {

                    newCount = 0
                    newMalaCount += 1
                    newLifetimeMalaCount += 1
                    newSessionMalaCount += 1

                    updateGoalProgressUseCase(
                        safeCurrent.id,
                        malaIncrement = 1
                    )
                }

                // -------------------------
                // SOUND HANDLING
                // -------------------------
                handleSound(isMalaCompleted)

                val updated = safeCurrent.copy(
                    count = newCount,
                    todayCount = safeCurrent.todayCount + 1,
                    lifetimeCount = safeCurrent.lifetimeCount + 1,
                    sessionCount = safeCurrent.sessionCount + 1,
                    todayMalaCount = newMalaCount,
                    lifetimeMalaCount = newLifetimeMalaCount,
                    sessionMalaCount = newSessionMalaCount
                )

                _mantra.value = updated
                updateJaapUseCase(updated)
            }
        }
    }


    fun decreaseCount() {
        _mantra.value?.let { current ->

            viewModelScope.launch {

                val safeCurrent = ensureTodayUseCase(current)

                if (safeCurrent.count > 0) {
                    val updated = safeCurrent.copy(
                        count = safeCurrent.count - 1,
                        todayCount = maxOf(safeCurrent.todayCount - 1, 0),
                        lifetimeCount = maxOf(safeCurrent.lifetimeCount - 1, 0),
                        sessionCount = maxOf(safeCurrent.sessionCount - 1, 0)
                    )

                    _mantra.value = updated
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
    private fun handleSound(isMalaCompleted: Boolean) {

        when (currentSoundMode) {

            SoundMode.OFF -> { }

            SoundMode.EVERY_COUNT -> {
                jaapSoundManager.playJaapTick()
                if (isMalaCompleted) {
                    jaapSoundManager.playMalaBell()
                }
            }

            SoundMode.MALA_COMPLETION -> {
                if (isMalaCompleted) {
                    jaapSoundManager.playMalaBell()
                }
            }
        }
        }


}
