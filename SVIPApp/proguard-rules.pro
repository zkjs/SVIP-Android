# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in E:\android-soft\adt-bundle-windows-x86_64-20140702\sdk/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}
 # 指定代码的压缩级别
-optimizationpasses 5

 # 是否使用大小写混合
-dontusemixedcaseclassnames

 # 是否混淆第三方jar
-dontskipnonpubliclibraryclasses

 # 混淆时是否做预校验
-dontpreverify

 # 混淆时是否记录日志
-verbose

 # 混淆时所采用的算法
-optimizations !code/simplification/arithmetic,!field/*,!class/merging/*

 # 保持四大组件以及android原生类和派生类不被混淆
-keep public class * extends android.app.Activity
-keep public class * extends android.app.Application
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider
-keep public class * extends android.app.backup.BackupAgentHelper
-keep public class * extends android.preference.Preference
-keep public class com.android.vending.licensing.ILicensingService

# 忽略警告
-ignorewarning

 # 保持 native 方法不被混淆
-keepclasseswithmembernames class * {
    native <methods>;
}

 # 保持自定义控件重载方法不被混淆
-keepclasseswithmembernames class * {
    public <init>(android.content.Context, android.util.AttributeSet);
}

 # 保持自定义控件另一重载方法不被混淆
-keepclasseswithmembernames class * {
    public <init>(android.content.Context, android.util.AttributeSet, int);
}

 # 保持自定义控件类不被混淆
-keepclassmembers class * extends android.app.Activity {
    public void *(android.view.View);
}

 # 保持枚举 enum 类不被混淆
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

 # 保持 Parcelable 不被混淆
-keep class * implements android.os.Parcelable {
    public static final android.os.Parcelable$Creator *;
}

 # 保持签名类不被混淆
-keepattributes Signature

 # 保持注解不被混淆
-keepattributes *Annotation*
-keep class sun.misc.Unsafe { *; }
-keep class com.google.gson.examples.android.model.** { *; }
-keep class com.guosen.crm.common.ui.launcher.LauncherPageGridView.** {*;}

# 防止R被混淆，R类反射混淆，找不到资源ID
-keep class **.R$* {
    *;
}

#保留网络请求的实体不被混淆
-keep class com.zkjinshi.svip.vo.** { *; }
-keep class com.zkjinshi.svip.net.** { *; }
-keep class com.zkjinshi.svip.activity.**{ *; }
-keep class com.zkjinshi.svip.view.**{ *; }
-keep class com.zkjinshi.base.util.**{ *;}
-keep class com.zkjinshi.svip.response.**{ *; }

#保留扩展控件不被混淆
-keep class android.support.**{ *; }
-dontwarn android.support.**

#---根据 Android Dependencies 配置library工程包start---#


 #1 自定义图片选择器
-dontwarn me.nereo.multi_image_selector.**
-keep class me.nereo.multi_image_selector.** { *; }



 #2 中科金石基类
-dontwarn com.zkjinshi.base.**
-keep class com.zkjinshi.base.** { *; }

-keep class com.facebook.**{*;}

#---配置library工程包over---#

#----根据 Android Private Libraries 配置lib jar包start---#

 #mime网络框架包
-dontwarn org.apache.http.entity.mime.**
-keep class org.apache.http.entity.mime.**{ *; }

#高德地图
-dontwarn com.amap.api.**
-keep class com.amap.api.**{ *; }
-dontwarn com.autonavi.aps.amapapi.model.**
-keep class com.autonavi.aps.amapapi.model.**{ *; }
-dontwarn com.loc.**
-keep class com.loc.**{ *; }

#云巴
-dontwarn io.yunba.android.**
-keep class io.yunba.android.**{ *; }
-dontwarn org.eclipse.paho.client.mqttv3.**
-keep class org.eclipse.paho.client.mqtv3.**{ *; }

 #Pyxis sdk
-keep class com.zkjinshi.pyxis.bluetooth.** { *; }
-keep class com.zkjinshi.pyxis.utils.** { *; }
-keep class org.altbeacon.**{ *; }

#ping++
-dontwarn com.alipay.**
-keep class com.alipay.** {*;}

-dontwarn  com.ta.utdid2.**
-keep class com.ta.utdid2.** {*;}

-dontwarn  com.ut.device.**
-keep class com.ut.device.** {*;}

-dontwarn  com.tencent.**
-keep class com.tencent.** {*;}

-dontwarn  com.unionpay.**
-keep class com.unionpay.** {*;}

-dontwarn com.pingplusplus.**
-keep class com.pingplusplus.** {*;}

-dontwarn com.baidu.**
-keep class com.baidu.** {*;}

-keepclassmembers class * {
    @android.webkit.JavascriptInterface <methods>;
}

-dontwarn org.apache.commons.**
-keep class org.apache.http.impl.client.**
-dontwarn org.apache.commons.**
-keep class com.blueware.** { *; }
-dontwarn com.blueware.**
-keepattributes Exceptions, Signature, InnerClasses
-keepattributes SourceFile ,LineNumberTable

#---配置lib jar包over---#





