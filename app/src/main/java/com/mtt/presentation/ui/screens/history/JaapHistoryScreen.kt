package com.mtt.presentation.ui.screens.history

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.mtt.presentation.ui.screens.home.HomeIntent
import com.mtt.presentation.ui.screens.home.HomeViewModel
import com.mtt.presentation.ui.screens.jaap.JaapDetailIntent
import com.mtt.presentation.ui.screens.jaap.JaapDetailViewModel

@Composable
fun JaapHistoryScreen(
    jaapId: Int,
    viewModel: JaapDetailViewModel,
    homeViewModel: HomeViewModel,
    padding: PaddingValues
) {
    val state by viewModel.state.collectAsState()
    val mantra = state.mantra
    val history = state.history

    LaunchedEffect(jaapId) {
        viewModel.onIntent(JaapDetailIntent.LoadMantra(jaapId))
        viewModel.onIntent(JaapDetailIntent.LoadHistory(jaapId))
    }

    LaunchedEffect(mantra) {
        mantra?.let {
            homeViewModel.onIntent(
                HomeIntent.UpdateTopBar(
                    title = it.name,
                    actions = emptyList()
                )
            )
        }
    }

    if (history.isEmpty()) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentAlignment = Alignment.Center
        ) {
            Text("No History Available")
        }
    } else {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp)
        ) {
            items(history) { entry ->
                HistoryListItem(entry)
            }
        }
    }
}
