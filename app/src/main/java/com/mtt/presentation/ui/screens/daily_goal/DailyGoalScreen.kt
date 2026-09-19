package com.mtt.presentation.ui.screens.daily_goal

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mtt.presentation.ui.screens.lifetime_goals.EmptyGoalsScreen

@Composable
fun DailyGoalScreen(
    viewModel: DailyGoalViewModel = hiltViewModel(),
    paddingValues: PaddingValues
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.onIntent(DailyGoalIntent.LoadGoals())
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(10.dp)
    )
    {
        Text(
            text = "Daily Goals",
            style = MaterialTheme.typography.titleLarge
        )
        Spacer(modifier = Modifier.height(16.dp))

        if (uiState.isLoading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
        } else if (uiState.goals.isEmpty()) {
            EmptyGoalsScreen(paddingValues)
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                items(
                    uiState.goals,
                    key = { it.goalId })
                { goal ->
                    DailyGoalCard(
                        goal = goal,
                        onViewHistory = {
                            viewModel.onIntent(
                                DailyGoalIntent.LoadHistory(goal.goalId)
                            )
                        },
                        onStop = {
                            viewModel.onIntent(
                                DailyGoalIntent.DeactivateGoal(
                                    goalId = goal.goalId
                                )
                            )
                        }
                    )
                }
            }
        }
    }
}