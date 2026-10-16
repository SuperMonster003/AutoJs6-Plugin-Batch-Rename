package io.github.supermonster003.autojs6.plugin.batch.rename.settings

import android.content.Context

/**
 * [KeyValueStore] on the plugin's `batch-rename` SharedPreferences, shared by the screen and the capability service
 * (one process); edits are applied asynchronously but visible at once.
 * zh-CN: 基于插件 `batch-rename` SharedPreferences 的 [KeyValueStore], 由界面与能力服务共用 (同一进程); 修改异步落盘但立即可见.
 */
internal class SharedPreferencesStore(context: Context) : KeyValueStore {

    private val preferences = context.applicationContext.getSharedPreferences(NAME, Context.MODE_PRIVATE)

    override fun getString(key: String): String? = preferences.getString(key, null)

    override fun edit(block: KeyValueEditor.() -> Unit) {
        val editor = preferences.edit()
        object : KeyValueEditor {
            override fun putString(key: String, value: String) {
                editor.putString(key, value)
            }

            override fun remove(key: String) {
                editor.remove(key)
            }
        }.block()
        editor.apply()
    }

    companion object {
        const val NAME = "batch-rename"
    }
}
