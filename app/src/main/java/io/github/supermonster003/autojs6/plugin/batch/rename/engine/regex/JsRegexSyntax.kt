package io.github.supermonster003.autojs6.plugin.batch.rename.engine.regex

/**
 * Pure helpers that turn rename options into ECMAScript pattern, flag and replacement text (roadmap D15 / D17).
 * zh-CN: 把改名选项转换为 ECMAScript 表达式, 标志与替换文本的纯函数 (路线图 D15 / D17).
 */
internal object JsRegexSyntax {

    /** The ECMAScript `SyntaxCharacter`s plus `/`; escaping them is valid with and without the `u` flag. */
    private const val SYNTAX_CHARACTERS = "\\^$.*+?()[]{}|/"

    /** The flags a caller may add to `g` and `i` (contract option `regexFlags`). zh-CN: 调用方可在 `g` 与 `i` 之外追加的标志. */
    const val EXTRA_FLAGS = "msuy"

    /**
     * Escapes [text] so that `new RegExp(result)` matches it literally: a plain-text search runs through the same
     * regular expression path, like PowerRename.
     * zh-CN: 转义 [text], 使 `new RegExp(result)` 按字面匹配它: 纯文本查找与 PowerRename 一样走同一条正则路径.
     */
    fun escapePattern(text: String): String = buildString(text.length + 8) {
        text.forEach { char ->
            if (char in SYNTAX_CHARACTERS) append('\\')
            append(char)
        }
    }

    /**
     * Escapes [text] so that `String.prototype.replace` inserts it literally (`$` becomes `$$`).
     * zh-CN: 转义 [text], 使 `String.prototype.replace` 按字面插入它 (`$` 变为 `$$`).
     */
    fun escapeReplacement(text: String): String = text.replace("$", "$$")

    /**
     * The flags of a search: `g` for [matchAll], `i` unless [caseSensitive], then [extraFlags] in the canonical order of
     * `RegExp.prototype.flags`.
     * zh-CN: 查找使用的标志: [matchAll] 时加 `g`, 非 [caseSensitive] 时加 `i`, 再按 `RegExp.prototype.flags` 的规范顺序加
     * [extraFlags].
     *
     * @throws IllegalArgumentException when [extraFlags] holds anything but distinct [EXTRA_FLAGS].
     */
    fun flags(matchAll: Boolean, caseSensitive: Boolean, extraFlags: String = ""): String {
        require(extraFlags.all { it in EXTRA_FLAGS } && extraFlags.toSet().size == extraFlags.length) {
            "Regular expression flags must be distinct letters of \"$EXTRA_FLAGS\", got \"$extraFlags\""
        }
        val flags = buildString {
            if (matchAll) append('g')
            if (!caseSensitive) append('i')
            append(extraFlags)
        }
        return CANONICAL_ORDER.filter { it in flags }
    }

    private const val CANONICAL_ORDER = "dgimsuvy"
}
