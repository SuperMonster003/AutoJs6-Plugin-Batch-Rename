# Third-party notices

Batch Rename is licensed under the Mozilla Public License 2.0 (see `LICENSE`). The APK bundles the
components below; their licenses apply to those components only.

## AutoJs6 host contract libraries

| Artifact | Source | License |
| --- | --- | --- |
| `libs/common-plugin-api.aar` | AutoJs6 host module `plugin-api/common-plugin-api` (https://github.com/SuperMonster003/AutoJs6) | MPL-2.0 |
| `libs/explorer-action-api.aar` | AutoJs6 host module `plugin-api/explorer-action-api` (https://github.com/SuperMonster003/AutoJs6) | MPL-2.0 |

Provenance and SHA-256 digests are recorded in `locks/host-api-aars.lock` and `libs/README.md`.

## AndroidX and Kotlin libraries (Maven)

| Library | Version | License |
| --- | --- | --- |
| Jetpack Compose (BOM `androidx.compose:compose-bom`: runtime, ui, foundation, material3, ui-tooling-preview) | 2026.09.00 | Apache-2.0 |
| AndroidX Activity Compose (`androidx.activity:activity-compose`) | 1.12.2 | Apache-2.0 |
| AndroidX Core KTX (`androidx.core:core-ktx`) | 1.15.0 | Apache-2.0 |
| Kotlin Coroutines (`org.jetbrains.kotlinx:kotlinx-coroutines-android`) | 1.11.0 | Apache-2.0 |
| Kotlin standard library (`org.jetbrains.kotlin:kotlin-stdlib`, added by the Android Gradle Plugin) | follows the build's Kotlin version | Apache-2.0 |

Jetpack Compose `ui-graphics` depends on `androidx.graphics:graphics-path`, whose
`libandroidx.graphics.path.so` (Apache-2.0) is packaged for arm64-v8a, armeabi-v7a, x86_64 and x86.

The Apache License 2.0 text is available at https://www.apache.org/licenses/LICENSE-2.0.

## Feature reference

The feature set, options and replacement variable syntax follow the public documentation of
Microsoft PowerToys PowerRename (https://learn.microsoft.com/windows/powertoys/powerrename). No
PowerToys code, text or resources are included in this repository.
