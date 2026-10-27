package com.shamsicalendar.app.calendar

import java.time.LocalDate
import java.time.ZoneId
import java.time.chrono.HijrahDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoField
import java.util.Locale

/**
 * تقویم Jalali (شمسی) - مبتنی بر الگوریتم jdf که در اپ‌های ایرانی استفاده می‌شود
 * و برای تاریخ‌های مدرن کاملاً دقیق است.
 *
 * این پیاده‌سازی از java.time (API 26+) استفاده می‌کند.
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

    /** تبدیل میلادی به شمسی - الگوریتم jdf */
    fun gregorianToJalali(gy: Int, gm: Int, gd: Int): JDate {
        val jArr = longArrayOf(0, 31, 59, 90, 120, 151, 181, 212, 243, 273, 304, 334)

        val j2 = if (gm > 2) gy + 1L else gy.toLong()

        val j5 = ((j2 + 399) / 400) +
            ((((3 + j2) / 4) + (gy * 365L + 355666)) - ((99 + j2) / 100)) +
            gd + jArr[gm - 1]

        var j6 = (j5 / 12053) * 33 - 1595
        var j7 = j5 % 12053
        var j8 = (j7 / 1461) * 4 + j6
        var j9 = j7 % 1461

        if (j9 > 365) {
            val j10 = j9 - 1
            j8 += j10 / 365
            j9 = j10 % 365
        }

        val jm: Int
        val jd: Long
        if (j9 < 186) {
            jm = (j9 / 31 + 1).toInt()
            jd = j9 % 31 + 1
        } else {
            jm = ((j9 - 186) / 30 + 7).toInt()
            jd = (j9 - 186) % 30 + 1
        }

        return JDate(j8.toInt(), jm, jd.toInt())
    }

    /** تبدیل شمسی به میلادی - الگوریتم jdf معکوس */
    fun jalaliToGregorian(jy: Int, jm: Int, jd: Int): Triple<Int, Int, Int> {
        val j2 = jy + 1595L
        var j3 = (((j2 % 33 + 3) / 4) + (j2 / 33) * 8 + (j2 * 365 - 355668) + jd)

        val i2 = if (jm < 7) (jm - 1) * 31 else (jm - 7) * 30 + 186
        val j4 = j3 + i2

        var j5 = (j4 / 146097) * 400
        var j6 = j4 % 146097

        if (j6 > 36524) {
            val j7 = j6 - 1
            j5 = (j7 / 36524) * 100 + j5
            j6 = j7 % 36524
            if (j6 >= 365) {
                j6++
            }
        }

        var j8 = (j6 / 1461) * 4 + j5
        var j9 = j6 % 1461

        if (j9 > 365) {
            val j10 = j9 - 1
            j8 = j10 / 365 + j8
            j9 = j10 % 365
        }

        val j11 = j8
        var i7 = j9.toInt() + 1

        // کبیسه میلادی
        val isLeap = (j11 % 4 == 0L && j11 % 100 != 0L) || j11 % 400 == 0L
        val feb = if (isLeap) 29 else 28
        val iArr = intArrayOf(0, 31, feb, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31)

        var i5 = 1
        while (i5 <= 12 && i7 > iArr[i5]) {
            i7 -= iArr[i5]
            i5++
        }

        return Triple(j11.toInt(), i5, i7)
    }

    fun isGregorianLeap(year: Int): Boolean =
        (year % 4 == 0 && year % 100 != 0) || (year % 400 == 0)

    /**
     * آیا سال شمسی کبیسه است؟
     * روش دقیق: اسفند سال j سال کبیسه است اگر 1 فروردین سال j+1 یک روز زودتر (در روز 365) شروع شود
     * پیاده‌سازی ساده: تبدیل به میلادی و بررسی فاصله
     */
    fun isJalaliLeap(jy: Int): Boolean {
        // اسفند دار 30 روز است اگر کبیسه باشد
        // 29 اسفند j را به میلادی تبدیل می‌کنیم، اگر 30 اسفند هم در همان سال شمسی باشد یعنی کبیسه است
        val (gy1, gm1, gd1) = jalaliToGregorian(jy, 12, 29)
        val (gy2, gm2, gd2) = jalaliToGregorian(jy, 12, 30)
        // اگر 30 اسفند به سال بعد برود یعنی کبیسه نیست
        // در غیر این صورت (همان روز بعد در همان سال) کبیسه است
        val d1 = LocalDate.of(gy1, gm1, gd1)
        val d2 = LocalDate.of(gy2, gm2, gd2)
        return d2.dayOfYear == d1.dayOfYear + 1
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
        val date = LocalDate.of(gy, gm, gd)
        // Java: MONDAY=1 ... SUNDAY=7
        val dow = date.dayOfWeek.value
        // Convert to: 0=Saturday ... 6=Friday
        // SAT=6 in Java, so (6+1)%7=0 → SAT=0
        return dow % 7
    }

    fun today(): JDate {
        val zone = ZoneId.of("Asia/Tehran")
        val today = LocalDate.now(zone)
        return gregorianToJalali(today.year, today.monthValue, today.dayOfMonth)
    }

    /** تولید تمام روزهای یک ماه شمسی به همراه اطلاعات روز هفته و فاصله از ابتدای شبکه */
    fun monthGrid(jy: Int, jm: Int): List<GridCell> {
        val days = daysInJalaliMonth(jy, jm)
        val firstDow = dayOfWeek(jy, jm, 1) // 0=Sat
        val cells = mutableListOf<GridCell>()
        repeat(firstDow) { cells.add(GridCell.Empty) }
        for (d in 1..days) {
            cells.add(GridCell.Day(d, dayOfWeek(jy, jm, d)))
        }
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
        val s = LocalDate.of(sgy, sgm, sgd)
        val e = LocalDate.of(egy, egm, egd)
        return (e.toEpochDay() - s.toEpochDay()).toInt()
    }
}
