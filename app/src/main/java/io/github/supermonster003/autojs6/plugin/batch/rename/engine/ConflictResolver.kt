package io.github.supermonster003.autojs6.plugin.batch.rename.engine

import io.github.supermonster003.autojs6.plugin.batch.rename.engine.validate.NameValidator
import org.autojs.plugin.batchrename.api.BatchRenameLimits
import org.autojs.plugin.batchrename.api.BatchRenameValues
import java.util.Locale

/**
 * Validation and conflicts of a plan (roadmap D19 / D20, the decision part; the host executes and checks again).
 *
 * Per directory, the names that stay occupied are the existing names minus the old names of the items renamed in it.
 * An item conflicts when another renamed item gets the same name (`duplicateInBatch`) or when its name stays occupied
 * (`existingEntry`); names compare case-insensitively in directories marked so. Items that keep their names (invalid,
 * skipped, conflicting) keep occupying them, so resolution repeats until nothing changes.
 *
 * Policies: `fail` reports every conflicting item and rejects the plan; `skip` keeps the names of conflicting items;
 * `number` appends the pattern (` ({n})` by default) with the first free number up to 999; `overwrite` replaces an
 * existing entry, which the host moves to its Trash first. Within a group of duplicates the first item in request order
 * keeps the name under the last three policies. Invalid items always reject the plan.
 *
 * zh-CN: 方案的校验与冲突 (路线图 D19 / D20 的判定部分; 宿主执行时会再次校验). 在每个目录中, 仍被占用的名称为既有名称减去本批
 * 在该目录中改名条目的旧名. 当另一个改名条目得到相同名称 (`duplicateInBatch`) 或新名称仍被占用 (`existingEntry`) 时条目冲突;
 * 标记为不区分大小写的目录按不区分大小写比较. 保留原名的条目 (无效, 跳过, 冲突) 继续占用原名, 因此反复判定直到不再变化.
 * 策略: `fail` 报告所有冲突条目并拒绝方案; `skip` 让冲突条目保留原名; `number` 追加模式 (默认 ` ({n})`) 并取 999 以内第一个
 * 空闲序号; `overwrite` 覆盖既有条目, 宿主先将其移入回收站. 在后三种策略下, 一组重名条目中请求顺序最前的条目保留该名称. 无效条目
 * 总会使方案被拒绝.
 */
internal object ConflictResolver {

    fun resolve(request: RenameRequest, proposals: List<PlanItem>): RenamePlan {
        val parents = request.parents.associateBy { it.parentId }
        val resolved = proposals.toMutableList()
        // 1. Validation: invalid names never reach the file system. zh-CN: 校验: 无效名称不会到达文件系统.
        request.items.forEach { item ->
            val proposal = resolved[item.index]
            if (proposal.status != PlanItemStatus.RENAME) return@forEach
            NameValidator.validate(proposal.newName, parents.getValue(item.parentId).restrictedCharacters)?.let { reason ->
                resolved[item.index] = proposal.copy(status = PlanItemStatus.INVALID, reason = reason)
            }
        }
        // 2. Conflicts, per directory. zh-CN: 冲突, 按目录判定.
        val policy = request.options.onConflict
        request.items.groupBy { it.parentId }.forEach { (parentId, items) ->
            DirectoryResolution(parents.getValue(parentId), items, resolved, request.options).run(policy)
        }
        // 3. Two-phase renames. zh-CN: 两阶段改名.
        request.items.groupBy { it.parentId }.forEach { (parentId, items) ->
            markTwoPhase(parents.getValue(parentId), items, resolved)
        }
        val rejected = resolved.any { it.status == PlanItemStatus.INVALID } ||
            policy == ConflictPolicy.FAIL && resolved.any { it.status == PlanItemStatus.CONFLICT }
        return RenamePlan(resolved, rejected)
    }

    private class DirectoryResolution(
        private val parent: RenameParent,
        private val items: List<RenameItem>,
        private val resolved: MutableList<PlanItem>,
        private val options: RenameOptions,
    ) {
        private val itemsByIndex = items.associateBy { it.index }

        fun run(policy: ConflictPolicy) {
            // Every round turns at least one rename into a conflict or a free numbered name; a numbered name only
            // conflicts again after another item fell back to its old name, so the loop ends.
            // zh-CN: 每一轮至少把一个改名变为冲突或空闲的编号名称; 编号名称只会在另一条目退回原名后再次冲突, 因此循环必然结束.
            while (true) {
                val renamed = renamedItems()
                val conflicts = findConflicts(renamed, occupiedKeys(renamed), firstWins = policy != ConflictPolicy.FAIL)
                if (conflicts.isEmpty()) return
                conflicts.forEach { (index, reason) ->
                    when {
                        policy == ConflictPolicy.OVERWRITE && reason == PlanReason.EXISTING_ENTRY -> overwrite(index)
                        policy == ConflictPolicy.NUMBER -> number(index)
                        else -> markConflict(index, reason)
                    }
                }
            }
        }

        private fun renamedItems(): List<RenameItem> = items.filter { resolved[it.index].status == PlanItemStatus.RENAME }

        /** Keys of the names that stay in the directory: existing names minus the old names of renamed items. */
        private fun occupiedKeys(renamed: List<RenameItem>): MutableMap<String, Int> {
            val occupied = HashMap<String, Int>()
            parent.existingNames.forEach { occupied.merge(key(it), 1, Int::plus) }
            renamed.forEach { item -> occupied.computeIfPresent(key(item.name)) { _, count -> (count - 1).takeIf { it > 0 } } }
            return occupied
        }

        /**
         * Conflicting items and their reasons, in request order. With [firstWins] the first item of a duplicate group
         * keeps the name; otherwise every member is reported.
         */
        private fun findConflicts(renamed: List<RenameItem>, occupied: Map<String, Int>, firstWins: Boolean): List<Pair<Int, PlanReason>> {
            val conflicts = mutableListOf<Pair<Int, PlanReason>>()
            renamed.groupBy { key(resolved[it.index].newName) }.forEach { (newKey, group) ->
                val taken = (occupied[newKey] ?: 0) > 0
                group.forEachIndexed { position, item ->
                    when {
                        // An overwrite already accepted the existing entry. zh-CN: 覆盖已接受该既有条目.
                        taken && !resolved[item.index].overwrites -> conflicts += item.index to PlanReason.EXISTING_ENTRY
                        group.size > 1 && (!firstWins || position > 0) -> conflicts += item.index to PlanReason.DUPLICATE_IN_BATCH
                    }
                }
            }
            return conflicts.sortedBy { it.first }
        }

        private fun markConflict(index: Int, reason: PlanReason) {
            resolved[index] = resolved[index].copy(status = PlanItemStatus.CONFLICT, reason = reason)
        }

        private fun overwrite(index: Int) {
            resolved[index] = resolved[index].copy(reason = PlanReason.OVERWRITE, overwrites = true)
        }

        /** The first free numbered name, or a `numbersExhausted` conflict. zh-CN: 第一个空闲的编号名称, 否则为序号耗尽冲突. */
        /**
         * Gives the item the first free numbered name; the pattern goes before the extension (`photo (1).jpg`). A
         * numbered name equal to the item's own name leaves it unchanged.
         * zh-CN: 为条目取第一个空闲的编号名称; 模式插在扩展名之前 (`photo (1).jpg`). 编号名称与条目自身名称相同时保持不变.
         */
        private fun number(index: Int) {
            val proposal = resolved[index]
            val item = itemsByIndex.getValue(index)
            val renamed = renamedItems()
            val taken = occupiedKeys(renamed).filterValues { it > 0 }.keys.toHashSet()
            renamed.filter { it.index != index }.forEach { taken += key(resolved[it.index].newName) }
            val parts = NameParts.of(proposal.newName, item.kind)
            for (number in 1..BatchRenameLimits.MAX_CONFLICT_NUMBER) {
                val suffix = options.conflictNumberPattern.replace(BatchRenameValues.CONFLICT_NUMBER_PLACEHOLDER, number.toString())
                val candidate = parts.copy(stem = parts.stem + suffix).join()
                // Longer numbers only make the name longer. zh-CN: 更大的序号只会让名称更长.
                if (NameValidator.validate(candidate, parent.restrictedCharacters) != null) break
                if (key(candidate) in taken) continue
                resolved[index] = if (candidate == item.name) {
                    proposal.copy(newName = item.name, status = PlanItemStatus.UNCHANGED, reason = PlanReason.SAME_NAME)
                } else {
                    proposal.copy(newName = candidate, reason = PlanReason.NUMBERED)
                }
                return
            }
            markConflict(index, PlanReason.NUMBERS_EXHAUSTED)
        }

        private fun key(name: String): String = if (parent.caseInsensitive) name.lowercase(Locale.ROOT) else name
    }

    /**
     * Marks renames that need a temporary name: a case-only change in a case-insensitive directory, and a name that
     * another renamed item of the batch still holds (chains, swaps and cycles), so the host can move every such item
     * aside first and never depends on ordering.
     * zh-CN: 标记需要临时名的改名: 不区分大小写的目录中仅大小写变化, 以及目标名仍被本批另一个改名条目占用 (链, 交换与循环),
     * 宿主可先把这些条目移开, 不依赖执行顺序.
     */
    private fun markTwoPhase(parent: RenameParent, items: List<RenameItem>, resolved: MutableList<PlanItem>) {
        fun key(name: String) = if (parent.caseInsensitive) name.lowercase(Locale.ROOT) else name
        val renamed = items.filter { resolved[it.index].status == PlanItemStatus.RENAME }
        val oldKeys = renamed.associateBy { key(it.name) }
        renamed.forEach { item ->
            val proposal = resolved[item.index]
            val newKey = key(proposal.newName)
            val caseOnly = newKey == key(item.name)
            val heldByAnother = oldKeys[newKey]?.let { it.index != item.index } == true
            if (caseOnly || heldByAnother) resolved[item.index] = proposal.copy(twoPhase = true)
        }
    }
}
