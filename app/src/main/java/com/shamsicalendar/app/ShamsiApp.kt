package com.shamsicalendar.app

import android.app.Application
import com.shamsicalendar.app.notification.ReminderScheduler

class ShamsiApp : Application() {
    override fun onCreate() {
        super.onCreate()
        ReminderScheduler.createChannel(this)
    }
}
