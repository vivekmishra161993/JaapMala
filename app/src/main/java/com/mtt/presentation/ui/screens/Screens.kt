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
    data object AddGoalScreen :Screens("add_goal")
    object Settings : Screens("settings")
    data object  AddDailyGoalScreen:Screens("add_daily_goal")
    data object DailyGoalHistoryScreen:Screens("daily_goal_history/{goalId}"){
        fun passGoalId(id:Int): String ="daily_goal_history/$id"
    }
    data object PracticeInsightsScreen: Screens("practice_insights/{jaapId}") {
        fun passJaapId(jaapId: Int): String =
            "practice_insights/$jaapId"
    }

}