# Room Proguard rules
-keep class * extends androidx.room.RoomDatabase
-dontwarn androidx.room.paging.**

# BouncyCastle
-keep class org.bouncycastle.** { *; }
-dontwarn org.bouncycastle.**

# Gson
-keepattributes Signature
-keepattributes *Annotation*
-keep class org.sahayak.mesh.core.model.** { *; }
-keep class org.sahayak.mesh.core.protocol.** { *; }
