package io.github.supermonster003.autojs6.plugin.batch.rename.service

import android.os.Bundle

/**
 * Converts the contract Bundles to the plain maps [PlanCodec] works on and back (roadmap P2.5): a Bundle becomes a
 * `Map<String, Any?>`, a list of Bundles a list of maps, a list of strings stays a list of strings; other values pass
 * unchanged. Only the shapes of contract v1 are written back.
 * zh-CN: 在契约 Bundle 与 [PlanCodec] 使用的纯 Map 之间转换 (路线图 P2.5): Bundle 变为 `Map<String, Any?>`, Bundle 列表变为
 * Map 列表, 字符串列表保持不变, 其他值原样传递. 写回时只使用契约 v1 的形态.
 */
internal object BundleMaps {

    fun toMap(bundle: Bundle): Map<String, Any?> = bundle.keySet().associateWith { key ->
        @Suppress("DEPRECATION")
        convert(bundle.get(key))
    }

    private fun convert(value: Any?): Any? = when (value) {
        is Bundle -> toMap(value)
        is List<*> -> value.map(::convert)
        is Array<*> -> value.map(::convert)
        else -> value
    }

    fun toBundle(map: Map<String, Any?>): Bundle = Bundle().apply {
        map.forEach { (key, value) -> putValue(key, value) }
    }

    @Suppress("UNCHECKED_CAST")
    private fun Bundle.putValue(key: String, value: Any?) {
        when (value) {
            null -> putString(key, null)
            is String -> putString(key, value)
            is Int -> putInt(key, value)
            is Long -> putLong(key, value)
            is Boolean -> putBoolean(key, value)
            is Map<*, *> -> putBundle(key, toBundle(value as Map<String, Any?>))
            // An empty list is written as a list of Bundles, the shape of every list the answer carries.
            is List<*> -> if (value.isNotEmpty() && value.all { it is String }) {
                putStringArrayList(key, ArrayList(value as List<String>))
            } else {
                putParcelableArrayList(key, ArrayList(value.map { toBundle(it as Map<String, Any?>) }))
            }
            else -> throw IllegalArgumentException("Unsupported value of \"$key\": ${value.javaClass.name}")
        }
    }
}
