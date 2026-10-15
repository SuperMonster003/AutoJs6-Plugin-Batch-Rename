package io.github.supermonster003.autojs6.plugin.batch.rename

import android.app.Service
import android.content.Intent
import android.os.Bundle
import android.os.IBinder
import org.autojs.plugin.batchrename.api.BatchRenameContract
import org.autojs.plugin.batchrename.api.BatchRenameErrorCodes
import org.autojs.plugin.batchrename.api.BatchRenameRequestKeys
import org.autojs.plugin.batchrename.api.BatchRenameResultKeys
import org.autojs.plugin.batchrename.api.IBatchRenamePlugin
import org.autojs.plugin.common.api.PluginInfo

/**
 * Capability service for `org.autojs.plugin.BATCH_RENAME` (category `batch-rename`, roadmap D14), implementing the host
 * contract `IBatchRenamePlugin` v1. `getInfo()` answers the batch rename identity with the contract capabilities, so the
 * Plugin Center can probe the package through this service. Until the engine of roadmap P2 is wired in (P4.1), `plan()`
 * answers every supported request with [BatchRenameErrorCodes.INTERNAL_ERROR] instead of guessing a plan.
 *
 * zh-CN: `org.autojs.plugin.BATCH_RENAME` (category `batch-rename`, 路线图 D14) 的能力服务, 实现宿主契约 `IBatchRenamePlugin` v1.
 * `getInfo()` 返回带契约能力的批量重命名身份, 使插件中心可经由本服务探测该包. 在路线图 P2 的引擎接入 (P4.1) 之前, `plan()` 对每个
 * 受支持的请求都以 [BatchRenameErrorCodes.INTERNAL_ERROR] 应答, 不猜测方案.
 */
class BatchRenamePluginService : Service() {

    private val binder = object : IBatchRenamePlugin.Stub() {

        override fun getInfo(): PluginInfo = pluginRuntimeFields().toPluginInfo()

        override fun plan(request: Bundle?): Bundle = planWithoutEngine(request)
    }

    override fun onBind(intent: Intent?): IBinder = binder

    internal companion object {

        const val ENGINE_UNAVAILABLE_MESSAGE = "The rename engine is not available in this Batch Rename build"

        /** The contract answer before the engine exists; never throws. zh-CN: 引擎落地之前的契约应答; 从不抛出异常. */
        fun planWithoutEngine(request: Bundle?): Bundle {
            val version = request?.getInt(BatchRenameRequestKeys.CONTRACT_VERSION, 0) ?: 0
            val answer = Bundle().apply { putInt(BatchRenameResultKeys.CONTRACT_VERSION, version) }
            return when {
                request == null -> answer.error(BatchRenameErrorCodes.INVALID_REQUEST, "The plan request is missing")
                !BatchRenameContract.supportsContractVersion(version) -> answer.error(
                    BatchRenameErrorCodes.UNSUPPORTED_CONTRACT_VERSION,
                    "Contract version $version is outside ${BatchRenameContract.MIN_CONTRACT_VERSION}..${BatchRenameContract.MAX_CONTRACT_VERSION}",
                )
                else -> answer.error(BatchRenameErrorCodes.INTERNAL_ERROR, ENGINE_UNAVAILABLE_MESSAGE)
            }
        }

        private fun Bundle.error(code: String, message: String): Bundle = apply {
            putString(BatchRenameResultKeys.ERROR_CODE, code)
            putString(BatchRenameResultKeys.ERROR_MESSAGE, message)
        }
    }
}
