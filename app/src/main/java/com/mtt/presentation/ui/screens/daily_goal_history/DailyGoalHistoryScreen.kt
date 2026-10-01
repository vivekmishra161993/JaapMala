package com.mtt.presentation.ui.screens.daily_goal_history

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mtt.jaapmala.R
import com.mtt.presentation.ui.screens.daily_goal.DailyGoalIntent
import com.mtt.presentation.ui.screens.daily_goal.DailyGoalViewModel

@Composable
fun DailyGoalHistoryScreen(
    goalId: Int,
    padding: PaddingValues,
    viewModel: DailyGoalViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val history = uiState.selectedGoalHistory

    LaunchedEffect(goalId) {
        viewModel.onIntent(
            DailyGoalIntent.LoadHistory(goalId)
        )
    }

    if (history.isEmpty()) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentAlignment = Alignment.Center
        ) {
            Text(stringResource(R.string.no_history_available))
        }
    } else {

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(padding),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp)
            ) {

                items(
                    items = history,
                    key = { it.date }
                ) { entry ->
                    DailyGoalHistoryItem(entry)
                }
            }
        }
    }
}