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

The Batch Rename plugin adds batch renaming to AutoJs6, modeled on [PowerRename](https://learn.microsoft.com/windows/powertoys/powerrename) from PowerToys: rename many files and folders at once with search and replace, regular expressions, counters, date and random variables, preview every result before anything changes, and undo afterwards.

The plugin is a separately installed APK without a launcher icon. It has two entry points: "Batch rename" in the file manager's multi-select toolbar, folder menu and rename dialog, and `files.batchRename` in scripts. The plugin only computes the new names; AutoJs6 performs the renames, the conflict checks and the undo records, so the plugin needs no storage permission.

> This is a development preview: AutoJs6 recognizes the plugin, but the rename screen, the file manager entries and the script API are not delivered yet. The sections below describe the design goals of 1.0.0; see the roadmap for progress.

******

### Features

******

- Search and replace: plain text or regular expressions, all matches or only the first, case sensitive or not, applied to the file name, the extension or the whole name.
- Variables: counters (`${start=1;increment=1;padding=3}`), date variables from the modification time (`$YYYY`, `$MM`, `$DD` and more) and random strings, written as in PowerRename.
- Text cleanup: case conversion, Unicode normalization, full-width to half-width conversion, illegal character cleanup, trimming of leading and trailing spaces and dots, and numbering that continues existing sequences.
- Preview first: every item shows its old and new name, single items can be excluded, and conflicts and invalid names are marked; a conflict can reject the batch, skip the item, add a number or overwrite.
- Undo: AutoJs6 keeps the last 20 rename batches; both the file manager and scripts can undo them after checking that the files have not changed again.

******

### Usage

******

1. Update AutoJs6 to build 5352 (6.8.0) or later.
2. Install Batch Rename from the AutoJs6 Plugin Center, or download the APK from [Releases](https://github.com/SuperMonster003/AutoJs6-Plugin-Batch-Rename/releases), then authorize and enable it in the Plugin Center.
3. Long-press in the file manager to select items and tap "Batch rename", or call `files.batchRename` in a script.

> The plugin ships a single universal APK for Android 7.0 (API 24) and later. It does not add a launcher icon.

******

### Replacement variables

******

The replacement text can contain the following variables:

| Variable | Meaning |
|---|---|
| `${}` `${start=X;increment=X;padding=X}` | Counter with start value, step and zero padding; it only advances for items that are renamed |
| `$YYYY` `$YY` `$MM` `$MMM` `$DD` `$DDDD` `$hh` `$mm` `$ss` `$fff` | Year, month, day, hour, minute, second and millisecond of the modification time, or of the current time |
| `${rstringalnum=N}` `${rstringalpha=N}` `${rstringdigit=N}` `${ruuidv4}` | Random alphanumeric, letter or digit strings and UUIDs |
| `$1` `$<name>` `$&` `$$` | Regular expression groups and the whole match, as in JavaScript's `String.prototype.replace` |

******

### Script API

******

Scripts use the same engine through `files.batchRename` (1.0.0 design draft; the released documentation is authoritative):

```javascript
let plan = files.batchRename.plan("/sdcard/DCIM/Camera", {
    search: /^IMG_(\d+)/,
    replace: "$YYYY-$MM-$DD_${padding=3}_$1",
    enumerate: true,
});
plan.items.forEach(it => console.log(it.name, "->", it.newName, it.status));

let result = files.batchRename.apply(plan);
files.batchRename.undo(result);
```

- `files.batchRename.plan(paths, options)` computes without renaming and returns a result per item; set `items[i].excluded` and pass the plan to `apply`.
- `files.batchRename(paths, options)` renames in one step and returns the batch ID with a result per item; `files.batchRename.undo(result)` undoes that batch.
- Regular expressions behave exactly like `RegExp` in scripts, and `RegExp` objects can be passed directly. `planAsync`, `applyAsync` and `undoAsync` return promises.

******

### FAQ

******

#### Why is there no launcher icon?

The plugin has no standalone entry. AutoJs6 discovers it after installation, and everything happens in the AutoJs6 file manager or in scripts.

#### Does the plugin need storage permission?

No. The plugin only computes new names from file names; AutoJs6 lists folders and renames files with the access it already has.

#### How does it differ from PowerRename?

Date variables use the modification time, because creation times are unreliable on Android. It adds Unicode normalization, full-width conversion, illegal character cleanup and undoable batch records. Photo EXIF variables are planned for 1.1.0.

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
minimum host build: 5352
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

#### v1.0.0

_2026/10/15_

- `Hint` Development preview: AutoJs6 recognizes the plugin, while the rename screen, the file manager entries and the files.batchRename script API are not delivered yet
- `Hint` Requires AutoJs6 6.8.0 (5352) or later (the exact minimum build is filled in once the host-side changes land)
- `Feature` Plugin skeleton: Wake activation, the Plugin Center identity service, the script capability service and the file manager action service (empty action catalog for now)
- `Feature` README, Plugin Center instructions and changelog in 10 languages, generated from JSON sources
- `Dependency` Attach Jetpack Compose BOM 2026.09.00
- `Dependency` Attach AndroidX Activity Compose 1.12.2
- `Dependency` Attach AndroidX Core KTX 1.15.0
- `Dependency` Attach Kotlin Coroutines 1.11.0

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
