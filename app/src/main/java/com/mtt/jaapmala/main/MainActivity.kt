package com.mtt.jaapmala.main

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.mtt.jaapmala.data.local.db.BackupPrefs
import com.mtt.jaapmala.data.local.db.DatabaseManager
import com.mtt.presentation.ui.screens.AddMantraDialog
import com.mtt.presentation.ui.screens.Screens
import com.mtt.presentation.ui.screens.history.JaapHistoryScreen
import com.mtt.presentation.ui.screens.home.HomeScreen
import com.mtt.presentation.ui.screens.home.HomeViewModel
import com.mtt.presentation.ui.screens.jaap.JaapDetailScreen
import com.mtt.presentation.ui.screens.jaap.JaapDetailViewModel
import com.mtt.presentation.ui.screens.onboarding.OnboardingScreen
import com.mtt.presentation.ui.theme.JaapMalaTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val viewModel: HomeViewModel = hiltViewModel()
            val toolbarTitle = remember { mutableStateOf("Jaap Mala") }
            val navController = rememberNavController()
            val navBackStackEntry by navController.currentBackStackEntryAsState()
            val currentDestination = navBackStackEntry?.destination?.route
            val context = LocalContext.current
            val showOnboarding = remember {
                !BackupPrefs.isOnboardingShown(context)
            }
            val startDestination =
                if (showOnboarding) Screens.OnBoardingScreen.route else Screens.HomeScreen.route

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
                        viewModel.refreshData()
                    }
                }
            }
            // Now safely initialize the backupManager *after* launchers are remembered
            backupManager = remember {
                viewModel.databaseManager.apply {
                    setupActivityLaunchers(backupLauncher, restoreLauncher)
                }
            }

            JaapMalaTheme {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    contentColor = MaterialTheme.colorScheme.background,
                    floatingActionButton = {
                        if (currentDestination == Screens.HomeScreen.route) {
                            SmallFloatingActionButton(
                                shape = CircleShape,
                                onClick = {
                                    navController.navigate(Screens.AddJaapDialog.route)
                                },
                                modifier = Modifier.size(72.dp)
                            ) {
                                Icon(Icons.Filled.Add, "Add")
                            }
                        }
                    }
                ) { innerPadding ->
                    NavHost(
                        navController = navController,
                        startDestination = startDestination,
                    ) {
                        composable(route = Screens.OnBoardingScreen.route) {
                            OnboardingScreen(
                                onFinish = {
                                    BackupPrefs.setOnboardingShown(context)
                                    navController.popBackStack()
                                    navController.navigate(Screens.HomeScreen.route)
                                }
                            )
                        }
                        composable(Screens.HomeScreen.route) {
                            HomeScreen(
                                viewModel,
                                navController,
                                onBackupClick = { backupManager.backupDatabase() },
                                onRestoreClick = { backupManager.restoreDatabase() }
                            )
                            toolbarTitle.value = "Jaap Mala"
                        }
                        composable(
                            Screens.JaapDetailScreen.route,
                            arguments = listOf(navArgument("jaapId") { type = NavType.IntType })
                        ) { backStackEntry ->
                            val jaapId = backStackEntry.arguments?.getInt("jaapId") ?: -1
                            JaapDetailScreen(jaapId = jaapId,navController, setTitle = { title ->
                                toolbarTitle.value = title
                            })
                        }
                        composable(Screens.AddJaapDialog.route) {
                            AddMantraDialog(
                                onDismiss = { navController.popBackStack() },
                                onSubmit = { name, size ->
                                    navController.popBackStack()
                                    viewModel.addMantra(name, size)
                                }
                            )
                        }
                        composable(Screens.JaapHistoryScreen.route,
                            arguments = listOf(navArgument("jaapId") { type = NavType.IntType })
                        ) {backStackEntry ->

                            val viewModel: JaapDetailViewModel = hiltViewModel()
                            val jaapId = backStackEntry.arguments?.getInt("jaapId") ?: -1
                            JaapHistoryScreen(
                                jaapId,
                                viewModel = viewModel,
                                onBack = { navController.popBackStack() }
                            )
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