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
-keep class com.zkjinshi.superservice.bean.** { *; }
-keep class com.zkjinshi.superservice.entity.** { *; }
-keep class com.zkjinshi.superservice.vo.** { *; }
-keep class com.zkjinshi.superservice.net.** { *; }
-keep class com.zkjinshi.superservice.fragment.**{ *; }
-keep class com.zkjinshi.superservice.activity.**{ *; }

#保留扩展控件不被混淆
-keep class android.support.**{ *; }
-dontwarn android.support.**

#---根据 Android Dependencies 配置library工程包start---#

 #1 自定义日期选择器
-libraryjars ..\\CalendarListview
-dontwarn com.andexert.calendarlistview.calendarlistview.**
-keep class com.andexert.calendarlistview.calendarlistview.** { *; }

 #2 自定义标签控件
-libraryjars ..\\Cloud_TagView
-dontwarn me.kaede.tagviewr.**
-keep class me.kaede.tagview.** { *; }

 #3 自定义文件选择器
-libraryjars ..\\library.FileChoser
-dontwarn com.zkjinshi.filechoser.**
-keep class com.zkjinshi.filechoser.** { *; }

 #4 自定义图片选择器
-libraryjars ..\\multi-image-selector
-dontwarn me.nereo.multi_image_selector.**
-keep class me.nereo.multi_image_selector.** { *; }

 #5 中科金石基类
-libraryjars ..\\ZKJinShiBaseClass
-dontwarn com.zkjinshi.base.**
-keep class com.zkjinshi.base.** { *; }

#---配置library工程包over---#

#----根据 Android Private Libraries 配置lib jar包start---#

 #1 短信验证包
-dontwarn com.cloopen.rest.**
-keep class com.cloopen.rest.** { *; }
-dontwarn de.mindpipe.android.logging.log4j.**
-keep class de.mindpipe.android.logging.log4j.** { *; }
-dontwarn org.**
-keep class org.** { *; }
-dontwarn ytx.org.apache.http.**
-keep class ytx.org.apache.http.** { *; }

 #2 mime网络框架包
-dontwarn org.apache.http.entity.mime.**
-keep class org.apache.http.entity.mime.**{ *; }

 #3 xls/xlsx电子表格解析包
-dontwarn jxl.**
-keep class jxl.**{ *; }

 #4 云测包
-dontwarn com.testin.agent.**
-keep class com.testin.agent.** { *; }

#---配置lib jar包over---#



