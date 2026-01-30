package com.mtt.presentation.ui.screens.settings

import android.Manifest
import android.app.TimePickerDialog
import android.content.pm.PackageManager
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.lerp
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.mtt.jaapmala.domain.model.ThemeOption
import com.mtt.jaapmala.util.DateUtils
import com.mtt.presentation.ui.screens.FontScaledSpacer

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    navController: NavController,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val meditationSoundEnabled by viewModel.meditationSoundEnabled.collectAsState()
    val hapticFeedbackEnabled by viewModel.hapticFeedbackEnabled.collectAsState()
    val hapticFeedbackFrequency by viewModel.hapticFeedbackFrequency.collectAsState()
    val themeOption by viewModel.themeOption.collectAsState()
    val isReminderEnabled by viewModel.isDailyReminderEnabled.collectAsState()
    val reminderTime by viewModel.reminderTime.collectAsState()
    val context = LocalContext.current
    // Launcher to request POST_NOTIFICATIONS
    val notificationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (!isGranted) {
            Toast.makeText(context, "Notifications disabled", Toast.LENGTH_SHORT).show()
            // reset reminder if permission not granted
            viewModel.toggleDailyReminder(false)
        }else{
            viewModel.toggleDailyReminder(true)
        }
    }
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                modifier = Modifier.shadow(elevation = 10.dp),
                title = { Text("Settings") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary,
                    actionIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->

        Column(
            modifier = Modifier
                .padding(
                    top = padding.calculateTopPadding(),
                    start = 20.dp,
                    end = 20.dp,
                    bottom = padding.calculateBottomPadding()
                )
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
        ) {
            // Daily Reminder Toggle
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    "Daily Reminder",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Spacer(modifier = Modifier.weight(1f))
                Switch(
                    checked = isReminderEnabled,
                    onCheckedChange = { enabled ->
                        if (enabled) {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                val hasPermission = ContextCompat.checkSelfPermission(
                                    context,
                                    Manifest.permission.POST_NOTIFICATIONS
                                ) == PackageManager.PERMISSION_GRANTED

                                if (!hasPermission) {
                                    notificationPermissionLauncher.launch(
                                        Manifest.permission.POST_NOTIFICATIONS
                                    )
                                } else {
                                    viewModel.toggleDailyReminder(true)
                                }
                            } else {
                                viewModel.toggleDailyReminder(true)
                            }
                        } else {
                            viewModel.toggleDailyReminder(false)
                        }
                    }
                )
            }

            // Reminder Time (only if enabled)
            if (isReminderEnabled) {
                FontScaledSpacer(startHeight = 8.dp, endHeight = 16.dp)
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            // Open Android TimePickerDialog
                            val (hour, minute) = reminderTime.split(":").map { it.toInt() }
                            TimePickerDialog(
                                context,
                                { _, selectedHour, selectedMinute ->
                                    val newTime = "%02d:%02d".format(selectedHour, selectedMinute)
                                    viewModel.updateReminderTime(newTime)
                                },
                                hour,
                                minute,
                                false
                            ).show()
                        }
                        .padding(vertical = 8.dp)
                ) {
                    Text(
                        "Reminder Time",
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    Text(
                        DateUtils.formatTimeTo12Hour(reminderTime),
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            FontScaledSpacer(startHeight = 24.dp, endHeight = 48.dp)

            // Meditation Sound Section
            Text(
                "Sound & Feedback",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onBackground
            )
            FontScaledSpacer(startHeight = 8.dp, endHeight = 16.dp)
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    "Enable Meditation Sound",
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.weight(1f)
                )
                Switch(
                    checked = meditationSoundEnabled,
                    onCheckedChange = { viewModel.toggleMeditationSound(it) }
                )
            }

            // Haptic feedback
            FontScaledSpacer(startHeight = 8.dp, endHeight = 16.dp)
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    "Haptic Feedback",
                    color = MaterialTheme.colorScheme.onBackground
                )
                Spacer(modifier = Modifier.weight(1f))
                Switch(
                    checked = hapticFeedbackEnabled,
                    onCheckedChange = { viewModel.toggleHapticFeedback(it) }
                )
            }

            if (hapticFeedbackEnabled) {
                val fontScale = LocalDensity.current.fontScale
                val fraction = ((fontScale - 1f) / 1f).coerceIn(0f, 1f)
                val spacerHeight = lerp(8.dp, 48.dp, fraction)

                Spacer(modifier = Modifier.height(spacerHeight))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        "Vibrate every $hapticFeedbackFrequency jaaps",
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }
                Slider(
                    value = hapticFeedbackFrequency.toFloat(),
                    onValueChange = { viewModel.setHapticFeedbackFrequency(it.toInt()) },
                    valueRange = 1f..108f,
                    steps = 107
                )
            }

            val fontScale = LocalDensity.current.fontScale
            val fraction = ((fontScale - 1f) / 1f).coerceIn(0f, 1f)
            val spacerHeight = lerp(24.dp, 48.dp, fraction)

            Spacer(modifier = Modifier.height(spacerHeight))

            // Theme Section
            Text(
                "Theme",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(modifier = Modifier.height(8.dp))
            ThemeOption.entries.forEach { option ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { viewModel.updateTheme(option) }
                        .padding(vertical = 8.dp)
                ) {
                    RadioButton(
                        selected = themeOption == option,
                        onClick = { viewModel.updateTheme(option) }
                    )
                    Text(
                        option.displayName,
                        modifier = Modifier.padding(start = 8.dp),
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }
            }
        }
    }
}
