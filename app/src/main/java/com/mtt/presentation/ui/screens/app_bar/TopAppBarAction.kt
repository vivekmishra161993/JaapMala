package com.mtt.presentation.ui.screens.app_bar

sealed class TopBarAction(val title: String) {
    object Backup : TopBarAction("Backup")
    object Restore : TopBarAction("Restore")
    object IncrementCount : TopBarAction("Add Count")
    object History : TopBarAction("History")
    object  Share : TopBarAction("Share Progress")
    object Settings : TopBarAction("Settings")
}
