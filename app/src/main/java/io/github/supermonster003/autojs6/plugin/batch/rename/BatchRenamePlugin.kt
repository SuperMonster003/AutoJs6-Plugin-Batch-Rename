package io.github.supermonster003.autojs6.plugin.batch.rename

import org.autojs.plugin.batchrename.api.BatchRenamePluginActions
import org.autojs.plugin.batchrename.api.BatchRenamePluginIds
import org.autojs.plugin.batchrename.api.IBatchRenamePlugin

/**
 * Identity and protocol constants of the plugin (roadmap 5.4). The discovery literals come from the host contract
 * `plugin-api/batch-rename-api` (roadmap P1.1), so the host and the plugin cannot drift apart; only plugin-side values
 * live here.
 *
 * zh-CN: 插件的身份与协议常量 (路线图 5.4). 发现用字面值取自宿主契约 `plugin-api/batch-rename-api` (路线图 P1.1), 避免宿主与插件
 * 两侧漂移; 这里只保留插件侧的取值.
 */
internal object BatchRenamePlugin {

    const val PLUGIN_ID = BatchRenamePluginIds.ID
    const val ENGINE = BatchRenamePluginIds.ENGINE
    const val VARIANT = BatchRenamePluginIds.VARIANT_DEFAULT

    /** Capability service discovery (roadmap D14). zh-CN: 能力服务的发现 action 与 category (路线图 D14). */
    const val SERVICE_ACTION = BatchRenamePluginActions.BATCH_RENAME
    const val SERVICE_CATEGORY = BatchRenamePluginActions.SERVICE_CATEGORY

    /** Descriptor of `IBatchRenamePlugin` (contract v1). zh-CN: `IBatchRenamePlugin` 的描述符 (契约 v1). */
    const val CAPABILITY_DESCRIPTOR = IBatchRenamePlugin.DESCRIPTOR

    /**
     * Minimum host build (roadmap D26): the first build with the contract, protocol v25 and the Plugin Center
     * registration. Mirrored by `plugin_requires_host_version` in `app/build.gradle.kts`; P5.4 sets the final value.
     * zh-CN: 最低宿主构建号 (路线图 D26): 首个包含契约, 协议 v25 与插件中心注册的宿主构建号. 与 `app/build.gradle.kts` 中的
     * `plugin_requires_host_version` 一致; P5.4 回填最终值.
     */
    const val REQUIRED_HOST_VERSION = 5431L

    /**
     * Optional contract features this build implements (`BatchRenameValues.FEATURES`). Empty until the engine of
     * roadmap P2 is wired in; the host never relies on a feature that is not advertised.
     * zh-CN: 本构建实现的可选契约功能. 路线图 P2 的引擎接入之前为空; 宿主不会依赖未声明的功能.
     */
    val FEATURES: List<String> = emptyList()

    /**
     * Explorer Action protocol the catalog declares: v25 adds `targetRename` and the rename dialog placement
     * (roadmap D22). The catalog stays empty until the actions of roadmap P4.1 exist.
     * zh-CN: 动作目录声明的 Explorer Action 协议版本: v25 加入 `targetRename` 与重命名对话框位置 (路线图 D22).
     * 路线图 P4.1 的动作落地之前目录为空.
     */
    const val EXPLORER_PROTOCOL_VERSION = 25

    /** Resource reference of the localized Plugin Center instruction. zh-CN: 插件中心本地化说明的资源引用. */
    const val INSTRUCTION_REFERENCE = "@raw/plugin_instruction"

}
