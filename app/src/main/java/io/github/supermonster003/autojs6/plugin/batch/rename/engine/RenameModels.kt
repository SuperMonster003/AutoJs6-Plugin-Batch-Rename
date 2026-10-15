package io.github.supermonster003.autojs6.plugin.batch.rename.engine

import org.autojs.plugin.batchrename.api.BatchRenameErrorCodes
import org.autojs.plugin.batchrename.api.BatchRenameValues
import java.time.ZoneId
import java.util.Locale

/**
 * Plain models of one plan (contract v1, `docs/dev/batch-rename-plugin-protocol-v1.md` in the host): the engine works on
 * them, `service/BundleCodec` converts them from and to Bundles (roadmap P2.5).
 * zh-CN: 单个方案的纯数据模型 (契约 v1, 宿主 `docs/dev/batch-rename-plugin-protocol-v1.md`): 引擎处理它们, `service/BundleCodec`
 * 负责与 Bundle 互转 (路线图 P2.5).
 */
internal enum class ItemKind(val value: Int) {
    FILE(BatchRenameValues.KIND_FILE),
    DIRECTORY(BatchRenameValues.KIND_DIRECTORY),
}

/** One target in enumeration order. zh-CN: 按枚举顺序排列的一个目标. */
internal data class RenameItem(
    val index: Int,
    val parentId: String,
    val name: String,
    val kind: ItemKind,
    /** 0 for a selected item, 1 for its children and so on. zh-CN: 选中项为 0, 其子项为 1, 依此类推. */
    val depth: Int = 0,
    /** Epoch milliseconds, the `modified` date source. zh-CN: 毫秒时间戳, `modified` 日期来源. */
    val lastModified: Long = 0L,
    val length: Long = -1L,
    /** Excluded by the caller: keeps its name and does not advance counters. zh-CN: 调用方排除: 保持原名, 不推进计数器. */
    val excluded: Boolean = false,
)

/** A directory holding targets, with every name already present in it. zh-CN: 目标所在目录及其中已有的全部名称. */
internal data class RenameParent(
    val parentId: String,
    val existingNames: List<String>,
    /** Names differing only in case collide (FAT32 / exFAT). zh-CN: 仅大小写不同的名称相互冲突 (FAT32 / exFAT). */
    val caseInsensitive: Boolean = false,
    /** The file system rejects `\ : * ? " < > |`. zh-CN: 文件系统拒绝 `\ : * ? " < > |`. */
    val restrictedCharacters: Boolean = false,
)

internal data class RenameRequest(
    val options: RenameOptions,
    val parents: List<RenameParent>,
    val items: List<RenameItem>,
    /** Month and weekday names and case conversion. zh-CN: 月份与星期名称以及大小写转换所用的区域. */
    val locale: Locale = Locale.ROOT,
    /** Time zone of the date variables. zh-CN: 日期变量所用的时区. */
    val timeZone: ZoneId = ZoneId.of("UTC"),
    /** Epoch milliseconds of the `now` date source, fixed for the whole plan. zh-CN: `now` 日期来源的毫秒时间戳, 整个方案固定. */
    val now: Long = 0L,
)

internal enum class PlanItemStatus(val value: String) {
    RENAME(BatchRenameValues.STATUS_RENAME),
    UNCHANGED(BatchRenameValues.STATUS_UNCHANGED),
    CONFLICT(BatchRenameValues.STATUS_CONFLICT),
    INVALID(BatchRenameValues.STATUS_INVALID),
    EXCLUDED(BatchRenameValues.STATUS_EXCLUDED),
}

internal enum class PlanReason(val value: String) {
    NO_MATCH(BatchRenameValues.REASON_NO_MATCH),
    SAME_NAME(BatchRenameValues.REASON_SAME_NAME),
    EXCLUDED_BY_CALLER(BatchRenameValues.REASON_EXCLUDED_BY_CALLER),
    NOT_INCLUDED(BatchRenameValues.REASON_NOT_INCLUDED),
    DUPLICATE_IN_BATCH(BatchRenameValues.REASON_DUPLICATE_IN_BATCH),
    EXISTING_ENTRY(BatchRenameValues.REASON_EXISTING_ENTRY),
    NUMBERS_EXHAUSTED(BatchRenameValues.REASON_NUMBERS_EXHAUSTED),
    NUMBERED(BatchRenameValues.REASON_NUMBERED),
    OVERWRITE(BatchRenameValues.REASON_OVERWRITE),
    EMPTY(BatchRenameValues.REASON_EMPTY),
    DOT_NAME(BatchRenameValues.REASON_DOT_NAME),
    SEPARATOR(BatchRenameValues.REASON_SEPARATOR),
    TOO_LONG(BatchRenameValues.REASON_TOO_LONG),
    RESTRICTED_CHARACTER(BatchRenameValues.REASON_RESTRICTED_CHARACTER),
}

/** The answer for one request item. zh-CN: 单个请求条目的应答. */
internal data class PlanItem(
    val index: Int,
    /** Equals the current name unless the item is renamed. zh-CN: 除非改名, 否则与当前名称相同. */
    val newName: String,
    val status: PlanItemStatus,
    val reason: PlanReason? = null,
    /** Needs a temporary name first (case-only change, swap or cycle). zh-CN: 需要先改为临时名 (仅大小写变化, 交换或循环). */
    val twoPhase: Boolean = false,
    /** Replaces an existing entry (overwrite policy). zh-CN: 覆盖已有条目 (覆盖策略). */
    val overwrites: Boolean = false,
)

internal data class RenamePlan(
    val items: List<PlanItem>,
    /** The plan must not be executed. zh-CN: 方案不得执行. */
    val rejected: Boolean,
) {
    /** Item count per status, every status present. zh-CN: 每种状态的条目数, 包含全部状态. */
    val counts: Map<PlanItemStatus, Int>
        get() = PlanItemStatus.entries.associateWith { status -> items.count { it.status == status } }
}

/**
 * A request the engine refuses as a whole; [code] is one of `BatchRenameErrorCodes`.
 * zh-CN: 引擎整体拒绝的请求; [code] 为 `BatchRenameErrorCodes` 之一.
 */
internal class PlanException(val code: String, message: String) : Exception(message) {
    companion object {
        fun invalidOptions(message: String) = PlanException(BatchRenameErrorCodes.INVALID_OPTIONS, message)
        fun invalidRequest(message: String) = PlanException(BatchRenameErrorCodes.INVALID_REQUEST, message)
        fun limitExceeded(message: String) = PlanException(BatchRenameErrorCodes.LIMIT_EXCEEDED, message)
    }
}
