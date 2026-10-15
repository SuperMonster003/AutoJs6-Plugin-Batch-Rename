package io.github.supermonster003.autojs6.plugin.batch.rename.ui

import android.content.Context
import android.content.res.Configuration
import android.net.Uri
import android.os.Bundle
import org.autojs.plugin.common.api.AutoJs6HostSettingsContract
import java.util.Locale
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

/**
 * The host's appearance (roadmap D24): its theme colors, whether it is dark right now and its language, read through
 * the versioned host settings contract. Null fields mean "follow the system".
 * zh-CN: 宿主的外观 (路线图 D24): 主题色, 当前是否为深色以及界面语言, 经带版本的宿主设置契约读取. 字段为 null 表示跟随系统.
 */
internal data class HostAppearance(
    val primary: Int?,
    val accent: Int?,
    val dark: Boolean?,
    val languageTag: String?,
) {
    /** [base] with the host's language and night mode applied. zh-CN: 应用了宿主语言与夜间模式的 [base]. */
    fun wrap(base: Context): Context {
        if (languageTag == null && dark == null) return base
        val configuration = Configuration(base.resources.configuration)
        languageTag?.let { tag ->
            val locale = Locale.forLanguageTag(tag)
            configuration.setLocale(locale)
            configuration.setLayoutDirection(locale)
        }
        dark?.let { night ->
            val mode = if (night) Configuration.UI_MODE_NIGHT_YES else Configuration.UI_MODE_NIGHT_NO
            configuration.uiMode = (configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK.inv()) or mode
        }
        return base.createConfigurationContext(configuration)
    }

    companion object {
        val SYSTEM = HostAppearance(null, null, null, null)

        private val executor = Executors.newSingleThreadExecutor { runnable -> Thread(runnable, "BatchRenameHostAppearance").apply { isDaemon = true } }

        @Volatile
        private var cached: HostAppearance? = null

        /**
         * The appearance for a new screen: asks the host (in the host process, which just launched the plugin) and
         * waits at most [TIMEOUT_MILLIS]; the last answer serves when the host is slow, the system appearance when it
         * never answered.
         * zh-CN: 新界面的外观: 询问宿主 (宿主进程刚启动了插件) 并至多等待 [TIMEOUT_MILLIS]; 宿主较慢时使用上次的应答, 从未应答时跟随系统.
         */
        fun load(context: Context): HostAppearance {
            val application = context.applicationContext ?: context
            val future = executor.submit<HostAppearance?> { query(application) }
            val fresh = runCatching { future.get(TIMEOUT_MILLIS, TimeUnit.MILLISECONDS) }.getOrNull()
            fresh?.let { cached = it }
            return fresh ?: cached ?: SYSTEM
        }

        private fun query(context: Context): HostAppearance? = runCatching {
            val answer: Bundle = context.contentResolver.call(
                Uri.parse(AutoJs6HostSettingsContract.CONTENT_URI),
                AutoJs6HostSettingsContract.METHOD_GET_SETTINGS,
                null,
                null,
            ) ?: return null
            parse(answer)
        }.getOrNull()

        /** The appearance of a contract answer; null for an answer of another protocol version. */
        fun parse(answer: Bundle): HostAppearance? {
            if (answer.getInt(AutoJs6HostSettingsContract.KEY_PROTOCOL_VERSION, -1) != AutoJs6HostSettingsContract.PROTOCOL_VERSION) return null
            fun color(key: String): Int? = if (answer.containsKey(key)) answer.getInt(key) else null
            return HostAppearance(
                primary = color(AutoJs6HostSettingsContract.KEY_THEME_COLOR_PRIMARY),
                accent = color(AutoJs6HostSettingsContract.KEY_THEME_COLOR_ACCENT),
                dark = if (answer.containsKey(AutoJs6HostSettingsContract.KEY_DARK_MODE_ACTIVE)) {
                    answer.getBoolean(AutoJs6HostSettingsContract.KEY_DARK_MODE_ACTIVE)
                } else {
                    null
                },
                languageTag = answer.getString(AutoJs6HostSettingsContract.KEY_RESOLVED_LANGUAGE_TAG)?.takeIf { it.isNotBlank() },
            )
        }

        private const val TIMEOUT_MILLIS = 400L
    }
}
