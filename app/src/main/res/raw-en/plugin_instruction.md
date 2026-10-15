The Batch Rename plugin adds batch renaming to AutoJs6, modeled on [PowerRename](https://learn.microsoft.com/windows/powertoys/powerrename) from PowerToys: rename many files and folders at once with search and replace, regular expressions, counters, date and random variables, preview every result before anything changes, and undo afterwards.

> This is a development preview: AutoJs6 recognizes the plugin, but the rename screen, the file manager entries and the script API are not delivered yet. The sections below describe the design goals of 1.0.0; see the roadmap for progress.

### Features

- Search and replace: plain text or regular expressions, all matches or only the first, case sensitive or not, applied to the file name, the extension or the whole name.
- Variables: counters (`${start=1;increment=1;padding=3}`), date variables from the modification time (`$YYYY`, `$MM`, `$DD` and more) and random strings, written as in PowerRename.
- Text cleanup: case conversion, Unicode normalization, full-width to half-width conversion, illegal character cleanup, trimming of leading and trailing spaces and dots, and numbering that continues existing sequences.
- Preview first: every item shows its old and new name, single items can be excluded, and conflicts and invalid names are marked; a conflict can reject the batch, skip the item, add a number or overwrite.
- Undo: AutoJs6 keeps the last 20 rename batches; both the file manager and scripts can undo them after checking that the files have not changed again.

See the [project page](https://github.com/SuperMonster003/AutoJs6-Plugin-Batch-Rename) for progress and design.
