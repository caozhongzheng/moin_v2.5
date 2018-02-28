# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in /Users/yanghonghe/Downloads/adt-bundle-mac-x86_64-20140702/sdk/tools/proguard/proguard-android.txt
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
#指定代码的压缩级别
-optimizationpasses 5
#包明不混合大小写
-dontusemixedcaseclassnames
#不去忽略非公共的库类
-dontskipnonpubliclibraryclasses
 #优化  不优化输入的类文件
-dontoptimize
 #预校验
-dontpreverify
 #混淆时是否记录日志
-verbose
 # 混淆时所采用的算法
-optimizations !code/simplification/arithmetic,!field/*,!class/merging/*

-keepattributes *Annotation*

#不告警
-dontwarn butterknife.**
-dontwarn com.makeramen.**
-dontwarn com.tencent.**
-dontwarn demo.**
-dontwarn com.keyboard.**
-dontwarn com.melnykov.**
-dontwarn com.thoughtworks.**
-dontwarn com.umeng.**
-dontwarn it.sephiroth.**
-dontwarn com.moinapp.**
-dontwarn com.dtr.**


#如果有引用v4包可以添加下面这行
-keep public class * extends android.support.v4.app.Fragment
#忽略警告
-ignorewarning
#####################记录生成的日志数据,gradle build时在本项目根目录输出################
#apk 包内所有 class 的内部结构
-dump class_files.txt
#未混淆的类和成员
-printseeds seeds.txt
#列出从 apk 中删除的代码
-printusage unused.txt
#混淆前后的映射
-printmapping mapping.txt

#imageloader
-keep class com.nostra13.universalimageloader.** { *; }

#umeng
-keep class com.umeng.**{*;}

#gifdrawble
-keep class pl.droidsonroids.gif.**{*;}

#butterknife
-keep class **$$ViewInjector { *; }
-keepnames class * { @butterknife.InjectView *;}

#eventbus,以下是用到了eventbus接收的类,不能混淆
-keep class com.moinapp.wuliao.widget.FollowView{*;}
-keep class com.moinapp.wuliao.modules.discovery.ui.FollowFragment{*;}
-keep class com.moinapp.wuliao.modules.discovery.ui.DiscoveryFragment{*;}
-keep class com.moinapp.wuliao.modules.discovery.ui.MainViewPagerFragment{*;}
-keep class com.moinapp.wuliao.modules.discovery.ui.TagDetailViewPagerFragment{*;}
-keep class com.moinapp.wuliao.modules.discovery.ui.CommentListFragment{*;}
-keep class com.moinapp.wuliao.modules.discovery.ui.CosplayDetailFragment{*;}
-keep class com.moinapp.wuliao.modules.post.PostDetailFragment{*;}
-keep class com.moinapp.wuliao.modules.mine.message.MyMessageViewPageFragment{*;}
-keep class com.moinapp.wuliao.modules.mine.MineManager{*;}
-keep class com.moinapp.wuliao.modules.mine.MyCosplayFragment{*;}
-keep class com.moinapp.wuliao.modules.mine.PersonalInfoActivity{*;}
-keep class com.moinapp.wuliao.modules.login.RegistActivity{*;}
-keep class com.moinapp.wuliao.modules.login.UserinfoShortActivity{*;}
-keep class com.moinapp.wuliao.modules.mine.UserActivityFragment{*;}
-keep class com.moinapp.wuliao.modules.mine.UserSpaceFragment{*;}
-keep class com.moinapp.wuliao.modules.mine.InviteListFragment{*;}
-keep class com.moinapp.wuliao.modules.mine.chat.ChatFragment{*;}
-keep class com.moinapp.wuliao.modules.discovery.ui.CommentListFragment{*;}
-keep class com.moinapp.wuliao.modules.mine.chat.ChatListFragment{*;}
-keep class com.moinapp.wuliao.modules.mine.adapter.MyMessagesAdapter{*;}
-keep class com.moinapp.wuliao.modules.stickercamera.app.camera.adapter.StickerListAdaptor{*;}
-keep class com.moinapp.wuliao.modules.stickercamera.app.camera.fragment.MyStickerFragment{*;}
-keep class com.moinapp.wuliao.modules.update.UpdateManager{*;}
-keep class com.moinapp.wuliao.ui.SimpleBackActivity{*;}
-keep class com.moinapp.wuliao.BackgroundService{*;}
-keep class com.moinapp.wuliao.ui.MainActivity{*;}


#floating button
-keep class com.melnykov.fab.**{*;}

#baidu push
-keep class com.baidu.**{*; }

#Gson
-keepattributes Signature
-keepattributes *Annotation*
-keep class sun.misc.Unsafe { *; }
-keep class com.idea.fifaalarmclock.entity.***
-keep class com.google.gson.stream.** { *; }
-keep class com.google.**{*;}
-keep class com.google.gson.examples.android.model.** { *; }
-keep class com.moinapp.wuliao.bean.** { *; }
-keep class com.moinapp.wuliao.modules.discovery.result.** { *; }
-keep class com.moinapp.wuliao.modules.discovery.model.** { *; }
-keep class com.moinapp.wuliao.modules.events.model.** { *; }
-keep class com.moinapp.wuliao.modules.events.result.** { *; }
-keep class com.moinapp.wuliao.modules.mine.model.** { *; }
-keep class com.moinapp.wuliao.modules.login.model.** { *; }
-keep class com.moinapp.wuliao.modules.mine.emoji.** { *; }
-keep class com.moinapp.wuliao.modules.sticker.model.** { *; }
-keep class com.moinapp.wuliao.modules.update.model.** { *; }
-keep class com.moinapp.wuliao.modules.stickercamera.app.model.** { *; }
-keep class com.moinapp.wuliao.commons.init.model.** { *; }

-keepclassmembers class * implements java.io.Serializable {
    static final long serialVersionUID;
    private static final java.io.ObjectStreamField[] serialPersistentFields;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
    java.lang.Object writeReplace();
    java.lang.Object readResolve();
}

#如果引用了v4或者v7包
-keep class android.support.**{*;}

-keep public class * extends android.view.View {
    public <init>(android.content.Context);
    public <init>(android.content.Context, android.util.AttributeSet);
    public <init>(android.content.Context, android.util.AttributeSet, int);
    public void set*(...);
}
#保持 native 方法不被混淆
-keepclasseswithmembernames class * {
    native <methods>;
}
#保持自定义控件类不被混淆
-keepclasseswithmembers class * {
    public <init>(android.content.Context, android.util.AttributeSet);
}
#保持自定义控件类不被混淆
-keepclasseswithmembers class * {
    public <init>(android.content.Context, android.util.AttributeSet, int);
}
#保持自定义控件类不被混淆
-keepclassmembers class * extends android.app.Activity {
   public void *(android.view.View);
}
#保持 Parcelable 不被混淆
-keep class * implements android.os.Parcelable {
  public static final android.os.Parcelable$Creator *;
}
#保持 Serializable 不被混淆
-keepnames class * implements java.io.Serializable

-keepclassmembers class * {
    public void *ButtonClicked(android.view.View);
}
#不混淆资源类
-keepclassmembers class **.R$* {
    public static <fields>;
}

-assumenosideeffects class android.util.Log {
    public static int v(...);
    public static int i(...);
    public static int w(...);
    public static int d(...);
    public static int e(...);
}