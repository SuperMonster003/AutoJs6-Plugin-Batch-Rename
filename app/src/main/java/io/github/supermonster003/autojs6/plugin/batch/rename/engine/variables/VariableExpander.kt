package io.github.supermonster003.autojs6.plugin.batch.rename.engine.variables

import io.github.supermonster003.autojs6.plugin.batch.rename.engine.regex.JsRegexSyntax
import java.security.SecureRandom
import java.time.DayOfWeek
import java.time.Instant
import java.time.Month
import java.time.ZoneId
import java.time.format.TextStyle
import java.util.Locale
import java.util.UUID

/**
 * Turns a [ReplacementTemplate] into the ECMAScript replacement string for one item (roadmap D16): variable values are
 * escaped (`$` becomes `$$`), literal text keeps its `$1` / `$<name>` meaning in regex mode and is escaped in plain-text
 * mode, and `$$` stays one literal `$`.
 * zh-CN: 把 [ReplacementTemplate] 转为单个条目的 ECMAScript 替换串 (路线图 D16): 变量值被转义 (`$` 变为 `$$`), 字面文本在正则
 * 模式下保留 `$1` / `$<name>` 的含义, 在纯文本模式下被转义, `$$` 始终是一个字面 `$`.
 */
internal class VariableExpander(
    private val template: ReplacementTemplate,
    private val regexMode: Boolean,
    private val dates: DateFieldFormatter,
    private val random: RandomSource,
) {

    /**
     * @param dateMillis epoch milliseconds of the date variables (modification time or `now`).
     * @param renamedBefore items renamed before this one, plus the continued numbering offset.
     */
    fun expand(dateMillis: Long, renamedBefore: Long): String = buildString {
        template.parts.forEach { part ->
            when (part) {
                is TemplatePart.Literal -> append(if (regexMode) part.text else JsRegexSyntax.escapeReplacement(part.text))
                TemplatePart.Dollar -> append("$$")
                is TemplatePart.Counter -> append(JsRegexSyntax.escapeReplacement(part.spec.format(part.spec.valueAt(renamedBefore))))
                is TemplatePart.Date -> append(JsRegexSyntax.escapeReplacement(dates.format(part.field, dateMillis)))
                is TemplatePart.Random -> append(JsRegexSyntax.escapeReplacement(random.next(part.kind, part.length)))
            }
        }
    }
}

/**
 * Formats the date variables like PowerRename: 24-hour clock, Gregorian year, names in the request locale.
 * zh-CN: 像 PowerRename 一样格式化日期变量: 24 小时制, 公历年份, 名称使用请求的区域.
 */
internal class DateFieldFormatter(private val locale: Locale, private val zone: ZoneId) {

    fun format(field: DateField, epochMillis: Long): String {
        val time = Instant.ofEpochMilli(epochMillis).atZone(zone)
        val millis = time.nano / 1_000_000
        return when (field) {
            DateField.YEAR -> time.year.toString().padStart(4, '0')
            DateField.YEAR_TWO_DIGITS -> Math.floorMod(time.year, 100).toString().padStart(2, '0')
            DateField.YEAR_LAST_DIGIT -> Math.floorMod(time.year, 10).toString()
            DateField.MONTH_NAME -> monthName(time.month, TextStyle.FULL_STANDALONE, TextStyle.FULL)
            DateField.MONTH_SHORT_NAME -> monthName(time.month, TextStyle.SHORT_STANDALONE, TextStyle.SHORT)
            DateField.MONTH_TWO_DIGITS -> time.monthValue.toString().padStart(2, '0')
            DateField.MONTH -> time.monthValue.toString()
            DateField.DAY_NAME -> dayName(time.dayOfWeek, TextStyle.FULL_STANDALONE, TextStyle.FULL)
            DateField.DAY_SHORT_NAME -> dayName(time.dayOfWeek, TextStyle.SHORT_STANDALONE, TextStyle.SHORT)
            DateField.DAY_TWO_DIGITS -> time.dayOfMonth.toString().padStart(2, '0')
            DateField.DAY -> time.dayOfMonth.toString()
            DateField.HOUR_TWO_DIGITS -> time.hour.toString().padStart(2, '0')
            DateField.HOUR -> time.hour.toString()
            DateField.MINUTE_TWO_DIGITS -> time.minute.toString().padStart(2, '0')
            DateField.MINUTE -> time.minute.toString()
            DateField.SECOND_TWO_DIGITS -> time.second.toString().padStart(2, '0')
            DateField.SECOND -> time.second.toString()
            DateField.MILLIS -> millis.toString().padStart(3, '0')
            DateField.MILLIS_TWO_DIGITS -> (millis / 10).toString().padStart(2, '0')
            DateField.MILLIS_ONE_DIGIT -> (millis / 100).toString()
        }
    }

    // Standalone names suit a token on its own ("октябрь", not the genitive "октября"); some locale data lack them and
    // answer the number instead, so the format form is the fallback.
    // zh-CN: 独立形式适合单独出现的名称 (如 "октябрь" 而非属格 "октября"); 部分区域数据缺少独立形式而返回数字, 此时回退到格式形式.
    private fun monthName(month: Month, standalone: TextStyle, format: TextStyle): String =
        month.getDisplayName(standalone, locale).takeUnless { it.all(Char::isDigit) } ?: month.getDisplayName(format, locale)

    private fun dayName(day: DayOfWeek, standalone: TextStyle, format: TextStyle): String =
        day.getDisplayName(standalone, locale).takeUnless { it.all(Char::isDigit) } ?: day.getDisplayName(format, locale)
}

/** Source of random values; tests inject a fixed one. zh-CN: 随机值来源; 测试注入固定实现. */
internal fun interface RandomSource {
    fun next(kind: RandomKind, length: Int): String
}

/** [SecureRandom] strings and version 4 UUIDs (lower case, like `crypto.randomUUID`). zh-CN: 安全随机串与第 4 版 UUID. */
internal object SecureRandomSource : RandomSource {

    private val random = SecureRandom()

    override fun next(kind: RandomKind, length: Int): String = when (kind) {
        RandomKind.UUID_V4 -> UUID.randomUUID().toString()
        else -> String(CharArray(length) { kind.alphabet[random.nextInt(kind.alphabet.length)] })
    }
}

/**
 * `continueNumbering` (roadmap D5): the first counter continues after the largest number already present in the
 * directories, found between the literal text that surrounds the counter in the template. Best effort: without literal
 * context, with a non-positive increment or when the context may hold a regex replacement token, numbering starts as usual.
 * zh-CN: `continueNumbering` (路线图 D5): 第一个计数器从目录中已有的最大序号之后继续, 序号按模板中计数器两侧的字面文本查找.
 * 尽力而为: 没有字面上下文, 增量不为正, 或上下文可能含正则替换记号时, 照常从起始值开始.
 */
internal object ContinuedNumbering {

    private const val MAX_DIGITS = 15

    /** Renamed items to skip before the first one, so that the first value lies above every existing number. */
    fun offset(template: ReplacementTemplate, regexMode: Boolean, existingNames: Iterable<String>): Long {
        val counterIndex = template.parts.indexOfFirst { it is TemplatePart.Counter }
        if (counterIndex < 0) return 0
        val spec = (template.parts[counterIndex] as TemplatePart.Counter).spec
        if (spec.increment <= 0) return 0
        val before = StringBuilder()
        for (index in counterIndex - 1 downTo 0) {
            before.insert(0, literalText(template.parts[index], regexMode) ?: break)
        }
        val after = StringBuilder()
        for (index in counterIndex + 1 until template.parts.size) {
            after.append(literalText(template.parts[index], regexMode) ?: break)
        }
        // In regex mode a `$` may start a replacement token whose text is unknown here.
        // zh-CN: 正则模式下 `$` 可能是替换记号的开头, 其文本在此无法得知.
        if (regexMode && ('$' in before || '$' in after)) return 0
        if (before.isEmpty() && after.isEmpty()) return 0
        val pattern = Regex(Regex.escape(before.toString()) + "(-?[0-9]{1,$MAX_DIGITS})" + Regex.escape(after.toString()))
        val largest = existingNames.flatMap { name -> pattern.findAll(name).map { it.groupValues[1].toLong() }.toList() }.maxOrNull() ?: return 0
        if (largest < spec.start) return 0
        return (largest - spec.start) / spec.increment + 1
    }

    /** The text of a literal part, or null for a variable. zh-CN: 字面部分的文本; 变量返回 null. */
    private fun literalText(part: TemplatePart, regexMode: Boolean): String? = when (part) {
        is TemplatePart.Literal -> part.text
        TemplatePart.Dollar -> if (regexMode) "$$" else "$"
        else -> null
    }
}
