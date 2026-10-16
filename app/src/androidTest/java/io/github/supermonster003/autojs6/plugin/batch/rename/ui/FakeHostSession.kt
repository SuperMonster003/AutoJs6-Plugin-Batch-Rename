package io.github.supermonster003.autojs6.plugin.batch.rename.ui

import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.os.ParcelFileDescriptor
import org.autojs.plugin.explorer.api.ExplorerActionHostSessionKeys
import org.autojs.plugin.explorer.api.ExplorerActionHostSessionValues
import org.autojs.plugin.explorer.api.ExplorerActionValues
import org.autojs.plugin.explorer.api.IExplorerActionHostSession

/**
 * An in-memory host session for the rename screen tests: one parent folder with the names in [parent] and, for
 * directory targets, the children in [tree] (`relativePath -> names`, folders end with `/`). Renames succeed and are
 * recorded; undo restores the last batch. Every other transaction is outside protocol v25 rename and throws.
 * zh-CN: 供重命名界面测试使用的内存宿主会话. 改名成功并被记录; 撤销恢复最近的批次; 其余事务不属于 v25 改名, 一律抛出.
 */
internal class FakeHostSession(
    private val parent: List<String>,
    private val tree: Map<String, List<String>> = emptyMap(),
    /** v26 photo metadata by entry name; entries without one answer none. zh-CN: 按条目名称给出的 v26 照片元数据. */
    private val metadata: Map<String, Bundle> = emptyMap(),
) : IExplorerActionHostSession.Stub() {

    /** Names `readMetadata` was asked about, call by call. zh-CN: 每次 `readMetadata` 调用所询问的名称. */
    val metadataRequests = mutableListOf<List<String>>()

    val renameRequests = mutableListOf<List<Triple<String, String, String>>>()
    val undone = mutableListOf<String>()

    @Volatile
    var attached = false

    @Volatile
    var closed = false

    private var batch = 0

    override fun listRenameParent(offset: Int, limit: Int): Bundle = page(parent.map { name ->
        Bundle().apply {
            putString(ExplorerActionHostSessionKeys.DISPLAY_NAME, name.removeSuffix("/"))
            putInt(ExplorerActionHostSessionKeys.KIND, kind(name))
        }
    }, offset, limit)

    override fun listChildren(targetId: String, relativePath: String, offset: Int, limit: Int): Bundle =
        page(tree[relativePath].orEmpty().map { raw ->
            val name = raw.removeSuffix("/")
            Bundle().apply {
                putString(ExplorerActionHostSessionKeys.RELATIVE_PATH, if (relativePath.isEmpty()) name else "$relativePath/$name")
                putString(ExplorerActionHostSessionKeys.DISPLAY_NAME, name)
                putInt(ExplorerActionHostSessionKeys.KIND, kind(raw))
                putLong(ExplorerActionHostSessionKeys.SIZE, name.length.toLong())
                putLong(ExplorerActionHostSessionKeys.LAST_MODIFIED, 1_700_000_000_000L)
                putBoolean(ExplorerActionHostSessionKeys.SYMBOLIC_LINK, false)
            }
        }, offset, limit)

    override fun renameEntries(request: Bundle): Bundle {
        val entries = bundles(request, ExplorerActionHostSessionKeys.RENAME_ENTRIES).map {
            Triple(
                it.getString(ExplorerActionHostSessionKeys.RENAME_TARGET_ID)!!,
                it.getString(ExplorerActionHostSessionKeys.RELATIVE_PATH)!!,
                it.getString(ExplorerActionHostSessionKeys.RENAME_NEW_NAME)!!,
            )
        }
        synchronized(this) { renameRequests += entries }
        batch++
        return batchBundle("b$batch", ExplorerActionHostSessionValues.RENAME_BATCH_STATE_COMMITTED, entries.size, 0, ExplorerActionHostSessionValues.RENAME_ITEM_STATE_RENAMED)
    }

    override fun listRenameBatches(): Bundle = Bundle().apply {
        putParcelableArrayList(ExplorerActionHostSessionKeys.RENAME_BATCHES, arrayListOf())
    }

    override fun undoRenameBatch(renameBatchId: String): Bundle {
        synchronized(this) { undone += renameBatchId }
        val size = renameRequests.lastOrNull()?.size ?: 0
        return batchBundle(renameBatchId, ExplorerActionHostSessionValues.RENAME_BATCH_STATE_RESTORED, size, size, ExplorerActionHostSessionValues.RENAME_ITEM_STATE_RESTORED)
    }

    override fun queryRenameBatch(renameBatchId: String): Bundle = unsupported()

    override fun readMetadata(request: Bundle): Bundle {
        val names = bundles(request, ExplorerActionHostSessionKeys.METADATA_ENTRIES).map { entry ->
            val relativePath = entry.getString(ExplorerActionHostSessionKeys.RELATIVE_PATH).orEmpty()
            relativePath.ifEmpty { entry.getString(ExplorerActionHostSessionKeys.RENAME_TARGET_ID)!! }.substringAfterLast('/')
        }
        synchronized(this) { metadataRequests += names }
        return Bundle().apply {
            putParcelableArrayList(ExplorerActionHostSessionKeys.METADATA_ITEMS, ArrayList(names.map { name ->
                Bundle().apply { metadata[name]?.let { putBundle(ExplorerActionHostSessionKeys.METADATA, it) } }
            }))
        }
    }

    override fun attachClient(clientToken: IBinder) {
        attached = true
    }

    override fun close() {
        closed = true
    }

    private fun batchBundle(id: String, state: Int, renamed: Int, restored: Int, itemState: Int) = Bundle().apply {
        putString(ExplorerActionHostSessionKeys.RENAME_BATCH_ID, id)
        putInt(ExplorerActionHostSessionKeys.RENAME_BATCH_STATE, state)
        putInt(ExplorerActionHostSessionKeys.RENAME_RENAMED_COUNT, renamed)
        putInt(ExplorerActionHostSessionKeys.RENAME_NOT_RENAMED_COUNT, 0)
        putInt(ExplorerActionHostSessionKeys.RENAME_RESTORED_COUNT, restored)
        putParcelableArrayList(ExplorerActionHostSessionKeys.RENAME_ENTRIES, ArrayList((0 until renamed).map {
            Bundle().apply {
                putInt(ExplorerActionHostSessionKeys.RENAME_ITEM_STATE, itemState)
                putString(ExplorerActionHostSessionKeys.RENAME_NEW_RELATIVE_PATH, "x")
            }
        }))
    }

    private fun page(items: List<Bundle>, offset: Int, limit: Int) = Bundle().apply {
        val slice = items.drop(offset).take(limit)
        putParcelableArrayList(ExplorerActionHostSessionKeys.ITEMS, ArrayList(slice))
        putInt(ExplorerActionHostSessionKeys.NEXT_OFFSET, offset + slice.size)
        putBoolean(ExplorerActionHostSessionKeys.COMPLETE, offset + slice.size >= items.size)
    }

    private fun kind(raw: String) = if (raw.endsWith("/")) ExplorerActionValues.TARGET_DIRECTORY else ExplorerActionValues.TARGET_FILE

    private fun bundles(bundle: Bundle, key: String): List<Bundle> =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            bundle.getParcelableArrayList(key, Bundle::class.java)
        } else {
            @Suppress("DEPRECATION")
            bundle.getParcelableArrayList(key)
        }.orEmpty()

    private fun unsupported(): Nothing = throw UnsupportedOperationException("not part of the rename screen")

    override fun openFile(targetId: String, relativePath: String): ParcelFileDescriptor = unsupported()
    override fun prepareOutput(displayName: String, mimeType: String, conflictPolicy: Int): Bundle = unsupported()
    override fun openOutput(transactionId: String): ParcelFileDescriptor = unsupported()
    override fun commitOutput(transactionId: String): Bundle = unsupported()
    override fun abortOutput(transactionId: String) = unsupported()
    override fun openPendingOutput(transactionId: String): ParcelFileDescriptor = unsupported()
    override fun prepareTargetReplacement(targetId: String): Bundle = unsupported()
    override fun prepareOutputTree(displayName: String, conflictPolicy: Int): Bundle = unsupported()
    override fun createOutputDirectory(transactionId: String, relativePath: String) = unsupported()
    override fun openOutputFile(transactionId: String, relativePath: String): ParcelFileDescriptor = unsupported()
    override fun queryOutput(transactionId: String): Bundle = unsupported()
    override fun listOutputs(): Bundle = unsupported()
    override fun prepareOutputBatch(transactionIds: MutableList<String>): Bundle = unsupported()
    override fun commitOutputBatch(batchId: String): Bundle = unsupported()
    override fun abortOutputBatch(batchId: String) = unsupported()
    override fun queryOutputBatch(batchId: String): Bundle = unsupported()
    override fun listOutputBatches(): Bundle = unsupported()
    override fun moveTargetsToTrash(targetIds: MutableList<String>, outputTransactionIds: MutableList<String>): Bundle = unsupported()
    override fun queryTargetTrash(): Bundle = unsupported()
    override fun getPlaybackProgress(targetId: String, relativePath: String): Bundle = unsupported()
    override fun reportPlaybackProgress(targetId: String, relativePath: String, positionMillis: Long, durationMillis: Long, reportState: Int) = unsupported()
    override fun queryTargetReplacement(targetId: String): Bundle = unsupported()
    override fun undoTargetReplacement(targetId: String, replacementHistoryId: String): Bundle = unsupported()
    override fun queryTargetTrashBatch(targetTrashBatchId: String): Bundle = unsupported()
    override fun listTargetTrashBatches(): Bundle = unsupported()
    override fun undoTargetTrashBatch(targetTrashBatchId: String): Bundle = unsupported()
    override fun openRelatedFile(targetId: String, suffix: String): ParcelFileDescriptor = unsupported()
}
