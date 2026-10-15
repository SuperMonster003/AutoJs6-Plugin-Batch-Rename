package io.github.supermonster003.autojs6.plugin.batch.rename.engine.regex

import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Assert.fail
import org.junit.Test
import org.junit.runner.RunWith

/**
 * The slim Rhino jar on Android (roadmap P2.1): the interpreter, the sealed scope and `NativeRegExp` load and evaluate
 * the ECMAScript features the JVM parity table covers, down to API 24 (with core library desugaring for the `java.time`
 * that Rhino's `NativeDate` needs). Unicode property escapes work on API 24 / 25 as well since the Rhino jar of host
 * commit 259467203, which no longer parses them with `java.util.regex` named groups.
 * zh-CN: slim Rhino jar 在 Android 上的表现 (路线图 P2.1): 解释器, 封存作用域与 `NativeRegExp` 能在 API 24 及以上加载并求值
 * JVM 对照表覆盖的 ECMAScript 特性 (Rhino 的 `NativeDate` 所需的 `java.time` 依靠 core library desugaring). 自宿主提交
 * 259467203 的 Rhino jar 起不再用 `java.util.regex` 命名分组解析 Unicode 属性转义, API 24 / 25 上同样可用.
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
    fun unicodePropertyEscapesWorkOnEveryApiLevel() {
        RhinoRegexEngine().session { session ->
            val input = "abc 123 \u7167\u7247"
            assertEquals("L 123 L", session.replace(input, session.compile("\\p{L}+", "gu"), "L"))
            assertTrue(session.test("a" + input, session.compile("\\p{Script=Han}", "u")))
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
