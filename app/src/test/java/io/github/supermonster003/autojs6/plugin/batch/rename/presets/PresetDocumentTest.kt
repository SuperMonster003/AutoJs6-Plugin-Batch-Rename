package io.github.supermonster003.autojs6.plugin.batch.rename.presets

import io.github.supermonster003.autojs6.plugin.batch.rename.engine.ConflictPolicy
import io.github.supermonster003.autojs6.plugin.batch.rename.engine.PinyinSeparator
import io.github.supermonster003.autojs6.plugin.batch.rename.engine.PlanException
import io.github.supermonster003.autojs6.plugin.batch.rename.engine.RenameOptions
import io.github.supermonster003.autojs6.plugin.batch.rename.engine.RenameTarget
import org.autojs.plugin.batchrename.api.BatchRenameErrorCodes
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Assert.fail
import org.junit.Test

/**
 * The preset document (roadmap D36 / D45) and the JSON reader under it: round trips, defaults for missing keys, refused
 * entries and documents refused as a whole.
 * zh-CN: 预设文档 (路线图 D36 / D45) 及其底层 JSON 读取器: 往返, 缺省键的默认值, 被拒的条目与整体被拒的文档.
 */
class PresetDocumentTest {

    private val backslash = 92.toChar()

    private val photos = Preset(
        "照片 \"按日期\"",
        RenameOptions(
            search = "IMG_(${backslash}d+)", replace = "\$YYYY-$1", regex = true, target = RenameTarget.NAME,
            onConflict = ConflictPolicy.NUMBER, pinyin = true, pinyinSeparator = PinyinSeparator.HYPHEN,
        ),
    )

    @Test
    fun `presets survive a round trip and the document is readable`() {
        val presets = listOf(photos, Preset("plain", RenameOptions(search = "")))
        val text = PresetDocument.encode(presets)
        val decoded = PresetDocument.decode(text)
        assertEquals(presets, decoded.entries.map { it.preset })
        assertEquals(listOf(0, 1), decoded.entries.map { it.index })
        assertTrue(decoded.rejected.isEmpty())
        assertTrue(text, text.startsWith("{\n  \"format\": \"autojs6-batch-rename-presets\",\n  \"version\": 1,\n  \"presets\": [\n    {\n      \"name\": "))
        assertTrue(text.endsWith("\n  ]\n}\n"))
        assertEquals(PresetDocument.encode(emptyList()), "{\n  \"format\": \"autojs6-batch-rename-presets\",\n  \"version\": 1,\n  \"presets\": []\n}\n")
        assertTrue(PresetDocument.decode(PresetDocument.encode(emptyList())).entries.isEmpty())
    }

    @Test
    fun `missing keys take their defaults, unknown keys and names are trimmed or dropped`() {
        val decoded = PresetDocument.decode(
            """{"format": "autojs6-batch-rename-presets", "version": 1, "presets": [
                {"name": "  short  ", "options": {"replace": "x", "futureOption": 3, "preset": "other"}}
            ]}""",
        )
        assertEquals(listOf(Preset("short", RenameOptions(search = "", replace = "x"))), decoded.entries.map { it.preset })
    }

    @Test
    fun `bad entries are refused one by one`() {
        val decoded = PresetDocument.decode(
            """{"format": "autojs6-batch-rename-presets", "version": 1, "presets": [
                {"name": "ok", "options": {}},
                {"options": {}},
                {"name": "   ", "options": {}},
                {"name": "${"x".repeat(65)}", "options": {}},
                {"name": "ok", "options": {}},
                {"name": "bad type", "options": {"regex": "yes"}},
                {"name": "bad value", "options": {"target": "stem"}},
                {"name": "no options"},
                "text"
            ]}""",
        )
        assertEquals(listOf("ok"), decoded.entries.map { it.preset.name })
        assertEquals(
            listOf(
                1 to RejectionKind.NAME_MISSING, 2 to RejectionKind.NAME_MISSING, 3 to RejectionKind.NAME_INVALID,
                4 to RejectionKind.DUPLICATE_NAME, 5 to RejectionKind.OPTIONS_INVALID, 6 to RejectionKind.OPTIONS_INVALID,
                7 to RejectionKind.OPTIONS_INVALID, 8 to RejectionKind.NAME_MISSING,
            ),
            decoded.rejected.map { it.index to it.kind },
        )
        assertEquals("ok", decoded.rejected.single { it.index == 4 }.name)
        assertTrue(decoded.rejected.single { it.index == 5 }.message.contains("regex"))
    }

    @Test
    fun `documents of another format, version or size fail as a whole`() {
        fun code(text: String): String? = try {
            PresetDocument.decode(text)
            null
        } catch (error: PlanException) {
            error.code
        }
        listOf(
            "", "[]", "{}", "not json", """{"format": "other", "version": 1, "presets": []}""",
            """{"format": "autojs6-batch-rename-presets", "version": 2, "presets": []}""",
            """{"format": "autojs6-batch-rename-presets", "version": 1.5, "presets": []}""",
            """{"format": "autojs6-batch-rename-presets", "version": 1}""",
            """{"format": "autojs6-batch-rename-presets", "version": 1, "presets": []} trailing""",
        ).forEach { text -> assertEquals(text, BatchRenameErrorCodes.INVALID_OPTIONS, code(text)) }
        assertEquals(BatchRenameErrorCodes.LIMIT_EXCEEDED, code(" ".repeat(131_073)))
        // A byte order mark of an edited file is accepted. zh-CN: 接受编辑过的文件开头的字节顺序标记.
        assertEquals(null, code(0xfeff.toChar() + PresetDocument.encode(emptyList())))
    }

    @Test
    fun `the JSON reader is strict and keeps member order`() {
        assertEquals(linkedMapOf("b" to 1L, "a" to listOf(true, null, -2.5e3, "x")), Json.parse("""{"b": 1, "a": [true, null, -2.5e3, "x"]}"""))
        val escaped = "\"a${backslash}\"b${backslash}${backslash}c${backslash}n${backslash}u00e9${backslash}/\""
        assertEquals("a\"b${backslash}c\né/", Json.parse(escaped))
        listOf("{\"a\": 1, \"a\": 2}", "[1,]", "{'a': 1}", "01", "\"tab\there\"", "[".repeat(40) + "]".repeat(40), "tru", "1 2")
            .forEach { text ->
                try {
                    Json.parse(text)
                    fail("accepted $text")
                } catch (_: Json.SyntaxException) {
                }
            }
        val tricky = "line${10.toChar()}quote\"slash${backslash}ctl${1.toChar()}sep${0x2028.toChar()}"
        assertEquals(tricky, Json.parse(Json.quote(tricky)))
    }
}
