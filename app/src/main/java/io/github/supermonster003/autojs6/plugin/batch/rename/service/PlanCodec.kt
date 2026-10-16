package io.github.supermonster003.autojs6.plugin.batch.rename.service

import io.github.supermonster003.autojs6.plugin.batch.rename.engine.ItemKind
import io.github.supermonster003.autojs6.plugin.batch.rename.engine.ParsedOptions
import io.github.supermonster003.autojs6.plugin.batch.rename.engine.PlanException
import io.github.supermonster003.autojs6.plugin.batch.rename.engine.PlanItemStatus
import io.github.supermonster003.autojs6.plugin.batch.rename.engine.RenameEngine
import io.github.supermonster003.autojs6.plugin.batch.rename.engine.RenameItem
import io.github.supermonster003.autojs6.plugin.batch.rename.engine.RenameOptions
import io.github.supermonster003.autojs6.plugin.batch.rename.engine.RenameOptionsParser
import io.github.supermonster003.autojs6.plugin.batch.rename.engine.RenameParent
import io.github.supermonster003.autojs6.plugin.batch.rename.engine.RenamePlan
import io.github.supermonster003.autojs6.plugin.batch.rename.engine.RenameRequest
import org.autojs.plugin.batchrename.api.BatchRenameContract
import org.autojs.plugin.batchrename.api.BatchRenameErrorCodes
import org.autojs.plugin.batchrename.api.BatchRenameItemKeys
import org.autojs.plugin.batchrename.api.BatchRenameLimits
import org.autojs.plugin.batchrename.api.BatchRenameOptionKeys
import org.autojs.plugin.batchrename.api.BatchRenameParentKeys
import org.autojs.plugin.batchrename.api.BatchRenameRequestKeys
import org.autojs.plugin.batchrename.api.BatchRenameResultKeys
import java.time.DateTimeException
import java.time.ZoneId
import java.util.Locale

/**
 * Converts the contract v1 request and answer (roadmap P2.5, host `docs/dev/batch-rename-plugin-protocol-v1.md`)
 * between plain maps and the engine models. [BundleMaps] turns Bundles into these maps and back, so everything here
 * runs on the JVM. Strings from the host are only data; every key is checked for presence, type and limits, and any
 * mismatch fails the whole request with a contract error code.
 *
 * Map shapes: a Bundle is a `Map<String, Any?>`, an `ArrayList<Bundle>` a `List<Map<String, Any?>>`, an
 * `ArrayList<String>` a `List<String>`; numbers keep their boxed type.
 *
 * zh-CN: 在纯 Map 与引擎模型之间转换契约 v1 的请求与应答 (路线图 P2.5, 宿主 `docs/dev/batch-rename-plugin-protocol-v1.md`).
 * [BundleMaps] 负责 Bundle 与这些 Map 的互转, 因此这里的一切都能在 JVM 上运行. 来自宿主的字符串只作为数据; 每个键都检查是否存在,
 * 类型与上限, 不符合时以契约错误码使整个请求失败. Map 形态: Bundle 为 `Map<String, Any?>`, `ArrayList<Bundle>` 为
 * `List<Map<String, Any?>>`, `ArrayList<String>` 为 `List<String>`; 数值保持其装箱类型.
 */
internal object PlanCodec {

    /** A decoded request and the option keys the parser ignored. zh-CN: 解码后的请求及解析器忽略的选项键. */
    data class Decoded(val request: RenameRequest, val unknownOptionKeys: Set<String>)

    /**
     * @param presets the options of a preset by name (roadmap D36), null for an unknown name.
     * @throws PlanException with `UNSUPPORTED_CONTRACT_VERSION`, `INVALID_REQUEST`, `INVALID_OPTIONS` or `LIMIT_EXCEEDED`.
     */
    fun decodeRequest(values: Map<String, Any?>, presets: (String) -> RenameOptions? = { null }): Decoded {
        val version = contractVersionOf(values)
        if (!BatchRenameContract.supportsContractVersion(version)) {
            throw PlanException(
                BatchRenameErrorCodes.UNSUPPORTED_CONTRACT_VERSION,
                "Contract version $version is outside ${BatchRenameContract.MIN_CONTRACT_VERSION}..${BatchRenameContract.MAX_CONTRACT_VERSION}",
            )
        }
        val parentMaps = values.list(BatchRenameRequestKeys.PARENTS, "the request")
        val itemMaps = values.list(BatchRenameRequestKeys.ITEMS, "the request")
        if (itemMaps.size > BatchRenameLimits.MAX_ITEMS_PER_PLAN) {
            throw PlanException.limitExceeded("${itemMaps.size} items exceed ${BatchRenameLimits.MAX_ITEMS_PER_PLAN}")
        }
        if (parentMaps.size > BatchRenameLimits.MAX_PARENTS_PER_PLAN) {
            throw PlanException.limitExceeded("${parentMaps.size} parent directories exceed ${BatchRenameLimits.MAX_PARENTS_PER_PLAN}")
        }
        val characters = countCharacters(values)
        if (characters > BatchRenameLimits.MAX_REQUEST_CHARS) {
            throw PlanException.limitExceeded("The request holds $characters characters, more than ${BatchRenameLimits.MAX_REQUEST_CHARS}")
        }
        val options: ParsedOptions = RenameOptionsParser.parse(withPreset(values.map(BatchRenameRequestKeys.OPTIONS, "the request"), presets))
        val parents = parentMaps.mapIndexed { position, element -> decodeParent(element.asMap("parent $position"), position) }
        val items = itemMaps.mapIndexed { position, element -> decodeItem(element.asMap("item $position"), position) }
        val locale = values.optionalString(BatchRenameRequestKeys.LOCALE, "the request")
            ?.let { Locale.forLanguageTag(it) } ?: Locale.ROOT
        val timeZone = values.optionalString(BatchRenameRequestKeys.TIME_ZONE, "the request")?.let { id ->
            try {
                ZoneId.of(id)
            } catch (_: DateTimeException) {
                throw PlanException.invalidRequest("Unknown time zone \"$id\"")
            }
        } ?: ZoneId.of("UTC")
        val now = values.optionalLong(BatchRenameRequestKeys.NOW, "the request") ?: System.currentTimeMillis()
        return Decoded(RenameRequest(options.options, parents, items, locale, timeZone, now), options.unknownKeys)
    }

    /**
     * The options of a request that names a preset: the preset's options, then every other key of the request
     * (roadmap D36 / D44); other requests stay as they are.
     * @throws PlanException with `INVALID_OPTIONS` for a preset name that is not a string or not known.
     */
    fun withPreset(options: Map<String, Any?>, presets: (String) -> RenameOptions?): Map<String, Any?> {
        if (!options.containsKey(BatchRenameOptionKeys.PRESET)) return options
        val name = options[BatchRenameOptionKeys.PRESET] as? String
            ?: throw PlanException.invalidOptions("The option \"preset\" must be a string")
        val preset = presets(name) ?: throw PlanException.invalidOptions("The preset \"$name\" does not exist")
        return RenameOptionsParser.values(preset) + (options - BatchRenameOptionKeys.PRESET)
    }

    /**
     * The answer of a plan; every status appears in the counts, and the options it used come back complete (roadmap D44).
     * zh-CN: 方案应答; 计数中包含全部状态, 并完整回传所用的选项 (路线图 D44).
     */
    fun encodePlan(plan: RenamePlan, contractVersion: Int, options: RenameOptions): Map<String, Any?> = mapOf(
        BatchRenameResultKeys.CONTRACT_VERSION to contractVersion,
        BatchRenameResultKeys.OPTIONS to RenameOptionsParser.values(options),
        BatchRenameResultKeys.REJECTED to plan.rejected,
        BatchRenameResultKeys.ITEMS to plan.items.map { item ->
            buildMap {
                put(BatchRenameItemKeys.INDEX, item.index)
                put(BatchRenameItemKeys.NEW_NAME, item.newName)
                put(BatchRenameItemKeys.STATUS, item.status.value)
                item.reason?.let { put(BatchRenameItemKeys.REASON, it.value) }
                put(BatchRenameItemKeys.TWO_PHASE, item.twoPhase)
                put(BatchRenameItemKeys.OVERWRITES, item.overwrites)
            }
        },
        BatchRenameResultKeys.COUNTS to PlanItemStatus.entries.associate { status -> status.value to (plan.counts[status] ?: 0) },
    )

    /** A failed call: the contract version, the error code and a message of at most 1024 characters. */
    fun encodeError(code: String, message: String, contractVersion: Int): Map<String, Any?> = mapOf(
        BatchRenameResultKeys.CONTRACT_VERSION to contractVersion,
        BatchRenameResultKeys.ERROR_CODE to code,
        BatchRenameResultKeys.ERROR_MESSAGE to message.take(MAX_ERROR_MESSAGE_CHARS),
    )

    /** The version the host wrote, or 0 when it is missing or not an Int. zh-CN: 宿主写入的版本; 缺失或不是 Int 时为 0. */
    fun contractVersionOf(values: Map<String, Any?>?): Int = values?.get(BatchRenameRequestKeys.CONTRACT_VERSION) as? Int ?: 0

    private const val MAX_ERROR_MESSAGE_CHARS = 1024

    private fun decodeParent(values: Map<String, Any?>, position: Int): RenameParent {
        val subject = "parent $position"
        val names = values.optionalList(BatchRenameParentKeys.EXISTING_NAMES, subject).orEmpty().mapIndexed { index, name ->
            name as? String ?: throw PlanException.invalidRequest("Existing name $index of $subject must be a string")
        }
        return RenameParent(
            parentId = values.string(BatchRenameParentKeys.PARENT_ID, subject).also {
                if (it.isEmpty()) throw PlanException.invalidRequest("The id of $subject is empty")
            },
            existingNames = names,
            caseInsensitive = values.optionalBoolean(BatchRenameParentKeys.CASE_INSENSITIVE, subject) ?: false,
            restrictedCharacters = values.optionalBoolean(BatchRenameParentKeys.RESTRICTED_CHARACTERS, subject) ?: false,
        )
    }

    private fun decodeItem(values: Map<String, Any?>, position: Int): RenameItem {
        val subject = "item $position"
        val kindValue = values.int(BatchRenameItemKeys.KIND, subject)
        return RenameItem(
            index = values.int(BatchRenameItemKeys.INDEX, subject),
            parentId = values.string(BatchRenameItemKeys.PARENT_ID, subject),
            name = values.string(BatchRenameItemKeys.NAME, subject),
            kind = ItemKind.entries.firstOrNull { it.value == kindValue } ?: throw PlanException.invalidRequest("Unknown kind $kindValue of $subject"),
            depth = values.optionalInt(BatchRenameItemKeys.DEPTH, subject) ?: 0,
            lastModified = values.optionalLong(BatchRenameItemKeys.LAST_MODIFIED, subject) ?: 0L,
            length = values.optionalLong(BatchRenameItemKeys.LENGTH, subject) ?: -1L,
            excluded = values.optionalBoolean(BatchRenameItemKeys.EXCLUDED, subject) ?: false,
        )
    }

    /** Characters of every string in the request, including nested lists and maps. zh-CN: 请求中全部字符串的字符数. */
    private fun countCharacters(value: Any?): Long = when (value) {
        is String -> value.length.toLong()
        is Map<*, *> -> value.entries.sumOf { (key, element) -> (key as? String)?.length?.toLong().orZero() + countCharacters(element) }
        is List<*> -> value.sumOf { countCharacters(it) }
        else -> 0L
    }

    private fun Long?.orZero(): Long = this ?: 0L

    private fun Map<String, Any?>.string(key: String, subject: String): String =
        get(key) as? String ?: throw PlanException.invalidRequest("\"$key\" of $subject must be a string")

    private fun Map<String, Any?>.optionalString(key: String, subject: String): String? =
        if (get(key) == null) null else string(key, subject)

    private fun Map<String, Any?>.int(key: String, subject: String): Int =
        get(key) as? Int ?: throw PlanException.invalidRequest("\"$key\" of $subject must be an Int")

    private fun Map<String, Any?>.optionalInt(key: String, subject: String): Int? = if (get(key) == null) null else int(key, subject)

    /** Longs accept Int values as well. zh-CN: Long 字段同样接受 Int 值. */
    private fun Map<String, Any?>.optionalLong(key: String, subject: String): Long? = when (val value = get(key)) {
        null -> null
        is Long -> value
        is Int -> value.toLong()
        else -> throw PlanException.invalidRequest("\"$key\" of $subject must be a Long")
    }

    private fun Map<String, Any?>.optionalBoolean(key: String, subject: String): Boolean? = when (val value = get(key)) {
        null -> null
        is Boolean -> value
        else -> throw PlanException.invalidRequest("\"$key\" of $subject must be a Boolean")
    }

    private fun Map<String, Any?>.list(key: String, subject: String): List<Any?> =
        get(key) as? List<*> ?: throw PlanException.invalidRequest("\"$key\" of $subject must be a list")

    private fun Map<String, Any?>.optionalList(key: String, subject: String): List<Any?>? = if (get(key) == null) null else list(key, subject)

    private fun Map<String, Any?>.map(key: String, subject: String): Map<String, Any?> = get(key).asMap("\"$key\" of $subject")

    @Suppress("UNCHECKED_CAST")
    private fun Any?.asMap(subject: String): Map<String, Any?> {
        val map = this as? Map<*, *> ?: throw PlanException.invalidRequest("$subject must be a Bundle")
        if (map.keys.any { it !is String }) throw PlanException.invalidRequest("$subject has keys that are not strings")
        return map as Map<String, Any?>
    }
}

/**
 * The answer to a plan request, computed without ever throwing (contract v1: `plan` never throws and never returns
 * null): engine errors keep their codes, anything unexpected becomes `INTERNAL_ERROR`.
 * zh-CN: 计算方案请求的应答, 从不抛出异常 (契约 v1: `plan` 不抛异常也不返回 null): 引擎错误保留其错误码, 其他意外都变为
 * `INTERNAL_ERROR`.
 */
internal class BatchRenamePlanner(
    private val engine: RenameEngine,
    /** The options of a preset by name (roadmap D36). zh-CN: 按名称取预设的选项. */
    private val presets: (String) -> RenameOptions? = { null },
    private val log: (String) -> Unit = {},
) {

    fun answer(request: Map<String, Any?>?): Map<String, Any?> {
        val version = PlanCodec.contractVersionOf(request)
        if (request == null) return PlanCodec.encodeError(BatchRenameErrorCodes.INVALID_REQUEST, "The plan request is missing", version)
        return try {
            val decoded = PlanCodec.decodeRequest(request, presets)
            if (decoded.unknownOptionKeys.isNotEmpty()) log("Ignored unknown option keys ${decoded.unknownOptionKeys.sorted()}")
            PlanCodec.encodePlan(engine.plan(decoded.request), version, decoded.request.options)
        } catch (error: PlanException) {
            PlanCodec.encodeError(error.code, error.message.orEmpty(), version)
        } catch (error: Exception) {
            log("Plan failed: $error")
            PlanCodec.encodeError(BatchRenameErrorCodes.INTERNAL_ERROR, error.toString(), version)
        } catch (error: StackOverflowError) {
            log("Plan failed: $error")
            PlanCodec.encodeError(BatchRenameErrorCodes.INTERNAL_ERROR, error.toString(), version)
        }
    }
}
