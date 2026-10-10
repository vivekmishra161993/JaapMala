package com.mtt.presentation.ui.screens.settings

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.mtt.jaapmala.domain.usecase.reminder.GetDailyReminderEnabledUseCase
import com.mtt.jaapmala.domain.usecase.reminder.GetReminderTimeUseCase
import com.mtt.jaapmala.util.AppLogger
import com.mtt.jaapmala.util.ReminderScheduler
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class BootReceiver : BroadcastReceiver() {

    @Inject
    lateinit var reminderScheduler: ReminderScheduler

    @Inject
    lateinit var getDailyReminderEnabledUseCase: GetDailyReminderEnabledUseCase

    @Inject
    lateinit var getReminderTimeUseCase: GetReminderTimeUseCase

    override fun onReceive(
        context: Context,
        intent: Intent
    ) {
        if (intent.action != Intent.ACTION_BOOT_COMPLETED) {
            return
        }

        AppLogger.log(
            context,
            "BootReceiver",
            "BOOT_COMPLETED received"
        )

        val pendingResult = goAsync()

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val enabled = getDailyReminderEnabledUseCase().first()

                AppLogger.log(
                    context,
                    "BootReceiver",
                    "Reminder enabled = $enabled"
                )

                if (enabled) {
                    val time = getReminderTimeUseCase().first()

                    AppLogger.log(
                        context,
                        "BootReceiver",
                        "Scheduling reminder for $time"
                    )

                    val (hour, minute) = time
                        .split(":")
                        .map { it.toInt() }

                    reminderScheduler.scheduleReminder(
                        hour = hour,
                        minute = minute
                    )
                }
            } catch (e: Exception) {
                AppLogger.log(
                    context,
                    "BootReceiver",
                    "Failed to schedule reminder after boot: ${e.message}"
                )
            } finally {
                pendingResult.finish()
            }
        }
    }
}