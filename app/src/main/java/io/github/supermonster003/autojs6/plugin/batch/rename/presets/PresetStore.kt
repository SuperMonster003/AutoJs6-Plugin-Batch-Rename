package io.github.supermonster003.autojs6.plugin.batch.rename.presets

import io.github.supermonster003.autojs6.plugin.batch.rename.engine.PlanException
import io.github.supermonster003.autojs6.plugin.batch.rename.engine.RenameOptions
import io.github.supermonster003.autojs6.plugin.batch.rename.engine.RenameOptionsParser
import io.github.supermonster003.autojs6.plugin.batch.rename.settings.KeyValueStore
import org.autojs.plugin.batchrename.api.BatchRenameLimits

/** The outcome of an import (roadmap D43). zh-CN: 一次导入的结果 (路线图 D43). */
internal data class PresetImport(val imported: List<String>, val skipped: List<String>, val rejected: List<PresetRejection>)

/**
 * The plugin's presets (roadmap D36), kept as one [PresetDocument] in the plugin's preferences. The screen and the
 * capability service share it within one process; every change reads, checks and writes under one lock. At most
 * [BatchRenameLimits.MAX_PRESETS] presets of [BatchRenameLimits.MAX_PRESET_DOCUMENT_CHARS] characters in all.
 * zh-CN: 插件的预设 (路线图 D36), 以一个 [PresetDocument] 存于插件偏好. 界面与能力服务在同一进程内共用; 每次修改都在同一把锁下
 * 读取, 检查并写回. 最多 [BatchRenameLimits.MAX_PRESETS] 个预设, 合计不超过 [BatchRenameLimits.MAX_PRESET_DOCUMENT_CHARS] 个字符.
 */
internal class PresetStore(private val store: KeyValueStore) {

    fun list(): List<Preset> = synchronized(LOCK) { read() }

    fun find(name: String): Preset? = list().firstOrNull { it.name == name }

    /**
     * Saves [options] under [name], replacing a preset of that name in place. Options the parser refuses (a number
     * pattern without `{n}`, for example) are not saved, since the preset could not be read back.
     * @return null when saved, otherwise why not.
     */
    fun save(name: String, options: RenameOptions): RejectionKind? = synchronized(LOCK) {
        PresetNames.problem(name)?.let { return it }
        try {
            RenameOptionsParser.parse(RenameOptionsParser.values(options))
        } catch (_: PlanException) {
            return RejectionKind.OPTIONS_INVALID
        }
        val presets = read().toMutableList()
        val index = presets.indexOfFirst { it.name == name }
        if (index >= 0) presets[index] = Preset(name, options) else presets += Preset(name, options)
        check(presets) ?: write(presets).let { null }
    }

    /** @return null when renamed (or unchanged), otherwise why not. */
    fun rename(from: String, to: String): RejectionKind? = synchronized(LOCK) {
        PresetNames.problem(to)?.let { return it }
        val presets = read().toMutableList()
        val index = presets.indexOfFirst { it.name == from }
        if (index < 0 || from == to) return null
        if (presets.any { it.name == to }) return RejectionKind.DUPLICATE_NAME
        presets[index] = presets[index].copy(name = to)
        check(presets) ?: write(presets).let { null }
    }

    fun delete(names: Collection<String>) = synchronized(LOCK) {
        val presets = read()
        val kept = presets.filter { it.name !in names }
        if (kept.size != presets.size) write(kept)
    }

    /** The document of the named presets in the store's order. zh-CN: 按存储顺序导出指定预设的文档. */
    fun export(names: Collection<String>): String = PresetDocument.encode(list().filter { it.name in names })

    /**
     * Imports the entries of a decoded document in order: refused entries stay out, a taken name follows [onConflict],
     * and entries that would break the count or size limit are refused.
     * zh-CN: 按顺序导入已解码文档的条目: 被拒的条目不导入, 名称被占用时按 [onConflict] 处理, 会超出数量或大小上限的条目被拒.
     */
    fun import(decoded: PresetDocument.Decoded, onConflict: PresetConflict): PresetImport = synchronized(LOCK) {
        val presets = read().toMutableList()
        val imported = mutableListOf<String>()
        val skipped = mutableListOf<String>()
        val rejected = decoded.rejected.toMutableList()
        decoded.entries.forEach { (index, preset) ->
            val existing = presets.indexOfFirst { it.name == preset.name }
            val candidate = presets.toMutableList()
            val stored = when {
                existing < 0 -> preset.also { candidate += it }
                onConflict == PresetConflict.SKIP -> {
                    skipped += preset.name
                    return@forEach
                }
                onConflict == PresetConflict.OVERWRITE -> preset.also { candidate[existing] = it }
                else -> preset.copy(name = PresetNames.free(preset.name, presets.map { it.name }.toSet())).also { candidate += it }
            }
            val problem = check(candidate)
            if (problem != null) {
                rejected += PresetRejection(index, preset.name, problem, PresetNames.message(problem))
            } else {
                presets.clear()
                presets += candidate
                imported += stored.name
            }
        }
        if (imported.isNotEmpty()) write(presets)
        PresetImport(imported, skipped, rejected.sortedBy { it.index })
    }

    private fun check(presets: List<Preset>): RejectionKind? = when {
        presets.size > BatchRenameLimits.MAX_PRESETS -> RejectionKind.TOO_MANY
        PresetDocument.encode(presets).length > BatchRenameLimits.MAX_PRESET_DOCUMENT_CHARS -> RejectionKind.TOO_LARGE
        else -> null
    }

    /** A stored document this build cannot read (it was never written by it) counts as empty. zh-CN: 无法读取的存储视为空. */
    private fun read(): List<Preset> {
        val text = store.getString(KEY) ?: return emptyList()
        return try {
            PresetDocument.decode(text).entries.map { it.preset }
        } catch (_: Exception) {
            emptyList()
        }
    }

    private fun write(presets: List<Preset>) = store.edit {
        if (presets.isEmpty()) remove(KEY) else putString(KEY, PresetDocument.encode(presets))
    }

    companion object {
        const val KEY = "presets"
        private val LOCK = Any()
    }
}
