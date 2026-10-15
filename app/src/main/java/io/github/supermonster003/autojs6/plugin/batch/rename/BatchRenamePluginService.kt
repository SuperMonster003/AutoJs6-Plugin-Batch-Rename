package io.github.supermonster003.autojs6.plugin.batch.rename

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder

/**
 * Capability service for `org.autojs.plugin.BATCH_RENAME` (category `batch-rename`, roadmap D14).
 *
 * Until the engine is wired in (roadmap P4.1), it answers a placeholder Binder that only carries the
 * `IBatchRenamePlugin` descriptor of the host contract, so discovery and binding can already be tested;
 * any transaction is rejected by the default Binder instead of being answered with guesses.
 *
 * zh-CN: `org.autojs.plugin.BATCH_RENAME` (category `batch-rename`, 路线图 D14) 的能力服务. 在接入引擎 (路线图 P4.1) 之前,
 * 它返回只携带宿主契约 `IBatchRenamePlugin` 描述符的占位 Binder, 以便提前测试发现与绑定; 任何事务都会被默认 Binder 拒绝,
 * 不以猜测的结果应答.
 */
class BatchRenamePluginService : Service() {

    private val binder = Binder().apply {
        attachInterface(null, BatchRenamePlugin.CAPABILITY_DESCRIPTOR)
    }

    override fun onBind(intent: Intent?): IBinder = binder

}
