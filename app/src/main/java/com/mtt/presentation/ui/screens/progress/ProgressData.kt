package com.mtt.presentation.ui.screens.progress

data class ProgressData(
    val totalJaaps: Int,
    val totalCount: Long,
    val totalMalas: Long,
    val perMantraProgress: List<MantraProgress>
)

data class MantraProgress(
    val name: String,
    val progressPercent: Int
)
