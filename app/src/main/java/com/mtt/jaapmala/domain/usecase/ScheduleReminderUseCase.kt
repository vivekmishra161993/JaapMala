package com.mtt.jaapmala.domain.usecase

import com.mtt.jaapmala.util.ReminderScheduler
import javax.inject.Inject

class ScheduleReminderUseCase @Inject constructor(
    private val reminderScheduler: ReminderScheduler
) {
    operator fun invoke(time: String) {
        val (hour, minute) = time.split(":").map { it.toInt() }
        reminderScheduler.scheduleReminder(hour, minute)
    }
}
