package com.shamsicalendar.app.calendar

import java.util.Calendar
import java.util.GregorianCalendar

/**
 * تقویم Jalali (شمسی) - مبتنی بر الگوریتم div/mod معتبر jdf
 * (نسخه استاندارد استفاده‌شده در اپ‌های ایرانی)
 *
 * ماه‌ها: ۶ ماه اول ۳۱ روزه، ۵ ماه بعدی ۳۰ روزه، ماه آخر ۲۹ یا ۳۰ روزه (کبیسه)
 */
object JalaliCalendar {

    data class JDate(
        val year: Int,
        val month: Int,   // 1..12
        val day: Int      // 1..31
    ) {
        override fun toString(): String =
            "${toPersianDigits(year.toString())}/${toPersianDigits(month.toString().padStart(2, '0'))}/${toPersianDigits(day.toString().padStart(2, '0'))}"
    }

    /** تبدیل میلادی به شمسی - الگوریتم استاندارد jdf */
    fun gregorianToJalali(gy: Int, gm: Int, gd: Int): JDate {
        var g_d_m = intArrayOf(0, 31, 59, 90, 120, 151, 181, 212, 243, 273, 304, 334)

        var gy2 = if (gm > 2) gy + 1 else gy
        var days = 355666 + 365 * gy + (gy2 + 3) / 4 - (gy2 + 99) / 100 + (gy2 + 399) / 400 + gd + g_d_m[gm - 1]

        var jy = -1595 + 33 * (days / 12053)
        days %= 12053

        jy += 4 * (days / 1461)
        days %= 1461

        if (days > 365) {
            jy += (days - 1) / 365
            days = (days - 1) % 365
        }

        var jm: Int
        var jd: Int
        if (days < 186) {
            jm = 1 + days / 31
            jd = 1 + (days % 31)
        } else {
            jm = 7 + (days - 186) / 30
            jd = 1 + (days - 186) % 30
        }

        return JDate(jy, jm, jd)
    }

    /** تبدیل شمسی به میلادی - الگوریتم استاندارد jdf */
    fun jalaliToGregorian(jy: Int, jm: Int, jd: Int): Triple<Int, Int, Int> {
        var jy2 = jy - 979
        var days = 365 * jy2 + (jy2 / 33) * 8 + (jy2 % 33 + 3) / 4 + jd
        if (jm < 7) {
            days += (jm - 1) * 31
        } else {
            days += (jm - 7) * 30 + 186
        }

        var gy = 400 * (days / 146097)
        days %= 146097

        var leap = true
        if (days >= 36525) {
            days--
            gy += 100 * (days / 36524)
            days %= 36524
            if (days >= 365) {
                days++
            } else {
                leap = false
            }
        }

        gy += 4 * (days / 1461)
        days %= 1461

        if (days > 365) {
            gy += (days - 1) / 365
            days = (days - 1) % 365
            leap = false
        }

        var sal_a = if (leap) intArrayOf(0, 31, 60, 91, 121, 152, 182, 213, 244, 274, 305, 335)
        else intArrayOf(0, 31, 59, 90, 120, 151, 181, 212, 243, 273, 304, 334)

        var gm = 0
        while (gm < 11 && days >= sal_a[gm + 1]) {
            gm++
        }
        var gd = days - sal_a[gm] + 1

        return Triple(gy + 1600, gm + 1, gd)
    }

    fun isGregorianLeap(year: Int): Boolean =
        (year % 4 == 0 && year % 100 != 0) || (year % 400 == 0)

    /** آیا سال شمسی کبیسه است؟ - بر اساس محاسبه دقیق */
    fun isJalaliLeap(jy: Int): Boolean {
        val cycle = ((jy + 12) % 33 + 33) % 33
        return cycle in setOf(1, 5, 9, 13, 17, 22, 26, 30)
    }

    fun daysInJalaliMonth(jy: Int, jm: Int): Int = when (jm) {
        in 1..6 -> 31
        in 7..11 -> 30
        12 -> if (isJalaliLeap(jy)) 30 else 29
        else -> 30
    }

    /** روز هفته: 0=شنبه، 1=یکشنبه، ... 6=جمعه */
    fun dayOfWeek(jy: Int, jm: Int, jd: Int): Int {
        val (gy, gm, gd) = jalaliToGregorian(jy, jm, jd)
        val cal = GregorianCalendar(gy, gm - 1, gd)
        // Java: 1=Sunday ... 7=Saturday
        val dow = cal.get(Calendar.DAY_OF_WEEK)
        // Convert to: 0=Saturday ... 6=Friday
        return (dow + 1) % 7
    }

    fun today(): JDate {
        val cal = GregorianCalendar()
        return gregorianToJalali(
            cal.get(Calendar.YEAR),
            cal.get(Calendar.MONTH) + 1,
            cal.get(Calendar.DAY_OF_MONTH)
        )
    }

    /** تولید تمام روزهای یک ماه شمسی به همراه اطلاعات روز هفته و فاصله از ابتدای شبکه */
    fun monthGrid(jy: Int, jm: Int): List<GridCell> {
        val days = daysInJalaliMonth(jy, jm)
        val firstDow = dayOfWeek(jy, jm, 1) // 0=Sat
        val cells = mutableListOf<GridCell>()
        // سلول‌های خالی ابتدای ماه
        repeat(firstDow) { cells.add(GridCell.Empty) }
        for (d in 1..days) {
            cells.add(GridCell.Day(d, dayOfWeek(jy, jm, d)))
        }
        // تکمیل تا 42 سلول برای 6 ردیف
        while (cells.size % 7 != 0) cells.add(GridCell.Empty)
        return cells
    }

    sealed class GridCell {
        object Empty : GridCell()
        data class Day(val day: Int, val dayOfWeek: Int) : GridCell()
    }

    private val persianDigits = charArrayOf('۰', '۱', '۲', '۳', '۴', '۵', '۶', '۷', '۸', '۹')

    fun toPersianDigits(s: String): String =
        s.map { if (it in '0'..'9') persianDigits[it - '0'] else it }.joinToString("")

    fun toPersianDigits(n: Int): String = toPersianDigits(n.toString())

    val monthNames = arrayOf(
        "فروردین", "اردیبهشت", "خرداد", "تیر", "مرداد", "شهریور",
        "مهر", "آبان", "آذر", "دی", "بهمن", "اسفند"
    )

    val monthNamesEn = arrayOf(
        "Farvardin", "Ordibehesht", "Khordad", "Tir", "Mordad", "Shahrivar",
        "Mehr", "Aban", "Azar", "Dey", "Bahman", "Esfand"
    )

    /** نام روزهای هفته به فارسی - شنبه اول */
    val weekDayNames = arrayOf(
        "شنبه", "یکشنبه", "دوشنبه", "سه‌شنبه", "چهارشنبه", "پنجشنبه", "جمعه"
    )

    val weekDayShort = arrayOf("ش", "ی", "د", "س", "چ", "پ", "ج")

    /** فصل هر ماه */
    fun season(jm: Int): String = when (jm) {
        in 1..3 -> "بهار"
        in 4..6 -> "تابستان"
        in 7..9 -> "پاییز"
        else -> "زمستان"
    }

    /** محاسبه فاصله روزها از تاریخ مشخص - برای یادآور */
    fun daysBetween(start: JDate, end: JDate): Int {
        val (sgy, sgm, sgd) = jalaliToGregorian(start.year, start.month, start.day)
        val (egy, egm, egd) = jalaliToGregorian(end.year, end.month, end.day)
        val s = GregorianCalendar(sgy, sgm - 1, sgd)
        val e = GregorianCalendar(egy, egm - 1, egd)
        val diff = (e.timeInMillis - s.timeInMillis) / (1000L * 60 * 60 * 24)
        return diff.toInt()
    }
}
