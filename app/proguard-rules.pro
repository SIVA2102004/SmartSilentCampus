# ProGuard rules for SmartSilent Campus
-keepattributes *Annotation*
-dontwarn javax.annotation.**
-keepclassmembers class * {
    @androidx.room.TypeConverter *;
}
