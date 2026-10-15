package io.github.supermonster003.autojs6.plugin.batch.rename.engine

/**
 * A name split for the `target` option (roadmap P2.2): the extension is the text after the last dot of a file name.
 * Folders, names without a dot, names whose only dot leads (`.bashrc`) and names ending with a dot have no extension;
 * `archive.tar.gz` has the stem `archive.tar` and the extension `gz`.
 * zh-CN: 按 `target` 选项拆分的名称 (路线图 P2.2): 扩展名为文件名最后一个点号之后的文本. 文件夹, 不含点号的名称, 仅以点号开头的
 * 名称 (`.bashrc`) 与以点号结尾的名称都没有扩展名; `archive.tar.gz` 的主名为 `archive.tar`, 扩展名为 `gz`.
 */
internal data class NameParts(val stem: String, val extension: String?) {

    /** Joins the parts; an empty or missing extension leaves no dot. zh-CN: 拼接各部分; 扩展名为空或缺失时不留点号. */
    fun join(): String = if (extension.isNullOrEmpty()) stem else "$stem.$extension"

    companion object {
        fun of(name: String, kind: ItemKind): NameParts {
            if (kind == ItemKind.DIRECTORY) return NameParts(name, null)
            val dot = name.lastIndexOf('.')
            return if (dot <= 0 || dot == name.length - 1) NameParts(name, null) else NameParts(name.substring(0, dot), name.substring(dot + 1))
        }
    }
}
