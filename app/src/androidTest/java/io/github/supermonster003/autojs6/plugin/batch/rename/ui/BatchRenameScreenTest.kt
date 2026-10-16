package io.github.supermonster003.autojs6.plugin.batch.rename.ui

import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.v2.createEmptyComposeRule
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.test.captureToImage
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollToNode
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.test.performTextReplacement
import androidx.lifecycle.Lifecycle
import androidx.test.core.app.ActivityScenario
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import io.github.supermonster003.autojs6.plugin.batch.rename.BatchRenameActivity
import io.github.supermonster003.autojs6.plugin.batch.rename.BatchRenamePlugin
import io.github.supermonster003.autojs6.plugin.batch.rename.presets.PresetStore
import io.github.supermonster003.autojs6.plugin.batch.rename.settings.SharedPreferencesStore
import org.autojs.plugin.explorer.api.ExplorerActionHostSessionKeys
import org.autojs.plugin.explorer.api.ExplorerActionIntentExtras
import org.autojs.plugin.explorer.api.ExplorerActionPluginActions
import org.autojs.plugin.explorer.api.ExplorerActionTargetKeys
import org.autojs.plugin.explorer.api.ExplorerActionValues
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.util.UUID

/**
 * The rename screen against an in-memory host session (roadmap P4.2): typing updates the preview, unchecking excludes a
 * row, the filter hides unchanged rows, conflicts disable Rename, applied texts come back from the history menu, and
 * undo reaches the host. Assertions use test tags and the session, never translated text.
 * zh-CN: 使用内存宿主会话测试重命名界面 (路线图 P4.2). 断言只用测试标签与会话, 从不依赖翻译文本.
 */
@RunWith(AndroidJUnit4::class)
class BatchRenameScreenTest {

    @get:Rule
    val compose = createEmptyComposeRule()

    private val context: Context get() = InstrumentationRegistry.getInstrumentation().targetContext

    @Before
    fun clearPreferences() {
        context.getSharedPreferences("batch-rename", Context.MODE_PRIVATE).edit().clear().commit()
    }

    @Test
    fun typingPlansExcludingAndApplyingReachTheHost() {
        val session = FakeHostSession(parent = listOf("album/"), tree = mapOf("" to listOf("IMG_1.jpg", "IMG_2.jpg", "notes.txt")))
        ActivityScenario.launch<BatchRenameActivity>(request("batch-rename-directory", session, target("album", directory = true))).use {
            waitForTag(row("IMG_1.jpg"))
            compose.onNodeWithTag(TAG_APPLY).assertIsNotEnabled()
            inList(TAG_SEARCH).performTextInput("IMG_")
            inList(TAG_REPLACE).performTextInput("Photo_")
            compose.waitUntil(TIMEOUT) { applyEnabled() }
            screenshot("preview")

            // Only renamed rows: notes.txt disappears.
            inList(TAG_ONLY_RENAMED).performClick()
            compose.waitUntil(TIMEOUT) { !exists(row("notes.txt")) }
            inList(TAG_ONLY_RENAMED).performClick()
            waitForTag(row("notes.txt"))

            // Unchecking IMG_2.jpg leaves one rename.
            inList(row("IMG_2.jpg")).performClick()
            compose.waitForIdle()
            Thread.sleep(400)
            compose.onNodeWithTag(TAG_APPLY).performClick()
            compose.waitUntil(TIMEOUT) { session.renameRequests.isNotEmpty() }
            assertEquals(listOf(Triple("album", "IMG_1.jpg", "Photo_1.jpg")), session.renameRequests.single())
            assertTrue(session.attached)

            // Undo reaches the host.
            compose.waitUntil(TIMEOUT) { enabled(TAG_UNDO) }
            compose.onNodeWithTag(TAG_UNDO).performClick()
            compose.waitUntil(TIMEOUT) { session.undone.isNotEmpty() }
            assertEquals(listOf("b1"), session.undone)

            // The applied search comes back from the history menu.
            inList(TAG_SEARCH).performTextReplacement("")
            waitForTag(TAG_SEARCH_HISTORY)
            inList(TAG_SEARCH_HISTORY).performClick()
            compose.waitUntil(TIMEOUT) { compose.onAllNodes(hasText("IMG_")).fetchSemanticsNodes().isNotEmpty() }
        }
        compose.waitUntil(TIMEOUT) { session.closed }
    }

    @Test
    fun conflictsDisableRename() {
        val session = FakeHostSession(parent = listOf("a.txt", "b.txt"))
        val targets = arrayOf(target("a.txt"), target("b.txt"))
        ActivityScenario.launch<BatchRenameActivity>(request("batch-rename-selection", session, *targets)).use {
            waitForTag(row("a.txt", targetId = "a.txt"))
            inList(TAG_SEARCH).performTextInput("a")
            inList(TAG_REPLACE).performTextInput("b")
            compose.waitForIdle()
            Thread.sleep(400)
            compose.waitForIdle()
            compose.onNodeWithTag(TAG_APPLY).assertIsNotEnabled()
            inList(TAG_REPLACE).performTextReplacement("c")
            compose.waitUntil(TIMEOUT) { applyEnabled() }
            compose.onNodeWithTag(TAG_APPLY).assertIsEnabled()
        }
    }

    /** The "Chinese characters to pinyin" cleanup with an empty search (roadmap P6.1). zh-CN: 查找为空时的 "汉字转拼音" 整理. */
    @Test
    fun pinyinCleanupRenamesChineseNames() {
        val session = FakeHostSession(parent = listOf("我的照片.jpg", "notes.txt"))
        val targets = arrayOf(target("我的照片.jpg"), target("notes.txt"))
        ActivityScenario.launch<BatchRenameActivity>(request("batch-rename-selection", session, *targets)).use {
            waitForTag(row("我的照片.jpg", targetId = "我的照片.jpg"))
            inList(TAG_CLEANUP).performClick()
            inList(TAG_PINYIN).performClick()
            compose.waitUntil(TIMEOUT) { applyEnabled() }
            screenshot("pinyin")
            compose.onNodeWithTag(TAG_APPLY).performClick()
            compose.waitUntil(TIMEOUT) { session.renameRequests.isNotEmpty() }
            assertEquals(listOf("wodezhaopian.jpg"), session.renameRequests.single().map { it.third })
        }
    }

    /**
     * Presets (roadmap P6.2): save the current options, load them back, export to the clipboard, delete, import from
     * the clipboard and import again keeping both.
     * zh-CN: 预设 (路线图 P6.2): 保存当前选项, 加载回来, 导出到剪贴板, 删除, 从剪贴板导入, 再次导入并保留两者.
     */
    @Test
    fun presetsAreSavedLoadedAndRoundTripThroughTheClipboard() {
        val store = PresetStore(SharedPreferencesStore(context))
        val session = FakeHostSession(parent = listOf("IMG_1.jpg", "notes.txt"))
        val targets = arrayOf(target("IMG_1.jpg"), target("notes.txt"))
        ActivityScenario.launch<BatchRenameActivity>(request("batch-rename-selection", session, *targets)).use {
            waitForTag(row("IMG_1.jpg", targetId = "IMG_1.jpg"))
            inList(TAG_SEARCH).performTextInput("IMG_")
            inList(TAG_REPLACE).performTextInput("Pic_")
            compose.waitUntil(TIMEOUT) { applyEnabled() }

            compose.onNodeWithTag(TAG_PRESETS).performClick()
            compose.onNodeWithTag(TAG_SAVE_PRESET).performClick()
            compose.onNodeWithTag(TAG_PRESET_NAME).performTextInput("photos")
            compose.onNodeWithTag(TAG_DIALOG_CONFIRM).performClick()
            compose.waitUntil(TIMEOUT) { store.find("photos") != null }
            assertEquals("Pic_", store.find("photos")!!.options.replace)

            // Loading the preset brings back the replacement. zh-CN: 加载预设可恢复替换文本.
            inList(TAG_REPLACE).performTextReplacement("Other_")
            compose.onNodeWithTag(TAG_PRESETS).performClick()
            screenshot("presets-menu")
            compose.onNodeWithTag(TAG_PRESET_PREFIX + "photos").performClick()
            compose.waitUntil(TIMEOUT) { compose.onAllNodes(hasText("Pic_")).fetchSemanticsNodes().isNotEmpty() }

            compose.onNodeWithTag(TAG_PRESETS).performClick()
            compose.onNodeWithTag(TAG_MANAGE_PRESETS).performClick()
            compose.waitForIdle()
            Thread.sleep(500)
            screenshot("presets-manage")
            compose.onNodeWithTag(TAG_EXPORT_CLIPBOARD).performClick()
            compose.waitForIdle()
            assertTrue(clipboardText().orEmpty().contains("\"photos\""))

            compose.onNodeWithTag(TAG_PRESETS).performClick()
            compose.onNodeWithTag(TAG_MANAGE_PRESETS).performClick()
            compose.onNodeWithTag(TAG_PRESET_DELETE_PREFIX + "photos").performClick()
            compose.onNodeWithTag(TAG_DIALOG_CONFIRM).performClick()
            compose.waitUntil(TIMEOUT) { store.list().isEmpty() }
            compose.onNodeWithTag(TAG_IMPORT_CLIPBOARD).performClick()
            compose.waitUntil(TIMEOUT) { store.find("photos") != null }

            compose.onNodeWithTag(TAG_PRESETS).performClick()
            compose.onNodeWithTag(TAG_MANAGE_PRESETS).performClick()
            compose.onNodeWithTag(TAG_IMPORT_CLIPBOARD).performClick()
            compose.waitUntil(TIMEOUT) { exists(TAG_CONFLICT_KEEP_BOTH) }
            screenshot("presets-conflict")
            compose.onNodeWithTag(TAG_CONFLICT_KEEP_BOTH).performClick()
            compose.waitUntil(TIMEOUT) { store.list().size == 2 }
            assertEquals(listOf("photos", "photos (1)"), store.list().map { it.name })
        }
    }

    private fun clipboardText(): String? {
        var text: String? = null
        InstrumentationRegistry.getInstrumentation().runOnMainSync {
            val clipboard = context.getSystemService(ClipboardManager::class.java)
            text = clipboard.primaryClip?.takeIf { it.itemCount > 0 }?.getItemAt(0)?.coerceToText(context)?.toString()
        }
        return text
    }

    @Test
    fun anOlderProtocolEndsTheScreenAndClosesTheSession() {
        val session = FakeHostSession(parent = listOf("a.txt"))
        val intent = request("batch-rename-single", session, target("a.txt")).putExtra(ExplorerActionIntentExtras.PROTOCOL_VERSION, 24)
        ActivityScenario.launch<BatchRenameActivity>(intent).use { scenario ->
            compose.waitUntil(TIMEOUT) { scenario.state == Lifecycle.State.DESTROYED }
        }
        assertTrue(session.closed)
    }

    /**
     * Saves the screen as `files/screenshots/<name>.png` of the app for review (roadmap R2); pull it with
     * `adb exec-out run-as <package> cat files/screenshots/<name>.png` before the test package is uninstalled.
     * Compose captures through `PixelCopy.request(Window, ...)`, which API 24 / 25 lack, so older systems skip it.
     */
    private fun screenshot(name: String) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return
        compose.waitForIdle()
        val bitmap = compose.onRoot().captureToImage().asAndroidBitmap()
        val directory = java.io.File(context.filesDir, "screenshots").apply { mkdirs() }
        java.io.File(directory, "$name.png").outputStream().use { bitmap.compress(android.graphics.Bitmap.CompressFormat.PNG, 100, it) }
    }

    private fun applyEnabled(): Boolean = enabled(TAG_APPLY)

    private fun enabled(tag: String): Boolean = nodes(tag).any { node ->
        !node.config.contains(androidx.compose.ui.semantics.SemanticsProperties.Disabled)
    }

    private fun exists(tag: String) = nodes(tag).isNotEmpty()

    /**
     * Nodes with [tag]; none while the activity has not composed yet (the first launch after installing takes longer),
     * so the waits keep waiting instead of failing.
     * zh-CN: 带 [tag] 的节点; 活动尚未完成组合时 (安装后首次启动较慢) 视为没有, 使等待继续而不是直接失败.
     */
    private fun nodes(tag: String) = try {
        compose.onAllNodes(hasTestTag(tag)).fetchSemanticsNodes()
    } catch (_: IllegalStateException) {
        emptyList()
    }

    private fun waitForTag(tag: String) {
        compose.waitUntil(TIMEOUT) {
            if (!exists(tag)) runCatching { scrollTo(tag) }
            exists(tag)
        }
    }

    private fun scrollTo(tag: String) {
        compose.onNodeWithTag(TAG_LIST).performScrollToNode(hasTestTag(tag))
    }

    /** A node of the list, scrolled into view first: the lazy list composes only what is visible. */
    private fun inList(tag: String): androidx.compose.ui.test.SemanticsNodeInteraction {
        scrollTo(tag)
        return compose.onNodeWithTag(tag)
    }

    private fun row(relativePath: String, targetId: String = "album") =
        TAG_ROW_PREFIX + if (targetId == relativePath) "$targetId/" else "$targetId/$relativePath"

    private fun target(name: String, directory: Boolean = false) = Bundle().apply {
        putString(ExplorerActionTargetKeys.ID, name)
        putString(ExplorerActionTargetKeys.DISPLAY_NAME, name)
        putInt(ExplorerActionTargetKeys.KIND, if (directory) ExplorerActionValues.TARGET_DIRECTORY else ExplorerActionValues.TARGET_FILE)
        putString(ExplorerActionTargetKeys.MIME_TYPE, if (directory) "inode/directory" else "text/plain")
        putLong(ExplorerActionTargetKeys.SIZE, if (directory) -1L else 1L)
        putLong(ExplorerActionTargetKeys.LAST_MODIFIED, 1_700_000_000_000L)
    }

    private fun request(actionId: String, session: FakeHostSession, vararg targets: Bundle) =
        Intent(ExplorerActionPluginActions.EXECUTE).apply {
            setClassName(context, BatchRenameActivity::class.java.name)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            putExtra(ExplorerActionIntentExtras.ACTION_ID, actionId)
            putExtra(ExplorerActionIntentExtras.PROTOCOL_VERSION, BatchRenamePlugin.EXPLORER_PROTOCOL_VERSION)
            putExtra(ExplorerActionIntentExtras.REQUEST_ID, UUID.randomUUID().toString())
            putExtra(ExplorerActionIntentExtras.PARENT_DISPLAY_PATH, "/storage/emulated/0/Pictures")
            putExtra(ExplorerActionIntentExtras.HOST_VERSION_CODE, BatchRenamePlugin.REQUIRED_HOST_VERSION)
            putParcelableArrayListExtra(ExplorerActionIntentExtras.TARGETS, arrayListOf(*targets))
            putExtra(ExplorerActionIntentExtras.HOST_SESSION, Bundle().apply { putBinder(ExplorerActionHostSessionKeys.BINDER, session.asBinder()) })
        }

    private companion object {
        const val TIMEOUT = 15_000L
    }
}
