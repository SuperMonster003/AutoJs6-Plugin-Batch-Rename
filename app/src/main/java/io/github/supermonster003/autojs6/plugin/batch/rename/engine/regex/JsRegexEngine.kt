package io.github.supermonster003.autojs6.plugin.batch.rename.engine.regex

/**
 * ECMAScript regular expressions for the rename engine (roadmap D6 / D15): compiling, testing and
 * `String.prototype.replace` with the semantics scripts see in `RegExp`.
 *
 * Work happens in sessions. A session belongs to the thread that opened it, shares one time budget among everything it
 * evaluates (a plan or one preview pass) and owns the regular expressions it compiled.
 *
 * zh-CN: 改名引擎使用的 ECMAScript 正则表达式 (路线图 D6 / D15): 编译, 测试以及与脚本中 `RegExp` 语义一致的
 * `String.prototype.replace`. 工作在会话中进行: 会话属于打开它的线程, 其中所有求值 (一个方案或一次预览) 共享一个时间预算,
 * 会话编译的正则表达式只在该会话内有效.
 */
internal interface JsRegexEngine {

    /** Runs [block] in a new session on the current thread. zh-CN: 在当前线程的新会话中执行 [block]. */
    fun <T> session(block: (JsRegexSession) -> T): T
}

internal interface JsRegexSession {

    /**
     * Compiles [source] with [flags] like `new RegExp(source, flags)`.
     * zh-CN: 像 `new RegExp(source, flags)` 一样编译 [source].
     *
     * @throws JsRegexSyntaxException when ECMAScript rejects the pattern or the flags.
     */
    fun compile(source: String, flags: String): JsRegex

    /**
     * Whether [regex] matches [input]; every call starts at index 0, also for sticky and global expressions.
     * zh-CN: [regex] 是否匹配 [input]; 每次调用都从下标 0 开始, 粘连与全局表达式亦然.
     *
     * @throws JsRegexTimeoutException when the session's time budget runs out.
     */
    fun test(input: String, regex: JsRegex): Boolean

    /**
     * `input.replace(regex, replacement)`: [replacement] may use `$1`, `$<name>`, `$&`, `` $` ``, `$'` and `$$`; every
     * call starts at index 0.
     * zh-CN: `input.replace(regex, replacement)`: [replacement] 可使用 `$1`, `$<name>`, `$&`, `` $` ``, `$'` 与 `$$`;
     * 每次调用都从下标 0 开始.
     *
     * @throws JsRegexTimeoutException when the session's time budget runs out.
     */
    fun replace(input: String, regex: JsRegex, replacement: String): String
}

/**
 * A compiled regular expression, valid only inside the session that compiled it.
 * zh-CN: 已编译的正则表达式, 只在编译它的会话内有效.
 */
internal class JsRegex internal constructor(
    val source: String,
    val flags: String,
    internal val owner: JsRegexSession,
    internal val handle: Any,
)

/**
 * ECMAScript rejected a pattern or its flags; the message is the engine's explanation.
 * zh-CN: ECMAScript 拒绝了表达式或其标志; 消息为引擎给出的说明.
 */
internal class JsRegexSyntaxException(message: String) : IllegalArgumentException(message)

/**
 * Matching ran out of the session's time budget, typically through catastrophic backtracking.
 * zh-CN: 匹配耗尽了会话的时间预算, 通常由灾难性回溯引起.
 */
internal class JsRegexTimeoutException(val budgetMillis: Long) :
    RuntimeException("The regular expression did not finish within $budgetMillis ms")
