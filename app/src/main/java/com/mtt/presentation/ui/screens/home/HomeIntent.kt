package com.mtt.presentation.ui.screens.home

import android.content.Context
import com.mtt.jaapmala.data.local.entity.JaapEntity
import com.mtt.presentation.ui.screens.app_bar.TopBarAction

sealed class HomeIntent {
    data class AddMantra(val name: String, val date: String, val size:Int): HomeIntent()
    data class DeleteMantra(val jaap: JaapEntity): HomeIntent()
    data class UpdateMantraName(val jaapId: Int,val newName: String): HomeIntent()
    object RefreshData: HomeIntent()
    object OnBackPressed: HomeIntent()
    object ConfirmExit: HomeIntent()
    object DismissExitDialog: HomeIntent()
    object OnWhatsNewDismissed: HomeIntent()
    data class OnTopBarAction(val action: TopBarAction,val context: Context): HomeIntent()
    object InitializeHome: HomeIntent()
    data class OnMantraClicked(val jaapId:Int): HomeIntent()
    data class UpdateTopBar(val title: String,val actions: List<TopBarAction>): HomeIntent()

}