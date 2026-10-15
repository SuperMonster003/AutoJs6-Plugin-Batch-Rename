# Staged host artifacts

This directory holds the exact, hash-locked binary artifacts the plugin consumes from the AutoJs6
host. Gradle never resolves them from sibling repositories or from `mavenLocal()`: `app/build.gradle.kts`
reads `../locks/host-api-aars.lock` and fails configuration when a staged AAR is missing, extra,
listed with an invalid digest or different from its locked SHA-256.

## Host contract AARs (lock `../locks/host-api-aars.lock`)

All are **release** artifacts of the AutoJs6 host, consumed with `implementation` because the plugin
runs in its own process and needs the AIDL stubs and constants at run time.

| File | Host module | Content | SHA-256 |
| --- | --- | --- | --- |
| `common-plugin-api.aar` | `plugin-api/common-plugin-api` | `PluginInfo`, `IPluginInfoProvider`, `PluginActions`, `PluginCapabilityKeys`, `AutoJs6HostSettingsContract` | `ed7a71942ffb67255ebad8d8a2c912b01c4a3cdd8aad497baba0687b4072032a` |
| `explorer-action-api.aar` | `plugin-api/explorer-action-api` | Explorer Action protocol v24: `IExplorerActionPlugin`, `IExplorerActionHostSession`, catalog keys and values | `fd56dddd35351be5a9e598dad80f5d0aaa45575452639cf7ed7ef53de49f3f9b` |
| `batch-rename-api.aar` | `plugin-api/batch-rename-api` | Batch Rename contract v1: `IBatchRenamePlugin`, discovery literals, request / parent / item / option / result keys, value sets, error codes and limits | `315e37b94297582fbb975da71b163fcb3ac78d2a870f03b5847c5a42b1d207cb` |

Provenance (2026-10-15): all were assembled with `:plugin-api:<module>:assembleRelease`.
`common-plugin-api` and `explorer-action-api` come from the host snapshot `6c151c900e` (6.8.0,
versionCode 5352, roadmap P0.1); `common-plugin-api` last changed at host commit `aa8ebaf72e`,
`explorer-action-api` last changed at host commit `ccddb727b7` (protocol v24) and is byte-identical to
the artifact staged by 3-Maple Image 3.2.0. `batch-rename-api` comes from host commit `aa4fd4f6d`
(contract v1, roadmap P1.1); the module depends only on `common-plugin-api`.

Planned changes (see `../ROADMAP.md`):

- P1.2 replaces `explorer-action-api.aar` with the protocol v25 build (`targetRename`, the rename
  dialog placement and the session rename transactions).

## Re-staging

1. Build the release AAR in the host repository, for example
   `.\gradlew.bat :plugin-api:explorer-action-api:assembleRelease`, from a commit that is recorded here.
2. Copy `plugin-api/<module>/build/outputs/aar/<module>-release.aar` to this directory as `<module>.aar`.
3. Update the SHA-256 in `../locks/host-api-aars.lock`, the table above and `../THIRD_PARTY_NOTICES.md`
   when the artifact set changes. Never stage debug AARs.
