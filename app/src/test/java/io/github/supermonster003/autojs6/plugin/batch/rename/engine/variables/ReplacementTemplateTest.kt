package io.github.supermonster003.autojs6.plugin.batch.rename.engine.variables

import io.github.supermonster003.autojs6.plugin.batch.rename.engine.PlanException
import io.github.supermonster003.autojs6.plugin.batch.rename.engine.variables.TemplatePart.Counter
import io.github.supermonster003.autojs6.plugin.batch.rename.engine.variables.TemplatePart.Date
import io.github.supermonster003.autojs6.plugin.batch.rename.engine.variables.TemplatePart.Dollar
import io.github.supermonster003.autojs6.plugin.batch.rename.engine.variables.TemplatePart.Literal
import io.github.supermonster003.autojs6.plugin.batch.rename.engine.variables.TemplatePart.Pinyin
import io.github.supermonster003.autojs6.plugin.batch.rename.engine.variables.TemplatePart.Random
import org.autojs.plugin.batchrename.api.BatchRenameErrorCodes
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Parsing of the PowerRename variables in `replace` (roadmap P2.2, appendix A).
 * zh-CN: `replace` 中 PowerRename 变量的解析 (路线图 P2.2, 附录 A).
 */
class ReplacementTemplateTest {

    private fun parse(text: String, enumerate: Boolean = true, random: Boolean = true, dateTime: Boolean = true) =
        ReplacementTemplate.parse(text, enumerate, random, dateTime).parts

    @Test
    fun `counter forms parse in any key order`() {
        assertEquals(listOf(Literal("Image_"), Counter(CounterSpec()), Literal("_")), parse("Image_\${}_"))
        assertEquals(listOf(Counter(CounterSpec(start = 10, increment = 2, padding = 4))), parse("\${padding=4;increment=2;start=10}"))
        assertEquals(listOf(Counter(CounterSpec(start = -3))), parse("\${start=-3}"))
        assertEquals(listOf(Counter(CounterSpec(increment = 5)), Counter(CounterSpec(padding = 2))), parse("\${increment=5}\${padding=2}"))
    }

    @Test
    fun `the pinyin variable needs no option and $$ keeps it literal`() {
        assertEquals(listOf(Literal("a"), Pinyin, Literal("_")), parse("a\${pinyin}_", enumerate = false, random = false, dateTime = false))
        assertEquals(listOf(Pinyin, Counter(CounterSpec())), parse("\${pinyin}\${}"))
        assertEquals(listOf(Dollar, Literal("{pinyin}")), parse("\$\${pinyin}"))
        assertEquals(listOf(Literal("\${Pinyin}")), parse("\${Pinyin}", enumerate = false, random = false, dateTime = false))
    }

    @Test
    fun `malformed counters stay literal`() {
        listOf("\${start=}", "\${start=1;start=2}", "\${size=3}", "\${padding=-1}", "\${padding=256}", "\${start=1 }", "\${start=1234567890123456}", "\${").forEach { text ->
            val parts = parse(text)
            assertTrue("$text -> $parts", parts.none { it is Counter })
            assertEquals(text, parts.joinToString("") { (it as? Literal)?.text ?: "$$" })
        }
    }

    @Test
    fun `date variables are case sensitive and the longest name wins`() {
        assertEquals(listOf(Date(DateField.MONTH_SHORT_NAME), Literal("-"), Date(DateField.DAY_TWO_DIGITS)), parse("\$MMM-\$DD"))
        assertEquals(listOf(Date(DateField.MONTH), Literal("m")), parse("\$Mm"))
        assertEquals(listOf(Date(DateField.MINUTE_TWO_DIGITS), Date(DateField.MONTH)), parse("\$mm\$M"))
        assertEquals(listOf(Date(DateField.YEAR), Date(DateField.YEAR_LAST_DIGIT)), parse("\$YYYY\$Y"))
        assertEquals(listOf(Date(DateField.MILLIS), Literal("f")), parse("\$ffff"))
        assertEquals(listOf(Literal("\$x \$1 \$<n>")), parse("\$x \$1 \$<n>"))
        assertEquals(20, DateField.entries.size)
    }

    @Test
    fun `double dollars escape variables`() {
        assertEquals(listOf(Dollar, Literal("YYYY")), parse("\$\$YYYY"))
        assertEquals(listOf(Dollar, Date(DateField.YEAR)), parse("\$\$\$YYYY"))
        assertEquals(listOf(Literal("a"), Dollar, Literal("{}")), parse("a\$\${}"))
    }

    @Test
    fun `random values parse within their limits`() {
        assertEquals(
            listOf(Random(RandomKind.ALNUM, 9), Random(RandomKind.ALPHA, 1), Random(RandomKind.DIGIT, 256), Literal("{"), Random(RandomKind.UUID_V4, 0), Literal("}")),
            parse("\${rstringalnum=9}\${rstringalpha=1}\${rstringdigit=256}{\${ruuidv4}}"),
        )
        listOf("\${rstringalnum=0}", "\${rstringalnum=}", "\${rstringalnum=+3}", "\${rstringhex=3}", "\${ruuidv7}").forEach { text ->
            assertTrue(text, parse(text).none { it is Random })
        }
        val error = runCatching { parse("\${rstringdigit=257}") }.exceptionOrNull() as PlanException
        assertEquals(BatchRenameErrorCodes.LIMIT_EXCEEDED, error.code)
    }

    @Test
    fun `variables of disabled options stay literal`() {
        val parts = parse("\${}\${ruuidv4}\$YYYY", enumerate = false, random = false, dateTime = false)
        assertEquals(listOf(Literal("\${}\${ruuidv4}\$YYYY")), parts)
        assertFalse(ReplacementTemplate.parse("\${}", enumerate = false, random = false, dateTime = false).hasCounters)
    }

    @Test
    fun `counters format with sign and padding`() {
        assertEquals("0007", CounterSpec(padding = 4).format(7))
        assertEquals("-007", CounterSpec(padding = 3).format(-7))
        assertEquals("12345", CounterSpec(padding = 2).format(12345))
        assertEquals(16L, CounterSpec(start = 10, increment = 2).valueAt(3))
        val overflow = runCatching { CounterSpec(start = 999_999_999_999_999, increment = 999_999_999_999_999).valueAt(1_000_000) }.exceptionOrNull()
        assertEquals(BatchRenameErrorCodes.INVALID_OPTIONS, (overflow as PlanException).code)
    }
}
