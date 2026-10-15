package io.github.supermonster003.autojs6.plugin.batch.rename.engine.regex

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * The Rhino engine against V8, the reference ECMAScript implementation (roadmap P2.1, D6 / D15). Every expected value
 * below was computed by Node.js 24 (V8) from the same pattern, flags, input and replacement: the PowerRename
 * documentation examples (roadmap appendix A, group 3), the replacement patterns, every flag the contract allows,
 * named groups, look-around assertions, backreferences and Unicode property escapes. Plain-text searches go through
 * [JsRegexSyntax.escapePattern] and [JsRegexSyntax.escapeReplacement] and must behave literally.
 *
 * zh-CN: Rhino 引擎对照参考 ECMAScript 实现 V8 (路线图 P2.1, D6 / D15). 下列每个期望值都由 Node.js 24 (V8) 以相同的表达式,
 * 标志, 输入与替换串计算得出: PowerRename 文档示例 (路线图附录 A 第 3 组), 替换模式, 契约允许的全部标志, 命名组, 前后行断言,
 * 反向引用与 Unicode 属性转义. 纯文本查找经 [JsRegexSyntax.escapePattern] 与 [JsRegexSyntax.escapeReplacement] 处理后必须按字面生效.
 */
class EcmaRegexParityTest {

    private val engine = RhinoRegexEngine()

    @Test
    fun `replacements and tests match V8`() {
        assertTrue("the roadmap asks for at least 60 cases", CASES.size >= 60)
        val mismatches = engine.session { session ->
            CASES.mapNotNull { case ->
                val regex = session.compile(case.source, case.flags)
                val actual: Any = if (case.replacement == null) session.test(case.input, regex) else session.replace(case.input, regex, case.replacement)
                case.takeUnless { actual == case.expected }?.let { "${it.describe()} -> ${quote(actual)}" }
            }
        }
        assertEquals("cases that differ from V8:\n" + mismatches.joinToString("\n"), emptyList<String>(), mismatches)
    }

    @Test
    fun `plain-text searches and replacements are literal`() {
        val mismatches = engine.session { session ->
            PLAIN_CASES.mapNotNull { case ->
                val regex = session.compile(JsRegexSyntax.escapePattern(case.search), JsRegexSyntax.flags(case.matchAll, case.caseSensitive))
                val actual = session.replace(case.input, regex, JsRegexSyntax.escapeReplacement(case.replacement))
                case.takeUnless { actual == case.expected }?.let { "$it -> ${quote(actual)}" }
            }
        }
        assertEquals(emptyList<String>(), mismatches)
    }

    @Test
    fun `options map to the flags g and i like PowerRename`() {
        assertEquals("i", JsRegexSyntax.flags(matchAll = false, caseSensitive = false))
        assertEquals("", JsRegexSyntax.flags(matchAll = false, caseSensitive = true))
        assertEquals("gi", JsRegexSyntax.flags(matchAll = true, caseSensitive = false))
        assertEquals("g", JsRegexSyntax.flags(matchAll = true, caseSensitive = true))
        // Extra flags join in the canonical order of RegExp.prototype.flags.
        assertEquals("gimsuy", JsRegexSyntax.flags(matchAll = true, caseSensitive = false, extraFlags = "yusm"))
        listOf("g", "i", "x", "mm", "d", "v").forEach { flags ->
            assertTrue("\"$flags\" is rejected", runCatching { JsRegexSyntax.flags(false, false, flags) }.isFailure)
        }
    }

    private data class Case(val group: String, val source: String, val flags: String, val input: String, val replacement: String?, val expected: Any) {
        fun describe() = "[$group] /${source}/$flags on ${quote(input)}" + (replacement?.let { " with ${quote(it)}" } ?: " (test)") + " expected ${quote(expected)}"
    }

    private data class PlainCase(val search: String, val input: String, val replacement: String, val matchAll: Boolean, val caseSensitive: Boolean, val expected: String)

    private companion object {

        fun quote(value: Any): String = if (value is String) "\"" + value.replace("\n", "\\n") + "\"" else value.toString()

        fun replace(group: String, source: String, flags: String, input: String, replacement: String, expected: String) =
            Case(group, source, flags, input, replacement, expected)

        fun test(group: String, source: String, flags: String, input: String, expected: Boolean) =
            Case(group, source, flags, input, null, expected)

        fun plain(search: String, input: String, replacement: String, matchAll: Boolean, caseSensitive: Boolean, expected: String) =
            PlainCase(search, input, replacement, matchAll, caseSensitive, expected)

        // Generated with Node.js 24.15.0 (V8): expected = input.replace(new RegExp(source, flags), replacement).
        val CASES = listOf(
        replace("powerrename", "(.*).png", "", "photo.png", "foo_\$1.png", "foo_photo.png"),
        replace("powerrename", "(.*).png", "", "photo.png", "\$1_foo.png", "photo_foo.png"),
        replace("powerrename", "(.*)", "", "notes", "\$1.txt", "notes.txt"),
        replace("powerrename", "(^\\w+\\.\$)|(^\\w+\$)", "", "notes", "\$2.txt", "notes.txt"),
        replace("powerrename", "(^\\w+\\.\$)|(^\\w+\$)", "", "notes.md", "\$2.txt", "notes.md"),
        replace("powerrename", "(\\d\\d)-(\\d\\d)-(\\d\\d\\d\\d)", "", "29-03-2020", "\$3-\$2-\$1", "2020-03-29"),
        replace("powerrename", "^(.{3})(.*)", "", "abcdef", "\$1foo\$2", "abcfoodef"),
        replace("powerrename", "^.{3}", "", "abcdef", "", "def"),
        replace("powerrename", ".+?(?=bar)", "", "foo_bar_bar", "X", "Xbar_bar"),
        replace("powerrename", "foo[\\s\\S]*bar", "", "foo 1 bar 2 bar!", "X", "X!"),
        replace("powerrename", "^foo.*bar\$", "", "foo middle bar", "X", "X"),
        replace("replacement", "b", "", "abc", "[\$&]", "a[b]c"),
        replace("replacement", "b", "", "abc", "[\$`]", "a[a]c"),
        replace("replacement", "b", "", "abc", "[\$']", "a[c]c"),
        replace("replacement", "b", "", "abc", "\$\$", "a\$c"),
        replace("replacement", "b", "", "abc", "\$", "a\$c"),
        replace("replacement", "b", "", "abc", "a\$", "aa\$c"),
        replace("replacement", "(b)", "", "abc", "\$0", "a\$0c"),
        replace("replacement", "(b)", "", "abc", "\$2", "a\$2c"),
        replace("replacement", "(b)", "", "abc", "\$10", "ab0c"),
        replace("replacement", "(a)(b)(c)(d)(e)(f)(g)(h)(i)(j)", "", "abcdefghij", "\$10-\$1", "j-a"),
        replace("replacement", "(a)(b)(c)(d)(e)(f)(g)(h)(i)(j)", "", "abcdefghij", "\$11", "a1"),
        replace("replacement", "(?<word>b)", "", "abc", "[\$<word>]", "a[b]c"),
        replace("replacement", "(?<word>b)", "", "abc", "[\$<missing>]", "a[]c"),
        replace("replacement", "(b)", "", "abc", "[\$<word>]", "a[\$<word>]c"),
        replace("replacement", "(?<word>b)", "", "abc", "[\$<word]", "a[\$<word]c"),
        replace("replacement", "(x)?b", "", "abc", "[\$1]", "a[]c"),
        replace("replacement", "b", "", "abc", "\$\$1", "a\$1c"),
        replace("flags", "a", "", "banana", "o", "bonana"),
        replace("flags", "a", "g", "banana", "o", "bonono"),
        replace("flags", "A", "", "banana", "o", "banana"),
        replace("flags", "A", "i", "banana", "o", "bonana"),
        replace("flags", "A", "gi", "banana", "o", "bonono"),
        replace("flags", "^b", "gm", "a\nb\nb", "X", "a\nX\nX"),
        replace("flags", "^b", "g", "a\nb\nb", "X", "a\nb\nb"),
        replace("flags", "a.b", "", "a\nb", "X", "a\nb"),
        replace("flags", "a.b", "s", "a\nb", "X", "X"),
        replace("flags", "a", "y", "aab", "X", "Xab"),
        replace("flags", "b", "y", "aab", "X", "aab"),
        replace("flags", "a", "gy", "aaba", "X", "XXba"),
        replace("groups", "(?<year>\\d{4})-(?<month>\\d{2})", "", "2026-10", "\$<month>.\$<year>", "10.2026"),
        replace("groups", "(?<=\\\$)\\d+", "g", "cost \$30 and \$45", "N", "cost \$N and \$N"),
        replace("groups", "(?<!\\\$)\\b\\d+", "g", "cost \$30 and 45", "N", "cost \$30 and N"),
        replace("groups", "\\d+(?=px)", "g", "10px 20em 30px", "N", "Npx 20em Npx"),
        replace("groups", "\\d+(?!px)\\b", "g", "10px 20em 30px", "N", "10px 20em 30px"),
        replace("groups", "(\\w)\\1", "g", "aabbcd", "<\$1>", "<a><b>cd"),
        replace("groups", "(?<c>\\w)\\k<c>", "g", "aabbcd", "<\$<c>>", "<a><b>cd"),
        replace("groups", "(?:ab)+", "", "ababx", "Y", "Yx"),
        replace("groups", "(a)|(b)", "g", "ab", "[\$1|\$2]", "[a|][|b]"),
        replace("syntax", "x*", "g", "abc", "-", "-a-b-c-"),
        replace("syntax", "", "g", "ab", "-", "-a-b-"),
        replace("syntax", "a{2,3}", "g", "aaaaaaa", "X", "XXa"),
        replace("syntax", "a{2,}?", "g", "aaaaa", "X", "XXa"),
        replace("syntax", "\\bcat\\b", "g", "cat concat cat", "dog", "dog concat dog"),
        replace("syntax", "\\Bcat", "g", "cat concat", "dog", "cat condog"),
        replace("syntax", "[^\\w.]", "g", "a b-c.d", "_", "a_b_c.d"),
        replace("syntax", "[\\d\\s]+", "g", "a 1 2 b", "#", "a#b"),
        replace("syntax", "\\s+\$", "", "name  ", "", "name"),
        replace("syntax", "^\\s+|\\s+\$", "g", "  name  ", "", "name"),
        replace("syntax", "\\.+\$", "", "name...", "", "name"),
        replace("syntax", "[A-Z]", "g", "IMG_Photo", "*", "***_*hoto"),
        replace("syntax", "_(\\d+)", "", "IMG_0042", "-\$1", "IMG-0042"),
        replace("syntax", "\\x41\\u0042", "", "zABz", "ok", "zokz"),
        replace("syntax", "[\\u4e00-\\u9fff]+", "g", "\u7167\u7247_2026", "CJK", "CJK_2026"),
        replace("unicode", "\\p{L}+", "gu", "abc 123 \u7167\u7247", "L", "L 123 L"),
        replace("unicode", "\\p{Script=Han}", "gu", "a\u7167b\u7247", "H", "aHbH"),
        replace("unicode", "\\p{Lu}", "gu", "aBcD", "_", "a_c_"),
        replace("unicode", "\\P{L}", "gu", "a1b2", "_", "a_b_"),
        replace("unicode", ".", "gu", "a\ud83d\ude00b", ".", "..."),
        replace("unicode", ".", "g", "a\ud83d\ude00b", ".", "...."),
        replace("unicode", "\\u{1F600}", "u", "x\ud83d\ude00y", "smile", "xsmiley"),
        replace("unicode", "[\ud83d\ude00-\ud83d\ude4f]", "gu", "a\ud83d\ude00\ud83d\ude4fb", "E", "aEEb"),
        replace("unicode", "\u00e9", "i", "CAF\u00c9", "e", "CAFe"),
        replace("unicode", "\u017f", "iu", "S", "x", "x"),
        test("test", "^IMG_\\d{4}\$", "", "IMG_0042", true),
        test("test", "^IMG_\\d{4}\$", "", "IMG_42", false),
        test("test", "png\$", "i", "photo.PNG", true),
        test("test", "png\$", "", "photo.PNG", false),
        test("test", "a", "y", "ba", false),
        test("test", "a", "g", "aa", true),
        )

        // Generated with Node.js 24.15.0 (V8) from the same escaping rules.
        val PLAIN_CASES = listOf(
        plain("a.b", "axb a.b", "X", matchAll = true, caseSensitive = false, "axb X"),
        plain("\$1", "cost \$1 now", "one", matchAll = false, caseSensitive = true, "cost one now"),
        plain("(x)", "f(x) = (x)", "y", matchAll = true, caseSensitive = true, "fy = y"),
        plain("\\", "C:\\dir\\file", "/", matchAll = true, caseSensitive = true, "C:/dir/file"),
        plain("[1]", "a[1]b[1]", "#", matchAll = true, caseSensitive = true, "a#b#"),
        plain("a+b", "aab a+b", "plus", matchAll = true, caseSensitive = true, "aab plus"),
        plain("^", "x^2", "**", matchAll = false, caseSensitive = true, "x**2"),
        plain("*.txt", "*.txt", "all", matchAll = false, caseSensitive = true, "all"),
        plain("{n}", "{n}-{N}", "k", matchAll = true, caseSensitive = false, "k-k"),
        plain("a|b", "a|b ab", "or", matchAll = true, caseSensitive = true, "or ab"),
        plain("IMG", "img_IMG", "Photo", matchAll = true, caseSensitive = false, "Photo_Photo"),
        plain("IMG", "img_IMG", "Photo", matchAll = false, caseSensitive = false, "Photo_IMG"),
        plain("?", "why?", "!", matchAll = false, caseSensitive = true, "why!"),
        plain("/", "a/b", "_", matchAll = false, caseSensitive = true, "a_b"),
        plain("b", "abc", "\$&\$1\$\$", matchAll = false, caseSensitive = true, "a\$&\$1\$\$c"),
        )
    }
}
