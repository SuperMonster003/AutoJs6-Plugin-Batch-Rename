The Batch Rename plugin adds batch renaming to AutoJs6, modeled on [PowerRename](https://learn.microsoft.com/windows/powertoys/powerrename) from PowerToys: rename many files and folders at once with search and replace, regular expressions, counters, date and random variables, photo metadata and Chinese to pinyin, preview every result before anything changes, and undo afterwards.

### Features

- Search and replace: plain text or JavaScript regular expressions, every match or only the first, case sensitive or not, applied to the name, the extension or both.
- Variables: counters (`${start=1;increment=1;padding=3}`), date variables from the modification time or the current time (`$YYYY`, `$MM`, `$DD` and more) and random strings, written as in PowerRename.
- Chinese to pinyin: "Chinese characters to pinyin" under "Name cleanup" turns the Chinese characters of the new name into pinyin, and the `${pinyin}` variable inserts the pinyin of the original name; tones, syllable separators, capitalization and initials only are optional, and polyphonic characters follow their words.
- Photo metadata: with "Metadata variables" on under "Scope and format", name photos by the date taken, camera, lens, aperture, shutter speed, ISO, GPS and more, 25 values in all (`$DATE_TAKEN_YYYY`, `$CAMERA_MODEL` and so on); rows without metadata are marked.
- Named presets: save the current options as a preset with the "Presets" button in the top bar and load it with one tap; presets can be renamed, deleted, imported and exported through files or the clipboard, and scripts can use them too.
- Name cleanup: case conversion, Unicode normalization, full-width to half-width, replacement of characters that are not allowed, trimming of surrounding spaces, and numbering that continues existing names.
- Preview before renaming: old and new names side by side, uncheck items to exclude them, show only the items that change; when a new name is taken, refuse, skip, add a number or replace the existing entry.
- Undo: AutoJs6 keeps the last 20 rename batches of scripts and of each plugin (50 or 100 if you prefer); they can be undone from the file manager and from scripts, or viewed, undone and deleted on the "Rename history" page of AutoJs6; undo checks first whether a file was changed again.
- The screen follows the AutoJs6 theme color, dark mode and language, and can remember the last options and recent search and replace texts.

### In the file manager

Once the plugin is installed and enabled, the AutoJs6 file manager offers three entries:

- Multiple selection: select files or folders and tap "Batch rename" in the selection bar (under "More" when several plugin actions are available).
- Folder menu: choose "Batch rename items inside" in the menu of a folder to rename what it contains.
- Rename dialog: while renaming a single file or folder, open the "More" menu in the top right corner of the dialog and choose "Batch rename...". "Rename history" at the end of the same menu shows past batches and undoes them.
- Type what to search and replace on the plugin screen and the preview updates at once; uncheck items to exclude them, tap "Rename", and tap "Undo last batch" to restore the old names. "Rename history" at the end of the "More options" menu in the top bar opens the rename history page of AutoJs6.

See the [AutoJs6 documentation](https://docs.autojs6.com/#/files?id=m-batchrename) for the script API and the [project page](https://github.com/SuperMonster003/AutoJs6-Plugin-Batch-Rename) for development notes.
