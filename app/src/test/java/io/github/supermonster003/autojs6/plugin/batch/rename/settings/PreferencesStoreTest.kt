package io.github.supermonster003.autojs6.plugin.batch.rename.settings

import io.github.supermonster003.autojs6.plugin.batch.rename.engine.ConflictPolicy
import io.github.supermonster003.autojs6.plugin.batch.rename.engine.DateTimeSource
import io.github.supermonster003.autojs6.plugin.batch.rename.engine.PinyinCase
import io.github.supermonster003.autojs6.plugin.batch.rename.engine.PinyinSeparator
import io.github.supermonster003.autojs6.plugin.batch.rename.engine.PinyinTone
import io.github.supermonster003.autojs6.plugin.batch.rename.engine.RenameOptions
import io.github.supermonster003.autojs6.plugin.batch.rename.engine.RenameTarget
import io.github.supermonster003.autojs6.plugin.batch.rename.engine.TextFormat
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

/** Remembered options and recent texts (roadmap D25, P4.3). zh-CN: 记住的选项与最近使用的文本. */
class PreferencesStoreTest {

    private class MemoryStore : KeyValueStore {
        val values = LinkedHashMap<String, String>()

        override fun getString(key: String): String? = values[key]

        override fun edit(block: KeyValueEditor.() -> Unit) {
            object : KeyValueEditor {
                override fun putString(key: String, value: String) {
                    values[key] = value
                }

                override fun remove(key: String) {
                    values.remove(key)
                }
            }.block()
        }
    }

    private val memory = MemoryStore()
    private val store = PreferencesStore(memory)

    private val custom = RenameOptions(
        search = "IMG_(\\d+)",
        replace = "Photo-$1",
        regex = true,
        target = RenameTarget.NAME,
        format = TextFormat.UPPER,
        dateTime = DateTimeSource.NOW,
        onConflict = ConflictPolicy.NUMBER,
        conflictNumberPattern = "_{n}",
        pinyin = true,
        pinyinTone = PinyinTone.NUMBER,
        pinyinSeparator = PinyinSeparator.HYPHEN,
        pinyinCase = PinyinCase.CAPITALIZE,
        pinyinInitials = true,
        includeSubfolders = false,
    )

    @Test
    fun `options round trip and fall back to the defaults`() {
        assertTrue(store.rememberOptions)
        assertEquals(PreferencesStore.DEFAULT_OPTIONS, store.options())
        store.saveOptions(custom)
        assertEquals(custom, PreferencesStore(memory).options())
        store.resetOptions()
        assertEquals(PreferencesStore.DEFAULT_OPTIONS, store.options())
    }

    @Test
    fun `turning remembering off forgets and stops saving`() {
        store.saveOptions(custom)
        store.rememberOptions = false
        assertFalse(memory.values.keys.any { it.startsWith("option.") })
        store.saveOptions(custom)
        assertEquals(PreferencesStore.DEFAULT_OPTIONS, store.options())
        store.rememberOptions = true
        assertEquals(PreferencesStore.DEFAULT_OPTIONS, store.options())
    }

    @Test
    fun `a stored value the parser refuses falls back to the defaults`() {
        store.saveOptions(custom)
        memory.values["option.onConflict"] = "rename"
        assertEquals(PreferencesStore.DEFAULT_OPTIONS, store.options())
        memory.values["option.onConflict"] = "number"
        memory.values["option.regex"] = "maybe"
        assertEquals(PreferencesStore.DEFAULT_OPTIONS, store.options())
    }

    @Test
    fun `recent texts are newest first, without repeats and bounded`() {
        (1..12).forEach { store.remember("search $it", if (it % 2 == 0) "even" else "") }
        assertEquals((12 downTo 3).map { "search $it" }, store.recentSearches())
        assertEquals(listOf("even"), store.recentReplacements())
        store.remember("search 5", "odd")
        assertEquals("search 5", store.recentSearches().first())
        assertEquals(PreferencesStore.MAX_RECENT, store.recentSearches().size)
        assertEquals(1, store.recentSearches().count { it == "search 5" })
        assertEquals(listOf("odd", "even"), store.recentReplacements())
        store.clearHistory()
        assertTrue(store.recentSearches().isEmpty() && store.recentReplacements().isEmpty())
        assertFalse("no path or file name is ever stored", memory.values.keys.any { it.contains("path") })
    }

    @Test
    fun `option checks report what the parser refuses`() {
        assertNull(OptionsCodec.check(custom))
        assertNotNull(OptionsCodec.check(custom.copy(conflictNumberPattern = "no placeholder")))
        assertNotNull(OptionsCodec.check(custom.copy(sanitize = true, sanitizeReplacement = "a/b")))
    }
}
