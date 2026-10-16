package io.github.supermonster003.autojs6.plugin.batch.rename.engine.pinyin

import android.util.Log
import androidx.test.ext.junit.runners.AndroidJUnit4
import io.github.supermonster003.autojs6.plugin.batch.rename.engine.PinyinSeparator
import io.github.supermonster003.autojs6.plugin.batch.rename.engine.PinyinStyle
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import kotlin.system.measureNanoTime

/**
 * The pinyin dictionary loads from the APK's Java resources on the device (roadmap P6.1): the time of a load (the delay
 * of the first pinyin preview) goes to logcat under [TAG]; the assertion only catches gross regressions.
 * zh-CN: 设备上从 APK 的 Java 资源加载拼音词典 (路线图 P6.1): 单次加载耗时 (即首次拼音预览的延迟) 按 [TAG] 写入 logcat; 断言只拦截严重退化.
 */
@RunWith(AndroidJUnit4::class)
class PinyinDictionaryDeviceTest {

    @Test
    fun theDictionaryLoadsFromTheApkResources() {
        val timings = List(5) {
            measureNanoTime { PinyinDictionary.load { name -> PinyinDictionary::class.java.getResourceAsStream("/pinyin/$name") } } / 1_000_000
        }
        Log.i(TAG, "pinyin dictionary loadMs=$timings")
        assertTrue("loading took $timings ms", timings.min() < 5_000)
        assertEquals("chong qing yin hang", PinyinConverter.default.convert("重慶銀行", PinyinStyle(separator = PinyinSeparator.SPACE)))
    }

    private companion object {
        const val TAG = "BatchRenameBenchmark"
    }
}
