# تقویم شمسی اندروید 🗓️

یک اپلیکیشن تقویم شمسی کامل برای اندروید با ویژگی‌های:

- ✅ نمایش تقویم شمسی با تم تاریک پریمیوم و لهجه‌های طلایی
- ✅ نمایش همزمان تاریخ میلادی و قمری
- ✅ تعطیلات رسمی و مناسبت‌های ایران
- ✅ افزودن رویداد شخصی با یادآور (نوتیفیکیشن)
- ✅ مبدل تاریخ بین سه تقویم (شمسی/میلادی/قمری)
- ✅ **دو نوع ویجت: ماه کامل 4×4 و مینی امروز 2×2**
- ✅ رابط کاربری کاملاً RTL و فارسی
- ✅ فونت Vazirmatn
- ✅ حداقل اندروید 8+ (API 26)

---

## نیازمندی‌ها

| ابزار | نسخه |
|------|-----|
| Android Studio | Hedgehog 2023.1.1 یا بالاتر |
| JDK | 17 |
| Android SDK | Compile SDK 34 |
| Kotlin | 1.9.22 |
| Gradle | 8.5 |

---

## راه‌اندازی پروژه

### ۱. دانلود فونت Vazirmatn (الزامی)

فونت‌ها را طبق راهنمای `app/src/main/res/font/README.md` دانلود کنید:

```bash
cd app/src/main/res/font
curl -L -o vazirmatn_regular.ttf "https://github.com/rastikerdar/vazirmatn/raw/master/fonts/ttf/Vazirmatn-Regular.ttf"
curl -L -o vazirmatn_medium.ttf "https://github.com/rastikerdar/vazirmatn/raw/master/fonts/ttf/Vazirmatn-Medium.ttf"
curl -L -o vazirmatn_bold.ttf "https://github.com/rastikerdar/vazirmatn/raw/master/fonts/ttf/Vazirmatn-Bold.ttf"
```

### ۲. باز کردن پروژه در Android Studio

1. Android Studio را باز کنید
2. **File → Open** را بزنید
3. پوشه `ShamsiCalendar` را انتخاب کنید
4. صبر کنید تا Gradle Sync انجام شود (این مرحله ممکن است چند دقیقه طول بکشد)

### ۳. اجرای اپ

- یک دستگاه اندروید واقعی (USB Debugging فعال) یا شبیه‌ساز بسازید
- دکمه **Run ▶** را در Android Studio بزنید
- اپ با نام «تقویم شمسی» روی دستگاه نصب می‌شود

---

## ساخت APK

### Debug APK
```bash
./gradlew assembleDebug
# خروجی در: app/build/outputs/apk/debug/app-debug.apk
```

### Release APK
```bash
./gradlew assembleRelease
# خروجی در: app/build/outputs/apk/release/app-release-unsigned.apk
```

برای Sign کردن Release APK به یک keystore نیاز دارید. راهنما در [مستندات رسمی اندروید](https://developer.android.com/build/building-cmdline#sign_cmdline).

---

## ساختار پروژه

```
ShamsiCalendar/
├── app/
│   ├── build.gradle.kts
│   └── src/main/
│       ├── AndroidManifest.xml
│       ├── java/com/shamsicalendar/app/
│       │   ├── MainActivity.kt              # اکتیویتی اصلی + NavHost
│       │   ├── ShamsiApp.kt                 # کلاس Application
│       │   ├── calendar/                    # منطق تقویم
│       │   │   ├── JalaliCalendar.kt        # تقویم شمسی + الگوریتم Borkowski
│       │   │   ├── IslamicCalendar.kt       # تقویم قمری
│       │   │   └── DateConverter.kt         # مبدل سه‌گانه
│       │   ├── data/                        # لایه داده
│       │   │   ├── Event.kt                 # مدل رویداد
│       │   │   ├── EventDao.kt              # DAO Room
│       │   │   ├── EventDatabase.kt         # دیتابیس Room
│       │   │   ├── EventRepository.kt       # Repository
│       │   │   └── Holidays.kt              # تعطیلات و مناسبت‌ها
│       │   ├── ui/                          # رابط کاربری Compose
│       │   │   ├── theme/                   # تم، رنگ‌ها، تایپوگرافی
│       │   │   ├── components/MonthGrid.kt  # شبکه ماه
│       │   │   └── screens/                 # صفحات تقویم، رویداد، مبدل
│       │   ├── viewmodel/                   # ViewModel
│       │   ├── widget/                      # ویجت‌ها
│       │   │   ├── MonthWidgetProvider.kt   # ویجت ماه 4×4
│       │   │   ├── MiniWidgetProvider.kt    # ویجت مینی 2×2
│       │   │   └── WidgetUpdateReceiver.kt  # به‌روزرسانی خودکار
│       │   └── notification/                # یادآور رویداد
│       │       └── ReminderScheduler.kt
│       └── res/
│           ├── drawable/                    # آیکون‌ها و پس‌زمینه‌ها
│           ├── layout/                      # چیدمان ویجت‌ها
│           │   ├── widget_month.xml         # 42 سلولی ماه
│           │   └── widget_mini.xml          # نمایش امروز
│           ├── values/                      # رنگ‌ها، استرینگ‌ها، تم
│           ├── values-fa/                   # استرینگ‌های فارسی
│           ├── xml/                         # تنظیمات ویجت
│           └── font/                        # فونت Vazirmatn (نیاز به دانلود)
├── build.gradle.kts
├── settings.gradle.kts
└── README.md
```

---

## ویژگی‌های فنی

### الگوریتم تقویم شمسی
از الگوریتم دقیق **Borkowski** برای تبدیل میلادی ↔ شمسی استفاده شده که از سال ۱۲۹۰ تا ۱۴۰۰+ دقیق است. تشخیص کبیسه‌گیری بر اساس چرخه ۳۳ساله.

### دیتابیس
از **Room** برای ذخیره رویدادهای کاربر استفاده شده. جداول با KSP پردازش می‌شوند.

### یادآور رویداد
از **AlarmManager** با `setExactAndAllowWhileIdle` برای یادآور دقیق استفاده شده. کانال نوتیفیکیشن با اولویت HIGH.

### ویجت‌ها
- **MonthWidgetProvider**: چیدمان `widget_month.xml` با 42 سلول (۶ ردیف × ۷ ستون). امروز با پس‌زمینه طلایی، تعطیلات با قرمز.
- **MiniWidgetProvider**: چیدمان `widget_mini.xml` با نمایش بزرگ روز و تاریخ‌های سه‌گانه.
- **WidgetUpdateReceiver**: به‌روزرسانی خودکار پس از Boot، تغییر زمان، یا تغییر منطقه‌ زمانی.

### تم
- پالت **Dark Premium** با رنگ‌های عمیق `#0A0E1A` و لهجه طلایی `#D4AF37`
- فونت **Vazirmatn** در سه وزن Regular/Medium/Bold
- پشتیبانی کامل RTL

---

## افزودن ویجت به صفحه خانه

1. روی صفحه خانه اندروید انگشت نگه دارید
2. گزینه **ویجت‌ها** (Widgets) را انتخاب کنید
3. اپ **تقویم شمسی** را پیدا کنید
4. یکی از دو ویجت زیر را انتخاب و روی صفحه بکشید:
   - **ماه کامل** (4×4): نمایش کامل ماه با هایلایت امروز
   - **مینی** (2×2): نمایش سریع تاریخ امروز شمسی/میلادی/قمری

---

## سفارشی‌سازی

### افزودن مناسبت جدید
فایل `app/src/main/java/com/shamsicalendar/app/data/Holidays.kt` را ویرایش کنید و به لیست `occasions` اضافه کنید:

```kotlin
Occasion(7, 1, "عنوان مناسبت", isHoliday = false)
```

### تغییر رنگ تم
فایل `app/src/main/java/com/shamsicalendar/app/ui/theme/Color.kt` را ویرایش کنید.

### تنظیم آیکون لانچر
فایل‌های `app/src/main/res/drawable/ic_launcher_foreground.xml` و `ic_launcher_background.xml` را ویرایش کنید.

---

## نکات و محدودیت‌ها

1. **مناسبت‌های قمری**: تاریخ‌های قمری متغیر (عید فطر، قربان، غدیر و ...) به صورت تقریبی محاسبه می‌شوند. برای دقت کامل باید هر سال با تقویم رسمی مطابقت داده شوند.

2. **دقت الگوریتم قمری**: الگوریتم Kuwaiti با دقت ±۱ روز کار می‌کند. برای کاربرد حرفه‌ای، استفاده از کتابخانه‌هایی مثل `Time4A` یا API تقویم رسمی توصیه می‌شود.

3. **مجوز POST_NOTIFICATIONS**: روی Android 13+ کاربر باید دسترسی نوتیفیکیشن بدهد.

4. **مجوز SCHEDULE_EXACT_ALARM**: روی Android 12+ برای یادآور دقیق لازم است.

---

## لایسنس

این پروژه برای استفاده آزاد است. فونت Vazirmatn تحت لایسنس SIL Open Font License 1.1 منتشر شده است.

---

## راه‌های توسعه آینده

- [ ] نمایش اوقات شرعی (نیازمند API یا محاسبات نجومی)
- [ ] همگام‌سازی با تقویم گوگل
- [ ] پشتیبانی از چند زبان
- [ ] تم روشن به‌علاوه تم تاریک
- [ ] ویجت قابل اسکرول بین ماه‌ها
- [ ] نوتیفیکیشن روزانه برای مناسبت روز
- [ ] جستجوی رویدادها

ساخته شده با ❤️ برای کاربران فارسی‌زبان
