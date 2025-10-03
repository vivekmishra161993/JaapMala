package com.mtt.presentation.ui.screens.app_bar

sealed class TopBarState(
    open val title: String,
    open val actions: List<TopBarAction>
) {
    data class HomeTopBar(
        override val title: String = "Jaap Mala",
        override val actions: List<TopBarAction> = listOf(
            TopBarAction.Backup,
            TopBarAction.Restore,
            TopBarAction.Settings
        )
    ) : TopBarState(title, actions)

    data class DetailTopBar(
        override val title: String,
        override val actions: List<TopBarAction> = listOf(
            TopBarAction.IncrementCount,
            TopBarAction.History,
            TopBarAction.Share
        )
    ) : TopBarState(title, actions)
}
