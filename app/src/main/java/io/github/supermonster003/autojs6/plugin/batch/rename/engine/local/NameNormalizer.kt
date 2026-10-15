package io.github.supermonster003.autojs6.plugin.batch.rename.engine.local

import io.github.supermonster003.autojs6.plugin.batch.rename.engine.NameCharacters
import io.github.supermonster003.autojs6.plugin.batch.rename.engine.Normalization
import io.github.supermonster003.autojs6.plugin.batch.rename.engine.RenameOptions
import java.text.Normalizer
import java.util.Locale

/**
 * The local extensions of roadmap D5 / D19 in pipeline order (D16): Unicode normalization, full-width to half-width
 * conversion, sanitizing and trimming. Each step is off unless its option is on.
 * zh-CN: 路线图 D5 / D19 的本土扩展, 按流水线顺序 (D16) 执行: Unicode 规范化, 全角转半角, 清理, 修剪. 每一步只在对应选项开启时生效.
 */
internal object NameNormalizer {

    private val RESERVED_WINDOWS_NAMES: Set<String> =
        setOf("CON", "PRN", "AUX", "NUL") + (1..9).map { "COM$it" } + (1..9).map { "LPT$it" }

    /**
     * @param holdsStem whether [text] begins with the name stem (the `name` and `all` targets), where Windows device
     *   names such as `CON` are checked.
     */
    fun apply(text: String, options: RenameOptions, holdsStem: Boolean): String {
        var result = text
        result = when (options.normalize) {
            Normalization.NONE -> result
            Normalization.NFC -> Normalizer.normalize(result, Normalizer.Form.NFC)
            Normalization.NFD -> Normalizer.normalize(result, Normalizer.Form.NFD)
        }
        if (options.halfWidth) result = toHalfWidth(result)
        if (options.sanitize) result = sanitize(result, options.sanitizeReplacement, holdsStem)
        if (options.trim) result = trim(result)
        return result
    }

    /** U+FF01..U+FF5E to U+0021..U+007E and U+3000 to U+0020. zh-CN: 全角字符与全角空格转为半角. */
    fun toHalfWidth(text: String): String = buildString(text.length) {
        text.forEach { char ->
            append(
                when (char) {
                    in '\uff01'..'\uff5e' -> char - 0xfee0
                    '\u3000' -> ' '
                    else -> char
                },
            )
        }
    }

    /**
     * Replaces `\ : * ? " < > |` and control characters with [replacement]; a Windows device name before the first dot
     * (`CON`, `com1.txt`) gets [replacement] appended, or `_` when the replacement is empty.
     * zh-CN: 把 `\ : * ? " < > |` 与控制字符替换为 [replacement]; 首个点号之前为 Windows 设备名 (`CON`, `com1.txt`) 时在其后追加
     * [replacement], 替换串为空时追加 `_`.
     */
    fun sanitize(text: String, replacement: String, holdsStem: Boolean): String {
        val cleaned = buildString(text.length) {
            text.forEach { char ->
                if (char in NameCharacters.RESTRICTED || char < ' ' || char == '\u007f') append(replacement) else append(char)
            }
        }
        if (!holdsStem) return cleaned
        val baseEnd = cleaned.indexOf('.').takeIf { it >= 0 } ?: cleaned.length
        val base = cleaned.substring(0, baseEnd).trimEnd()
        if (base.uppercase(Locale.ROOT) !in RESERVED_WINDOWS_NAMES) return cleaned
        val suffix = replacement.ifEmpty { "_" }
        return cleaned.substring(0, base.length) + suffix + cleaned.substring(base.length)
    }

    /** Removes leading white space and trailing white space and dots. zh-CN: 去除开头的空白以及结尾的空白与点号. */
    fun trim(text: String): String {
        var start = 0
        while (start < text.length && isBlank(text[start])) start++
        var end = text.length
        while (end > start && (isBlank(text[end - 1]) || text[end - 1] == '.')) end--
        return text.substring(start, end)
    }

    private fun isBlank(char: Char): Boolean = Character.isWhitespace(char) || Character.isSpaceChar(char)
}
