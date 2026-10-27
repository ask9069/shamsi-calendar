# ProGuard rules for Shamsi Calendar

# Keep widget providers and receivers
-keep class com.shamsicalendar.app.widget.** { *; }
-keep class com.shamsicalendar.app.notification.** { *; }

# Keep calendar logic (used by reflection in some cases)
-keep class com.shamsicalendar.app.calendar.** { *; }

# Room - حفظ کلاس‌های Entity و Dao
-keep class com.shamsicalendar.app.data.** { *; }

# Keep Application class
-keep class com.shamsicalendar.app.ShamsiApp { *; }

# Keep MainActivity
-keep class com.shamsicalendar.app.MainActivity { *; }

# Kotlin metadata
-keepattributes *Annotation*
-keep class kotlin.Metadata { *; }

# Compose - قوانین استاندارد
-dontwarn androidx.compose.**

# Don't warn about missing classes
-dontwarn org.jetbrains.annotations.**

# Remove logging in release
-assumenosideeffects class android.util.Log {
    public static *** d(...);
    public static *** v(...);
    public static *** i(...);
}

# Optimization passes
-optimizationpasses 5
-allowaccessmodification

# kotlinx.coroutines
-dontwarn kotlinx.coroutines.**
-keep class kotlinx.coroutines.** { *; }

# Room
-keep class * extends androidx.room.RoomDatabase
-keep @androidx.room.Entity class *
-dontwarn androidx.room.paging.**

# Compose runtime
-keep class androidx.compose.runtime.** { *; }
-keep class androidx.compose.ui.** { *; }
-keep class androidx.compose.material3.** { *; }

# Disable some warnings
-dontwarn java.lang.invoke.StringConcatFactory
