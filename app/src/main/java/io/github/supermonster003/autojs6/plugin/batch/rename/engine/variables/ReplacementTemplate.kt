package io.github.supermonster003.autojs6.plugin.batch.rename.engine.variables

import io.github.supermonster003.autojs6.plugin.batch.rename.engine.PlanException
import org.autojs.plugin.batchrename.api.BatchRenameLimits

/**
 * The `replace` option split into literal text and PowerRename variables (roadmap D5 / D16, appendix A): counters
 * `${...}`, the 20 date and time variables `$YYYY` ... `$f` and the random values `${rstring...=N}` / `${ruuidv4}`.
 *
 * - `$$` is a literal `$` in both search modes and keeps the following text from being read as a variable (`$$YYYY`
 *   stays `$YYYY`).
 * - A variable whose option is off, and anything that does not parse, stays literal text, like in PowerRename.
 * - Date variables are case sensitive and the longest name wins (`$MMM` before `$MM`, `$Mm` is `$M` plus `m`).
 *
 * zh-CN: 把 `replace` 选项拆分为字面文本与 PowerRename 变量 (路线图 D5 / D16, 附录 A): 计数器 `${...}`, 20 个日期时间变量
 * `$YYYY` ... `$f`, 以及随机值 `${rstring...=N}` / `${ruuidv4}`. `$$` 在两种查找模式下都表示一个字面 `$`, 并使其后文本不被
 * 识别为变量 (`$$YYYY` 保持为 `$YYYY`). 选项关闭的变量以及无法解析的内容都保持为字面文本, 与 PowerRename 一致. 日期变量区分
 * 大小写且最长名称优先 (`$MMM` 先于 `$MM`, `$Mm` 为 `$M` 加 `m`).
 */
internal class ReplacementTemplate private constructor(val parts: List<TemplatePart>) {

    val hasCounters: Boolean = parts.any { it is TemplatePart.Counter }

    val hasVariables: Boolean = parts.any { it !is TemplatePart.Literal && it !is TemplatePart.Dollar }

    companion object {

        /**
         * @throws PlanException with `LIMIT_EXCEEDED` for a random string longer than [BatchRenameLimits.MAX_RANDOM_LENGTH].
         */
        fun parse(replace: String, enumerate: Boolean, random: Boolean, dateTime: Boolean): ReplacementTemplate {
            val parts = mutableListOf<TemplatePart>()
            val literal = StringBuilder()
            fun flushLiteral() {
                if (literal.isNotEmpty()) parts += TemplatePart.Literal(literal.toString())
                literal.setLength(0)
            }
            var index = 0
            while (index < replace.length) {
                val char = replace[index]
                if (char != '$') {
                    literal.append(char)
                    index++
                    continue
                }
                if (replace.startsWith("$$", index)) {
                    flushLiteral()
                    parts += TemplatePart.Dollar
                    index += 2
                    continue
                }
                val braced = if (replace.startsWith("\${", index)) parseBraced(replace, index, enumerate, random) else null
                if (braced != null) {
                    flushLiteral()
                    parts += braced.first
                    index = braced.second
                    continue
                }
                val field = if (dateTime) DateField.matchAt(replace, index + 1) else null
                if (field != null) {
                    flushLiteral()
                    parts += TemplatePart.Date(field)
                    index += 1 + field.token.length
                    continue
                }
                literal.append(char)
                index++
            }
            flushLiteral()
            return ReplacementTemplate(parts)
        }

        /** Parses `${...}` at [start]; returns the part and the index after `}`, or null for literal text. */
        private fun parseBraced(text: String, start: Int, enumerate: Boolean, random: Boolean): Pair<TemplatePart, Int>? {
            val close = text.indexOf('}', start + 2)
            if (close < 0) return null
            val body = text.substring(start + 2, close)
            val part = (if (enumerate) CounterSpec.parse(body)?.let(TemplatePart::Counter) else null)
                ?: (if (random) parseRandom(body) else null)
                ?: return null
            return part to close + 1
        }

        private fun parseRandom(body: String): TemplatePart.Random? {
            if (body == "ruuidv4") return TemplatePart.Random(RandomKind.UUID_V4, 0)
            val kind = RandomKind.entries.firstOrNull { it.key != null && body.startsWith("${it.key}=") } ?: return null
            val digits = body.substringAfter('=')
            if (digits.isEmpty() || digits.length > 9 || !digits.all { it in '0'..'9' }) return null
            val length = digits.toInt()
            if (length == 0) return null
            if (length > BatchRenameLimits.MAX_RANDOM_LENGTH) {
                throw PlanException.limitExceeded("\${${kind.key}=$length} exceeds ${BatchRenameLimits.MAX_RANDOM_LENGTH} characters")
            }
            return TemplatePart.Random(kind, length)
        }
    }
}

internal sealed interface TemplatePart {
    /** Text the user wrote; in regex mode it may hold `$1`, `$<name>` and the like. zh-CN: 用户书写的文本. */
    data class Literal(val text: String) : TemplatePart

    /** `$$`: one literal `$`. zh-CN: `$$`: 一个字面 `$`. */
    data object Dollar : TemplatePart

    data class Counter(val spec: CounterSpec) : TemplatePart

    data class Date(val field: DateField) : TemplatePart

    data class Random(val kind: RandomKind, val length: Int) : TemplatePart
}

/**
 * `${}` or `${start=X;increment=X;padding=X}` with the keys in any order (PowerRename). Start and increment are
 * integers of at most 15 digits, padding is 0 to 255 digits; anything else is not a counter.
 * zh-CN: `${}` 或 `${start=X;increment=X;padding=X}`, 键的顺序任意 (PowerRename). 起始值与增量为至多 15 位的整数, 填充为
 * 0 到 255 位; 其他内容都不是计数器.
 */
internal data class CounterSpec(val start: Long = 0, val increment: Long = 1, val padding: Int = 0) {

    /**
     * The value for the item that is renamed after [renamedBefore] others.
     * zh-CN: 在 [renamedBefore] 项之后改名的条目的值.
     *
     * @throws PlanException with `INVALID_OPTIONS` when the value leaves the range of a 64-bit integer.
     */
    fun valueAt(renamedBefore: Long): Long = try {
        Math.addExact(start, Math.multiplyExact(renamedBefore, increment))
    } catch (_: ArithmeticException) {
        throw PlanException.invalidOptions("A counter leaves the range of a 64-bit integer")
    }

    fun format(value: Long): String {
        val digits = value.toString().removePrefix("-")
        val padded = if (digits.length >= padding) digits else "0".repeat(padding - digits.length) + digits
        return if (value < 0) "-$padded" else padded
    }

    companion object {
        private val INTEGER = Regex("-?[0-9]{1,15}")
        private val PADDING = Regex("[0-9]{1,3}")
        const val MAX_PADDING = 255

        fun parse(body: String): CounterSpec? {
            if (body.isEmpty()) return CounterSpec()
            var spec = CounterSpec()
            val seen = mutableSetOf<String>()
            for (entry in body.split(';')) {
                val key = entry.substringBefore('=', "")
                val value = entry.substringAfter('=', "")
                if (!seen.add(key)) return null
                spec = when (key) {
                    "start" -> if (INTEGER.matches(value)) spec.copy(start = value.toLong()) else return null
                    "increment" -> if (INTEGER.matches(value)) spec.copy(increment = value.toLong()) else return null
                    "padding" -> if (PADDING.matches(value) && value.toInt() <= MAX_PADDING) spec.copy(padding = value.toInt()) else return null
                    else -> return null
                }
            }
            return spec
        }
    }
}

/** The 20 date and time variables of PowerRename, longest token first. zh-CN: PowerRename 的 20 个日期时间变量, 长名称在前. */
internal enum class DateField(val token: String) {
    YEAR("YYYY"), YEAR_TWO_DIGITS("YY"), YEAR_LAST_DIGIT("Y"),
    MONTH_NAME("MMMM"), MONTH_SHORT_NAME("MMM"), MONTH_TWO_DIGITS("MM"), MONTH("M"),
    DAY_NAME("DDDD"), DAY_SHORT_NAME("DDD"), DAY_TWO_DIGITS("DD"), DAY("D"),
    HOUR_TWO_DIGITS("hh"), HOUR("h"),
    MINUTE_TWO_DIGITS("mm"), MINUTE("m"),
    SECOND_TWO_DIGITS("ss"), SECOND("s"),
    MILLIS("fff"), MILLIS_TWO_DIGITS("ff"), MILLIS_ONE_DIGIT("f");

    companion object {
        private val BY_LENGTH = entries.sortedByDescending { it.token.length }

        fun matchAt(text: String, index: Int): DateField? = BY_LENGTH.firstOrNull { text.startsWith(it.token, index) }
    }
}

internal enum class RandomKind(val key: String?, val alphabet: String) {
    ALNUM("rstringalnum", "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789"),
    ALPHA("rstringalpha", "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz"),
    DIGIT("rstringdigit", "0123456789"),
    UUID_V4(null, ""),
}
