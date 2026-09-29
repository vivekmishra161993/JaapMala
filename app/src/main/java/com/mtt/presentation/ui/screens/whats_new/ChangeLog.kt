package com.mtt.presentation.ui.screens.whats_new

data class ChangeLog(
    val versionCode: Int,
    val versionName: String,
    val changes: List<String>
)
