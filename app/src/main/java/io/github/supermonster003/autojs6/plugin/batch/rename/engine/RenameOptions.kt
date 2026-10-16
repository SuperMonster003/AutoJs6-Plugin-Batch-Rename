package io.github.supermonster003.autojs6.plugin.batch.rename.engine

import io.github.supermonster003.autojs6.plugin.batch.rename.engine.regex.JsRegexSyntax
import org.autojs.plugin.batchrename.api.BatchRenameLimits
import org.autojs.plugin.batchrename.api.BatchRenameOptionKeys
import org.autojs.plugin.batchrename.api.BatchRenameValues

internal enum class RenameTarget(val value: String) {
    NAME(BatchRenameValues.TARGET_NAME),
    EXTENSION(BatchRenameValues.TARGET_EXTENSION),
    ALL(BatchRenameValues.TARGET_ALL),
}

internal enum class TextFormat(val value: String) {
    NONE(BatchRenameValues.FORMAT_NONE),
    LOWER(BatchRenameValues.FORMAT_LOWER),
    UPPER(BatchRenameValues.FORMAT_UPPER),
    TITLE(BatchRenameValues.FORMAT_TITLE),
    CAPITALIZE(BatchRenameValues.FORMAT_CAPITALIZE),
}

internal enum class DateTimeSource(val value: String) {
    MODIFIED(BatchRenameValues.DATE_TIME_MODIFIED),
    NOW(BatchRenameValues.DATE_TIME_NOW),
    NONE(BatchRenameValues.DATE_TIME_NONE),
}

internal enum class Normalization(val value: String) {
    NONE(BatchRenameValues.NORMALIZE_NONE),
    NFC(BatchRenameValues.NORMALIZE_NFC),
    NFD(BatchRenameValues.NORMALIZE_NFD),
}

internal enum class ConflictPolicy(val value: String) {
    FAIL(BatchRenameValues.CONFLICT_FAIL),
    SKIP(BatchRenameValues.CONFLICT_SKIP),
    NUMBER(BatchRenameValues.CONFLICT_NUMBER),
    OVERWRITE(BatchRenameValues.CONFLICT_OVERWRITE),
}

internal enum class PinyinTone(val value: String) {
    NONE(BatchRenameValues.PINYIN_TONE_NONE),
    MARK(BatchRenameValues.PINYIN_TONE_MARK),
    NUMBER(BatchRenameValues.PINYIN_TONE_NUMBER),
}

internal enum class PinyinSeparator(val value: String, val text: String) {
    NONE(BatchRenameValues.PINYIN_SEPARATOR_NONE, ""),
    SPACE(BatchRenameValues.PINYIN_SEPARATOR_SPACE, " "),
    UNDERSCORE(BatchRenameValues.PINYIN_SEPARATOR_UNDERSCORE, "_"),
    HYPHEN(BatchRenameValues.PINYIN_SEPARATOR_HYPHEN, "-"),
}

internal enum class PinyinCase(val value: String) {
    LOWER(BatchRenameValues.PINYIN_CASE_LOWER),
    CAPITALIZE(BatchRenameValues.PINYIN_CASE_CAPITALIZE),
}

/**
 * How pinyin is written (roadmap D35), shared by the `pinyin` cleanup and the `${pinyin}` variable.
 * zh-CN: 拼音的书写方式 (路线图 D35), 由 `pinyin` 整理选项与 `${pinyin}` 变量共用.
 */
internal data class PinyinStyle(
    val tone: PinyinTone = PinyinTone.NONE,
    val separator: PinyinSeparator = PinyinSeparator.NONE,
    val case: PinyinCase = PinyinCase.LOWER,
    /** Only the first letter of each syllable, without tone. zh-CN: 每个音节只取首字母, 不带声调. */
    val initials: Boolean = false,
)

/**
 * The rename options of contract v1 with the PowerRename defaults of roadmap D17; the local extensions (roadmap D5) are
 * off by default.
 * zh-CN: 契约 v1 的改名选项, 默认值取路线图 D17 的 PowerRename 默认值; 本土扩展 (路线图 D5) 默认关闭.
 */
internal data class RenameOptions(
    /** Text or ECMAScript source; empty means every included item takes part without a replacement. */
    val search: String,
    val replace: String = "",
    val regex: Boolean = false,
    /** Distinct letters of `msuy` added to `g` and `i`. zh-CN: 在 `g` 与 `i` 之外追加的 `msuy` 标志. */
    val regexFlags: String = "",
    val matchAll: Boolean = false,
    val caseSensitive: Boolean = false,
    val target: RenameTarget = RenameTarget.ALL,
    val includeFiles: Boolean = true,
    val includeFolders: Boolean = true,
    val includeSubfolders: Boolean = true,
    val format: TextFormat = TextFormat.NONE,
    val enumerate: Boolean = false,
    val random: Boolean = false,
    val dateTime: DateTimeSource = DateTimeSource.MODIFIED,
    val normalize: Normalization = Normalization.NONE,
    val halfWidth: Boolean = false,
    val sanitize: Boolean = false,
    val sanitizeReplacement: String = BatchRenameValues.DEFAULT_SANITIZE_REPLACEMENT,
    val trim: Boolean = false,
    val continueNumbering: Boolean = false,
    val onConflict: ConflictPolicy = ConflictPolicy.FAIL,
    val conflictNumberPattern: String = BatchRenameValues.DEFAULT_CONFLICT_NUMBER_PATTERN,
    /** Chinese characters to pinyin after the replacement, before [format] (roadmap D35). zh-CN: 替换之后, 格式化之前汉字转拼音. */
    val pinyin: Boolean = false,
    val pinyinTone: PinyinTone = PinyinTone.NONE,
    val pinyinSeparator: PinyinSeparator = PinyinSeparator.NONE,
    val pinyinCase: PinyinCase = PinyinCase.LOWER,
    val pinyinInitials: Boolean = false,
    /** Expands the photo metadata variables (roadmap D34); they stay literal when off. zh-CN: 展开照片元数据变量; 关闭时保持字面. */
    val metadata: Boolean = false,
) {
    /** The ECMAScript source of [search]: plain text is escaped (roadmap D15). zh-CN: [search] 对应的 ECMAScript 源码. */
    val pattern: String
        get() = if (regex) search else JsRegexSyntax.escapePattern(search)

    val flags: String
        get() = JsRegexSyntax.flags(matchAll, caseSensitive, regexFlags)

    val pinyinStyle: PinyinStyle
        get() = PinyinStyle(pinyinTone, pinyinSeparator, pinyinCase, pinyinInitials)
}

/**
 * Options read from the transport (the options Bundle as a map) plus the keys that were ignored.
 * zh-CN: 从传输层 (以 Map 表示的选项 Bundle) 读出的选项, 以及被忽略的键.
 */
internal data class ParsedOptions(val options: RenameOptions, val unknownKeys: Set<String>)

/**
 * Validates and normalizes the options of one request. Missing keys take their defaults, unknown keys are ignored and
 * reported, and anything else that does not fit the contract fails the whole request.
 * zh-CN: 校验并规范化单个请求的选项. 缺省的键取默认值, 未知键被忽略并报告, 其余不符合契约的内容使整个请求失败.
 */
internal object RenameOptionsParser {

    private val KNOWN_KEYS = setOf(
        BatchRenameOptionKeys.SEARCH, BatchRenameOptionKeys.REPLACE, BatchRenameOptionKeys.REGEX, BatchRenameOptionKeys.REGEX_FLAGS,
        BatchRenameOptionKeys.MATCH_ALL, BatchRenameOptionKeys.CASE_SENSITIVE, BatchRenameOptionKeys.TARGET,
        BatchRenameOptionKeys.INCLUDE_FILES, BatchRenameOptionKeys.INCLUDE_FOLDERS, BatchRenameOptionKeys.INCLUDE_SUBFOLDERS,
        BatchRenameOptionKeys.FORMAT, BatchRenameOptionKeys.ENUMERATE, BatchRenameOptionKeys.RANDOM, BatchRenameOptionKeys.DATE_TIME,
        BatchRenameOptionKeys.NORMALIZE, BatchRenameOptionKeys.HALF_WIDTH, BatchRenameOptionKeys.SANITIZE,
        BatchRenameOptionKeys.SANITIZE_REPLACEMENT, BatchRenameOptionKeys.TRIM, BatchRenameOptionKeys.CONTINUE_NUMBERING,
        BatchRenameOptionKeys.ON_CONFLICT, BatchRenameOptionKeys.CONFLICT_NUMBER_PATTERN,
        BatchRenameOptionKeys.PINYIN, BatchRenameOptionKeys.PINYIN_TONE, BatchRenameOptionKeys.PINYIN_SEPARATOR,
        BatchRenameOptionKeys.PINYIN_CASE, BatchRenameOptionKeys.PINYIN_INITIALS, BatchRenameOptionKeys.METADATA,
    )

    /** Characters a sanitize replacement or a number pattern must not bring into a name. */
    private const val FORBIDDEN_IN_NAME_TEXT = "/\u0000"

    /** @throws PlanException with `INVALID_OPTIONS` or `LIMIT_EXCEEDED`. */
    fun parse(values: Map<String, Any?>): ParsedOptions {
        val defaults = RenameOptions(search = "")
        val options = RenameOptions(
            search = values.text(BatchRenameOptionKeys.SEARCH) ?: throw PlanException.invalidOptions("The option \"search\" is required"),
            replace = values.text(BatchRenameOptionKeys.REPLACE) ?: defaults.replace,
            regex = values.flag(BatchRenameOptionKeys.REGEX) ?: defaults.regex,
            regexFlags = values.text(BatchRenameOptionKeys.REGEX_FLAGS)?.also(::checkRegexFlags) ?: defaults.regexFlags,
            matchAll = values.flag(BatchRenameOptionKeys.MATCH_ALL) ?: defaults.matchAll,
            caseSensitive = values.flag(BatchRenameOptionKeys.CASE_SENSITIVE) ?: defaults.caseSensitive,
            target = values.choice(BatchRenameOptionKeys.TARGET, RenameTarget.entries, RenameTarget::value) ?: defaults.target,
            includeFiles = values.flag(BatchRenameOptionKeys.INCLUDE_FILES) ?: defaults.includeFiles,
            includeFolders = values.flag(BatchRenameOptionKeys.INCLUDE_FOLDERS) ?: defaults.includeFolders,
            includeSubfolders = values.flag(BatchRenameOptionKeys.INCLUDE_SUBFOLDERS) ?: defaults.includeSubfolders,
            format = values.choice(BatchRenameOptionKeys.FORMAT, TextFormat.entries, TextFormat::value) ?: defaults.format,
            enumerate = values.flag(BatchRenameOptionKeys.ENUMERATE) ?: defaults.enumerate,
            random = values.flag(BatchRenameOptionKeys.RANDOM) ?: defaults.random,
            dateTime = values.choice(BatchRenameOptionKeys.DATE_TIME, DateTimeSource.entries, DateTimeSource::value) ?: defaults.dateTime,
            normalize = values.choice(BatchRenameOptionKeys.NORMALIZE, Normalization.entries, Normalization::value) ?: defaults.normalize,
            halfWidth = values.flag(BatchRenameOptionKeys.HALF_WIDTH) ?: defaults.halfWidth,
            sanitize = values.flag(BatchRenameOptionKeys.SANITIZE) ?: defaults.sanitize,
            sanitizeReplacement = values.text(BatchRenameOptionKeys.SANITIZE_REPLACEMENT)?.also(::checkSanitizeReplacement)
                ?: defaults.sanitizeReplacement,
            trim = values.flag(BatchRenameOptionKeys.TRIM) ?: defaults.trim,
            continueNumbering = values.flag(BatchRenameOptionKeys.CONTINUE_NUMBERING) ?: defaults.continueNumbering,
            onConflict = values.choice(BatchRenameOptionKeys.ON_CONFLICT, ConflictPolicy.entries, ConflictPolicy::value) ?: defaults.onConflict,
            conflictNumberPattern = values.text(BatchRenameOptionKeys.CONFLICT_NUMBER_PATTERN)?.also(::checkNumberPattern)
                ?: defaults.conflictNumberPattern,
            pinyin = values.flag(BatchRenameOptionKeys.PINYIN) ?: defaults.pinyin,
            pinyinTone = values.choice(BatchRenameOptionKeys.PINYIN_TONE, PinyinTone.entries, PinyinTone::value) ?: defaults.pinyinTone,
            pinyinSeparator = values.choice(BatchRenameOptionKeys.PINYIN_SEPARATOR, PinyinSeparator.entries, PinyinSeparator::value)
                ?: defaults.pinyinSeparator,
            pinyinCase = values.choice(BatchRenameOptionKeys.PINYIN_CASE, PinyinCase.entries, PinyinCase::value) ?: defaults.pinyinCase,
            pinyinInitials = values.flag(BatchRenameOptionKeys.PINYIN_INITIALS) ?: defaults.pinyinInitials,
            metadata = values.flag(BatchRenameOptionKeys.METADATA) ?: defaults.metadata,
        )
        return ParsedOptions(options, values.keys - KNOWN_KEYS)
    }

    /**
     * [options] as the contract option map: every key, booleans as Boolean and everything else as String. Parsing the
     * map gives [options] back; presets, the effective options of a plan and the remembered options use it.
     * zh-CN: 以契约选项表表示的 [options]: 包含全部键, 布尔值为 Boolean, 其余为 String. 解析该表可得回 [options]; 预设,
     * 方案的生效选项与记住的选项都使用它.
     */
    fun values(options: RenameOptions): LinkedHashMap<String, Any> = linkedMapOf(
        BatchRenameOptionKeys.SEARCH to options.search,
        BatchRenameOptionKeys.REPLACE to options.replace,
        BatchRenameOptionKeys.REGEX to options.regex,
        BatchRenameOptionKeys.REGEX_FLAGS to options.regexFlags,
        BatchRenameOptionKeys.MATCH_ALL to options.matchAll,
        BatchRenameOptionKeys.CASE_SENSITIVE to options.caseSensitive,
        BatchRenameOptionKeys.TARGET to options.target.value,
        BatchRenameOptionKeys.INCLUDE_FILES to options.includeFiles,
        BatchRenameOptionKeys.INCLUDE_FOLDERS to options.includeFolders,
        BatchRenameOptionKeys.INCLUDE_SUBFOLDERS to options.includeSubfolders,
        BatchRenameOptionKeys.FORMAT to options.format.value,
        BatchRenameOptionKeys.ENUMERATE to options.enumerate,
        BatchRenameOptionKeys.RANDOM to options.random,
        BatchRenameOptionKeys.DATE_TIME to options.dateTime.value,
        BatchRenameOptionKeys.NORMALIZE to options.normalize.value,
        BatchRenameOptionKeys.HALF_WIDTH to options.halfWidth,
        BatchRenameOptionKeys.SANITIZE to options.sanitize,
        BatchRenameOptionKeys.SANITIZE_REPLACEMENT to options.sanitizeReplacement,
        BatchRenameOptionKeys.TRIM to options.trim,
        BatchRenameOptionKeys.CONTINUE_NUMBERING to options.continueNumbering,
        BatchRenameOptionKeys.ON_CONFLICT to options.onConflict.value,
        BatchRenameOptionKeys.CONFLICT_NUMBER_PATTERN to options.conflictNumberPattern,
        BatchRenameOptionKeys.PINYIN to options.pinyin,
        BatchRenameOptionKeys.PINYIN_TONE to options.pinyinTone.value,
        BatchRenameOptionKeys.PINYIN_SEPARATOR to options.pinyinSeparator.value,
        BatchRenameOptionKeys.PINYIN_CASE to options.pinyinCase.value,
        BatchRenameOptionKeys.PINYIN_INITIALS to options.pinyinInitials,
        BatchRenameOptionKeys.METADATA to options.metadata,
    )

    private fun Map<String, Any?>.text(key: String): String? {
        if (!containsKey(key)) return null
        val value = get(key) as? String ?: throw PlanException.invalidOptions("The option \"$key\" must be a string")
        if (value.length > BatchRenameLimits.MAX_PATTERN_CHARS) {
            throw PlanException.limitExceeded("The option \"$key\" exceeds ${BatchRenameLimits.MAX_PATTERN_CHARS} characters")
        }
        return value
    }

    private fun Map<String, Any?>.flag(key: String): Boolean? {
        if (!containsKey(key)) return null
        return get(key) as? Boolean ?: throw PlanException.invalidOptions("The option \"$key\" must be a boolean")
    }

    private fun <E> Map<String, Any?>.choice(key: String, entries: List<E>, value: (E) -> String): E? {
        if (!containsKey(key)) return null
        val text = get(key) as? String
        return entries.firstOrNull { value(it) == text }
            ?: throw PlanException.invalidOptions("The option \"$key\" must be one of ${entries.joinToString { "\"${value(it)}\"" }}")
    }

    private fun checkRegexFlags(flags: String) {
        if (!flags.all { it in JsRegexSyntax.EXTRA_FLAGS } || flags.toSet().size != flags.length) {
            throw PlanException.invalidOptions("The option \"regexFlags\" takes distinct letters of \"${JsRegexSyntax.EXTRA_FLAGS}\"")
        }
    }

    private fun checkSanitizeReplacement(replacement: String) {
        if (replacement.any { it in FORBIDDEN_IN_NAME_TEXT || it in NameCharacters.RESTRICTED || it < ' ' || it == '\u007f' }) {
            throw PlanException.invalidOptions("The option \"sanitizeReplacement\" must not contain characters it replaces")
        }
    }

    private fun checkNumberPattern(pattern: String) {
        if (BatchRenameValues.CONFLICT_NUMBER_PLACEHOLDER !in pattern || pattern.any { it in FORBIDDEN_IN_NAME_TEXT }) {
            throw PlanException.invalidOptions(
                "The option \"conflictNumberPattern\" must contain ${BatchRenameValues.CONFLICT_NUMBER_PLACEHOLDER} and no \"/\" or NUL",
            )
        }
    }
}

/** Character classes shared by cleaning and validation (roadmap D19). zh-CN: 清理与校验共用的字符类别 (路线图 D19). */
internal object NameCharacters {
    /** Rejected by FAT32 / exFAT and Windows, replaced by `sanitize`. zh-CN: FAT32 / exFAT 与 Windows 拒绝的字符. */
    const val RESTRICTED = "\\:*?\"<>|"
}
