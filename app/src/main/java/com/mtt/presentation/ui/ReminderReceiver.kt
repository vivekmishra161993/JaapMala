package com.mtt.presentation.ui

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.mtt.jaapmala.R
import com.mtt.jaapmala.util.ReminderScheduler
import com.mtt.presentation.ui.screens.main.MainActivity
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class ReminderReceiver : BroadcastReceiver() {

    @Inject
    lateinit var reminderScheduler: ReminderScheduler

    override fun onReceive(
        context: Context,
        intent: Intent
    ) {
        val hour = intent.getIntExtra(
            ReminderScheduler.EXTRA_HOUR,
            -1
        )

        val minute = intent.getIntExtra(
            ReminderScheduler.EXTRA_MINUTE,
            -1
        )

        showNotification(context)

        if (hour >= 0 && minute >= 0) {
            reminderScheduler.scheduleReminder(
                hour = hour,
                minute = minute
            )
        }
    }

    private fun showNotification(context: Context) {
        val notificationManager =
            context.getSystemService(
                Context.NOTIFICATION_SERVICE
            ) as NotificationManager

        val channelId = "jaap_reminder_channel"

        val channel = NotificationChannel(
            channelId,
            "Jaap Reminder",
            NotificationManager.IMPORTANCE_HIGH
        )

        notificationManager.createNotificationChannel(channel)

        val openIntent = Intent(
            context,
            MainActivity::class.java
        ).apply {
            flags =
                Intent.FLAG_ACTIVITY_NEW_TASK or
                        Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            openIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or
                    PendingIntent.FLAG_IMMUTABLE
        )

        val notification =
            NotificationCompat.Builder(
                context,
                channelId
            )
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle(context.getString(R.string.daily_jaap_reminder))
                .setContentText(
                    context.getString(R.string.take_a_moment_to_complete_your_jaaps_today)
                )
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .build()

        notificationManager.notify(1, notification)
    }
}
