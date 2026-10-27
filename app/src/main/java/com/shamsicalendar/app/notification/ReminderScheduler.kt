package com.shamsicalendar.app.notification

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.shamsicalendar.app.MainActivity
import com.shamsicalendar.app.R
import com.shamsicalendar.app.calendar.JalaliCalendar
import com.shamsicalendar.app.data.Event
import com.shamsicalendar.app.data.EventDatabase
import java.util.Calendar
import java.util.GregorianCalendar
import java.util.concurrent.TimeUnit

const val CHANNEL_ID = "shamsi_events"
const val CHANNEL_NAME = "یادآور رویدادها"

object ReminderScheduler {

    fun createChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val ch = NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH).apply {
                description = "اعلان‌های یادآور رویدادهای تقویم"
                enableVibration(true)
            }
            context.getSystemService(NotificationManager::class.java).createNotificationChannel(ch)
        }
    }

    fun schedule(context: Context, event: Event) {
        if (event.reminderMinutes <= 0) return
        createChannel(context)

        // محاسبه زمان میلادی برای رویداد
        val (jy, jm, jd) = event.parseJalali()
        val (gy, gm, gd) = JalaliCalendar.jalaliToGregorian(jy, jm, jd)
        val cal = GregorianCalendar(gy, gm - 1, gd, event.hour ?: 9, event.minute ?: 0)
        val triggerAt = cal.timeInMillis - event.reminderMinutes * 60L * 1000L
        if (triggerAt < System.currentTimeMillis()) return

        val intent = Intent(context, ReminderReceiver::class.java).apply {
            putExtra("event_id", event.id)
            putExtra("title", event.title)
            putExtra("desc", event.description)
        }
        val pi = PendingIntent.getBroadcast(
            context, event.id.toInt(), intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        val am = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            am.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerAt, pi)
        } else {
            am.setExact(AlarmManager.RTC_WAKEUP, triggerAt, pi)
        }
    }

    fun cancel(context: Context, eventId: Int) {
        val intent = Intent(context, ReminderReceiver::class.java)
        val pi = PendingIntent.getBroadcast(
            context, eventId, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        val am = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        am.cancel(pi)
    }
}

class ReminderReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val id = intent.getLongExtra("event_id", -1).toInt()
        val title = intent.getStringExtra("title") ?: "رویداد"
        val desc = intent.getStringExtra("desc") ?: ""

        val notif = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(title)
            .setContentText(desc.ifEmpty { "یادآور رویداد" })
            .setStyle(NotificationCompat.BigTextStyle().bigText(desc))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(
                PendingIntent.getActivity(
                    context, id,
                    Intent(context, MainActivity::class.java),
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )
            )
            .build()

        val nm = context.getSystemService(NotificationManager::class.java)
        nm.notify(id, notif)
    }
}
