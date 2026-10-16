package io.github.supermonster003.autojs6.plugin.batch.rename.ui

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import io.github.supermonster003.autojs6.plugin.batch.rename.R
import io.github.supermonster003.autojs6.plugin.batch.rename.presets.PresetConflict
import io.github.supermonster003.autojs6.plugin.batch.rename.presets.PresetImport
import io.github.supermonster003.autojs6.plugin.batch.rename.presets.PresetNames
import io.github.supermonster003.autojs6.plugin.batch.rename.presets.RejectionKind
import org.autojs.plugin.batchrename.api.BatchRenameLimits

/**
 * The "Presets" button of the top bar (roadmap D36 / D46): its menu loads a preset with one tap and leads to saving the
 * current options and to the management sheet (rename, delete, import and export through the system file picker or
 * the clipboard, roadmap D45). The file pickers are registered here, outside the sheet, so their results arrive after
 * the sheet has closed.
 * zh-CN: 顶栏的 "预设" 按钮 (路线图 D36 / D46): 菜单中点按即可加载预设, 并可保存当前选项或打开管理面板 (重命名, 删除, 经系统文件
 * 选择器或剪贴板导入导出, 路线图 D45). 文件选择器在面板之外注册, 因此面板关闭后仍能收到结果.
 */
@Composable
internal fun PresetsButton(state: ScreenState, model: BatchRenameViewModel) {
    val context = LocalContext.current
    var menu by remember { mutableStateOf(false) }
    var saving by remember { mutableStateOf(false) }
    var managing by remember { mutableStateOf(false) }
    var exportNames by remember { mutableStateOf(emptyList<String>()) }
    val exportLauncher = rememberLauncherForActivityResult(ActivityResultContracts.CreateDocument(MIME_JSON)) { uri ->
        if (uri != null) model.writePresets(uri, exportNames)
    }
    val importLauncher = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
        if (uri != null) model.readPresets(uri)
    }
    Box {
        IconButton(
            onClick = {
                model.reloadPresets()
                menu = true
            },
            enabled = state.busy != Busy.RENAMING && state.busy != Busy.UNDOING,
            modifier = Modifier.testTag(TAG_PRESETS),
        ) {
            Icon(painterResource(R.drawable.ic_bookmarks), contentDescription = stringResource(R.string.action_presets))
        }
        DropdownMenu(expanded = menu, onDismissRequest = { menu = false }) {
            if (state.presetNames.isEmpty()) {
                DropdownMenuItem(text = { Text(stringResource(R.string.presets_empty)) }, onClick = {}, enabled = false)
            }
            state.presetNames.forEach { name ->
                DropdownMenuItem(
                    text = { Text(name, maxLines = 1, overflow = TextOverflow.Ellipsis) },
                    onClick = {
                        menu = false
                        model.loadPreset(name)
                    },
                    modifier = Modifier.testTag(TAG_PRESET_PREFIX + name),
                )
            }
            HorizontalDivider()
            DropdownMenuItem(
                text = { Text(stringResource(R.string.menu_save_preset)) },
                onClick = {
                    menu = false
                    saving = true
                },
                modifier = Modifier.testTag(TAG_SAVE_PRESET),
            )
            DropdownMenuItem(
                text = { Text(stringResource(R.string.menu_manage_presets)) },
                onClick = {
                    menu = false
                    managing = true
                },
                modifier = Modifier.testTag(TAG_MANAGE_PRESETS),
            )
        }
    }
    if (saving) SavePresetDialog(model) { saving = false }
    if (managing) {
        ManagePresetsSheet(
            state = state,
            model = model,
            onImportFile = {
                managing = false
                importLauncher.launch(arrayOf(MIME_JSON, "text/plain", "application/octet-stream"))
            },
            onImportClipboard = {
                managing = false
                model.prepareImport(clipboardText(context))
            },
            onExportFile = { names ->
                managing = false
                exportNames = names
                exportLauncher.launch(EXPORT_FILE_NAME)
            },
            onExportClipboard = { names ->
                managing = false
                copyToClipboard(context, model.exportPresets(names))
                // Android 13 and later confirm a copy themselves. zh-CN: Android 13 起系统会自行提示已复制.
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) model.presetsCopied()
            },
            onDismiss = { managing = false },
        )
    }
    state.pendingImport?.let { ImportConflictDialog(it, model) }
    state.importResult?.let { ImportResultDialog(it, model) }
}

@Composable
private fun SavePresetDialog(model: BatchRenameViewModel, onDismiss: () -> Unit) {
    var name by rememberSaveable { mutableStateOf("") }
    var problem by remember { mutableStateOf<RejectionKind?>(null) }
    var confirmReplace by remember { mutableStateOf(false) }
    fun save() {
        val result = model.savePreset(name)
        if (result == null) onDismiss() else problem = result
    }
    if (confirmReplace) {
        AlertDialog(
            onDismissRequest = { confirmReplace = false },
            text = { Text(stringResource(R.string.confirm_replace_preset, name.trim())) },
            confirmButton = {
                TextButton(
                    onClick = {
                        confirmReplace = false
                        save()
                    },
                    modifier = Modifier.testTag(TAG_DIALOG_CONFIRM),
                ) { Text(stringResource(R.string.action_replace_existing)) }
            },
            dismissButton = { TextButton(onClick = { confirmReplace = false }) { Text(stringResource(R.string.action_cancel)) } },
        )
        return
    }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.title_save_preset)) },
        text = {
            OutlinedTextField(
                value = name,
                onValueChange = {
                    name = it
                    problem = null
                },
                label = { Text(stringResource(R.string.field_preset_name)) },
                singleLine = true,
                isError = problem != null,
                supportingText = problem?.let { kind -> { Text(problemText(kind)) } },
                modifier = Modifier.fillMaxWidth().testTag(TAG_PRESET_NAME),
            )
        },
        confirmButton = {
            TextButton(
                onClick = {
                    val trimmed = name.trim()
                    val local = PresetNames.problem(trimmed)
                    when {
                        local != null -> problem = local
                        model.presetExists(trimmed) -> confirmReplace = true
                        else -> save()
                    }
                },
                modifier = Modifier.testTag(TAG_DIALOG_CONFIRM),
            ) { Text(stringResource(R.string.action_save)) }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text(stringResource(R.string.action_cancel)) } },
    )
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
private fun ManagePresetsSheet(
    state: ScreenState,
    model: BatchRenameViewModel,
    onImportFile: () -> Unit,
    onImportClipboard: () -> Unit,
    onExportFile: (List<String>) -> Unit,
    onExportClipboard: (List<String>) -> Unit,
    onDismiss: () -> Unit,
) {
    // Every preset starts selected for export (roadmap D45). zh-CN: 导出时默认全选 (路线图 D45).
    var unselected by remember { mutableStateOf(emptySet<String>()) }
    var renaming by remember { mutableStateOf<String?>(null) }
    var deleting by remember { mutableStateOf<String?>(null) }
    val selected = state.presetNames.filter { it !in unselected }
    ModalBottomSheet(onDismissRequest = onDismiss) {
        Column(Modifier.fillMaxWidth().padding(bottom = 16.dp)) {
            Text(
                stringResource(R.string.title_manage_presets),
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp),
            )
            if (state.presetNames.isEmpty()) {
                Text(
                    stringResource(R.string.presets_empty),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp),
                )
            }
            LazyColumn(Modifier.fillMaxWidth().heightIn(max = 360.dp)) {
                items(state.presetNames, key = { it }) { name ->
                    Row(Modifier.fillMaxWidth().padding(start = 12.dp, end = 8.dp), verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(
                            checked = name !in unselected,
                            onCheckedChange = { checked -> unselected = if (checked) unselected - name else unselected + name },
                            modifier = Modifier.testTag(TAG_PRESET_CHECK_PREFIX + name),
                        )
                        Text(name, maxLines = 1, overflow = TextOverflow.Ellipsis, modifier = Modifier.weight(1f))
                        IconButton(onClick = { renaming = name }, modifier = Modifier.testTag(TAG_PRESET_RENAME_PREFIX + name)) {
                            Icon(painterResource(R.drawable.ic_edit), contentDescription = stringResource(R.string.action_rename_preset))
                        }
                        IconButton(onClick = { deleting = name }, modifier = Modifier.testTag(TAG_PRESET_DELETE_PREFIX + name)) {
                            Icon(painterResource(R.drawable.ic_delete), contentDescription = stringResource(R.string.action_delete_preset))
                        }
                    }
                }
            }
            HorizontalDivider(Modifier.padding(vertical = 8.dp))
            FlowRow(Modifier.padding(horizontal = 16.dp), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                TextButton(onClick = onImportFile) { Text(stringResource(R.string.action_import_file)) }
                TextButton(onClick = onImportClipboard, modifier = Modifier.testTag(TAG_IMPORT_CLIPBOARD)) {
                    Text(stringResource(R.string.action_import_clipboard))
                }
                TextButton(onClick = { onExportFile(selected) }, enabled = selected.isNotEmpty()) {
                    Text(stringResource(R.string.action_export_file))
                }
                TextButton(onClick = { onExportClipboard(selected) }, enabled = selected.isNotEmpty(), modifier = Modifier.testTag(TAG_EXPORT_CLIPBOARD)) {
                    Text(stringResource(R.string.action_export_clipboard))
                }
            }
        }
    }
    renaming?.let { name -> RenamePresetDialog(name, model) { renaming = null } }
    deleting?.let { name ->
        AlertDialog(
            onDismissRequest = { deleting = null },
            text = { Text(stringResource(R.string.confirm_delete_preset, name)) },
            confirmButton = {
                TextButton(
                    onClick = {
                        deleting = null
                        unselected = unselected - name
                        model.deletePreset(name)
                    },
                    modifier = Modifier.testTag(TAG_DIALOG_CONFIRM),
                ) { Text(stringResource(R.string.action_delete)) }
            },
            dismissButton = { TextButton(onClick = { deleting = null }) { Text(stringResource(R.string.action_cancel)) } },
        )
    }
}

@Composable
private fun RenamePresetDialog(from: String, model: BatchRenameViewModel, onDismiss: () -> Unit) {
    var name by rememberSaveable(from) { mutableStateOf(from) }
    var problem by remember { mutableStateOf<RejectionKind?>(null) }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.action_rename_preset)) },
        text = {
            OutlinedTextField(
                value = name,
                onValueChange = {
                    name = it
                    problem = null
                },
                label = { Text(stringResource(R.string.field_preset_name)) },
                singleLine = true,
                isError = problem != null,
                supportingText = problem?.let { kind -> { Text(problemText(kind)) } },
                modifier = Modifier.fillMaxWidth().testTag(TAG_PRESET_NAME),
            )
        },
        confirmButton = {
            TextButton(
                onClick = {
                    val result = model.renamePreset(from, name)
                    if (result == null) onDismiss() else problem = result
                },
                modifier = Modifier.testTag(TAG_DIALOG_CONFIRM),
            ) { Text(stringResource(R.string.action_rename)) }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text(stringResource(R.string.action_cancel)) } },
    )
}

/** Names of the document are taken: overwrite, keep both or skip them (roadmap D36). zh-CN: 文档中的名称已被占用时的选择. */
@Composable
private fun ImportConflictDialog(pending: PendingImport, model: BatchRenameViewModel) {
    AlertDialog(
        onDismissRequest = { model.finishImport(null) },
        title = { Text(stringResource(R.string.title_import_conflicts)) },
        text = {
            Text(
                stringResource(R.string.message_import_conflicts, pending.conflicts.joinToString(", ") { "\"$it\"" }),
                modifier = Modifier.verticalScroll(rememberScrollState()),
            )
        },
        confirmButton = {
            Column(horizontalAlignment = Alignment.End) {
                TextButton(onClick = { model.finishImport(PresetConflict.OVERWRITE) }, modifier = Modifier.testTag(TAG_CONFLICT_OVERWRITE)) {
                    Text(stringResource(R.string.action_overwrite))
                }
                TextButton(onClick = { model.finishImport(PresetConflict.RENAME) }, modifier = Modifier.testTag(TAG_CONFLICT_KEEP_BOTH)) {
                    Text(stringResource(R.string.action_keep_both))
                }
                TextButton(onClick = { model.finishImport(PresetConflict.SKIP) }, modifier = Modifier.testTag(TAG_CONFLICT_SKIP)) {
                    Text(stringResource(R.string.action_skip))
                }
                // One column keeps the four choices aligned on narrow screens. zh-CN: 单列排列使四个选项在窄屏上对齐.
                TextButton(onClick = { model.finishImport(null) }) { Text(stringResource(R.string.action_cancel)) }
            }
        },
    )
}

/** An import that refused entries: the counts and why each entry was refused. zh-CN: 有条目被拒的导入: 计数与各条目被拒的原因. */
@Composable
private fun ImportResultDialog(result: PresetImport, model: BatchRenameViewModel) {
    AlertDialog(
        onDismissRequest = model::importResultShown,
        title = { Text(stringResource(R.string.title_import_result)) },
        text = {
            Column(Modifier.verticalScroll(rememberScrollState())) {
                Text(stringResource(R.string.message_import_result, result.imported.size, result.skipped.size, result.rejected.size))
                result.rejected.forEach { rejection ->
                    Text(
                        stringResource(
                            R.string.import_rejected_entry,
                            rejection.index + 1,
                            rejection.name?.let { "\"$it\"" }.orEmpty(),
                            reasonText(rejection.kind),
                        ),
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(top = 4.dp),
                    )
                }
            }
        },
        confirmButton = {
            TextButton(onClick = model::importResultShown, modifier = Modifier.testTag(TAG_DIALOG_CONFIRM)) {
                Text(stringResource(R.string.action_close))
            }
        },
    )
}

@Composable
private fun problemText(kind: RejectionKind): String = when (kind) {
    RejectionKind.NAME_MISSING -> stringResource(R.string.error_preset_name_missing)
    RejectionKind.NAME_INVALID -> stringResource(R.string.error_preset_name_invalid, BatchRenameLimits.MAX_PRESET_NAME_CHARS)
    RejectionKind.DUPLICATE_NAME -> stringResource(R.string.error_preset_name_taken)
    RejectionKind.OPTIONS_INVALID -> stringResource(R.string.error_preset_options_invalid)
    RejectionKind.TOO_MANY -> stringResource(R.string.error_presets_too_many, BatchRenameLimits.MAX_PRESETS)
    RejectionKind.TOO_LARGE -> stringResource(R.string.error_presets_too_large)
}

@Composable
private fun reasonText(kind: RejectionKind): String = stringResource(
    when (kind) {
        RejectionKind.NAME_MISSING -> R.string.reason_preset_name_missing
        RejectionKind.NAME_INVALID -> R.string.reason_preset_name_invalid
        RejectionKind.DUPLICATE_NAME -> R.string.reason_preset_duplicate
        RejectionKind.OPTIONS_INVALID -> R.string.reason_preset_options_invalid
        RejectionKind.TOO_MANY -> R.string.reason_preset_too_many
        RejectionKind.TOO_LARGE -> R.string.reason_preset_too_large
    },
)

/** The text of the clipboard, or null. zh-CN: 剪贴板中的文本, 或 null. */
private fun clipboardText(context: Context): String? {
    val clipboard = context.getSystemService(ClipboardManager::class.java) ?: return null
    val clip = clipboard.primaryClip?.takeIf { it.itemCount > 0 } ?: return null
    return clip.getItemAt(0).coerceToText(context)?.toString()
}

private fun copyToClipboard(context: Context, text: String) {
    context.getSystemService(ClipboardManager::class.java)?.setPrimaryClip(ClipData.newPlainText(CLIP_LABEL, text))
}

private const val MIME_JSON = "application/json"
private const val EXPORT_FILE_NAME = "batch-rename-presets.json"
private const val CLIP_LABEL = "Batch Rename presets"
