package com.shamsicalendar.app.data

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * رویداد شخصی کاربر با یادآور
 */
@Entity(tableName = "events")
data class Event(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val description: String = "",
    /** تاریخ شمسی به فرمت YYYY-MM-DD */
    val jalaliDate: String,
    /** ساعت و دقیقه 0..23 و 0..59 - اگر null باشد کل‌روز */
    val hour: Int? = null,
    val minute: Int? = null,
    /** چند دقیقه قبل یادآور */
    val reminderMinutes: Int = 0,
    val color: Int = 0xFFFFC107.toInt(),  // طلایی پیش‌فرض
    val createdAt: Long = System.currentTimeMillis()
) {
    fun parseJalali(): Triple<Int, Int, Int> {
        val parts = jalaliDate.split("-").map { it.toInt() }
        return Triple(parts[0], parts[1], parts[2])
    }

    companion object {
        fun formatDate(jy: Int, jm: Int, jd: Int): String =
            "$jy-${jm.toString().padStart(2, '0')}-${jd.toString().padStart(2, '0')}"
    }
}
