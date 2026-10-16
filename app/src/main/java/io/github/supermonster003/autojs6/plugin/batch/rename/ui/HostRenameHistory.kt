package io.github.supermonster003.autojs6.plugin.batch.rename.ui

import android.content.Context
import android.content.Intent
import android.util.Log
import org.autojs.plugin.batchrename.api.BatchRenamePluginActions
import org.autojs.plugin.common.api.AutoJs6HostSettingsContract

/**
 * The rename history page of the host (roadmap D38 / D51), which lists the batches of the file manager and of scripts
 * and undoes or deletes them. Only hosts that resolve [BatchRenamePluginActions.RENAME_HISTORY] have it; the page
 * requires the plugin permission, which the plugin holds.
 * zh-CN: 宿主的重命名历史页面 (路线图 D38 / D51), 列出文件管理器与脚本产生的批次, 可撤销或删除. 只有能解析
 * [BatchRenamePluginActions.RENAME_HISTORY] 的宿主才有该页面; 页面要求插件权限, 插件持有该权限.
 */
internal object HostRenameHistory {

    private const val TAG = "HostRenameHistory"

    /** The intent of the page, or null when the host has none. zh-CN: 打开页面的 Intent; 宿主没有该页面时为 null. */
    fun intent(context: Context): Intent? = Intent(BatchRenamePluginActions.RENAME_HISTORY)
        .setPackage(AutoJs6HostSettingsContract.HOST_PACKAGE_NAME)
        .takeIf { context.packageManager.resolveActivity(it, 0) != null }

    fun open(context: Context, intent: Intent) {
        runCatching { context.startActivity(intent) }.onFailure { error ->
            Log.w(TAG, "Could not open the rename history of the host", error)
        }
    }
}
