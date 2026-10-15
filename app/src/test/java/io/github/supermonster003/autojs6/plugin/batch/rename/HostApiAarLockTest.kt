package io.github.supermonster003.autojs6.plugin.batch.rename

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.nio.file.Files
import java.nio.file.Path
import java.security.MessageDigest
import java.util.zip.ZipFile

/**
 * The host API lock, the staged AARs and the human-readable records describe the same bytes (roadmap 5.4, P0.1).
 * Gradle enforces the lock during configuration; this test keeps `libs/README.md` and `THIRD_PARTY_NOTICES.md` honest
 * and pins the artifact set until roadmap P1.1 adds `batch-rename-api`.
 * zh-CN: 宿主 API 锁文件, 暂存的 AAR 与人读记录描述同一份字节 (路线图 5.4, P0.1). Gradle 在配置阶段强制锁文件; 本测试
 * 保证 `libs/README.md` 与 `THIRD_PARTY_NOTICES.md` 如实记录, 并在路线图 P1.1 加入 `batch-rename-api` 之前固定产物集合.
 */
class HostApiAarLockTest {

    private val lock: Map<String, LockEntry> by lazy { readLock() }

    @Test
    fun `the lock lists the staged host AARs with matching digests`() {
        assertEquals(setOf("common-plugin-api", "explorer-action-api"), lock.keys)
        lock.forEach { (id, entry) ->
            assertEquals("$id is staged under its module name", "$id.aar", entry.file)
            val file = RepositoryFiles.path("libs/${entry.file}")
            assertTrue("$id: missing libs/${entry.file}", Files.isRegularFile(file))
            assertEquals("$id: digest drifted from the lock", entry.sha256, sha256(file))
        }
        Files.list(RepositoryFiles.path("libs")).use { files ->
            val staged = files.map { it.fileName.toString() }.filter { it.endsWith(".aar") || it.endsWith(".jar") }.sorted().toList()
            assertEquals("libs/ holds exactly the locked artifacts", lock.values.map { it.file }.sorted(), staged)
        }
    }

    @Test
    fun `the staged AARs are release bytecode without native code`() {
        lock.forEach { (id, entry) ->
            assertFalse("$id: debug AARs are not accepted", entry.file.contains("debug"))
            val names = ZipFile(RepositoryFiles.path("libs/${entry.file}").toFile()).use { zip -> zip.entries().asSequence().map { it.name }.toList() }
            assertTrue("$id must contain classes.jar", "classes.jar" in names)
            assertTrue("$id must not ship native libraries", names.none { it.startsWith("jni/") })
        }
    }

    @Test
    fun `the human-readable records repeat every locked artifact`() {
        val readme = RepositoryFiles.text("libs/README.md")
        val notices = RepositoryFiles.text("THIRD_PARTY_NOTICES.md")
        lock.forEach { (id, entry) ->
            assertTrue("libs/README.md must describe ${entry.file}", readme.contains("`${entry.file}`"))
            assertTrue("libs/README.md must repeat the digest of $id", readme.contains(entry.sha256))
            assertTrue("THIRD_PARTY_NOTICES.md must list ${entry.file}", notices.contains("`libs/${entry.file}`"))
        }
    }

    @Test
    fun `the build script reads the lock instead of listing files`() {
        val build = RepositoryFiles.text("app/build.gradle.kts")
        assertTrue(build.contains("rootProject.file(\"locks/host-api-aars.lock\")"))
        assertTrue(build.contains("implementation(files(hostApiAars))"))
        assertFalse("no sibling repository paths", build.contains("../AutoJs6") || build.contains("flatDir"))
        assertFalse("no mavenLocal()", RepositoryFiles.text("settings.gradle.kts").contains("mavenLocal(") || build.contains("mavenLocal("))
    }

    private data class LockEntry(val file: String, val sha256: String)

    private fun readLock(): Map<String, LockEntry> {
        val lines = Files.readAllLines(RepositoryFiles.path("locks/host-api-aars.lock")).map { it.trim() }.filter { it.isNotEmpty() && !it.startsWith("#") }
        assertEquals("format=1", lines.first())
        val values = lines.drop(1).associate { it.substringBefore('=') to it.substringAfter('=') }
        return values.keys.map { it.substringBeforeLast('.') }.toSet().associateWith { id ->
            val sha256 = values.getValue("$id.sha256")
            assertTrue("$id: lock digest must be lowercase hex", Regex("[0-9a-f]{64}").matches(sha256))
            LockEntry(values.getValue("$id.file"), sha256)
        }
    }

    private fun sha256(path: Path): String =
        MessageDigest.getInstance("SHA-256").digest(Files.readAllBytes(path)).joinToString("") { "%02x".format(it) }

}
