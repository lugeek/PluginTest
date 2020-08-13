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

#-ignorewarnings
-dontshrink
-dontpreverify
-dontusemixedcaseclassnames
-keepattributes *Annotation*
-keepattributes Signature
-keepattributes EnclosingMethod
-keepattributes SourceFile,LineNumberTable
-allowaccessmodification
-printmapping mapping.txt
-printusage unused.txt
-printseeds seeds.txt

-optimizationpasses 5
-verbose
-dontskipnonpubliclibraryclasses
-dontskipnonpubliclibraryclassmembers
#-dontoptimize
-optimizations code/removal/simple,code/removal/advanced,method/removal/parameter,method/inlining/short,method/inlining/tailrecursion,method/inlining/unique,class/marking/final,class/unboxing/enum,code/merging,code/simplification/variable,code/simplification/field,code/simplification/branch,code/removal/variable
-optimizations code/simplification/string,code/simplification/advanced,code/removal/exception,code/removal/variable
-optimizations method/marking/static,method/marking/private,method/marking/final
-optimizations field/removal/writeonly,field/marking/private,code/simplification/arithmetic,code/simplification/cast
#-optimizations field/propagation/value,method/propagation/parameter,method/propagation/returnvalue

#noinspection ShrinkerUnresolvedReference
#系统
-keep public class com.google.vending.licensing.ILicensingService
-keep public class com.android.vending.licensing.ILicensingService

# For native methods, see http://proguard.sourceforge.net/manual/examples.html#native
-keepclasseswithmembernames class * {
    native <methods>;
}

# keep setters in Views so that animations can still work.
# see http://proguard.sourceforge.net/manual/examples.html#beans
-keepclassmembers public class * extends android.view.View {
   void set*(***);
   *** get*();
}

# We want to keep methods in Activity that could be used in the XML attribute onClick
-keepclassmembers class * extends android.app.Activity {
   public void *(android.view.View);
}

# For enumeration classes, see http://proguard.sourceforge.net/manual/examples.html#enumerations
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

-keepclassmembers class * implements android.os.Parcelable {
  public static final android.os.Parcelable$Creator CREATOR;
}

-keepclassmembers class **.R$* {
    public static <fields>;
}

# The support library contains references to newer platform versions.
# Don't warn about those in case this app is linking against an older
# platform version.  We know about them, and they are safe.
-dontwarn android.support.**

# Understand the @Keep support annotation.
-keep class android.support.annotation.Keep

-keep @android.support.annotation.Keep class * {*;}

-keepclasseswithmembers class * {
    @android.support.annotation.Keep <methods>;
}

-keepclasseswithmembers class * {
    @android.support.annotation.Keep <fields>;
}

-keepclasseswithmembers class * {
    @android.support.annotation.Keep <init>(...);
}

-dontwarn com.lugeek.plugin_base.**
-dontwarn com.lugeek.pluginapptest.plugin.PluginFragment
-dontwarn com.lugeek.pluginapptest.plugin.PluginViewHolder
#-keep class * extends android.support.v4.app.Fragment {*;}
#-keepclasseswithmembers class * extends android.support.v4.app.Fragment {*;}
#-keepclasseswithmembers class * implements com.lugeek.plugin_base.PluginViewHolderInterface {}
-keepclasseswithmembers class com.lugeek.pluginapptest.plugin.ToastTest {*;}
-keep class com.lugeek.pluginapptest.plugin.** {*;}

#-dontwarn com.lugeek.**
#-keep class com.lugeek.** {*;}