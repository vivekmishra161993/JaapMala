package com.mtt.presentation.ui.screens.goals

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.mtt.presentation.ui.screens.daily_goal.DailyGoalScreen
import com.mtt.presentation.ui.screens.lifetime_goals.LifetimeGoalScreen

@Composable
fun GoalsScreen(
    paddingValues: PaddingValues,
    selectedGoalTab: Int,
    onTabSelected: (Int) -> Unit
) {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
    ) {

        GoalsTypeSelector(
            selectedTab = selectedGoalTab,
            onTabSelected = onTabSelected
        )

        when (selectedGoalTab) {
            0 -> LifetimeGoalScreen(
                paddingValues = paddingValues
            )

            1 -> DailyGoalScreen(paddingValues=paddingValues)
        }
    }
}