package io.github.supermonster003.autojs6.plugin.batch.rename.engine

import org.autojs.plugin.batchrename.api.BatchRenameErrorCodes
import org.autojs.plugin.batchrename.api.BatchRenameOptionKeys
import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * Option parsing of roadmap P2.2: the D17 defaults, closed value sets, types, limits and ignored keys.
 * zh-CN: 路线图 P2.2 的选项解析: D17 默认值, 闭合取值集合, 类型, 上限与被忽略的键.
 */
class RenameOptionsParserTest {

    @Test
    fun `missing keys take the PowerRename defaults`() {
        val parsed = RenameOptionsParser.parse(mapOf(BatchRenameOptionKeys.SEARCH to "a"))
        val options = parsed.options
        assertEquals(RenameOptions(search = "a"), options)
        assertEquals("", options.replace)
        assertEquals(false, options.regex)
        assertEquals(false, options.matchAll)
        assertEquals(false, options.caseSensitive)
        assertEquals(RenameTarget.ALL, options.target)
        assertEquals(listOf(true, true, true), listOf(options.includeFiles, options.includeFolders, options.includeSubfolders))
        assertEquals(TextFormat.NONE, options.format)
        assertEquals(listOf(false, false), listOf(options.enumerate, options.random))
        assertEquals(DateTimeSource.MODIFIED, options.dateTime)
        assertEquals(Normalization.NONE, options.normalize)
        assertEquals(listOf(false, false, false, false), listOf(options.halfWidth, options.sanitize, options.trim, options.continueNumbering))
        assertEquals("_", options.sanitizeReplacement)
        assertEquals(ConflictPolicy.FAIL, options.onConflict)
        assertEquals(" ({n})", options.conflictNumberPattern)
        assertEquals("i", options.flags)
        assertEquals(false, options.pinyin)
        assertEquals(PinyinStyle(PinyinTone.NONE, PinyinSeparator.NONE, PinyinCase.LOWER, initials = false), options.pinyinStyle)
        assertEquals(emptySet<String>(), parsed.unknownKeys)
    }

    @Test
    fun `every key is read with its contract value`() {
        val options = RenameOptionsParser.parse(
            mapOf(
                "search" to "(a)", "replace" to "b", "regex" to true, "regexFlags" to "us", "matchAll" to true, "caseSensitive" to true,
                "target" to "extension", "includeFiles" to false, "includeFolders" to false, "includeSubfolders" to false,
                "format" to "capitalize", "enumerate" to true, "random" to true, "dateTime" to "now", "normalize" to "nfd",
                "halfWidth" to true, "sanitize" to true, "sanitizeReplacement" to "-", "trim" to true, "continueNumbering" to true,
                "onConflict" to "number", "conflictNumberPattern" to "_{n}", "pinyin" to true, "pinyinTone" to "number",
                "pinyinSeparator" to "hyphen", "pinyinCase" to "capitalize", "pinyinInitials" to true, "metadata" to true,
            ),
        ).options
        assertEquals(
            RenameOptions(
                search = "(a)", replace = "b", regex = true, regexFlags = "us", matchAll = true, caseSensitive = true,
                target = RenameTarget.EXTENSION, includeFiles = false, includeFolders = false, includeSubfolders = false,
                format = TextFormat.CAPITALIZE, enumerate = true, random = true, dateTime = DateTimeSource.NOW, normalize = Normalization.NFD,
                halfWidth = true, sanitize = true, sanitizeReplacement = "-", trim = true, continueNumbering = true,
                onConflict = ConflictPolicy.NUMBER, conflictNumberPattern = "_{n}", pinyin = true, pinyinTone = PinyinTone.NUMBER,
                pinyinSeparator = PinyinSeparator.HYPHEN, pinyinCase = PinyinCase.CAPITALIZE, pinyinInitials = true, metadata = true,
            ),
            options,
        )
        assertEquals("(a)", options.pattern)
        assertEquals("gsu", options.flags)
        assertEquals("\\(a\\)", options.copy(regex = false).pattern)
    }

    @Test
    fun `unknown keys are ignored and reported`() {
        val parsed = RenameOptionsParser.parse(mapOf("search" to "a", "futureOption" to 1, "onProgress" to null))
        assertEquals(setOf("futureOption", "onProgress"), parsed.unknownKeys)
    }

    @Test
    fun `values outside the contract fail the request`() {
        val invalid = listOf(
            emptyMap(),
            mapOf("search" to 1),
            mapOf("search" to "a", "regex" to "true"),
            mapOf("search" to "a", "target" to "stem"),
            mapOf("search" to "a", "format" to "Title"),
            mapOf("search" to "a", "dateTime" to false),
            mapOf("search" to "a", "normalize" to "nfkc"),
            mapOf("search" to "a", "onConflict" to "rename"),
            mapOf("search" to "a", "regexFlags" to "g"),
            mapOf("search" to "a", "regexFlags" to "mm"),
            mapOf("search" to "a", "sanitizeReplacement" to ":"),
            mapOf("search" to "a", "sanitizeReplacement" to "/"),
            mapOf("search" to "a", "conflictNumberPattern" to " (n)"),
            mapOf("search" to "a", "conflictNumberPattern" to "/{n}"),
            mapOf("search" to "a", "pinyin" to "true"),
            mapOf("search" to "a", "pinyinTone" to "marks"),
            mapOf("search" to "a", "pinyinSeparator" to "_"),
            mapOf("search" to "a", "pinyinCase" to "upper"),
            mapOf("search" to "a", "pinyinInitials" to 1),
            mapOf("search" to "a", "metadata" to "true"),
        )
        invalid.forEach { values -> assertEquals(values.toString(), BatchRenameErrorCodes.INVALID_OPTIONS, errorCode(values)) }
        assertEquals(BatchRenameErrorCodes.LIMIT_EXCEEDED, errorCode(mapOf("search" to "a".repeat(4097))))
        assertEquals(BatchRenameErrorCodes.LIMIT_EXCEEDED, errorCode(mapOf("search" to "a", "replace" to "b".repeat(4097))))
        assertEquals(null, errorCode(mapOf("search" to "a".repeat(4096), "replace" to "")))
    }

    private fun errorCode(values: Map<String, Any?>): String? =
        runCatching { RenameOptionsParser.parse(values) }.exceptionOrNull()?.let { (it as PlanException).code }
}
