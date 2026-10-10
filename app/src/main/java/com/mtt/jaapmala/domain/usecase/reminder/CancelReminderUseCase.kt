package com.mtt.jaapmala.domain.usecase.reminder

import com.mtt.jaapmala.util.ReminderScheduler
import javax.inject.Inject

class CancelReminderUseCase @Inject constructor(
    private val reminderScheduler: ReminderScheduler
) {
    operator fun invoke() {
        reminderScheduler.cancelDailyReminder()
    }
}