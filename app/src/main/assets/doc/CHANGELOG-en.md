******

### Release history

******

# v1.1.0

###### 2026/10/16

* `Hint` Requires AutoJs6 6.8.0 (5455) or later
* `Feature` Chinese to pinyin: "Chinese characters to pinyin" under "Name cleanup" and the `${pinyin}` variable, with optional tones, syllable separators, capitalization and initials only; polyphonic characters follow their words
* `Feature` Photo metadata variables: name photos by the date taken, camera, lens, aperture, shutter speed, ISO, GPS and more, 25 values in all; rows without metadata are marked (turn on "Metadata variables" under "Scope and format")
* `Feature` Named presets: save the current options as a preset and load it with one tap, rename, delete, import and export presets through files or the clipboard (entry: "Presets" button in the top bar)
* `Feature` Rename history: view, search, undo or delete the rename batches of the file manager and of scripts in AutoJs6, and set how many batches are kept (entry: "More options" in the top bar > "Rename history")
* `Feature` Chinese to pinyin, photo metadata, named presets and name previews without files for the files.batchRename script API of AutoJs6
* `Improvement` New plugin icon
* `Improvement` "Clear history" in the "More options" menu of the top bar is now "Clear recently used", so it is not mistaken for "Rename history"

# v1.0.0

###### 2026/10/16

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
