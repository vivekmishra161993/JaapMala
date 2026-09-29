package com.mtt.presentation.ui.screens.goals

import android.annotation.SuppressLint
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.mtt.jaapmala.util.toEntity
import com.mtt.presentation.ui.screens.home.LoadingPlaceholderView

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun GoalsScreen(viewModel: GoalsViewModel = hiltViewModel(), paddingValues: PaddingValues) {
    val uiState = viewModel.uiState.collectAsState()
    Crossfade(
        targetState = uiState,
        animationSpec = tween(durationMillis = 500, easing = LinearEasing),
        label = "Home-crossfade"
    ) { state ->
        when (state.value) {
            is GoalUIState.Loading -> {
                LoadingPlaceholderView(modifier = Modifier.padding(paddingValues))
            }

            is GoalUIState.Empty -> {
                EmptyGoalView(paddingValues)
            }
            is GoalUIState.Success -> {
                val goals = (uiState.value as GoalUIState.Success).goals
                LazyColumn(modifier = Modifier.padding(paddingValues)) {
                    items(goals) {
                        GoalCard(goal = it, onDeleteGoal = {
                            viewModel.deleteGoal(it.toEntity())
                        })
                    }
                }
            }

        }
    }
}
@Composable
fun EmptyGoalView(paddingValues: PaddingValues) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                imageVector = Icons.Filled.EmojiEvents, // or Meditation icon
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f),
                modifier = Modifier.size(80.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                "No goals yet",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold
                ),
                color = MaterialTheme.colorScheme.onBackground
            )
            Text(
                "Tap the + button to add a new goal",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
