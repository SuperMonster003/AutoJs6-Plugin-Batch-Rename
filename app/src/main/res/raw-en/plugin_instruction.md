The Batch Rename plugin adds batch renaming to AutoJs6, modeled on [PowerRename](https://learn.microsoft.com/windows/powertoys/powerrename) from PowerToys: rename many files and folders at once with search and replace, regular expressions, counters, date and random variables, preview every result before anything changes, and undo afterwards.

### Features

- Search and replace: plain text or JavaScript regular expressions, every match or only the first, case sensitive or not, applied to the name, the extension or both.
- Variables: counters (`${start=1;increment=1;padding=3}`), date variables from the modification time or the current time (`$YYYY`, `$MM`, `$DD` and more) and random strings, written as in PowerRename.
- Name cleanup: case conversion, Unicode normalization, full-width to half-width, replacement of characters that are not allowed, trimming of surrounding spaces, and numbering that continues existing names.
- Preview before renaming: old and new names side by side, uncheck items to exclude them, show only the items that change; when a new name is taken, refuse, skip, add a number or replace the existing entry.
- Undo: AutoJs6 keeps the last 20 rename batches, which can be undone from the file manager and from scripts; undo checks first whether a file was changed again.
- The screen follows the AutoJs6 theme color, dark mode and language, and can remember the last options and recent search and replace texts.

### In the file manager

Once the plugin is installed and enabled, the AutoJs6 file manager offers three entries:

- Multiple selection: select files or folders and tap "Batch rename" in the selection bar (under "More" when several plugin actions are available).
- Folder menu: choose "Batch rename items inside" in the menu of a folder to rename what it contains.
- Rename dialog: while renaming a single file or folder, open the "More" menu in the top right corner of the dialog and choose "Batch rename...".
- Type what to search and replace on the plugin screen and the preview updates at once; uncheck items to exclude them, tap "Rename", and tap "Undo last batch" to restore the old names.

See the [AutoJs6 documentation](https://docs.autojs6.com/#/files?id=m-batchrename) for the script API and the [project page](https://github.com/SuperMonster003/AutoJs6-Plugin-Batch-Rename) for development notes.
