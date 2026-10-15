package io.github.supermonster003.autojs6.plugin.batch.rename

import org.autojs.plugin.explorer.api.ExplorerActionProtocol
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * The pure PluginInfo field mapping and the identity published by the build, the Manifest and the README sources
 * (roadmap 5.4, D26, D28). The Android-backed `toPluginInfo()` mapping is exercised by the device contract test.
 * zh-CN: 纯数据的 PluginInfo 字段映射, 以及构建, Manifest 与 README 源发布的身份 (路线图 5.4, D26, D28). 依赖 Android 的
 * `toPluginInfo()` 映射由设备契约测试覆盖.
 */
class PluginRuntimeInfoTest {

    @Test
    fun `runtime fields keep the identity, the package version and an unrestricted ABI list`() {
        val fields = pluginRuntimeFields(
            name = "Batch Rename",
            description = "Batch renames files and folders",
            author = "SuperMonster003",
            id = "batch-rename",
            engine = "batch-rename",
            variant = "default",
            versionName = "1.0.0",
            versionCode = 5L,
            versionDate = "Oct 15, 2026",
        )
        assertEquals("Batch Rename", fields.name)
        assertEquals("Batch renames files and folders", fields.description)
        assertEquals("@raw/plugin_instruction", fields.instruction)
        assertEquals(BatchRenamePlugin.INSTRUCTION_REFERENCE, fields.instruction)
        assertEquals("SuperMonster003", fields.author)
        assertEquals("batch-rename", fields.id)
        assertEquals("batch-rename", fields.engine)
        assertEquals("default", fields.variant)
        assertEquals("1.0.0", fields.versionName)
        assertEquals(5L, fields.versionCode)
        assertEquals("Oct 15, 2026", fields.versionDate)
        // Roadmap D28: one universal APK, so no ABI restriction is reported to the Plugin Center.
        assertEquals(emptyList<String>(), fields.supportedAbis)
        assertEquals(BatchRenamePlugin.REQUIRED_HOST_VERSION, fields.requiredHostVersion)
    }

    @Test
    fun `identity constants follow roadmap 5_4`() {
        assertEquals("batch-rename", BatchRenamePlugin.PLUGIN_ID)
        assertEquals("batch-rename", BatchRenamePlugin.ENGINE)
        assertEquals("default", BatchRenamePlugin.VARIANT)
        assertEquals("org.autojs.plugin.BATCH_RENAME", BatchRenamePlugin.SERVICE_ACTION)
        assertEquals("batch-rename", BatchRenamePlugin.SERVICE_CATEGORY)
        assertEquals(BatchRenamePlugin.PLUGIN_ID, BatchRenamePlugin.SERVICE_CATEGORY)
        assertEquals("org.autojs.plugin.batchrename.api.IBatchRenamePlugin", BatchRenamePlugin.CAPABILITY_DESCRIPTOR)
    }

    @Test
    fun `the declared explorer action protocol is served by the staged contract`() {
        assertTrue(
            "the catalog cannot declare a protocol newer than libs/explorer-action-api.aar",
            BatchRenamePlugin.EXPLORER_PROTOCOL_VERSION <= ExplorerActionProtocol.VERSION,
        )
        assertEquals("P0 declares the current host protocol; P1.2 raises it to 25", 24, BatchRenamePlugin.EXPLORER_PROTOCOL_VERSION)
    }

    @Test
    fun `the build publishes the same identity as the constants`() {
        val build = RepositoryFiles.text("app/build.gradle.kts")
        assertTrue(build.contains("val globalApplicationId = \"$PACKAGE_NAME\""))
        assertTrue(build.contains("namespace = globalApplicationId"))
        assertTrue(build.contains("applicationId = globalApplicationId"))
        assertTrue(build.contains("resValue(\"string\", \"app_name\", \"Batch Rename\")"))
        assertTrue(build.contains("resValue(\"string\", \"plugin_author\", \"SuperMonster003\")"))
        assertTrue(build.contains("resValue(\"string\", \"plugin_id\", \"${BatchRenamePlugin.PLUGIN_ID}\")"))
        assertTrue(build.contains("resValue(\"string\", \"plugin_engine\", \"${BatchRenamePlugin.ENGINE}\")"))
        assertTrue(build.contains("resValue(\"string\", \"plugin_variant\", \"${BatchRenamePlugin.VARIANT}\")"))
        assertTrue(build.contains("resValue(\"string\", \"plugin_requires_host_version\", \"\$requiredHostVersion\")"))
        // Roadmap D26: the minimum host build is changed in the build script, the constant and the README sources together.
        assertTrue(build.contains("val requiredHostVersion = ${BatchRenamePlugin.REQUIRED_HOST_VERSION}\n"))
        // Roadmap D28: no ABI splits; the release verifier pins the Compose graphics-path library set.
        assertFalse("no ABI split block is declared", build.contains("splits {"))
        assertTrue(build.contains("\"lib/\$it/libandroidx.graphics.path.so\""))
        assertTrue(build.contains("nativeAlignment { expectNoNativeLibraries.set(false) }"))

        val settings = RepositoryFiles.text("settings.gradle.kts")
        assertTrue(settings.contains("rootProject.name = \"autojs6-plugin-batch-rename\""))
    }

    @Test
    fun `the README sources publish the same identity as the constants`() {
        val common = RepositoryFiles.text(".readme/common.json")
        mapOf(
            "plugin_application_id" to PACKAGE_NAME,
            "plugin_id" to BatchRenamePlugin.PLUGIN_ID,
            "plugin_engine" to BatchRenamePlugin.ENGINE,
            "plugin_variant" to BatchRenamePlugin.VARIANT,
            "discovery_action" to BatchRenamePlugin.SERVICE_ACTION,
            "discovery_category" to BatchRenamePlugin.SERVICE_CATEGORY,
            "required_host_version_code" to BatchRenamePlugin.REQUIRED_HOST_VERSION.toString(),
        ).forEach { (key, value) ->
            assertTrue(".readme/common.json must publish $key = $value", common.contains("\"$key\": \"$value\""))
        }
    }

    @Test
    fun `the package of the Kotlin sources is the application id`() {
        assertEquals(PACKAGE_NAME, BatchRenamePlugin::class.java.packageName)
    }

    private companion object {
        const val PACKAGE_NAME = "io.github.supermonster003.autojs6.plugin.batch.rename"
    }

}
