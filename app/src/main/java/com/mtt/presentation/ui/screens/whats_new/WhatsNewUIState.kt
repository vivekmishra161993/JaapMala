package com.mtt.presentation.ui.screens.whats_new

data class WhatsNewUIState(
    val showDialog: Boolean = false,
    val changeLogs: List<ChangeLog> = emptyList()
)