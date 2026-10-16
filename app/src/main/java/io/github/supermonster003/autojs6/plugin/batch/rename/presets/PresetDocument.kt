package io.github.supermonster003.autojs6.plugin.batch.rename.presets

import io.github.supermonster003.autojs6.plugin.batch.rename.engine.PlanException
import io.github.supermonster003.autojs6.plugin.batch.rename.engine.RenameOptions
import io.github.supermonster003.autojs6.plugin.batch.rename.engine.RenameOptionsParser
import org.autojs.plugin.batchrename.api.BatchRenameLimits
import org.autojs.plugin.batchrename.api.BatchRenameOptionKeys
import org.autojs.plugin.batchrename.api.BatchRenameValues

/** A named set of complete options (roadmap D36). zh-CN: 带名称的一组完整选项 (路线图 D36). */
internal data class Preset(val name: String, val options: RenameOptions)

/** Why an entry is refused; the screen shows the kind, scripts get the message. zh-CN: 条目被拒的原因. */
internal enum class RejectionKind { NAME_MISSING, NAME_INVALID, DUPLICATE_NAME, OPTIONS_INVALID, TOO_MANY, TOO_LARGE }

internal data class PresetRejection(val index: Int, val name: String?, val kind: RejectionKind, val message: String)

/** What `importPresets` does with a name that is taken (roadmap D43). zh-CN: 名称已被占用时的导入策略 (路线图 D43). */
internal enum class PresetConflict(val value: String) {
    SKIP(BatchRenameValues.PRESET_CONFLICT_SKIP),
    OVERWRITE(BatchRenameValues.PRESET_CONFLICT_OVERWRITE),
    RENAME(BatchRenameValues.PRESET_CONFLICT_RENAME),
}

/** Preset names: trimmed, 1 to 64 characters, no control characters. zh-CN: 预设名称规则. */
internal object PresetNames {

    fun problem(name: String): RejectionKind? = when {
        name.isEmpty() -> RejectionKind.NAME_MISSING
        name != name.trim() || name.length > BatchRenameLimits.MAX_PRESET_NAME_CHARS -> RejectionKind.NAME_INVALID
        name.any { it < ' ' || it.code == 0x7f || it.code == 0x2028 || it.code == 0x2029 } -> RejectionKind.NAME_INVALID
        else -> null
    }

    fun message(kind: RejectionKind): String = when (kind) {
        RejectionKind.NAME_MISSING -> "the name is missing or blank"
        RejectionKind.NAME_INVALID -> "the name must be 1 to ${BatchRenameLimits.MAX_PRESET_NAME_CHARS} characters without control characters"
        RejectionKind.DUPLICATE_NAME -> "the name appears more than once"
        RejectionKind.OPTIONS_INVALID -> "the options are invalid"
        RejectionKind.TOO_MANY -> "the plugin keeps at most ${BatchRenameLimits.MAX_PRESETS} presets"
        RejectionKind.TOO_LARGE -> "the presets would exceed ${BatchRenameLimits.MAX_PRESET_DOCUMENT_CHARS} characters"
    }

    /**
     * The first free name of the form `name (n)`, shortening [name] to stay within the length limit.
     * zh-CN: 形如 `name (n)` 的第一个空闲名称, 必要时截短 [name] 以满足长度上限.
     */
    fun free(name: String, taken: Set<String>): String {
        var number = 1
        while (true) {
            val suffix = " ($number)"
            val candidate = name.take(BatchRenameLimits.MAX_PRESET_NAME_CHARS - suffix.length).trimEnd() + suffix
            if (candidate !in taken) return candidate
            number++
        }
    }
}

/**
 * The JSON document of exported, imported and stored presets (roadmap D36 / D45):
 * `{"format": "autojs6-batch-rename-presets", "version": 1, "presets": [{"name": ..., "options": {...}}]}` with the
 * contract option keys. Decoding checks the document as a whole and every entry on its own: missing option keys take
 * their defaults, unknown keys are dropped, and an entry with a bad name or bad option values is refused alone.
 * zh-CN: 导出, 导入与存储预设所用的 JSON 文档 (路线图 D36 / D45), 选项使用契约键. 解码时整体检查文档并逐项检查条目: 缺省的选项键
 * 取默认值, 未知键被丢弃, 名称或选项值不合法的条目单独被拒.
 */
internal object PresetDocument {

    const val FORMAT = "autojs6-batch-rename-presets"
    const val VERSION = 1

    data class Entry(val index: Int, val preset: Preset)

    data class Decoded(val entries: List<Entry>, val rejected: List<PresetRejection>)

    /** Pretty-printed with two-space indents and LF line ends. zh-CN: 两空格缩进, LF 换行. */
    fun encode(presets: List<Preset>): String = buildString {
        append("{\n  \"format\": ").append(Json.quote(FORMAT)).append(",\n  \"version\": ").append(VERSION).append(",\n  \"presets\": [")
        presets.forEachIndexed { index, preset ->
            append(if (index == 0) "\n" else ",\n")
            append("    {\n      \"name\": ").append(Json.quote(preset.name)).append(",\n      \"options\": {")
            RenameOptionsParser.values(preset.options).entries.forEachIndexed { position, (key, value) ->
                append(if (position == 0) "\n" else ",\n")
                append("        ").append(Json.quote(key)).append(": ")
                append(if (value is Boolean) value.toString() else Json.quote(value.toString()))
            }
            append("\n      }\n    }")
        }
        append(if (presets.isEmpty()) "]\n}\n" else "\n  ]\n}\n")
    }

    /**
     * @throws PlanException with `LIMIT_EXCEEDED` for a document longer than the limit and `INVALID_OPTIONS` for text
     *   that is not a preset document of a supported version.
     */
    fun decode(text: String): Decoded {
        if (text.length > BatchRenameLimits.MAX_PRESET_DOCUMENT_CHARS) {
            throw PlanException.limitExceeded("The preset document exceeds ${BatchRenameLimits.MAX_PRESET_DOCUMENT_CHARS} characters")
        }
        val root = try {
            Json.parse(text.removePrefix(BYTE_ORDER_MARK))
        } catch (error: Json.SyntaxException) {
            throw PlanException.invalidOptions("The preset document is not JSON: ${error.message}")
        }
        val document = root as? Map<*, *> ?: throw PlanException.invalidOptions("The preset document must be a JSON object")
        if (document["format"] != FORMAT) throw PlanException.invalidOptions("The preset document must have \"format\": \"$FORMAT\"")
        val version = document["version"]
        if (version !is Long || version < 1) throw PlanException.invalidOptions("The preset document must have a positive integer \"version\"")
        if (version > VERSION) throw PlanException.invalidOptions("Preset document version $version is newer than the supported version $VERSION")
        val list = document["presets"] as? List<*> ?: throw PlanException.invalidOptions("The preset document must have a \"presets\" array")
        val entries = mutableListOf<Entry>()
        val rejected = mutableListOf<PresetRejection>()
        val seen = mutableSetOf<String>()
        list.forEachIndexed { index, element ->
            val entry = element as? Map<*, *>
            val name = (entry?.get("name") as? String)?.trim()
            fun reject(kind: RejectionKind, detail: String = PresetNames.message(kind)) {
                rejected += PresetRejection(index, name, kind, detail)
            }
            val nameProblem = if (name == null) RejectionKind.NAME_MISSING else PresetNames.problem(name)
            val options = entry?.get("options") as? Map<*, *>
            when {
                entry == null -> rejected += PresetRejection(index, null, RejectionKind.NAME_MISSING, "the entry must be an object")
                nameProblem != null -> reject(nameProblem)
                name!! in seen -> reject(RejectionKind.DUPLICATE_NAME)
                options == null -> reject(RejectionKind.OPTIONS_INVALID, "the entry must have an \"options\" object")
                else -> try {
                    entries += Entry(index, Preset(name, parseOptions(options)))
                    seen += name
                } catch (error: PlanException) {
                    reject(RejectionKind.OPTIONS_INVALID, error.message ?: PresetNames.message(RejectionKind.OPTIONS_INVALID))
                }
            }
        }
        return Decoded(entries, rejected)
    }

    /**
     * Options of one entry: the contract keys with their defaults, unknown keys and `preset` dropped.
     * @throws PlanException with `INVALID_OPTIONS` or `LIMIT_EXCEEDED`.
     */
    fun parseOptions(values: Map<*, *>): RenameOptions {
        val known = RenameOptionsParser.values(RenameOptions(search = "")).keys
        val map = values.entries.mapNotNull { (key, value) -> (key as? String)?.takeIf { it in known }?.let { it to value } }.toMap()
        return RenameOptionsParser.parse(mapOf(BatchRenameOptionKeys.SEARCH to "") + map).options
    }

    private val BYTE_ORDER_MARK = String(charArrayOf(0xfeff.toChar()))
}
