package io.github.supermonster003.autojs6.plugin.batch.rename

import android.app.Service
import android.content.Intent
import android.os.Bundle
import android.os.IBinder
import android.os.ParcelFileDescriptor
import io.github.supermonster003.autojs6.plugin.batch.rename.explorer.BatchRenameCatalog
import org.autojs.plugin.common.api.PluginInfo
import org.autojs.plugin.explorer.api.ExplorerArchiveOpenKeys
import org.autojs.plugin.explorer.api.ExplorerArchiveOpenValues
import org.autojs.plugin.explorer.api.IExplorerActionPlugin
import org.autojs.plugin.explorer.api.IExplorerArchiveSession

/**
 * Answers `org.autojs.plugin.EXPLORER_ACTION` for the AutoJs6 file manager with the three rename actions of roadmap
 * D23 ([BatchRenameCatalog]). The archive and host-file-information transactions belong to other action shapes and
 * are never used by this plugin; they answer with an error and close the received descriptor.
 *
 * zh-CN: 为 AutoJs6 文件管理器响应 `org.autojs.plugin.EXPLORER_ACTION`, 提供路线图 D23 的三个重命名动作 ([BatchRenameCatalog]).
 * 归档与宿主文件信息事务属于其他动作形态, 本插件从不使用; 它们返回错误并关闭收到的描述符.
 */
class BatchRenameExplorerActionService : Service() {

    private val binder = object : IExplorerActionPlugin.Stub() {

        override fun getInfo(): PluginInfo = pluginRuntimeFields().toExplorerPluginInfo()

        override fun getActionCatalog(): Bundle = BatchRenameCatalog.catalog(BatchRenamePlugin.EXPLORER_PROTOCOL_VERSION)

        override fun openArchive(source: ParcelFileDescriptor?, request: Bundle?): IExplorerArchiveSession? {
            closeQuietly(source)
            return null
        }

        override fun openArchiveV11(source: ParcelFileDescriptor?, request: Bundle?): Bundle {
            closeQuietly(source)
            return Bundle().apply {
                putInt(ExplorerArchiveOpenKeys.ERROR_CODE, ExplorerArchiveOpenValues.ERROR_UNRECOVERABLE)
                putString(ExplorerArchiveOpenKeys.ERROR_MESSAGE, "Batch Rename does not open archives")
            }
        }

        override fun getHostFileInfo(source: ParcelFileDescriptor?, request: Bundle?): Bundle? {
            closeQuietly(source)
            return null
        }
    }

    override fun onBind(intent: Intent?): IBinder = binder

    private fun closeQuietly(descriptor: ParcelFileDescriptor?) {
        runCatching { descriptor?.close() }
    }

}
