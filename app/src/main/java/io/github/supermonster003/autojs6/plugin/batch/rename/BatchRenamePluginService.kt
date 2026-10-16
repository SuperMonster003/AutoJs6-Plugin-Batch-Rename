package io.github.supermonster003.autojs6.plugin.batch.rename

import android.app.Service
import android.content.Intent
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import io.github.supermonster003.autojs6.plugin.batch.rename.engine.RenameEngine
import io.github.supermonster003.autojs6.plugin.batch.rename.service.BatchRenamePlanner
import io.github.supermonster003.autojs6.plugin.batch.rename.service.BundleMaps
import io.github.supermonster003.autojs6.plugin.batch.rename.service.PlanCodec
import io.github.supermonster003.autojs6.plugin.batch.rename.service.PresetCalls
import io.github.supermonster003.autojs6.plugin.batch.rename.presets.PresetStore
import io.github.supermonster003.autojs6.plugin.batch.rename.settings.SharedPreferencesStore
import org.autojs.plugin.batchrename.api.BatchRenameErrorCodes
import org.autojs.plugin.batchrename.api.IBatchRenamePlugin
import org.autojs.plugin.common.api.PluginInfo

/**
 * Capability service for `org.autojs.plugin.BATCH_RENAME` (category `batch-rename`, roadmap D14), implementing the host
 * contract `IBatchRenamePlugin` v1. `getInfo()` answers the batch rename identity with the contract capabilities, so the
 * Plugin Center can probe the package through this service; `plan()` runs the rename engine on the request (roadmap
 * D13), resolving a named preset, and `presets()` / `importPresets()` read and extend the presets (roadmap D36). No
 * method throws: failures come back as contract error codes.
 *
 * zh-CN: `org.autojs.plugin.BATCH_RENAME` (category `batch-rename`, 路线图 D14) 的能力服务, 实现宿主契约 `IBatchRenamePlugin` v1.
 * `getInfo()` 返回带契约能力的批量重命名身份, 使插件中心可经由本服务探测该包; `plan()` 对请求运行改名引擎 (路线图 D13) 并解析命名预设,
 * `presets()` / `importPresets()` 读取与扩充预设 (路线图 D36). 所有方法都不抛出异常: 失败以契约错误码返回.
 */
class BatchRenamePluginService : Service() {

    private val binder = object : IBatchRenamePlugin.Stub() {

        override fun getInfo(): PluginInfo = pluginRuntimeFields().toPluginInfo()

        override fun plan(request: Bundle?): Bundle {
            val values = try {
                request?.let(BundleMaps::toMap)
            } catch (error: RuntimeException) {
                // A Parcel the plugin cannot read (unknown classes, corrupted data). zh-CN: 插件无法读取的 Parcel.
                return BundleMaps.toBundle(PlanCodec.encodeError(BatchRenameErrorCodes.INVALID_REQUEST, "Unreadable request: $error", 0))
            }
            return BundleMaps.toBundle(planner.answer(values))
        }

        override fun presets(request: Bundle?): Bundle = call(request, presetCalls::list)

        override fun importPresets(request: Bundle?): Bundle = call(request, presetCalls::import)

        private fun call(request: Bundle?, block: (Map<String, Any?>?) -> Map<String, Any?>): Bundle {
            val values = try {
                request?.let(BundleMaps::toMap)
            } catch (error: RuntimeException) {
                return BundleMaps.toBundle(PlanCodec.encodeError(BatchRenameErrorCodes.INVALID_REQUEST, "Unreadable request: $error", 0))
            }
            return BundleMaps.toBundle(block(values))
        }
    }

    /** The presets of roadmap D36, shared with the screen. zh-CN: 路线图 D36 的预设, 与界面共用. */
    private val presetStore by lazy { PresetStore(SharedPreferencesStore(applicationContext)) }

    private val planner by lazy {
        BatchRenamePlanner(engine, presets = { name -> presetStore.find(name)?.options }) { message -> Log.w(TAG, message) }
    }

    private val presetCalls by lazy { PresetCalls({ presetStore }) { message -> Log.w(TAG, message) } }

    override fun onBind(intent: Intent?): IBinder = binder

    private companion object {
        const val TAG = "BatchRenamePlugin"

        /** One engine for the process: it holds the shared sealed Rhino scope. zh-CN: 进程内唯一的引擎, 持有共享的封存 Rhino 作用域. */
        val engine = RenameEngine()
    }
}
