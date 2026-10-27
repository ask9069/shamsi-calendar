package com.shamsicalendar.app.calendar

import java.util.Calendar
import java.util.GregorianCalendar

/**
 * تقویم قمری (هجری اسلامی) - مبتنی بر الگوریتم Umm al-Qura تقریبی
 * این الگوریتم برای نمایش تقریبی تاریخ قمری مناسب است (دقت ±۱ روز)
 */
object IslamicCalendar {

    data class IDate(
        val year: Int,
        val month: Int,  // 1..12
        val day: Int     // 1..30
    )

    /**
     * تبدیل میلادی به قمری - الگوریتم Kuwaiti
     */
    fun gregorianToIslamic(gy: Int, gm: Int, gd: Int): IDate {
        val jd = gregorianToJD(gy, gm, gd)
        return jdToIslamic(jd)
    }

    fun islamicToGregorian(iy: Int, im: Int, id: Int): Triple<Int, Int, Int> {
        val jd = islamicToJD(iy, im, id)
        return jdToGregorian(jd)
    }

    private fun gregorianToJD(year: Int, month: Int, day: Int): Double {
        val a = (year - 1) / 4
        val b = (year - 1) / 100
        val c = (year - 1) / 400
        val d = (year - 1) * 365 + a - b + c
        val e = intArrayOf(0, 31, 59, 90, 120, 151, 181, 212, 243, 273, 304, 334)
        var dayOfYear = e[month - 1] + day
        if (month > 2 && (year % 4 == 0 && year % 100 != 0 || year % 400 == 0)) {
            dayOfYear++
        }
        return d + dayOfYear + 1721425.5
    }

    private fun jdToGregorian(jd: Double): Triple<Int, Int, Int> {
        val z = (jd + 0.5).toInt()
        val a = (z - 1867216.25) / 36524.25
        val b = z + 1 + a.toInt() - (a.toInt() / 4)
        val c = b + 1524
        val d = (c - 122.1) / 365.25
        val e = (365.25 * d.toInt()).toInt()
        val f = (c - e) / 30.6001
        val day = c - e - (30.6001 * f.toInt()).toInt()
        val month = if (f.toInt() < 14) f.toInt() - 1 else f.toInt() - 13
        val year = if (month > 2) d.toInt() - 4716 else d.toInt() - 4715
        return Triple(year, month, day)
    }

    private fun jdToIslamic(jd: Double): IDate {
        val l1 = jd.toInt() - 1948440 + 10632
        val n = (l1 - 1) / 10631
        val l2 = l1 - 10631 * n + 354
        val j = if ((10985 - l2) / 5316 < (50 - l2) / 15819) {
            (l2 - 1) / 354 + (l2 - 1) % 354 / 30
        } else {
            (l2 - 1) / 355 + (l2 - 1) % 355 / 30
        }
        val m2 = (11 * j + 3) / 30
        val day = l2 - 30 * j - 29 * (m2 / 11) + 1
        val month = if (m2 < 11) m2 + 4 else m2 - 10 + 1
        val year = 30 * n + j - 30 + 1394 + (if (m2 > 10) 1 else 0)
        return IDate(year, month, day)
    }

    private fun islamicToJD(iy: Int, im: Int, id: Int): Double {
        val n = (iy - 1394) / 30
        val j = iy - 30 * n - 1394
        val d1 = (11 * j + 3) / 30 + id
        val m1 = if (im > 4) im - 4 else im + 8
        val adj = if (im > 4) 1 else 0
        val l1 = 30 * n + j - 30 + adj
        val d2 = (10985 - l1 * 30) / 5316
        return (l1 * 30 + d1 - 1).toDouble() + 1948440.0 - 10632.0 + 1.0
    }

    fun today(): IDate {
        val cal = GregorianCalendar()
        return gregorianToIslamic(
            cal.get(Calendar.YEAR),
            cal.get(Calendar.MONTH) + 1,
            cal.get(Calendar.DAY_OF_MONTH)
        )
    }

    val monthNames = arrayOf(
        "محرم", "صفر", "ربیع‌الاول", "ربیع‌الثانی", "جمادی‌الاول", "جمادی‌الثانی",
        "رجب", "شعبان", "رمضان", "شوال", "ذی‌القعده", "ذی‌الحجه"
    )

    fun toPersianDigits(s: String): String = JalaliCalendar.toPersianDigits(s)
}
