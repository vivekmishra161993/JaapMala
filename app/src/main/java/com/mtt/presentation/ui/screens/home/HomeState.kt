package com.mtt.presentation.ui.screens.home

import com.mtt.presentation.ui.screens.app_bar.TopBarState

data class HomeState(
    val mantraList: HomeUIState = HomeUIState.Loading,
    val topBarState: TopBarState,
    val showExitDialog : Boolean =false,
    val showWhatsNewDialog : Boolean = false,
    val changeLogs : List<String> = emptyList()
)