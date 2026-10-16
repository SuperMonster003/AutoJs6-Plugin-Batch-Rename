package io.github.supermonster003.autojs6.plugin.batch.rename.presets

import java.util.Locale

/**
 * A small strict JSON reader and writer for preset documents (RFC 8259): objects keep their member order and refuse
 * duplicate names, numbers become Long or Double, and the text holds exactly one value. The plugin carries no JSON
 * library, and the same code runs in JVM tests and on devices.
 * zh-CN: 供预设文档使用的小型严格 JSON 读写器 (RFC 8259): 对象保持成员顺序并拒绝重复成员名, 数值为 Long 或 Double, 文本必须
 * 恰好包含一个值. 插件不带 JSON 库, JVM 测试与设备运行同一份代码.
 */
internal object Json {

    class SyntaxException(message: String) : Exception(message)

    private const val MAX_DEPTH = 32
    private val BACKSLASH = 92.toChar()

    /**
     * Objects become `LinkedHashMap<String, Any?>`, arrays `List<Any?>`.
     * @throws SyntaxException when [text] is not one JSON value.
     */
    fun parse(text: String): Any? {
        val reader = Reader(text)
        reader.skipSpace()
        val value = reader.value(0)
        reader.skipSpace()
        if (!reader.atEnd) reader.fail("unexpected text after the value")
        return value
    }

    /** [text] as a JSON string literal; line separators are escaped as well. zh-CN: [text] 的 JSON 字符串字面量. */
    fun quote(text: String): String = buildString(text.length + 2) {
        append('"')
        text.forEach { char ->
            when {
                char == '"' || char == BACKSLASH -> append(BACKSLASH).append(char)
                char == '\n' -> append(BACKSLASH).append('n')
                char == '\r' -> append(BACKSLASH).append('r')
                char == '\t' -> append(BACKSLASH).append('t')
                char < ' ' || char.code == 0x2028 || char.code == 0x2029 ->
                    append(BACKSLASH).append('u').append(String.format(Locale.ROOT, "%04x", char.code))
                else -> append(char)
            }
        }
        append('"')
    }

    private class Reader(private val text: String) {

        private var index = 0

        val atEnd: Boolean get() = index >= text.length

        fun fail(message: String): Nothing = throw SyntaxException("$message at character $index")

        fun skipSpace() {
            while (index < text.length && (text[index] == ' ' || text[index] == '\t' || text[index] == '\r' || text[index] == '\n')) index++
        }

        fun value(depth: Int): Any? {
            if (depth > MAX_DEPTH) fail("nesting deeper than $MAX_DEPTH levels")
            if (atEnd) fail("unexpected end of text")
            val char = text[index]
            return when {
                char == '{' -> objectValue(depth)
                char == '[' -> arrayValue(depth)
                char == '"' -> stringValue()
                char == 't' -> literal("true", true)
                char == 'f' -> literal("false", false)
                char == 'n' -> literal("null", null)
                char == '-' || char in '0'..'9' -> numberValue()
                else -> fail("unexpected character '$char'")
            }
        }

        private fun objectValue(depth: Int): Map<String, Any?> {
            index++
            val result = LinkedHashMap<String, Any?>()
            skipSpace()
            if (next('}')) return result
            while (true) {
                skipSpace()
                if (atEnd || text[index] != '"') fail("expected a member name")
                val name = stringValue()
                if (result.containsKey(name)) fail("duplicate member \"$name\"")
                skipSpace()
                if (!next(':')) fail("expected ':'")
                skipSpace()
                result[name] = value(depth + 1)
                skipSpace()
                if (next(',')) continue
                if (next('}')) return result
                fail("expected ',' or '}'")
            }
        }

        private fun arrayValue(depth: Int): List<Any?> {
            index++
            val result = ArrayList<Any?>()
            skipSpace()
            if (next(']')) return result
            while (true) {
                skipSpace()
                result += value(depth + 1)
                skipSpace()
                if (next(',')) continue
                if (next(']')) return result
                fail("expected ',' or ']'")
            }
        }

        private fun stringValue(): String {
            index++
            val result = StringBuilder()
            while (true) {
                if (atEnd) fail("unterminated string")
                val char = text[index++]
                when {
                    char == '"' -> return result.toString()
                    char == BACKSLASH -> result.append(escape())
                    char < ' ' -> fail("control character in a string")
                    else -> result.append(char)
                }
            }
        }

        private fun escape(): Char {
            if (atEnd) fail("unterminated escape")
            return when (val char = text[index++]) {
                '"', '/', BACKSLASH -> char
                'b' -> 8.toChar()
                'f' -> 12.toChar()
                'n' -> 10.toChar()
                'r' -> 13.toChar()
                't' -> 9.toChar()
                'u' -> {
                    val hex = text.substring(index, minOf(index + 4, text.length))
                    if (hex.length != 4 || !hex.all { it in '0'..'9' || it in 'a'..'f' || it in 'A'..'F' }) fail("bad unicode escape")
                    index += 4
                    hex.toInt(16).toChar()
                }
                else -> fail("unknown escape '$char'")
            }
        }

        private fun numberValue(): Any {
            val start = index
            next('-')
            if (next('0')) {
                if (!atEnd && text[index] in '0'..'9') fail("leading zero")
            } else if (!digits()) {
                fail("expected a digit")
            }
            var integer = true
            if (next('.')) {
                integer = false
                if (!digits()) fail("expected a digit")
            }
            if (next('e') || next('E')) {
                integer = false
                if (!next('+')) next('-')
                if (!digits()) fail("expected a digit")
            }
            val token = text.substring(start, index)
            return if (integer) token.toLongOrNull() ?: token.toDouble() else token.toDouble()
        }

        private fun digits(): Boolean {
            val start = index
            while (!atEnd && text[index] in '0'..'9') index++
            return index > start
        }

        private fun literal(word: String, value: Any?): Any? {
            if (!text.startsWith(word, index)) fail("unexpected character '${text[index]}'")
            index += word.length
            return value
        }

        private fun next(char: Char): Boolean {
            if (atEnd || text[index] != char) return false
            index++
            return true
        }
    }
}
