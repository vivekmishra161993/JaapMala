package com.mtt.presentation.ui.screens.home

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.mtt.jaapmala.util.toJaapEntity
import com.mtt.presentation.ui.screens.Screens
import com.mtt.presentation.ui.screens.app_bar.TopAppBarWithMenu
import com.mtt.presentation.ui.screens.app_bar.TopBarAction

@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    navController: NavController,
    onBackupClick: () -> Unit,
    onRestoreClick: () -> Unit,
    onExit: () -> Unit,
) {
    val showExitDialog by viewModel.showExitDialog.collectAsState()
    val mantras = viewModel.mantras.collectAsState()
    val topBarState by viewModel.topBarState.collectAsState()
    LaunchedEffect(Unit) {
        viewModel.topBarEvent.collect { action ->
            when (action) {
                is TopBarAction.Backup -> onBackupClick()
                is TopBarAction.Restore -> onRestoreClick()
                is TopBarAction.Settings -> {
                    navController.navigate(Screens.Settings.route)
                }
                else -> Unit
            }
        }
    }
    // Intercept back press
    BackHandler {
        viewModel.onBackPressed()
    }
    Scaffold(
        topBar = {
            TopAppBarWithMenu(topBarState, onActionSelected = viewModel::onTopBarAction)
        }
    ) { scaffoldPadding ->
        if (mantras.value.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(scaffoldPadding),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    "No mantras yet. \nClick + to add a new Mantra",
                    color = MaterialTheme.colorScheme.onBackground,
                    style = TextStyle(
                        fontWeight = FontWeight.ExtraBold,
                        textAlign = TextAlign.Center
                    ),
                    fontSize = 20.sp
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(
                        top = scaffoldPadding.calculateTopPadding(),
                        bottom = scaffoldPadding.calculateBottomPadding()
                    )
            ) {
                items(mantras.value) { mantra ->
                    MantraListItem(
                        mantra,
                        onMantraClick = {
                            navController.navigate(Screens.JaapDetailScreen.passJaapId(mantra.id))
                        },
                        onDeleteMantra = {
                            viewModel.deleteJaap(mantra.toJaapEntity())
                        }
                    )
                }
            }
        }
        // Exit dialog
        if (showExitDialog) {
            AlertDialog(
                onDismissRequest = { viewModel.dismissExitDialog() },
                title = { Text("Exit App") },
                text = { Text("Are you sure you want to exit the app?") },
                confirmButton = {
                    Button(onClick = {
                        viewModel.confirmExit()
                        onExit()
                    }) {
                        Text("Yes")
                    }
                },
                dismissButton = {
                    Button(onClick = { viewModel.dismissExitDialog() }) {
                        Text("No")
                    }
                }
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