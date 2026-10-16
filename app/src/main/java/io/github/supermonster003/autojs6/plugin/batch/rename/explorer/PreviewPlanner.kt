package io.github.supermonster003.autojs6.plugin.batch.rename.explorer

import io.github.supermonster003.autojs6.plugin.batch.rename.engine.ItemKind
import io.github.supermonster003.autojs6.plugin.batch.rename.engine.PlanException
import io.github.supermonster003.autojs6.plugin.batch.rename.engine.PlanItemStatus
import io.github.supermonster003.autojs6.plugin.batch.rename.engine.PlanReason
import io.github.supermonster003.autojs6.plugin.batch.rename.engine.RenameEngine
import io.github.supermonster003.autojs6.plugin.batch.rename.engine.RenameItem
import io.github.supermonster003.autojs6.plugin.batch.rename.engine.RenameOptions
import io.github.supermonster003.autojs6.plugin.batch.rename.engine.RenameParent
import io.github.supermonster003.autojs6.plugin.batch.rename.engine.RenameRequest
import java.time.ZoneId
import java.util.Locale

/** One row of the preview. zh-CN: 预览中的一行. */
internal data class PreviewRow(
    val candidate: RenameCandidate,
    val newName: String,
    val status: PlanItemStatus,
    val reason: PlanReason?,
    val overwrites: Boolean,
    /** The user unchecked the row. zh-CN: 用户取消勾选了该行. */
    val excluded: Boolean,
    /** A metadata variable had no value for this row (roadmap D34). zh-CN: 该行缺少某个元数据变量的值 (路线图 D34). */
    val metadataMissing: Boolean = false,
) {
    val renames: Boolean get() = status == PlanItemStatus.RENAME
}

/** A computed preview, or why the options cannot be planned. zh-CN: 计算好的预览, 或选项无法计算的原因. */
internal data class Preview(
    val rows: List<PreviewRow>,
    val rejected: Boolean,
    /** The engine refused the options as a whole (a regular expression that does not compile, a limit). */
    val error: PlanException? = null,
) {
    val renameCount: Int get() = rows.count { it.renames }
    val problemCount: Int get() = rows.count { it.status == PlanItemStatus.CONFLICT || it.status == PlanItemStatus.INVALID }
    val excludedCount: Int get() = rows.count { it.status == PlanItemStatus.EXCLUDED }

    /** Apply is possible: nothing refuses the plan and something would be renamed. zh-CN: 可以执行. */
    val applicable: Boolean get() = error == null && !rejected && renameCount > 0

    /** The `renameEntries` entries of the rows that rename, in preview order. zh-CN: 改名行对应的 `renameEntries` 条目. */
    fun entries(): List<RenameEntry> = rows.filter { it.renames }.map { row ->
        RenameEntry(row.candidate.targetId, row.candidate.relativePath, row.newName, row.overwrites, row.candidate.depth, row.candidate.parentId)
    }

    companion object {
        val EMPTY = Preview(emptyList(), rejected = false)
    }
}

/**
 * Plans the screen's candidates with the plugin's own engine (roadmap D13: the same engine as the script path).
 * `now` is fixed when the screen opens, so date variables do not change while the user types.
 * zh-CN: 用插件自身的引擎为界面的候选项计算方案 (路线图 D13: 与脚本路径同一引擎). `now` 在界面打开时固定, 输入期间日期变量不会变化.
 */
internal class PreviewPlanner(
    private val engine: RenameEngine,
    private val now: Long,
    private val locale: Locale = Locale.getDefault(),
    private val timeZone: ZoneId = ZoneId.systemDefault(),
) {

    /** @param metadata photo metadata by candidate key, for the metadata variables. */
    fun plan(set: CandidateSet, options: RenameOptions, excludedKeys: Set<String>, metadata: Map<String, Map<String, Any>?> = emptyMap()): Preview {
        if (set.candidates.isEmpty()) return Preview.EMPTY
        val request = RenameRequest(
            options = options,
            parents = set.parents.map { RenameParent(it.id, it.existingNames, it.caseInsensitive, it.restrictedCharacters) },
            items = set.candidates.mapIndexed { index, candidate ->
                RenameItem(
                    index = index,
                    parentId = candidate.parentId,
                    name = candidate.name,
                    kind = if (candidate.directory) ItemKind.DIRECTORY else ItemKind.FILE,
                    depth = candidate.depth,
                    lastModified = candidate.lastModified,
                    length = candidate.size,
                    excluded = candidate.key in excludedKeys,
                    metadata = metadata[candidate.key],
                )
            },
            locale = locale,
            timeZone = timeZone,
            now = now,
        )
        val plan = try {
            engine.plan(request)
        } catch (error: PlanException) {
            return Preview(set.candidates.map { PreviewRow(it, it.name, PlanItemStatus.UNCHANGED, null, false, it.key in excludedKeys) }, rejected = true, error = error)
        }
        return Preview(
            rows = set.candidates.mapIndexed { index, candidate ->
                val item = plan.items[index]
                PreviewRow(candidate, item.newName, item.status, item.reason, item.overwrites, candidate.key in excludedKeys, item.metadataMissing)
            },
            rejected = plan.rejected,
        )
    }
}
