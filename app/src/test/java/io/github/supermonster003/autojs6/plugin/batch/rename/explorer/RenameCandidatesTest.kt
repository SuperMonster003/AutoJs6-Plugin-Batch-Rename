package io.github.supermonster003.autojs6.plugin.batch.rename.explorer

import io.github.supermonster003.autojs6.plugin.batch.rename.engine.EngineFixtures
import io.github.supermonster003.autojs6.plugin.batch.rename.engine.PlanItemStatus
import io.github.supermonster003.autojs6.plugin.batch.rename.engine.PlanReason
import io.github.supermonster003.autojs6.plugin.batch.rename.engine.RenameOptions
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.ZoneId
import java.util.Locale

/**
 * Candidate enumeration over the host session, request splitting and the preview (roadmap P4.1 / P4.2), against an
 * in-memory tree standing in for the session.
 * zh-CN: 基于宿主会话的候选项枚举, 请求拆分与预览 (路线图 P4.1 / P4.2), 以内存目录树代替会话.
 */
class RenameCandidatesTest {

    /** Target `t` is the folder `album`; paths below it are its descendants. */
    private class TreeSource(private val tree: Map<String, List<String>>, private val parent: List<String> = listOf("album", "notes.txt")) : RenameSource {
        val listed = mutableListOf<String>()

        override fun listParent() = ParentListing(parent, caseInsensitive = true, restrictedCharacters = false)

        override fun listChildren(targetId: String, relativePath: String): List<ListedChild> {
            listed += "$targetId:$relativePath"
            return tree[relativePath].orEmpty().map { raw ->
                val name = raw.removeSuffix("/").removeSuffix("@")
                val path = if (relativePath.isEmpty()) name else "$relativePath/$name"
                ListedChild(path, name, directory = raw.endsWith("/"), symbolicLink = raw.endsWith("@"), size = name.length.toLong(), lastModified = 1_000L)
            }
        }
    }

    private val tree = mapOf(
        "" to listOf("IMG_1.jpg", "IMG_2.jpg", "link@", "raw/"),
        "raw" to listOf("r.dng", "deeper/"),
        "raw/deeper" to listOf("x.txt"),
    )
    private val album = RequestTarget("t", "album", directory = true, size = -1, lastModified = 5)
    private val notes = RequestTarget("n", "notes.txt", directory = false, size = 9, lastModified = 7)

    private fun CandidateSet.paths() = candidates.map { "${it.targetId}:${it.relativePath}@${it.depth}" }

    @Test
    fun `selected targets come first, folders are walked depth first and links are skipped`() {
        val source = TreeSource(tree)
        val set = RenameCandidateEnumerator(source).enumerate(RenameScope.TARGETS, listOf(notes, album), includeSubfolders = true)
        assertEquals(listOf("n:@0", "t:@0", "t:IMG_1.jpg@1", "t:IMG_2.jpg@1", "t:raw@1", "t:raw/r.dng@2", "t:raw/deeper@2", "t:raw/deeper/x.txt@3"), set.paths())
        assertFalse(set.truncated)
        assertEquals(listOf("parent", "t/", "t/raw", "t/raw/deeper"), set.parents.map { it.id })
        assertEquals(listOf("IMG_1.jpg", "IMG_2.jpg", "link", "raw"), set.parents[1].existingNames)
        assertTrue("traits come from the parent listing", set.parents.all { it.caseInsensitive })
        assertEquals(9L, set.candidates[0].size)
        assertEquals(-1L, set.candidates[1].size)
        assertEquals("t/raw/r.dng", set.candidates[5].key)
    }

    @Test
    fun `without subfolders only the targets take part`() {
        val source = TreeSource(tree)
        val set = RenameCandidateEnumerator(source).enumerate(RenameScope.TARGETS, listOf(album), includeSubfolders = false)
        assertEquals(listOf("t:@0"), set.paths())
        assertEquals(emptyList<String>(), source.listed)
    }

    @Test
    fun `the folder menu renames what is inside the folder`() {
        val inside = RenameCandidateEnumerator(TreeSource(tree)).enumerate(RenameScope.INSIDE_DIRECTORY, listOf(album), includeSubfolders = false)
        assertEquals(listOf("t:IMG_1.jpg@0", "t:IMG_2.jpg@0", "t:raw@0"), inside.paths())
        val deep = RenameCandidateEnumerator(TreeSource(tree)).enumerate(RenameScope.INSIDE_DIRECTORY, listOf(album), includeSubfolders = true)
        assertEquals(6, deep.candidates.size)
        assertEquals(listOf(0, 0, 0, 1, 1, 2), deep.candidates.map { it.depth })
    }

    @Test
    fun `limits truncate the walk instead of failing`() {
        val few = RenameCandidateEnumerator(TreeSource(tree), maxItems = 3).enumerate(RenameScope.TARGETS, listOf(album), includeSubfolders = true)
        assertEquals(3, few.candidates.size)
        assertTrue(few.truncated)
        val shallow = RenameCandidateEnumerator(TreeSource(tree), maxDepth = 2).enumerate(RenameScope.TARGETS, listOf(album), includeSubfolders = true)
        assertTrue(shallow.truncated)
        assertFalse(shallow.paths().any { it.endsWith("@3") })
    }

    @Test
    fun `requests split by folder, deepest first, and keep a folder together`() {
        fun entry(parent: String, depth: Int, name: String) = RenameEntry("t", "$parent/$name", "$name-new", false, depth, parent)
        val entries = listOf(entry("a", 1, "x"), entry("a", 1, "y"), entry("b", 2, "z"), entry("root", 0, "a"))
        assertEquals(listOf(entries), RenameRequestSplitter.split(entries, limit = 100) { it.size })
        val split = RenameRequestSplitter.split(entries, limit = 2) { it.size }
        assertEquals(listOf(listOf("z"), listOf("x", "y"), listOf("a")), split.map { request -> request.map { it.relativePath.substringAfterLast('/') } })
        val packed = RenameRequestSplitter.split(entries, limit = 3) { it.size }
        assertEquals(listOf(3, 1), packed.map { it.size })
        try {
            RenameRequestSplitter.split(entries, limit = 1) { it.size }
            throw AssertionError("a folder of two entries does not fit a limit of one")
        } catch (e: RenameRequestSplitter.RequestTooLargeException) {
            assertEquals(2, e.entries)
        }
        assertEquals(emptyList<List<RenameEntry>>(), RenameRequestSplitter.split(emptyList(), 1) { it.size })
    }

    @Test
    fun `the preview plans with the engine, honors unchecked rows and reports refused options`() {
        val set = RenameCandidateEnumerator(TreeSource(tree)).enumerate(RenameScope.INSIDE_DIRECTORY, listOf(album), includeSubfolders = false)
        val planner = PreviewPlanner(EngineFixtures.engine, now = 0L, locale = Locale.ENGLISH, timeZone = ZoneId.of("UTC"))
        val preview = planner.plan(set, RenameOptions(search = "IMG_", replace = "Photo_"), excludedKeys = setOf("t/IMG_2.jpg"))
        assertEquals(listOf("Photo_1.jpg", "IMG_2.jpg", "raw"), preview.rows.map { it.newName })
        assertEquals(listOf(PlanItemStatus.RENAME, PlanItemStatus.EXCLUDED, PlanItemStatus.UNCHANGED), preview.rows.map { it.status })
        assertEquals(PlanReason.EXCLUDED_BY_CALLER, preview.rows[1].reason)
        assertTrue(preview.rows[1].excluded)
        assertTrue(preview.applicable)
        assertEquals(listOf(RenameEntry("t", "IMG_1.jpg", "Photo_1.jpg", false, 0, "t/")), preview.entries())

        val clash = planner.plan(set, RenameOptions(search = "IMG_\\d", replace = "raw", regex = true), excludedKeys = emptySet())
        assertTrue(clash.rejected)
        assertFalse(clash.applicable)
        assertEquals(2, clash.problemCount)

        val broken = planner.plan(set, RenameOptions(search = "(", regex = true), excludedKeys = emptySet())
        assertNotNull(broken.error)
        assertFalse(broken.applicable)
        assertEquals(Preview.EMPTY, planner.plan(CandidateSet(emptyList(), emptyList(), false), RenameOptions(search = "a"), emptySet()))
    }
}
