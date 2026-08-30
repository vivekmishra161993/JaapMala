package com.mtt.presentation.ui.screens.jaap

import com.mtt.jaapmala.data.local.entity.JaapEntity
import com.mtt.jaapmala.data.local.entity.JaapHistoryEntity
import com.mtt.presentation.ui.screens.settings.SoundMode

data class JaapDetailState(
    val mantra: JaapEntity?=null,
    val showManualEntryDialog: Boolean =false,
    val isMeditationSoundEnabled: Boolean = false,
    val history:List<JaapHistoryEntity> = emptyList(),
    val soundMode: SoundMode = SoundMode.MALA_COMPLETION,
    val isHapticFeedbackEnabled: Boolean=false,
    val hapticFrequency: Int=0,
    val updateStatus: Boolean?=false
)