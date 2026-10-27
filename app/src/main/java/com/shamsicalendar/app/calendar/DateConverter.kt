package com.shamsicalendar.app.calendar

import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

/**
 * مبدل تاریخ بین سه تقویم شمسی، میلادی و قمری
 */
object DateConverter {

    data class TriCalendar(
        val jalali: JalaliCalendar.JDate,
        val gregorian: Triple<Int, Int, Int>,
        val islamic: IslamicCalendar.IDate
    ) {
        val gregorianDisplay: String
            get() {
                val months = arrayOf("January","February","March","April","May","June",
                    "July","August","September","October","November","December")
                return "${gregorian.third} ${months[gregorian.second - 1]} ${gregorian.first}"
            }
        val islamicDisplay: String
            get() = "${IslamicCalendar.toPersianDigits(islamic.day.toString())} ${IslamicCalendar.monthNames[islamic.month - 1]} ${IslamicCalendar.toPersianDigits(islamic.year.toString())}"
        val jalaliDisplay: String
            get() = "${JalaliCalendar.toPersianDigits(jalali.day.toString())} ${JalaliCalendar.monthNames[jalali.month - 1]} ${JalaliCalendar.toPersianDigits(jalali.year.toString())}"
    }

    fun fromJalali(jy: Int, jm: Int, jd: Int): TriCalendar {
        val g = JalaliCalendar.jalaliToGregorian(jy, jm, jd)
        val i = IslamicCalendar.gregorianToIslamic(g.first, g.second, g.third)
        return TriCalendar(
            JalaliCalendar.JDate(jy, jm, jd),
            g,
            i
        )
    }

    fun fromGregorian(gy: Int, gm: Int, gd: Int): TriCalendar {
        val j = JalaliCalendar.gregorianToJalali(gy, gm, gd)
        val i = IslamicCalendar.gregorianToIslamic(gy, gm, gd)
        return TriCalendar(j, Triple(gy, gm, gd), i)
    }

    fun fromIslamic(iy: Int, im: Int, id: Int): TriCalendar {
        val g = IslamicCalendar.islamicToGregorian(iy, im, id)
        val j = JalaliCalendar.gregorianToJalali(g.first, g.second, g.third)
        return TriCalendar(j, g, IslamicCalendar.IDate(iy, im, id))
    }

    fun today(): TriCalendar {
        val today = LocalDate.now()
        return fromGregorian(today.year, today.monthValue, today.dayOfMonth)
    }
}
