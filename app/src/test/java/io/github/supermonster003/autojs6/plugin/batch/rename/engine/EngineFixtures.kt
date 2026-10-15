package io.github.supermonster003.autojs6.plugin.batch.rename.engine

import io.github.supermonster003.autojs6.plugin.batch.rename.engine.variables.RandomKind
import io.github.supermonster003.autojs6.plugin.batch.rename.engine.variables.RandomSource
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZoneOffset
import java.util.Locale

/** Builders shared by the engine tests. zh-CN: 引擎测试共用的构造函数. */
internal object EngineFixtures {

    const val PARENT = "p0"

    /** One parent holding [names]; every name becomes an item, folders end with `/` in [names]. */
    fun request(
        options: RenameOptions,
        vararg names: String,
        existingNames: List<String> = emptyList(),
        locale: Locale = Locale.ENGLISH,
        lastModified: Long = millis(2020, 11, 2, 13, 5, 9, 87),
        now: Long = millis(2026, 10, 15, 8, 30, 0, 0),
        caseInsensitive: Boolean = false,
        restrictedCharacters: Boolean = false,
    ): RenameRequest {
        val items = names.mapIndexed { index, raw ->
            val directory = raw.endsWith("/")
            RenameItem(index, PARENT, raw.removeSuffix("/"), if (directory) ItemKind.DIRECTORY else ItemKind.FILE, lastModified = lastModified)
        }
        return RenameRequest(
            options = options,
            parents = listOf(RenameParent(PARENT, items.map { it.name } + existingNames, caseInsensitive, restrictedCharacters)),
            items = items,
            locale = locale,
            timeZone = ZoneId.of("UTC"),
            now = now,
        )
    }

    fun millis(year: Int, month: Int, day: Int, hour: Int = 0, minute: Int = 0, second: Int = 0, millis: Int = 0): Long =
        LocalDateTime.of(year, month, day, hour, minute, second, millis * 1_000_000).toInstant(ZoneOffset.UTC).toEpochMilli()

    /** Deterministic random values: `A`, `B`, ... repeated to the length, and a fixed UUID. */
    val fixedRandom = RandomSource { kind, length ->
        when (kind) {
            RandomKind.UUID_V4 -> "123e4567-e89b-42d3-a456-426614174000"
            else -> String(CharArray(length) { kind.alphabet[it % kind.alphabet.length] })
        }
    }

    val engine: RenameEngine by lazy { RenameEngine(random = fixedRandom) }

    fun newNames(options: RenameOptions, vararg names: String, existingNames: List<String> = emptyList(), locale: Locale = Locale.ENGLISH): List<String> =
        engine.plan(request(options, *names, existingNames = existingNames, locale = locale)).items.map { it.newName }
}
