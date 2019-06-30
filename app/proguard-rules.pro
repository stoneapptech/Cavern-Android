# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class nickname to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file nickname.
#-renamesourcefileattribute SourceFile
-dontwarn javax.annotation.**
-keepnames class okhttp3.internal.publicsuffix.PublicSuffixDatabase
-dontwarn org.codehaus.mojo.animal_sniffer.*
-dontwarn okhttp3.internal.platform.ConscryptPlatform

-dontwarn com.caverock.androidsvg.**

-keep public class * extends tech.stoneapp.secminhr.cavern.editor.tools.EditorTool
-keepclassmembers class * extends tech.stoneapp.secminhr.cavern.editor.tools.EditorTool {
 public <init>(int);
}
-keepnames public class ru.noties.prism4j.Prism4j
-keep public class ru.noties.prism4j.languages.*
-keepclassmembers class ru.noties.prism4j.languages.* {
 public static *** create(***);
}