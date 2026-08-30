package com.mtt.presentation.ui.screens.main

//import com.mtt.presentation.ui.screens.add_goal.AddGoalScreen
import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.mtt.jaapmala.data.local.db.BackupPrefs
import com.mtt.jaapmala.data.local.db.DatabaseManager
import com.mtt.jaapmala.domain.model.ThemeOption
import com.mtt.jaapmala.domain.repository.SettingsRepository
import com.mtt.jaapmala.util.DateUtils
import com.mtt.presentation.ui.screens.Screens
import com.mtt.presentation.ui.screens.add_goal.AddGoalScreen
import com.mtt.presentation.ui.screens.app_bar.TopAppBarWithMenu
import com.mtt.presentation.ui.screens.app_bar.TopBarAction
import com.mtt.presentation.ui.screens.goals.GoalsScreen
import com.mtt.presentation.ui.screens.history.JaapHistoryScreen
import com.mtt.presentation.ui.screens.home.AddMantraDialog
import com.mtt.presentation.ui.screens.home.HomeEffect
import com.mtt.presentation.ui.screens.home.HomeIntent
import com.mtt.presentation.ui.screens.home.HomeScreen
import com.mtt.presentation.ui.screens.home.HomeViewModel
import com.mtt.presentation.ui.screens.jaap.JaapDetailScreen
import com.mtt.presentation.ui.screens.jaap.JaapDetailViewModel
import com.mtt.presentation.ui.screens.onboarding.OnboardingScreen
import com.mtt.presentation.ui.screens.settings.SettingsScreen
import com.mtt.presentation.ui.theme.JaapMalaTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject
    lateinit var settingsRepository: SettingsRepository

    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val viewModel: HomeViewModel = hiltViewModel()
            val navController = rememberNavController()
            val navBackStackEntry by navController.currentBackStackEntryAsState()
            val currentDestination = navBackStackEntry?.destination?.route
            val context = LocalContext.current
            val themeOption by settingsRepository.themeOption.collectAsState(initial = ThemeOption.SYSTEM)

            val showOnboarding = remember {
                !BackupPrefs.isOnboardingShown(context)
            }
            val startDestination =
                if (showOnboarding) Screens.OnBoardingScreen.route else BottomTabItem.Jaaps.route

            // Declare launchers first
            lateinit var backupManager: DatabaseManager

            val backupLauncher = rememberLauncherForActivityResult(
                contract = ActivityResultContracts.CreateDocument("application/octet-stream")
            ) { uri ->
                backupManager.handleBackup(uri)
            }

            val restoreLauncher = rememberLauncherForActivityResult(
                contract = ActivityResultContracts.OpenDocument()
            ) { uri ->
                if (uri != null) {
                    val success = backupManager.handleRestore(uri)
                    if (success) {
                        viewModel.onIntent(HomeIntent.RefreshData)
                    }
                }
            }
            // Now safely initialize the backupManager *after* launchers are remembered
            backupManager = remember {
                viewModel.databaseManager.apply {
                    setupActivityLaunchers(backupLauncher, restoreLauncher)
                }
            }
            val tabs = listOf(
                BottomTabItem.Jaaps,
                BottomTabItem.Goals
            )
            val hideMenuOnRoutes = listOf(
                BottomTabItem.Goals.route,
                Screens.AddGoalScreen.route,
                Screens.OnBoardingScreen.route,
                Screens.Settings.route
            )
            val topLevelRoutes = remember {
                setOf(
                    BottomTabItem.Jaaps.route,
                    BottomTabItem.Goals.route,
                    Screens.OnBoardingScreen.route,
                    Screens.AddJaapDialog.route
                )
            }
            val shouldShowBackButton =
                currentDestination !in topLevelRoutes && currentDestination != null
            val state by viewModel.state.collectAsState()
            
            LaunchedEffect(currentDestination) {
                when (currentDestination) {
                    Screens.HomeScreen.route, BottomTabItem.Jaaps.route -> {
                        viewModel.onIntent(HomeIntent.InitializeHome)
                        viewModel.onIntent(HomeIntent.UpdateTopBar("Jaap Mala", listOf(TopBarAction.Backup, TopBarAction.Restore, TopBarAction.Settings)))
                    }
                    BottomTabItem.Goals.route -> {
                        viewModel.onIntent(HomeIntent.UpdateTopBar("Goals", emptyList()))
                    }
                    Screens.Settings.route -> {
                        viewModel.onIntent(HomeIntent.UpdateTopBar("Settings", emptyList()))
                    }
                    Screens.AddGoalScreen.route -> {
                        viewModel.onIntent(HomeIntent.UpdateTopBar("Add Goal", emptyList()))
                    }
                    Screens.AddJaapDialog.route -> {
                        viewModel.onIntent(HomeIntent.UpdateTopBar("Add Mantra", emptyList()))
                    }
                    Screens.JaapHistoryScreen.route -> {
                        viewModel.onIntent(HomeIntent.UpdateTopBar("History", emptyList()))
                    }
                }
            }

            LaunchedEffect(Unit) {
                viewModel.effect.collect { action ->
                    when (action) {
                        is HomeEffect.NavigateToBackup -> {
                            backupManager.backupDatabase()
                        }

                        is HomeEffect.NavigateToRestore -> {
                            backupManager.restoreDatabase()
                        }

                        is HomeEffect.NavigateToSettings -> {
                            navController.navigate(Screens.Settings.route)
                        }

                        else -> Unit
                    }
                }
            }

            JaapMalaTheme(themeOption) {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    contentColor = MaterialTheme.colorScheme.background,
                    topBar = {
                        TopAppBarWithMenu(
                            state.topBarState,
                            showBackButton = shouldShowBackButton,
                            onBack = { navController.navigateUp() },
                            onActionSelected = { action ->
                                viewModel.onIntent(HomeIntent.OnTopBarAction(action,this))
                            },
                            showOverFlowMenu = currentDestination !in hideMenuOnRoutes
                        )
                    },
                    bottomBar = {
                        if (navController.currentDestination?.route == BottomTabItem.Jaaps.route
                            || navController.currentDestination?.route == BottomTabItem.Goals.route
                        ) {
                            AnimatedBottomBar(tabs, currentDestination, onTabSelected = { tab ->
                                navController.navigate(tab.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            })
                        }
                    },
                    floatingActionButton = {

                        if (currentDestination == BottomTabItem.Jaaps.route || currentDestination == BottomTabItem.Goals.route) {
                            FloatingActionButton(
                                onClick = {
                                    if (currentDestination == BottomTabItem.Jaaps.route) {
                                        navController.navigate(Screens.AddJaapDialog.route)
                                    } else {
                                        navController.navigate(Screens.AddGoalScreen.route)

                                    }
                                },
                                containerColor = MaterialTheme.colorScheme.primary,
                                contentColor = Color.White,
                                modifier = Modifier.padding(bottom = 16.dp)
                            ) {
                                Icon(Icons.Filled.Add, "Add")
                                }

                        }
                    }
                ) { innerPadding ->
                    NavHost(
                        navController = navController,
                        startDestination = startDestination
                    ) {
                        composable(
                            route = Screens.OnBoardingScreen.route,
                            enterTransition = { fadeIn(animationSpec = tween(300)) },
                            exitTransition = { fadeOut(animationSpec = tween(300)) }) {
                            OnboardingScreen(
                                onFinish = {
                                    BackupPrefs.setOnboardingShown(context)
                                    navController.popBackStack()
                                    navController.navigate(BottomTabItem.Jaaps.route)
                                }
                            )
                        }
                        composable(
                            Screens.JaapDetailScreen.route,
                            enterTransition = {
                                slideIntoContainer(
                                    AnimatedContentTransitionScope.SlideDirection.Left,
                                    animationSpec = tween(200)
                                )
                            },
                            exitTransition = {
                                fadeOut(animationSpec = tween(200)) +
                                        slideOutOfContainer(
                                            AnimatedContentTransitionScope.SlideDirection.Left,
                                            animationSpec = tween(
                                                200,
                                                easing = FastOutLinearInEasing
                                            )
                                        )
                            },
                            popEnterTransition = {
                                slideIntoContainer(
                                    AnimatedContentTransitionScope.SlideDirection.Right,
                                    animationSpec = tween(200)
                                )
                            },
                            popExitTransition = {
                                slideOutOfContainer(
                                    AnimatedContentTransitionScope.SlideDirection.Right,
                                    animationSpec = tween(200)
                                )
                            },
                            arguments = listOf(
                                navArgument("jaapId") { type = NavType.IntType },
                            )
                        ) { backStackEntry ->
                            val jaapId = backStackEntry.arguments?.getInt("jaapId") ?: -1
                            JaapDetailScreen(
                                jaapId = jaapId, navController, padding = innerPadding,
                                homeViewModel = viewModel
                            )
                        }
                        composable(Screens.AddJaapDialog.route) {
                            AddMantraDialog(
                                onDismiss = { navController.popBackStack() },
                                onSubmit = { name, size ->
                                    navController.popBackStack()
                                    viewModel.onIntent(HomeIntent.AddMantra(name, DateUtils.getTodayDate(),size))
                                }
                            )
                        }
                        composable(
                            Screens.JaapHistoryScreen.route,
                            arguments = listOf(navArgument("jaapId") { type = NavType.IntType })
                        ) { backStackEntry ->

                            val detailViewModel: JaapDetailViewModel = hiltViewModel()
                            val jaapId = backStackEntry.arguments?.getInt("jaapId") ?: -1
                            JaapHistoryScreen(
                                jaapId = jaapId,
                                viewModel = detailViewModel,
                                homeViewModel = viewModel,
                                padding = innerPadding
                            )
                        }
                        composable(Screens.Settings.route) {
                            SettingsScreen(navController)
                        }
                        composable(BottomTabItem.Jaaps.route) {
                            HomeScreen(
                                viewModel = viewModel,
                                navController,
                                onExit = { finish() },
                                innerPadding
                            )
                        }

                        composable(BottomTabItem.Goals.route) {
                            GoalsScreen(paddingValues = innerPadding)
                        }
                        composable(Screens.AddGoalScreen.route) {
                            AddGoalScreen(paddingValues = innerPadding, navController)
                        }
                    }
                }
            }
        }
    }

}


@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    JaapMalaTheme {
        //HomeScreen()
    }
}