package io.github.supermonster003.autojs6.plugin.batch.rename.service

import io.github.supermonster003.autojs6.plugin.batch.rename.engine.EngineFixtures
import io.github.supermonster003.autojs6.plugin.batch.rename.engine.ConflictPolicy
import io.github.supermonster003.autojs6.plugin.batch.rename.engine.RenameEngine
import io.github.supermonster003.autojs6.plugin.batch.rename.engine.RenameOptions
import io.github.supermonster003.autojs6.plugin.batch.rename.engine.TextFormat
import io.github.supermonster003.autojs6.plugin.batch.rename.presets.Preset
import io.github.supermonster003.autojs6.plugin.batch.rename.presets.PresetDocument
import io.github.supermonster003.autojs6.plugin.batch.rename.presets.PresetStore
import io.github.supermonster003.autojs6.plugin.batch.rename.settings.MemoryStore
import org.autojs.plugin.batchrename.api.BatchRenameErrorCodes
import org.autojs.plugin.batchrename.api.BatchRenameValues
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import kotlin.random.Random

/**
 * The contract v1 transport of roadmap P2.5: requests decoded from Bundle-shaped maps, answers encoded with every key
 * the host reads, and a planner that answers every malformed request with an error code instead of throwing.
 * zh-CN: 路线图 P2.5 的契约 v1 传输: 从 Bundle 形态的 Map 解码请求, 编码带有宿主读取的全部键的应答, 且规划器对每个畸形请求都以
 * 错误码应答而不抛出异常.
 */
class PlanCodecTest {

    private val logged = mutableListOf<String>()
    private val planner = BatchRenamePlanner(RenameEngine(random = EngineFixtures.fixedRandom)) { logged += it }

    private fun parent(id: String = "p", names: List<Any?> = listOf("IMG_1.jpg", "IMG_2.jpg", "notes.txt")): Map<String, Any?> =
        mapOf("parentId" to id, "existingNames" to names, "caseInsensitive" to false, "restrictedCharacters" to false)

    private fun item(index: Int, name: String, kind: Int = BatchRenameValues.KIND_FILE, parentId: String = "p"): Map<String, Any?> =
        mapOf("index" to index, "parentId" to parentId, "name" to name, "kind" to kind, "depth" to 0, "lastModified" to 0L, "length" to 10L, "excluded" to false)

    private fun request(
        items: List<Map<String, Any?>> = listOf(item(0, "IMG_1.jpg"), item(1, "IMG_2.jpg"), item(2, "notes.txt")),
        options: Map<String, Any?> = mapOf("search" to "IMG", "replace" to "Photo"),
        parents: List<Map<String, Any?>> = listOf(parent()),
        version: Int = 1,
    ): MutableMap<String, Any?> = mutableMapOf(
        "contractVersion" to version, "options" to options, "parents" to parents, "items" to items,
        "locale" to "en", "timeZone" to "Asia/Shanghai", "now" to 0L,
    )

    @Suppress("UNCHECKED_CAST")
    private fun Map<String, Any?>.items() = get("items") as List<Map<String, Any?>>

    @Test
    fun `a request goes through the engine and comes back with every answer key`() {
        val answer = planner.answer(request())
        assertEquals(1, answer["contractVersion"])
        assertEquals(false, answer["rejected"])
        assertFalse("a plan never carries an error code", answer.containsKey("errorCode"))
        assertEquals(
            listOf(
                mapOf("index" to 0, "newName" to "Photo_1.jpg", "status" to "rename", "twoPhase" to false, "overwrites" to false),
                mapOf("index" to 1, "newName" to "Photo_2.jpg", "status" to "rename", "twoPhase" to false, "overwrites" to false),
                mapOf("index" to 2, "newName" to "notes.txt", "status" to "unchanged", "reason" to "noMatch", "twoPhase" to false, "overwrites" to false),
            ),
            answer.items(),
        )
        assertEquals(mapOf("rename" to 2, "unchanged" to 1, "conflict" to 0, "invalid" to 0, "excluded" to 0), answer["counts"])
    }

    @Test
    fun `locale, time zone and plan time reach the date variables`() {
        val options = mapOf("search" to "^", "replace" to "\$hh \$MMMM ", "regex" to true, "dateTime" to "now")
        val answer = planner.answer(request(items = listOf(item(0, "x")), options = options, parents = listOf(parent(names = listOf("x")))))
        // Epoch 0 is 08:00 in Shanghai on 1 January 1970.
        assertEquals("08 January x", answer.items().single()["newName"])
    }

    @Test
    fun `optional keys take their defaults`() {
        val minimal = mutableMapOf<String, Any?>(
            "contractVersion" to 1, "options" to mapOf("search" to "a", "replace" to "b"),
            "parents" to listOf(mapOf("parentId" to "p")),
            "items" to listOf(mapOf("index" to 0, "parentId" to "p", "name" to "a", "kind" to 2)),
        )
        val answer = planner.answer(minimal)
        assertEquals("b", answer.items().single()["newName"])
        // Longs accept Int values.
        val intTime = request(items = listOf(item(0, "IMG_1.jpg").toMutableMap().apply { put("lastModified", 5) }))
        assertEquals("rename", planner.answer(intTime).items().single()["status"])
    }

    @Test
    fun `unknown option keys are ignored and logged`() {
        planner.answer(request(options = mapOf("search" to "IMG", "futureOption" to true)))
        assertEquals(listOf("Ignored unknown option keys [futureOption]"), logged)
    }

    @Test
    fun `malformed requests answer contract error codes`() {
        fun code(values: Map<String, Any?>?) = planner.answer(values)["errorCode"]
        assertEquals(BatchRenameErrorCodes.INVALID_REQUEST, code(null))
        assertEquals(BatchRenameErrorCodes.UNSUPPORTED_CONTRACT_VERSION, code(request(version = 2)))
        assertEquals(2, planner.answer(request(version = 2))["contractVersion"])
        assertEquals(BatchRenameErrorCodes.UNSUPPORTED_CONTRACT_VERSION, code(request().apply { remove("contractVersion") }))
        assertEquals(BatchRenameErrorCodes.UNSUPPORTED_CONTRACT_VERSION, code(request().apply { put("contractVersion", 1L) }))
        val invalidRequests = listOf(
            request().apply { remove("options") },
            request().apply { put("options", "search=a") },
            request().apply { remove("items") },
            request().apply { put("items", mapOf("0" to item(0, "a"))) },
            request().apply { put("parents", listOf("p")) },
            request(items = listOf(item(0, "IMG_1.jpg").toMutableMap().apply { remove("name") })),
            request(items = listOf(item(0, "IMG_1.jpg", kind = 3))),
            request(items = listOf(item(0, "IMG_1.jpg").toMutableMap().apply { put("depth", "0") })),
            request(items = listOf(item(0, "IMG_1.jpg").toMutableMap().apply { put("excluded", 1) })),
            request(items = listOf(item(0, "IMG_1.jpg").toMutableMap().apply { put("lastModified", 1.5) })),
            request(items = listOf(item(0, "IMG_1.jpg", parentId = "q"))),
            request(items = listOf(item(1, "IMG_1.jpg"))),
            request(parents = listOf(parent(names = listOf("a", 7)))),
            request(parents = listOf(parent(id = ""))),
            request().apply { put("timeZone", "Mars/Olympus_Mons") },
            request().apply { put("locale", 42) },
        )
        invalidRequests.forEachIndexed { index, values -> assertEquals("case $index", BatchRenameErrorCodes.INVALID_REQUEST, code(values)) }
        assertEquals(BatchRenameErrorCodes.INVALID_OPTIONS, code(request(options = mapOf("search" to "a", "target" to "stem"))))
        assertEquals(BatchRenameErrorCodes.INVALID_OPTIONS, code(request(options = mapOf("search" to "(", "regex" to true))))
        assertEquals(BatchRenameErrorCodes.LIMIT_EXCEEDED, code(request(items = List(2001) { item(it, "f$it") })))
        assertEquals(BatchRenameErrorCodes.LIMIT_EXCEEDED, code(request(parents = List(513) { parent(id = "p$it") })))
        // 300 existing names of 1000 characters exceed the 262144 characters of one request.
        assertEquals(BatchRenameErrorCodes.LIMIT_EXCEEDED, code(request(parents = listOf(parent(names = List(300) { "n".repeat(999) + it % 10 })))))
    }

    @Test
    fun `a named preset fills the options the request leaves out and the answer shows the result`() {
        val presets = PresetStore(MemoryStore()).apply {
            save("photos", RenameOptions(search = "IMG", replace = "Pic", format = TextFormat.UPPER, onConflict = ConflictPolicy.SKIP))
        }
        val withPresets = BatchRenamePlanner(RenameEngine(random = EngineFixtures.fixedRandom), presets = { name -> presets.find(name)?.options })
        val preset = withPresets.answer(request(options = mapOf("preset" to "photos")))
        assertEquals(listOf("PIC_1.JPG", "PIC_2.JPG", "notes.txt"), preset.items().map { it["newName"] })
        val overridden = withPresets.answer(request(options = mapOf("preset" to "photos", "replace" to "Shot", "format" to "none")))
        assertEquals(listOf("Shot_1.jpg", "Shot_2.jpg", "notes.txt"), overridden.items().map { it["newName"] })
        @Suppress("UNCHECKED_CAST")
        val effective = overridden["options"] as Map<String, Any?>
        assertEquals(listOf("IMG", "Shot", "none", "skip"), listOf(effective["search"], effective["replace"], effective["format"], effective["onConflict"]))
        assertFalse(effective.containsKey("preset"))
        // Sending the effective options back reproduces the plan without the preset. zh-CN: 回传生效选项可在没有预设时重现方案.
        assertEquals(overridden.items(), planner.answer(request(options = effective)).items())

        assertEquals(BatchRenameErrorCodes.INVALID_OPTIONS, withPresets.answer(request(options = mapOf("preset" to "missing")))["errorCode"])
        assertEquals(BatchRenameErrorCodes.INVALID_OPTIONS, withPresets.answer(request(options = mapOf("preset" to 3)))["errorCode"])
        assertEquals(BatchRenameErrorCodes.INVALID_OPTIONS, planner.answer(request(options = mapOf("preset" to "photos")))["errorCode"])
    }

    @Test
    fun `the preset calls list and import presets with contract answers`() {
        val store = PresetStore(MemoryStore()).apply { save("a", RenameOptions(search = "x")) }
        val calls = PresetCalls({ store })
        val listed = calls.list(mapOf("contractVersion" to 1))
        @Suppress("UNCHECKED_CAST")
        val presets = listed["presets"] as List<Map<String, Any?>>
        assertEquals(listOf("a"), presets.map { it["name"] })
        assertEquals("x", (presets.single()["options"] as Map<*, *>)["search"])

        val document = PresetDocument.encode(listOf(Preset("a", RenameOptions(search = "y")), Preset("b", RenameOptions(search = "z"))))
        val imported = calls.import(mapOf("contractVersion" to 1, "document" to document, "onConflict" to "overwrite"))
        assertEquals(listOf("a", "b"), imported["imported"])
        assertEquals(emptyList<String>(), imported["skipped"])
        assertEquals("y", store.find("a")!!.options.search)
        val skipped = calls.import(mapOf("contractVersion" to 1, "document" to document))
        assertEquals(listOf("a", "b"), skipped["skipped"])
        val refused = calls.import(
            mapOf("contractVersion" to 1, "document" to """{"format": "autojs6-batch-rename-presets", "version": 1, "presets": [{"options": {}}]}"""),
        )
        assertEquals(listOf(mapOf("index" to 0, "reason" to "the name is missing or blank")), refused["rejected"])

        assertEquals(BatchRenameErrorCodes.INVALID_OPTIONS, calls.import(mapOf("contractVersion" to 1, "document" to "[]"))["errorCode"])
        assertEquals(BatchRenameErrorCodes.INVALID_OPTIONS, calls.import(mapOf("contractVersion" to 1, "document" to document, "onConflict" to "merge"))["errorCode"])
        assertEquals(BatchRenameErrorCodes.INVALID_REQUEST, calls.import(mapOf("contractVersion" to 1))["errorCode"])
        assertEquals(BatchRenameErrorCodes.UNSUPPORTED_CONTRACT_VERSION, calls.list(mapOf("contractVersion" to 2))["errorCode"])
        assertEquals(BatchRenameErrorCodes.INVALID_REQUEST, calls.list(null)["errorCode"])
    }

    @Test
    fun `error messages stay short`() {
        val answer = planner.answer(request(items = listOf(item(0, "IMG_1.jpg", parentId = "q".repeat(4000)))))
        assertTrue((answer["errorMessage"] as String).length <= 1024)
        assertEquals(setOf("contractVersion", "errorCode", "errorMessage"), answer.keys)
    }

    @Test
    fun `randomly damaged requests never throw`() {
        val random = Random(20261015)
        val junk = listOf<Any?>(null, 1, 2L, true, "", "x", 1.5, listOf<Any?>(), listOf(1, "a", null), mapOf<String, Any?>(), mapOf("search" to 1), List(3) { "s" })
        repeat(2000) {
            val values = request()
            val damaged: MutableMap<String, Any?> = when (random.nextInt(4)) {
                0 -> values.apply { put(keys.random(random), junk.random(random)) }
                1 -> values.apply { remove(keys.random(random)) }
                2 -> request(items = listOf(item(0, "IMG_1.jpg").toMutableMap().apply { put(keys.random(random), junk.random(random)) }))
                else -> request(options = mutableMapOf<String, Any?>("search" to "IMG").apply {
                    put(listOf("replace", "regex", "target", "format", "dateTime", "onConflict", "regexFlags", "conflictNumberPattern").random(random), junk.random(random))
                })
            }
            val answer = planner.answer(damaged)
            assertTrue("$damaged -> $answer", answer.containsKey("errorCode") || answer.containsKey("items"))
            assertFalse("$damaged -> $answer", answer["errorCode"] == BatchRenameErrorCodes.INTERNAL_ERROR)
        }
    }
}
