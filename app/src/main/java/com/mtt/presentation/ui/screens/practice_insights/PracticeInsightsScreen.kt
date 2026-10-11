package com.mtt.presentation.ui.screens.practice_insights

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun PracticeInsightsScreen(
    jaapId: Int,
    padding: PaddingValues,
    viewModel: PracticeInsightsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(jaapId) {
        viewModel.loadInsights(jaapId)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(padding)
    ) {

        when {
            uiState.isLoading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            uiState.error != null -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = uiState.error
                            ?: "Unable to load insights",
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }

            uiState.insights != null -> {
                PracticeInsightsContent(
                    insights = uiState.insights!!
                )
            }
        }
    }
}