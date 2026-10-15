package io.github.supermonster003.autojs6.plugin.batch.rename.explorer

import io.github.supermonster003.autojs6.plugin.batch.rename.BatchRenamePlugin
import io.github.supermonster003.autojs6.plugin.batch.rename.RepositoryFiles
import org.autojs.plugin.explorer.api.ExplorerActionValues
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * The Explorer Action catalog (roadmap D23, P4.1): the three shapes the host's catalog policy accepts for
 * `targetRename`, labels that every locale defines and the release build keeps, and the activity the Manifest exports.
 * zh-CN: Explorer Action 动作目录 (路线图 D23, P4.1): 宿主目录策略为 `targetRename` 接受的三种形态, 每种语言都定义且 release
 * 构建保留的标签, 以及 Manifest 导出的 Activity.
 */
class BatchRenameCatalogTest {

    @Test
    fun `the catalog holds the three rename shapes`() {
        val snapshot = BatchRenameCatalog.actions.map {
            listOf(it.id, it.targetKind, it.cardinality, it.placement, it.mimeTypes, it.scope)
        }
        assertEquals(
            listOf(
                listOf("batch-rename-selection", ExplorerActionValues.TARGET_MIXED, ExplorerActionValues.CARDINALITY_MULTIPLE, ExplorerActionValues.PLACEMENT_SELECTION_TOOLBAR, listOf("*/*"), RenameScope.TARGETS),
                listOf("batch-rename-directory", ExplorerActionValues.TARGET_DIRECTORY, ExplorerActionValues.CARDINALITY_SINGLE, ExplorerActionValues.PLACEMENT_OVERFLOW, emptyList<String>(), RenameScope.INSIDE_DIRECTORY),
                listOf("batch-rename-single", ExplorerActionValues.TARGET_MIXED, ExplorerActionValues.CARDINALITY_SINGLE, ExplorerActionValues.PLACEMENT_RENAME_DIALOG, listOf("*/*"), RenameScope.TARGETS),
            ),
            snapshot,
        )
        assertEquals(25, BatchRenamePlugin.EXPLORER_PROTOCOL_VERSION)
        assertEquals(BatchRenameCatalog.actions[1], BatchRenameCatalog.action("batch-rename-directory"))
        assertEquals(null, BatchRenameCatalog.action("view-image"))
    }

    @Test
    fun `every label exists in every locale and survives resource shrinking`() {
        val keep = RepositoryFiles.text("app/src/main/res/raw/keep.xml")
        (LOCALES + "values").forEach { directory ->
            val strings = RepositoryFiles.strings(directory)
            BatchRenameCatalog.actions.forEach { action ->
                assertTrue("$directory lacks ${action.labelResourceName}", strings[action.labelResourceName].orEmpty().isNotBlank())
            }
        }
        BatchRenameCatalog.actions.forEach { action ->
            assertTrue("keep.xml must keep ${action.labelResourceName}", "@string/${action.labelResourceName}" in keep)
            assertEquals(action.labelFallback, RepositoryFiles.strings("values").getValue(action.labelResourceName))
        }
    }

    @Test
    fun `the activity of the catalog is the exported rename activity`() {
        val manifest = RepositoryFiles.text("app/src/main/AndroidManifest.xml")
        val simpleName = BatchRenameCatalog.ACTIVITY_CLASS_NAME.removePrefix("io.github.supermonster003.autojs6.plugin.batch.rename")
        assertTrue(manifest.contains("android:name=\"$simpleName\""))
    }

    @Test
    fun `plurals exist with the same names in every locale`() {
        val reference = plurals("values")
        assertTrue(reference.isNotEmpty())
        LOCALES.forEach { directory -> assertEquals(directory, reference, plurals(directory)) }
    }

    private fun plurals(directory: String): List<String> =
        Regex("<plurals name=\"([^\"]+)\"").findAll(RepositoryFiles.text("app/src/main/res/$directory/plurals.xml")).map { it.groupValues[1] }.toList()

    private companion object {
        val LOCALES = listOf("values-en", "values-ar", "values-es", "values-fr", "values-ja", "values-ko", "values-ru", "values-zh", "values-zh-rHK", "values-zh-rTW")
    }
}
