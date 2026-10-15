package io.github.supermonster003.autojs6.plugin.batch.rename

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.nio.file.Files
import java.security.MessageDigest
import java.util.zip.ZipFile
import kotlin.io.path.extension
import kotlin.io.path.invariantSeparatorsPathString
import kotlin.io.path.readText

/**
 * The slim Rhino jar of the regular expression engine (roadmap P2.1 / D15): the lock, the staged bytes and the
 * human-readable records agree, the jar is the slim build, and only `engine/regex` imports Rhino so that the rest of the
 * engine stays plain Kotlin.
 * zh-CN: 正则引擎使用的 slim Rhino jar (路线图 P2.1 / D15): 锁文件, 暂存字节与人读记录一致, jar 为 slim 构建, 且只有
 * `engine/regex` 导入 Rhino, 引擎其余部分保持纯 Kotlin.
 */
class RhinoJarLockTest {

    private val lock: Map<String, String> by lazy {
        RepositoryFiles.text("locks/rhino-jar.lock").lines().map { it.trim() }.filter { it.isNotEmpty() && !it.startsWith("#") }
            .associate { it.substringBefore('=') to it.substringAfter('=') }
    }

    @Test
    fun `the lock pins the staged slim jar`() {
        assertEquals(setOf("format", "rhino.file", "rhino.sha256", "rhino.source"), lock.keys)
        assertEquals("1", lock["format"])
        assertEquals("org-mozilla-rhino-2_0_0-SNAPSHOT-slim.jar", lock["rhino.file"])
        assertTrue("the source is a full host commit", Regex("[0-9a-f]{40}").matches(lock.getValue("rhino.source")))
        val bytes = Files.readAllBytes(RepositoryFiles.path("libs/${lock["rhino.file"]}"))
        assertEquals(lock["rhino.sha256"], MessageDigest.getInstance("SHA-256").digest(bytes).joinToString("") { "%02x".format(it) })
    }

    @Test
    fun `the jar is the slim interpreter build with the regular expression implementation`() {
        val names = ZipFile(RepositoryFiles.path("libs/${lock["rhino.file"]}").toFile()).use { zip -> zip.entries().asSequence().map { it.name }.toSet() }
        assertTrue("org/mozilla/javascript/regexp/NativeRegExp.class" in names)
        assertTrue("org/mozilla/javascript/Interpreter.class" in names)
        assertTrue("no optimizer", names.none { it.startsWith("org/mozilla/javascript/optimizer/") })
        assertTrue("no JLine", names.none { it.startsWith("org/jline/") })
    }

    @Test
    fun `the build and the records follow the lock`() {
        val build = RepositoryFiles.text("app/build.gradle.kts")
        assertTrue(build.contains("rootProject.file(\"locks/rhino-jar.lock\")"))
        assertTrue(build.contains("implementation(files(rhinoJar))"))
        val readme = RepositoryFiles.text("libs/README.md")
        assertTrue(readme.contains("`${lock["rhino.file"]}`"))
        assertTrue(readme.contains(lock.getValue("rhino.sha256")))
        assertTrue(readme.contains(lock.getValue("rhino.source")))
        assertTrue(RepositoryFiles.text("THIRD_PARTY_NOTICES.md").contains("`libs/${lock["rhino.file"]}`"))
        val rules = RepositoryFiles.text("app/proguard-rules.pro")
        assertTrue("Rhino looks its interpreter up by name", rules.contains("-keep class org.mozilla.javascript.Interpreter"))
        assertTrue("Rhino finds its platform bridge by name", rules.contains("-keep class org.mozilla.javascript.jdk18.VMBridge_jdk18"))
    }

    @Test
    fun `only the regular expression package imports Rhino`() {
        val sources = RepositoryFiles.path("app/src/main/java")
        val importers = Files.walk(sources).use { paths ->
            paths.filter { it.extension == "kt" || it.extension == "java" }
                .filter { path -> path.readText().lineSequence().any { it.startsWith("import org.mozilla.") } }
                .map { sources.relativize(it).invariantSeparatorsPathString }
                .toList()
        }
        assertEquals(listOf("io/github/supermonster003/autojs6/plugin/batch/rename/engine/regex/RhinoRegexEngine.kt"), importers)
    }
}
