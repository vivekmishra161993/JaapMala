package com.mtt.presentation.ui.screens.history

sealed class HistoryIntent {
    data class LoadHistory(val jaapId: Int): HistoryIntent()
}