package io.github.supermonster003.autojs6.plugin.batch.rename.engine.local

import io.github.supermonster003.autojs6.plugin.batch.rename.engine.Normalization
import io.github.supermonster003.autojs6.plugin.batch.rename.engine.RenameOptions
import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * The local extensions of roadmap D5 / D19 / P2.3: normalization, half-width conversion, sanitizing and trimming.
 * zh-CN: 路线图 D5 / D19 / P2.3 的本土扩展: 规范化, 全角转半角, 清理与修剪.
 */
class NameNormalizerTest {

    private val off = RenameOptions(search = "")

    @Test
    fun `every step is off by default`() {
        val text = " e\u0301\uff21:CON. "
        assertEquals(text, NameNormalizer.apply(text, off, holdsStem = true))
    }

    @Test
    fun `normalization composes and decomposes`() {
        assertEquals("\u00e9t\u00e9", NameNormalizer.apply("e\u0301te\u0301", off.copy(normalize = Normalization.NFC), holdsStem = true))
        assertEquals("e\u0301te\u0301", NameNormalizer.apply("\u00e9t\u00e9", off.copy(normalize = Normalization.NFD), holdsStem = true))
        // Hangul syllables and emoji stay intact.
        assertEquals("\ud55c\uae00\ud83d\ude00", NameNormalizer.apply("\u1112\u1161\u11ab\u1100\u1173\u11af\ud83d\ude00", off.copy(normalize = Normalization.NFC), holdsStem = true))
    }

    @Test
    fun `half-width conversion covers the full-width ASCII block and the ideographic space`() {
        val fullWidth = (0xff01..0xff5e).map { it.toChar() }.joinToString("") + "\u3000"
        val ascii = (0x21..0x7e).map { it.toChar() }.joinToString("") + " "
        assertEquals(ascii, NameNormalizer.toHalfWidth(fullWidth))
        assertEquals("\u7167\u7247 2026", NameNormalizer.toHalfWidth("\u7167\u7247\u3000\uff12\uff10\uff12\uff16"))
        assertEquals("\uff65\uff71", NameNormalizer.toHalfWidth("\uff65\uff71"))
    }

    @Test
    fun `sanitize replaces restricted characters and suffixes device names`() {
        assertEquals("a_b_c_d_e_f_g_h_i_j", NameNormalizer.sanitize("a\\b:c*d?e\"f<g>h|i\u0001j", "_", holdsStem = true))
        assertEquals("abc", NameNormalizer.sanitize("a:b?c", "", holdsStem = true))
        assertEquals("CON_", NameNormalizer.sanitize("CON", "_", holdsStem = true))
        assertEquals("com1-.txt", NameNormalizer.sanitize("com1.txt", "-", holdsStem = true))
        assertEquals("nul_.tar.gz", NameNormalizer.sanitize("nul.tar.gz", "", holdsStem = true))
        assertEquals("CONSOLE", NameNormalizer.sanitize("CONSOLE", "_", holdsStem = true))
        assertEquals("CON", NameNormalizer.sanitize("CON", "_", holdsStem = false))
    }

    @Test
    fun `trim removes outer white space and trailing dots`() {
        assertEquals("name", NameNormalizer.trim("  name . . "))
        assertEquals(".hidden", NameNormalizer.trim("\u3000.hidden"))
        assertEquals("a b", NameNormalizer.trim("a b"))
        assertEquals("", NameNormalizer.trim(" ... "))
    }

    @Test
    fun `steps run in pipeline order`() {
        val options = off.copy(normalize = Normalization.NFC, halfWidth = true, sanitize = true, trim = true)
        // Half-width conversion turns U+FF1A into ':', which sanitizing then replaces; trimming runs last.
        assertEquals("caf\u00e9_menu", NameNormalizer.apply("\u3000cafe\u0301\uff1amenu.\u3000", options, holdsStem = true))
    }
}
