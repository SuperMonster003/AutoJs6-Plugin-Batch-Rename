package io.github.supermonster003.autojs6.plugin.batch.rename.presets

import io.github.supermonster003.autojs6.plugin.batch.rename.engine.RenameOptions
import io.github.supermonster003.autojs6.plugin.batch.rename.settings.MemoryStore
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * The plugin's preset store (roadmap D36 / D43): save, replace, rename, delete, export and the three import policies
 * within the count and size limits.
 * zh-CN: 插件的预设存储 (路线图 D36 / D43): 保存, 替换, 重命名, 删除, 导出, 以及数量与大小上限内的三种导入策略.
 */
class PresetStoreTest {

    private val memory = MemoryStore()
    private val store = PresetStore(memory)

    private fun options(replace: String) = RenameOptions(search = "a", replace = replace)

    private fun document(vararg presets: Preset) = PresetDocument.decode(PresetDocument.encode(presets.toList()))

    @Test
    fun `save, replace, rename and delete keep the order`() {
        assertNull(store.save("one", options("1")))
        assertNull(store.save("two", options("2")))
        assertNull(store.save("one", options("1b")))
        assertEquals(listOf("one" to "1b", "two" to "2"), store.list().map { it.name to it.options.replace })
        assertEquals(RejectionKind.DUPLICATE_NAME, store.rename("one", "two"))
        assertNull(store.rename("one", "three"))
        assertEquals(listOf("three", "two"), store.list().map { it.name })
        assertEquals(RejectionKind.NAME_MISSING, store.save("", options("x")))
        assertEquals(RejectionKind.NAME_INVALID, store.save(" padded", options("x")))
        assertEquals(RejectionKind.OPTIONS_INVALID, store.save("bad", RenameOptions(search = "", conflictNumberPattern = "no placeholder")))
        store.delete(listOf("three", "missing"))
        assertEquals(listOf("two"), store.list().map { it.name })
        store.delete(listOf("two"))
        assertTrue(memory.values.isEmpty())
    }

    @Test
    fun `export holds the chosen presets in the store order`() {
        store.save("a", options("1"))
        store.save("b", options("2"))
        store.save("c", options("3"))
        assertEquals(listOf("a", "c"), PresetDocument.decode(store.export(listOf("c", "a"))).entries.map { it.preset.name })
    }

    @Test
    fun `imports skip, overwrite or rename taken names`() {
        store.save("a", options("old"))
        val incoming = document(Preset("a", options("new")), Preset("b", options("b")))

        val skipped = store.import(incoming, PresetConflict.SKIP)
        assertEquals(listOf("b"), skipped.imported)
        assertEquals(listOf("a"), skipped.skipped)
        assertEquals("old", store.find("a")!!.options.replace)

        val overwritten = store.import(document(Preset("a", options("new"))), PresetConflict.OVERWRITE)
        assertEquals(listOf("a"), overwritten.imported)
        assertEquals(listOf("a", "b"), store.list().map { it.name })
        assertEquals("new", store.find("a")!!.options.replace)

        val renamed = store.import(document(Preset("a", options("third")), Preset("a (1)", options("fourth"))), PresetConflict.RENAME)
        assertEquals(listOf("a (1)", "a (1) (1)"), renamed.imported)
        assertEquals("fourth", store.find("a (1) (1)")!!.options.replace)
        val long = "x".repeat(64)
        store.save(long, options("x"))
        assertEquals(listOf("x".repeat(60) + " (1)"), store.import(document(Preset(long, options("y"))), PresetConflict.RENAME).imported)
    }

    @Test
    fun `entries beyond the limits are refused and the rest imported`() {
        (1..99).forEach { assertNull(store.save("p$it", options("$it"))) }
        val result = store.import(document(Preset("new 1", options("1")), Preset("new 2", options("2"))), PresetConflict.SKIP)
        assertEquals(listOf("new 1"), result.imported)
        assertEquals(listOf(1 to RejectionKind.TOO_MANY), result.rejected.map { it.index to it.kind })
        assertEquals(RejectionKind.TOO_MANY, store.save("p101", options("x")))

        store.delete(store.list().map { it.name })
        val large = RenameOptions(search = "s".repeat(4096), replace = "r".repeat(4096))
        // Each preset takes about 9000 characters: the first document fits whole, the second only in part.
        // zh-CN: 每个预设约 9000 个字符: 第一份文档可全部导入, 第二份只能导入一部分.
        val first = store.import(document(*(1..8).map { Preset("large $it", large) }.toTypedArray()), PresetConflict.SKIP)
        assertEquals(8, first.imported.size)
        val second = store.import(document(*(9..16).map { Preset("large $it", large) }.toTypedArray()), PresetConflict.SKIP)
        assertTrue(second.imported.size in 4..7)
        assertTrue(second.rejected.isNotEmpty() && second.rejected.all { it.kind == RejectionKind.TOO_LARGE })
        assertEquals(8, second.imported.size + second.rejected.size)
    }

    @Test
    fun `refused entries of a document come back with the import`() {
        val decoded = PresetDocument.decode(
            """{"format": "autojs6-batch-rename-presets", "version": 1, "presets": [{"name": "", "options": {}}, {"name": "ok", "options": {}}]}""",
        )
        val result = store.import(decoded, PresetConflict.SKIP)
        assertEquals(listOf("ok"), result.imported)
        assertEquals(listOf(0 to RejectionKind.NAME_MISSING), result.rejected.map { it.index to it.kind })
    }

    @Test
    fun `an unreadable stored document counts as empty`() {
        memory.values[PresetStore.KEY] = "{broken"
        assertTrue(store.list().isEmpty())
        assertNull(store.save("fresh", options("x")))
        assertEquals(listOf("fresh"), store.list().map { it.name })
    }
}
