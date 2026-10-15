package io.github.supermonster003.autojs6.plugin.batch.rename.engine.validate

import io.github.supermonster003.autojs6.plugin.batch.rename.engine.NameCharacters
import io.github.supermonster003.autojs6.plugin.batch.rename.engine.PlanReason
import org.autojs.plugin.batchrename.api.BatchRenameLimits

/**
 * The rules every resulting name must pass (roadmap D19): not empty, not `.` or `..`, a single path segment without
 * NUL, at most 255 UTF-8 bytes (ext4 / f2fs), and on FAT32 / exFAT directories none of `\ : * ? " < > |` or control
 * characters.
 * zh-CN: 每个结果名称都必须满足的规则 (路线图 D19): 非空, 不是 `.` 或 `..`, 是不含 NUL 的单个路径段, 至多 255 个 UTF-8 字节
 * (ext4 / f2fs), 在 FAT32 / exFAT 目录中不含 `\ : * ? " < > |` 与控制字符.
 */
internal object NameValidator {

    /** The reason [name] is invalid, or null when it is valid. zh-CN: [name] 无效的原因, 有效时为 null. */
    fun validate(name: String, restrictedCharacters: Boolean): PlanReason? = when {
        name.isEmpty() -> PlanReason.EMPTY
        name == "." || name == ".." -> PlanReason.DOT_NAME
        '/' in name || '\u0000' in name -> PlanReason.SEPARATOR
        utf8Length(name) > BatchRenameLimits.MAX_RESULT_NAME_BYTES -> PlanReason.TOO_LONG
        restrictedCharacters && name.any { it in NameCharacters.RESTRICTED || it < ' ' || it == '\u007f' } -> PlanReason.RESTRICTED_CHARACTER
        else -> null
    }

    /** UTF-8 length without allocating; lone surrogates count as the 3 bytes of U+FFFD. */
    fun utf8Length(text: String): Int {
        var bytes = 0
        var index = 0
        while (index < text.length) {
            val char = text[index]
            bytes += when {
                char.code < 0x80 -> 1
                char.code < 0x800 -> 2
                char.isHighSurrogate() && index + 1 < text.length && text[index + 1].isLowSurrogate() -> {
                    index++
                    4
                }
                else -> 3
            }
            index++
        }
        return bytes
    }
}
