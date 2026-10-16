package io.github.supermonster003.autojs6.plugin.batch.rename.service

import io.github.supermonster003.autojs6.plugin.batch.rename.engine.PlanException
import io.github.supermonster003.autojs6.plugin.batch.rename.engine.RenameOptionsParser
import io.github.supermonster003.autojs6.plugin.batch.rename.presets.PresetConflict
import io.github.supermonster003.autojs6.plugin.batch.rename.presets.PresetDocument
import io.github.supermonster003.autojs6.plugin.batch.rename.presets.PresetStore
import org.autojs.plugin.batchrename.api.BatchRenameContract
import org.autojs.plugin.batchrename.api.BatchRenameErrorCodes
import org.autojs.plugin.batchrename.api.BatchRenamePresetKeys
import org.autojs.plugin.batchrename.api.BatchRenameResultKeys

/**
 * The preset transactions `presets` and `importPresets` (roadmap D36, host protocol "Presets") as plain maps, never
 * throwing: failures come back as contract error codes like those of `plan`. Empty name lists travel as empty lists of
 * Bundles ([BundleMaps]); the host reads a missing or mistyped list as empty.
 * zh-CN: 以纯 Map 实现预设事务 `presets` 与 `importPresets` (路线图 D36, 宿主协议 "Presets"), 从不抛出: 失败与 `plan` 一样以契约错误码
 * 返回. 空的名称列表按空 Bundle 列表传输 ([BundleMaps]); 宿主把缺失或类型不符的列表视为空.
 */
internal class PresetCalls(
    private val store: () -> PresetStore,
    private val log: (String) -> Unit = {},
) {

    fun list(request: Map<String, Any?>?): Map<String, Any?> = answer(request) {
        mapOf(
            BatchRenamePresetKeys.PRESETS to store().list().map { preset ->
                mapOf(BatchRenamePresetKeys.NAME to preset.name, BatchRenamePresetKeys.OPTIONS to RenameOptionsParser.values(preset.options))
            },
        )
    }

    fun import(request: Map<String, Any?>?): Map<String, Any?> = answer(request) { values ->
        val document = values[BatchRenamePresetKeys.DOCUMENT] as? String
            ?: throw PlanException.invalidRequest("\"${BatchRenamePresetKeys.DOCUMENT}\" must be a string")
        val policy = when (val value = values[BatchRenamePresetKeys.ON_CONFLICT]) {
            null -> PresetConflict.SKIP
            else -> PresetConflict.entries.firstOrNull { it.value == value } ?: throw PlanException.invalidOptions(
                "\"${BatchRenamePresetKeys.ON_CONFLICT}\" must be one of ${PresetConflict.entries.joinToString { "\"${it.value}\"" }}",
            )
        }
        val result = store().import(PresetDocument.decode(document), policy)
        mapOf(
            BatchRenamePresetKeys.IMPORTED to result.imported,
            BatchRenamePresetKeys.SKIPPED to result.skipped,
            BatchRenamePresetKeys.REJECTED to result.rejected.map { rejection ->
                buildMap<String, Any> {
                    put(BatchRenamePresetKeys.INDEX, rejection.index)
                    rejection.name?.let { put(BatchRenamePresetKeys.NAME, it) }
                    put(BatchRenamePresetKeys.REASON, rejection.message)
                }
            },
        )
    }

    private fun answer(request: Map<String, Any?>?, block: (Map<String, Any?>) -> Map<String, Any?>): Map<String, Any?> {
        val version = PlanCodec.contractVersionOf(request)
        if (request == null) return PlanCodec.encodeError(BatchRenameErrorCodes.INVALID_REQUEST, "The preset request is missing", version)
        if (!BatchRenameContract.supportsContractVersion(version)) {
            return PlanCodec.encodeError(
                BatchRenameErrorCodes.UNSUPPORTED_CONTRACT_VERSION,
                "Contract version $version is outside ${BatchRenameContract.MIN_CONTRACT_VERSION}..${BatchRenameContract.MAX_CONTRACT_VERSION}",
                version,
            )
        }
        return try {
            mapOf(BatchRenameResultKeys.CONTRACT_VERSION to version) + block(request)
        } catch (error: PlanException) {
            PlanCodec.encodeError(error.code, error.message.orEmpty(), version)
        } catch (error: Exception) {
            log("Preset call failed: $error")
            PlanCodec.encodeError(BatchRenameErrorCodes.INTERNAL_ERROR, error.toString(), version)
        }
    }
}
