<!--suppress HtmlDeprecatedAttribute, HttpUrlsUsage -->

<div align="center">
  <p>
    <picture>
      <source srcset="https://github.com/SuperMonster003/AutoJs6-Plugin-Batch-Rename/blob/master/app/src/main/res/mipmap-night/ic_launcher.png?raw=true" media="(prefers-color-scheme: dark)" />
      <img src="https://github.com/SuperMonster003/AutoJs6-Plugin-Batch-Rename/blob/master/app/src/main/res/mipmap/ic_launcher.png?raw=true" alt="autojs6-plugin-batch-rename-ic-launcher" border="0" width="128" />
    </picture>
  </p>

  <p>使用尋找取代, 正則表示式, 計數器與日期變數批次重新命名檔案與資料夾</p>

  <p>
    <a href="https://github.com/SuperMonster003/AutoJs6-Plugin-Batch-Rename/releases"><img alt="GitHub release (latest by date)" src="https://img.shields.io/github/v/release/SuperMonster003/AutoJs6-Plugin-Batch-Rename?label=Release"/></a>
    <a href="https://github.com/SuperMonster003/AutoJs6-Plugin-Batch-Rename/issues"><img alt="GitHub closed issues" src="https://img.shields.io/github/issues/SuperMonster003/AutoJs6-Plugin-Batch-Rename?color=A24232&label=Issues"/></a>
    <a href="https://github.com/SuperMonster003/AutoJs6-Plugin-Batch-Rename/blob/master/LICENSE"><img alt="GitHub License" src="https://img.shields.io/github/license/SuperMonster003/AutoJs6-Plugin-Batch-Rename?color=534BAE&label=License"/></a>
  </p>
</div>

******

### 語言 (Languages)

******

目前 README.md 支援以下語言:

- [简体中文 [zh-Hans]](https://github.com/SuperMonster003/AutoJs6-Plugin-Batch-Rename/blob/master/.readme/README-zh-Hans.md)
- [繁體中文 (香港) [zh-Hant-HK]](https://github.com/SuperMonster003/AutoJs6-Plugin-Batch-Rename/blob/master/.readme/README-zh-Hant-HK.md)
- 繁體中文 (台灣) [zh-Hant-TW] # 目前
- [English [en]](https://github.com/SuperMonster003/AutoJs6-Plugin-Batch-Rename/blob/master/.readme/README-en.md)
- [Français [fr]](https://github.com/SuperMonster003/AutoJs6-Plugin-Batch-Rename/blob/master/.readme/README-fr.md)
- [Español [es]](https://github.com/SuperMonster003/AutoJs6-Plugin-Batch-Rename/blob/master/.readme/README-es.md)
- [日本語 [ja]](https://github.com/SuperMonster003/AutoJs6-Plugin-Batch-Rename/blob/master/.readme/README-ja.md)
- [한국어 [ko]](https://github.com/SuperMonster003/AutoJs6-Plugin-Batch-Rename/blob/master/.readme/README-ko.md)
- [Русский [ru]](https://github.com/SuperMonster003/AutoJs6-Plugin-Batch-Rename/blob/master/.readme/README-ru.md)
- [العربية [ar]](https://github.com/SuperMonster003/AutoJs6-Plugin-Batch-Rename/blob/master/.readme/README-ar.md)

******

### 簡介

******

Batch Rename 外掛程式為 AutoJs6 提供批次重新命名能力, 功能參照 PowerToys 的 [PowerRename](https://learn.microsoft.com/windows/powertoys/powerrename): 以尋找取代, 正則表示式, 計數器, 日期與隨機變數一次重新命名多個檔案和資料夾, 執行前可完整預覽, 執行後可以復原.

外掛程式是獨立安裝的 APK, 沒有桌面圖示. 它提供兩個入口: 檔案管理員的多選工具列, 資料夾選單與重新命名對話方塊中的 "批次重新命名", 以及指令碼中的 `files.batchRename`. 外掛程式只負責計算新名稱, 實際的重新命名, 衝突檢查與復原記錄由 AutoJs6 完成, 因此外掛程式不需要任何儲存空間權限.

> 目前為開發預覽版: 外掛程式可以被 AutoJs6 識別, 但重新命名介面, 檔案管理員入口與指令碼 API 尚未交付. 下文描述的是 1.0.0 的設計目標, 進度請見開發路線圖.

******

### 功能亮點

******

- 尋找與取代: 支援純文字與正則表示式, 可選擇全部相符或僅取代第一處, 區分或忽略大小寫, 只作用於檔名, 副檔名或完整名稱.
- 變數: 計數器 (`${start=1;increment=1;padding=3}`), 檔案修改時間的日期變數 (`$YYYY`, `$MM`, `$DD` 等) 與隨機字串, 寫法與 PowerRename 一致.
- 文字整理: 大小寫轉換, Unicode 正規化, 全形轉半形, 清除不合法字元, 修剪首尾空格與句點, 從既有序號繼續編號.
- 先預覽再執行: 逐項顯示原名與新名, 可排除個別項目, 衝突與不合法名稱會被標示; 衝突可選擇整批拒絕, 略過, 自動編號或覆寫.
- 可以復原: AutoJs6 保存最近 20 批重新命名記錄, 檔案管理員和指令碼都能復原, 復原前會檢查檔案是否已被再次修改.

******

### 使用方法

******

1. 將 AutoJs6 升級到內部版本號 5352 (6.8.0) 或更新版本.
2. 在 AutoJs6 的外掛程式中心安裝 Batch Rename, 或從 [Releases](https://github.com/SuperMonster003/AutoJs6-Plugin-Batch-Rename/releases) 下載 APK 安裝, 然後在外掛程式中心授權並啟用.
3. 在檔案管理員中長按進入多選後點選 "批次重新命名", 或在指令碼中呼叫 `files.batchRename`.

> 外掛程式只有一個通用安裝檔, 支援 Android 7.0 (API 24) 以上的裝置. 安裝後不會在桌面建立圖示.

******

### 取代變數

******

取代文字中可以使用下列變數:

| 變數 | 意義 |
|---|---|
| `${}` `${start=X;increment=X;padding=X}` | 計數器: 可設定起始值, 間隔與補零位數, 只對實際重新命名的項目遞增 |
| `$YYYY` `$YY` `$MM` `$MMM` `$DD` `$DDDD` `$hh` `$mm` `$ss` `$fff` | 檔案修改時間的年, 月, 日, 時, 分, 秒與毫秒, 也可改用目前時間 |
| `${rstringalnum=N}` `${rstringalpha=N}` `${rstringdigit=N}` `${ruuidv4}` | 隨機英數字串, 字母字串, 數字字串與 UUID |
| `$1` `$<name>` `$&` `$$` | 正則表示式的擷取群組與整個相符結果, 寫法與 JavaScript 的 `String.prototype.replace` 相同 |

******

### 腳本 API

******

指令碼透過 `files.batchRename` 使用同一套能力 (1.0.0 設計草案, 以正式版本的文件為準):

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

- `files.batchRename.plan(paths, options)` 只計算不執行, 傳回逐項結果; 修改 `items[i].excluded` 後可交給 `apply` 執行.
- `files.batchRename(paths, options)` 一次完成, 傳回批次編號與每一項的結果; `files.batchRename.undo(result)` 復原該批次.
- 正則表示式的語意與指令碼中的 `RegExp` 完全一致, 可以直接傳入 `RegExp` 物件. 另有 `planAsync` / `applyAsync` / `undoAsync` 非同步版本.

******

### 常見問題

******

#### 為什麼桌面上沒有外掛程式圖示?

外掛程式沒有獨立介面入口, 安裝後由 AutoJs6 自動探索, 所有操作都在 AutoJs6 的檔案管理員或指令碼中進行.

#### 外掛程式需要儲存空間權限嗎?

不需要. 外掛程式只根據檔名計算新名稱, 讀取資料夾與實際重新命名都由 AutoJs6 使用其既有的權限完成.

#### 和 PowerRename 有什麼不同?

日期變數使用檔案修改時間 (Android 上的建立時間並不可靠), 另外加入了 Unicode 正規化, 全形轉半形, 不合法字元清除與可復原的批次記錄. 相片 EXIF 變數預計在 1.1.0 提供.

******

### 權限與安全

******

外掛程式在設計上盡量縮小權限範圍:

- 最小權限: 只宣告 AutoJs6 外掛程式權限 (`org.autojs.permission.PLUGIN`), 不要求網路或儲存空間權限.
- 宿主執行: 外掛程式只傳回重新命名方案, AutoJs6 在執行前重新驗證每一項, 不會覆寫既有檔案, 除非明確選擇覆寫策略.
- 受保護的入口: 服務與介面入口均受外掛程式權限保護, 第三方應用程式無法直接呼叫.

******

### 外掛介面

******

以下資訊面向 AutoJs6 主程式與外掛開發者, 主程式透過這些識別碼發現外掛並完成能力協商:

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

### 開發路線圖

******

功能規劃, 設計決策與完成情況以可勾選清單維護在 ROADMAP.md 中. 未勾選的項目表示規劃意向, 不代表目前版本的能力.

- [檢視 ROADMAP.md](https://github.com/SuperMonster003/AutoJs6-Plugin-Batch-Rename/blob/master/ROADMAP.md)

******

### 發行歷史

******

#### v1.0.0

_2026/10/15_

- `提示` 開發預覽: 外掛程式可以被 AutoJs6 識別, 重新命名介面, 檔案管理員入口與 files.batchRename 指令碼 API 尚未交付
- `提示` 需要 AutoJs6 6.8.0 (5352) 或更新版本 (準確的最低版本號待宿主端變更落地後回填)
- `新增` 外掛程式骨架: Wake 啟用協定, 外掛程式中心身分服務, 指令碼能力服務與檔案管理員動作服務 (動作目錄暫時為空)
- `新增` 10 種語言的 README, 外掛程式中心說明與更新日誌, 由 JSON 來源檔案統一產生
- `相依性` 附加 Jetpack Compose BOM 版本 2026.09.00
- `相依性` 附加 AndroidX Activity Compose 版本 1.12.2
- `相依性` 附加 AndroidX Core KTX 版本 1.15.0
- `相依性` 附加 Kotlin Coroutines 版本 1.11.0

##### 更多發行歷史可參閱

* [CHANGELOG.md](https://github.com/SuperMonster003/AutoJs6-Plugin-Batch-Rename/blob/master/app/src/main/assets/doc/CHANGELOG-zh-Hant-TW.md)

******

### 建置

******

本節面向希望從原始碼建置外掛的開發者.

```powershell
.\gradlew.bat :app:assembleDebug :app:testDebugUnitTest
.\gradlew.bat :app:appendDigestToReleasedFiles
py .python\generate_markdown.py --check
py .python\generate_icon_studio.py --check
py .python\check_repository.py
```

******

### 授權

******

專案程式碼採用 [Mozilla Public License 2.0](https://github.com/SuperMonster003/AutoJs6-Plugin-Batch-Rename/blob/master/LICENSE). 功能設計參照 PowerToys PowerRename 的公開文件, 不包含其程式碼或資源.

******

### 相關連結

******

- AutoJs6 專案: https://github.com/SuperMonster003/AutoJs6
- PowerToys PowerRename 文件 (功能參照): https://learn.microsoft.com/windows/powertoys/powerrename
