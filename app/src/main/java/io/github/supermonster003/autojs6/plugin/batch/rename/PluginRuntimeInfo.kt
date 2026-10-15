package io.github.supermonster003.autojs6.plugin.batch.rename

import android.content.Context
import android.content.pm.PackageInfo
import android.os.Build
import android.os.Bundle
import org.autojs.plugin.batchrename.api.BatchRenameCapabilityKeys
import org.autojs.plugin.batchrename.api.BatchRenameContract
import org.autojs.plugin.batchrename.api.BatchRenameLimits
import org.autojs.plugin.common.api.PluginCapabilityKeys
import org.autojs.plugin.common.api.PluginInfo
import org.autojs.plugin.explorer.api.ExplorerActionCapabilityKeys
import org.autojs.plugin.explorer.api.ExplorerActionPluginIds

/**
 * The PluginInfo fields as plain values, so JVM tests can freeze the mapping without Android.
 * zh-CN: 以纯值表示的 PluginInfo 字段, 使 JVM 测试无需 Android 即可冻结映射.
 */
internal data class PluginRuntimeFields(
    val name: String,
    val description: String,
    val instruction: String,
    val author: String,
    val id: String,
    val engine: String,
    val variant: String,
    val versionName: String,
    val versionCode: Long,
    val versionDate: String,
    /** Empty means "any ABI" (roadmap D28). zh-CN: 空列表表示 "不限 ABI" (路线图 D28). */
    val supportedAbis: List<String>,
    val requiredHostVersion: Long,
    /** Batch Rename contract version and limits advertised to the host (contract v1). zh-CN: 向宿主声明的契约版本与上限. */
    val contractVersion: Int,
    val features: List<String>,
    val maxItems: Int,
    val regexDialect: String,
)

/**
 * Assembles the fields; [id], [engine] and [variant] come from the build resources, which the JVM
 * tests keep equal to [BatchRenamePlugin] and the README sources.
 * zh-CN: 组装字段; [id], [engine] 与 [variant] 来自构建资源, JVM 测试保证其与 [BatchRenamePlugin] 及 README 源一致.
 */
internal fun pluginRuntimeFields(
    name: String,
    description: String,
    author: String,
    id: String,
    engine: String,
    variant: String,
    versionName: String,
    versionCode: Long,
    versionDate: String,
): PluginRuntimeFields = PluginRuntimeFields(
    name = name,
    description = description,
    instruction = BatchRenamePlugin.INSTRUCTION_REFERENCE,
    author = author,
    id = id,
    engine = engine,
    variant = variant,
    versionName = versionName,
    versionCode = versionCode,
    versionDate = versionDate,
    supportedAbis = emptyList(),
    requiredHostVersion = BatchRenamePlugin.REQUIRED_HOST_VERSION,
    contractVersion = BatchRenameContract.CONTRACT_VERSION,
    features = BatchRenamePlugin.FEATURES,
    maxItems = BatchRenameLimits.MAX_ITEMS_PER_PLAN,
    regexDialect = BatchRenameContract.REGEX_DIALECT_ECMASCRIPT,
)

/**
 * The batch rename identity answered by the INFO and capability services.
 * zh-CN: INFO 服务与能力服务返回的批量重命名身份.
 */
internal fun PluginRuntimeFields.toPluginInfo(): PluginInfo = PluginInfo().also { info ->
    info.name = name
    info.description = description
    info.instruction = instruction
    info.author = author
    info.id = id
    info.engine = engine
    info.variant = variant
    info.versionName = versionName
    info.versionCode = versionCode
    info.versionDate = versionDate
    // Explicit even though the default is empty: the plugin runs on every ABI (roadmap D28).
    // zh-CN: 即使默认即为空也显式声明: 插件适用于任意 ABI (路线图 D28).
    info.supportedAbis = supportedAbis.toTypedArray()
    info.capabilities = Bundle().apply {
        putLong(PluginCapabilityKeys.REQUIRES_HOST_VERSION, requiredHostVersion)
        putInt(BatchRenameCapabilityKeys.CONTRACT_VERSION, contractVersion)
        putStringArray(BatchRenameCapabilityKeys.FEATURES, features.toTypedArray())
        putInt(BatchRenameCapabilityKeys.MAX_ITEMS, maxItems)
        putString(BatchRenameCapabilityKeys.REGEX_DIALECT, regexDialect)
    }
}

/**
 * The Explorer Action identity: the same metadata with engine `explorer-action` and only the two
 * capabilities the host's Explorer Action registry reads. The Plugin Center keeps showing the batch
 * rename identity of the INFO service for this package.
 * zh-CN: Explorer Action 身份: 相同的元数据, engine 为 `explorer-action`, 只带宿主 Explorer Action 注册表读取的两项能力.
 * 插件中心对本包仍显示 INFO 服务的批量重命名身份.
 */
internal fun PluginRuntimeFields.toExplorerPluginInfo(): PluginInfo = toPluginInfo().also { info ->
    info.engine = ExplorerActionPluginIds.ENGINE
    info.capabilities = Bundle().apply {
        putLong(PluginCapabilityKeys.REQUIRES_HOST_VERSION, requiredHostVersion)
        putInt(ExplorerActionCapabilityKeys.PROTOCOL_VERSION, BatchRenamePlugin.EXPLORER_PROTOCOL_VERSION)
    }
}

/**
 * Reads the localized description, build resources and the installed package version.
 * zh-CN: 读取本地化描述, 构建资源与已安装包的版本.
 */
internal fun Context.pluginRuntimeFields(): PluginRuntimeFields {
    val appContext = applicationContext
    val packageInfo = appContext.packageManager.getPackageInfo(appContext.packageName, 0)
    return pluginRuntimeFields(
        name = appContext.getString(R.string.app_name),
        description = appContext.getString(R.string.plugin_description),
        author = appContext.getString(R.string.plugin_author),
        id = appContext.getString(R.string.plugin_id),
        engine = appContext.getString(R.string.plugin_engine),
        variant = appContext.getString(R.string.plugin_variant),
        versionName = packageInfo.versionName.orEmpty(),
        versionCode = packageInfo.versionCodeCompat(),
        versionDate = appContext.getString(R.string.plugin_version_date),
    )
}

private fun PackageInfo.versionCodeCompat(): Long {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) return longVersionCode
    @Suppress("DEPRECATION")
    return versionCode.toLong()
}
