package io.github.supermonster003.autojs6.plugin.batch.rename.explorer

import android.os.Binder
import android.os.Build
import android.os.Bundle
import android.os.Parcel
import org.autojs.plugin.explorer.api.ExplorerActionHostSessionKeys
import org.autojs.plugin.explorer.api.ExplorerActionHostSessionValues
import org.autojs.plugin.explorer.api.ExplorerActionProtocol
import org.autojs.plugin.explorer.api.ExplorerActionValues
import org.autojs.plugin.explorer.api.IExplorerActionHostSession

/** One entry of a rename batch as the host reports it, in request order. zh-CN: 宿主报告的批次条目, 按请求顺序. */
internal data class RenameItemResult(val state: Int, val newRelativePath: String)

/** A rename batch as the host reports it. zh-CN: 宿主报告的改名批次. */
internal data class RenameBatchResult(
    val batchId: String,
    val state: Int,
    val createdAt: Long,
    val renamed: Int,
    val notRenamed: Int,
    val restored: Int,
    val items: List<RenameItemResult>,
) {
    val known: Boolean get() = state != ExplorerActionHostSessionValues.RENAME_BATCH_STATE_UNKNOWN
}

/**
 * The plugin's side of the host session (Explorer Action protocol v25): pages `listRenameParent` and `listChildren`,
 * sends `renameEntries` in requests that fit one Binder transaction, and reads the rename history. Every call blocks,
 * so callers stay off the main thread. The client token ties the session to this process: the host closes it when the
 * plugin dies.
 * zh-CN: 插件一侧的宿主会话 (Explorer Action 协议 v25): 对 `listRenameParent` 与 `listChildren` 分页, 以单次 Binder 事务能容纳的请求
 * 发送 `renameEntries`, 并读取改名历史. 每个调用都会阻塞, 调用方不得在主线程上调用. 客户端令牌把会话绑定到本进程: 插件进程退出时宿主关闭会话.
 */
internal class HostSessionClient(private val session: IExplorerActionHostSession) : RenameSource {

    private val clientToken = Binder()

    @Volatile
    private var closed = false

    fun attach() {
        session.attachClient(clientToken)
    }

    override fun listParent(): ParentListing {
        val names = ArrayList<String>()
        var caseInsensitive = false
        var restricted = false
        page { offset ->
            val page = session.listRenameParent(offset, ExplorerActionProtocol.MAX_SESSION_PAGE_SIZE)
            caseInsensitive = page.getBoolean(ExplorerActionHostSessionKeys.RENAME_CASE_INSENSITIVE, false)
            restricted = page.getBoolean(ExplorerActionHostSessionKeys.RENAME_RESTRICTED_CHARACTERS, false)
            items(page).mapNotNullTo(names) { it.getString(ExplorerActionHostSessionKeys.DISPLAY_NAME) }
            page
        }
        return ParentListing(names, caseInsensitive, restricted)
    }

    override fun listChildren(targetId: String, relativePath: String): List<ListedChild> {
        val children = ArrayList<ListedChild>()
        page { offset ->
            val page = session.listChildren(targetId, relativePath, offset, ExplorerActionProtocol.MAX_SESSION_PAGE_SIZE)
            items(page).forEach { item ->
                val path = item.getString(ExplorerActionHostSessionKeys.RELATIVE_PATH) ?: return@forEach
                val name = item.getString(ExplorerActionHostSessionKeys.DISPLAY_NAME) ?: path.substringAfterLast('/')
                children += ListedChild(
                    relativePath = path,
                    name = name,
                    directory = item.getInt(ExplorerActionHostSessionKeys.KIND, 0) == ExplorerActionValues.TARGET_DIRECTORY,
                    symbolicLink = item.getBoolean(ExplorerActionHostSessionKeys.SYMBOLIC_LINK, true),
                    size = item.getLong(ExplorerActionHostSessionKeys.SIZE, -1L),
                    lastModified = item.getLong(ExplorerActionHostSessionKeys.LAST_MODIFIED, 0L),
                )
            }
            page
        }
        return children
    }

    /**
     * Renames [entries] as one or more batches (deeper folders first, see [RenameRequestSplitter]); the results come
     * in the order the batches ran.
     * zh-CN: 把 [entries] 作为一个或多个批次改名 (深层文件夹先, 见 [RenameRequestSplitter]); 结果按批次执行顺序返回.
     */
    fun rename(entries: List<RenameEntry>): List<Pair<List<RenameEntry>, RenameBatchResult>> =
        RenameRequestSplitter.split(entries, MAX_REQUEST_BYTES) { sizeOf(request(it)) }.map { part ->
            part to batch(session.renameEntries(request(part)))
        }

    /** The newest batch this plugin made on this folder, or null. zh-CN: 本插件在该文件夹上最近的批次, 或 null. */
    fun latestBatch(): RenameBatchResult? {
        val batches = bundles(session.listRenameBatches(), ExplorerActionHostSessionKeys.RENAME_BATCHES)
        return batches.firstOrNull()?.let(::batch)
    }

    fun undo(batchId: String): RenameBatchResult = batch(session.undoRenameBatch(batchId))

    /**
     * Photo metadata of entries this session listed (Explorer Action v26, roadmap D34), [ExplorerActionProtocol.MAX_METADATA_ENTRIES_PER_REQUEST]
     * per call; null for an entry the host read nothing from. Values keep their Bundle types (String, Int, Double).
     * zh-CN: 本会话列出过的条目的照片元数据 (Explorer Action v26, 路线图 D34), 每次调用至多
     * [ExplorerActionProtocol.MAX_METADATA_ENTRIES_PER_REQUEST] 个; 宿主未读到内容的条目为 null. 值保持 Bundle 中的类型.
     */
    fun readMetadata(entries: List<Pair<String, String>>): List<Map<String, Any>?> =
        entries.chunked(ExplorerActionProtocol.MAX_METADATA_ENTRIES_PER_REQUEST).flatMap { chunk ->
            val answer = session.readMetadata(Bundle().apply {
                putParcelableArrayList(ExplorerActionHostSessionKeys.METADATA_ENTRIES, ArrayList(chunk.map { (targetId, relativePath) ->
                    Bundle().apply {
                        putString(ExplorerActionHostSessionKeys.RENAME_TARGET_ID, targetId)
                        putString(ExplorerActionHostSessionKeys.RELATIVE_PATH, relativePath)
                    }
                }))
            })
            val items = bundles(answer, ExplorerActionHostSessionKeys.METADATA_ITEMS)
            check(items.size == chunk.size) { "the host answered ${items.size} metadata items for ${chunk.size} entries" }
            items.map { item -> item.getBundle(ExplorerActionHostSessionKeys.METADATA)?.let(::values) }
        }

    @Suppress("DEPRECATION")
    private fun values(bundle: Bundle): Map<String, Any> = bundle.keySet().mapNotNull { key -> bundle.get(key)?.let { key to it } }.toMap()

    fun close() {
        if (closed) return
        closed = true
        runCatching { session.close() }
    }

    private fun request(entries: List<RenameEntry>) = Bundle().apply {
        putParcelableArrayList(ExplorerActionHostSessionKeys.RENAME_ENTRIES, ArrayList(entries.map { entry ->
            Bundle().apply {
                putString(ExplorerActionHostSessionKeys.RENAME_TARGET_ID, entry.targetId)
                putString(ExplorerActionHostSessionKeys.RELATIVE_PATH, entry.relativePath)
                putString(ExplorerActionHostSessionKeys.RENAME_NEW_NAME, entry.newName)
                if (entry.overwrite) putBoolean(ExplorerActionHostSessionKeys.RENAME_OVERWRITE, true)
            }
        }))
    }

    private fun batch(bundle: Bundle) = RenameBatchResult(
        batchId = bundle.getString(ExplorerActionHostSessionKeys.RENAME_BATCH_ID).orEmpty(),
        state = bundle.getInt(ExplorerActionHostSessionKeys.RENAME_BATCH_STATE, ExplorerActionHostSessionValues.RENAME_BATCH_STATE_UNKNOWN),
        createdAt = bundle.getLong(ExplorerActionHostSessionKeys.RENAME_BATCH_CREATED_AT, 0L),
        renamed = bundle.getInt(ExplorerActionHostSessionKeys.RENAME_RENAMED_COUNT, 0),
        notRenamed = bundle.getInt(ExplorerActionHostSessionKeys.RENAME_NOT_RENAMED_COUNT, 0),
        restored = bundle.getInt(ExplorerActionHostSessionKeys.RENAME_RESTORED_COUNT, 0),
        items = bundles(bundle, ExplorerActionHostSessionKeys.RENAME_ENTRIES).map { item ->
            RenameItemResult(
                state = item.getInt(ExplorerActionHostSessionKeys.RENAME_ITEM_STATE, ExplorerActionHostSessionValues.RENAME_ITEM_STATE_UNKNOWN),
                newRelativePath = item.getString(ExplorerActionHostSessionKeys.RENAME_NEW_RELATIVE_PATH).orEmpty(),
            )
        },
    )

    /** Calls [read] with growing offsets until the host reports the listing complete. */
    private inline fun page(read: (Int) -> Bundle) {
        var offset = 0
        while (true) {
            val page = read(offset)
            val count = items(page).size
            val next = page.getInt(ExplorerActionHostSessionKeys.NEXT_OFFSET, -1)
            if (page.getBoolean(ExplorerActionHostSessionKeys.COMPLETE, false)) return
            check(count > 0 && next == offset + count) { "the host returned an inconsistent page" }
            offset = next
        }
    }

    private fun items(page: Bundle): List<Bundle> = bundles(page, ExplorerActionHostSessionKeys.ITEMS)

    private fun bundles(bundle: Bundle, key: String): List<Bundle> =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            bundle.getParcelableArrayList(key, Bundle::class.java)
        } else {
            @Suppress("DEPRECATION")
            bundle.getParcelableArrayList(key)
        }.orEmpty()

    private fun sizeOf(bundle: Bundle): Int {
        val parcel = Parcel.obtain()
        return try {
            parcel.writeBundle(bundle)
            parcel.dataSize()
        } finally {
            parcel.recycle()
        }
    }

    companion object {
        /** Half of the Binder transaction buffer, leaving room for other calls. zh-CN: Binder 事务缓冲区的一半. */
        const val MAX_REQUEST_BYTES = 512 * 1024
    }
}
