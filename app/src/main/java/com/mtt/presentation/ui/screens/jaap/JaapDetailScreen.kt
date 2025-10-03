package com.mtt.presentation.ui.screens.jaap

import android.Manifest
import android.content.Context
import android.media.MediaPlayer
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import androidx.activity.ComponentActivity
import androidx.annotation.RequiresPermission
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.mtt.jaapmala.R
import com.mtt.jaapmala.data.local.entity.JaapEntity
import com.mtt.jaapmala.util.DateUtils
import com.mtt.jaapmala.util.UIEvent
import com.mtt.jaapmala.util.disableImmersiveMode
import com.mtt.jaapmala.util.enableImmersiveMode
import com.mtt.presentation.ui.screens.Screens
import com.mtt.presentation.ui.screens.app_bar.TopAppBarWithMenu
import com.mtt.presentation.ui.screens.app_bar.TopBarAction
import kotlinx.coroutines.flow.collectLatest

@Composable
fun JaapDetailScreen(
    jaapId: Int,
    navController: NavController,
    setTitle: (String) -> Unit,
) {
    val viewModel: JaapDetailViewModel = hiltViewModel()
    val context = LocalContext.current
    val mantra by viewModel.mantra.collectAsState()
    val showDialog by viewModel.showManualEntryDialog.collectAsState()
    val topBarState by viewModel.topBarState.collectAsState()
    LaunchedEffect(Unit) {
        // Enable immersive mode when this screen appears
        (context as? ComponentActivity)?.enableImmersiveMode()
    }
    LaunchedEffect(jaapId) {
        viewModel.getMantra(jaapId)
    }

    LaunchedEffect(Unit) {
        viewModel.uiEvent.collectLatest { event ->
            when (event) {
                is UIEvent.TriggerFeedback -> triggerFeedback(context)
            }
        }
    }
    LaunchedEffect(Unit) {
        viewModel.topBarEvent.collect { action ->
            when (action) {
                is TopBarAction.History -> { navController.navigate(Screens.JaapHistoryScreen.passJaapId(jaapId))}
                else -> Unit
            }
        }
    }
    DisposableEffect(Unit) {
        onDispose {
            viewModel.saveHistoryForToday()
        }
    }
    // Disable immersive mode when leaving this screen
    DisposableEffect(Unit) {
        onDispose {
            (context as? ComponentActivity)?.disableImmersiveMode()
        }
    }

// Show dialog
    if (showDialog) {
        ManualJaapEntryDialog(
            onSubmit = { enteredCount ->
                viewModel.dismissManualEntryDialog()
                viewModel.updateJaapCountManually(jaapId, enteredCount)
            },
            onDismiss = {
                viewModel.dismissManualEntryDialog()
            }
        )
    }
    mantra?.let { detail ->
        setTitle(detail.name)
        Scaffold(topBar = {
            TopAppBarWithMenu(
                topBarState,
                onActionSelected = { viewModel.onTopBarAction(it,context) },
                onBack = {navController.popBackStack()}
            )
        }) { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(
                        start = 16.dp,
                        bottom = padding.calculateBottomPadding(),
                        end = 16.dp,
                        top = padding.calculateTopPadding() + 20.dp
                    )
            ) {
                Text(
                    text = "Date: ${DateUtils.formatDate(detail.date)}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )

                Spacer(modifier = Modifier.height(24.dp))

                StatsSection(detail)

                Spacer(modifier = Modifier.height(36.dp))

                // Undo button works independently
                Button(
                    onClick = { viewModel.decreaseCount() },
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .size(100.dp)
                ) {
                    Text("Undo")
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Whole lower half is clickable
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f) // take all remaining lower half space
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) {
                            viewModel.increaseCount()
                        },
                    contentAlignment = Alignment.Center
                ) {
                    ProgressCountButton(
                        currentCount = detail.count,
                        onClick = { viewModel.increaseCount() }, // still clickable on progress button
                        malaSize = detail.malaSize
                    )
                }
            }
        }
    } ?: run {
        Box(
            modifier = Modifier.fillMaxSize()
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) {},
            contentAlignment = Alignment.Center,
        ) {
            CircularProgressIndicator()
        }
    }

}

@Composable
fun StatsSection(mantra: JaapEntity) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 10.dp)
    ) {
        Column(
            modifier = Modifier.weight(1f),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                "Today",
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onBackground,
                fontSize = 16.sp
            )
            Text(
                "${mantra.todayCount}",
                color = MaterialTheme.colorScheme.onBackground,
                fontSize = 20.sp
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                "Total",
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onBackground,
                fontSize = 16.sp
            )
            Text(
                "${mantra.lifetimeCount}",
                color = MaterialTheme.colorScheme.onBackground,
                fontSize = 20.sp
            )
        }
        Column(
            modifier = Modifier.weight(1f),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                "Today Mala",
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onBackground,
                fontSize = 16.sp
            )
            Text(
                "${mantra.todayMalaCount}",
                color = MaterialTheme.colorScheme.onBackground,
                fontSize = 20.sp
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                "Lifetime Mala",
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onBackground,
                fontSize = 16.sp
            )
            Text(
                "${mantra.lifetimeMalaCount}",
                color = MaterialTheme.colorScheme.onBackground,
                fontSize = 20.sp
            )
        }
    }
}

@Composable
fun ProgressCountButton(currentCount: Int, onClick: () -> Unit, malaSize: Int) {
    val progress = (currentCount % malaSize).toFloat() / malaSize.toFloat()

    Box(
        modifier = Modifier
            .size(280.dp)
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawCircle(
                color = Color.LightGray,
                style = Stroke(width = 20f)
            )
            drawArc(
                color = Color(0xFF4CAF50),
                startAngle = -90f,
                sweepAngle = 360f * progress,
                useCenter = false,
                style = Stroke(width = 20f, cap = StrokeCap.Round)
            )
        }
        Text(
            text = "$currentCount",
            fontSize = 56.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground,
        )
    }
}

@Preview(showBackground = true)
@Composable
fun JaapDetailScreenPreview() {
    val dummy = JaapEntity(
        id = 1,
        name = "Gayatri Mantra",
        date = "19/04/2025",
        count = 54,
        todayCount = 54,
        todayMalaCount = 0,
        lifetimeCount = 154,
        lifetimeMalaCount = 1
    )
    StatsSection(dummy)
}

@RequiresPermission(Manifest.permission.VIBRATE)
private fun triggerFeedback(context: Context) {
    val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        vibrator.vibrate(
            VibrationEffect.createOneShot(300, VibrationEffect.DEFAULT_AMPLITUDE)
        )
    } else {
        // Deprecated method for older devices
        vibrator.vibrate(300)
    }

    playMalaCompletionSound(context)
}

private fun playMalaCompletionSound(context: Context) {
    val mediaPlayer = MediaPlayer.create(context, R.raw.bell)
    mediaPlayer.setOnCompletionListener {
        it.release() // Always release the player after playback
    }
    mediaPlayer.start()
}




