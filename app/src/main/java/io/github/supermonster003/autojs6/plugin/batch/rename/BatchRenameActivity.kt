package io.github.supermonster003.autojs6.plugin.batch.rename

import android.app.Activity
import android.os.Bundle

/**
 * Target of the `org.autojs.plugin.EXPLORER_ACTION_EXECUTE` requests of the three catalog actions
 * (roadmap D23). The Compose rename screen arrives in roadmap P4.2; until then the catalog is empty,
 * the host never starts this activity, and a stray start is cancelled at once.
 *
 * zh-CN: 三个目录动作 (路线图 D23) 的 `org.autojs.plugin.EXPLORER_ACTION_EXECUTE` 请求目标. Compose 重命名界面在路线图 P4.2
 * 落地; 在此之前目录为空, 宿主不会启动本 Activity, 意外启动会立即取消.
 */
class BatchRenameActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setResult(RESULT_CANCELED)
        finish()
    }
}
