#Fabric
-keepattributes SourceFile,LineNumberTable
-keep public class * extends java.lang.Exception
-keep class android.support.v7.widget.SearchView { *; }
# yalantis
-dontwarn com.yalantis.ucrop**
-keep class com.yalantis.ucrop** { *; }
-keep interface com.yalantis.ucrop** { *; }
# retrofit
-dontwarn okio.**
-dontwarn retrofit2.Platform$Java8
# eventbus
-keepattributes *Annotation*
-keepclassmembers class ** {
    @org.greenrobot.eventbus.Subscribe <methods>;
}
-keep enum org.greenrobot.eventbus.ThreadMode { *; }
