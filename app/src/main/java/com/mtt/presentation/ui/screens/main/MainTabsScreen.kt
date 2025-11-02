package com.mtt.presentation.ui.screens.main

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.mtt.presentation.ui.screens.Screens
import com.mtt.presentation.ui.screens.app_bar.TopAppBarWithMenu
import com.mtt.presentation.ui.screens.app_bar.TopBarAction
import com.mtt.presentation.ui.screens.goals.GoalsScreen
import com.mtt.presentation.ui.screens.home.HomeScreen
import com.mtt.presentation.ui.screens.home.HomeViewModel
import com.mtt.presentation.ui.screens.progress.ProgressScreen


@Composable
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
fun MainTabsScreen(
    parentNavController: NavHostController,
    viewModel: HomeViewModel,
    onBackupClick: () -> Unit,
    onRestoreClick: () -> Unit,
    onExit: () -> Unit
) {
    val tabs = listOf(
        BottomTabItem.Jaaps,
        BottomTabItem.Progress,
        BottomTabItem.Goals
    )
    val bottomNavController = rememberNavController()
    val currentBackStack by bottomNavController.currentBackStackEntryAsState()
    val currentDestination = currentBackStack?.destination?.route
    val topBarState by viewModel.topBarState.collectAsState()
    val hideMenuOnRoutes = listOf(
        BottomTabItem.Progress.route,
        BottomTabItem.Goals.route
    )

    val toolbarTitle = when (currentDestination) {
        Screens.HomeScreen.route,
        BottomTabItem.Jaaps.route -> "Jaap Mala"
        BottomTabItem.Progress.route -> "Progress"
        BottomTabItem.Goals.route -> "Goals"
        else -> "Jaap Mala"
    }
    topBarState.title = toolbarTitle
    LaunchedEffect(Unit) {
        viewModel.topBarEvent.collect { action ->
            when (action) {
                is TopBarAction.Backup -> onBackupClick()
                is TopBarAction.Restore -> onRestoreClick()
                is TopBarAction.Settings -> {
                    parentNavController.navigate(Screens.Settings.route)
                }

                else -> Unit
            }
        }
    }
    Scaffold(
        topBar = {
            TopAppBarWithMenu(
                topBarState,
                onActionSelected = viewModel::onTopBarAction,
                showOverFlowMenu = currentDestination !in hideMenuOnRoutes
            )
        },
        bottomBar = {
            AnimatedBottomBar(tabs, currentDestination, onTabSelected = { tab ->
                bottomNavController.navigate(tab.route) {
                    popUpTo(bottomNavController.graph.startDestinationId)
                    launchSingleTop = true
                }
            })
        },
        floatingActionButton = {
            if (currentDestination == BottomTabItem.Jaaps.route || currentDestination == BottomTabItem.Goals.route) {
                FloatingActionButton(
                    onClick = {
                        parentNavController.navigate(Screens.AddJaapDialog.route)
                    },
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = Color.White
                ) {
                    Icon(Icons.Filled.Add, "Add")
                }

            }
        }
    ) { innerPadding ->
        NavHost(
            navController = bottomNavController,
            startDestination = BottomTabItem.Jaaps.route,
            modifier = Modifier.padding(
                top = innerPadding.calculateTopPadding(),
                bottom = innerPadding.calculateBottomPadding(),
                start = 10.dp,
                end = 10.dp
            )
        ) {
            composable(BottomTabItem.Jaaps.route) {
                HomeScreen(
                    viewModel = viewModel,
                    parentNavController,

                    onExit = onExit
                )
            }
            composable(BottomTabItem.Progress.route) {
                ProgressScreen()
            }
            composable(BottomTabItem.Goals.route) {
                GoalsScreen()
            }
        }
    }
}
