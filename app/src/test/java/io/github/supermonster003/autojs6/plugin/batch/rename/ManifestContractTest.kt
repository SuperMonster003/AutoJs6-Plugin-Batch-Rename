package io.github.supermonster003.autojs6.plugin.batch.rename

import io.github.supermonster003.autojs6.plugin.batch.rename.RepositoryFiles.android
import io.github.supermonster003.autojs6.plugin.batch.rename.RepositoryFiles.androidOrNull
import io.github.supermonster003.autojs6.plugin.batch.rename.RepositoryFiles.child
import io.github.supermonster003.autojs6.plugin.batch.rename.RepositoryFiles.children
import org.autojs.plugin.common.api.PluginActions
import org.autojs.plugin.common.api.PluginCapabilityKeys
import org.autojs.plugin.explorer.api.ExplorerActionPluginActions
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.w3c.dom.Element

/**
 * Keeps `AndroidManifest.xml` and [BatchRenamePlugin] from drifting apart: the host discovers the plugin through the
 * Manifest (Wake activity, INFO / capability / Explorer Action services, the rename activity), while the services and
 * the tests use the Kotlin constants (roadmap 5.4, D22, D23, D28).
 * zh-CN: 防止 `AndroidManifest.xml` 与 [BatchRenamePlugin] 漂移: 宿主经 Manifest 发现插件 (Wake Activity, INFO / 能力 /
 * Explorer Action 服务, 重命名 Activity), 服务与测试则使用 Kotlin 常量 (路线图 5.4, D22, D23, D28).
 */
class ManifestContractTest {

    private val manifest: Element by lazy { RepositoryFiles.xml("app/src/main/AndroidManifest.xml") }

    private val application: Element get() = manifest.child("application")

    @Test
    fun `the plugin permission is the only permission and nothing else is requested`() {
        assertEquals(listOf(PLUGIN_PERMISSION), manifest.children("uses-permission").map { it.android("name") })
        assertTrue("no package queries: the plugin never looks up other apps", manifest.children("queries").isEmpty())
        assertTrue("no uses-sdk override: SDK levels come from version.properties", manifest.children("uses-sdk").isEmpty())
        assertTrue("no feature requirements", manifest.children("uses-feature").isEmpty())
    }

    @Test
    fun `the application disables backups and uses the generated title and system icon`() {
        assertEquals("false", application.android("allowBackup"))
        assertEquals("false", application.android("fullBackupContent"))
        assertEquals("@xml/data_extraction_rules", application.android("dataExtractionRules"))
        assertEquals("@string/app_name", application.android("label"))
        assertEquals("true", application.android("supportsRtl"))
        assertEquals("@mipmap/ic_icon_studio_application", application.android("icon"))
        assertEquals("@mipmap/ic_icon_studio_application", application.android("roundIcon"))
        assertNull("no Application subclass", application.androidOrNull("name"))
    }

    @Test
    fun `application meta-data names the wake activity, the author and the page alignment`() {
        val metaData = application.children("meta-data").associate { it.android("name") to it.android("value") }
        assertEquals(
            mapOf(
                "org.autojs.plugin.contract.NATIVE_PAGE_ALIGNMENT" to "16384",
                "org.autojs.plugin.WAKE_ACTIVITY" to ".WakeActivity",
                "org.autojs.plugin.info.AUTHOR" to "@string/plugin_author",
            ),
            metaData,
        )
        assertEquals(PluginCapabilityKeys.NATIVE_PAGE_ALIGNMENT, "org.autojs.plugin.contract.NATIVE_PAGE_ALIGNMENT")
    }

    @Test
    fun `the wake activity follows the activation contract`() {
        val wake = activity(".WakeActivity")
        assertEquals("true", wake.android("exported"))
        assertEquals("true", wake.android("excludeFromRecents"))
        assertEquals("true", wake.android("finishOnTaskLaunch"))
        assertEquals(PLUGIN_PERMISSION, wake.android("permission"))
        assertEquals("@android:style/Theme.NoDisplay", wake.android("theme"))
        assertFilter(wake, action = "org.autojs.plugin.action.WAKE", category = DEFAULT_CATEGORY)
    }

    @Test
    fun `the rename activity accepts only explorer action requests behind the plugin permission`() {
        val rename = activity(".BatchRenameActivity")
        assertEquals("true", rename.android("exported"))
        assertEquals(PLUGIN_PERMISSION, rename.android("permission"))
        // Without the EXECUTE + DEFAULT filter the host silently drops every catalog action (3-Maple Image lesson).
        assertFilter(rename, action = ExplorerActionPluginActions.EXECUTE, category = DEFAULT_CATEGORY)
        assertEquals(listOf(".WakeActivity", ".BatchRenameActivity"), application.children("activity").map { it.android("name") })
    }

    @Test
    fun `no component offers a launcher entry`() {
        val categories = listOf("activity", "activity-alias")
            .flatMap { application.children(it) }
            .flatMap { it.children("intent-filter") }
            .flatMap { it.children("category") }
            .map { it.android("name") }
        assertTrue("roadmap D4: no launcher icon", "android.intent.category.LAUNCHER" !in categories)
        assertTrue(application.children("activity-alias").isEmpty())
    }

    @Test
    fun `the three services match the discovery constants`() {
        assertEquals(
            listOf(".BatchRenamePluginInfoService", ".BatchRenamePluginService", ".BatchRenameExplorerActionService"),
            application.children("service").map { it.android("name") },
        )
        val info = service(".BatchRenamePluginInfoService")
        assertFilter(info, action = PluginActions.INFO, category = BatchRenamePlugin.SERVICE_CATEGORY)
        assertRequiresHostVersion(info)

        val capability = service(".BatchRenamePluginService")
        assertFilter(capability, action = BatchRenamePlugin.SERVICE_ACTION, category = BatchRenamePlugin.SERVICE_CATEGORY)
        assertRequiresHostVersion(capability)

        val explorer = service(".BatchRenameExplorerActionService")
        assertFilter(explorer, action = ExplorerActionPluginActions.EXPLORER_ACTION, category = DEFAULT_CATEGORY)
        // The Explorer Action registry reads the host floor from getInfo().capabilities, not from meta-data.
        assertTrue(explorer.children("meta-data").isEmpty())

        listOf(info, capability, explorer).forEach { service ->
            assertEquals("true", service.android("exported"))
            assertEquals(PLUGIN_PERMISSION, service.android("permission"))
            assertNull("services run in the plugin's default process", service.androidOrNull("process"))
        }
    }

    @Test
    fun `every exported component requires the plugin permission`() {
        val components = listOf("activity", "activity-alias", "service", "receiver", "provider").flatMap { application.children(it) }
        val unprotected = components.filter { it.androidOrNull("exported") == "true" && it.androidOrNull("permission") != PLUGIN_PERMISSION }
        assertTrue("exported without the plugin permission: ${unprotected.map { it.android("name") }}", unprotected.isEmpty())
        assertTrue("receivers and providers are not declared", application.children("receiver").isEmpty() && application.children("provider").isEmpty())
    }

    private fun activity(name: String): Element = application.children("activity").single { it.android("name") == name }

    private fun service(name: String): Element = application.children("service").single { it.android("name") == name }

    private fun assertFilter(component: Element, action: String, category: String) {
        val filter = component.child("intent-filter")
        assertEquals(listOf(action), filter.children("action").map { it.android("name") })
        assertEquals(listOf(category), filter.children("category").map { it.android("name") })
        assertTrue(filter.children("data").isEmpty())
    }

    private fun assertRequiresHostVersion(service: Element) {
        val metaData = service.children("meta-data").associate { it.android("name") to it.android("value") }
        assertEquals(mapOf(PluginCapabilityKeys.REQUIRES_HOST_VERSION to "@string/plugin_requires_host_version"), metaData)
    }

    private companion object {
        const val PLUGIN_PERMISSION = "org.autojs.permission.PLUGIN"
        const val DEFAULT_CATEGORY = "android.intent.category.DEFAULT"
    }

}
