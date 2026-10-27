package com.shamsicalendar.app.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.text.SpannableString
import android.text.Spanned
import android.text.style.AbsoluteSizeSpan
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.view.View
import android.widget.RemoteViews
import com.shamsicalendar.app.MainActivity
import com.shamsicalendar.app.R
import com.shamsicalendar.app.calendar.DateConverter
import com.shamsicalendar.app.calendar.JalaliCalendar
import com.shamsicalendar.app.data.Holidays

/**
 * ویجت ماه کامل 4×4 - نمایش تقویم کامل ماه با هایلایت امروز
 */
class MonthWidgetProvider : AppWidgetProvider() {

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        appWidgetIds.forEach { id ->
            updateWidget(context, appWidgetManager, id)
        }
    }

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)
        if (intent.action == ACTION_NEXT_MONTH || intent.action == ACTION_PREV_MONTH) {
            // برای سادگی فقط ماه جاری را نمایش می‌دهیم - می‌توان با SharedPreferences حالت را ذخیره کرد
            val mgr = AppWidgetManager.getInstance(context)
            val ids = mgr.getAppWidgetIds(ComponentName(context, MonthWidgetProvider::class.java))
            ids.forEach { updateWidget(context, mgr, it) }
        }
    }

    private fun updateWidget(
        context: Context,
        appWidgetManager: AppWidgetManager,
        widgetId: Int
    ) {
        val today = JalaliCalendar.today()
        val jy = today.year
        val jm = today.month
        val cells = JalaliCalendar.monthGrid(jy, jm)

        val views = RemoteViews(context.packageName, R.layout.widget_month)

        // عنوان ماه
        views.setTextViewText(
            R.id.widget_title,
            "${JalaliCalendar.monthNames[jm - 1]} ${JalaliCalendar.toPersianDigits(jy)}"
        )

        // روزهای هفته - به ترتیب شنبه تا جمعه
        val weekHeaders = intArrayOf(
            R.id.dow_0, R.id.dow_1, R.id.dow_2, R.id.dow_3, R.id.dow_4, R.id.dow_5, R.id.dow_6
        )
        JalaliCalendar.weekDayShort.forEachIndexed { i, name ->
            views.setTextViewText(weekHeaders[i], name)
        }

        // 42 سلول روز
        val dayIds = IntArray(42) { idx ->
            context.resources.getIdentifier("day_$idx", "id", context.packageName)
        }

        cells.forEachIndexed { index, cell ->
            val viewId = dayIds.getOrNull(index) ?: return@forEachIndexed
            when (cell) {
                is JalaliCalendar.GridCell.Empty -> {
                    views.setViewVisibility(viewId, View.INVISIBLE)
                }
                is JalaliCalendar.GridCell.Day -> {
                    views.setViewVisibility(viewId, View.VISIBLE)
                    val isToday = cell.day == today.day
                    val isHoliday = cell.dayOfWeek == 6 || Holidays.isHoliday(jm, cell.day)
                    views.setTextViewText(
                        viewId,
                        JalaliCalendar.toPersianDigits(cell.day)
                    )
                    // تنظیم رنگ پس‌زمینه و متن با استفاده از setBackgroundResource
                    val bgRes = when {
                        isToday -> R.drawable.widget_day_today
                        isHoliday -> R.drawable.widget_day_holiday
                        else -> R.drawable.widget_day_normal
                    }
                    views.setInt(viewId, "setBackgroundResource", bgRes)
                    // رنگ متن
                    val textColor = when {
                        isToday -> Color.BLACK
                        isHoliday -> Color.parseColor("#FF6B6B")
                        else -> Color.parseColor("#F5F5F7")
                    }
                    views.setTextColor(viewId, textColor)

                    val openIntent = Intent(context, MainActivity::class.java)
                    val pi = PendingIntent.getActivity(
                        context, widgetId * 100 + index, openIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                    )
                    views.setOnClickPendingIntent(viewId, pi)
                }
            }
        }

        // کلیک روی عنوان = باز کردن اپ
        val openAppIntent = Intent(context, MainActivity::class.java)
        val openPi = PendingIntent.getActivity(
            context, widgetId, openAppIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        views.setOnClickPendingIntent(R.id.widget_title, openPi)
        views.setOnClickPendingIntent(R.id.widget_root, openPi)

        appWidgetManager.updateAppWidget(widgetId, views)
    }

    companion object {
        const val ACTION_NEXT_MONTH = "com.shamsicalendar.app.NEXT_MONTH"
        const val ACTION_PREV_MONTH = "com.shamsicalendar.app.PREV_MONTH"
    }
}
