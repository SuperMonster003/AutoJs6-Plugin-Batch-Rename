package io.github.supermonster003.autojs6.plugin.batch.rename

/**
 * Identity and protocol constants of the plugin (roadmap 5.4).
 *
 * The capability action, category and Binder descriptor live here only until the host contract
 * module `plugin-api/batch-rename-api` exists (roadmap P1.1); P1.1 replaces them with the contract
 * constants so the host and the plugin cannot drift apart.
 *
 * zh-CN: 插件的身份与协议常量 (路线图 5.4). 能力服务的 action, category 与 Binder 描述符只在宿主契约模块
 * `plugin-api/batch-rename-api` 落地 (路线图 P1.1) 之前暂存于此; P1.1 改用契约常量, 避免宿主与插件两侧漂移.
 */
internal object BatchRenamePlugin {

    const val PLUGIN_ID = "batch-rename"
    const val ENGINE = "batch-rename"
    const val VARIANT = "default"

    /** Capability service discovery (roadmap D14). zh-CN: 能力服务的发现 action 与 category (路线图 D14). */
    const val SERVICE_ACTION = "org.autojs.plugin.BATCH_RENAME"
    const val SERVICE_CATEGORY = "batch-rename"

    /** Descriptor of the future `IBatchRenamePlugin` (roadmap appendix C.1). zh-CN: 未来 `IBatchRenamePlugin` 的描述符 (附录 C.1). */
    const val CAPABILITY_DESCRIPTOR = "org.autojs.plugin.batchrename.api.IBatchRenamePlugin"

    /**
     * Placeholder minimum host build (roadmap D26), mirrored by `plugin_requires_host_version` in
     * `app/build.gradle.kts`; P1.3 and P5.4 replace both together.
     * zh-CN: 最低宿主构建号占位值 (路线图 D26), 与 `app/build.gradle.kts` 中的 `plugin_requires_host_version` 一致; P1.3 与
     * P5.4 同时替换两者.
     */
    const val REQUIRED_HOST_VERSION = 5352L

    /**
     * Explorer Action protocol the catalog declares. The catalog stays empty until the v25 actions of
     * roadmap P4.1 exist; P1.2 raises the version to 25.
     * zh-CN: 动作目录声明的 Explorer Action 协议版本. 路线图 P4.1 的 v25 动作落地之前目录为空; P1.2 将版本提升到 25.
     */
    const val EXPLORER_PROTOCOL_VERSION = 24

    /** Resource reference of the localized Plugin Center instruction. zh-CN: 插件中心本地化说明的资源引用. */
    const val INSTRUCTION_REFERENCE = "@raw/plugin_instruction"

}
