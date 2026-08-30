package com.mtt.presentation.ui.screens.jaap

sealed class JaapDetailEffect {
    object TriggerHaptic : JaapDetailEffect()
    data class NavigateToHistory(val jaapId: Int): JaapDetailEffect()
    data class ShareText(val shareText: String): JaapDetailEffect()
    object NavigateBack : JaapDetailEffect()

}