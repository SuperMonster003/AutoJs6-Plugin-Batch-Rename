package io.github.supermonster003.autojs6.plugin.batch.rename

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.content.pm.ServiceInfo
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.os.Parcel
import android.os.Parcelable
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.autojs.plugin.common.api.IPluginInfoProvider
import org.autojs.plugin.common.api.PluginActions
import org.autojs.plugin.common.api.PluginCapabilityKeys
import org.autojs.plugin.common.api.PluginInfo
import org.autojs.plugin.explorer.api.ExplorerActionCapabilityKeys
import org.autojs.plugin.explorer.api.ExplorerActionCatalogKeys
import org.autojs.plugin.explorer.api.ExplorerActionPluginActions
import org.autojs.plugin.explorer.api.ExplorerActionPluginIds
import org.autojs.plugin.explorer.api.ExplorerArchiveOpenKeys
import org.autojs.plugin.explorer.api.ExplorerArchiveOpenValues
import org.autojs.plugin.explorer.api.IExplorerActionPlugin
import org.junit.Assert.assertArrayEquals
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicReference
import java.util.zip.ZipEntry
import java.util.zip.ZipFile

/**
 * The host-facing activation and discovery contract against the installed APK (roadmap P0.2, AGENTS 14.2): the Wake
 * activity, discovery of exactly one component per action / category, explicit binding with the expected Binder
 * descriptors, `getInfo()` answers marshalled through real Parcels with the AIDL transaction codes the host uses, the
 * empty protocol 24 catalog, the rename activity entry, and the single universal APK of roadmap D28.
 *
 * The services run in the plugin's own process, so the calls below use `transact` with explicit Parcels instead of
 * `asInterface`, which would return the local object and skip marshalling.
 *
 * zh-CN: 针对已安装 APK 验证面向宿主的激活与发现契约 (路线图 P0.2, AGENTS 14.2): Wake Activity, 每组 action / category 恰好
 * 发现一个组件, 显式绑定与 Binder 描述符, 按宿主使用的 AIDL 事务号经真实 Parcel 编组的 `getInfo()` 应答, 协议 24 的空动作目录,
 * 重命名 Activity 入口, 以及路线图 D28 的单个通用 APK. 服务与测试同进程, 因此下文用显式 Parcel 调用 `transact`, 不用会直接返回
 * 本地对象而跳过编组的 `asInterface`.
 */
@RunWith(AndroidJUnit4::class)
class BatchRenamePluginContractTest {

    private val context: Context
        get() = InstrumentationRegistry.getInstrumentation().targetContext

    private val packageName: String
        get() = context.packageName

    @Test
    fun wakeActivityFollowsTheHostActivationContract() {
        val metaData = requireNotNull(context.packageManager.getApplicationInfo(packageName, PackageManager.GET_META_DATA).metaData)
        assertEquals(".WakeActivity", metaData.getString("org.autojs.plugin.WAKE_ACTIVITY"))
        assertEquals(context.getString(R.string.plugin_author), metaData.getString("org.autojs.plugin.info.AUTHOR"))
        assertEquals(16384, metaData.getInt(PluginCapabilityKeys.NATIVE_PAGE_ALIGNMENT, -1))

        val component = ComponentName(packageName, "$packageName.WakeActivity")
        val activityInfo = context.packageManager.getActivityInfo(component, 0)
        assertTrue(activityInfo.exported)
        assertTrue(activityInfo.enabled)
        assertEquals(PLUGIN_PERMISSION, activityInfo.permission)
        assertEquals(android.R.style.Theme_NoDisplay, activityInfo.theme)
        assertTrue(activityInfo.flags and ActivityInfo.FLAG_EXCLUDE_FROM_RECENTS != 0)
        assertTrue(activityInfo.flags and ActivityInfo.FLAG_FINISH_ON_TASK_LAUNCH != 0)

        val matches = queryActivities(Intent("org.autojs.plugin.action.WAKE").addCategory(Intent.CATEGORY_DEFAULT).setPackage(packageName))
        assertEquals("the WAKE action resolves exactly one activity", listOf(component.className), matches)
    }

    @Test
    fun theRenameActivityIsTheOnlyExecuteTargetAndThereIsNoLauncherEntry() {
        val execute = Intent(ExplorerActionPluginActions.EXECUTE).addCategory(Intent.CATEGORY_DEFAULT).setPackage(packageName)
        assertEquals(listOf("$packageName.BatchRenameActivity"), queryActivities(execute))
        val activityInfo = context.packageManager.getActivityInfo(ComponentName(packageName, "$packageName.BatchRenameActivity"), 0)
        assertTrue(activityInfo.exported)
        assertEquals(PLUGIN_PERMISSION, activityInfo.permission)

        val launcher = Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_LAUNCHER).setPackage(packageName)
        assertEquals("roadmap D4: no launcher entry", emptyList<String>(), queryActivities(launcher))
    }

    @Test
    fun infoServiceAnswersTheBatchRenameIdentity() {
        val serviceInfo = discoverSingleService(PluginActions.INFO, BatchRenamePlugin.SERVICE_CATEGORY, BatchRenamePluginInfoService::class.java.name)
        assertEquals(BatchRenamePlugin.REQUIRED_HOST_VERSION.toString(), serviceInfo.metaData?.get(PluginCapabilityKeys.REQUIRES_HOST_VERSION)?.toString())
        withBoundService(serviceInfo) { binder ->
            assertEquals(IPluginInfoProvider.DESCRIPTOR, binder.interfaceDescriptor)
            val info = transactPluginInfo(binder, IPluginInfoProvider.DESCRIPTOR)
            assertIdentity(info, expectedEngine = BatchRenamePlugin.ENGINE)
            assertEquals(setOf(PluginCapabilityKeys.REQUIRES_HOST_VERSION), requireNotNull(info.capabilities).keySet())
            assertEquals(BatchRenamePlugin.REQUIRED_HOST_VERSION, info.capabilities!!.getLong(PluginCapabilityKeys.REQUIRES_HOST_VERSION))
        }
    }

    @Test
    fun capabilityServiceIsDiscoverableAndRejectsCallsUntilTheContractLands() {
        val serviceInfo = discoverSingleService(BatchRenamePlugin.SERVICE_ACTION, BatchRenamePlugin.SERVICE_CATEGORY, BatchRenamePluginService::class.java.name)
        assertEquals(BatchRenamePlugin.REQUIRED_HOST_VERSION.toString(), serviceInfo.metaData?.get(PluginCapabilityKeys.REQUIRES_HOST_VERSION)?.toString())
        withBoundService(serviceInfo) { binder ->
            assertEquals(BatchRenamePlugin.CAPABILITY_DESCRIPTOR, binder.interfaceDescriptor)
            // Placeholder until roadmap P1.1 / P4.1: every transaction is refused instead of answered with guesses.
            val data = Parcel.obtain()
            val reply = Parcel.obtain()
            try {
                data.writeInterfaceToken(BatchRenamePlugin.CAPABILITY_DESCRIPTOR)
                assertFalse(binder.transact(IBinder.FIRST_CALL_TRANSACTION, data, reply, 0))
            } finally {
                data.recycle()
                reply.recycle()
            }
        }
    }

    @Test
    fun explorerActionServiceAnswersTheExplorerIdentityAndAnEmptyCatalog() {
        val serviceInfo = discoverSingleService(ExplorerActionPluginActions.EXPLORER_ACTION, Intent.CATEGORY_DEFAULT, BatchRenameExplorerActionService::class.java.name)
        assertNull("the Explorer Action registry reads the host floor from getInfo()", serviceInfo.metaData?.get(PluginCapabilityKeys.REQUIRES_HOST_VERSION))
        withBoundService(serviceInfo) { binder ->
            assertEquals(IExplorerActionPlugin.DESCRIPTOR, binder.interfaceDescriptor)
            val info = transactPluginInfo(binder, IExplorerActionPlugin.DESCRIPTOR)
            assertIdentity(info, expectedEngine = ExplorerActionPluginIds.ENGINE)
            val capabilities = requireNotNull(info.capabilities)
            assertEquals(setOf(PluginCapabilityKeys.REQUIRES_HOST_VERSION, ExplorerActionCapabilityKeys.PROTOCOL_VERSION), capabilities.keySet())
            assertEquals(BatchRenamePlugin.REQUIRED_HOST_VERSION, capabilities.getLong(PluginCapabilityKeys.REQUIRES_HOST_VERSION))
            assertEquals(BatchRenamePlugin.EXPLORER_PROTOCOL_VERSION, capabilities.getInt(ExplorerActionCapabilityKeys.PROTOCOL_VERSION))

            // getActionCatalog() is the second transaction of IExplorerActionPlugin.
            val catalog = transactBundle(binder, IExplorerActionPlugin.DESCRIPTOR, IBinder.FIRST_CALL_TRANSACTION + 1) {}
            assertEquals(BatchRenamePlugin.EXPLORER_PROTOCOL_VERSION, catalog.getInt(ExplorerActionCatalogKeys.PROTOCOL_VERSION))
            @Suppress("DEPRECATION")
            val actions = catalog.getParcelableArrayList<Bundle>(ExplorerActionCatalogKeys.ACTIONS)
            assertNotNull("the catalog carries an explicit action list", actions)
            assertTrue("no action until roadmap P4.1", actions!!.isEmpty())

            // openArchiveV11(source, request) is the fourth transaction; this plugin never opens archives.
            val archive = transactBundle(binder, IExplorerActionPlugin.DESCRIPTOR, IBinder.FIRST_CALL_TRANSACTION + 3) { data ->
                data.writeInt(0) // null ParcelFileDescriptor
                data.writeInt(0) // null request Bundle
            }
            assertEquals(ExplorerArchiveOpenValues.ERROR_UNRECOVERABLE, archive.getInt(ExplorerArchiveOpenKeys.ERROR_CODE))
        }
    }

    @Test
    fun repeatedBindingsKeepAnsweringTheSameIdentity() {
        val serviceInfo = discoverSingleService(PluginActions.INFO, BatchRenamePlugin.SERVICE_CATEGORY, BatchRenamePluginInfoService::class.java.name)
        val versions = (1..3).map {
            var versionCode = -1L
            withBoundService(serviceInfo) { binder -> versionCode = transactPluginInfo(binder, IPluginInfoProvider.DESCRIPTOR).versionCode }
            versionCode
        }
        assertEquals(1, versions.toSet().size)
        assertTrue(versions.first() > 0)
    }

    @Test
    fun theApkIsOneUniversalPackageWithOnlyTheComposeGraphicsPathHelper() {
        // Roadmap D28: no first-party native code and no ABI split; Compose ui-graphics brings graphics-path, stored
        // uncompressed for every ABI, and it must load on the running device (16 KB page devices reject 4 KB alignment).
        val applicationInfo = context.applicationInfo
        val apks = listOfNotNull(applicationInfo.sourceDir) + applicationInfo.splitSourceDirs.orEmpty()
        assertEquals("the plugin is a single universal APK", 1, apks.size)
        val natives = ZipFile(apks.single()).use { zip -> zip.entries().asSequence().filter { it.name.startsWith("lib/") }.map { it.name to it.method }.toList() }
        val expected = listOf("arm64-v8a", "armeabi-v7a", "x86_64", "x86").map { "lib/$it/libandroidx.graphics.path.so" }
        assertEquals(expected.toSet(), natives.map { it.first }.toSet())
        assertTrue("native libraries are stored uncompressed", natives.all { it.second == ZipEntry.STORED })
        System.loadLibrary("androidx.graphics.path")
    }

    private fun assertIdentity(info: PluginInfo, expectedEngine: String) {
        val packageInfo = context.packageManager.getPackageInfo(packageName, 0)
        val expectedVersionCode = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            packageInfo.longVersionCode
        } else {
            @Suppress("DEPRECATION")
            packageInfo.versionCode.toLong()
        }
        assertEquals("Batch Rename", info.name)
        assertEquals(context.getString(R.string.app_name), info.name)
        assertEquals(context.getString(R.string.plugin_description), info.description)
        assertEquals(BatchRenamePlugin.INSTRUCTION_REFERENCE, info.instruction)
        assertTrue("the instruction resource exists", context.resources.getIdentifier("plugin_instruction", "raw", packageName) != 0)
        assertEquals("SuperMonster003", info.author)
        assertEquals(BatchRenamePlugin.PLUGIN_ID, info.id)
        assertEquals(context.getString(R.string.plugin_id), info.id)
        assertEquals(expectedEngine, info.engine)
        assertEquals(BatchRenamePlugin.VARIANT, info.variant)
        assertEquals(packageInfo.versionName, info.versionName)
        assertEquals(expectedVersionCode, info.versionCode)
        assertEquals(context.getString(R.string.plugin_version_date), info.versionDate)
        // Roadmap D28: an explicit empty list means "any ABI".
        assertArrayEquals(emptyArray<String>(), requireNotNull(info.supportedAbis) { "supportedAbis must not be null" })
    }

    /** `getInfo()` is the first transaction of both IPluginInfoProvider and IExplorerActionPlugin. */
    private fun transactPluginInfo(binder: IBinder, descriptor: String): PluginInfo {
        val data = Parcel.obtain()
        val reply = Parcel.obtain()
        try {
            data.writeInterfaceToken(descriptor)
            assertTrue("getInfo() transaction must be handled", binder.transact(IBinder.FIRST_CALL_TRANSACTION, data, reply, 0))
            reply.readException()
            assertTrue("getInfo() must not return null", reply.readInt() != 0)
            return pluginInfoCreator.createFromParcel(reply)
        } finally {
            data.recycle()
            reply.recycle()
        }
    }

    private fun transactBundle(binder: IBinder, descriptor: String, code: Int, writeArguments: (Parcel) -> Unit): Bundle {
        val data = Parcel.obtain()
        val reply = Parcel.obtain()
        try {
            data.writeInterfaceToken(descriptor)
            writeArguments(data)
            assertTrue("transaction $code must be handled", binder.transact(code, data, reply, 0))
            reply.readException()
            assertTrue("transaction $code must not return null", reply.readInt() != 0)
            return Bundle.CREATOR.createFromParcel(reply).apply { classLoader = PluginInfo::class.java.classLoader }
        } finally {
            data.recycle()
            reply.recycle()
        }
    }

    private fun queryActivities(intent: Intent): List<String> {
        @Suppress("DEPRECATION")
        return context.packageManager.queryIntentActivities(intent, 0).map { it.activityInfo.name }
    }

    private fun discoverSingleService(action: String, category: String, expectedClassName: String): ServiceInfo {
        val intent = Intent(action).addCategory(category).setPackage(packageName)
        @Suppress("DEPRECATION")
        val matches = context.packageManager.queryIntentServices(intent, PackageManager.GET_META_DATA)
        assertEquals("$action / $category must resolve exactly one service: ${matches.map { it.serviceInfo.name }}", 1, matches.size)
        val serviceInfo = matches.single().serviceInfo
        assertEquals(packageName, serviceInfo.packageName)
        assertEquals(expectedClassName, serviceInfo.name)
        assertTrue("$expectedClassName must be exported", serviceInfo.exported)
        assertTrue("$expectedClassName must be enabled", serviceInfo.enabled)
        assertEquals(PLUGIN_PERMISSION, serviceInfo.permission)
        assertEquals("services run in the plugin's default process", packageName, serviceInfo.processName)
        return serviceInfo
    }

    private fun withBoundService(serviceInfo: ServiceInfo, block: (IBinder) -> Unit) {
        val binderReference = AtomicReference<IBinder>()
        val connected = CountDownLatch(1)
        val connection = object : ServiceConnection {
            override fun onServiceConnected(name: ComponentName, service: IBinder) {
                binderReference.set(service)
                connected.countDown()
            }

            override fun onServiceDisconnected(name: ComponentName) = Unit

            override fun onNullBinding(name: ComponentName) {
                connected.countDown()
            }
        }
        val explicit = Intent().setComponent(ComponentName(serviceInfo.packageName, serviceInfo.name))
        assertTrue("bindService returned false", context.bindService(explicit, connection, Context.BIND_AUTO_CREATE))
        try {
            assertTrue("timed out waiting for ${serviceInfo.name}", connected.await(10, TimeUnit.SECONDS))
            block(requireNotNull(binderReference.get()) { "${serviceInfo.name} returned a null binding" })
        } finally {
            context.unbindService(connection)
        }
    }

    private companion object {
        const val PLUGIN_PERMISSION = "org.autojs.permission.PLUGIN"

        /** The Parcelize-generated `CREATOR` is a JVM static field that Kotlin sources cannot name directly. */
        @Suppress("UNCHECKED_CAST")
        val pluginInfoCreator: Parcelable.Creator<PluginInfo> =
            PluginInfo::class.java.getField("CREATOR").get(null) as Parcelable.Creator<PluginInfo>
    }

}
