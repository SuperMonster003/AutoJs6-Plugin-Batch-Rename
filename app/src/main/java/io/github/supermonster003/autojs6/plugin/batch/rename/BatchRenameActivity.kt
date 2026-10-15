package io.github.supermonster003.autojs6.plugin.batch.rename

import android.content.Context
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import io.github.supermonster003.autojs6.plugin.batch.rename.explorer.ExplorerRequestParser
import io.github.supermonster003.autojs6.plugin.batch.rename.explorer.ExplorerRequestResult
import io.github.supermonster003.autojs6.plugin.batch.rename.ui.BatchRenameScreen
import io.github.supermonster003.autojs6.plugin.batch.rename.ui.BatchRenameTheme
import io.github.supermonster003.autojs6.plugin.batch.rename.ui.BatchRenameViewModel
import io.github.supermonster003.autojs6.plugin.batch.rename.ui.HostAppearance

/**
 * The rename screen of the three catalog actions (roadmap D23, P4.2): reads the `EXPLORER_ACTION_EXECUTE` request,
 * follows the host's appearance and hands the request to [BatchRenameViewModel]. A request this plugin cannot serve
 * closes its host session and ends the activity; an older host gets a localized hint first.
 *
 * zh-CN: 三个目录动作的重命名界面 (路线图 D23, P4.2): 读取 `EXPLORER_ACTION_EXECUTE` 请求, 跟随宿主外观, 把请求交给
 * [BatchRenameViewModel]. 本插件无法处理的请求会关闭其宿主会话并结束 Activity; 宿主过旧时先给出本地化提示.
 */
class BatchRenameActivity : ComponentActivity() {

    private val model by viewModels<BatchRenameViewModel>()
    private var appearance = HostAppearance.SYSTEM

    override fun attachBaseContext(newBase: Context) {
        appearance = HostAppearance.load(newBase)
        super.attachBaseContext(appearance.wrap(newBase))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        if (!model.attached) {
            when (val result = ExplorerRequestParser.parse(intent)) {
                is ExplorerRequestResult.Accepted -> model.attach(result.request)
                ExplorerRequestResult.HostTooOld -> return refuse(getString(R.string.error_host_too_old))
                is ExplorerRequestResult.Rejected -> return refuse(getString(R.string.error_request_invalid))
            }
        }
        setContent {
            val state by model.state.collectAsState()
            BatchRenameTheme(appearance) {
                BatchRenameScreen(
                    state = state,
                    model = model,
                    onClose = ::close,
                )
            }
        }
    }

    private fun refuse(message: String) {
        ExplorerRequestParser.closeSession(intent)
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
        setResult(RESULT_CANCELED)
        finish()
    }

    private fun close() {
        setResult(if (model.state.value.changedFiles) RESULT_OK else RESULT_CANCELED)
        finish()
    }
}
