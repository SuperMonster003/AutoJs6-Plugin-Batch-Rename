package io.github.supermonster003.autojs6.plugin.batch.rename.ui

import android.app.Application
import android.content.Context
import android.net.Uri
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import io.github.supermonster003.autojs6.plugin.batch.rename.engine.RenameOptions
import io.github.supermonster003.autojs6.plugin.batch.rename.presets.PresetStore
import io.github.supermonster003.autojs6.plugin.batch.rename.settings.SharedPreferencesStore
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.File

/**
 * Exporting presets to a file and importing them back through the content resolver, the way the system file picker
 * hands the screen a Uri (roadmap P6.2); only the picker itself is left to manual acceptance.
 * zh-CN: 经内容解析器把预设导出到文件再导入回来, 与系统文件选择器交给界面 Uri 的方式相同 (路线图 P6.2); 只剩选择器本身需人工验收.
 */
@RunWith(AndroidJUnit4::class)
class PresetFileRoundTripTest {

    private val instrumentation = InstrumentationRegistry.getInstrumentation()
    private val context: Context get() = instrumentation.targetContext
    private val file by lazy { File(context.cacheDir, "presets-${System.nanoTime()}.json") }
    private val store by lazy { PresetStore(SharedPreferencesStore(context)) }

    @Before
    fun clearPreferences() {
        context.getSharedPreferences(SharedPreferencesStore.NAME, Context.MODE_PRIVATE).edit().clear().commit()
    }

    @After
    fun deleteFile() {
        file.delete()
    }

    @Test
    fun presetsGoToAFileAndComeBack() {
        lateinit var model: BatchRenameViewModel
        instrumentation.runOnMainSync { model = BatchRenameViewModel(context.applicationContext as Application) }
        store.save("file preset", RenameOptions(search = "a", replace = "b"))

        instrumentation.runOnMainSync { model.writePresets(Uri.fromFile(file), listOf("file preset")) }
        assertEquals(ScreenMessage.PresetsExported, awaitMessage(model))
        assertTrue(file.readText().contains("\"file preset\""))

        store.delete(listOf("file preset"))
        instrumentation.runOnMainSync {
            model.messageShown()
            model.readPresets(Uri.fromFile(file))
        }
        assertEquals(ScreenMessage.PresetsImported(1, 0), awaitMessage(model))
        assertEquals(listOf("file preset" to "b"), store.list().map { it.name to it.options.replace })

        file.writeText("not presets")
        instrumentation.runOnMainSync {
            model.messageShown()
            model.readPresets(Uri.fromFile(file))
        }
        assertEquals(ScreenMessage.PresetFailed(PresetFailure.NOT_PRESETS), awaitMessage(model))
    }

    private fun awaitMessage(model: BatchRenameViewModel): ScreenMessage {
        val deadline = System.currentTimeMillis() + 10_000
        while (System.currentTimeMillis() < deadline) {
            model.state.value.message?.let { return it }
            Thread.sleep(50)
        }
        throw AssertionError("no message within 10 s")
    }
}
