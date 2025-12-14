package com.mtt.presentation.ui.screens.add_goal

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mtt.jaapmala.domain.usecase.GetMantrasUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class AddGoalViewModel @Inject constructor(
    private val getMantrasUseCase: GetMantrasUseCase
) : ViewModel() {
    val mantra = getMantrasUseCase().stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

}