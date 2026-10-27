package com.shamsicalendar.app.calendar

import java.time.LocalDate
import java.time.ZoneId
import java.time.chrono.HijrahDate
import java.time.temporal.ChronoField

/**
 * تقویم قمری (هجری اسلامی) - استفاده از java.time.chrono.HijrahDate
 * که بر اساس تقویم Umm al-Qura است و دقیق‌ترین روش موجود در جاوا است.
 *
 * این روش از API 26+ پشتیبانی می‌شود.
 */
object IslamicCalendar {

    data class IDate(
        val year: Int,
        val month: Int,  // 1..12
        val day: Int     // 1..30
    )

    /** تبدیل میلادی به قمری - استفاده از HijrahDate */
    fun gregorianToIslamic(gy: Int, gm: Int, gd: Int): IDate {
        val gDate = LocalDate.of(gy, gm, gd)
        val hDate = HijrahDate.from(gDate)
        return IDate(
            hDate.get(ChronoField.YEAR),
            hDate.get(ChronoField.MONTH_OF_YEAR),
            hDate.get(ChronoField.DAY_OF_MONTH)
        )
    }

    /** تبدیل قمری به میلادی */
    fun islamicToGregorian(iy: Int, im: Int, id: Int): Triple<Int, Int, Int> {
        // HijrahDate.from(LocalDate) برعکس کار می‌کند - برای معکوس:
        // ابتدا یک HijrahDate بساز و سپس به LocalDate تبدیل کن
        val hDate = HijrahDate.of(iy, im, id)
        val gDate = LocalDate.from(hDate)
        return Triple(gDate.year, gDate.monthValue, gDate.dayOfMonth)
    }

    fun today(): IDate {
        val zone = ZoneId.of("Asia/Tehran")
        val today = LocalDate.now(zone)
        return gregorianToIslamic(today.year, today.monthValue, today.dayOfMonth)
    }

    val monthNames = arrayOf(
        "محرم", "صفر", "ربیع‌الاول", "ربیع‌الثانی", "جمادی‌الاول", "جمادی‌الثانی",
        "رجب", "شعبان", "رمضان", "شوال", "ذی‌القعده", "ذی‌الحجه"
    )

    fun toPersianDigits(s: String): String = JalaliCalendar.toPersianDigits(s)
}
