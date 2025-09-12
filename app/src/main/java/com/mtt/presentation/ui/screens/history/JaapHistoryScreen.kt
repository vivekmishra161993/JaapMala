package com.mtt.presentation.ui.screens.history

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.mtt.presentation.ui.screens.jaap.JaapDetailViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JaapHistoryScreen(jaapId: Int,
    viewModel: JaapDetailViewModel,
    onBack: () -> Unit
) {
    val mantra by viewModel.mantra.collectAsState()
    val history by viewModel.history.collectAsState()

    // Start collecting history when screen is opened
    LaunchedEffect(Unit) {
        viewModel.getHistory(jaapId)
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("${mantra?.name ?: ""} History") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onSurface,
                    actionIconContentColor = MaterialTheme.colorScheme.onSurface
                ),
            )
        }
    ) { padding ->
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
            LazyColumn(modifier = Modifier.padding(padding)) {
                items(history) { entry ->
                    HistoryListItem(entry)
                }
            }
        }
    }
}
