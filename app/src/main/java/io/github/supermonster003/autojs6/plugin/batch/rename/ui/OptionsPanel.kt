package io.github.supermonster003.autojs6.plugin.batch.rename.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.toggleable
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import io.github.supermonster003.autojs6.plugin.batch.rename.R
import io.github.supermonster003.autojs6.plugin.batch.rename.engine.ConflictPolicy
import io.github.supermonster003.autojs6.plugin.batch.rename.engine.DateTimeSource
import io.github.supermonster003.autojs6.plugin.batch.rename.engine.Normalization
import io.github.supermonster003.autojs6.plugin.batch.rename.engine.PinyinCase
import io.github.supermonster003.autojs6.plugin.batch.rename.engine.PinyinSeparator
import io.github.supermonster003.autojs6.plugin.batch.rename.engine.PinyinTone
import io.github.supermonster003.autojs6.plugin.batch.rename.engine.PlanItemStatus
import io.github.supermonster003.autojs6.plugin.batch.rename.engine.PlanReason
import io.github.supermonster003.autojs6.plugin.batch.rename.engine.RenameOptions
import io.github.supermonster003.autojs6.plugin.batch.rename.engine.RenameTarget
import io.github.supermonster003.autojs6.plugin.batch.rename.engine.TextFormat
import io.github.supermonster003.autojs6.plugin.batch.rename.explorer.PreviewRow

/** The search and replace fields and every option (roadmap P4.2). zh-CN: 查找与替换输入框及全部选项. */
@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
internal fun OptionsPanel(state: ScreenState, model: BatchRenameViewModel) {
    val options = state.options
    var variables by remember { mutableStateOf(false) }
    var replaceField by rememberSaveable(stateSaver = TextFieldValue.Saver) { mutableStateOf(TextFieldValue(options.replace)) }
    LaunchedEffect(options.replace) {
        if (replaceField.text != options.replace) replaceField = TextFieldValue(options.replace, TextRange(options.replace.length))
    }

    HistoryField(
        value = TextFieldValue(options.search, TextRange(options.search.length)),
        label = stringResource(R.string.field_search),
        history = state.recentSearches,
        error = state.optionsError,
        tag = TAG_SEARCH,
        historyTag = TAG_SEARCH_HISTORY,
        onValueChange = { model.setSearch(it.text) },
    )
    Gap()
    HistoryField(
        value = replaceField,
        label = stringResource(R.string.field_replace),
        history = state.recentReplacements,
        error = null,
        tag = TAG_REPLACE,
        historyTag = null,
        onValueChange = {
            replaceField = it
            model.setReplace(it.text)
        },
        extraAction = {
            IconButton(onClick = { variables = true }) {
                Icon(painterResource(R.drawable.ic_data_object), contentDescription = stringResource(R.string.action_insert_variable))
            }
        },
    )

    FlowRow(horizontalArrangement = ChipSpacing, modifier = Modifier.padding(top = 8.dp)) {
        ToggleChip(R.string.option_regex, options.regex) { value -> model.setOptions { it.copy(regex = value) } }
        ToggleChip(R.string.option_match_all, options.matchAll) { value -> model.setOptions { it.copy(matchAll = value) } }
        ToggleChip(R.string.option_case_sensitive, options.caseSensitive) { value -> model.setOptions { it.copy(caseSensitive = value) } }
    }

    ExpandableSection(R.string.section_options) {
        SectionTitle(stringResource(R.string.section_apply_to))
        val targets = listOf(RenameTarget.ALL to R.string.target_all, RenameTarget.NAME to R.string.target_name, RenameTarget.EXTENSION to R.string.target_extension)
        SingleChoiceSegmentedButtonRow(Modifier.fillMaxWidth()) {
            targets.forEachIndexed { index, (target, label) ->
                SegmentedButton(
                    selected = options.target == target,
                    onClick = { model.setOptions { it.copy(target = target) } },
                    shape = SegmentedButtonDefaults.itemShape(index, targets.size),
                ) { Text(stringResource(label), maxLines = 1, overflow = TextOverflow.Ellipsis) }
            }
        }
        FlowRow(horizontalArrangement = ChipSpacing, modifier = Modifier.padding(top = 8.dp)) {
            ToggleChip(R.string.option_include_files, options.includeFiles) { value -> model.setOptions { it.copy(includeFiles = value) } }
            ToggleChip(R.string.option_include_folders, options.includeFolders) { value -> model.setOptions { it.copy(includeFolders = value) } }
            ToggleChip(R.string.option_include_subfolders, options.includeSubfolders) { value -> model.setOptions { it.copy(includeSubfolders = value) } }
        }

        SectionTitle(stringResource(R.string.section_format))
        ChoiceRow(
            label = stringResource(R.string.option_text_format),
            choices = listOf(
                TextFormat.NONE to R.string.format_none,
                TextFormat.LOWER to R.string.format_lower,
                TextFormat.UPPER to R.string.format_upper,
                TextFormat.TITLE to R.string.format_title,
                TextFormat.CAPITALIZE to R.string.format_capitalize,
            ),
            selected = options.format,
            onSelect = { value -> model.setOptions { it.copy(format = value) } },
        )
        FlowRow(horizontalArrangement = ChipSpacing) {
            ToggleChip(R.string.option_enumerate, options.enumerate) { value -> model.setOptions { it.copy(enumerate = value) } }
            ToggleChip(R.string.option_random, options.random) { value -> model.setOptions { it.copy(random = value) } }
        }
        ChoiceRow(
            label = stringResource(R.string.option_date_source),
            choices = listOf(
                DateTimeSource.MODIFIED to R.string.date_modified,
                DateTimeSource.NOW to R.string.date_now,
                DateTimeSource.NONE to R.string.date_off,
            ),
            selected = options.dateTime,
            onSelect = { value -> model.setOptions { it.copy(dateTime = value) } },
        )
    }

    ExpandableSection(R.string.section_cleanup, Modifier.testTag(TAG_CLEANUP)) {
        SwitchRow(R.string.option_pinyin, options.pinyin, Modifier.testTag(TAG_PINYIN)) { value -> model.setOptions { it.copy(pinyin = value) } }
        if (options.pinyin || PINYIN_VARIABLE in options.replace) PinyinStyleRows(options, model)
        ChoiceRow(
            label = stringResource(R.string.option_normalize),
            choices = listOf(
                Normalization.NONE to R.string.normalize_none,
                Normalization.NFC to R.string.normalize_nfc,
                Normalization.NFD to R.string.normalize_nfd,
            ),
            selected = options.normalize,
            onSelect = { value -> model.setOptions { it.copy(normalize = value) } },
        )
        SwitchRow(R.string.option_half_width, options.halfWidth) { value -> model.setOptions { it.copy(halfWidth = value) } }
        SwitchRow(R.string.option_sanitize, options.sanitize) { value -> model.setOptions { it.copy(sanitize = value) } }
        if (options.sanitize) {
            OutlinedTextField(
                value = options.sanitizeReplacement,
                onValueChange = { text -> model.setOptions { it.copy(sanitizeReplacement = text) } },
                label = { Text(stringResource(R.string.option_sanitize_replacement)) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
            )
        }
        SwitchRow(R.string.option_trim, options.trim) { value -> model.setOptions { it.copy(trim = value) } }
        SwitchRow(R.string.option_continue_numbering, options.continueNumbering) { value -> model.setOptions { it.copy(continueNumbering = value) } }
    }

    ExpandableSection(R.string.section_conflicts) {
        ConflictChoices(options, model)
    }

    if (variables) {
        ModalBottomSheet(onDismissRequest = { variables = false }) {
            VariablesSheet(options) { token ->
                variables = false
                val selection = replaceField.selection
                val text = replaceField.text.replaceRange(selection.min, selection.max, token)
                replaceField = TextFieldValue(text, TextRange(selection.min + token.length))
                model.setReplace(text)
            }
        }
    }
}

@Composable
private fun HistoryField(
    value: TextFieldValue,
    label: String,
    history: List<String>,
    error: String?,
    tag: String,
    historyTag: String?,
    onValueChange: (TextFieldValue) -> Unit,
    extraAction: @Composable (() -> Unit)? = null,
) {
    var open by remember { mutableStateOf(false) }
    Box {
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            label = { Text(label) },
            singleLine = true,
            isError = error != null,
            supportingText = error?.let { { Text(it, maxLines = 3, overflow = TextOverflow.Ellipsis) } },
            textStyle = MaterialTheme.typography.bodyLarge.copy(fontFamily = FontFamily.Monospace),
            trailingIcon = {
                Row {
                    extraAction?.invoke()
                    if (history.isNotEmpty()) {
                        IconButton(onClick = { open = true }, modifier = historyTag?.let { Modifier.testTag(it) } ?: Modifier) {
                            Icon(painterResource(R.drawable.ic_history), contentDescription = stringResource(R.string.action_recent_entries))
                        }
                    }
                }
            },
            modifier = Modifier.fillMaxWidth().testTag(tag),
        )
        DropdownMenu(expanded = open, onDismissRequest = { open = false }) {
            history.forEach { entry ->
                DropdownMenuItem(
                    text = { Text(entry, maxLines = 1, overflow = TextOverflow.Ellipsis, fontFamily = FontFamily.Monospace) },
                    onClick = {
                        open = false
                        onValueChange(TextFieldValue(entry, TextRange(entry.length)))
                    },
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ToggleChip(label: Int, selected: Boolean, onChange: (Boolean) -> Unit) {
    FilterChip(selected = selected, onClick = { onChange(!selected) }, label = { Text(stringResource(label)) })
}

@Composable
private fun SwitchRow(label: Int, checked: Boolean, modifier: Modifier = Modifier, onChange: (Boolean) -> Unit) {
    Row(
        modifier.fillMaxWidth().toggleable(value = checked, role = Role.Switch, onValueChange = onChange).padding(vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(stringResource(label), modifier = Modifier.weight(1f), style = MaterialTheme.typography.bodyMedium)
        Switch(checked = checked, onCheckedChange = null)
    }
}

@Composable
private fun <T> ChoiceRow(label: String, choices: List<Pair<T, Int>>, selected: T, onSelect: (T) -> Unit) {
    var open by remember { mutableStateOf(false) }
    val current = stringResource(choices.first { it.first == selected }.second)
    Row(Modifier.fillMaxWidth().padding(vertical = 2.dp), verticalAlignment = Alignment.CenterVertically) {
        Text(label, modifier = Modifier.weight(1f), style = MaterialTheme.typography.bodyMedium)
        Box {
            TextButton(onClick = { open = true }, modifier = Modifier.semantics { stateDescription = current }) {
                Text(current)
                Icon(painterResource(R.drawable.ic_expand_more), contentDescription = null, modifier = Modifier.size(18.dp))
            }
            DropdownMenu(expanded = open, onDismissRequest = { open = false }) {
                choices.forEach { (value, text) ->
                    DropdownMenuItem(text = { Text(stringResource(text)) }, onClick = {
                        open = false
                        onSelect(value)
                    })
                }
            }
        }
    }
}

/**
 * The pinyin style (roadmap D35), shown while the cleanup or the `${pinyin}` variable uses it.
 * zh-CN: 拼音样式 (路线图 D35), 在整理选项或 `${pinyin}` 变量用到时显示.
 */
@Composable
private fun PinyinStyleRows(options: RenameOptions, model: BatchRenameViewModel) {
    ChoiceRow(
        label = stringResource(R.string.option_pinyin_tone),
        choices = listOf(
            PinyinTone.NONE to R.string.pinyin_tone_none,
            PinyinTone.MARK to R.string.pinyin_tone_mark,
            PinyinTone.NUMBER to R.string.pinyin_tone_number,
        ),
        selected = options.pinyinTone,
        onSelect = { value -> model.setOptions { it.copy(pinyinTone = value) } },
    )
    ChoiceRow(
        label = stringResource(R.string.option_pinyin_separator),
        choices = listOf(
            PinyinSeparator.NONE to R.string.pinyin_separator_none,
            PinyinSeparator.SPACE to R.string.pinyin_separator_space,
            PinyinSeparator.UNDERSCORE to R.string.pinyin_separator_underscore,
            PinyinSeparator.HYPHEN to R.string.pinyin_separator_hyphen,
        ),
        selected = options.pinyinSeparator,
        onSelect = { value -> model.setOptions { it.copy(pinyinSeparator = value) } },
    )
    ChoiceRow(
        label = stringResource(R.string.option_pinyin_case),
        choices = listOf(PinyinCase.LOWER to R.string.pinyin_case_lower, PinyinCase.CAPITALIZE to R.string.pinyin_case_capitalize),
        selected = options.pinyinCase,
        onSelect = { value -> model.setOptions { it.copy(pinyinCase = value) } },
    )
    SwitchRow(R.string.option_pinyin_initials, options.pinyinInitials) { value -> model.setOptions { it.copy(pinyinInitials = value) } }
}

@Composable
private fun ExpandableSection(title: Int, modifier: Modifier = Modifier, content: @Composable () -> Unit) {
    var expanded by rememberSaveable { mutableStateOf(false) }
    val label = stringResource(title)
    val state = stringResource(if (expanded) R.string.state_expanded else R.string.state_collapsed)
    Row(
        modifier.fillMaxWidth().clickable { expanded = !expanded }.padding(top = 12.dp, bottom = 4.dp)
            .semantics { stateDescription = state },
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(label, style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.primary, modifier = Modifier.weight(1f))
        Icon(painterResource(if (expanded) R.drawable.ic_expand_less else R.drawable.ic_expand_more), contentDescription = null)
    }
    if (expanded) Column { content() }
}

@Composable
private fun ConflictChoices(options: RenameOptions, model: BatchRenameViewModel) {
    var confirmOverwrite by remember { mutableStateOf(false) }
    val policies = listOf(
        ConflictPolicy.FAIL to R.string.conflict_fail,
        ConflictPolicy.SKIP to R.string.conflict_skip,
        ConflictPolicy.NUMBER to R.string.conflict_number,
        ConflictPolicy.OVERWRITE to R.string.conflict_overwrite,
    )
    policies.forEach { (policy, label) ->
        Row(
            Modifier.fillMaxWidth()
                .selectable(selected = options.onConflict == policy, role = Role.RadioButton) {
                    if (policy == ConflictPolicy.OVERWRITE) confirmOverwrite = true else model.setOptions { it.copy(onConflict = policy) }
                }
                .padding(vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            RadioButton(selected = options.onConflict == policy, onClick = null)
            Text(stringResource(label), modifier = Modifier.padding(start = 8.dp), style = MaterialTheme.typography.bodyMedium)
        }
    }
    if (options.onConflict == ConflictPolicy.NUMBER) {
        OutlinedTextField(
            value = options.conflictNumberPattern,
            onValueChange = { text -> model.setOptions { it.copy(conflictNumberPattern = text) } },
            label = { Text(stringResource(R.string.option_number_pattern)) },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
        )
    }
    if (confirmOverwrite) {
        AlertDialog(
            onDismissRequest = { confirmOverwrite = false },
            title = { Text(stringResource(R.string.confirm_overwrite_title)) },
            text = { Text(stringResource(R.string.confirm_overwrite_message)) },
            confirmButton = {
                TextButton(onClick = {
                    confirmOverwrite = false
                    model.setOptions { it.copy(onConflict = ConflictPolicy.OVERWRITE) }
                }) { Text(stringResource(R.string.action_replace_existing)) }
            },
            dismissButton = { TextButton(onClick = { confirmOverwrite = false }) { Text(stringResource(R.string.action_cancel)) } },
        )
    }
}

/** The variable that writes the original text in pinyin (roadmap D35). zh-CN: 以拼音书写原文的变量 (路线图 D35). */
private const val PINYIN_VARIABLE = "\${pinyin}"

/** A token the variables sheet inserts and what it stands for. zh-CN: 变量面板插入的记号及其含义. */
private data class Variable(val token: String, val description: Int)

@Composable
private fun VariablesSheet(options: RenameOptions, onInsert: (String) -> Unit) {
    val groups = buildList {
        add(R.string.variables_counter to listOf(Variable("\${}", R.string.variable_counter), Variable("\${start=1;padding=3}", R.string.variable_counter_options)))
        add(
            R.string.variables_date to listOf(
                Variable("\$YYYY", R.string.variable_year),
                Variable("\$MM", R.string.variable_month),
                Variable("\$MMMM", R.string.variable_month_name),
                Variable("\$DD", R.string.variable_day),
                Variable("\$DDDD", R.string.variable_weekday),
                Variable("\$hh", R.string.variable_hour),
                Variable("\$mm", R.string.variable_minute),
                Variable("\$ss", R.string.variable_second),
                Variable("\$fff", R.string.variable_millis),
            ),
        )
        add(
            R.string.variables_random to listOf(
                Variable("\${rstringalnum=8}", R.string.variable_random_alnum),
                Variable("\${rstringdigit=4}", R.string.variable_random_digits),
                Variable("\${ruuidv4}", R.string.variable_uuid),
            ),
        )
        add(R.string.variables_pinyin to listOf(Variable(PINYIN_VARIABLE, R.string.variable_pinyin)))
        if (options.regex) add(R.string.variables_regex to listOf(Variable("\$1", R.string.variable_group), Variable("\$&", R.string.variable_match)))
    }
    LazyColumn(Modifier.fillMaxWidth().padding(bottom = 24.dp)) {
        groups.forEach { (title, variables) ->
            item(key = "title:$title") {
                Text(stringResource(title), style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.primary, modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp))
            }
            items(variables, key = { it.token }) { variable ->
                ListItem(
                    headlineContent = { Text(variable.token, fontFamily = FontFamily.Monospace) },
                    supportingContent = { Text(stringResource(variable.description)) },
                    modifier = Modifier.clickable { onInsert(variable.token) },
                )
            }
        }
        item(key = "hint") {
            Text(stringResource(R.string.variables_hint), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.padding(16.dp))
        }
    }
}

/** One preview row: checkbox, icon, old and new name, and why a name is refused. zh-CN: 预览中的一行. */
@Composable
internal fun PreviewRowItem(row: PreviewRow, onToggle: () -> Unit) {
    val candidate = row.candidate
    val colors = MaterialTheme.colorScheme
    val newColor = when (row.status) {
        PlanItemStatus.RENAME -> colors.primary
        PlanItemStatus.CONFLICT, PlanItemStatus.INVALID -> colors.error
        else -> colors.onSurfaceVariant
    }
    val statusText = statusText(row)
    val included = stringResource(R.string.state_included)
    val excluded = stringResource(R.string.state_excluded)
    Row(
        Modifier.fillMaxWidth()
            .toggleable(value = !row.excluded, role = Role.Checkbox, onValueChange = { onToggle() })
            .padding(start = (8 + candidate.depth.coerceAtMost(8) * 12).dp, end = 16.dp, top = 4.dp, bottom = 4.dp)
            .semantics(mergeDescendants = true) {
                stateDescription = if (row.excluded) excluded else included
                contentDescription = listOfNotNull(candidate.name, row.newName.takeIf { row.renames }, statusText).joinToString(", ")
            }
            .testTag(TAG_ROW_PREFIX + candidate.key),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Checkbox(checked = !row.excluded, onCheckedChange = null)
        Icon(
            painterResource(if (candidate.directory) R.drawable.ic_folder else R.drawable.ic_file),
            contentDescription = null,
            tint = colors.onSurfaceVariant,
            modifier = Modifier.padding(horizontal = 8.dp).size(20.dp),
        )
        Column(Modifier.weight(1f)) {
            Text(candidate.name, style = MaterialTheme.typography.bodyMedium, maxLines = 1, overflow = TextOverflow.Ellipsis)
            if (row.newName != candidate.name || statusText != null) {
                Text(
                    if (row.newName != candidate.name) row.newName else statusText.orEmpty(),
                    style = MaterialTheme.typography.bodyMedium,
                    color = newColor,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
            }
            if (statusText != null && row.newName != candidate.name) {
                Text(statusText, style = MaterialTheme.typography.bodySmall, color = newColor)
            }
        }
    }
}

/** Why a row keeps its name or how it is renamed, or null for a plain rename. */
@Composable
private fun statusText(row: PreviewRow): String? {
    val reason = row.reason ?: return null
    if (row.status == PlanItemStatus.UNCHANGED || row.status == PlanItemStatus.EXCLUDED) {
        if (reason == PlanReason.NO_MATCH || reason == PlanReason.SAME_NAME || reason == PlanReason.EXCLUDED_BY_CALLER) return null
    }
    return stringResource(
        when (reason) {
            PlanReason.NO_MATCH -> R.string.reason_no_match
            PlanReason.SAME_NAME -> R.string.reason_same_name
            PlanReason.EXCLUDED_BY_CALLER -> R.string.reason_excluded
            PlanReason.NOT_INCLUDED -> R.string.reason_not_included
            PlanReason.DUPLICATE_IN_BATCH -> R.string.reason_duplicate
            PlanReason.EXISTING_ENTRY -> R.string.reason_existing
            PlanReason.NUMBERS_EXHAUSTED -> R.string.reason_numbers_exhausted
            PlanReason.NUMBERED -> R.string.reason_numbered
            PlanReason.OVERWRITE -> R.string.reason_overwrite
            PlanReason.EMPTY -> R.string.reason_empty
            PlanReason.DOT_NAME -> R.string.reason_dot_name
            PlanReason.SEPARATOR -> R.string.reason_separator
            PlanReason.TOO_LONG -> R.string.reason_too_long
            PlanReason.RESTRICTED_CHARACTER -> R.string.reason_restricted_character
        },
    )
}
