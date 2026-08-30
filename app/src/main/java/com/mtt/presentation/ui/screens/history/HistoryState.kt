package com.mtt.presentation.ui.screens.history

import com.mtt.jaapmala.data.local.entity.JaapHistoryEntity

data class HistoryState(
    val history:List<JaapHistoryEntity>,
    val mantraName: String,
    val isLoading : Boolean
)