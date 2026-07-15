package com.almagribii.goalmate.core.notification

import android.content.Context
import androidx.work.*
import java.util.*
import java.util.concurrent.TimeUnit

object ReminderScheduler {
    private const val REMINDER_WORK_NAME = "daily_goal_reminder"

    fun scheduleDailyReminder(context: Context) {
        val workManager = WorkManager.getInstance(context)

        // Hitung delay sampai jam 9 pagi besok atau hari ini
        val calendar = Calendar.getInstance()
        val now = calendar.timeInMillis
        
        calendar.set(Calendar.HOUR_OF_DAY, 9)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        
        if (calendar.timeInMillis <= now) {
            calendar.add(Calendar.DAY_OF_YEAR, 1)
        }
        
        val delay = calendar.timeInMillis - now

        val reminderRequest = PeriodicWorkRequestBuilder<StreakReminderWorker>(24, TimeUnit.HOURS)
            .setInitialDelay(delay, TimeUnit.MILLISECONDS)
            .setConstraints(
                Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.NOT_REQUIRED)
                    .build()
            )
            .build()

        workManager.enqueueUniquePeriodicWork(
            REMINDER_WORK_NAME,
            ExistingPeriodicWorkPolicy.KEEP,
            reminderRequest
        )
    }
}