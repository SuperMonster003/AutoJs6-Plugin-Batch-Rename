# Host contract classes cross the Binder boundary by name (AIDL descriptors, PluginInfo Parcelable).
# The AARs ship consumer rules as well; this keeps the plugin safe if a staged AAR omits them.
# zh-CN: 宿主契约类按名称跨越 Binder 边界 (AIDL 描述符, PluginInfo Parcelable). AAR 自带 consumer 规则, 此处在 AAR 缺少规则时兜底.
-keep class org.autojs.plugin.** { *; }

# Rhino (roadmap P2.1 / D15): only engine/regex references the slim jar, and R8 retains the interpreter closure it
# reaches. Rhino looks two classes up by name and builds them reflectively (Context finds its Evaluator, VMBridge its
# platform bridge); RegExp comes through ServiceLoader, whose service files R8 renames together with the classes
# (verified with a release build against the host in roadmap P2.5). The slim jar still declares JVM-only entry points
# (javax.script, java.beans) that Android lacks.
# zh-CN: 只有 engine/regex 引用 slim jar, R8 保留其用到的解释器闭包. Rhino 按名称查找并反射构造两个类 (Context 的
# Evaluator, VMBridge 的平台桥接); RegExp 经 ServiceLoader 加载, R8 会把服务文件与类一同改名 (路线图 P2.5 用 release 构建与宿主
# 联调验证). slim jar 仍声明 Android 缺少的 JVM 专用入口.
-keep class org.mozilla.javascript.Interpreter { <init>(); }
-keep class org.mozilla.javascript.jdk18.VMBridge_jdk18 { <init>(); }
-dontwarn org.mozilla.javascript.**
-dontwarn org.mozilla.classfile.**

-dontwarn kotlinx.parcelize.Parcelize
