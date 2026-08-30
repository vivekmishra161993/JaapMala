package com.mtt.presentation.ui.screens.home

sealed class HomeEffect {
    object NavigateToSettings: HomeEffect()
    object NavigateToBackup: HomeEffect()
    object NavigateToRestore : HomeEffect()
    object CloseApp: HomeEffect()
    data class NavigateToJaapDetail(val jaapId:Int): HomeEffect()

}