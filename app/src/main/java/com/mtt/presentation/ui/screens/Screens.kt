package com.mtt.presentation.ui.screens

sealed class Screens(val route: String) {
    data object OnBoardingScreen: Screens("onboarding")
    data object HomeScreen : Screens("home")
    data object JaapDetailScreen : Screens("detail/{jaapId}") {
        fun passJaapId(id: Int): String = "detail/$id"
    }
    data object AddJaapDialog :Screens("add_jaap")
    data object JaapHistoryScreen : Screens("jaap_history/{jaapId}"){
        fun passJaapId(id: Int): String = "jaap_history/$id"
    }
}