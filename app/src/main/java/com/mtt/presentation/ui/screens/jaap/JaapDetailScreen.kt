package com.mtt.presentation.ui.screens.jaap

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.navigation.NavController
import com.mtt.jaapmala.data.local.entity.JaapEntity
import com.mtt.jaapmala.util.DateUtils
import com.mtt.jaapmala.util.formatIndianNumber
import com.mtt.presentation.ui.screens.FontScaledSpacer
import com.mtt.presentation.ui.screens.Screens
import com.mtt.presentation.ui.screens.app_bar.TopBarAction
import com.mtt.presentation.ui.screens.home.HomeIntent
import com.mtt.presentation.ui.screens.home.HomeViewModel

@Composable
fun JaapDetailScreen(
    jaapId: Int,
    navController: NavController,
    padding: PaddingValues,
    homeViewModel: HomeViewModel
) {
    val viewModel: JaapDetailViewModel = hiltViewModel()
    val context = LocalContext.current
    val state by viewModel.state.collectAsState()
    val meditationSoundEnabled = state.isMeditationSoundEnabled
    val showManualEntryDialog = state.showManualEntryDialog
    val mantra = state.mantra

    val vibrator = remember {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val manager = context.getSystemService(
                Context.VIBRATOR_MANAGER_SERVICE
            ) as VibratorManager
            manager.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        }
    }
    //Meditation Sound
    LifeCycleAwareSound(viewModel)
    val lifeCycleOwner = androidx.lifecycle.compose.LocalLifecycleOwner.current

    DisposableEffect(lifeCycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_START -> {
                    viewModel.onIntent(JaapDetailIntent.OnAppForegrounded)
                }

                Lifecycle.Event.ON_STOP -> {
                    viewModel.onIntent(JaapDetailIntent.OnAppBackgrounded)
                }

                else -> Unit
            }
        }
        lifeCycleOwner.lifecycle.addObserver(
            observer
        )
        onDispose {
            lifeCycleOwner.lifecycle.removeObserver(observer)
        }
    }
    DisposableEffect(Unit) {
        onDispose {
            viewModel.saveHistoryForToday()
            homeViewModel.unregisterCustomActionHandler()
        }
    }

    LaunchedEffect(jaapId) {
        viewModel.onIntent(JaapDetailIntent.LoadMantra(jaapId))
    }
    LaunchedEffect(viewModel) {
        homeViewModel.registerCustomActionHandler { action, _ ->
            viewModel.onIntent(JaapDetailIntent.OnTopBarAction(action))
        }
    }
    LaunchedEffect(viewModel) {
        viewModel.effect.collect { effect ->
            when (effect) {
                JaapDetailEffect.TriggerHaptic -> {
                    if (vibrator.hasVibrator()) {
                        vibrator.vibrate(
                            VibrationEffect.createOneShot(
                                30L,
                                VibrationEffect.DEFAULT_AMPLITUDE
                            )
                        )
                    }
                }

                is JaapDetailEffect.NavigateToHistory -> {
                    navController.navigate(Screens.JaapHistoryScreen.passJaapId(effect.jaapId))
                }

                is JaapDetailEffect.ShareText -> {
                    val sendIntent = Intent(Intent.ACTION_SEND).apply {
                        putExtra(Intent.EXTRA_TEXT, effect.shareText)
                        type = "text/plain"
                    }
                    val shareIntent = Intent.createChooser(sendIntent, "Share your Jaap progress")
                    context.startActivity(shareIntent)
                }

                is JaapDetailEffect.NavigateBack -> {
                    navController.popBackStack()
                }
            }
        }
    }

    // --- Meditation Sound Handling with Screen Lock/Unlock ---
    val screenReceiver = remember {
        object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                when (intent?.action) {
                    Intent.ACTION_SCREEN_OFF -> viewModel.onIntent(JaapDetailIntent.OnAppBackgrounded)
                    Intent.ACTION_USER_PRESENT -> {
                        if (meditationSoundEnabled)
                            viewModel.onIntent(JaapDetailIntent.OnAppForegrounded)
                    }
                }
            }
        }
    }

    DisposableEffect(Unit) {
        val filter = IntentFilter().apply {
            addAction(Intent.ACTION_SCREEN_OFF)
            addAction(Intent.ACTION_USER_PRESENT)
        }
        context.registerReceiver(screenReceiver, filter)

        // Also handle normal composable dispose
        onDispose {
            context.unregisterReceiver(screenReceiver)
            viewModel.onIntent(JaapDetailIntent.OnAppBackgrounded)
        }
    }
    DisposableEffect(Unit) {
        onDispose {
            homeViewModel.unregisterCustomActionHandler()
        }
    }

    // Show dialog
    if (showManualEntryDialog) {
        ManualJaapEntryDialog(
            onSubmit = { enteredCount ->
                viewModel.onIntent(JaapDetailIntent.DismissManualEntryDialog)
                viewModel.onIntent(JaapDetailIntent.SubmitManualEntry(jaapId, enteredCount))
            },
            onDismiss = { viewModel.onIntent(JaapDetailIntent.DismissManualEntryDialog) }
        )
    }

    mantra?.let { detail ->
        LaunchedEffect(detail.name) {
            homeViewModel.onIntent(
                HomeIntent.UpdateTopBar(
                    title = detail.name,
                    actions = listOf(
                        TopBarAction.IncrementCount,
                        TopBarAction.History,
                        TopBarAction.Share
                    )
                )
            )
        }
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

            FontScaledSpacer(startHeight = 24.dp, endHeight = 32.dp)
            StatsSection(detail)
            Spacer(modifier = Modifier.height(36.dp))

            val fontScale = LocalDensity.current.fontScale
            val buttonSize = (100 * fontScale).dp.coerceIn(100.dp, 140.dp)

            Button(
                onClick = { viewModel.onIntent(JaapDetailIntent.DecreaseCount) },
                shape = CircleShape,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .size(buttonSize)
            ) {
                Text(
                    text = "Undo",
                    maxLines = 1,
                    softWrap = false
                )
            }


            Spacer(modifier = Modifier.height(24.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) { viewModel.onIntent(JaapDetailIntent.IncreaseCount) },
                contentAlignment = Alignment.Center
            ) {
                ProgressCountButton(
                    currentCount = detail.count,
                    onClick = { viewModel.onIntent(JaapDetailIntent.IncreaseCount) },
                    malaSize = detail.malaSize
                )
            }
        }

    } ?: run {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) {},
            contentAlignment = Alignment.Center,
        ) { CircularProgressIndicator() }
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
                formatIndianNumber(mantra.todayCount),
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
                formatIndianNumber(mantra.lifetimeCount),
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
                formatIndianNumber(mantra.lifetimeMalaCount),
                color = MaterialTheme.colorScheme.onBackground,
                fontSize = 20.sp
            )
        }
    }
}

@Composable
fun ProgressCountButton(currentCount: Int, onClick: () -> Unit, malaSize: Int) {
    var progress = 0f
    if (malaSize != 0) {
        progress = (currentCount % malaSize).toFloat() / malaSize.toFloat()
    }

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




