package io.github.supermonster003.autojs6.plugin.batch.rename.engine

import io.github.supermonster003.autojs6.plugin.batch.rename.engine.EngineFixtures.engine
import io.github.supermonster003.autojs6.plugin.batch.rename.engine.EngineFixtures.request
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Validation and conflicts of roadmap D19 / D20 / P2.4. Most cases feed [ConflictResolver] proposals directly (a swap
 * cannot come from one replacement); the last ones run through the whole engine.
 * zh-CN: 路线图 D19 / D20 / P2.4 的校验与冲突. 多数用例直接向 [ConflictResolver] 提供提议 (单次替换无法产生交换);
 * 最后几项经过整个引擎.
 */
class ConflictResolverTest {

    /** Renames [renames] (old name to new name) among [others], which keep their names. */
    private fun resolve(
        renames: List<Pair<String, String>>,
        others: List<String> = emptyList(),
        policy: ConflictPolicy = ConflictPolicy.FAIL,
        caseInsensitive: Boolean = false,
        restrictedCharacters: Boolean = false,
        pattern: String = " ({n})",
    ): RenamePlan {
        val names = renames.map { it.first }
        val options = RenameOptions(search = "", onConflict = policy, conflictNumberPattern = pattern)
        val request = request(
            options, *names.toTypedArray(), existingNames = others,
            caseInsensitive = caseInsensitive, restrictedCharacters = restrictedCharacters,
        )
        val proposals = renames.mapIndexed { index, (old, new) ->
            if (old == new) PlanItem(index, old, PlanItemStatus.UNCHANGED, PlanReason.SAME_NAME) else PlanItem(index, new, PlanItemStatus.RENAME)
        }
        return ConflictResolver.resolve(request, proposals)
    }

    private fun RenamePlan.summary() = items.map { listOfNotNull(it.newName, it.status.value, it.reason?.value, "2p".takeIf { _ -> it.twoPhase }, "ow".takeIf { _ -> it.overwrites }).joinToString(" ") }

    @Test
    fun `swaps and cycles are executable in two phases`() {
        val swap = resolve(listOf("a.txt" to "b.txt", "b.txt" to "a.txt"))
        assertEquals(listOf("b.txt rename 2p", "a.txt rename 2p"), swap.summary())
        assertFalse(swap.rejected)
        val cycle = resolve(listOf("1" to "2", "2" to "3", "3" to "1"))
        assertEquals(listOf("2 rename 2p", "3 rename 2p", "1 rename 2p"), cycle.summary())
        // In a chain only the item whose target is still held needs a temporary name.
        val chain = resolve(listOf("a" to "b", "b" to "c"))
        assertEquals(listOf("b rename 2p", "c rename"), chain.summary())
    }

    @Test
    fun `case-only changes need two phases on case-insensitive volumes only`() {
        assertEquals(listOf("Photo.jpg rename 2p"), resolve(listOf("photo.jpg" to "Photo.jpg"), caseInsensitive = true).summary())
        assertEquals(listOf("Photo.jpg rename"), resolve(listOf("photo.jpg" to "Photo.jpg")).summary())
    }

    @Test
    fun `the fail policy reports every conflicting item and rejects the plan`() {
        val duplicates = resolve(listOf("a" to "x", "b" to "x", "c" to "y"))
        assertEquals(listOf("x conflict duplicateInBatch", "x conflict duplicateInBatch", "y rename"), duplicates.summary())
        assertTrue(duplicates.rejected)
        val existing = resolve(listOf("a" to "keep"), others = listOf("keep"))
        assertEquals(listOf("keep conflict existingEntry"), existing.summary())
        assertTrue(existing.rejected)
        assertEquals(mapOf(PlanItemStatus.RENAME to 1, PlanItemStatus.UNCHANGED to 0, PlanItemStatus.CONFLICT to 2, PlanItemStatus.INVALID to 0, PlanItemStatus.EXCLUDED to 0), duplicates.counts)
    }

    @Test
    fun `names compare case-insensitively where the directory says so`() {
        assertEquals(listOf("photo.jpg conflict existingEntry"), resolve(listOf("a.jpg" to "photo.jpg"), others = listOf("Photo.JPG"), caseInsensitive = true).summary())
        assertEquals(listOf("photo.jpg rename"), resolve(listOf("a.jpg" to "photo.jpg"), others = listOf("Photo.JPG")).summary())
        assertEquals(listOf("X conflict duplicateInBatch", "x conflict duplicateInBatch"), resolve(listOf("a" to "X", "b" to "x"), caseInsensitive = true).summary())
    }

    @Test
    fun `skip keeps the first of duplicates and cascades to names that stay occupied`() {
        val plan = resolve(listOf("a" to "x", "b" to "x"), policy = ConflictPolicy.SKIP)
        assertEquals(listOf("x rename", "x conflict duplicateInBatch"), plan.summary())
        assertFalse(plan.rejected)
        // "a" cannot move to the existing "keep", so it stays, and "b" can no longer take the name "a".
        val cascade = resolve(listOf("a" to "keep", "b" to "a"), others = listOf("keep"), policy = ConflictPolicy.SKIP)
        assertEquals(listOf("keep conflict existingEntry", "a conflict existingEntry"), cascade.summary())
        // Under fail the cascade is reported as well, so the preview shows every blocked item.
        assertEquals(listOf("keep conflict existingEntry", "a conflict existingEntry"), resolve(listOf("a" to "keep", "b" to "a"), others = listOf("keep")).summary())
    }

    @Test
    fun `number appends the first free number before the extension`() {
        val plan = resolve(listOf("a.jpg" to "x.jpg", "b.jpg" to "x.jpg", "c.jpg" to "photo.jpg", "d" to "dir"), others = listOf("photo.jpg", "photo (1).jpg", "dir"), policy = ConflictPolicy.NUMBER)
        assertEquals(listOf("x.jpg rename", "x (1).jpg rename numbered", "photo (2).jpg rename numbered", "dir (1) rename numbered"), plan.summary())
        assertFalse(plan.rejected)
        assertEquals(listOf("x.jpg rename", "x_1.jpg rename numbered"), resolve(listOf("a.jpg" to "x.jpg", "b.jpg" to "x.jpg"), policy = ConflictPolicy.NUMBER, pattern = "_{n}").summary())
        assertEquals(listOf(".bashrc (1) rename numbered"), resolve(listOf("a" to ".bashrc"), others = listOf(".bashrc"), policy = ConflictPolicy.NUMBER).summary())
    }

    @Test
    fun `numbering stops at 999 and at the name length limit`() {
        val taken = listOf("x.jpg") + (1..999).map { "x ($it).jpg" }
        assertEquals(listOf("x.jpg conflict numbersExhausted"), resolve(listOf("a.jpg" to "x.jpg"), others = taken, policy = ConflictPolicy.NUMBER).summary())
        val long = "n".repeat(251) + ".jpg"
        assertEquals(listOf("$long conflict numbersExhausted"), resolve(listOf("a.jpg" to long), others = listOf(long), policy = ConflictPolicy.NUMBER).summary())
    }

    @Test
    fun `overwrite replaces existing entries but never another renamed item`() {
        val plan = resolve(listOf("a" to "keep", "b" to "x", "c" to "x"), others = listOf("keep"), policy = ConflictPolicy.OVERWRITE)
        assertEquals(listOf("keep rename overwrite ow", "x rename", "x conflict duplicateInBatch"), plan.summary())
        assertFalse(plan.rejected)
    }

    @Test
    fun `invalid names keep their old names and reject the plan under every policy`() {
        val invalid = listOf(0, 2, 3, 4)
        ConflictPolicy.entries.forEach { policy ->
            // 86 CJK characters take 258 UTF-8 bytes.
            val plan = resolve(listOf("a" to "", "b" to "a", "c" to "..", "d" to "x/y", "e" to "\u7167".repeat(86)), policy = policy)
            assertEquals("$policy", listOf("empty", "dotName", "separator", "tooLong"), invalid.map { plan.items[it].reason?.value })
            assertTrue("$policy", invalid.all { plan.items[it].status == PlanItemStatus.INVALID })
            assertTrue("$policy", plan.rejected)
        }
        // The invalid item keeps occupying its old name. zh-CN: 无效条目继续占用原名.
        assertEquals("a conflict existingEntry", resolve(listOf("a" to "", "b" to "a"), policy = ConflictPolicy.SKIP).summary()[1])
        assertEquals(listOf("a:b invalid restrictedCharacter"), resolve(listOf("a" to "a:b"), restrictedCharacters = true).summary())
        assertEquals(listOf("a:b rename"), resolve(listOf("a" to "a:b")).summary())
        assertEquals(listOf("${"\u7167".repeat(85)} rename"), resolve(listOf("a" to "\u7167".repeat(85))).summary())
    }

    @Test
    fun `the engine plans swaps of extensions and rejects duplicates end to end`() {
        val options = RenameOptions(search = "^(\\w+)\\.(\\w+)$", replace = "\$1", regex = true, target = RenameTarget.ALL)
        val plan = engine.plan(request(options, "a.txt", "a.md"))
        assertEquals(listOf("a conflict duplicateInBatch", "a conflict duplicateInBatch"), plan.summary())
        assertTrue(plan.rejected)
        val numbered = engine.plan(request(options.copy(onConflict = ConflictPolicy.NUMBER), "a.txt", "a.md"))
        assertEquals(listOf("a rename", "a (1) rename numbered"), numbered.summary())
    }

    @Test
    fun `a plan of 2000 items stays well within the interactive budget`() {
        val names = (0 until 2000).map { "IMG_%04d.jpg".format(it) }
        val options = RenameOptions(search = "IMG_(\\d+)", replace = "Photo_\${padding=5}_\$1", regex = true, enumerate = true, format = TextFormat.LOWER)
        val request = request(options, *names.toTypedArray(), existingNames = (0 until 2000).map { "other_$it.png" })
        repeat(3) { engine.plan(request) }
        val millis = (0 until 3).minOf {
            val started = System.nanoTime()
            val plan = engine.plan(request)
            assertEquals(2000, plan.counts[PlanItemStatus.RENAME])
            (System.nanoTime() - started) / 1_000_000
        }
        println("2000-item plan: $millis ms")
        assertTrue("2000 items took $millis ms", millis < 300)
    }
}
