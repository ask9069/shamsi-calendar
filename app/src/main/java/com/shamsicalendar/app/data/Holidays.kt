package com.shamsicalendar.app.data

/**
 * تعطیلات و مناسبت‌های رسمی ایران - بر اساس ماه و روز شمسی
 * isHoliday = true یعنی روز تعطیل است
 */
data class Occasion(
    val month: Int,    // 1..12 شمسی
    val day: Int,      // 1..31
    val title: String,
    val isHoliday: Boolean = false,
    val isIslamic: Boolean = false  // مناسبت قمری
)

object Holidays {

    val occasions: List<Occasion> = listOf(
        // فروردین
        Occasion(1, 1, "نوروز - جشن سال نو", isHoliday = true),
        Occasion(1, 2, "عید نوروز - روز دوم", isHoliday = true),
        Occasion(1, 3, "عید نوروز - روز سوم", isHoliday = true),
        Occasion(1, 4, "عید نوروز - روز چهارم", isHoliday = true),
        Occasion(1, 12, "روز جمهوری اسلامی", isHoliday = true),
        Occasion(1, 13, "روز طبیعت (سیزده‌بدر)", isHoliday = true),

        // اردیبهشت
        Occasion(2, 1, "روز بزرگداشت سعدی"),
        Occasion(2, 2, "روز زمین پاک"),
        Occasion(2, 3, "روز معلم"),
        Occasion(2, 10, "روز ملی خلیج فارس"),
        Occasion(2, 25, "روز بزرگداشت فردوسی"),

        // خرداد
        Occasion(3, 14, "رحلت امام خمینی", isHoliday = true),
        Occasion(3, 15, "قیام ۱۵ خرداد", isHoliday = true),
        Occasion(3, 4, "روز دزفول"),
        Occasion(3, 1, "روز بزرگداشت ملاصدرا"),

        // تیر
        Occasion(4, 1, "روز اصناف"),
        Occasion(4, 7, "روز قلم"),
        Occasion(4, 8, "روز مبارزه با سلاح‌های شیمیایی و میکروبی"),
        Occasion(4, 10, "روز صنعت و معدن"),
        Occasion(4, 14, "روز قلم"),
        Occasion(4, 17, "روز مطبوعات"),

        // مرداد
        Occasion(5, 5, "روز بزرگداشت شیخ بهایی"),
        Occasion(5, 14, "روز اهدای خون"),
        Occasion(5, 17, "روز خبرنگار"),
        Occasion(5, 28, "روز بزرگداشت ابوریحان بیرونی"),

        // شهریور
        Occasion(6, 1, "روز پزشک"),
        Occasion(6, 17, "قیام ۱۷ شهریور"),
        Occasion(6, 31, "آغاز هفته دفاع مقدس"),

        // مهر
        Occasion(7, 1, "روز بزرگداشت امام خمینی و آغاز هفته دفاع مقدس"),
        Occasion(7, 5, "روز بزرگداشت شهدا"),
        Occasion(7, 8, "روز بزرگداشت فرمانداران"),
        Occasion(7, 13, "روز نیروی انتظامی"),
        Occasion(7, 14, "روز دامپزشکی"),
        Occasion(7, 26, "روز تربیت‌بدنی و ورزش"),

        // آبان
        Occasion(8, 4, "روز کوروش بزرگ"),
        Occasion(8, 13, "روز دانش‌آموز"),
        Occasion(8, 24, "روز کتاب و کتاب‌خوانی"),

        // آذر
        Occasion(9, 7, "روز نیروی دریایی"),
        Occasion(9, 16, "روز دانشجو"),
        Occasion(9, 30, "شب یلدا (چله)"),

        // دی
        Occasion(10, 19, "قیام مردم قم"),

        // بهمن
        Occasion(11, 12, "بازگشت امام خمینی به ایران"),
        Occasion(11, 19, "روز نیروی هوایی"),
        Occasion(11, 22, "پیروزی انقلاب اسلامی"),
        Occasion(11, 29, "قیام مردم تبریز"),

        // اسفند
        Occasion(12, 5, "روز بزرگداشت خواجه نصیر و روز مهندس"),
        Occasion(12, 15, "روز درختکاری"),
        Occasion(12, 25, "روز بزرگداشت پروین اعتصامی"),
        Occasion(12, 29, "روز ملی شدن صنعت نفت"),

        // مناسبت‌های قمری متغیر - این‌ها تقریبی هستند
        Occasion(0, 0, "عید فطر (متغیر - تقویم قمری)", isHoliday = true, isIslamic = true),
        Occasion(0, 0, "عید قربان (متغیر - تقویم قمری)", isHoliday = true, isIslamic = true),
        Occasion(0, 0, "عید غدیر خم (متغیر - تقویم قمری)", isHoliday = true, isIslamic = true),
        Occasion(0, 0, "تاسوعا (متغیر - تقویم قمری)", isHoliday = true, isIslamic = true),
        Occasion(0, 0, "عاشورا (متغیر - تقویم قمری)", isHoliday = true, isIslamic = true),
        Occasion(0, 0, "اربعین (متغیر - تقویم قمری)", isHoliday = true, isIslamic = true),
        Occasion(0, 0, "رحلت پیامبر (متغیر - تقویم قمری)", isHoliday = true, isIslamic = true),
        Occasion(0, 0, "میلاد پیامبر (متغیر - تقویم قمری)", isHoliday = true, isIslamic = true)
    )

    /** مناسبت‌های شمسی یک روز خاص */
    fun getForDay(month: Int, day: Int): List<Occasion> =
        occasions.filter { it.month == month && it.day == day }

    fun isHoliday(month: Int, day: Int): Boolean =
        getForDay(month, day).any { it.isHoliday }

    /** همه مناسبت‌های یک ماه شمسی - برای نمایش در صفحه ماه */
    fun getForMonth(month: Int): List<Occasion> =
        occasions.filter { it.month == month }.sortedBy { it.day }

    /** فهرست مناسبت‌های قمری (تقریبی) - این‌ها باید هر سال با تقویم رسمی مطابقت داده شوند */
    fun getIslamicHolidays(): List<Occasion> = occasions.filter { it.isIslamic }
}
