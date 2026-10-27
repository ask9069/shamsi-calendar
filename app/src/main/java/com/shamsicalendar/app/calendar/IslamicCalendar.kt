package com.shamsicalendar.app.calendar

import java.util.Calendar
import java.util.GregorianCalendar

/**
 * تقویم قمری (هجری اسلامی) - مبتنی بر الگوریتم Umm al-Qura تقریبی
 * این الگوریتم برای نمایش تقریبی تاریخ قمری مناسب است (دقت ±۱ روز)
 *
 * برای کاربردهای دقیق باید از تقویم رسمی استفاده شود.
 */
object IslamicCalendar {

    data class IDate(
        val year: Int,
        val month: Int,  // 1..12
        val day: Int     // 1..30
    )

    /**
     * تبدیل میلادی به قمری - الگوریتم Umm al-Qura تقریبی
     * مبتنی بر محاسبه Julian Day Number
     */
    fun gregorianToIslamic(gy: Int, gm: Int, gd: Int): IDate {
        // محاسبه Julian Day Number
        val a = (14 - gm) / 12
        val y = gy + 4800 - a
        val m = gm + 12 * a - 3

        val jd = gd + (153 * m + 2) / 5 + 365 * y + y / 4 - y / 100 + y / 400 - 32045

        // تبدیل JD به قمری - الگوریتم Fliegel-Van Flandern
        val l1 = jd - 1948440 + 10632
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

    fun islamicToGregorian(iy: Int, im: Int, id: Int): Triple<Int, Int, Int> {
        // معکوس الگوریتم فوق
        val n = (iy - 1394) / 30
        val j = iy - 30 * n - 1394

        val m2 = if (im > 4) im - 4 else im + 8
        val adj = if (im > 4) 1 else 0

        val l1 = 30 * n + j - 30 + adj
        val d1 = (11 * j + 3) / 30 + id

        val jd = l1 * 30 + d1 - 1 + 1948440 - 10632

        // تبدیل JD به میلادی - الگوریتم Fliegel
        val a = jd + 32044
        val b = (4 * a + 3) / 146097
        val c = a - (146097 * b) / 4

        val d = (4 * c + 3) / 1461
        val e = c - (1461 * d) / 4
        val m = (5 * e + 2) / 153

        val day = e - (153 * m + 2) / 5 + 1
        val month = m + 3 - 12 * (m / 10)
        val year = 100 * b + d - 4800 + m / 10

        return Triple(year, month, day)
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
