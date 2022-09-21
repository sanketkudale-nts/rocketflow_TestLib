# Default PV proguard file - use it and abuse it if its useful.

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile

-dontusemixedcaseclassnames
-dontskipnonpubliclibraryclassmembers
-verbose

# Save the obfuscation mapping to a file, so we can de-obfuscate any stack
# traces later on. Keep a fixed source file attribute and all line number
# tables to get line numbers in the stack traces.
# You can comment this out if you're not interested in stack traces.

#-printmapping out.map
-keepparameternames

# Optimization is turned off by default. Dex does not like code run
# through the ProGuard optimize and preverify steps (and performs some
# of these optimizations on its own).
#-dontoptimize
-dontpreverify

# Note that if you want to enable optimization, you cannot just
# include optimization flags in your own project configuration file;
# instead you will need to point to the
# "proguard-android-optimize.txt" file instead of this one from your
# project.properties file.


##########
# Maintain all attributes - To avoid having to add each in several different places below
##########
-keepattributes Exceptions,InnerClasses,Signature,Deprecated, SourceFile,LineNumberTable,*Annotation*,EnclosingMethod

##########
# okio
##########
# Okio
-keep class sun.misc.Unsafe { *; }
-dontwarn java.nio.file.*
-dontwarn org.codehaus.mojo.animal_sniffer.IgnoreJRERequirement
-dontwarn okio.**

##########
# Dagger2
##########
# Dagger ProGuard rules.
# https://github.com/square/dagger

-dontwarn dagger.internal.codegen.**
-keepclassmembers,allowobfuscation class * {
    @javax.inject.* *;
    @dagger.* *;
    <init>();
}

-keep class dagger.* { *; }
-keep class javax.inject.* { *; }
#-keep class * extends dagger.internal.Binding
#-keep class * extends dagger.internal.ModuleAdapter
#-keep class * extends dagger.internal.StaticInjection
-dontwarn com.google.errorprone.annotations.**
-dontwarn dagger.android.**

##########
# Android
##########
-keep public class * extends android.app.Activity
-keep public class * extends android.app.Application
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider
-keep public class * extends android.app.backup.BackupAgentHelper
-keep public class * extends android.preference.Preference
# Data Binding
-dontwarn android.databinding.**
-keep class android.databinding.** { *; }

-keepclassmembers class * extends android.content.Context {
   public void *(android.view.View);
   public void *(android.view.MenuItem);
}

##########
# View - Gets and setters - keep setters in Views so that animations can still work.
# see http://proguard.sourceforge.net/manual/examples.html#beans
##########

-keepclassmembers public class * extends android.view.View {
   void set*(***);
   *** get*();
}

-keepclasseswithmembers class * {
    public <init>(android.content.Context, android.util.AttributeSet);
}

-keepclasseswithmembers class * {
    public <init>(android.content.Context, android.util.AttributeSet, int);
}

##########
#Enums - For enumeration classes, see http://proguard.sourceforge.net/manual/examples.html#enumerations
##########

-keepclassmembers enum * { *; }

##########
# Parcelables: Mantain the parcelables working
##########
-keep class * implements android.os.Parcelable {
  public static final android.os.Parcelable$Creator *;
}

-keepclassmembers class * implements android.os.Parcelable {
    static ** CREATOR;
}

-keepclassmembers class **.R$* {
    public static <fields>;
}

#############
# Serializables
#############
-keepclassmembers class * implements java.io.Serializable {
    static final long serialVersionUID;
    private static final java.io.ObjectStreamField[] serialPersistentFields;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
    java.lang.Object writeReplace();
    java.lang.Object readResolve();
}
##########
# Kotlin
##########
-dontwarn kotlin.**
-dontnote kotlin.**
-keepclassmembers class **$WhenMappings {
    <fields>;
}

#Ignore null checks at runtime
-assumenosideeffects class kotlin.jvm.internal.Intrinsics {
    static void checkParameterIsNotNull(java.lang.Object, java.lang.String);
}
#############
# BottomBar (Needed to call methods via reflection to customize it)
#############
-keep class android.support.design.internal.** { *; }


#############
# WebViews
#############
# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}
-keep class android.support.v8.renderscript.** { *; }

########################################
# External Libraries
########################################


#############
# Google Play Services
#############
-keep class com.google.android.gms.* {  *; }
-dontwarn com.google.android.gms.**
-keep public class com.google.vending.licensing.ILicensingService
-keep public class com.android.vending.licensing.ILicensingService
-dontnote **ILicensingService
-dontnote com.google.android.gms.gcm.GcmListenerService
-dontnote com.google.android.gms.**

-dontwarn com.google.android.gms.ads.**
#############
# Android Support Lib
#############
-keep class android.support.design.widget.TextInputLayout { *; }
#############
# Firebase
#############
-dontnote com.google.firebase.**
-dontwarn com.google.firebase.crash.**


##########
# Android architecture components: Lifecycle ( https://issuetracker.google.com/issues/62113696 )
##########
# LifecycleObserver's empty constructor is considered to be unused by proguard
-keepclassmembers class * implements android.arch.lifecycle.LifecycleObserver {
    <init>(...);
}
# ViewModel's empty constructor is considered to be unused by proguard
-keepclassmembers class * extends android.arch.lifecycle.ViewModel {
    <init>(...);
}
# keep Lifecycle State and Event enums values
-keepclassmembers class android.arch.lifecycle.Lifecycle$State { *; }
-keepclassmembers class android.arch.lifecycle.Lifecycle$Event { *; }
# keep methods annotated with @OnLifecycleEvent even if they seem to be unused
# (Mostly for LiveData.LifecycleBoundObserver.onStateChange(), but who knows)
-keepclassmembers class * {
    @android.arch.lifecycle.OnLifecycleEvent *;
}
#############
# Retrofit
#############
-dontnote okio.**

#############
# HttpClient Legacy (Ignore) - org.apache.http legacy
#############
-dontnote android.net.http.*
-dontnote org.apache.commons.codec.**
-dontnote org.apache.http.**
##########
# Glide
##########
-keep public class * extends com.bumptech.glide.module.AppGlideModule
-keep public enum com.bumptech.glide.load.ImageHeaderParser$** {
  **[] $VALUES;
  public *;
}

-keep public class * implements com.bumptech.glide.module.GlideModule
-keep public enum com.bumptech.glide.load.resource.bitmap.ImageHeaderParser$** {
    **[] $VALUES;
    public *;
}
-dontnote com.bumptech.glide.**
##########
# RxJava 2
##########

-keep class rx.schedulers.Schedulers {
    public static <methods>;
}
-keep class rx.schedulers.ImmediateScheduler {
    public <methods>;
}
-keep class rx.schedulers.TestScheduler {
    public <methods>;
}
-keep class rx.schedulers.Schedulers {
    public static ** test();
}
-keepclassmembers class rx.internal.util.unsafe.*ArrayQueue*Field* {
    long producerIndex;
    long consumerIndex;
}
-keepclassmembers class rx.internal.util.unsafe.BaseLinkedQueueProducerNodeRef {
    long producerNode;
    long consumerNode;
}
#############
# Stetho
#############
-dontnote com.facebook.stetho.**

##########
# Crashlytics:
# Adding this in to preserve line numbers so that the stack traces can be remapped
##########
-renamesourcefileattribute SourceFile
-keepattributes SourceFile,LineNumberTable


##########
# Airbnb: Deep Linking Dispatch
##########

#-keep class com.airbnb.deeplinkdispatch.** { *; }
#-keepclasseswithmembers class * {
#     @com.airbnb.deeplinkdispatch.DeepLink <methods>;
#}
#
#-keep @interface your.package.deeplinks.** { *; }
#-keepclasseswithmembers class * {
#    @your.package.deeplinks.* <methods>;
#}
#
#-dontnote com.airbnb.deeplinkdispatch.**
#############
# Carousel View
#############
#-dontnote com.synnapps.carouselview.**
##########
# BlurView
##########
#-dontnote eightbitlab.com.blurview.**


##########
# cwac-netsecurity
##########
#-keep class com.commonsware.cwac.netsecurity.** { *; }

###########
# okhttp3
##########
-dontwarn okhttp3.internal.platform.*
-dontwarn okhttp3.**
-dontwarn okio.**
-dontwarn javax.annotation.**
-dontwarn org.conscrypt.**

##########
#UNKNOWN
##########
-dontnote com.android.org.conscrypt.SSLParametersImpl
-dontnote org.apache.harmony.xnet.provider.jsse.SSLParametersImpl
-dontnote sun.security.ssl.SSLContextImpl
-dontwarn org.conscrypt.OpenSSLProvider
-dontwarn org.conscrypt.Conscrypt
-dontnote dalvik.system.CloseGuard
-dontwarn sun.misc.Unsafe
-dontnote sun.misc.Unsafe
-keep public class * extends java.lang.Exception

############
#crashlytics
############
-keep class com.crashlytics.** { *; }
-dontwarn com.crashlytics.**
-dontwarn com.tracki.**
-keep class com.tracki.**{ *; }
-keep class com.trackthat.**{ *; }
-keep class com.google.**{ *; }

############################################
# Android architecture components: Lifecycle
############################################
# LifecycleObserver's empty constructor is considered to be unused by proguard
-keepclassmembers class * implements android.arch.lifecycle.LifecycleObserver {
    <init>(...);
}
# ViewModel's empty constructor is considered to be unused by proguard
-keepclassmembers class * extends android.arch.lifecycle.ViewModel {
    <init>(...);
}
# keep Lifecycle State and Event enums values
-keepclassmembers class android.arch.lifecycle.Lifecycle$State { *; }
-keepclassmembers class android.arch.lifecycle.Lifecycle$Event { *; }
# keep methods annotated with @OnLifecycleEvent even if they seem to be unused
# (Mostly for LiveData.LifecycleBoundObserver.onStateChange(), but who knows)
-keepclassmembers class * {
    @android.arch.lifecycle.OnLifecycleEvent *;
}

-keepclassmembers class * implements android.arch.lifecycle.LifecycleObserver {
    <init>(...);
}

-keep class * implements android.arch.lifecycle.LifecycleObserver {
    <init>(...);
}
-keepclassmembers class android.arch.** { *; }
-keep class android.arch.** { *; }
-keep class * implements android.arch.lifecycle.GeneratedAdapter {<init>(...);}

-keep class io.fabric.sdk.android.services.events.EventsStorageListener
-keep class io.fabric.sdk.android.services.events.EventsStorage
-keep class io.fabric.sdk.android.services.common.CurrentTimeProvider
-keep class io.fabric.sdk.android.services.events.EventTransform
-keep class io.fabric.sdk.android.services.concurrency.Task
-keep class io.fabric.sdk.android.services.network.HttpMethod
-keep class io.fabric.sdk.android.services.network.HttpRequestFactory
-keep class io.fabric.sdk.android.Kit
-keep class io.fabric.sdk.android.services.common.IdManager
-keep class io.fabric.sdk.android.InitializationCallback
-keep class io.fabric.sdk.android.Fabric
-keep class io.fabric.sdk.android.services.common.IdManager$DeviceIdentifierType
-keep class io.fabric.sdk.android.services.persistence.PreferenceStore
-keep class io.fabric.sdk.android.services.persistence.FileStore
-keep class io.fabric.sdk.android.services.settings.PromptSettingsData
-keep class io.fabric.sdk.android.services.network.HttpRequest
-keep class io.fabric.sdk.android.services.settings.SessionSettingsData
-keep class io.fabric.sdk.android.services.settings.SettingsData
-keep class io.fabric.sdk.android.services.settings.BetaSettingsData
-keep class io.fabric.sdk.android.ActivityLifecycleManager
-keep class io.fabric.sdk.android.services.settings.AnalyticsSettingsData
-keep class io.fabric.sdk.android.services.concurrency.internal.RetryState
-keep class io.fabric.sdk.android.services.concurrency.internal.Backoff
-keep class io.fabric.sdk.android.services.common.Crash$LoggedException
-keep class io.fabric.sdk.android.services.common.Crash$FatalException

-keepattributes *Annotation*
-keep public class * extends android.support.design.widget.CoordinatorLayout.Behavior { *; }
-keep public class * extends android.support.design.widget.ViewOffsetBehavior { *; }
-keep class android.support.design.** { *; }

######################################################################################################
## Add project specific ProGuard rules here.
## You can control the set of applied configuration files using the
## proguardFiles setting in build.gradle.
##
## For more details, see
##   http://developer.android.com/guide/developing/tools/proguard.html
#
## If your project uses WebView with JS, uncomment the following
## and specify the fully qualified class name to the JavaScript interface
## class:
##-keepclassmembers class fqcn.of.javascript.interface.for.webview {
##   public *;
##}
#
## Uncomment this to preserve the line number information for
## debugging stack traces.
##-keepattributes SourceFile,LineNumberTable
#
## If you keep the line number information, uncomment this to
## hide the original source file name.
##-renamesourcefileattribute SourceFile
#
#
## Save the obfuscation mapping to a file, so we can de-obfuscate any stack
## traces later on. Keep a fixed source file attribute and all line number
## tables to get line numbers in the stack traces.
## You can comment this out if you're not interested in stack traces.
#
#-printmapping out.map
#-keepparameternames
#-renamesourcefileattribute SourceFile
#-keepattributes Exceptions,InnerClasses,Signature,Deprecated,SourceFile,LineNumberTable,EnclosingMethod
#
## Preserve all annotations.
#
#-keepattributes *Annotation*
#
## Preserve all public classes, and their public and protected fields and
## methods.
#
#-keep public class * {
#    public protected *;
#}
#
## Preserve all .class method names.
#
#-keepclassmembernames class * {
#    java.lang.Class class$(java.lang.String);
#    java.lang.Class class$(java.lang.String, boolean);
#}
#
## Preserve all native method names and the names of their classes.
#
#-keepclasseswithmembernames class * {
#    native <methods>;
#}
#
## Preserve the special static methods that are required in all enumeration
## classes.
#
#-keepclassmembers class * extends java.lang.Enum {
#    public static **[] values();
#    public static ** valueOf(java.lang.String);
#}
#
#-dontwarn java.lang.invoke.**
#
#
#-keep public class com.google.android.gms.* { *; }
#-dontwarn com.google.android.gms.**
#
#-keep class * extends java.util.ListResourceBundle {
#    protected java.lang.Object[][] getContents();
#}
#
#-keep public class com.google.android.gms.common.internal.safeparcel.SafeParcelable {
#    public static final *** NULL;
#}
#
#-keepnames @com.google.android.gms.common.annotation.KeepName class *
#-keepclassmembernames class * {
#    @com.google.android.gms.common.annotation.KeepName *;
#}
#
#-keepnames class * implements android.os.Parcelable {
#    public static final ** CREATOR;
#}
#
#-dontnote android.net.http.*
#-dontnote org.apache.commons.codec.**
#-dontnote org.apache.http.**
-dontnote java.lang.invoke.**

-dontwarn com.google.android.material.**
-keep class com.google.android.material.** { *; }

-dontwarn androidx.**
-keep class androidx.** { *; }
-keep interface androidx.** { *; }

-keep public class io.github.aveuiller.** { public *; }
# Mozila Rhino
-keep class javax.script.** { *; }
-keep class com.sun.script.javascript.** { *; }
-keep class org.mozilla.javascript.** { *; }
-printconfiguration /tmp/rhinosampleapp-r8-config.txt
-dontwarn org.xmlpull.v1.XmlPullParser
-dontwarn org.xmlpull.v1.XmlSerializer
-keep class org.xmlpull.v1.* {*;}
