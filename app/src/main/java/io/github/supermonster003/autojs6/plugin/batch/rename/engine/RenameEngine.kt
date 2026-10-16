package io.github.supermonster003.autojs6.plugin.batch.rename.engine

import io.github.supermonster003.autojs6.plugin.batch.rename.engine.format.TextFormatter
import io.github.supermonster003.autojs6.plugin.batch.rename.engine.local.NameNormalizer
import io.github.supermonster003.autojs6.plugin.batch.rename.engine.pinyin.PinyinConverter
import io.github.supermonster003.autojs6.plugin.batch.rename.engine.regex.JsRegex
import io.github.supermonster003.autojs6.plugin.batch.rename.engine.regex.JsRegexEngine
import io.github.supermonster003.autojs6.plugin.batch.rename.engine.regex.JsRegexSession
import io.github.supermonster003.autojs6.plugin.batch.rename.engine.regex.JsRegexSyntaxException
import io.github.supermonster003.autojs6.plugin.batch.rename.engine.regex.JsRegexTimeoutException
import io.github.supermonster003.autojs6.plugin.batch.rename.engine.regex.RhinoRegexEngine
import io.github.supermonster003.autojs6.plugin.batch.rename.engine.variables.ContinuedNumbering
import io.github.supermonster003.autojs6.plugin.batch.rename.engine.variables.DateFieldFormatter
import io.github.supermonster003.autojs6.plugin.batch.rename.engine.variables.RandomSource
import io.github.supermonster003.autojs6.plugin.batch.rename.engine.variables.ReplacementTemplate
import io.github.supermonster003.autojs6.plugin.batch.rename.engine.variables.SecureRandomSource
import io.github.supermonster003.autojs6.plugin.batch.rename.engine.variables.VariableExpander
import org.autojs.plugin.batchrename.api.BatchRenameLimits

/**
 * Computes a rename plan (roadmap D13 / D16): the single engine behind the script path (the host's `plan` call) and the
 * file manager screen. For every item, in request order:
 *
 * 1. items excluded by the caller or by `includeFiles` / `includeFolders` / `includeSubfolders` keep their names;
 * 2. the `target` part (name, extension or whole name) must match the search, otherwise the item is unchanged;
 * 3. the variables of `replace` are expanded for the item, then the ECMAScript replacement runs on the part;
 * 4. pinyin (roadmap D35), text formatting and the local extensions apply to the part, which is joined back into the name.
 *
 * Counters advance only for items whose name changes (PowerRename). [ConflictResolver] then validates the new names and
 * applies the conflict policy (roadmap D19 / D20).
 *
 * zh-CN: 计算改名方案 (路线图 D13 / D16): 脚本路径 (宿主的 `plan` 调用) 与文件管理器界面共用的唯一引擎. 按请求顺序处理每个条目:
 * 被调用方或 `includeFiles` / `includeFolders` / `includeSubfolders` 排除的条目保持原名; `target` 部分 (主名, 扩展名或全名)
 * 必须匹配查找, 否则不变; 先为该条目展开 `replace` 中的变量, 再对该部分执行 ECMAScript 替换; 拼音 (路线图 D35), 文本格式化与本土扩展作用于该部分,
 * 再拼回名称. 计数器只对名称发生变化的条目递增 (PowerRename). 随后由 [ConflictResolver] 校验新名称并应用冲突策略 (路线图 D19 / D20).
 */
internal class RenameEngine(
    private val regexEngine: JsRegexEngine = RhinoRegexEngine(),
    private val random: RandomSource = SecureRandomSource,
    /** Loads the dictionary on first use. zh-CN: 首次使用时加载词典. */
    private val pinyin: () -> PinyinConverter = { PinyinConverter.default },
) {

    /**
     * @throws PlanException when the request as a whole cannot be planned.
     */
    fun plan(request: RenameRequest): RenamePlan {
        RequestChecks.check(request)
        val options = request.options
        val template = ReplacementTemplate.parse(
            replace = options.replace,
            enumerate = options.enumerate,
            random = options.random,
            dateTime = options.dateTime != DateTimeSource.NONE,
        )
        val style = options.pinyinStyle
        val expander = VariableExpander(template, options.regex, DateFieldFormatter(request.locale, request.timeZone), random) { text ->
            pinyin().convert(text, style)
        }
        val offset = if (options.continueNumbering && template.hasCounters) {
            ContinuedNumbering.offset(template, options.regex, request.parents.asSequence().flatMap { it.existingNames }.asIterable())
        } else {
            0L
        }
        val items = try {
            regexEngine.session { session ->
                val regex = if (options.search.isEmpty()) null else compile(session, options)
                var renamed = 0L
                request.items.map { item ->
                    propose(session, regex, request, expander, item, renamed + offset).also { if (it.status == PlanItemStatus.RENAME) renamed++ }
                }
            }
        } catch (error: JsRegexTimeoutException) {
            throw PlanException.limitExceeded(error.message.orEmpty())
        }
        return ConflictResolver.resolve(request, items)
    }

    private fun compile(session: JsRegexSession, options: RenameOptions): JsRegex = try {
        session.compile(options.pattern, options.flags)
    } catch (error: JsRegexSyntaxException) {
        throw PlanException.invalidOptions("Invalid regular expression: ${error.message}")
    }

    private fun propose(
        session: JsRegexSession,
        regex: JsRegex?,
        request: RenameRequest,
        expander: VariableExpander,
        item: RenameItem,
        counterIndex: Long,
    ): PlanItem {
        val options = request.options
        if (item.excluded) return item.keep(PlanItemStatus.EXCLUDED, PlanReason.EXCLUDED_BY_CALLER)
        if (!options.includes(item)) return item.keep(PlanItemStatus.EXCLUDED, PlanReason.NOT_INCLUDED)
        val parts = NameParts.of(item.name, item.kind)
        val editable = when (options.target) {
            RenameTarget.NAME -> parts.stem
            RenameTarget.EXTENSION -> parts.extension ?: return item.keep(PlanItemStatus.UNCHANGED, PlanReason.NO_MATCH)
            RenameTarget.ALL -> item.name
        }
        if (regex != null && !session.test(editable, regex)) return item.keep(PlanItemStatus.UNCHANGED, PlanReason.NO_MATCH)
        val dateMillis = if (options.dateTime == DateTimeSource.NOW) request.now else item.lastModified
        val replaced = regex?.let { session.replace(editable, it, expander.expand(dateMillis, counterIndex, editable)) } ?: editable
        val romanized = if (options.pinyin) pinyin().convert(replaced, options.pinyinStyle) else replaced
        val formatted = TextFormatter.format(romanized, options.format, request.locale)
        val cleaned = NameNormalizer.apply(formatted, options, holdsStem = options.target != RenameTarget.EXTENSION)
        val newName = when (options.target) {
            RenameTarget.NAME -> NameParts(cleaned, parts.extension).join()
            RenameTarget.EXTENSION -> NameParts(parts.stem, cleaned).join()
            RenameTarget.ALL -> cleaned
        }
        return if (newName == item.name) item.keep(PlanItemStatus.UNCHANGED, PlanReason.SAME_NAME) else PlanItem(item.index, newName, PlanItemStatus.RENAME)
    }

    private fun RenameOptions.includes(item: RenameItem): Boolean = when {
        item.kind == ItemKind.FILE && !includeFiles -> false
        item.kind == ItemKind.DIRECTORY && !includeFolders -> false
        item.depth > 0 && !includeSubfolders -> false
        else -> true
    }

    private fun RenameItem.keep(status: PlanItemStatus, reason: PlanReason) = PlanItem(index, name, status, reason)
}

/**
 * The limits and consistency rules of one request (contract v1). zh-CN: 单个请求的上限与一致性规则 (契约 v1).
 */
internal object RequestChecks {

    fun check(request: RenameRequest) {
        if (request.items.size > BatchRenameLimits.MAX_ITEMS_PER_PLAN) {
            throw PlanException.limitExceeded("${request.items.size} items exceed ${BatchRenameLimits.MAX_ITEMS_PER_PLAN}")
        }
        if (request.parents.size > BatchRenameLimits.MAX_PARENTS_PER_PLAN) {
            throw PlanException.limitExceeded("${request.parents.size} parent directories exceed ${BatchRenameLimits.MAX_PARENTS_PER_PLAN}")
        }
        val existingNames = request.parents.sumOf { it.existingNames.size }
        if (existingNames > BatchRenameLimits.MAX_EXISTING_NAMES_PER_PLAN) {
            throw PlanException.limitExceeded("$existingNames existing names exceed ${BatchRenameLimits.MAX_EXISTING_NAMES_PER_PLAN}")
        }
        val parentIds = request.parents.map { it.parentId }
        if (parentIds.toSet().size != parentIds.size) throw PlanException.invalidRequest("Parent ids must be unique")
        request.parents.forEach { parent -> parent.existingNames.forEach { checkName(it, "An existing name") } }
        val knownParents = parentIds.toSet()
        request.items.forEachIndexed { position, item ->
            if (item.index != position) throw PlanException.invalidRequest("Item $position carries the index ${item.index}")
            if (item.parentId !in knownParents) throw PlanException.invalidRequest("Item $position refers to the unknown parent \"${item.parentId}\"")
            if (item.depth < 0) throw PlanException.invalidRequest("Item $position has a negative depth")
            checkName(item.name, "Item $position")
        }
    }

    private fun checkName(name: String, subject: String) {
        if (name.length > BatchRenameLimits.MAX_NAME_CHARS) {
            throw PlanException.limitExceeded("$subject exceeds ${BatchRenameLimits.MAX_NAME_CHARS} characters")
        }
        if (name.isEmpty() || name == "." || name == ".." || '/' in name || '\u0000' in name) {
            throw PlanException.invalidRequest("$subject is not a single path segment")
        }
    }
}
