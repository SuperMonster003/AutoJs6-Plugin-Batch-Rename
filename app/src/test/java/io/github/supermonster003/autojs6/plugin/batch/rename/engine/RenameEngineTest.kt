package io.github.supermonster003.autojs6.plugin.batch.rename.engine

import io.github.supermonster003.autojs6.plugin.batch.rename.engine.EngineFixtures.engine
import io.github.supermonster003.autojs6.plugin.batch.rename.engine.EngineFixtures.millis
import io.github.supermonster003.autojs6.plugin.batch.rename.engine.EngineFixtures.newNames
import io.github.supermonster003.autojs6.plugin.batch.rename.engine.EngineFixtures.request
import io.github.supermonster003.autojs6.plugin.batch.rename.engine.regex.RhinoRegexEngine
import org.autojs.plugin.batchrename.api.BatchRenameErrorCodes
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Assert.fail
import org.junit.Test
import java.util.Locale

/**
 * The single-item pipeline of roadmap D16 / P2.2 / P2.3, led by the examples of the PowerRename documentation (roadmap
 * appendix A).
 * zh-CN: 路线图 D16 / P2.2 / P2.3 的单项流水线, 以 PowerRename 文档 (路线图附录 A) 中的示例为主.
 */
class RenameEngineTest {

    @Test
    fun `PowerRename documentation - match all occurrences`() {
        assertEquals(listOf("supertoys-powerrename.txt"), newNames(RenameOptions(search = "power", replace = "super"), "powertoys-powerrename.txt"))
        assertEquals(
            listOf("supertoys-superrename.txt"),
            newNames(RenameOptions(search = "power", replace = "super", matchAll = true), "powertoys-powerrename.txt"),
        )
    }

    @Test
    fun `PowerRename documentation - apply to filename or extension only`() {
        assertEquals(listOf("NewName.txt"), newNames(RenameOptions(search = "txt", replace = "NewName", target = RenameTarget.NAME), "txt.txt"))
        assertEquals(listOf("txt.NewExtension"), newNames(RenameOptions(search = "txt", replace = "NewExtension", target = RenameTarget.EXTENSION), "txt.txt"))
    }

    @Test
    fun `PowerRename documentation - enumerate items`() {
        val options = RenameOptions(search = "a", replace = "Image_\${padding=4;increment=2;start=10}_", enumerate = true)
        assertEquals(listOf("Image_0010_.jpg", "Image_0012_b.jpg", "Image_0014_bc.jpg"), newNames(options, "a.jpg", "ab.jpg", "abc.jpg"))
    }

    @Test
    fun `PowerRename documentation - file date and time`() {
        val options = RenameOptions(search = "powertoys", replace = "\$MMM-\$DD-\$YY-powertoys")
        val plan = engine.plan(
            request(options, "powertoys.png", "powertoys-menu.png").let { request ->
                request.copy(items = request.items.mapIndexed { index, item -> item.copy(lastModified = millis(2020, 11, 2 + index)) })
            },
        )
        assertEquals(listOf("Nov-02-20-powertoys.png", "Nov-03-20-powertoys-menu.png"), plan.items.map { it.newName })
    }

    @Test
    fun `PowerRename documentation - regular expression examples`() {
        fun regex(search: String, replace: String, vararg names: String) =
            newNames(RenameOptions(search = search, replace = replace, regex = true, target = RenameTarget.ALL), *names)
        assertEquals(listOf("foo_photo.png", "notes.txt"), regex("(.*).png", "foo_\$1.png", "photo.png", "notes.txt"))
        assertEquals(listOf("photo_foo.png"), regex("(.*).png", "\$1_foo.png", "photo.png"))
        assertEquals(listOf("2020-03-29.txt"), regex("(\\d\\d)-(\\d\\d)-(\\d\\d\\d\\d)", "\$3-\$2-\$1", "29-03-2020.txt"))
        assertEquals(listOf("def.txt"), regex("^.{3}", "", "abcdef.txt"))
        assertEquals(listOf("README.txt", "notes.md"), regex("(^\\w+\\.$)|(^\\w+$)", "\$2.txt", "README/", "notes.md"))
    }

    @Test
    fun `counters advance only for renamed items and start at zero`() {
        val options = RenameOptions(search = "IMG", replace = "Photo_\${}", enumerate = true)
        assertEquals(
            listOf("Photo_0_1.jpg", "notes.txt", "Photo_1_2.jpg", "Photo_2_3.jpg"),
            newNames(options, "IMG_1.jpg", "notes.txt", "IMG_2.jpg", "IMG_3.jpg"),
        )
        // An excluded item keeps its name and its number.
        val request = request(options, "IMG_1.jpg", "IMG_2.jpg", "IMG_3.jpg").let { it.copy(items = it.items.map { item -> item.copy(excluded = item.index == 1) }) }
        val plan = engine.plan(request)
        assertEquals(listOf("Photo_0_1.jpg", "IMG_2.jpg", "Photo_1_3.jpg"), plan.items.map { it.newName })
        assertEquals(PlanReason.EXCLUDED_BY_CALLER, plan.items[1].reason)
    }

    @Test
    fun `several counters advance together and negative values keep their sign`() {
        val options = RenameOptions(search = "x", replace = "\${start=10;increment=-5;padding=2}~\${padding=3}", enumerate = true)
        assertEquals(listOf("10~000", "05~001", "00~002", "-05~003"), newNames(options, "x/", "x/", "x/", "x/"))
    }

    @Test
    fun `variables stay literal while their option is off`() {
        val options = RenameOptions(search = "a", replace = "\${}\${rstringdigit=3}", dateTime = DateTimeSource.NONE)
        assertEquals(listOf("\${}\${rstringdigit=3}.jpg"), newNames(options, "a.jpg"))
        assertEquals(listOf("\$YYYY.jpg"), newNames(RenameOptions(search = "a", replace = "\$YYYY", dateTime = DateTimeSource.NONE), "a.jpg"))
    }

    @Test
    fun `dollar escapes keep variables literal in both modes`() {
        assertEquals(listOf("\$YYYY-2020.jpg"), newNames(RenameOptions(search = "a", replace = "\$\$YYYY-\$YYYY"), "a.jpg"))
        assertEquals(listOf("\$YYYY-2020.jpg"), newNames(RenameOptions(search = "a", replace = "\$\$YYYY-\$YYYY", regex = true), "a.jpg"))
        // Plain-text mode never reads replacement patterns.
        assertEquals(listOf("\$1\$&.jpg"), newNames(RenameOptions(search = "a", replace = "\$1\$&"), "a.jpg"))
        assertEquals(listOf("a(a).jpg"), newNames(RenameOptions(search = "(a)", replace = "\$1(\$&)", regex = true), "a.jpg"))
    }

    @Test
    fun `date variables use the modification time or the plan time`() {
        val replace = "\$YYYY\$YY\$Y-\$MMMM-\$MMM-\$MM-\$M-\$DDDD-\$DDD-\$DD-\$D-\$hh\$h-\$mm\$m-\$ss\$s-\$fff\$ff\$f"
        val modified = newNames(RenameOptions(search = "^", replace = replace, regex = true), "x.jpg").single()
        // 2020-11-02 13:05:09.087 UTC, a Monday; $ff and $f are the first two digits and the first digit of the milliseconds.
        assertEquals("2020200-November-Nov-11-11-Monday-Mon-02-2-1313-055-099-087080x.jpg", modified)
        val now = engine.plan(request(RenameOptions(search = "^", replace = "\$YYYY-\$MM-\$DD \$hh.\$mm", regex = true, dateTime = DateTimeSource.NOW), "x.jpg"))
        assertEquals("2026-10-15 08.30x.jpg", now.items.single().newName)
    }

    @Test
    fun `date names follow the request locale`() {
        val options = RenameOptions(search = "^", replace = "\$MMMM \$DDDD ", regex = true)
        assertEquals("\u5341\u4e00\u6708 \u661f\u671f\u4e00 x.jpg", newNames(options, "x.jpg", locale = Locale.SIMPLIFIED_CHINESE).single())
        assertEquals("\u043d\u043e\u044f\u0431\u0440\u044c \u043f\u043e\u043d\u0435\u0434\u0435\u043b\u044c\u043d\u0438\u043a x.jpg", newNames(options, "x.jpg", locale = Locale.forLanguageTag("ru")).single())
    }

    @Test
    fun `random values use their character sets and lengths`() {
        val options = RenameOptions(search = "^", replace = "\${rstringalnum=5}-\${rstringalpha=3}-\${rstringdigit=4}-{\${ruuidv4}}-", regex = true, random = true)
        assertEquals(listOf("ABCDE-ABC-0123-{123e4567-e89b-42d3-a456-426614174000}-x.jpg"), newNames(options, "x.jpg"))
        val real = RenameEngine().plan(request(options.copy(replace = "\${rstringalnum=12}_\${ruuidv4}"), "x.jpg")).items.single().newName
        assertTrue(real, Regex("[A-Za-z0-9]{12}_[0-9a-f]{8}-[0-9a-f]{4}-4[0-9a-f]{3}-[89ab][0-9a-f]{3}-[0-9a-f]{12}x\\.jpg").matches(real))
    }

    @Test
    fun `include options and depth exclude items`() {
        val request = request(RenameOptions(search = "a", replace = "b", includeFiles = false), "a.txt", "a/").let { r ->
            r.copy(items = r.items + RenameItem(2, EngineFixtures.PARENT, "aa", ItemKind.DIRECTORY, depth = 1))
        }
        val plan = engine.plan(request)
        assertEquals(listOf(PlanItemStatus.EXCLUDED, PlanItemStatus.RENAME, PlanItemStatus.RENAME), plan.items.map { it.status })
        assertEquals(PlanReason.NOT_INCLUDED, plan.items[0].reason)
        val noSubfolders = engine.plan(request.copy(options = request.options.copy(includeFiles = true, includeSubfolders = false)))
        assertEquals(listOf("b.txt", "b", "aa"), noSubfolders.items.map { it.newName })
        assertEquals(PlanReason.NOT_INCLUDED, noSubfolders.items[2].reason)
        val noFolders = engine.plan(request.copy(options = request.options.copy(includeFiles = true, includeFolders = false)))
        assertEquals(listOf(PlanItemStatus.RENAME, PlanItemStatus.EXCLUDED, PlanItemStatus.EXCLUDED), noFolders.items.map { it.status })
    }

    @Test
    fun `targets split names at the last dot of files only`() {
        val name = RenameOptions(search = "\\.", replace = "_", regex = true, matchAll = true, target = RenameTarget.NAME)
        // A leading or trailing dot is part of the stem, and folders have no extension.
        assertEquals(listOf("archive_tar.gz", "_bashrc", "v1_2", "trailing_"), newNames(name, "archive.tar.gz", ".bashrc", "v1.2/", "trailing."))
        val extension = RenameOptions(search = ".+", replace = "TXT", regex = true, target = RenameTarget.EXTENSION)
        val plan = engine.plan(request(extension, "a.md", ".bashrc", "dir.d/", "b."))
        assertEquals(listOf("a.TXT", ".bashrc", "dir.d", "b."), plan.items.map { it.newName })
        assertEquals(listOf(PlanReason.NO_MATCH, PlanReason.NO_MATCH, PlanReason.NO_MATCH), plan.items.drop(1).map { it.reason })
        // Removing the extension leaves no trailing dot.
        assertEquals(listOf("a"), newNames(RenameOptions(search = "md", replace = "", target = RenameTarget.EXTENSION), "a.md"))
    }

    @Test
    fun `unmatched and unchanged items carry their reasons`() {
        val plan = engine.plan(request(RenameOptions(search = "zzz", replace = "y"), "a.txt"))
        assertEquals(PlanItemStatus.UNCHANGED to PlanReason.NO_MATCH, plan.items.single().let { it.status to it.reason })
        val same = engine.plan(request(RenameOptions(search = "a", replace = "a"), "a.txt"))
        assertEquals(PlanItemStatus.UNCHANGED to PlanReason.SAME_NAME, same.items.single().let { it.status to it.reason })
        assertFalse(plan.rejected)
    }

    @Test
    fun `an empty search applies formatting and cleanup to every included item`() {
        assertEquals(listOf("photo.jpg", "readme"), newNames(RenameOptions(search = "", replace = "ignored", format = TextFormat.LOWER), "PHOTO.JPG", "README/"))
        assertEquals(listOf("a.txt"), newNames(RenameOptions(search = "", replace = "ignored"), "a.txt"))
    }

    @Test
    fun `formatting and local extensions run after the replacement`() {
        val options = RenameOptions(search = "IMG", replace = "\uff30\uff48\uff4f\uff54\uff4f\u3000", halfWidth = true, trim = true, format = TextFormat.UPPER)
        assertEquals(listOf("PHOTO .JPG"), newNames(options, "IMG.jpg"))
        val sanitize = RenameOptions(search = "x", replace = "a:b*c", sanitize = true, target = RenameTarget.NAME)
        assertEquals(listOf("a_b_c.txt"), newNames(sanitize, "x.txt"))
    }

    @Test
    fun `continued numbering starts after the largest existing number`() {
        val options = RenameOptions(search = "IMG_\\d+", replace = "Photo_\${padding=4}", regex = true, enumerate = true, continueNumbering = true)
        assertEquals(
            listOf("Photo_0013.jpg", "Photo_0014.jpg"),
            newNames(options, "IMG_1.jpg", "IMG_2.jpg", existingNames = listOf("Photo_0012.jpg", "Photo_0003.jpg", "Other_0099.jpg")),
        )
        // Without a matching existing name, or without the option, numbering starts at the start value.
        assertEquals(listOf("Photo_0000.jpg"), newNames(options, "IMG_1.jpg", existingNames = listOf("Other_0099.jpg")))
        assertEquals(listOf("Photo_0000.jpg"), newNames(options.copy(continueNumbering = false), "IMG_1.jpg", existingNames = listOf("Photo_0012.jpg")))
        // With an increment of 5 from 10, the next value above 12 is 15.
        val stepped = options.copy(replace = "Photo_\${start=10;increment=5;padding=4}")
        assertEquals(listOf("Photo_0015.jpg"), newNames(stepped, "IMG_1.jpg", existingNames = listOf("Photo_0012.jpg")))
    }

    @Test
    fun `whole-request errors carry contract error codes`() {
        assertError(BatchRenameErrorCodes.INVALID_OPTIONS) { engine.plan(request(RenameOptions(search = "(", regex = true), "a")) }
        assertError(BatchRenameErrorCodes.LIMIT_EXCEEDED) { engine.plan(request(RenameOptions(search = "a", replace = "\${rstringalnum=257}", random = true), "a")) }
        assertError(BatchRenameErrorCodes.LIMIT_EXCEEDED) {
            RenameEngine(regexEngine = RhinoRegexEngine(timeBudgetMillis = 100)).plan(request(RenameOptions(search = "^(a+)+$", regex = true), "a".repeat(40) + "!"))
        }
        val valid = request(RenameOptions(search = "a"), "a", "b")
        assertError(BatchRenameErrorCodes.INVALID_REQUEST) { engine.plan(valid.copy(items = valid.items.reversed())) }
        assertError(BatchRenameErrorCodes.INVALID_REQUEST) { engine.plan(valid.copy(items = valid.items.map { it.copy(parentId = "missing") })) }
        assertError(BatchRenameErrorCodes.INVALID_REQUEST) { engine.plan(valid.copy(items = listOf(valid.items[0].copy(name = "a/b")))) }
        assertError(BatchRenameErrorCodes.INVALID_REQUEST) { engine.plan(valid.copy(parents = valid.parents + valid.parents)) }
        assertError(BatchRenameErrorCodes.LIMIT_EXCEEDED) { engine.plan(valid.copy(items = listOf(valid.items[0].copy(name = "a".repeat(1025))))) }
        assertError(BatchRenameErrorCodes.LIMIT_EXCEEDED) {
            engine.plan(valid.copy(items = List(2001) { RenameItem(it, EngineFixtures.PARENT, "f$it", ItemKind.FILE) }))
        }
    }

    private fun assertError(code: String, block: () -> Unit) {
        try {
            block()
            fail("expected $code")
        } catch (error: PlanException) {
            assertEquals(error.message, code, error.code)
        }
    }
}
