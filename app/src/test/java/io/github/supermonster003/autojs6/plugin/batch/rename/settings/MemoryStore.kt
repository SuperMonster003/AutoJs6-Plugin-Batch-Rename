package io.github.supermonster003.autojs6.plugin.batch.rename.settings

/** [KeyValueStore] in memory for JVM tests. zh-CN: 供 JVM 测试使用的内存 [KeyValueStore]. */
internal class MemoryStore : KeyValueStore {

    val values = LinkedHashMap<String, String>()

    override fun getString(key: String): String? = values[key]

    override fun edit(block: KeyValueEditor.() -> Unit) {
        object : KeyValueEditor {
            override fun putString(key: String, value: String) {
                values[key] = value
            }

            override fun remove(key: String) {
                values.remove(key)
            }
        }.block()
    }
}
