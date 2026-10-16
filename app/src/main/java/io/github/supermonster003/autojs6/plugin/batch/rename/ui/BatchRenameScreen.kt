package io.github.supermonster003.autojs6.plugin.batch.rename.ui

import android.content.ClipData
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ClipEntry
import androidx.compose.ui.platform.LocalClipboard
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTagsAsResourceId
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import io.github.supermonster003.autojs6.plugin.batch.rename.R
import io.github.supermonster003.autojs6.plugin.batch.rename.explorer.RenameCandidateEnumerator
import io.github.supermonster003.autojs6.plugin.batch.rename.explorer.RenameScope
import kotlinx.coroutines.launch
import org.autojs.plugin.batchrename.api.BatchRenameLimits

/** Width from which the options and the preview sit side by side (roadmap P4.2). zh-CN: 选项与预览左右分栏的最小宽度. */
private val TWO_PANE_WIDTH = 600.dp

/**
 * The rename screen (roadmap P4.2, D4, D11): search and replace with history and variables, the options, the live
 * preview with per-row exclusion, and apply / undo with their results.
 * zh-CN: 重命名界面 (路线图 P4.2, D4, D11): 带历史与变量的查找替换, 选项, 可逐行排除的实时预览, 以及执行 / 撤销及其结果.
 */
@OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class)
@Composable
internal fun BatchRenameScreen(state: ScreenState, model: BatchRenameViewModel, onClose: () -> Unit) {
    BackHandler(onBack = onClose)
    val snackbar = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val messageText = state.message?.let { messageText(it) }
    val undoLabel = stringResource(R.string.action_undo)
    LaunchedEffect(state.message) {
        val message = state.message ?: return@LaunchedEffect
        model.messageShown()
        val offerUndo = message is ScreenMessage.Renamed && message.renamed > 0 && state.undo != null
        scope.launch {
            val result = snackbar.showSnackbar(
                message = messageText.orEmpty(),
                actionLabel = if (offerUndo) undoLabel else null,
                duration = if (offerUndo) SnackbarDuration.Long else SnackbarDuration.Short,
            )
            if (result == SnackbarResult.ActionPerformed) model.undo(onClose)
        }
    }

    Scaffold(
        // Test tags double as resource IDs, so the host's cross-process tests find the controls (roadmap P4.4).
        // zh-CN: 测试标签同时作为资源 ID, 宿主的跨进程测试据此找到控件 (路线图 P4.4).
        modifier = Modifier.semantics { testTagsAsResourceId = true },
        topBar = { TopBar(state, model, onClose) },
        bottomBar = { BottomBar(state, model) },
        snackbarHost = { SnackbarHost(snackbar) },
    ) { padding ->
        BoxWithConstraints(Modifier.fillMaxSize().padding(padding)) {
            if (maxWidth >= TWO_PANE_WIDTH) {
                Row(Modifier.fillMaxSize()) {
                    Column(Modifier.weight(0.45f).fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp)) {
                        OptionsPanel(state, model)
                    }
                    LazyColumn(Modifier.weight(0.55f).fillMaxSize().testTag(TAG_LIST)) {
                        previewItems(state, model)
                    }
                }
            } else {
                LazyColumn(Modifier.fillMaxSize().testTag(TAG_LIST)) {
                    item(key = "options") {
                        Column(Modifier.padding(16.dp)) { OptionsPanel(state, model) }
                    }
                    previewItems(state, model)
                }
            }
            if (state.busy == Busy.LISTING) {
                LinearProgressIndicator(Modifier.fillMaxWidth().align(Alignment.TopCenter))
            }
        }
    }

    if (state.busy == Busy.RENAMING || state.busy == Busy.UNDOING) {
        AlertDialog(
            onDismissRequest = {},
            confirmButton = {},
            title = { Text(stringResource(if (state.busy == Busy.RENAMING) R.string.progress_renaming else R.string.progress_undoing)) },
            text = { Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) { CircularProgressIndicator() } },
        )
    }
    if (state.failures.isNotEmpty()) FailuresDialog(state.failures, onDismiss = model::failuresShown)
    if (state.fatal == Fatal.SESSION_ENDED) {
        AlertDialog(
            onDismissRequest = onClose,
            confirmButton = { TextButton(onClick = onClose) { Text(stringResource(R.string.action_close)) } },
            text = { Text(stringResource(R.string.error_session_ended)) },
        )
    }
}

@Composable
private fun messageText(message: ScreenMessage): String = when (message) {
    is ScreenMessage.Renamed -> if (message.notRenamed == 0) {
        pluralStringResource(R.plurals.message_renamed, message.renamed, message.renamed)
    } else {
        stringResource(R.string.message_renamed_partially, message.renamed, message.notRenamed)
    }
    is ScreenMessage.Restored -> if (message.notRestored <= 0) {
        pluralStringResource(R.plurals.message_restored, message.restored, message.restored)
    } else {
        stringResource(R.string.message_restored_partially, message.restored, message.notRestored)
    }
    ScreenMessage.NothingToUndo -> stringResource(R.string.message_nothing_to_undo)
    ScreenMessage.TooManyInOneFolder -> stringResource(R.string.error_too_many_in_one_folder)
    is ScreenMessage.Failed -> stringResource(R.string.error_host_failed, message.detail)
    is ScreenMessage.PresetSaved -> stringResource(R.string.message_preset_saved, message.name)
    is ScreenMessage.PresetLoaded -> stringResource(R.string.message_preset_loaded, message.name)
    ScreenMessage.PresetsExported -> stringResource(R.string.message_presets_exported)
    ScreenMessage.PresetsCopied -> stringResource(R.string.message_presets_copied)
    is ScreenMessage.PresetsImported -> stringResource(R.string.message_presets_imported, message.imported, message.skipped)
    is ScreenMessage.PresetFailed -> stringResource(
        when (message.failure) {
            PresetFailure.CLIPBOARD_EMPTY -> R.string.error_clipboard_empty
            PresetFailure.NOT_PRESETS -> R.string.error_not_presets
            PresetFailure.TOO_LARGE -> R.string.error_presets_too_large
            PresetFailure.READ_FAILED -> R.string.error_read_failed
            PresetFailure.WRITE_FAILED -> R.string.error_write_failed
        },
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TopBar(state: ScreenState, model: BatchRenameViewModel, onClose: () -> Unit) {
    var menu by remember { mutableStateOf(false) }
    TopAppBar(
        navigationIcon = {
            IconButton(onClick = onClose) {
                Icon(painterResource(R.drawable.ic_arrow_back), contentDescription = stringResource(R.string.action_close))
            }
        },
        title = {
            Column {
                Text(stringResource(R.string.screen_title), maxLines = 1, overflow = TextOverflow.Ellipsis)
                val subtitle = when (state.scope) {
                    RenameScope.INSIDE_DIRECTORY -> state.parentDisplayPath
                    RenameScope.TARGETS -> pluralStringResource(R.plurals.subtitle_selected_items, state.targetCount, state.targetCount)
                }
                if (subtitle.isNotEmpty()) {
                    Text(subtitle, style = MaterialTheme.typography.bodySmall, maxLines = 1, overflow = TextOverflow.Ellipsis)
                }
            }
        },
        actions = {
            IconButton(
                onClick = { model.undo(onClose) },
                enabled = state.undo != null && state.busy == null,
                modifier = Modifier.testTag(TAG_UNDO),
            ) {
                Icon(painterResource(R.drawable.ic_undo), contentDescription = stringResource(R.string.action_undo_last_batch))
            }
            PresetsButton(state, model)
            Box {
                IconButton(onClick = { menu = true }) {
                    Icon(painterResource(R.drawable.ic_more_vert), contentDescription = stringResource(R.string.action_more))
                }
                DropdownMenu(expanded = menu, onDismissRequest = { menu = false }) {
                    DropdownMenuItem(
                        text = { Text(stringResource(R.string.menu_remember_options)) },
                        trailingIcon = { androidx.compose.material3.Checkbox(checked = state.rememberOptions, onCheckedChange = null) },
                        onClick = { model.setRememberOptions(!state.rememberOptions) },
                    )
                    DropdownMenuItem(
                        text = { Text(stringResource(R.string.menu_restore_defaults)) },
                        onClick = {
                            menu = false
                            model.restoreDefaults()
                        },
                    )
                    DropdownMenuItem(
                        text = { Text(stringResource(R.string.menu_clear_history)) },
                        enabled = state.recentSearches.isNotEmpty() || state.recentReplacements.isNotEmpty(),
                        onClick = {
                            menu = false
                            model.clearHistory()
                        },
                    )
                }
            }
        },
    )
}

@Composable
private fun BottomBar(state: ScreenState, model: BatchRenameViewModel) {
    Surface(tonalElevation = 3.dp) {
        Row(
            Modifier.fillMaxWidth().navigationBarsPadding().padding(horizontal = 16.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            val preview = state.preview
            Text(
                stringResource(R.string.summary_counts, preview.renameCount, preview.problemCount, preview.excludedCount),
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.weight(1f).testTag(TAG_SUMMARY),
            )
            Spacer(Modifier.width(12.dp))
            Button(
                onClick = model::apply,
                enabled = state.canApply,
                modifier = Modifier.testTag(TAG_APPLY),
            ) {
                Text(stringResource(R.string.action_rename))
            }
        }
    }
}

private fun LazyListScope.previewItems(state: ScreenState, model: BatchRenameViewModel) {
    item(key = "previewHeader") {
        PreviewHeader(state, model)
        HorizontalDivider()
    }
    val rows = if (state.onlyRenamed) state.preview.rows.filter { it.renames } else state.preview.rows
    items(rows, key = { it.candidate.key }) { row ->
        PreviewRowItem(row, onToggle = { model.toggleExcluded(row.candidate.key) })
    }
    if (state.candidates?.truncated == true) {
        item(key = "truncated") {
            Text(
                stringResource(R.string.notice_truncated, BatchRenameLimits.MAX_ITEMS_PER_PLAN, RenameCandidateEnumerator.MAX_DEPTH),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(16.dp),
            )
        }
    }
    if (state.busy != Busy.LISTING && state.candidates?.candidates?.isEmpty() == true) {
        item(key = "empty") {
            Text(stringResource(R.string.notice_nothing_to_rename), modifier = Modifier.padding(16.dp), color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
    item(key = "bottomSpace") { Spacer(Modifier.height(8.dp)) }
}

@Composable
private fun PreviewHeader(state: ScreenState, model: BatchRenameViewModel) {
    Row(Modifier.fillMaxWidth().padding(start = 16.dp, end = 8.dp, top = 8.dp, bottom = 4.dp), verticalAlignment = Alignment.CenterVertically) {
        Text(stringResource(R.string.preview_title), style = MaterialTheme.typography.titleSmall, modifier = Modifier.weight(1f))
        Text(stringResource(R.string.preview_only_renamed), style = MaterialTheme.typography.bodySmall)
        androidx.compose.material3.Switch(
            checked = state.onlyRenamed,
            onCheckedChange = model::setOnlyRenamed,
            modifier = Modifier.padding(start = 8.dp).testTag(TAG_ONLY_RENAMED),
        )
    }
}

@Composable
private fun FailuresDialog(failures: List<FailedEntry>, onDismiss: () -> Unit) {
    val clipboard = LocalClipboard.current
    val scope = rememberCoroutineScope()
    val stale = stringResource(R.string.failure_changed)
    val taken = stringResource(R.string.failure_not_renamed)
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.failures_title)) },
        text = {
            LazyColumn(Modifier.fillMaxWidth().height((failures.size.coerceAtMost(8) * 56).dp)) {
                items(failures) { failure ->
                    Column(Modifier.padding(vertical = 6.dp)) {
                        Text(failure.path, style = MaterialTheme.typography.bodyMedium, maxLines = 2, overflow = TextOverflow.Ellipsis)
                        Text(
                            "${failure.newName} - ${if (failure.stale) stale else taken}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.error,
                        )
                    }
                }
            }
        },
        confirmButton = { TextButton(onClick = onDismiss) { Text(stringResource(R.string.action_close)) } },
        dismissButton = {
            TextButton(onClick = {
                val text = failures.joinToString("\n") { "${it.path} -> ${it.newName}: ${if (it.stale) stale else taken}" }
                scope.launch { clipboard.setClipEntry(ClipEntry(ClipData.newPlainText(text, text))) }
            }) { Text(stringResource(R.string.action_copy)) }
        },
    )
}

@Composable
internal fun SectionTitle(text: String) {
    Text(text, style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.primary, modifier = Modifier.padding(top = 12.dp, bottom = 4.dp))
}

@Composable
internal fun Gap() = Spacer(Modifier.size(8.dp))

internal val ChipSpacing = Arrangement.spacedBy(8.dp)

internal const val TAG_LIST = "batchRename.list"
internal const val TAG_SEARCH = "batchRename.search"
internal const val TAG_REPLACE = "batchRename.replace"
internal const val TAG_APPLY = "batchRename.apply"
internal const val TAG_UNDO = "batchRename.undo"
internal const val TAG_SUMMARY = "batchRename.summary"
internal const val TAG_ONLY_RENAMED = "batchRename.onlyRenamed"
internal const val TAG_SEARCH_HISTORY = "batchRename.searchHistory"
internal const val TAG_CLEANUP = "batchRename.cleanup"
internal const val TAG_PINYIN = "batchRename.pinyin"
internal const val TAG_PRESETS = "batchRename.presets"
internal const val TAG_PRESET_PREFIX = "batchRename.preset:"
internal const val TAG_SAVE_PRESET = "batchRename.savePreset"
internal const val TAG_MANAGE_PRESETS = "batchRename.managePresets"
internal const val TAG_PRESET_NAME = "batchRename.presetName"
internal const val TAG_PRESET_CHECK_PREFIX = "batchRename.presetCheck:"
internal const val TAG_PRESET_RENAME_PREFIX = "batchRename.presetRename:"
internal const val TAG_PRESET_DELETE_PREFIX = "batchRename.presetDelete:"
internal const val TAG_IMPORT_CLIPBOARD = "batchRename.importClipboard"
internal const val TAG_EXPORT_CLIPBOARD = "batchRename.exportClipboard"
internal const val TAG_CONFLICT_OVERWRITE = "batchRename.conflictOverwrite"
internal const val TAG_CONFLICT_KEEP_BOTH = "batchRename.conflictKeepBoth"
internal const val TAG_CONFLICT_SKIP = "batchRename.conflictSkip"
internal const val TAG_DIALOG_CONFIRM = "batchRename.dialogConfirm"
internal const val TAG_ROW_PREFIX = "batchRename.row:"
