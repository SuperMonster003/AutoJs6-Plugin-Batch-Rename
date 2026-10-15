package io.github.supermonster003.autojs6.plugin.batch.rename.explorer

import org.autojs.plugin.batchrename.api.BatchRenameLimits

/**
 * What the file manager asked the plugin to rename (roadmap D23): the selected items themselves, or the items inside
 * one selected folder.
 * zh-CN: 文件管理器请求插件改名的对象 (路线图 D23): 选中的条目本身, 或选中文件夹之中的条目.
 */
enum class RenameScope {
    /** The selection toolbar and the rename dialog: the request targets are the items. zh-CN: 请求目标本身. */
    TARGETS,

    /** The folder menu: the items inside the single directory target. zh-CN: 单个目录目标之中的条目. */
    INSIDE_DIRECTORY,
}

/** A request target as the host launched it. zh-CN: 宿主启动时给出的请求目标. */
data class RequestTarget(
    val id: String,
    val name: String,
    val directory: Boolean,
    val size: Long,
    val lastModified: Long,
)

/** One entry `listChildren` returned. zh-CN: `listChildren` 返回的一个条目. */
data class ListedChild(
    val relativePath: String,
    val name: String,
    val directory: Boolean,
    val symbolicLink: Boolean,
    val size: Long,
    val lastModified: Long,
)

/** Names and file-system traits of the directory the request targets share. zh-CN: 请求目标共同父目录的名称与文件系统特征. */
data class ParentListing(val names: List<String>, val caseInsensitive: Boolean, val restrictedCharacters: Boolean)

/**
 * The host session as the enumeration needs it; the Android side pages the Binder calls.
 * zh-CN: 枚举所需的宿主会话; Android 侧负责对 Binder 调用分页.
 */
interface RenameSource {
    fun listParent(): ParentListing

    /** Every child of [relativePath] below the directory target [targetId]. zh-CN: 目录目标下 [relativePath] 的全部子项. */
    fun listChildren(targetId: String, relativePath: String): List<ListedChild>
}

/**
 * One entry the screen can rename, addressed the way `renameEntries` addresses it.
 * zh-CN: 界面可以改名的一个条目, 寻址方式与 `renameEntries` 相同.
 */
data class RenameCandidate(
    val targetId: String,
    /** `""` for the target itself. zh-CN: 目标本身为 `""`. */
    val relativePath: String,
    val name: String,
    val directory: Boolean,
    val depth: Int,
    val parentId: String,
    val lastModified: Long,
    val size: Long,
) {
    /** Stable across re-plans; changes only when the entry moves. zh-CN: 重新计算方案时保持不变; 仅在条目移动时改变. */
    val key: String get() = "$targetId/$relativePath"
}

data class CandidateParent(val id: String, val existingNames: List<String>, val caseInsensitive: Boolean, val restrictedCharacters: Boolean)

/**
 * The candidates of one screen, depth first in the file manager's order. [truncated] is true when the contract limit
 * of [BatchRenameLimits.MAX_ITEMS_PER_PLAN] items or [MAX_DEPTH] folder levels cut the walk short.
 * zh-CN: 一个界面的候选项, 按文件管理器的顺序深度优先排列. 契约上限 (条目数或 [MAX_DEPTH] 层) 截断遍历时 [truncated] 为真.
 */
data class CandidateSet(val candidates: List<RenameCandidate>, val parents: List<CandidateParent>, val truncated: Boolean)

/**
 * Walks the request through the host session (roadmap D30, the file manager path). Items named by the request are at
 * depth 0; with [includeSubfolders] everything below a directory follows at depth 1 and more. Symbolic links never
 * become candidates and are never entered, but keep their names in the existing names of their directory.
 * zh-CN: 经宿主会话遍历请求 (路线图 D30, 文件管理器路径). 请求指定的条目深度为 0; [includeSubfolders] 时目录之下的条目依次为深度
 * 1 及以上. 符号链接不会成为候选项也不会被进入, 但其名称保留在所在目录的既有名称中.
 */
class RenameCandidateEnumerator(
    private val source: RenameSource,
    private val maxItems: Int = BatchRenameLimits.MAX_ITEMS_PER_PLAN,
    private val maxDepth: Int = MAX_DEPTH,
) {

    fun enumerate(scope: RenameScope, targets: List<RequestTarget>, includeSubfolders: Boolean): CandidateSet {
        val walk = Walk()
        when (scope) {
            RenameScope.TARGETS -> {
                val parent = source.listParent()
                walk.parents += CandidateParent(PARENT_ID, parent.names, parent.caseInsensitive, parent.restrictedCharacters)
                targets.forEach { target ->
                    val added = walk.add(
                        RenameCandidate(target.id, "", target.name, target.directory, 0, PARENT_ID, target.lastModified, if (target.directory) -1L else target.size),
                    )
                    if (added && target.directory && includeSubfolders) walk.directory(target.id, "", 1, parent)
                }
            }
            RenameScope.INSIDE_DIRECTORY -> {
                val target = targets.singleOrNull { it.directory }
                    ?: throw IllegalArgumentException("the folder menu names exactly one folder")
                val traits = source.listParent()
                walk.directory(target.id, "", 0, traits, recurse = includeSubfolders)
            }
        }
        return CandidateSet(walk.candidates, walk.parents, walk.truncated)
    }

    private inner class Walk {
        val candidates = ArrayList<RenameCandidate>()
        val parents = ArrayList<CandidateParent>()
        var truncated = false

        fun add(candidate: RenameCandidate): Boolean {
            if (candidates.size >= maxItems) {
                truncated = true
                return false
            }
            candidates += candidate
            return true
        }

        /** Lists [relativePath] below [targetId]; its children are at [depth]. */
        fun directory(targetId: String, relativePath: String, depth: Int, traits: ParentListing, recurse: Boolean = true) {
            if (truncated) return
            if (depth > maxDepth) {
                truncated = true
                return
            }
            val children = source.listChildren(targetId, relativePath)
            val parentId = "$targetId/$relativePath"
            parents += CandidateParent(parentId, children.map { it.name }, traits.caseInsensitive, traits.restrictedCharacters)
            children.forEach { child ->
                if (child.symbolicLink || truncated) return@forEach
                val added = add(
                    RenameCandidate(targetId, child.relativePath, child.name, child.directory, depth, parentId, child.lastModified, if (child.directory) -1L else child.size),
                )
                if (added && child.directory && recurse) directory(targetId, child.relativePath, depth + 1, traits)
            }
        }
    }

    companion object {
        /** Folder levels below a request target (roadmap D30). zh-CN: 请求目标之下的层数上限. */
        const val MAX_DEPTH = 32

        const val PARENT_ID = "parent"
    }
}

/** One `renameEntries` entry. zh-CN: `renameEntries` 的一个条目. */
data class RenameEntry(val targetId: String, val relativePath: String, val newName: String, val overwrite: Boolean, val depth: Int, val parentId: String)

/**
 * Splits entries into `renameEntries` requests small enough for one Binder transaction (Explorer Action v25 asks
 * clients to measure). A directory's entries always stay together (swaps and cycles need each other) and deeper
 * directories go first, so a folder is renamed only after the requests for everything below it; undo runs the
 * batches in reverse. A single directory larger than [limit] is an error.
 * zh-CN: 把条目拆成单次 Binder 事务能容纳的 `renameEntries` 请求 (Explorer Action v25 要求客户端自行度量). 同一目录的条目始终
 * 在一起 (交换与循环相互依赖), 深层目录先发, 因此文件夹只在其下全部条目的请求之后改名; 撤销按相反顺序进行. 单个目录超过 [limit] 时报错.
 */
object RenameRequestSplitter {

    fun split(entries: List<RenameEntry>, limit: Int, sizeOf: (List<RenameEntry>) -> Int): List<List<RenameEntry>> {
        if (entries.isEmpty()) return emptyList()
        if (sizeOf(entries) <= limit) return listOf(entries)
        val groups = entries.groupBy { it.parentId }.values.sortedByDescending { group -> group.first().depth }
        val requests = ArrayList<List<RenameEntry>>()
        var current = ArrayList<RenameEntry>()
        groups.forEach { group ->
            if (sizeOf(group) > limit) throw RequestTooLargeException(group.size)
            if (current.isNotEmpty() && sizeOf(current + group) > limit) {
                requests += current
                current = ArrayList()
            }
            current.addAll(group)
        }
        if (current.isNotEmpty()) requests += current
        return requests
    }

    class RequestTooLargeException(val entries: Int) : RuntimeException("$entries entries of one folder do not fit into one request")
}
