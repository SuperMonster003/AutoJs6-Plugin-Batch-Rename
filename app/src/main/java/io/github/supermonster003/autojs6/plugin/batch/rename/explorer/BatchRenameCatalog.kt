package io.github.supermonster003.autojs6.plugin.batch.rename.explorer

import android.os.Bundle
import org.autojs.plugin.explorer.api.ExplorerActionCatalogKeys
import org.autojs.plugin.explorer.api.ExplorerActionValues

/**
 * One Explorer Action the plugin offers. Every action is read-only, opens [ACTIVITY_CLASS_NAME] and declares
 * `targetRename` (roadmap D22 / D23); only the target shape, the cardinality and the placement differ.
 * zh-CN: 插件提供的一个 Explorer Action. 每个动作都是只读的, 打开 [ACTIVITY_CLASS_NAME] 并声明 `targetRename` (路线图 D22 / D23);
 * 只有目标形态, 数量与位置不同.
 */
internal data class CatalogAction(
    val id: String,
    /** String resource of the menu label; [labelFallback] when the host cannot load it. zh-CN: 菜单标签的字符串资源. */
    val labelResourceName: String,
    val labelFallback: String,
    val targetKind: Int,
    val cardinality: Int,
    val placement: Int,
    val mimeTypes: List<String>,
    val scope: RenameScope,
)

/**
 * The three actions of roadmap D23: the selection toolbar (files and folders), the folder menu (the items inside one
 * folder) and the "More" menu of the host's rename dialog (one file or folder).
 * zh-CN: 路线图 D23 的三个动作: 多选工具栏 (文件与文件夹), 文件夹菜单 (一个文件夹之中的条目) 与宿主重命名对话框的 "更多" 菜单 (单个文件或文件夹).
 */
internal object BatchRenameCatalog {

    const val ACTIVITY_CLASS_NAME = "io.github.supermonster003.autojs6.plugin.batch.rename.BatchRenameActivity"

    const val ACTION_SELECTION = "batch-rename-selection"
    const val ACTION_DIRECTORY = "batch-rename-directory"
    const val ACTION_SINGLE = "batch-rename-single"

    val actions: List<CatalogAction> = listOf(
        CatalogAction(
            id = ACTION_SELECTION,
            labelResourceName = "explorer_action_rename_selection",
            labelFallback = "Batch rename",
            targetKind = ExplorerActionValues.TARGET_MIXED,
            cardinality = ExplorerActionValues.CARDINALITY_MULTIPLE,
            placement = ExplorerActionValues.PLACEMENT_SELECTION_TOOLBAR,
            mimeTypes = listOf("*/*"),
            scope = RenameScope.TARGETS,
        ),
        CatalogAction(
            id = ACTION_DIRECTORY,
            labelResourceName = "explorer_action_rename_inside",
            labelFallback = "Batch rename items inside",
            targetKind = ExplorerActionValues.TARGET_DIRECTORY,
            cardinality = ExplorerActionValues.CARDINALITY_SINGLE,
            placement = ExplorerActionValues.PLACEMENT_OVERFLOW,
            mimeTypes = emptyList(),
            scope = RenameScope.INSIDE_DIRECTORY,
        ),
        CatalogAction(
            id = ACTION_SINGLE,
            labelResourceName = "explorer_action_rename_single",
            labelFallback = "Batch rename...",
            targetKind = ExplorerActionValues.TARGET_MIXED,
            cardinality = ExplorerActionValues.CARDINALITY_SINGLE,
            placement = ExplorerActionValues.PLACEMENT_RENAME_DIALOG,
            mimeTypes = listOf("*/*"),
            scope = RenameScope.TARGETS,
        ),
    )

    fun action(id: String?): CatalogAction? = actions.firstOrNull { it.id == id }

    fun catalog(protocolVersion: Int): Bundle = Bundle().apply {
        putInt(ExplorerActionCatalogKeys.PROTOCOL_VERSION, protocolVersion)
        putParcelableArrayList(ExplorerActionCatalogKeys.ACTIONS, ArrayList(actions.map(::bundleOf)))
    }

    private fun bundleOf(action: CatalogAction) = Bundle().apply {
        putString(ExplorerActionCatalogKeys.ID, action.id)
        putString(ExplorerActionCatalogKeys.LABEL_RESOURCE_NAME, action.labelResourceName)
        putString(ExplorerActionCatalogKeys.LABEL_FALLBACK, action.labelFallback)
        putString(ExplorerActionCatalogKeys.ACTIVITY_CLASS_NAME, ACTIVITY_CLASS_NAME)
        putInt(ExplorerActionCatalogKeys.PRESENTATION, ExplorerActionValues.PRESENTATION_ACTIVITY)
        putInt(ExplorerActionCatalogKeys.TARGET_KIND, action.targetKind)
        putInt(ExplorerActionCatalogKeys.CARDINALITY, action.cardinality)
        putInt(ExplorerActionCatalogKeys.ACCESS_MODE, ExplorerActionValues.ACCESS_READ_ONLY)
        putInt(ExplorerActionCatalogKeys.PLACEMENT, action.placement)
        putStringArrayList(ExplorerActionCatalogKeys.MIME_TYPES, ArrayList(action.mimeTypes))
        putBoolean(ExplorerActionCatalogKeys.TARGET_RENAME, true)
    }
}
