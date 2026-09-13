package com.mindbloom.app

import android.app.Application
import com.mindbloom.app.data.seed.DemoDataSeeder
import com.mindbloom.app.di.AppContainer
import com.mindbloom.app.notifications.NotificationHelper
import com.mindbloom.app.notifications.ReminderScheduler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class MindBloomApplication : Application() {

    lateinit var container: AppContainer
        private set

    private val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun onCreate() {
        super.onCreate()
        container = AppContainer.get(this)

        NotificationHelper.createChannel(this)
        ReminderScheduler.apply(
            context = this,
            enabled = container.preferences.notificationsEnabled,
            timeLabel = container.preferences.reminderTime
        )

        applicationScope.launch {
            DemoDataSeeder.seedIfNeeded(container.database, container.preferences)
        }
    }
}
