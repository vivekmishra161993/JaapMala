package com.mtt.presentation.ui.screens.jaap

import com.mtt.presentation.ui.screens.app_bar.TopBarAction

sealed class JaapDetailIntent {
    object IncreaseCount: JaapDetailIntent()
    object DecreaseCount: JaapDetailIntent()
    object ShowManualEntryDialog: JaapDetailIntent()
    object DismissManualEntryDialog: JaapDetailIntent()
    data class SubmitManualEntry(val id: Int, val count:Int): JaapDetailIntent()
    data class OnTopBarAction(val action: TopBarAction): JaapDetailIntent()
    object OnAppBackgrounded: JaapDetailIntent()
    object OnAppForegrounded: JaapDetailIntent()
    data class LoadMantra(val id:Int): JaapDetailIntent()
    data class LoadHistory(val id:Int): JaapDetailIntent()
}