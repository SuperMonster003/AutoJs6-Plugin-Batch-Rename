package io.github.supermonster003.autojs6.plugin.batch.rename.settings

import io.github.supermonster003.autojs6.plugin.batch.rename.engine.PlanException
import io.github.supermonster003.autojs6.plugin.batch.rename.engine.RenameOptions
import io.github.supermonster003.autojs6.plugin.batch.rename.engine.RenameOptionsParser
import org.autojs.plugin.batchrename.api.BatchRenameOptionKeys

/** The few string operations the store needs; SharedPreferences on the device. zh-CN: 存储所需的字符串操作. */
interface KeyValueStore {
    fun getString(key: String): String?
    fun edit(block: KeyValueEditor.() -> Unit)
}

interface KeyValueEditor {
    fun putString(key: String, value: String)
    fun remove(key: String)
}

/**
 * The screen's remembered state (roadmap D25): the last options when "Remember last options" is on (the default), and
 * the most recently used search and replacement texts (at most [MAX_RECENT] each, newest first, no repeats). Paths and
 * file names are never stored; texts are what the user typed.
 * zh-CN: 界面记住的状态 (路线图 D25): 开启 "记住上次选项" (默认开启) 时的上次选项, 以及最近使用的查找与替换文本 (各至多 [MAX_RECENT] 条,
 * 最新在前, 不重复). 从不存储路径与文件名; 文本为用户输入的内容.
 */
internal class PreferencesStore(private val store: KeyValueStore) {

    var rememberOptions: Boolean
        get() = store.getString(KEY_REMEMBER) != "false"
        set(value) = store.edit {
            putString(KEY_REMEMBER, value.toString())
            if (!value) OptionsCodec.KEYS.forEach { remove(OPTION_PREFIX + it) }
        }

    /** The remembered options, or the defaults (D17) when nothing usable is stored. zh-CN: 记住的选项, 或默认值. */
    fun options(): RenameOptions {
        if (!rememberOptions) return DEFAULT_OPTIONS
        val stored = OptionsCodec.KEYS.mapNotNull { key -> store.getString(OPTION_PREFIX + key)?.let { key to it } }.toMap()
        if (stored.isEmpty()) return DEFAULT_OPTIONS
        return OptionsCodec.decode(stored) ?: DEFAULT_OPTIONS
    }

    fun saveOptions(options: RenameOptions) {
        if (!rememberOptions) return
        store.edit { OptionsCodec.encode(options).forEach { (key, value) -> putString(OPTION_PREFIX + key, value) } }
    }

    fun resetOptions() = store.edit { OptionsCodec.KEYS.forEach { remove(OPTION_PREFIX + it) } }

    fun recentSearches(): List<String> = recent(KEY_RECENT_SEARCH)

    fun recentReplacements(): List<String> = recent(KEY_RECENT_REPLACE)

    /** Puts the texts of an applied rename in front of their lists. zh-CN: 把一次执行所用的文本放到各自列表最前面. */
    fun remember(search: String, replace: String) {
        store.edit {
            if (search.isNotEmpty()) write(this, KEY_RECENT_SEARCH, listOf(search) + recentSearches())
            if (replace.isNotEmpty()) write(this, KEY_RECENT_REPLACE, listOf(replace) + recentReplacements())
        }
    }

    fun clearHistory() = store.edit {
        (0 until MAX_RECENT).forEach { index ->
            remove("$KEY_RECENT_SEARCH.$index")
            remove("$KEY_RECENT_REPLACE.$index")
        }
    }

    private fun recent(prefix: String): List<String> = (0 until MAX_RECENT).mapNotNull { store.getString("$prefix.$it") }

    private fun write(editor: KeyValueEditor, prefix: String, values: List<String>) {
        val kept = values.distinct().take(MAX_RECENT)
        (0 until MAX_RECENT).forEach { index ->
            val value = kept.getOrNull(index)
            if (value == null) editor.remove("$prefix.$index") else editor.putString("$prefix.$index", value)
        }
    }

    companion object {
        const val MAX_RECENT = 10

        val DEFAULT_OPTIONS = RenameOptions(search = "")

        private const val KEY_REMEMBER = "rememberOptions"
        private const val KEY_RECENT_SEARCH = "recent.search"
        private const val KEY_RECENT_REPLACE = "recent.replace"
        private const val OPTION_PREFIX = "option."
    }
}

/**
 * [RenameOptions] as the contract's option map (roadmap C.1 keys) with string values, and back through the plugin's
 * own option parser, so a stored value the parser refuses is dropped instead of reaching the engine.
 * zh-CN: 以契约选项键 (附录 C.1) 与字符串值表示的 [RenameOptions], 读回时经插件自身的选项解析器, 因此解析器拒绝的存储值会被丢弃而不会进入引擎.
 */
internal object OptionsCodec {

    private val BOOLEAN_KEYS = setOf(
        BatchRenameOptionKeys.REGEX, BatchRenameOptionKeys.MATCH_ALL, BatchRenameOptionKeys.CASE_SENSITIVE,
        BatchRenameOptionKeys.INCLUDE_FILES, BatchRenameOptionKeys.INCLUDE_FOLDERS, BatchRenameOptionKeys.INCLUDE_SUBFOLDERS,
        BatchRenameOptionKeys.ENUMERATE, BatchRenameOptionKeys.RANDOM, BatchRenameOptionKeys.HALF_WIDTH,
        BatchRenameOptionKeys.SANITIZE, BatchRenameOptionKeys.TRIM, BatchRenameOptionKeys.CONTINUE_NUMBERING,
        BatchRenameOptionKeys.PINYIN, BatchRenameOptionKeys.PINYIN_INITIALS,
    )

    fun encode(options: RenameOptions): Map<String, String> = linkedMapOf(
        BatchRenameOptionKeys.SEARCH to options.search,
        BatchRenameOptionKeys.REPLACE to options.replace,
        BatchRenameOptionKeys.REGEX to options.regex.toString(),
        BatchRenameOptionKeys.REGEX_FLAGS to options.regexFlags,
        BatchRenameOptionKeys.MATCH_ALL to options.matchAll.toString(),
        BatchRenameOptionKeys.CASE_SENSITIVE to options.caseSensitive.toString(),
        BatchRenameOptionKeys.TARGET to options.target.value,
        BatchRenameOptionKeys.INCLUDE_FILES to options.includeFiles.toString(),
        BatchRenameOptionKeys.INCLUDE_FOLDERS to options.includeFolders.toString(),
        BatchRenameOptionKeys.INCLUDE_SUBFOLDERS to options.includeSubfolders.toString(),
        BatchRenameOptionKeys.FORMAT to options.format.value,
        BatchRenameOptionKeys.ENUMERATE to options.enumerate.toString(),
        BatchRenameOptionKeys.RANDOM to options.random.toString(),
        BatchRenameOptionKeys.DATE_TIME to options.dateTime.value,
        BatchRenameOptionKeys.NORMALIZE to options.normalize.value,
        BatchRenameOptionKeys.HALF_WIDTH to options.halfWidth.toString(),
        BatchRenameOptionKeys.SANITIZE to options.sanitize.toString(),
        BatchRenameOptionKeys.SANITIZE_REPLACEMENT to options.sanitizeReplacement,
        BatchRenameOptionKeys.TRIM to options.trim.toString(),
        BatchRenameOptionKeys.CONTINUE_NUMBERING to options.continueNumbering.toString(),
        BatchRenameOptionKeys.ON_CONFLICT to options.onConflict.value,
        BatchRenameOptionKeys.CONFLICT_NUMBER_PATTERN to options.conflictNumberPattern,
        BatchRenameOptionKeys.PINYIN to options.pinyin.toString(),
        BatchRenameOptionKeys.PINYIN_TONE to options.pinyinTone.value,
        BatchRenameOptionKeys.PINYIN_SEPARATOR to options.pinyinSeparator.value,
        BatchRenameOptionKeys.PINYIN_CASE to options.pinyinCase.value,
        BatchRenameOptionKeys.PINYIN_INITIALS to options.pinyinInitials.toString(),
    )

    val KEYS: List<String> = encode(RenameOptions(search = "")).keys.toList()

    /** The options, or null when the map holds a value the parser refuses. zh-CN: 选项; 含解析器拒绝的值时为 null. */
    fun decode(values: Map<String, String>): RenameOptions? {
        val typed = typed(values + (BatchRenameOptionKeys.SEARCH to values[BatchRenameOptionKeys.SEARCH].orEmpty())) ?: return null
        return try {
            RenameOptionsParser.parse(typed).options
        } catch (_: PlanException) {
            null
        }
    }

    /**
     * Why the parser refuses [options] (a bad sanitize replacement or number pattern), or null when it accepts them.
     * zh-CN: 解析器拒绝 [options] 的原因 (不合法的替换字符或编号模式), 接受时为 null.
     */
    fun check(options: RenameOptions): PlanException? = try {
        RenameOptionsParser.parse(typed(encode(options))!!)
        null
    } catch (error: PlanException) {
        error
    }

    private fun typed(values: Map<String, String>): Map<String, Any?>? = values.mapValues { (key, value) ->
        if (key in BOOLEAN_KEYS) value.toBooleanStrictOrNull() ?: return null else value
    }
}
