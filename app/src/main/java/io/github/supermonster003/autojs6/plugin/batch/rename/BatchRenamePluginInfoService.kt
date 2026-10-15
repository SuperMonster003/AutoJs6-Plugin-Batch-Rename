package io.github.supermonster003.autojs6.plugin.batch.rename

import android.app.Service
import android.content.Intent
import android.os.IBinder
import org.autojs.plugin.common.api.IPluginInfoProvider
import org.autojs.plugin.common.api.PluginInfo

/**
 * Answers `org.autojs.plugin.INFO` (category `batch-rename`) so the Plugin Center lists the batch
 * rename identity of this package, independent of the Explorer Action identity.
 * zh-CN: 响应 `org.autojs.plugin.INFO` (category `batch-rename`), 使插件中心显示本包的批量重命名身份, 与 Explorer Action
 * 身份互不影响.
 */
class BatchRenamePluginInfoService : Service() {

    private val binder = object : IPluginInfoProvider.Stub() {
        override fun getInfo(): PluginInfo = pluginRuntimeFields().toPluginInfo()
    }

    override fun onBind(intent: Intent?): IBinder = binder

}
