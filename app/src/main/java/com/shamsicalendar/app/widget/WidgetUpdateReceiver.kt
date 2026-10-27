package com.shamsicalendar.app.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import com.shamsicalendar.app.MainActivity

/**
 * گیرنده‌ای برای به‌روزرسانی ویجت‌ها پس از تغییر در رویدادها
 * یا تغییر تاریخ (Boot، TIME_SET، DATE_CHANGED)
 */
class WidgetUpdateReceiver : android.content.BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val mgr = AppWidgetManager.getInstance(context)
        val monthIds = mgr.getAppWidgetIds(ComponentName(context, MonthWidgetProvider::class.java))
        if (monthIds.isNotEmpty()) {
            val intent = Intent(context, MonthWidgetProvider::class.java).apply {
                action = AppWidgetManager.ACTION_APPWIDGET_UPDATE
                putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, monthIds)
            }
            context.sendBroadcast(intent)
        }
        val miniIds = mgr.getAppWidgetIds(ComponentName(context, MiniWidgetProvider::class.java))
        if (miniIds.isNotEmpty()) {
            val intent = Intent(context, MiniWidgetProvider::class.java).apply {
                action = AppWidgetManager.ACTION_APPWIDGET_UPDATE
                putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, miniIds)
            }
            context.sendBroadcast(intent)
        }
    }
}
