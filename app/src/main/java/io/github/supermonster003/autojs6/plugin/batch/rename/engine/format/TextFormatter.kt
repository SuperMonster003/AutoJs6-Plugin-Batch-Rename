package io.github.supermonster003.autojs6.plugin.batch.rename.engine.format

import io.github.supermonster003.autojs6.plugin.batch.rename.engine.TextFormat
import java.util.Locale

/**
 * The text formatting of PowerRename (roadmap D18) with locale-aware case mapping (`i` becomes `İ` in Turkish):
 * `title` capitalizes the first character of the whole text and leaves the rest as it is; `capitalize` capitalizes the
 * first character of every word separated by white space. Code points are handled whole, so surrogate pairs survive.
 * zh-CN: PowerRename 的文本格式化 (路线图 D18), 大小写映射跟随区域 (土耳其语中 `i` 变为 `İ`): `title` 只把整段文本的首字符
 * 大写, 其余不变; `capitalize` 把每个以空白分隔的词的首字符大写. 按码点处理, 代理对不会被拆开.
 */
internal object TextFormatter {

    fun format(text: String, format: TextFormat, locale: Locale): String = when (format) {
        TextFormat.NONE -> text
        TextFormat.LOWER -> text.lowercase(locale)
        TextFormat.UPPER -> text.uppercase(locale)
        TextFormat.TITLE -> capitalizeAt(text, 0, locale)
        TextFormat.CAPITALIZE -> capitalizeWords(text, locale)
    }

    private fun capitalizeWords(text: String, locale: Locale): String {
        val result = StringBuilder(text.length)
        var wordStart = true
        var index = 0
        while (index < text.length) {
            val codePoint = text.codePointAt(index)
            val char = String(Character.toChars(codePoint))
            if (Character.isWhitespace(codePoint) || Character.isSpaceChar(codePoint)) {
                result.append(char)
                wordStart = true
            } else {
                result.append(if (wordStart) char.uppercase(locale) else char)
                wordStart = false
            }
            index += Character.charCount(codePoint)
        }
        return result.toString()
    }

    private fun capitalizeAt(text: String, index: Int, locale: Locale): String {
        if (index >= text.length) return text
        val end = index + Character.charCount(text.codePointAt(index))
        return text.substring(0, index) + text.substring(index, end).uppercase(locale) + text.substring(end)
    }
}
