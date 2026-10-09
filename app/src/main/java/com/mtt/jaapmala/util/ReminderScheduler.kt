package com.mtt.jaapmala.util

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.mtt.presentation.ui.ReminderReceiver
import dagger.hilt.android.qualifiers.ApplicationContext
import java.util.Calendar
import java.util.Date
import javax.inject.Inject

class ReminderScheduler @Inject constructor(
    @ApplicationContext private val context: Context
) {

    fun scheduleReminder(hour: Int, minute: Int) {

        val alarmManager =
            context.getSystemService(
                Context.ALARM_SERVICE
            ) as AlarmManager

        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)

            if (timeInMillis <= System.currentTimeMillis()) {
                add(Calendar.DAY_OF_YEAR, 1)
            }
        }

        val intent = Intent(
            context,
            ReminderReceiver::class.java
        ).apply {
            putExtra(EXTRA_HOUR, hour)
            putExtra(EXTRA_MINUTE, minute)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            REQUEST_CODE,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or
                    PendingIntent.FLAG_IMMUTABLE
        )

        AppLogger.log(
            context,
            "ReminderScheduler",
            "BEFORE setAndAllowWhileIdle: " +
                    "trigger=${calendar.time} " +
                    "millis=${calendar.timeInMillis} " +
                    "now=${Date()}"
        )

        alarmManager.setAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis,
            pendingIntent
        )

        AppLogger.log(
            context,
            "ReminderScheduler",
            "AFTER setAndAllowWhileIdle: " +
                    "trigger=${calendar.time}"
        )
    }

    fun cancelDailyReminder() {

        val alarmManager =
            context.getSystemService(
                Context.ALARM_SERVICE
            ) as AlarmManager

        AppLogger.log(
            context,
            "ReminderScheduler",
            "CANCEL daily reminder called"
        )

        alarmManager.cancel(createPendingIntent())

        AppLogger.log(
            context,
            "ReminderScheduler",
            "CANCEL daily reminder completed"
        )
    }

    private fun createPendingIntent(): PendingIntent {
        val intent = Intent(
            context,
            ReminderReceiver::class.java
        )

        return PendingIntent.getBroadcast(
            context,
            REQUEST_CODE,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or
                    PendingIntent.FLAG_IMMUTABLE
        )
    }

    companion object {
        private const val REQUEST_CODE = 1001

        const val EXTRA_HOUR = "reminder_hour"
        const val EXTRA_MINUTE = "reminder_minute"
    }
}
