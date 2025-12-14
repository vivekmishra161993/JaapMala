package com.mtt.presentation.ui.screens.home

import androidx.activity.compose.BackHandler
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.SelfImprovement
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.mtt.jaapmala.data.model.MantraDto
import com.mtt.jaapmala.util.toJaapEntity
import com.mtt.presentation.ui.screens.Screens

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    navController: NavController,
    onExit: () -> Unit,
    paddingValues: PaddingValues
) {
    val showExitDialog by viewModel.showExitDialog.collectAsState()
    val uiState = viewModel.uiState.collectAsState()

    // Intercept back press
    BackHandler {
        viewModel.onBackPressed()
    }
    /*
        Scaffold { scaffoldPadding ->
    */
    Crossfade(
        targetState = uiState,
        animationSpec = tween(durationMillis = 500, easing = LinearEasing),
        label = "Home-crossfade"
    ) { state ->
        when (state.value) {
            is HomeUIState.Loading -> {
                LoadingPlaceholderView(modifier = Modifier.padding(paddingValues))
            }

            is HomeUIState.Empty -> {
                EmptyView(paddingValues)
            }

            is HomeUIState.Success -> {
                val mantras = (uiState.value as HomeUIState.Success).mantras
                MantraList(mantras, navController, viewModel,paddingValues)
                // Exit dialog
                if (showExitDialog) {
                    ExitDialog(viewModel, onExit)
                }
            }
        }
        // }

    }
}

@Composable
fun MantraList(
    mantras: List<MantraDto>,
    navController: NavController,
    viewModel: HomeViewModel,
    paddingValues: PaddingValues
) {
    LazyColumn(modifier = Modifier.padding(paddingValues)) {
        items(mantras, key = { it.id }) { mantra ->
            MantraListItem(
                mantra,
                onMantraClick = {
                    navController.navigate(
                        Screens.JaapDetailScreen.passJaapId(
                            mantra.id
                        )
                    )
                },
                onDeleteMantra = {
                    viewModel.deleteJaap(mantra.toJaapEntity())
                },
                onEditMantra = { newName ->
                    viewModel.updateMantraName(mantra.id, newName)
                },
                modifier = Modifier.animateItem()
            )
        }
    }
}

@Composable
@Preview
fun Preview() {
    Box(
        modifier = Modifier
            .fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            "No mantras yet. \nClick + to add a new mantra",
            color = MaterialTheme.colorScheme.onBackground
        )
    }
}

@Composable
fun EmptyView(paddingValues: PaddingValues) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                imageVector = Icons.Default.SelfImprovement, // or Meditation icon
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f),
                modifier = Modifier.size(80.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                "No mantras yet",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold
                ),
                color = MaterialTheme.colorScheme.onBackground
            )
            Text(
                "Tap the + button to add a new mantra",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun ExitDialog(viewModel: HomeViewModel, onExit: () -> Unit) {
    AlertDialog(
        onDismissRequest = { viewModel.dismissExitDialog() },
        title = { Text("Exit App", fontWeight = FontWeight.Bold) },
        text = { Text("Are you sure you want to exit Jaap Mala?") },
        confirmButton = {
            TextButton(onClick = {
                viewModel.confirmExit()
                onExit()
            }) { Text("Yes", color = MaterialTheme.colorScheme.error) }
        },
        dismissButton = {
            TextButton(onClick = { viewModel.dismissExitDialog() }) {
                Text("No", color = MaterialTheme.colorScheme.primary)
            }
        },
        shape = RoundedCornerShape(16.dp)
    )

}

@Composable
fun LoadingPlaceholderView(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        val alpha = remember { Animatable(0.3f) }

        LaunchedEffect(Unit) {
            while (true) {
                alpha.animateTo(0.7f, tween(700))
                alpha.animateTo(0.3f, tween(700))
            }
        }

        Text(
            text = "Loading your Jaaps...",
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = alpha.value),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold
        )
    }
}
