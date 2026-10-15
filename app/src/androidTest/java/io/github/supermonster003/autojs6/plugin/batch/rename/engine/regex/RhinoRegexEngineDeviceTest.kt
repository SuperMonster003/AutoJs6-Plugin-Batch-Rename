package io.github.supermonster003.autojs6.plugin.batch.rename.engine.regex

import android.os.Build
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Assert.fail
import org.junit.Test
import org.junit.runner.RunWith

/**
 * The slim Rhino jar on Android (roadmap P2.1): the interpreter, the sealed scope and `NativeRegExp` load and evaluate
 * the ECMAScript features the JVM parity table covers, down to API 24 (with core library desugaring for the `java.time`
 * that Rhino's `NativeDate` needs). Unicode property escapes need API 26: Rhino parses them with `java.util.regex` named
 * groups, so API 24 / 25 report a syntax error instead of crashing.
 * zh-CN: slim Rhino jar 在 Android 上的表现 (路线图 P2.1): 解释器, 封存作用域与 `NativeRegExp` 能在 API 24 及以上加载并求值
 * JVM 对照表覆盖的 ECMAScript 特性 (Rhino 的 `NativeDate` 所需的 `java.time` 依靠 core library desugaring). Unicode 属性
 * 转义需要 API 26: Rhino 用 `java.util.regex` 命名分组解析它, 因此 API 24 / 25 报告语法错误而不是崩溃.
 */
@RunWith(AndroidJUnit4::class)
class RhinoRegexEngineDeviceTest {

    @Test
    fun ecmaScriptFeaturesEvaluateOnTheDevice() {
        RhinoRegexEngine().session { session ->
            fun replace(source: String, flags: String, input: String, replacement: String) =
                session.replace(input, session.compile(source, flags), replacement)
            assertEquals("2020-03-29", replace("(\\d\\d)-(\\d\\d)-(\\d\\d\\d\\d)", "", "29-03-2020", "\$3-\$2-\$1"))
            assertEquals("10.2026", replace("(?<year>\\d{4})-(?<month>\\d{2})", "", "2026-10", "\$<month>.\$<year>"))
            assertEquals("cost \$N and \$N", replace("(?<=\\$)\\d+", "g", "cost \$30 and \$45", "N"))
            assertEquals("x", replace("\u017f", "iu", "S", "x"))
            assertEquals("X", replace("a.b", "s", "a\nb", "X"))
            assertEquals("a[b]c", replace("b", "", "abc", "[\$&]"))
            assertTrue(session.test("IMG_0042", session.compile("^IMG_\\d{4}$", "")))
        }
    }

    @Test
    fun unicodePropertyEscapesNeedApi26() {
        RhinoRegexEngine().session { session ->
            val input = "abc 123 \u7167\u7247"
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                assertEquals("L 123 L", session.replace(input, session.compile("\\p{L}+", "gu"), "L"))
            } else {
                try {
                    session.compile("\\p{L}+", "gu")
                    fail("API ${Build.VERSION.SDK_INT} cannot parse Unicode property escapes")
                } catch (error: JsRegexSyntaxException) {
                    assertEquals(RhinoRegexEngine.UNICODE_PROPERTIES_UNAVAILABLE, error.message)
                }
            }
        }
    }

    @Test
    fun catastrophicBacktrackingStopsOnTheDevice() {
        try {
            RhinoRegexEngine(timeBudgetMillis = 300).session { session -> session.test("a".repeat(40) + "!", session.compile("^(a+)+$", "")) }
            fail("the exponential pattern must not finish")
        } catch (_: JsRegexTimeoutException) {
        }
    }
}
