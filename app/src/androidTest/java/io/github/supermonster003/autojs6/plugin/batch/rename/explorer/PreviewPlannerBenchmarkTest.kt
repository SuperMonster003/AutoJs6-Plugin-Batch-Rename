package io.github.supermonster003.autojs6.plugin.batch.rename.explorer

import android.os.Debug
import android.os.SystemClock
import android.util.Log
import androidx.test.ext.junit.runners.AndroidJUnit4
import io.github.supermonster003.autojs6.plugin.batch.rename.engine.RenameEngine
import io.github.supermonster003.autojs6.plugin.batch.rename.engine.RenameOptions
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Measures how long the screen takes to recompute its preview (roadmap P5.3: 500 items under 100 ms; 2000 items with
 * the memory it takes on the low-end G8441). The timings go to logcat under [TAG]; the assertions only catch gross
 * regressions so that slow emulators do not fail. Options use a regular expression with a capture, a counter and a
 * date variable, the heaviest common combination.
 * zh-CN: 测量界面重新计算预览的耗时 (路线图 P5.3: 500 项低于 100 ms; 2000 项及其在低端 G8441 上的内存). 结果按 [TAG] 写入 logcat;
 * 断言只拦截明显的退化, 以免慢模拟器失败. 选项用含捕获组的正则, 计数器与日期变量, 即常用组合中最重的一种.
 */
@RunWith(AndroidJUnit4::class)
class PreviewPlannerBenchmarkTest {

    private val options = RenameOptions(
        search = "^IMG_(\\d+)",
        replace = "\$YYYY-\$MM-\$DD_\${padding=4;start=1}_\$1",
        regex = true,
        enumerate = true,
    )

    @Test
    fun recomputesTheScreenPreview() {
        val planner = PreviewPlanner(RenameEngine(), now = NOW)
        listOf(500, 2000).forEach { size ->
            val set = candidates(size)
            repeat(WARM_UP_RUNS) { planner.plan(set, options, emptySet()) }
            Runtime.getRuntime().gc()
            val heapBefore = usedHeapKb()
            val timings = LongArray(MEASURED_RUNS)
            var renamed = 0
            for (run in 0 until MEASURED_RUNS) {
                val start = SystemClock.elapsedRealtimeNanos()
                val preview = planner.plan(set, options, emptySet())
                timings[run] = (SystemClock.elapsedRealtimeNanos() - start) / 1_000_000
                renamed = preview.renameCount
            }
            timings.sort()
            val median = timings[MEASURED_RUNS / 2]
            Log.i(TAG, "items=$size renamed=$renamed medianMs=$median minMs=${timings.first()} maxMs=${timings.last()} " +
                "heapDeltaKb=${usedHeapKb() - heapBefore} pssKb=${Debug.getPss()}")
            assertEquals(size, renamed)
            assertTrue("median $median ms for $size items", median < GROSS_LIMIT_MS)
        }
    }

    private fun candidates(size: Int): CandidateSet {
        val names = (1..size).map { "IMG_%05d.jpg".format(it) }
        return CandidateSet(
            candidates = names.mapIndexed { index, name ->
                RenameCandidate(
                    targetId = "target",
                    relativePath = name,
                    name = name,
                    directory = false,
                    depth = 1,
                    parentId = "p0",
                    lastModified = NOW - index * 60_000L,
                    size = 1024L,
                )
            },
            parents = listOf(CandidateParent("p0", names, caseInsensitive = false, restrictedCharacters = false)),
            truncated = false,
        )
    }

    private fun usedHeapKb(): Long = Runtime.getRuntime().let { (it.totalMemory() - it.freeMemory()) / 1024 }

    private companion object {
        const val TAG = "BatchRenameBenchmark"
        const val NOW = 1_760_000_000_000L
        const val WARM_UP_RUNS = 3
        const val MEASURED_RUNS = 7
        const val GROSS_LIMIT_MS = 5_000L
    }
}
