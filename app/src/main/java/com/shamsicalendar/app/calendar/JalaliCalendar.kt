package com.shamsicalendar.app.calendar

import java.util.Calendar
import java.util.GregorianCalendar

/**
 * تقویم Jalali (شمسی) - مبتنی بر الگوریتم دقیق Kazimierz M. Borkowski
 * که از سال ۱۲۹۰ تا ۱۴۰۰ و فراتر دقیق است.
 *
 * ماه‌ها: ۶ ماه اول ۳۱ روزه، ۵ ماه بعدی ۳۰ روزه، ماه آخر ۲۹ یا ۳۰ روزه (کبیسه)
 */
object JalaliCalendar {

    data class JDate(
        val year: Int,
        val month: Int,   // 1..12
        val day: Int      // 1..31
    ) {
        override fun toString(): String = "${toPersianDigits(year.toString())}/${toPersianDigits(month.toString().padStart(2, '0'))}/${toPersianDigits(day.toString().padStart(2, '0'))}"
    }

    /** تبدیل میلادی به شمسی */
    fun gregorianToJalali(gy: Int, gm: Int, gd: Int): JDate {
        val gDaysInMonth = intArrayOf(31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31)
        val jDaysInMonth = intArrayOf(31, 31, 31, 31, 31, 31, 30, 30, 30, 30, 30, 29)

        var gy2 = gy
        var gm2 = gm - 1
        var gd2 = gd

        var gDayNo = 365L * gy2 +
            (gy2 + 3) / 4 -
            (gy2 + 99) / 100 +
            (gy2 + 399) / 400

        for (i in 0 until gm2) {
            gDayNo += gDaysInMonth[i]
        }

        if (gm2 > 1 && isGregorianLeap(gy2)) {
            gDayNo++
        }
        gDayNo += gd2.toLong() - 1

        var jDayNo = gDayNo - 79

        var jNp = jDayNo / 12053
        jDayNo %= 12053

        var jy = 979 + 33 * jNp.toInt() + 4 * (jDayNo / 1461).toInt()
        jDayNo %= 1461

        if (jDayNo >= 366) {
            jy += ((jDayNo - 1) / 365).toInt()
            jDayNo = (jDayNo - 1) % 365
        }

        var i = 0
        while (i < 11 && jDayNo >= jDaysInMonth[i]) {
            jDayNo -= jDaysInMonth[i]
            i++
        }
        val jm = i + 1
        val jd = jDayNo.toInt() + 1
        return JDate(jy, jm, jd)
    }

    /** تبدیل شمسی به میلادی */
    fun jalaliToGregorian(jy: Int, jm: Int, jd: Int): Triple<Int, Int, Int> {
        val jDaysInMonth = intArrayOf(31, 31, 31, 31, 31, 31, 30, 30, 30, 30, 30, 29)

        var jy2 = jy - 979
        var jm2 = jm - 1
        var jd2 = jd - 1

        var jDayNo = 365L * jy2 +
            (jy2 / 33).toInt() * 8 +
            ((jy2 % 33 + 3) / 4).toInt()

        for (i in 0 until jm2) {
            jDayNo += jDaysInMonth[i]
        }
        jDayNo += jd2.toLong()

        var gDayNo = jDayNo + 79

        var gy = 1600 + 400 * (gDayNo / 146097).toInt()
        gDayNo %= 146097L

        var leap = true
        if (gDayNo >= 36525) {
            gDayNo--
            gy += 100 * (gDayNo / 36524).toInt()
            gDayNo %= 36524L
            if (gDayNo >= 365) gDayNo++ else leap = false
        }

        gy += 4 * (gDayNo / 1461).toInt()
        gDayNo %= 1461L

        if (gDayNo >= 366) {
            leap = false
            gDayNo--
            gy += (gDayNo / 365).toInt()
            gDayNo = gDayNo % 365
        }

        val salA = intArrayOf(31, if (leap) 29 else 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31)
        var gd = gDayNo.toInt() + 1
        var gm = 0
        while (gm < 11 && gd > salA[gm]) {
            gd -= salA[gm]
            gm++
        }
        return Triple(gy, gm + 1, gd)
    }

    fun isGregorianLeap(year: Int): Boolean =
        (year % 4 == 0 && year % 100 != 0) || (year % 400 == 0)

    /** آیا سال شمسی کبیسه است؟ - بر اساس محاسبه دقیق */
    fun isJalaliLeap(jy: Int): Boolean {
        val cycle = ((jy - 1394) % 33 + 33) % 33
        return (cycle == 1 || cycle == 5 || cycle == 9 || cycle == 13 ||
                cycle == 17 || cycle == 22 || cycle == 26 || cycle == 30)
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
