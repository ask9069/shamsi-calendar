package com.shamsicalendar.app.widget

import android.app.AlarmManager
import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.content.BroadcastReceiver
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.SystemClock
import android.service.quicksettings.TileService
import android.util.Log
import java.time.Duration
import java.time.ZoneId
import java.time.ZonedDateTime

/**
 * گیرنده به‌روزرسانی ویجت‌ها
 *
 * دو کار انجام می‌دهد:
 * 1. هر روز نیمه‌شب (00:01 به وقت تهران) ویجت‌ها را refresh می‌کند تا روز جدید نمایش داده شود
 * 2. به‌روزرسانی خودکار پس از Boot، تغییر زمان، تغییر منطقه‌ زمانی
 */
class WidgetUpdateReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        Log.d("WidgetUpdate", "Received: ${intent.action}")

        // به‌روزرسانی ویجت ماه
        refreshWidgets(context)

        // تنظیم alarm بعدی برای نیمه‌شب
        scheduleNextRefresh(context)
    }

    companion object {
        private const val ACTION_REFRESH = "com.shamsicalendar.app.REFRESH_WIDGET"
        private const val REQUEST_CODE = 872

        /**
         * به‌روزرسانی همه ویجت‌ها
         */
        fun refreshWidgets(context: Context) {
            val mgr = AppWidgetManager.getInstance(context)

            // ویجت ماه
            val monthIds = mgr.getAppWidgetIds(ComponentName(context, MonthWidgetProvider::class.java))
            if (monthIds.isNotEmpty()) {
                val intent = Intent(context, MonthWidgetProvider::class.java).apply {
                    action = AppWidgetManager.ACTION_APPWIDGET_UPDATE
                    putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, monthIds)
                }
                context.sendBroadcast(intent)
            }

            // ویجت مینی
            val miniIds = mgr.getAppWidgetIds(ComponentName(context, MiniWidgetProvider::class.java))
            if (miniIds.isNotEmpty()) {
                val intent = Intent(context, MiniWidgetProvider::class.java).apply {
                    action = AppWidgetManager.ACTION_APPWIDGET_UPDATE
                    putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, miniIds)
                }
                context.sendBroadcast(intent)
            }
        }

        /**
         * تنظیم AlarmManager برای به‌روزرسانی نیمه‌شب هر روز
         * این روش از اپ تایم استمپ استخراج شده و برای refresh روزانه ضروری است
         */
        fun scheduleNextRefresh(context: Context) {
            val alarmManager = context.getSystemService(AlarmManager::class.java) ?: return
            val zone = ZoneId.of("Asia/Tehran")
            val now = ZonedDateTime.now(zone)

            // زمان بعدی: 00:01 روز بعد (به وقت تهران)
            val nextMidnight = now.toLocalDate().plusDays(1).atStartOfDay(zone).plusMinutes(1)

            // محاسبه فاصله به میلی‌ثانیه - حداقل 1 دقیقه
            val delayMs = Math.max(
                Duration.between(now, nextMidnight).toMillis(),
                60_000L
            )

            val triggerAt = SystemClock.elapsedRealtime() + delayMs

            val intent = Intent(context, WidgetUpdateReceiver::class.java).apply {
                action = ACTION_REFRESH
                `package` = context.packageName
            }
            val pendingIntent = PendingIntent.getBroadcast(
                context,
                REQUEST_CODE,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            // RTC_WAKEUP برای بیدار کردن دستگاه در زمان مشخص
            alarmManager.setAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                System.currentTimeMillis() + delayMs,
                pendingIntent
            )

            Log.d("WidgetUpdate", "Next refresh scheduled in ${delayMs / 1000 / 60} minutes")
        }
    }
}
