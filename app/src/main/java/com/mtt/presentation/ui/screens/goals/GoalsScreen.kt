package com.mtt.presentation.ui.screens.goals

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mtt.jaapmala.data.local.entity.GoalEntity

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun GoalsScreen(viewModel: GoalsViewModel = hiltViewModel(),paddingValues: PaddingValues) {
    val goals by viewModel.goals.collectAsStateWithLifecycle() // or collectAsState()
    var entity by remember {
        mutableStateOf<GoalEntity>(
            GoalEntity(
                0, 0,
                "", 0, 0, null, null
            )
        )
    }

        if (goals.isEmpty()) {
            EmptyGoalsScreen(paddingValues)
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(goals) { goal ->
                    GoalCard(goal as GoalUiModel)
                }
            }
        }
}
