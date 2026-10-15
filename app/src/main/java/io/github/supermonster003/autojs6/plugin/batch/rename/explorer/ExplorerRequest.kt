package io.github.supermonster003.autojs6.plugin.batch.rename.explorer

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import io.github.supermonster003.autojs6.plugin.batch.rename.BatchRenamePlugin
import org.autojs.plugin.explorer.api.ExplorerActionHostSessionKeys
import org.autojs.plugin.explorer.api.ExplorerActionIntentExtras
import org.autojs.plugin.explorer.api.ExplorerActionPluginActions
import org.autojs.plugin.explorer.api.ExplorerActionProtocol
import org.autojs.plugin.explorer.api.ExplorerActionTargetKeys
import org.autojs.plugin.explorer.api.ExplorerActionValues
import org.autojs.plugin.explorer.api.IExplorerActionHostSession

/**
 * An `EXPLORER_ACTION_EXECUTE` request of one of the catalog actions: the action, its targets and the host session
 * that lists and renames them.
 * zh-CN: 目录动作之一的 `EXPLORER_ACTION_EXECUTE` 请求: 动作, 其目标, 以及负责列出与改名的宿主会话.
 */
internal data class ExplorerRequest(
    val action: CatalogAction,
    val requestId: String,
    /** The folder the targets are in, for display only. zh-CN: 目标所在文件夹, 仅用于显示. */
    val parentDisplayPath: String,
    val targets: List<RequestTarget>,
    val session: IExplorerActionHostSession,
)

/** What came of reading a request. zh-CN: 读取请求的结果. */
internal sealed interface ExplorerRequestResult {
    data class Accepted(val request: ExplorerRequest) : ExplorerRequestResult

    /** The host is older than this plugin needs (protocol below 25 or build below the minimum). zh-CN: 宿主过旧. */
    data object HostTooOld : ExplorerRequestResult

    data class Rejected(val reason: String) : ExplorerRequestResult
}

/**
 * Reads and checks the launch intent (Explorer Action protocol v25). The plugin trusts nothing it cannot check: the
 * action must be one of its own, the target list must fit the action's shape and the session must be an
 * `IExplorerActionHostSession`.
 * zh-CN: 读取并检查启动 Intent (Explorer Action 协议 v25). 插件不信任任何无法检查的内容: 动作必须属于本插件, 目标列表必须符合动作
 * 的形态, 会话必须是 `IExplorerActionHostSession`.
 */
internal object ExplorerRequestParser {

    fun parse(intent: Intent?): ExplorerRequestResult = try {
        parseUnchecked(intent)
    } catch (error: RuntimeException) {
        ExplorerRequestResult.Rejected(error.message ?: error.toString())
    }

    /** Closes the host session of a request the plugin will not serve. zh-CN: 关闭本插件不予处理的请求的宿主会话. */
    fun closeSession(intent: Intent?) {
        runCatching { sessionOf(intent)?.close() }
    }

    private fun parseUnchecked(intent: Intent?): ExplorerRequestResult {
        if (intent?.action != ExplorerActionPluginActions.EXECUTE) return ExplorerRequestResult.Rejected("not an Explorer Action request")
        val protocolVersion = intent.getIntExtra(ExplorerActionIntentExtras.PROTOCOL_VERSION, Int.MIN_VALUE)
        if (protocolVersion in 1 until BatchRenamePlugin.EXPLORER_PROTOCOL_VERSION) return ExplorerRequestResult.HostTooOld
        if (protocolVersion != BatchRenamePlugin.EXPLORER_PROTOCOL_VERSION) return ExplorerRequestResult.Rejected("protocol $protocolVersion is not supported")
        val hostVersion = intent.getLongExtra(ExplorerActionIntentExtras.HOST_VERSION_CODE, Long.MIN_VALUE)
        if (hostVersion != Long.MIN_VALUE && hostVersion < BatchRenamePlugin.REQUIRED_HOST_VERSION) return ExplorerRequestResult.HostTooOld
        val action = BatchRenameCatalog.action(intent.getStringExtra(ExplorerActionIntentExtras.ACTION_ID))
            ?: return ExplorerRequestResult.Rejected("unknown action")
        val requestId = intent.getStringExtra(ExplorerActionIntentExtras.REQUEST_ID)
        require(!requestId.isNullOrBlank()) { "the request has no ID" }
        val targets = bundleList(intent, ExplorerActionIntentExtras.TARGETS).map(::target)
        require(targets.size in 1..ExplorerActionProtocol.MAX_TARGETS_PER_REQUEST) { "the request has no targets or too many" }
        require(targets.map { it.id }.toSet().size == targets.size) { "target IDs repeat" }
        when (action.scope) {
            RenameScope.INSIDE_DIRECTORY -> require(targets.size == 1 && targets[0].directory) { "the folder action needs one folder" }
            RenameScope.TARGETS -> if (action.cardinality == ExplorerActionValues.CARDINALITY_SINGLE) {
                require(targets.size == 1) { "the action needs one target" }
            }
        }
        val session = sessionOf(intent) ?: return ExplorerRequestResult.Rejected("the request has no host session")
        val parent = intent.getStringExtra(ExplorerActionIntentExtras.PARENT_DISPLAY_PATH).orEmpty()
        return ExplorerRequestResult.Accepted(ExplorerRequest(action, requestId, parent, targets, session))
    }

    private fun target(bundle: Bundle): RequestTarget {
        val id = bundle.getString(ExplorerActionTargetKeys.ID)
        require(!id.isNullOrBlank() && id.length <= ExplorerActionProtocol.MAX_TARGET_ID_LENGTH) { "a target has no valid ID" }
        val name = bundle.getString(ExplorerActionTargetKeys.DISPLAY_NAME)
        require(!name.isNullOrEmpty()) { "a target has no name" }
        val directory = when (bundle.getInt(ExplorerActionTargetKeys.KIND, 0)) {
            ExplorerActionValues.TARGET_FILE -> false
            ExplorerActionValues.TARGET_DIRECTORY -> true
            else -> throw IllegalArgumentException("a target has no valid kind")
        }
        return RequestTarget(
            id = id,
            name = name,
            directory = directory,
            size = bundle.getLong(ExplorerActionTargetKeys.SIZE, -1L),
            lastModified = bundle.getLong(ExplorerActionTargetKeys.LAST_MODIFIED, 0L),
        )
    }

    private fun sessionOf(intent: Intent?): IExplorerActionHostSession? {
        val holder = intent?.let { bundleExtra(it, ExplorerActionIntentExtras.HOST_SESSION) } ?: return null
        val binder: IBinder = holder.getBinder(ExplorerActionHostSessionKeys.BINDER) ?: return null
        if (runCatching { binder.interfaceDescriptor }.getOrNull() != IExplorerActionHostSession.DESCRIPTOR) return null
        return IExplorerActionHostSession.Stub.asInterface(binder)
    }

    private fun bundleExtra(intent: Intent, key: String): Bundle? =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra(key, Bundle::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent.getParcelableExtra(key)
        }

    private fun bundleList(intent: Intent, key: String): List<Bundle> =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableArrayListExtra(key, Bundle::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent.getParcelableArrayListExtra(key)
        }.orEmpty()
}
