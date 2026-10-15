import com.android.build.api.variant.FilterConfiguration
import org.gradle.api.file.DuplicatesStrategy
import org.gradle.api.file.RelativePath
import org.gradle.api.provider.Property
import java.security.MessageDigest
import java.util.zip.ZipEntry
import java.util.zip.ZipFile

plugins {
    id("io.github.supermonster003.autojs6-native-alignment")
    id("org.autojs.build.utils")
    id("org.autojs.build.versions")
    id("org.autojs.build.signs")
    id("org.autojs.build.jvm-convention")
    id("com.android.application")
    id("org.jetbrains.kotlin.plugin.compose")
}

val globalApplicationId = "io.github.supermonster003.autojs6.plugin.batch.rename"

// Minimum host build (roadmap D26): 5431 is the first host build with the batch-rename contract, Explorer Action
// protocol v25 and the Plugin Center registration (host commit dcc5bd98d). P5.4 replaces it with the final value;
// keep it in sync with BatchRenamePlugin.REQUIRED_HOST_VERSION and .readme/common.json.
// zh-CN: 最低宿主构建号 (路线图 D26): 5431 是首个包含 batch-rename 契约, Explorer Action 协议 v25 与插件中心注册的宿主构建号
// (宿主提交 dcc5bd98d). P5.4 回填最终值; 与 BatchRenamePlugin.REQUIRED_HOST_VERSION 及 .readme/common.json 保持一致.
val requiredHostVersion = 5431

val buildTypeDebug = "debug"
val buildTypeRelease = "release"

android {
    namespace = globalApplicationId
    compileSdk = versions.sdkVersionCompile

    defaultConfig {
        applicationId = globalApplicationId
        minSdk = versions.sdkVersionMin
        targetSdk = versions.sdkVersionTarget
        versionCode = versions.appVersionCode
        versionName = versions.appVersionName

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        resValue("string", "app_name", "Batch Rename")
        resValue("string", "plugin_author", "SuperMonster003")
        resValue("string", "plugin_id", "batch-rename")
        resValue("string", "plugin_engine", "batch-rename")
        resValue("string", "plugin_variant", "default")
        resValue("string", "plugin_requires_host_version", "$requiredHostVersion")
        resValue("string", "plugin_version_date", utils.getDateString("MMM d, yyyy", "GMT+08:00"))
    }

    // Rhino initializes NativeDate, which uses java.time, in every scope (roadmap P2.1): API 24 / 25 need the desugared
    // JDK library, as in the host. zh-CN: Rhino 在每个作用域中都会初始化使用 java.time 的 NativeDate (路线图 P2.1):
    // API 24 / 25 需要脱糖的 JDK 类库, 与宿主相同.
    compileOptions {
        isCoreLibraryDesugaringEnabled = true
    }

    testOptions { unitTests.isIncludeAndroidResources = true }

    lint {
        abortOnError = true
    }

    signingConfigs {
        if (signs.isValid) {
            create(buildTypeRelease) {
                storeFile = signs.properties["storeFile"]?.let { file(it as String) }
                keyPassword = signs.properties["keyPassword"] as String
                keyAlias = signs.properties["keyAlias"] as String
                storePassword = signs.properties["storePassword"] as String
            }
        }
    }

    buildTypes {
        val proguardFiles = arrayOf<Any>(
            getDefaultProguardFile("proguard-android-optimize.txt"),
            "proguard-rules.pro",
        )
        val niceSigningConfig = takeIf { signs.isValid }?.let {
            signingConfigs.getByName(buildTypeRelease)
        }
        debug {
            isMinifyEnabled = false
            proguardFiles(*proguardFiles)
            niceSigningConfig?.let { signingConfig = it }
        }
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(*proguardFiles)
            niceSigningConfig?.let { signingConfig = it }
        }
    }

    buildFeatures {
        aidl = true
        compose = true
        resValues = true
    }

    packaging {
        resources.pickFirsts.addAll(
            listOf(
                "META-INF/DEPENDENCIES",
                "META-INF/LICENSE",
                "META-INF/LICENSE.*",
                "META-INF/NOTICE",
                "META-INF/NOTICE.*",
                "META-INF/*.kotlin_module",
            ),
        )
    }

    // One universal APK (roadmap D28): the plugin has no native code of its own, and the only packaged
    // library (Compose's graphics-path, see below) is tiny, so ABI splits would ship near-identical APKs.
    // zh-CN: 单个通用 APK (路线图 D28): 插件没有自有原生代码, 唯一随包的原生库 (Compose 的 graphics-path, 见下文) 体积很小,
    // ABI 拆分只会产出几乎相同的 APK.
    bundle {
        language.enableSplit = false
        density.enableSplit = false
        abi.enableSplit = false
    }
}

androidComponents {
    onVariants { variant ->
        variant.outputs.forEach { output ->
            val architecture = output.filters.find {
                it.filterType == FilterConfiguration.FilterType.ABI
            }?.identifier
            val outputFileNameProperty = output.javaClass.methods.firstOrNull {
                it.name == "getOutputFileName" && it.parameterTypes.isEmpty()
            }?.invoke(output) as? Property<*>

            @Suppress("UNCHECKED_CAST")
            (outputFileNameProperty as? Property<String>)?.set(
                output.versionName.map { versionName ->
                    val version = versionName.replace("\\s".toRegex(), "-")
                    val abiSuffix = architecture?.let { "-$it" }.orEmpty()
                    "${rootProject.name}-v$version$abiSuffix.${utils.FILE_EXTENSION_APK}".lowercase()
                },
            )
        }
    }
}

// Host contract AARs (roadmap 5.4): staged release artifacts locked by SHA-256 in locks/host-api-aars.lock.
// The build never reads a sibling repository; a missing, extra or modified artifact fails configuration.
// zh-CN: 宿主契约 AAR (路线图 5.4): 以 SHA-256 锁定在 locks/host-api-aars.lock 中的 release 产物. 构建从不读取兄弟仓库;
// 产物缺失, 多余或被修改时配置阶段即失败.
val hostApiAars: List<File> = run {
    val lockFile = rootProject.file("locks/host-api-aars.lock")
    check(lockFile.isFile) { "Missing locks/host-api-aars.lock" }
    val entries = lockFile.readLines()
        .map { it.trim() }
        .filter { it.isNotEmpty() && !it.startsWith("#") }
        .associate { line -> line.substringBefore('=') to line.substringAfter('=') }
    check(entries["format"] == "1") { "Unsupported host API AAR lock format" }
    val names = entries.keys.filter { it.endsWith(".file") }.map { it.removeSuffix(".file") }.sorted()
    val staged = rootProject.file("libs").listFiles { file -> file.extension == "aar" }.orEmpty().map { it.name }.sorted()
    check(staged == names.map { entries.getValue("$it.file") }.sorted()) {
        "Staged host API AARs $staged do not match the lock entries $names"
    }
    names.map { name ->
        val file = rootProject.file("libs/${entries.getValue("$name.file")}")
        val expected = entries["$name.sha256"]
        check(expected != null && expected.matches(Regex("[0-9a-f]{64}"))) { "Invalid SHA-256 for $name in the lock" }
        val actual = MessageDigest.getInstance("SHA-256").digest(file.readBytes()).joinToString("") { "%02x".format(it) }
        check(actual == expected) { "SHA-256 mismatch for ${file.name}: expected $expected, actual $actual" }
        file
    }
}

// The host's slim Rhino jar (roadmap P2.1 / D15), locked in locks/rhino-jar.lock: the regular expression engine runs
// RegExp on the same NativeRegExp as AutoJs6 scripts. Only engine/regex may import org.mozilla.javascript
// (RhinoImportGuardTest); R8 keeps the closure that package reaches.
// zh-CN: 宿主的 slim Rhino jar (路线图 P2.1 / D15), 锁定在 locks/rhino-jar.lock 中: 正则引擎与 AutoJs6 脚本使用同一个
// NativeRegExp 执行 RegExp. 只有 engine/regex 可以导入 org.mozilla.javascript (RhinoImportGuardTest); R8 只保留该包用到的部分.
val rhinoJar: File = run {
    val lockFile = rootProject.file("locks/rhino-jar.lock")
    check(lockFile.isFile) { "Missing locks/rhino-jar.lock" }
    val entries = lockFile.readLines()
        .map { it.trim() }
        .filter { it.isNotEmpty() && !it.startsWith("#") }
        .associate { line -> line.substringBefore('=') to line.substringAfter('=') }
    check(entries.keys == setOf("format", "rhino.file", "rhino.sha256", "rhino.source")) { "Unexpected keys in locks/rhino-jar.lock" }
    check(entries["format"] == "1") { "Unsupported Rhino jar lock format" }
    val fileName = entries.getValue("rhino.file")
    check(fileName == File(fileName).name && fileName.endsWith("-slim.jar")) { "Invalid rhino.file in locks/rhino-jar.lock" }
    check(entries.getValue("rhino.source").matches(Regex("[0-9a-f]{40}"))) { "rhino.source must be the full host commit" }
    val expected = entries.getValue("rhino.sha256")
    check(expected.matches(Regex("[0-9a-f]{64}"))) { "Invalid rhino.sha256 in locks/rhino-jar.lock" }
    val file = rootProject.file("libs/$fileName")
    check(file.isFile) { "Missing libs/$fileName" }
    val actual = MessageDigest.getInstance("SHA-256").digest(file.readBytes()).joinToString("") { "%02x".format(it) }
    check(actual == expected) { "SHA-256 mismatch for $fileName: expected $expected, actual $actual" }
    file
}

dependencies {
    coreLibraryDesugaring(libs.desugar)

    implementation(files(hostApiAars))
    implementation(files(rhinoJar))

    implementation(libs.core.ktx)
    implementation(libs.kotlinx.coroutines.android)

    // Jetpack Compose (roadmap D24): the BOM is the single version source; activity-compose follows it.
    implementation(platform(libs.compose.bom))
    implementation(libs.compose.runtime)
    implementation(libs.compose.ui)
    implementation(libs.compose.foundation)
    implementation(libs.compose.material3)
    implementation(libs.compose.activity)
    implementation(libs.compose.ui.tooling.preview)
    debugImplementation(libs.compose.ui.tooling)
    debugImplementation(libs.compose.ui.test.manifest)

    testImplementation(libs.junit)
    androidTestImplementation(libs.test.ext.junit)
    androidTestImplementation(libs.test.runner)
    androidTestImplementation(libs.test.rules)
    androidTestImplementation(platform(libs.compose.bom))
    androidTestImplementation(libs.compose.ui.test.junit4)
}

tasks {
    withType(JavaCompile::class.java) {
        options.encoding = "UTF-8"
    }

    register<Copy>("appendDigestToReleasedFiles") {
        description = "Appends CRC32 digest to released APK files"
        dependsOn("assembleRelease")

        val ext = utils.FILE_EXTENSION_APK
        val src = layout.buildDirectory.dir("outputs/apk/$buildTypeRelease")
        val dst = file("$rootDir/${buildTypeRelease}s")

        from(src)
        into(dst)
        include("*.$ext")
        includeEmptyDirs = false
        duplicatesStrategy = DuplicatesStrategy.FAIL

        eachFile {
            val digest = utils.digestCRC32(file)
            relativePath = RelativePath(true, "${name.removeSuffix(".$ext")}-$digest.$ext")
        }

        doLast { println("Destination: $dst") }
    }
}

extra {
    versions.handleIfNeeded(project, "", listOf(buildTypeDebug, buildTypeRelease))
}

// Native code inventory (roadmap D28): the plugin has no native code of its own, but Compose ui-graphics depends on
// androidx.graphics:graphics-path, whose libandroidx.graphics.path.so is packaged for four ABIs. The APK stays one
// universal package without ABI splits; the shared gate keeps the 16 KB alignment checks for the 64-bit ABIs and
// verifySignedReleaseArtifacts below asserts that exactly these libraries are present and stored uncompressed.
// zh-CN: 原生代码清单 (路线图 D28): 插件没有自有原生代码, 但 Compose ui-graphics 依赖 androidx.graphics:graphics-path,
// 其 libandroidx.graphics.path.so 随包提供四个 ABI. APK 保持单个通用包而不做 ABI 拆分; 共享门禁对 64 位 ABI 保留 16 KB
// 对齐检查, 下方的 verifySignedReleaseArtifacts 断言 APK 中恰好是这些库且以不压缩方式存储.
val allowedNativeLibraries = listOf("arm64-v8a", "armeabi-v7a", "x86_64", "x86").map { "lib/$it/libandroidx.graphics.path.so" }
nativeAlignment { expectNoNativeLibraries.set(false) }

// Fail before collection when credentials, keystore or the actual APK set are incomplete.
// zh-CN: 签名凭据, 密钥库或实际 APK 集合不完整时, 在收集之前失败.
val verifySignedReleaseArtifacts = tasks.register("verifySignedReleaseArtifacts") {
    group = "verification"
    dependsOn("assembleRelease")
    doLast {
        val signing = android.buildTypes.getByName(buildTypeRelease).signingConfig
        check(
            signing != null && signing.storeFile?.isFile == true &&
                !signing.storePassword.isNullOrBlank() && !signing.keyAlias.isNullOrBlank() &&
                !signing.keyPassword.isNullOrBlank(),
        ) { "Release signing configuration is missing or incomplete" }
        val directory = layout.buildDirectory.dir("outputs/apk/$buildTypeRelease").get().asFile
        val apks = directory.listFiles { file -> file.isFile && file.extension == "apk" }.orEmpty()
        check(apks.map { it.name }.toSet() == setOf("${rootProject.name}-v${versions.appVersionName}.apk")) {
            "Unexpected release APK set: ${apks.map { it.name }.sorted()}"
        }
        val buildTools = androidComponents.sdkComponents.sdkDirectory.get().asFile
            .resolve("build-tools/${android.buildToolsVersion}")
        val signerJar = buildTools.resolve("lib/apksigner.jar")
        check(signerJar.isFile) { "Android SDK apksigner is unavailable" }
        val result = providers.exec {
            commandLine("java", "-jar", signerJar.absolutePath, "verify", apks.single().absolutePath)
            isIgnoreExitValue = true
        }.result.get()
        check(result.exitValue == 0) { "Release APK signature verification failed" }
        ZipFile(apks.single()).use { zip ->
            val natives = zip.entries().asSequence().filter { it.name.startsWith("lib/") }.toList()
            check(natives.map { it.name }.sorted() == allowedNativeLibraries.sorted()) {
                "Unexpected native library set: ${natives.map { it.name }.sorted()}"
            }
            check(natives.all { it.method == ZipEntry.STORED }) { "Native libraries must be stored uncompressed" }
        }
    }
}
tasks.named("appendDigestToReleasedFiles") { dependsOn(verifySignedReleaseArtifacts) }
