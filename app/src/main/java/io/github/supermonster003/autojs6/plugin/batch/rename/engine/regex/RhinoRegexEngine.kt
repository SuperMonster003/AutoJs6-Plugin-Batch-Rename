package io.github.supermonster003.autojs6.plugin.batch.rename.engine.regex

import org.mozilla.javascript.Context
import org.mozilla.javascript.ContextFactory
import org.mozilla.javascript.Function
import org.mozilla.javascript.RhinoException
import org.mozilla.javascript.Scriptable
import org.mozilla.javascript.ScriptableObject
import org.mozilla.javascript.TopLevel

/**
 * [JsRegexEngine] on the host's Rhino build (roadmap D15): the same `NativeRegExp` that runs `RegExp` in AutoJs6
 * scripts, so patterns and replacement strings behave exactly as there.
 *
 * - One sealed scope with the safe standard objects is shared by all threads; each session enters its own [Context]
 *   in interpreted mode (the slim jar has no optimizer).
 * - User text only travels as arguments of three fixed helper functions; it is never spliced into JavaScript source.
 * - Rhino reports regular expression backtracking to the instruction observer, so an evaluation that exceeds
 *   [timeBudgetMillis], or a session that exceeds [sessionBudgetMillis], stops with [JsRegexTimeoutException] instead of
 *   blocking a Binder thread.
 *
 * zh-CN: 基于宿主 Rhino 构建的 [JsRegexEngine] (路线图 D15): 与 AutoJs6 脚本中运行 `RegExp` 的是同一个 `NativeRegExp`,
 * 表达式与替换串的行为完全一致. 所有线程共享一个封存的安全标准对象作用域; 每个会话以解释模式进入自己的 [Context] (slim jar
 * 不含优化器). 用户文本只作为三个固定辅助函数的参数传递, 从不拼接进 JavaScript 源码. Rhino 会把正则回溯计入指令观察器,
 * 因此单次求值超过 [timeBudgetMillis] 或会话超过 [sessionBudgetMillis] 时以 [JsRegexTimeoutException] 结束, 不会阻塞 Binder 线程.
 */
internal class RhinoRegexEngine(
    /** Budget of one compile, test or replace. zh-CN: 单次编译, 测试或替换的预算. */
    private val timeBudgetMillis: Long = DEFAULT_TIME_BUDGET_MILLIS,
    /** Budget of a whole session (a plan or a preview pass). zh-CN: 整个会话 (一个方案或一次预览) 的预算. */
    private val sessionBudgetMillis: Long = DEFAULT_SESSION_BUDGET_MILLIS,
) : JsRegexEngine {

    init {
        require(timeBudgetMillis > 0 && sessionBudgetMillis > 0) { "Time budgets must be positive" }
    }

    private val factory = withOwnClassLoader { BudgetContextFactory() }

    // Rhino initializes Context, and with it the RegExp implementation, through ServiceLoader, which reads
    // META-INF/services from the thread's context class loader. Binder threads of the plugin process do not carry the
    // plugin's loader (RegExp stayed undefined for host calls, roadmap P2.5), so the first entry sets it explicitly.
    // zh-CN: Rhino 经 ServiceLoader 初始化 Context 及其中的 RegExp 实现, ServiceLoader 从线程的上下文类加载器读取
    // META-INF/services. 插件进程的 Binder 线程不带插件的类加载器 (宿主调用时 RegExp 一直未定义, 路线图 P2.5), 因此首次进入时显式设置.
    private val helpers: Helpers by lazy {
        withOwnClassLoader { factory.call { cx -> Helpers.create(cx) } }
    }

    private inline fun <T> withOwnClassLoader(block: () -> T): T {
        val thread = Thread.currentThread()
        val previous = thread.contextClassLoader
        thread.contextClassLoader = RhinoRegexEngine::class.java.classLoader
        try {
            return block()
        } finally {
            thread.contextClassLoader = previous
        }
    }

    override fun <T> session(block: (JsRegexSession) -> T): T {
        val helpers = helpers
        val cx = factory.enterContext() as BudgetContext
        val previousDeadline = cx.deadlineNanos
        try {
            return block(RhinoSession(cx, helpers, timeBudgetMillis, System.nanoTime() + sessionBudgetMillis * NANOS_PER_MILLI))
        } finally {
            cx.deadlineNanos = previousDeadline
            Context.exit()
        }
    }

    private class RhinoSession(
        private val cx: BudgetContext,
        private val helpers: Helpers,
        private val budgetMillis: Long,
        private val sessionDeadlineNanos: Long,
    ) : JsRegexSession {

        override fun compile(source: String, flags: String): JsRegex {
            val handle = try {
                evaluate(helpers.compile, arrayOf(source, flags))
            } catch (error: RhinoException) {
                throw JsRegexSyntaxException(error.details())
            } catch (_: UnsupportedOperationException) {
                // Rhino parses `\p{...}` with java.util.regex named groups, which Android supports from API 26 only.
                // zh-CN: Rhino 用 java.util.regex 的命名分组解析 `\p{...}`, Android 自 API 26 起才支持.
                throw JsRegexSyntaxException(UNICODE_PROPERTIES_UNAVAILABLE)
            }
            return JsRegex(source, flags, this, checkNotNull(handle) { "RegExp construction returned nothing" })
        }

        override fun test(input: String, regex: JsRegex): Boolean =
            evaluate(helpers.test, arrayOf(input, handleOf(regex))) == true

        override fun replace(input: String, regex: JsRegex, replacement: String): String =
            Context.toString(evaluate(helpers.replace, arrayOf(input, handleOf(regex), replacement)))

        private fun handleOf(regex: JsRegex): Any {
            require(regex.owner === this) { "The regular expression belongs to another session" }
            return regex.handle
        }

        private fun evaluate(function: Function, arguments: Array<Any>): Any? {
            val callDeadline = System.nanoTime() + budgetMillis * NANOS_PER_MILLI
            cx.deadlineNanos = if (callDeadline - sessionDeadlineNanos < 0) callDeadline else sessionDeadlineNanos
            return try {
                function.call(cx, helpers.scope, helpers.self, arguments)
            } catch (_: BudgetExceeded) {
                throw JsRegexTimeoutException(budgetMillis)
            } finally {
                cx.deadlineNanos = 0L
            }
        }
    }

    /** The shared sealed scope and the helper functions compiled into it once. zh-CN: 共享的封存作用域及只编译一次的辅助函数. */
    private class Helpers(
        val scope: TopLevel,
        val self: Scriptable,
        val compile: Function,
        val test: Function,
        val replace: Function,
    ) {
        companion object {
            fun create(cx: Context): Helpers {
                val scope = cx.initSafeStandardObjects(null, true)
                val self = cx.evaluateString(scope, HELPER_SOURCE, HELPER_SOURCE_NAME, 1, null) as Scriptable
                fun function(name: String) = ScriptableObject.getProperty(self, name) as Function
                return Helpers(scope, self, function("compile"), function("test"), function("replace"))
            }
        }
    }

    private class BudgetContext(factory: ContextFactory) : Context(factory) {
        /** 0 while no session runs; otherwise the [System.nanoTime] after which evaluation stops. */
        @Volatile
        var deadlineNanos: Long = 0L
    }

    private class BudgetContextFactory : ContextFactory() {

        override fun makeContext(): Context = BudgetContext(this).apply {
            languageVersion = Context.VERSION_ECMASCRIPT
            isInterpretedMode = true
            instructionObserverThreshold = INSTRUCTION_SLICE
        }

        override fun observeInstructionCount(cx: Context, instructionCount: Int) {
            val deadline = (cx as BudgetContext).deadlineNanos
            if (deadline != 0L && System.nanoTime() - deadline > 0) throw BudgetExceeded()
        }
    }

    /**
     * An [Error], so neither Rhino nor JavaScript code can catch it on the way out.
     * zh-CN: 使用 [Error], Rhino 与 JavaScript 代码都不会在传出途中捕获它.
     */
    private class BudgetExceeded : Error() {
        override fun fillInStackTrace(): Throwable = this
    }

    companion object {
        /** Budget of one evaluation: catastrophic backtracking blows up a single match. zh-CN: 单次求值预算: 灾难性回溯发生在单次匹配中. */
        const val DEFAULT_TIME_BUDGET_MILLIS = 1_000L

        /** Budget of a session, far above a 2000-item plan on slow devices. zh-CN: 会话预算, 远高于慢速设备上 2000 项方案的耗时. */
        const val DEFAULT_SESSION_BUDGET_MILLIS = 30_000L

        /** The explanation for `\p{...}` on API 24 / 25. zh-CN: API 24 / 25 上 `\p{...}` 的说明. */
        const val UNICODE_PROPERTIES_UNAVAILABLE = "Unicode property escapes (\\p{...}) need Android 8.0 (API 26) or later"

        /** Instructions between two deadline checks. zh-CN: 两次截止时间检查之间的指令数. */
        private const val INSTRUCTION_SLICE = 10_000

        private const val NANOS_PER_MILLI = 1_000_000L

        private const val HELPER_SOURCE_NAME = "batch-rename-regex.js"

        // `lastIndex = 0` makes sticky and global expressions start at the beginning of every input.
        // zh-CN: `lastIndex = 0` 使粘连与全局表达式对每个输入都从开头开始.
        private val HELPER_SOURCE = """
            ({
                compile: function (source, flags) { return new RegExp(source, flags); },
                test: function (input, re) { re.lastIndex = 0; return re.test(input); },
                replace: function (input, re, replacement) { re.lastIndex = 0; return input.replace(re, replacement); }
            })
        """.trimIndent()
    }
}
