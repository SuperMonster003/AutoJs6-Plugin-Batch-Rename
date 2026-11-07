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

    /**
     * Catalog-level key of the first-level menu that holds the plugin's actions in the host's file and folder menus
     * (AutoJs6 6.8.1+, host explorer-action protocol v27 `menuGroupLabel`, same form as an action label). The bundled
     * explorer-action-api AAR predates `ExplorerActionCatalogKeys.MENU_GROUP_LABEL_RESOURCE_NAME` /
     * `MENU_GROUP_LABEL_FALLBACK`, so the key names are written out. Hosts read them from catalogs of every protocol
     * version and older hosts ignore them, so the catalog keeps [BatchRenamePlugin.EXPLORER_PROTOCOL_VERSION].
     * zh-CN: 宿主文件与文件夹菜单中收纳插件动作的一级菜单的 catalog 级键 (AutoJs6 6.8.1 起, 宿主 explorer-action 协议 v27 的
     * `menuGroupLabel`, 与动作标签同形式). 内置的 explorer-action-api AAR 早于这两个键常量, 因此直接写出键名. 宿主从任何协议版本的
     * catalog 读取它们, 旧宿主忽略它们, 所以 catalog 的协议版本不变.
     */
    const val MENU_GROUP_LABEL_RESOURCE_NAME_KEY = "menuGroupLabelResourceName"

    /** Text the host shows when it cannot read the resource. zh-CN: 宿主无法读取资源时显示的文本的键. */
    const val MENU_GROUP_LABEL_FALLBACK_KEY = "menuGroupLabelFallback"

    /**
     * The menu is named after the plugin, as in the Plugin Center (explorer roadmap Q27): `app_name`, an untranslated
     * build resource. zh-CN: 菜单以插件名命名, 与插件中心一致 (explorer 路线图 Q27): 不翻译的构建资源 `app_name`.
     */
    const val MENU_GROUP_LABEL_RESOURCE_NAME = "app_name"
    const val MENU_GROUP_LABEL_FALLBACK = "Batch Rename"

    /** The catalog-level entries of the menu name. zh-CN: 菜单名的 catalog 级条目. */
    val menuGroupLabel: List<Pair<String, String>> = listOf(
        MENU_GROUP_LABEL_RESOURCE_NAME_KEY to MENU_GROUP_LABEL_RESOURCE_NAME,
        MENU_GROUP_LABEL_FALLBACK_KEY to MENU_GROUP_LABEL_FALLBACK,
    )

    fun action(id: String?): CatalogAction? = actions.firstOrNull { it.id == id }

    fun catalog(protocolVersion: Int): Bundle = Bundle().apply {
        putInt(ExplorerActionCatalogKeys.PROTOCOL_VERSION, protocolVersion)
        menuGroupLabel.forEach { (key, value) -> putString(key, value) }
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
