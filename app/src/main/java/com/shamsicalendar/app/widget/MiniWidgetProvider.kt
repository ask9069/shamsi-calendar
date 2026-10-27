package com.shamsicalendar.app.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.view.View
import android.widget.RemoteViews
import com.shamsicalendar.app.MainActivity
import com.shamsicalendar.app.R
import com.shamsicalendar.app.calendar.DateConverter
import com.shamsicalendar.app.calendar.JalaliCalendar
import com.shamsicalendar.app.data.Holidays

/**
 * ویجت مینی 2×2 - فقط نمایش تاریخ امروز به صورت بزرگ
 */
class MiniWidgetProvider : AppWidgetProvider() {

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        appWidgetIds.forEach { id ->
            updateWidget(context, appWidgetManager, id)
        }
    }

    private fun updateWidget(
        context: Context,
        appWidgetManager: AppWidgetManager,
        widgetId: Int
    ) {
        val today = JalaliCalendar.today()
        val dow = JalaliCalendar.dayOfWeek(today.year, today.month, today.day)
        val occasions = Holidays.getForDay(today.month, today.day)
        val isHoliday = dow == 6 || occasions.any { it.isHoliday }

        val views = RemoteViews(context.packageName, R.layout.widget_mini)

        // روز ماه
        views.setTextViewText(R.id.mini_day, JalaliCalendar.toPersianDigits(today.day))
        // روز هفته
        views.setTextViewText(R.id.mini_weekday, JalaliCalendar.weekDayNames[dow])
        // نام ماه
        views.setTextViewText(R.id.mini_month, JalaliCalendar.monthNames[today.month - 1])

        // تاریخ‌های میلادی و قمری به صورت کوچک‌تر
        val tri = DateConverter.fromJalali(today.year, today.month, today.day)
        views.setTextViewText(R.id.mini_gregorian, tri.gregorianDisplay)
        views.setTextViewText(R.id.mini_islamic, tri.islamicDisplay)

        // نمایش مناسبت روز در صورت وجود
        if (occasions.isNotEmpty()) {
            views.setViewVisibility(R.id.mini_occasion, View.VISIBLE)
            views.setTextViewText(R.id.mini_occasion, occasions.first().title)
        } else {
            views.setViewVisibility(R.id.mini_occasion, View.GONE)
        }

        // کلیک = باز کردن اپ
        val openIntent = Intent(context, MainActivity::class.java)
        val pi = PendingIntent.getActivity(
            context, widgetId, openIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        views.setOnClickPendingIntent(R.id.mini_root, pi)

        appWidgetManager.updateAppWidget(widgetId, views)
    }
}
