package com.mtt.presentation.ui.screens.home

import com.mtt.jaapmala.data.model.MantraDto

sealed class HomeUIState {
    object Loading : HomeUIState()
    data class Success(val mantras: List<MantraDto>) : HomeUIState()
    object Empty : HomeUIState()
}
