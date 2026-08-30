package com.mtt.presentation.ui.screens.jaap

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mtt.jaapmala.R
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
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
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
    private val updateGoalProgressUseCase: UpdateGoalProgressUseCase,
    private val shouldTriggerHapticUseCase: ShouldTriggerHapticUseCase,
    private val ensureTodayUseCase: EnsureTodayUseCase,
    private val getSoundModeUseCase: GetSoundModeUseCase,
    private val jaapSoundManager: JaapSoundManager,
    private val getHapticFrequencyUseCase: GetHapticFrequencyUseCase,
    private val getHapticFeedbackUseCase: GetHapticFeedbackUseCase
) : ViewModel() {
    private val _state = MutableStateFlow(JaapDetailState())
    val state: StateFlow<JaapDetailState> = _state.asStateFlow()
    private val _effect = Channel<JaapDetailEffect>(Channel.BUFFERED)
    val effect = _effect.receiveAsFlow()


    init {
        observeSettings()
    }

    fun onIntent(intent: JaapDetailIntent) {
        when (intent) {
            is JaapDetailIntent.DecreaseCount -> {
                decreaseCount()
            }

            JaapDetailIntent.IncreaseCount -> increaseCount()
            JaapDetailIntent.ShowManualEntryDialog -> {
                reduce {
                    copy(showManualEntryDialog = true)
                }
            }

            JaapDetailIntent.DismissManualEntryDialog ->
                reduce {
                    copy(showManualEntryDialog = false, updateStatus = null)
                }

            is JaapDetailIntent.SubmitManualEntry -> {
                updateJaapCountManually(
                    intent.id,
                    intent.count
                )
            }

            is JaapDetailIntent.OnTopBarAction -> {
                onTopBarAction(intent.action)
            }

            is JaapDetailIntent.LoadMantra -> {
                getMantra(intent.id)
            }

            is JaapDetailIntent.LoadHistory -> {
                getHistory(intent.id)
            }

            JaapDetailIntent.OnAppBackgrounded -> onAppBackgrounded()
            JaapDetailIntent.OnAppForegrounded -> onAppForegrounded()

        }
    }

    private fun observeSettings() {
        viewModelScope.launch {
            combine(
                getSoundModeUseCase(),
                getHapticFeedbackUseCase(),
                getHapticFrequencyUseCase(),
                getMeditationSoundEnabledUseCase()
            ) { mode, hapticEnabled, frequency, meditationSoundEnabled ->
                reduce {
                    copy(
                        soundMode = mode,
                        isHapticFeedbackEnabled = hapticEnabled,
                        hapticFrequency = frequency,
                        isMeditationSoundEnabled = meditationSoundEnabled
                    )
                }
            }.collect()
        }
    }

    private fun getMantra(id: Int) {
        viewModelScope.launch {
            getMantraUseCase(id).collectLatest { entity ->
                val safeEntity = entity.copy(
                    count = entity.count.coerceAtLeast(0),
                    sessionCount = entity.sessionCount.coerceAtLeast(0),
                    sessionMalaCount = entity.sessionMalaCount.coerceAtLeast(0)
                )
                reduce {
                    copy(mantra = safeEntity)
                }
            }
        }
    }

    private fun increaseCount() {

        val current = state.value.mantra ?: return

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

            val updated = safeCurrent.copy(
                count = newCount,
                todayCount = safeCurrent.todayCount + 1,
                lifetimeCount = safeCurrent.lifetimeCount + 1,
                sessionCount = safeCurrent.sessionCount + 1,
                todayMalaCount = newMalaCount,
                lifetimeMalaCount = newLifetimeMalaCount,
                sessionMalaCount = newSessionMalaCount
            )

            reduce {
                copy(mantra = updated)
            }
            viewModelScope.launch {
                handleHaptic(nextCount = nextCount)
            }
            handleSound(isMalaCompleted = isMalaCompleted)
            updateJaapUseCase(updated)
        }
    }


    private fun decreaseCount() {
        val current = state.value.mantra ?: return

        viewModelScope.launch {

            val safeCurrent = ensureTodayUseCase(current)
            if (safeCurrent.count <= 0) {
                return@launch
            }
            val updated = safeCurrent.copy(
                count = safeCurrent.count - 1,
                todayCount = maxOf(safeCurrent.todayCount - 1, 0),
                lifetimeCount = maxOf(safeCurrent.lifetimeCount - 1, 0),
                sessionCount = maxOf(safeCurrent.sessionCount - 1, 0)
            )

            reduce {
                copy(mantra = updated)
            }
            updateJaapUseCase(updated)

        }
    }


    private suspend fun handleHaptic(nextCount: Int) {
        val currentState = state.value
        if (!currentState.isHapticFeedbackEnabled) {
            return
        }
        val frequency = currentState.hapticFrequency
        if (frequency <= 0) {
            return
        }
        if (nextCount % frequency != 0) {
            return
        }
        _effect.send(JaapDetailEffect.TriggerHaptic)
    }

    private fun updateJaapCountManually(jaapId: Int, addedCount: Int) {
        viewModelScope.launch {
            try {
                updateJaapManuallyUseCase.invoke(jaapId, addedCount)
                val mantra = state.value.mantra ?: return@launch
                val malaIncrement = if (mantra.malaSize > 0) {
                    addedCount / mantra.malaSize
                } else {
                    0
                }
                updateGoalProgressUseCase.invoke(
                    jaapId,
                    malaIncrement = malaIncrement
                )

                reduce {
                    copy(updateStatus = true)
                }
            } catch (e: Exception) {
                reduce {
                    copy(updateStatus = false)
                }
            }
        }
    }

    private fun onTopBarAction(action: TopBarAction) {
        viewModelScope.launch {
            when (action) {
                is TopBarAction.IncrementCount -> {
                    reduce {
                        copy(showManualEntryDialog = true)
                    }
                }

                is TopBarAction.History -> {
                    val jaapId = state.value.mantra?.id ?: return@launch
                    _effect.send(JaapDetailEffect.NavigateToHistory(jaapId))
                }

                is TopBarAction.Share -> {
                    shareProgress()
                }

                else -> {}
            }
        }
    }

    fun saveHistoryForToday() {
        viewModelScope.launch {
            val mantra = state.value.mantra ?: return@launch
            val today = LocalDate.now().toString()

            saveJaapHistoryUseCase(
                jaapId = mantra.id,
                date = today,
                count = mantra.todayCount,
                malaCount = mantra.todayMalaCount
            )
        }
    }

    private fun shareProgress() {
        val mantra = state.value.mantra ?: return
        val appLink = "https://play.google.com/store/apps/details?id=com.mtt.jaapmala"
        val shareText = """
            I am practicing "${mantra.name}" using Jaap Mala app!
            Today's count: ${mantra.todayCount}
            Lifetime count: ${mantra.lifetimeCount}
            Download the app here: $appLink
        """.trimIndent()
        viewModelScope.launch {
            _effect.send(JaapDetailEffect.ShareText(shareText = shareText))
        }
    }

    override fun onCleared() {
        super.onCleared()
    }

    private fun handleSound(isMalaCompleted: Boolean) {

        when (state.value.soundMode) {

            SoundMode.OFF -> {}

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

    private fun getHistory(id: Int) {
        viewModelScope.launch {
            getJaapHistoryUseCase(id).collect { list ->
                reduce {
                    copy(history = list)
                }
            }
        }
    }

    private fun onAppBackgrounded() {
        viewModelScope.launch {
            jaapSoundManager.stopMeditation()
        }
    }

    private fun onAppForegrounded() {
        viewModelScope.launch {
            if (state.value.isMeditationSoundEnabled) {
                jaapSoundManager.startMeditation(R.raw.sound)
            }
        }
    }

    private fun reduce(
        reducer: JaapDetailState.() -> JaapDetailState
    ) {
        _state.update {
            it.reducer()
        }
    }
}
