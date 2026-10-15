package io.github.supermonster003.autojs6.plugin.batch.rename.engine.format

import io.github.supermonster003.autojs6.plugin.batch.rename.engine.TextFormat
import org.junit.Assert.assertEquals
import org.junit.Test
import java.util.Locale

/**
 * Text formatting of roadmap D18 / P2.3 in English, Chinese and Turkish (the dotted and dotless i).
 * zh-CN: 路线图 D18 / P2.3 的文本格式化, 覆盖英语, 中文与土耳其语 (带点与不带点的 i).
 */
class TextFormatterTest {

    private val english = Locale.ENGLISH
    private val turkish = Locale.forLanguageTag("tr")

    @Test
    fun `lower and upper follow the locale`() {
        assertEquals("photo final.jpg", TextFormatter.format("Photo FINAL.jpg", TextFormat.LOWER, english))
        assertEquals("PHOTO FINAL.JPG", TextFormatter.format("Photo final.jpg", TextFormat.UPPER, english))
        assertEquals("\u0130STANBUL", TextFormatter.format("istanbul", TextFormat.UPPER, turkish))
        assertEquals("ISTANBUL", TextFormatter.format("istanbul", TextFormat.UPPER, english))
        assertEquals("\u0131stanbul", TextFormatter.format("ISTANBUL", TextFormat.LOWER, turkish))
        assertEquals("\u7167\u7247 abc", TextFormatter.format("\u7167\u7247 ABC", TextFormat.LOWER, Locale.SIMPLIFIED_CHINESE))
    }

    @Test
    fun `title capitalizes only the first character`() {
        assertEquals("Holiday photos 2026.JPG", TextFormatter.format("holiday photos 2026.JPG", TextFormat.TITLE, english))
        assertEquals("\u0130zmir", TextFormatter.format("izmir", TextFormat.TITLE, turkish))
        assertEquals("_photo", TextFormatter.format("_photo", TextFormat.TITLE, english))
        assertEquals("", TextFormatter.format("", TextFormat.TITLE, english))
    }

    @Test
    fun `capitalize handles every white space separated word`() {
        assertEquals("Holiday Photos Of 2026.jpg", TextFormatter.format("holiday photos of 2026.jpg", TextFormat.CAPITALIZE, english))
        assertEquals("A\u3000B\tC", TextFormatter.format("a\u3000b\tc", TextFormat.CAPITALIZE, english))
        assertEquals("Hello-world Again", TextFormatter.format("hello-world again", TextFormat.CAPITALIZE, english))
    }

    @Test
    fun `surrogate pairs survive every format`() {
        val text = "\ud83d\ude00 smile \ud801\udc28"
        TextFormat.entries.forEach { format ->
            val result = TextFormatter.format(text, format, english)
            assertEquals("$format keeps the emoji", true, result.contains("\ud83d\ude00"))
        }
        // U+10428 DESERET SMALL LETTER LONG I upper-cases to U+10400 as one code point.
        assertEquals("\ud83d\ude00 Smile \ud801\udc00", TextFormatter.format(text, TextFormat.CAPITALIZE, english))
    }
}
