package com.mtt.presentation.ui.screens.app_bar

 data class TopBarState(
     var title: String,
     val actions: List<TopBarAction>?=null
) {
    /*data class HomeTopBar(
        override var title: String = "Jaap Mala",
        override val actions: List<TopBarAction> = listOf(
            TopBarAction.Backup,
            TopBarAction.Restore,
            TopBarAction.Settings
        )
    ) : TopBarState(title, actions)

    data class DetailTopBar(
        override var title: String,
        override val actions: List<TopBarAction> = listOf(
            TopBarAction.IncrementCount,
            TopBarAction.History,
            TopBarAction.Share
        )
    ) : TopBarState(title, actions)*/
}
