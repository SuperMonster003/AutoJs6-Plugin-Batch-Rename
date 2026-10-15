package io.github.supermonster003.autojs6.plugin.batch.rename.engine.regex

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Assert.fail
import org.junit.Test
import java.util.concurrent.Callable
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

/**
 * Engine behavior beyond the ECMAScript parity table (roadmap P2.1): syntax errors, the time budget against
 * catastrophic backtracking, session ownership, independent threads and the longest names the contract transports.
 * zh-CN: ECMAScript 对照表之外的引擎行为 (路线图 P2.1): 语法错误, 防灾难性回溯的时间预算, 会话归属, 独立线程, 以及契约
 * 允许传输的最长名称.
 */
class RhinoRegexEngineTest {

    @Test
    fun `invalid patterns and flags report the engine's explanation`() {
        val engine = RhinoRegexEngine()
        listOf("(" to "", "a{2,1}" to "", "[z-a]" to "", "(?<n>a)(?<n>b)" to "", "a" to "gg", "a" to "x", "\\p{NoSuchProperty}" to "u").forEach { (source, flags) ->
            try {
                engine.session { it.compile(source, flags) }
                fail("/$source/$flags must not compile")
            } catch (error: JsRegexSyntaxException) {
                assertTrue("/$source/$flags: the message explains the error", !error.message.isNullOrBlank())
            }
        }
    }

    @Test
    fun `catastrophic backtracking stops at the session budget`() {
        val engine = RhinoRegexEngine(timeBudgetMillis = 200)
        val started = System.nanoTime()
        try {
            engine.session { session -> session.test("a".repeat(40) + "!", session.compile("^(a+)+$", "")) }
            fail("the exponential pattern must not finish")
        } catch (error: JsRegexTimeoutException) {
            assertEquals(200L, error.budgetMillis)
        }
        val elapsedMillis = (System.nanoTime() - started) / 1_000_000
        assertTrue("stopped soon after the budget, took $elapsedMillis ms", elapsedMillis < 5_000)
        // The engine stays usable, and a new session gets a fresh budget.
        assertTrue(engine.session { session -> session.test("aaa", session.compile("^(a+)+$", "")) })
    }

    @Test
    fun `sticky and global expressions restart at index 0 for every input`() {
        RhinoRegexEngine().session { session ->
            val sticky = session.compile("a", "y")
            assertEquals(listOf("Xab", "Xab", "Xab"), List(3) { session.replace("aab", sticky, "X") })
            val global = session.compile("a", "g")
            assertEquals(listOf(true, true, true), List(3) { session.test("a", global) })
        }
    }

    @Test
    fun `an expression only works in its own session`() {
        val engine = RhinoRegexEngine()
        val foreign = engine.session { it.compile("a", "") }
        engine.session { session ->
            assertTrue(runCatching { session.replace("a", foreign, "b") }.exceptionOrNull() is IllegalArgumentException)
        }
    }

    @Test
    fun `nested sessions keep their own deadline`() {
        val engine = RhinoRegexEngine()
        val result = engine.session { outer ->
            val inner = engine.session { inner -> inner.replace("abc", inner.compile("b", ""), "x") }
            inner + outer.replace("abc", outer.compile("c", ""), "y")
        }
        assertEquals("axcaby", result)
    }

    @Test
    fun `threads evaluate independently on the shared sealed scope`() {
        val engine = RhinoRegexEngine()
        val pool = Executors.newFixedThreadPool(8)
        try {
            val tasks = (0 until 64).map { index ->
                Callable {
                    engine.session { session ->
                        val regex = session.compile("(?<n>\\d+)", "g")
                        (0 until 200).all { round -> session.replace("IMG_$index-$round", regex, "<$<n>>") == "IMG_<$index>-<$round>" }
                    }
                }
            }
            assertTrue(pool.invokeAll(tasks).all { it.get(30, TimeUnit.SECONDS) })
        } finally {
            pool.shutdownNow()
        }
    }

    @Test
    fun `the longest transported names are handled`() {
        val name = "照".repeat(1024)
        RhinoRegexEngine().session { session ->
            assertEquals("x".repeat(1024), session.replace(name, session.compile("\\p{Script=Han}", "gu"), "x"))
            assertFalse(session.test(name, session.compile("^\\d", "")))
        }
    }
}
