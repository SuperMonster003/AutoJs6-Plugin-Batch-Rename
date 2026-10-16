<!--suppress HtmlDeprecatedAttribute, HttpUrlsUsage -->

<div align="center">
  <p>
    <picture>
      <source srcset="https://github.com/SuperMonster003/AutoJs6-Plugin-Batch-Rename/blob/master/app/src/main/res/mipmap-night/ic_launcher.png?raw=true" media="(prefers-color-scheme: dark)" />
      <img src="https://github.com/SuperMonster003/AutoJs6-Plugin-Batch-Rename/blob/master/app/src/main/res/mipmap/ic_launcher.png?raw=true" alt="autojs6-plugin-batch-rename-ic-launcher" border="0" width="128" />
    </picture>
  </p>

  <p>Batch renames files and folders with search and replace, regular expressions, counters and date variables</p>

  <p>
    <a href="https://github.com/SuperMonster003/AutoJs6-Plugin-Batch-Rename/releases"><img alt="GitHub release (latest by date)" src="https://img.shields.io/github/v/release/SuperMonster003/AutoJs6-Plugin-Batch-Rename?label=Release"/></a>
    <a href="https://github.com/SuperMonster003/AutoJs6-Plugin-Batch-Rename/issues"><img alt="GitHub closed issues" src="https://img.shields.io/github/issues/SuperMonster003/AutoJs6-Plugin-Batch-Rename?color=A24232&label=Issues"/></a>
    <a href="https://github.com/SuperMonster003/AutoJs6-Plugin-Batch-Rename/blob/master/LICENSE"><img alt="GitHub License" src="https://img.shields.io/github/license/SuperMonster003/AutoJs6-Plugin-Batch-Rename?color=534BAE&label=License"/></a>
  </p>
</div>

******

### Languages

******

The README.md file is currently available in the following languages:

- [简体中文 [zh-Hans]](https://github.com/SuperMonster003/AutoJs6-Plugin-Batch-Rename/blob/master/.readme/README-zh-Hans.md)
- [繁體中文 (香港) [zh-Hant-HK]](https://github.com/SuperMonster003/AutoJs6-Plugin-Batch-Rename/blob/master/.readme/README-zh-Hant-HK.md)
- [繁體中文 (台灣) [zh-Hant-TW]](https://github.com/SuperMonster003/AutoJs6-Plugin-Batch-Rename/blob/master/.readme/README-zh-Hant-TW.md)
- English [en] # current
- [Français [fr]](https://github.com/SuperMonster003/AutoJs6-Plugin-Batch-Rename/blob/master/.readme/README-fr.md)
- [Español [es]](https://github.com/SuperMonster003/AutoJs6-Plugin-Batch-Rename/blob/master/.readme/README-es.md)
- [日本語 [ja]](https://github.com/SuperMonster003/AutoJs6-Plugin-Batch-Rename/blob/master/.readme/README-ja.md)
- [한국어 [ko]](https://github.com/SuperMonster003/AutoJs6-Plugin-Batch-Rename/blob/master/.readme/README-ko.md)
- [Русский [ru]](https://github.com/SuperMonster003/AutoJs6-Plugin-Batch-Rename/blob/master/.readme/README-ru.md)
- [العربية [ar]](https://github.com/SuperMonster003/AutoJs6-Plugin-Batch-Rename/blob/master/.readme/README-ar.md)

******

### Introduction

******

The Batch Rename plugin adds batch renaming to AutoJs6, modeled on [PowerRename](https://learn.microsoft.com/windows/powertoys/powerrename) from PowerToys: rename many files and folders at once with search and replace, regular expressions, counters, date and random variables, photo metadata and Chinese to pinyin, preview every result before anything changes, and undo afterwards.

The plugin is a separately installed APK without a launcher icon. It has two entry points: "Batch rename" in the file manager's multi-select toolbar, folder menu and rename dialog, and `files.batchRename` in scripts. The plugin only computes the new names; AutoJs6 performs the renames, the conflict checks and the undo records, so the plugin needs no storage permission.

******

### Features

******

- Search and replace: plain text or JavaScript regular expressions, every match or only the first, case sensitive or not, applied to the name, the extension or both.
- Variables: counters (`${start=1;increment=1;padding=3}`), date variables from the modification time or the current time (`$YYYY`, `$MM`, `$DD` and more) and random strings, written as in PowerRename.
- Chinese to pinyin: "Chinese characters to pinyin" under "Name cleanup" turns the Chinese characters of the new name into pinyin, and the `${pinyin}` variable inserts the pinyin of the original name; tones, syllable separators, capitalization and initials only are optional, and polyphonic characters follow their words.
- Photo metadata: with "Metadata variables" on under "Scope and format", name photos by the date taken, camera, lens, aperture, shutter speed, ISO, GPS and more, 25 values in all (`$DATE_TAKEN_YYYY`, `$CAMERA_MODEL` and so on); rows without metadata are marked.
- Named presets: save the current options as a preset with the "Presets" button in the top bar and load it with one tap; presets can be renamed, deleted, imported and exported through files or the clipboard, and scripts can use them too.
- Name cleanup: case conversion, Unicode normalization, full-width to half-width, replacement of characters that are not allowed, trimming of surrounding spaces, and numbering that continues existing names.
- Preview before renaming: old and new names side by side, uncheck items to exclude them, show only the items that change; when a new name is taken, refuse, skip, add a number or replace the existing entry.
- Undo: AutoJs6 keeps the last 20 rename batches of scripts and of each plugin (50 or 100 if you prefer); they can be undone from the file manager and from scripts, or viewed, undone and deleted on the "Rename history" page of AutoJs6; undo checks first whether a file was changed again.
- The screen follows the AutoJs6 theme color, dark mode and language, and can remember the last options and recent search and replace texts.

******

### Usage

******

1. Update AutoJs6 to build 5455 (6.8.0) or later.
2. Install Batch Rename from the AutoJs6 Plugin Center, or download the APK from [Releases](https://github.com/SuperMonster003/AutoJs6-Plugin-Batch-Rename/releases), then authorize and enable it in the Plugin Center.
3. Long-press in the file manager to select items and tap "Batch rename", or call `files.batchRename` in a script.

> The plugin ships a single universal APK for Android 7.0 (API 24) and later. It does not add a launcher icon.

******

### In the file manager

******

Once the plugin is installed and enabled, the AutoJs6 file manager offers three entries:

- Multiple selection: select files or folders and tap "Batch rename" in the selection bar (under "More" when several plugin actions are available).
- Folder menu: choose "Batch rename items inside" in the menu of a folder to rename what it contains.
- Rename dialog: while renaming a single file or folder, open the "More" menu in the top right corner of the dialog and choose "Batch rename...". "Rename history" at the end of the same menu shows past batches and undoes them.
- Type what to search and replace on the plugin screen and the preview updates at once; uncheck items to exclude them, tap "Rename", and tap "Undo last batch" to restore the old names. "Rename history" at the end of the "More options" menu in the top bar opens the rename history page of AutoJs6.

******

### Replacement variables

******

The replacement text can contain the following variables:

| Variable | Meaning |
|---|---|
| `${}` `${start=X;increment=X;padding=X}` | Counter with start value, step and zero padding; it only advances for items that are renamed |
| `$YYYY` `$YY` `$MM` `$MMM` `$DD` `$DDDD` `$hh` `$mm` `$ss` `$fff` | Year, month, day, hour, minute, second and millisecond of the modification time, or of the current time |
| `${rstringalnum=N}` `${rstringalpha=N}` `${rstringdigit=N}` `${ruuidv4}` | Random alphanumeric, letter or digit strings and UUIDs |
| `${pinyin}` | The pinyin of the original name, in the style of the "Chinese characters to pinyin" options |
| `$CAMERA_MODEL` `$LENS` `$APERTURE` `$SHUTTER` `$ISO` `$DATE_TAKEN_YYYY` `$LATITUDE` ... | Camera, lens, exposure, size, author, copyright, GPS, date taken, orientation and color space of a photo (25 values; needs "Metadata variables") |
| `$1` `$<name>` `$&` `$$` | Regular expression groups and the whole match, as in JavaScript's `String.prototype.replace` |

******

### Script API

******

Scripts use the same engine through `files.batchRename` of AutoJs6; see the [AutoJs6 documentation](https://docs.autojs6.com/#/files?id=m-batchrename) for the full reference:

```javascript
let plan = files.batchRename.plan("/sdcard/DCIM/Camera", {
    search: /^IMG_(\d+)/,
    replace: "$YYYY-$MM-$DD_${padding=3}_$1",
    enumerate: true,
    includeFolders: false,
});
plan.items.forEach(it => console.log(it.name, "->", it.newName, it.status));

if (!plan.rejected) {
    let result = files.batchRename.apply(plan);
    files.batchRename.undo(result);
}
```

- `files.batchRename.plan(paths, options)` computes without renaming and returns the new name and status of every item; set `items[i].excluded` to `true` and pass the plan to `apply`.
- `files.batchRename(paths, options)` does both in one step; `files.batchRename.undo(result)` undoes the batch and `files.batchRename.history()` lists recent batches (file manager batches included).
- Regular expressions behave like `RegExp` in scripts, and a `RegExp` object can be passed directly. `planAsync` / `applyAsync` / `undoAsync` are the asynchronous forms; failures throw a `BatchRenameError` with a `code`.
- `files.batchRename.preview(name, options)` computes a new name without any file; `presets()` and `importPresets(json)` list and import named presets, and `preset` in the options uses one; `deleteHistory` and `clearHistory` remove batches from the history; metadata variables need `metadata: true`.

******

### Compatibility

******

- Requires AutoJs6 6.8.0 (build 5455) or later and Android 7.0 (API 24) or later.
- FAT32 / exFAT storage such as SD cards and USB drives ignores case and does not allow `: * ? " < > |`, the backslash and similar characters; new names containing them are marked as unusable, and "Replace characters that are not allowed" replaces them automatically.
- A batch holds at most 2000 items; scripts can reach at most 32 levels below the folders they name.

******

### FAQ

******

#### Why is there no plugin icon on the home screen?

The plugin has no screen of its own to launch. AutoJs6 discovers it after installation, and everything happens in the AutoJs6 file manager or in scripts.

#### Does the plugin need storage permission?

No. The plugin only computes new names from file names; AutoJs6 lists the folders and performs the renames with the permissions it already has.

#### How does it differ from PowerRename?

Date variables use the modification time (creation times are unreliable on Android), and there are additional Chinese to pinyin, named presets, Unicode normalization, full-width to half-width conversion, replacement of characters that are not allowed and undoable batches. All 25 photo metadata variables of PowerRename are supported.

#### Are regular expressions written as in JavaScript?

Yes. The plugin uses the same Rhino regular expression engine as AutoJs6 scripts, and the replacement can use `$1`, `$<name>` and `$&`. A single match that runs longer than 1 second is stopped so that the screen does not hang.

#### Why does the counter not increase?

Turn on the "Counter" option first (`enumerate: true` in scripts); otherwise `${}` stays as it is. The counter only counts items whose names change; unchanged or excluded items do not take a number.

#### Why do metadata variables expand to nothing?

Turn on "Metadata variables" under "Scope and format" first (`metadata: true` in scripts). Values the photo does not record expand to nothing and the row is marked "missing metadata": some RAW files record no ISO, for instance, and pictures saved by social apps carry no EXIF. A missing date taken falls back to the modification time.

#### Why is batch rename missing from the rename dialog?

It is in the "More" menu in the top right corner of the dialog. If the menu only shows a guide, the plugin is not installed, not enabled in the Plugin Center, or AutoJs6 needs an update; follow the guide and it becomes available.

******

### Permissions and security

******

The plugin keeps its permission surface small:

- Least privilege: the Manifest only declares the AutoJs6 plugin permission (`org.autojs.permission.PLUGIN`), with no network or storage permission.
- Host execution: the plugin only returns a rename plan; AutoJs6 validates every item again before renaming and never overwrites existing files unless the overwrite policy is chosen explicitly.
- Protected entry points: the services and the rename screen require the plugin permission, so other apps cannot call them.

******

### Plugin interface

******

The following information targets AutoJs6 host and plugin developers; the host uses these identifiers to discover the plugin and negotiate capabilities:

```text
application id: io.github.supermonster003.autojs6.plugin.batch.rename
plugin id: batch-rename
engine: batch-rename
variant: default
info service: org.autojs.plugin.INFO (category batch-rename)
capability service: org.autojs.plugin.BATCH_RENAME (category batch-rename)
file manager service: org.autojs.plugin.EXPLORER_ACTION
wake action: org.autojs.plugin.action.WAKE
minimum host build: 5455
native library: libandroidx.graphics.path.so (Jetpack Compose, all ABIs)
```

******

### Roadmap

******

Plans, design decisions and progress are tracked as a checklist in ROADMAP.md. Unchecked items are plans, not features of the current version.

- [View ROADMAP.md](https://github.com/SuperMonster003/AutoJs6-Plugin-Batch-Rename/blob/master/ROADMAP.md)

******

### Release history

******

#### v1.1.0

_2026/10/16_

- `Hint` Requires AutoJs6 6.8.0 (5455) or later
- `Feature` Chinese to pinyin: "Chinese characters to pinyin" under "Name cleanup" and the `${pinyin}` variable, with optional tones, syllable separators, capitalization and initials only; polyphonic characters follow their words
- `Feature` Photo metadata variables: name photos by the date taken, camera, lens, aperture, shutter speed, ISO, GPS and more, 25 values in all; rows without metadata are marked (turn on "Metadata variables" under "Scope and format")
- `Feature` Named presets: save the current options as a preset and load it with one tap, rename, delete, import and export presets through files or the clipboard (entry: "Presets" button in the top bar)
- `Feature` Rename history: view, search, undo or delete the rename batches of the file manager and of scripts in AutoJs6, and set how many batches are kept (entry: "More options" in the top bar > "Rename history")
- `Feature` Chinese to pinyin, photo metadata, named presets and name previews without files for the files.batchRename script API of AutoJs6
- `Improvement` New plugin icon
- `Improvement` "Clear history" in the "More options" menu of the top bar is now "Clear recently used", so it is not mistaken for "Rename history"

#### v1.0.0

_2026/10/16_

- `Hint` Requires AutoJs6 6.8.0 (5438) or later
- `Feature` Batch renaming of files and folders in the AutoJs6 file manager (entries: multiple-selection toolbar/folder menu "Batch rename items inside"/"More" menu of the rename dialog)
- `Feature` Search and replace with plain text or JavaScript regular expressions, every match, case sensitivity, and renaming of only the name or the extension
- `Feature` Counters, date variables from the modification time or the current time, random strings and UUIDs
- `Feature` Case conversion, Unicode normalization, full-width to half-width, replacement of characters that are not allowed, trimming of surrounding spaces and numbering that continues existing names
- `Feature` Preview of the new names before renaming, per-item exclusion or only the items that change, and a choice to refuse, skip, add a number or replace the existing entry when a new name is taken
- `Feature` Undo of recent rename batches (AutoJs6 keeps the last 20, shared by the file manager and scripts)
- `Feature` Computes the new names for the files.batchRename script API of AutoJs6
- `Feature` The screen follows the AutoJs6 theme color, dark mode and language, and can remember the last options and recent search and replace texts
- `Dependency` Attach Jetpack Compose BOM 2026.09.00
- `Dependency` Attach AndroidX Activity Compose 1.12.2
- `Dependency` Attach AndroidX Core KTX 1.15.0
- `Dependency` Attach Kotlin Coroutines 1.11.0
- `Dependency` Attach Desugar JDK Libs 2.1.5
- `Dependency` Attach Rhino (the slim AutoJs6 build, for regular expressions)

##### For more release history, refer to

* [CHANGELOG.md](https://github.com/SuperMonster003/AutoJs6-Plugin-Batch-Rename/blob/master/app/src/main/assets/doc/CHANGELOG-en.md)

******

### Build

******

This section targets developers who want to build the plugin from source.

```powershell
.\gradlew.bat :app:assembleDebug :app:testDebugUnitTest
.\gradlew.bat :app:appendDigestToReleasedFiles
py .python\generate_markdown.py --check
py .python\generate_icon_studio.py --check
py .python\check_repository.py
```

******

### License

******

The project is licensed under the [Mozilla Public License 2.0](https://github.com/SuperMonster003/AutoJs6-Plugin-Batch-Rename/blob/master/LICENSE). The feature design follows the public PowerToys PowerRename documentation and contains none of its code or resources.

******

### Related links

******

- AutoJs6 project: https://github.com/SuperMonster003/AutoJs6
- PowerToys PowerRename documentation (feature reference): https://learn.microsoft.com/windows/powertoys/powerrename
