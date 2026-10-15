# Host contract classes cross the Binder boundary by name (AIDL descriptors, PluginInfo Parcelable).
# The AARs ship consumer rules as well; this keeps the plugin safe if a staged AAR omits them.
# zh-CN: 宿主契约类按名称跨越 Binder 边界 (AIDL 描述符, PluginInfo Parcelable). AAR 自带 consumer 规则, 此处在 AAR 缺少规则时兜底.
-keep class org.autojs.plugin.** { *; }

-dontwarn kotlinx.parcelize.Parcelize
