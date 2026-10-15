******

### Release history

******

# v1.0.0

###### 2026/10/15

* `Hint` Requires AutoJs6 6.8.0 (5438) or later
* `Feature` Batch renaming of files and folders in the AutoJs6 file manager (entries: multiple-selection toolbar/folder menu "Batch rename items inside"/"More" menu of the rename dialog)
* `Feature` Search and replace with plain text or JavaScript regular expressions, every match, case sensitivity, and renaming of only the name or the extension
* `Feature` Counters, date variables from the modification time or the current time, random strings and UUIDs
* `Feature` Case conversion, Unicode normalization, full-width to half-width, replacement of characters that are not allowed, trimming of surrounding spaces and numbering that continues existing names
* `Feature` Preview of the new names before renaming, per-item exclusion or only the items that change, and a choice to refuse, skip, add a number or replace the existing entry when a new name is taken
* `Feature` Undo of recent rename batches (AutoJs6 keeps the last 20, shared by the file manager and scripts)
* `Feature` Computes the new names for the files.batchRename script API of AutoJs6
* `Feature` The screen follows the AutoJs6 theme color, dark mode and language, and can remember the last options and recent search and replace texts
* `Dependency` Attach Jetpack Compose BOM 2026.09.00
* `Dependency` Attach AndroidX Activity Compose 1.12.2
* `Dependency` Attach AndroidX Core KTX 1.15.0
* `Dependency` Attach Kotlin Coroutines 1.11.0
* `Dependency` Attach Desugar JDK Libs 2.1.5
* `Dependency` Attach Rhino (the slim AutoJs6 build, for regular expressions)
