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
| `explorer-action-api.aar` | `plugin-api/explorer-action-api` | Explorer Action protocol v25: `IExplorerActionPlugin`, `IExplorerActionHostSession` with the rename transactions, catalog keys and values | `df4bcec8f68f502476c2eb41c8ff3ef513f30abfd28e06b94d1d235041cf3855` |
| `batch-rename-api.aar` | `plugin-api/batch-rename-api` | Batch Rename contract v1: `IBatchRenamePlugin`, discovery literals, request / parent / item / option / result keys, value sets, error codes and limits | `315e37b94297582fbb975da71b163fcb3ac78d2a870f03b5847c5a42b1d207cb` |

Provenance (2026-10-15): all were assembled with `:plugin-api:<module>:assembleRelease`.
`common-plugin-api` comes from the host snapshot `6c151c900e` (6.8.0, versionCode 5352, roadmap P0.1)
and last changed at host commit `aa8ebaf72e`. `batch-rename-api` comes from host commit `aa4fd4f6d`
(contract v1, roadmap P1.1); the module depends only on `common-plugin-api`. `explorer-action-api`
comes from host commit `95c9f0455` (protocol v25, roadmap P1.2): the `targetRename` catalog key, the
rename dialog placement and the five appended session transactions. Every v1-v24 transaction code is
unchanged, and the plugin only implements `IExplorerActionPlugin`, whose order did not change.

## Re-staging

1. Build the release AAR in the host repository, for example
   `.\gradlew.bat :plugin-api:explorer-action-api:assembleRelease`, from a commit that is recorded here.
2. Copy `plugin-api/<module>/build/outputs/aar/<module>-release.aar` to this directory as `<module>.aar`.
3. Update the SHA-256 in `../locks/host-api-aars.lock`, the table above and `../THIRD_PARTY_NOTICES.md`
   when the artifact set changes. Never stage debug AARs.
