package io.github.supermonster003.autojs6.plugin.batch.rename.ui

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import io.github.supermonster003.autojs6.plugin.batch.rename.engine.RenameEngine
import io.github.supermonster003.autojs6.plugin.batch.rename.engine.RenameOptions
import io.github.supermonster003.autojs6.plugin.batch.rename.explorer.CandidateSet
import io.github.supermonster003.autojs6.plugin.batch.rename.explorer.ExplorerRequest
import io.github.supermonster003.autojs6.plugin.batch.rename.explorer.HostSessionClient
import io.github.supermonster003.autojs6.plugin.batch.rename.explorer.Preview
import io.github.supermonster003.autojs6.plugin.batch.rename.explorer.PreviewPlanner
import io.github.supermonster003.autojs6.plugin.batch.rename.explorer.RenameCandidateEnumerator
import io.github.supermonster003.autojs6.plugin.batch.rename.explorer.RenameEntry
import io.github.supermonster003.autojs6.plugin.batch.rename.explorer.RenameRequestSplitter
import io.github.supermonster003.autojs6.plugin.batch.rename.explorer.RenameScope
import io.github.supermonster003.autojs6.plugin.batch.rename.explorer.RequestTarget
import io.github.supermonster003.autojs6.plugin.batch.rename.settings.OptionsCodec
import io.github.supermonster003.autojs6.plugin.batch.rename.engine.PlanException
import io.github.supermonster003.autojs6.plugin.batch.rename.presets.PresetConflict
import io.github.supermonster003.autojs6.plugin.batch.rename.presets.PresetDocument
import io.github.supermonster003.autojs6.plugin.batch.rename.presets.PresetImport
import io.github.supermonster003.autojs6.plugin.batch.rename.presets.PresetStore
import io.github.supermonster003.autojs6.plugin.batch.rename.presets.RejectionKind
import io.github.supermonster003.autojs6.plugin.batch.rename.settings.PreferencesStore
import io.github.supermonster003.autojs6.plugin.batch.rename.settings.SharedPreferencesStore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.autojs.plugin.batchrename.api.BatchRenameErrorCodes
import org.autojs.plugin.batchrename.api.BatchRenameLimits
import org.autojs.plugin.explorer.api.ExplorerActionHostSessionValues

/** Something the screen is busy with. zh-CN: 界面正在进行的操作. */
internal enum class Busy { LISTING, RENAMING, UNDOING }

/** A one-off message for the snackbar. zh-CN: 一次性的 Snackbar 消息. */
internal sealed interface ScreenMessage {
    data class Renamed(val renamed: Int, val notRenamed: Int) : ScreenMessage
    data class Restored(val restored: Int, val notRestored: Int) : ScreenMessage
    data object NothingToUndo : ScreenMessage
    data object TooManyInOneFolder : ScreenMessage
    data class Failed(val detail: String) : ScreenMessage
    data class PresetSaved(val name: String) : ScreenMessage
    data class PresetLoaded(val name: String) : ScreenMessage
    data object PresetsExported : ScreenMessage
    data object PresetsCopied : ScreenMessage
    data class PresetsImported(val imported: Int, val skipped: Int) : ScreenMessage
    data class PresetFailed(val failure: PresetFailure) : ScreenMessage
}

/** Why presets could not be imported or exported as a whole. zh-CN: 预设整体无法导入或导出的原因. */
internal enum class PresetFailure { CLIPBOARD_EMPTY, NOT_PRESETS, TOO_LARGE, READ_FAILED, WRITE_FAILED }

/** A decoded document waiting for the user's answer about names that are taken. zh-CN: 等待用户决定同名处理的已解码文档. */
internal data class PendingImport(val decoded: PresetDocument.Decoded, val conflicts: List<String>)

/** An entry the host did not rename, for the failure list. zh-CN: 宿主未改名的条目, 用于失败列表. */
internal data class FailedEntry(val path: String, val newName: String, val stale: Boolean)

/** Why the screen cannot continue. zh-CN: 界面无法继续的原因. */
internal enum class Fatal { SESSION_ENDED }

/** A request target renamed by a batch: its position in the batch and its name before. zh-CN: 被批次改名的请求目标. */
internal data class RenamedTarget(val index: Int, val targetId: String, val oldName: String)

/** Batches the "Undo" actions revert, newest last. zh-CN: "撤销" 要回退的批次, 最新在后. */
internal data class UndoTarget(
    val batchIds: List<String>,
    /** Request targets each batch renamed; null when the batches come from an earlier screen. zh-CN: 各批次改名的请求目标. */
    val renamedTargets: Map<String, List<RenamedTarget>>?,
)

internal data class ScreenState(
    val scope: RenameScope = RenameScope.TARGETS,
    val parentDisplayPath: String = "",
    val targetCount: Int = 0,
    val options: RenameOptions = PreferencesStore.DEFAULT_OPTIONS,
    val excludedKeys: Set<String> = emptySet(),
    val candidates: CandidateSet? = null,
    val preview: Preview = Preview.EMPTY,
    /**
     * The options or exclusions changed since [preview] was computed; Rename waits for the new preview so it never
     * applies names the user no longer sees.
     * zh-CN: 计算 [preview] 之后选项或排除发生了变化; "重命名" 等待新预览, 以免执行用户已看不到的名称.
     */
    val planPending: Boolean = true,
    /** Why the options are refused before planning (a bad pattern of an option). */
    val optionsError: String? = null,
    val onlyRenamed: Boolean = false,
    val busy: Busy? = Busy.LISTING,
    val undo: UndoTarget? = null,
    val message: ScreenMessage? = null,
    val failures: List<FailedEntry> = emptyList(),
    val fatal: Fatal? = null,
    val recentSearches: List<String> = emptyList(),
    val recentReplacements: List<String> = emptyList(),
    val rememberOptions: Boolean = true,
    /** Something was renamed or restored through this screen. zh-CN: 本界面改过名或恢复过. */
    val changedFiles: Boolean = false,
    /** Names of the presets in the store's order (roadmap D36). zh-CN: 按存储顺序排列的预设名称 (路线图 D36). */
    val presetNames: List<String> = emptyList(),
    val pendingImport: PendingImport? = null,
    /** An import that refused entries; shown until dismissed. zh-CN: 有条目被拒的导入结果, 关闭前一直显示. */
    val importResult: PresetImport? = null,
) {
    /** Rename is possible right now. zh-CN: 当前可以执行重命名. */
    val canApply: Boolean get() = busy == null && !planPending && preview.applicable
}

/**
 * State of the rename screen (roadmap P4.2): enumerates the request through the host session, plans the preview with
 * the plugin's engine (150 ms after the last change, off the main thread), sends the renames and undoes them. The host
 * session lives as long as the view model and is closed with it.
 *
 * zh-CN: 重命名界面的状态 (路线图 P4.2): 经宿主会话枚举请求, 用插件引擎计算预览 (最后一次修改 150 ms 后, 在主线程之外), 发送改名并撤销.
 * 宿主会话与 ViewModel 同生命周期, 随之关闭.
 */
internal class BatchRenameViewModel(application: Application) : AndroidViewModel(application) {

    private val preferences = PreferencesStore(SharedPreferencesStore(application))
    private val presets = PresetStore(SharedPreferencesStore(application))
    private val engine = RenameEngine()
    private val now = System.currentTimeMillis()
    private val planner = PreviewPlanner(engine, now)

    private val _state = MutableStateFlow(ScreenState())
    val state: StateFlow<ScreenState> = _state.asStateFlow()

    private var request: ExplorerRequest? = null
    private var client: HostSessionClient? = null
    private var targets: List<RequestTarget> = emptyList()
    private var enumeratedSubfolders: Boolean? = null
    private var planJob: Job? = null

    val attached: Boolean get() = request != null

    fun attach(request: ExplorerRequest) {
        if (this.request != null) return
        this.request = request
        targets = request.targets
        val client = HostSessionClient(request.session).also { client = it }
        _state.update {
            it.copy(
                scope = request.action.scope,
                parentDisplayPath = request.parentDisplayPath,
                targetCount = request.targets.size,
                options = preferences.options(),
                recentSearches = preferences.recentSearches(),
                recentReplacements = preferences.recentReplacements(),
                rememberOptions = preferences.rememberOptions,
                presetNames = presets.list().map { preset -> preset.name },
            )
        }
        viewModelScope.launch {
            // The list holds the newest batch or null: an empty history is not a failure.
            // zh-CN: 列表中是最近的批次或 null: 历史为空并不是失败.
            val history = runSession { client.attach(); listOf(client.latestBatch()) } ?: return@launch
            val latest = history.single()
            if (latest != null && latest.known && latest.renamed > latest.restored && latest.state != ExplorerActionHostSessionValues.RENAME_BATCH_STATE_RESTORED) {
                _state.update { it.copy(undo = UndoTarget(listOf(latest.batchId), null)) }
            }
            refresh()
        }
    }

    fun setSearch(text: String) = setOptions { it.copy(search = text) }

    fun setReplace(text: String) = setOptions { it.copy(replace = text) }

    fun setOptions(transform: (RenameOptions) -> RenameOptions) {
        val before = _state.value.options
        val after = transform(before)
        if (after == before) return
        _state.update { it.copy(options = after, planPending = true) }
        if (after.includeSubfolders != before.includeSubfolders) refresh() else schedulePlan()
    }

    fun toggleExcluded(key: String) {
        _state.update { it.copy(excludedKeys = if (key in it.excludedKeys) it.excludedKeys - key else it.excludedKeys + key, planPending = true) }
        schedulePlan()
    }

    fun setOnlyRenamed(value: Boolean) = _state.update { it.copy(onlyRenamed = value) }

    fun setRememberOptions(value: Boolean) {
        preferences.rememberOptions = value
        if (value) preferences.saveOptions(_state.value.options)
        _state.update { it.copy(rememberOptions = value) }
    }

    fun clearHistory() {
        preferences.clearHistory()
        _state.update { it.copy(recentSearches = emptyList(), recentReplacements = emptyList()) }
    }

    fun reloadPresets() = _state.update { it.copy(presetNames = presets.list().map { preset -> preset.name }) }

    fun presetExists(name: String): Boolean = presets.find(name.trim()) != null

    fun loadPreset(name: String) {
        val preset = presets.find(name) ?: return reloadPresets()
        setOptions { preset.options }
        _state.update { it.copy(message = ScreenMessage.PresetLoaded(name)) }
    }

    /** Saves the current options; null when saved, otherwise why not. zh-CN: 保存当前选项; 成功时为 null. */
    fun savePreset(name: String): RejectionKind? {
        val trimmed = name.trim()
        val problem = presets.save(trimmed, _state.value.options)
        if (problem == null) {
            reloadPresets()
            _state.update { it.copy(message = ScreenMessage.PresetSaved(trimmed)) }
        }
        return problem
    }

    fun renamePreset(from: String, to: String): RejectionKind? = presets.rename(from, to.trim()).also { if (it == null) reloadPresets() }

    fun deletePreset(name: String) {
        presets.delete(listOf(name))
        reloadPresets()
    }

    fun exportPresets(names: Collection<String>): String = presets.export(names)

    fun presetsCopied() = _state.update { it.copy(message = ScreenMessage.PresetsCopied) }

    /** Writes the document of [names] to a file the user picked. zh-CN: 把 [names] 的文档写入用户选择的文件. */
    fun writePresets(uri: Uri, names: Collection<String>) {
        val document = presets.export(names)
        viewModelScope.launch {
            val written = withContext(Dispatchers.IO) {
                runCatching {
                    getApplication<Application>().contentResolver.openOutputStream(uri, "wt")!!.use { it.write(document.toByteArray(Charsets.UTF_8)) }
                }.isSuccess
            }
            _state.update { it.copy(message = if (written) ScreenMessage.PresetsExported else ScreenMessage.PresetFailed(PresetFailure.WRITE_FAILED)) }
        }
    }

    /** Reads a document from a file the user picked, then imports it. zh-CN: 读取用户选择的文件中的文档并导入. */
    fun readPresets(uri: Uri) {
        viewModelScope.launch {
            val text = withContext(Dispatchers.IO) {
                runCatching {
                    getApplication<Application>().contentResolver.openInputStream(uri)!!.use { stream ->
                        // One character more than the limit is enough to report the document as too large.
                        // zh-CN: 比上限多读一个字符即可判定文档过大.
                        val buffer = CharArray(BatchRenameLimits.MAX_PRESET_DOCUMENT_CHARS + 1)
                        val reader = stream.reader(Charsets.UTF_8)
                        var length = 0
                        while (length < buffer.size) {
                            val read = reader.read(buffer, length, buffer.size - length)
                            if (read < 0) break
                            length += read
                        }
                        String(buffer, 0, length)
                    }
                }.getOrNull()
            }
            if (text == null) _state.update { it.copy(message = ScreenMessage.PresetFailed(PresetFailure.READ_FAILED)) } else prepareImport(text)
        }
    }

    /**
     * Decodes a document from a file or the clipboard; imports it at once when no name is taken, otherwise waits for
     * [finishImport].
     * zh-CN: 解码来自文件或剪贴板的文档; 没有名称被占用时立即导入, 否则等待 [finishImport].
     */
    fun prepareImport(text: String?) {
        if (text.isNullOrBlank()) {
            _state.update { it.copy(message = ScreenMessage.PresetFailed(PresetFailure.CLIPBOARD_EMPTY)) }
            return
        }
        val decoded = try {
            PresetDocument.decode(text)
        } catch (error: PlanException) {
            val failure = if (error.code == BatchRenameErrorCodes.LIMIT_EXCEEDED) PresetFailure.TOO_LARGE else PresetFailure.NOT_PRESETS
            _state.update { it.copy(message = ScreenMessage.PresetFailed(failure)) }
            return
        }
        val taken = presets.list().map { it.name }.toSet()
        val conflicts = decoded.entries.map { it.preset.name }.filter { it in taken }
        if (conflicts.isEmpty()) {
            completeImport(decoded, PresetConflict.SKIP)
        } else {
            _state.update { it.copy(pendingImport = PendingImport(decoded, conflicts)) }
        }
    }

    /** The user's answer for taken names; null cancels the import. zh-CN: 用户对同名的处理; null 取消导入. */
    fun finishImport(conflict: PresetConflict?) {
        val pending = _state.value.pendingImport ?: return
        _state.update { it.copy(pendingImport = null) }
        if (conflict != null) completeImport(pending.decoded, conflict)
    }

    fun importResultShown() = _state.update { it.copy(importResult = null) }

    private fun completeImport(decoded: PresetDocument.Decoded, conflict: PresetConflict) {
        val result = presets.import(decoded, conflict)
        reloadPresets()
        _state.update {
            if (result.rejected.isEmpty()) {
                it.copy(message = ScreenMessage.PresetsImported(result.imported.size, result.skipped.size))
            } else {
                it.copy(importResult = result)
            }
        }
    }

    fun restoreDefaults() {
        preferences.resetOptions()
        setOptions { PreferencesStore.DEFAULT_OPTIONS }
    }

    fun messageShown() = _state.update { it.copy(message = null) }

    fun failuresShown() = _state.update { it.copy(failures = emptyList()) }

    fun apply() {
        val state = _state.value
        val client = client ?: return
        if (!state.canApply) return
        val entries = state.preview.entries()
        _state.update { it.copy(busy = Busy.RENAMING) }
        viewModelScope.launch {
            val results = try {
                runSession { client.rename(entries) }
            } catch (_: RenameRequestSplitter.RequestTooLargeException) {
                _state.update { it.copy(busy = null, message = ScreenMessage.TooManyInOneFolder) }
                return@launch
            } ?: return@launch
            val renamedTargets = HashMap<String, List<RenamedTarget>>()
            val failures = ArrayList<FailedEntry>()
            results.forEach { (sent, batch) ->
                val touched = ArrayList<RenamedTarget>()
                sent.forEachIndexed { index, entry ->
                    val item = batch.items.getOrNull(index)
                    when (item?.state) {
                        ExplorerActionHostSessionValues.RENAME_ITEM_STATE_RENAMED -> if (entry.relativePath.isEmpty()) {
                            renameTarget(entry.targetId, entry.newName)?.let { old -> touched += RenamedTarget(index, entry.targetId, old) }
                        }
                        else -> failures += FailedEntry(displayPath(entry), entry.newName, item?.state == ExplorerActionHostSessionValues.RENAME_ITEM_STATE_STALE)
                    }
                }
                renamedTargets[batch.batchId] = touched
            }
            val renamed = results.sumOf { it.second.renamed }
            val batches = results.map { it.second }.filter { it.known && it.renamed > 0 }.map { it.batchId }
            preferences.remember(state.options.search, state.options.replace)
            preferences.saveOptions(state.options)
            _state.update {
                it.copy(
                    busy = null,
                    excludedKeys = emptySet(),
                    undo = if (batches.isEmpty()) it.undo else UndoTarget(batches, renamedTargets),
                    message = ScreenMessage.Renamed(renamed, entries.size - renamed),
                    failures = failures,
                    recentSearches = preferences.recentSearches(),
                    recentReplacements = preferences.recentReplacements(),
                    changedFiles = it.changedFiles || renamed > 0,
                )
            }
            refresh(force = true)
        }
    }

    /**
     * Undoes [ScreenState.undo]. When a batch of an earlier screen gave request targets their old names back, the
     * screen no longer knows their names and calls [onTargetsUnknown] to close.
     * zh-CN: 撤销 [ScreenState.undo]. 早先界面的批次让请求目标恢复原名时, 界面不再知道其名称, 调用 [onTargetsUnknown] 关闭.
     */
    fun undo(onTargetsUnknown: () -> Unit) {
        val target = _state.value.undo ?: return
        val client = client ?: return
        if (_state.value.busy != null) return
        _state.update { it.copy(busy = Busy.UNDOING) }
        viewModelScope.launch {
            val results = runSession { target.batchIds.reversed().map(client::undo) } ?: return@launch
            if (results.all { !it.known }) {
                _state.update { it.copy(busy = null, undo = null, message = ScreenMessage.NothingToUndo) }
                return@launch
            }
            val restored = results.sumOf { it.restored }
            val touchedTargets = results.any { batch -> batch.items.any { it.newRelativePath.isEmpty() } }
            // A target is back at its old name only when its own entry was restored.
            // zh-CN: 只有目标自身的条目被恢复时, 目标才回到原名.
            results.forEach { batch ->
                target.renamedTargets?.get(batch.batchId)?.forEach { renamed ->
                    if (batch.items.getOrNull(renamed.index)?.state == ExplorerActionHostSessionValues.RENAME_ITEM_STATE_RESTORED) {
                        renameTarget(renamed.targetId, renamed.oldName)
                    }
                }
            }
            _state.update {
                it.copy(
                    busy = null,
                    undo = null,
                    message = ScreenMessage.Restored(restored, results.sumOf { batch -> batch.renamed } - restored),
                    changedFiles = it.changedFiles || restored > 0,
                )
            }
            if (target.renamedTargets == null && touchedTargets && _state.value.scope == RenameScope.TARGETS) {
                onTargetsUnknown()
            } else {
                refresh(force = true)
            }
        }
    }

    override fun onCleared() {
        client?.close()
    }

    private fun refresh(force: Boolean = false) {
        val client = client ?: return
        val subfolders = _state.value.options.includeSubfolders
        if (!force && enumeratedSubfolders == subfolders && _state.value.candidates != null) {
            schedulePlan()
            return
        }
        planJob?.cancel()
        _state.update { it.copy(busy = it.busy ?: Busy.LISTING, planPending = true) }
        viewModelScope.launch {
            val scope = _state.value.scope
            val set = runSession { RenameCandidateEnumerator(client).enumerate(scope, targets, subfolders) } ?: return@launch
            enumeratedSubfolders = subfolders
            _state.update { it.copy(candidates = set, busy = if (it.busy == Busy.LISTING) null else it.busy) }
            schedulePlan(immediately = true)
        }
    }

    private fun schedulePlan(immediately: Boolean = false) {
        planJob?.cancel()
        planJob = viewModelScope.launch {
            if (!immediately) delay(PLAN_DELAY_MILLIS)
            val state = _state.value
            val set = state.candidates ?: return@launch
            val refused = OptionsCodec.check(state.options)
            val preview = if (refused != null) {
                Preview.EMPTY.copy(error = refused)
            } else {
                withContext(Dispatchers.Default) { planner.plan(set, state.options, state.excludedKeys) }
            }
            _state.update { it.copy(preview = preview, optionsError = (refused ?: preview.error)?.message, planPending = it.options != state.options || it.excludedKeys != state.excludedKeys) }
            if (preferences.rememberOptions) preferences.saveOptions(state.options)
        }
    }

    /** Runs a session call on the IO dispatcher; a dead host session ends the screen. */
    private suspend fun <T> runSession(block: () -> T): T? = try {
        withContext(Dispatchers.IO) { block() }
    } catch (error: RenameRequestSplitter.RequestTooLargeException) {
        throw error
    } catch (error: android.os.DeadObjectException) {
        _state.update { it.copy(busy = null, fatal = Fatal.SESSION_ENDED) }
        null
    } catch (error: RuntimeException) {
        _state.update { it.copy(busy = null, message = ScreenMessage.Failed(error.message ?: error.toString())) }
        null
    } catch (error: android.os.RemoteException) {
        _state.update { it.copy(busy = null, fatal = Fatal.SESSION_ENDED) }
        null
    }

    /** Gives a request target its new name; returns the old one. */
    private fun renameTarget(id: String, name: String): String? {
        val index = targets.indexOfFirst { it.id == id }.takeIf { it >= 0 } ?: return null
        val old = targets[index].name
        targets = targets.toMutableList().also { it[index] = it[index].copy(name = name) }
        return old
    }

    private fun displayPath(entry: RenameEntry): String {
        val target = targets.firstOrNull { it.id == entry.targetId }?.name.orEmpty()
        return when {
            entry.relativePath.isEmpty() -> target
            _state.value.scope == RenameScope.INSIDE_DIRECTORY -> entry.relativePath
            else -> "$target/${entry.relativePath}"
        }
    }

    private companion object {
        const val PLAN_DELAY_MILLIS = 150L
    }
}
