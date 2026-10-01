package com.mtt.presentation.ui.screens.jaap

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.navigation.NavController
import com.mtt.jaapmala.R
import com.mtt.jaapmala.data.local.entity.JaapEntity
import com.mtt.jaapmala.util.DateUtils
import com.mtt.jaapmala.util.formatIndianNumber
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

    val shareTitle = stringResource(R.string.share_your_jaap_progress)

    val vibrator = remember {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val manager = context.getSystemService(
                Context.VIBRATOR_MANAGER_SERVICE
            ) as VibratorManager

            manager.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            context.getSystemService(
                Context.VIBRATOR_SERVICE
            ) as Vibrator
        }
    }

    // -------------------------------------------------------------
    // Meditation Sound
    // -------------------------------------------------------------

    LifeCycleAwareSound(viewModel)

    val lifeCycleOwner =
        androidx.lifecycle.compose.LocalLifecycleOwner.current

    DisposableEffect(lifeCycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_START -> {
                    viewModel.onIntent(
                        JaapDetailIntent.OnAppForegrounded
                    )
                }

                Lifecycle.Event.ON_STOP -> {
                    viewModel.onIntent(
                        JaapDetailIntent.OnAppBackgrounded
                    )
                }

                else -> Unit
            }
        }

        lifeCycleOwner.lifecycle.addObserver(observer)

        onDispose {
            lifeCycleOwner.lifecycle.removeObserver(observer)
        }
    }

    // -------------------------------------------------------------
    // Save today's history when leaving the screen
    // -------------------------------------------------------------

    DisposableEffect(Unit) {
        onDispose {
            viewModel.saveHistoryForToday()
            homeViewModel.unregisterCustomActionHandler()
        }
    }

    // -------------------------------------------------------------
    // Load mantra
    // -------------------------------------------------------------

    LaunchedEffect(jaapId) {
        viewModel.onIntent(
            JaapDetailIntent.LoadMantra(jaapId)
        )
    }

    // -------------------------------------------------------------
    // Register top bar actions
    // -------------------------------------------------------------

    LaunchedEffect(viewModel) {
        homeViewModel.registerCustomActionHandler { action, _ ->
            viewModel.onIntent(
                JaapDetailIntent.OnTopBarAction(action)
            )
        }
    }

    // -------------------------------------------------------------
    // Effects
    // -------------------------------------------------------------

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
                    navController.navigate(
                        Screens.JaapHistoryScreen.passJaapId(
                            effect.jaapId
                        )
                    )
                }

                is JaapDetailEffect.ShareText -> {
                    val sendIntent = Intent(Intent.ACTION_SEND).apply {
                        putExtra(
                            Intent.EXTRA_TEXT,
                            effect.shareText
                        )
                        type = "text/plain"
                    }

                    val shareIntent = Intent.createChooser(
                        sendIntent,
                        shareTitle
                    )

                    context.startActivity(shareIntent)
                }

                JaapDetailEffect.NavigateBack -> {
                    navController.popBackStack()
                }

                is JaapDetailEffect.NavigateToInsights -> {
                    navController.navigate(
                        Screens.PracticeInsightsScreen.passJaapId(
                            effect.jaapId
                        )
                    )
                }
            }
        }
    }

    // -------------------------------------------------------------
    // Screen lock / unlock handling
    // -------------------------------------------------------------

    val screenReceiver = remember {
        object : BroadcastReceiver() {

            override fun onReceive(
                context: Context?,
                intent: Intent?
            ) {
                when (intent?.action) {

                    Intent.ACTION_SCREEN_OFF -> {
                        viewModel.onIntent(
                            JaapDetailIntent.OnAppBackgrounded
                        )
                    }

                    Intent.ACTION_USER_PRESENT -> {
                        if (meditationSoundEnabled) {
                            viewModel.onIntent(
                                JaapDetailIntent.OnAppForegrounded
                            )
                        }
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

        context.registerReceiver(
            screenReceiver,
            filter
        )

        onDispose {
            context.unregisterReceiver(screenReceiver)

            viewModel.onIntent(
                JaapDetailIntent.OnAppBackgrounded
            )
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            homeViewModel.unregisterCustomActionHandler()
        }
    }

    // -------------------------------------------------------------
    // Manual entry dialog
    // -------------------------------------------------------------

    if (showManualEntryDialog) {
        ManualJaapEntryDialog(
            onSubmit = { enteredCount ->
                viewModel.onIntent(
                    JaapDetailIntent.DismissManualEntryDialog
                )

                viewModel.onIntent(
                    JaapDetailIntent.SubmitManualEntry(
                        jaapId,
                        enteredCount
                    )
                )
            },
            onDismiss = {
                viewModel.onIntent(
                    JaapDetailIntent.DismissManualEntryDialog
                )
            }
        )
    }

    // -------------------------------------------------------------
    // Content
    // -------------------------------------------------------------

    mantra?.let { detail ->

        LaunchedEffect(detail.name) {
            homeViewModel.onIntent(
                HomeIntent.UpdateTopBar(
                    title = detail.name,
                    actions = listOf(
                        TopBarAction.IncrementCount,
                        TopBarAction.History,
                        TopBarAction.PracticeInsights,
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
                    end = 16.dp,
                    top = padding.calculateTopPadding() + 12.dp,
                    bottom = padding.calculateBottomPadding()
                )
        ) {

            // ---------------------------------------------------------
            // Date
            // ---------------------------------------------------------

            Text(
                text = stringResource(
                    R.string.date,
                    DateUtils.formatDate(detail.date)
                ),
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.align(
                    Alignment.CenterHorizontally
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            // ---------------------------------------------------------
            // Stats
            // ---------------------------------------------------------

            StatsSection(detail)

            Spacer(modifier = Modifier.height(24.dp))

            // ---------------------------------------------------------
            // Undo
            // ---------------------------------------------------------

            Button(
                onClick = {
                    viewModel.onIntent(
                        JaapDetailIntent.DecreaseCount
                    )
                },
                shape = RoundedCornerShape(50),
                colors = ButtonDefaults.buttonColors(
                    containerColor =
                        MaterialTheme.colorScheme.surfaceVariant,
                    contentColor =
                        MaterialTheme.colorScheme.onSurfaceVariant
                ),
                elevation = ButtonDefaults.buttonElevation(
                    defaultElevation = 0.dp
                ),
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .height(42.dp)
            ) {
                Text(
                    text = "Undo",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // ---------------------------------------------------------
            // Counting area
            // ---------------------------------------------------------

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .clickable(
                        interactionSource = remember {
                            MutableInteractionSource()
                        },
                        indication = null
                    ) {
                        viewModel.onIntent(
                            JaapDetailIntent.IncreaseCount
                        )
                    },
                contentAlignment = Alignment.Center
            ) {
                ProgressCountButton(
                    currentCount = detail.count,
                    onClick = {
                        viewModel.onIntent(
                            JaapDetailIntent.IncreaseCount
                        )
                    },
                    malaSize = detail.malaSize
                )
            }
        }

    } ?: run {

        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}


@Composable
fun StatsSection(
    mantra: JaapEntity
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        color = MaterialTheme.colorScheme.surfaceVariant,
        border = BorderStroke(
            width = 1.dp,
            color = MaterialTheme.colorScheme.outlineVariant
        )
    ) {
        Column(
            modifier = Modifier.padding(
                horizontal = 16.dp,
                vertical = 14.dp
            )
        ) {

            // ---------------------------------------------------------
            // Today
            // ---------------------------------------------------------

            Text(
                text = stringResource(R.string.today),
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.align(
                    Alignment.CenterHorizontally
                )
            )

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {

                DetailStat(
                    value = formatIndianNumber(
                        mantra.todayCount
                    ),
                    modifier = Modifier.weight(1f)
                )

                VerticalDivider(
                    modifier = Modifier.height(42.dp),
                    color = MaterialTheme.colorScheme.outlineVariant
                )

                DetailStat(
                    value = formatIndianNumber(
                        mantra.todayMalaCount
                    ),
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${mantra.malaSize}×",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center
                )

                Spacer(
                    modifier = Modifier.width(1.dp)
                )

                Text(
                    text = "Malas",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            HorizontalDivider(
                color = MaterialTheme.colorScheme.outlineVariant
            )

            Spacer(modifier = Modifier.height(14.dp))

            // ---------------------------------------------------------
            // Lifetime
            // ---------------------------------------------------------

            Text(
                text = "Lifetime",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.align(
                    Alignment.CenterHorizontally
                )
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {

                DetailLifetimeStat(
                    title = stringResource(R.string.total),
                    value = formatIndianNumber(
                        mantra.lifetimeCount
                    ),
                    modifier = Modifier.weight(1f)
                )

                VerticalDivider(
                    modifier = Modifier.height(34.dp),
                    color = MaterialTheme.colorScheme.outlineVariant
                )

                DetailLifetimeStat(
                    title = stringResource(
                        R.string.total_x,
                        mantra.malaSize
                    ),
                    value = formatIndianNumber(
                        mantra.lifetimeMalaCount
                    ),
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
fun ProgressCountButton(
    currentCount: Int,
    onClick: () -> Unit,
    malaSize: Int
) {
    val progress = remember(
        currentCount,
        malaSize
    ) {
        if (malaSize > 0) {
            (currentCount % malaSize).toFloat() /
                    malaSize.toFloat()
        } else {
            0f
        }
    }

    val currentMalaCount =
        if (malaSize > 0) {
            currentCount % malaSize
        } else {
            0
        }

    val progressColor =
        MaterialTheme.colorScheme.primary

    val trackColor =
        MaterialTheme.colorScheme.surfaceVariant

    Box(
        modifier = Modifier
            .size(280.dp)
            .clickable(
                indication = null,
                interactionSource = remember {
                    MutableInteractionSource()
                },
                onClick = onClick
            ),
        contentAlignment = Alignment.Center
    ) {

        Canvas(
            modifier = Modifier.fillMaxSize()
        ) {

            // Background ring
            drawCircle(
                color = trackColor,
                style = Stroke(
                    width = 18.dp.toPx()
                )
            )

            // Progress ring
            drawArc(
                color = progressColor,
                startAngle = -90f,
                sweepAngle = 360f * progress,
                useCenter = false,
                style = Stroke(
                    width = 18.dp.toPx(),
                    cap = StrokeCap.Round
                )
            )
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Text(
                text = formatIndianNumber(currentCount),
                style = MaterialTheme.typography.displayMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                softWrap = false
            )

            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = "$currentMalaCount / $malaSize",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun DetailStat(
    value: String,
    modifier: Modifier = Modifier
) {
    Text(
        text = value,
        style = MaterialTheme.typography.headlineMedium,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.onSurface,
        modifier = modifier,
        textAlign = TextAlign.Center,
        maxLines = 1,
        softWrap = false
    )
}

@Composable
private fun DetailLifetimeStat(
    title: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            maxLines = 1,
            softWrap = false
        )

        Spacer(modifier = Modifier.height(2.dp))

        Text(
            text = value,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface,
            maxLines = 1,
            softWrap = false
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




