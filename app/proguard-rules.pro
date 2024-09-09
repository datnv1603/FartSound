# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile

# Keep classes related to Adjust SDK
-keep class com.adjust.sdk.** { *; }

# Keep classes related to Google Play Services and Install Referrer
-keep class com.google.android.gms.common.ConnectionResult {
  int SUCCESS;
}
-keep class com.google.android.gms.ads.identifier.AdvertisingIdClient {
  com.google.android.gms.ads.identifier.AdvertisingIdClient$Info getAdvertisingIdInfo(android.content.Context);
}
-keep class com.google.android.gms.ads.identifier.AdvertisingIdClient$Info {
  java.lang.String getId();
  boolean isLimitAdTrackingEnabled();
}
-keep public class com.android.installreferrer.** { *; }

# Suppress warnings related to Facebook annotations
-dontwarn com.facebook.infer.annotation.Nullsafe$Mode
-dontwarn com.facebook.infer.annotation.Nullsafe

# Keep attributes for generic signatures
-keepattributes Signature

# Keep classes in your package
-keep class com.joya.** { *; }

# Optional: Keep Gson's reflective fields and methods
-keep class com.google.gson.reflect.TypeToken { *; }

# Optional: Keep fields and methods that Gson might reflectively access
-keep class * {
    @com.google.gson.annotations.SerializedName <fields>;
}

# Optional: Keep fields and methods that Gson might reflectively access based on ProGuard/R8 optimizations
-keep class com.google.gson.** { *; }
-keepclassmembers class * {
    @com.google.gson.annotations.SerializedName *;
}

# Suppress warnings related to TypeToken usage
-dontwarn com.google.gson.reflect.TypeToken
