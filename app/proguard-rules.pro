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

-dontobfuscate
-keep class com.amazonaws.** { *; }
-keepnames class com.amazonaws.** { *; }
-dontwarn com.amazonaws.**
-dontwarn okio.**
-dontwarn dagger.**

-dontwarn retrofit2.Platform$Java8
-keep class retrofit2.** { *; }
-keepclasseswithmembers class * {
    @retrofit2.http.* <methods>;
}
# all classes in a package
-keep class com.localserviceprovider.customer.json.** { *; }
-keep class sun.misc.Unsafe { *; }
-keep class com.google.gson.examples.android.model.** { *; }
-keepattributes Signature
-keepattributes Exceptions

 -keep class com.utility.WaveDrawable {*;}
 # io card
-keep class io.card.payment.** {*;}

-dontwarn com.bumptech.glide.**
-keep public class * implements com.bumptech.glide.module.GlideModule
-keep public class * extends com.bumptech.glide.module.AppGlideModule
-keep public enum com.bumptech.glide.load.resource.bitmap.ImageHeaderParser$** {
  **[] $VALUES;
  public *;
}
-keep class org.webrtc.** { *; }


-keepattributes InnerClasses
-keepattributes EnclosingMethod
-keepattributes EnclosingMethod
-keepattributes Exceptions, Signature, InnerClasses


-keep class android.support.v7.widget.SearchView { *; }
-keep class org.eclipse.paho.android.service.MqttAndroidClient { *; }
-keep class org.eclipse.paho.client.mqttv3.IMqttActionListener { *; }
-keep class org.eclipse.paho.client.mqttv3.IMqttDeliveryToken { *; }
-keep class org.eclipse.paho.client.mqttv3.IMqttToken { *; }
-keep class org.eclipse.paho.client.mqttv3.MqttCallback { *; }
-keep class org.eclipse.paho.client.mqttv3.MqttConnectOptions { *; }
-keep class org.eclipse.paho.client.mqttv3.MqttException { *; }
-keep class org.eclipse.paho.client.mqttv3.MqttMessage { *; }
-keep class com.utility.ShimmerLayout { *; }

-dontwarn android.databinding.**
-keep class android.databinding.** { *; }
