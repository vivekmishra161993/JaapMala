package com.mtt.presentation.ui.screens.main

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Home
import androidx.compose.ui.graphics.vector.ImageVector

sealed class BottomTabItem(val route: String, val title: String, val icon: ImageVector) {
    object Jaaps : BottomTabItem("jaaps_tab", "Jaaps", Icons.Filled.Home)
    object Goals : BottomTabItem("goals_tab", "Goals", Icons.Filled.EmojiEvents)
}
