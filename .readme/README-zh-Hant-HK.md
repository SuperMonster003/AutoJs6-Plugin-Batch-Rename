<!--suppress HtmlDeprecatedAttribute, HttpUrlsUsage -->

<div align="center">
  <p>
    <picture>
      <source srcset="https://github.com/SuperMonster003/AutoJs6-Plugin-Batch-Rename/blob/master/app/src/main/res/mipmap-night/ic_launcher.png?raw=true" media="(prefers-color-scheme: dark)" />
      <img src="https://github.com/SuperMonster003/AutoJs6-Plugin-Batch-Rename/blob/master/app/src/main/res/mipmap/ic_launcher.png?raw=true" alt="autojs6-plugin-batch-rename-ic-launcher" border="0" width="128" />
    </picture>
  </p>

  <p>使用尋找取代, 正則表達式, 計數器與日期變數批量重新命名檔案與資料夾</p>

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
- 繁體中文 (香港) [zh-Hant-HK] # 目前
- [繁體中文 (台灣) [zh-Hant-TW]](https://github.com/SuperMonster003/AutoJs6-Plugin-Batch-Rename/blob/master/.readme/README-zh-Hant-TW.md)
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

Batch Rename 插件為 AutoJs6 提供批量重新命名能力, 功能參照 PowerToys 的 [PowerRename](https://learn.microsoft.com/windows/powertoys/powerrename): 以尋找取代, 正則表達式, 計數器, 日期與隨機變數一次重新命名多個檔案和資料夾, 執行前可完整預覽, 執行後可以撤銷.

插件是獨立安裝的 APK, 沒有桌面圖示. 它提供兩個入口: 檔案管理器的多選工具列, 資料夾選單與重新命名對話框中的 "批量重新命名", 以及腳本中的 `files.batchRename`. 插件只負責計算新名稱, 實際的重新命名, 衝突檢查與撤銷記錄由 AutoJs6 完成, 因此插件不需要任何儲存權限.

******

### 功能亮點

******

- 尋找與取代: 支援純文字或 JavaScript 正規表達式, 可選全部配對或只取代第一處, 區分或忽略大小寫, 只作用於名稱, 副檔名或兩者.
- 變數: 計數器 (`${start=1;increment=1;padding=3}`), 取檔案修改時間或目前時間的日期變數 (`$YYYY`, `$MM`, `$DD` 等) 與隨機字串, 寫法與 PowerRename 一致.
- 名稱整理: 大小寫轉換, Unicode 正規化, 全形轉半形, 取代不允許的字元, 去除首尾空格, 以及接續既有名稱的編號.
- 先預覽再執行: 逐項顯示原名與新名, 可取消勾選排除項目, 可只顯示改名項目; 新名稱被佔用時可選擇不重新命名, 略過, 加上編號或取代既有項目.
- 可以復原: AutoJs6 保存最近 20 個重新命名批次, 檔案管理器與腳本中都能復原, 復原前會檢查檔案是否已再次修改.
- 介面跟隨 AutoJs6 的主題色, 夜間模式與語言, 可記住上次選項與最近使用的尋找和取代文字.

******

### 使用方法

******

1. 將 AutoJs6 升級到內部版本號 5431 (6.8.0) 或更高版本.
2. 在 AutoJs6 的插件中心安裝 Batch Rename, 或從 [Releases](https://github.com/SuperMonster003/AutoJs6-Plugin-Batch-Rename/releases) 下載 APK 安裝, 然後在插件中心授權並啟用.
3. 在檔案管理器中長按進入多選後點擊 "批量重新命名", 或在腳本中呼叫 `files.batchRename`.

> 插件只有一個通用安裝包, 支援 Android 7.0 (API 24) 及以上的裝置. 安裝後不會在桌面建立圖示.

******

### 在檔案管理器中使用

******

安裝並啟用插件後, AutoJs6 檔案管理器中有三處入口:

- 多選: 選取若干檔案或資料夾, 在選取列中點 "批次重新命名" (有多個插件動作時位於 "更多" 中).
- 資料夾選單: 在資料夾的選單中選擇 "批次重新命名其中的項目", 為該資料夾中的項目改名.
- 重新命名對話框: 重新命名單一檔案或資料夾時, 點對話框右上角的 "更多" 選單, 選擇 "批次重新命名...".
- 在插件介面中輸入尋找與取代內容, 預覽會即時更新; 取消勾選可排除項目, 點 "重新命名" 執行, 之後可以點 "復原上一批" 恢復原名.

******

### 取代變數

******

取代文字中可以使用以下變數:

| 變數 | 含義 |
|---|---|
| `${}` `${start=X;increment=X;padding=X}` | 計數器: 可設定起始值, 步長與補零位數, 只對實際重新命名的項目遞增 |
| `$YYYY` `$YY` `$MM` `$MMM` `$DD` `$DDDD` `$hh` `$mm` `$ss` `$fff` | 檔案修改時間的年, 月, 日, 時, 分, 秒與毫秒, 也可改用目前時間 |
| `${rstringalnum=N}` `${rstringalpha=N}` `${rstringdigit=N}` `${ruuidv4}` | 隨機英數字串, 字母串, 數字串與 UUID |
| `$1` `$<name>` `$&` `$$` | 正則擷取組與整個符合結果, 寫法與 JavaScript 的 `String.prototype.replace` 相同 |

******

### 腳本 API

******

腳本透過 AutoJs6 的 `files.batchRename` 使用同一個引擎, 完整說明見 [AutoJs6 文件](https://docs.autojs6.com/#/files?id=m-batchrename):

```javascript
let plan = files.batchRename.plan("/sdcard/DCIM/Camera", {
    search: /^IMG_(\d+)/,
    replace: "$YYYY-$MM-$DD_${padding=3}_$1",
    enumerate: true,
    includeFolders: false,
});
plan.items.forEach(it => console.log(it.name, "->", it.newName, it.status));

if (!plan.rejected) {
    let result = files.batchRename.apply(plan);
    files.batchRename.undo(result);
}
```

- `files.batchRename.plan(paths, options)` 只計算不改名, 傳回每一項的新名稱與狀態; 把 `items[i].excluded` 設為 `true` 後再交給 `apply` 執行.
- `files.batchRename(paths, options)` 一步完成; `files.batchRename.undo(result)` 復原該批次, `files.batchRename.history()` 列出最近的批次 (包括檔案管理器中的批次).
- 正規表達式與腳本中的 `RegExp` 一致, 可以直接傳入 `RegExp` 物件. 另有 `planAsync` / `applyAsync` / `undoAsync` 非同步形態; 失敗時拋出帶有 `code` 的 `BatchRenameError`.

******

### 相容性

******

- 需要 AutoJs6 6.8.0 (建置 5431) 或更新版本, 以及 Android 7.0 (API 24) 或更新版本.
- SD 卡或 USB 手指等 FAT32 / exFAT 儲存空間不區分大小寫, 也不允許 `: * ? " < > |` 與反斜線等字元; 含這些字元的新名稱會被標為不可用, 開啟 "取代不允許的字元" 可以自動取代.
- 一個批次最多 2000 個項目; 腳本中指定的資料夾之下最多 32 層.

******

### 常見問題

******

#### 為甚麼桌面上沒有插件圖示?

插件沒有獨立的介面入口, 安裝後由 AutoJs6 自動發現, 全部操作都在 AutoJs6 的檔案管理器或腳本中進行.

#### 插件需要儲存權限嗎?

不需要. 插件只根據檔名計算新名稱, 讀取目錄與實際重新命名都由 AutoJs6 使用其既有的權限完成.

#### 和 PowerRename 有甚麼不同?

日期變數使用檔案修改時間 (Android 上的建立時間並不可靠), 另外增加了 Unicode 正規化, 全形轉半形, 不允許字元的取代與可復原的批次記錄. 相片 EXIF 變數預計在 1.1.0 提供.

#### 正規表達式的寫法和 JavaScript 一樣嗎?

一樣. 插件使用與 AutoJs6 腳本相同的 Rhino 正規表達式引擎, 取代文字中可以使用 `$1`, `$<name>` 與 `$&`. 單次配對超過 1 秒會被中止, 以免介面卡住.

#### 為甚麼計數器沒有遞增?

需要先開啟 "計數器" 選項 (腳本中為 `enumerate: true`), 否則 `${}` 會原樣保留. 計數器只為新名稱有變化的項目計數, 名稱不變或被排除的項目不佔用編號.

#### 為甚麼重新命名對話框中看不到批次重新命名?

入口位於對話框右上角的 "更多" 選單. 如果選單中只有引導項目, 表示插件尚未安裝, 未在插件中心啟用, 或需要更新 AutoJs6; 按引導處理後即可使用.

******

### 權限與安全

******

插件在設計上盡量收窄權限面:

- 最小權限: 只聲明 AutoJs6 插件權限 (`org.autojs.permission.PLUGIN`), 不申請網絡或儲存權限.
- 宿主執行: 插件只傳回重新命名方案, AutoJs6 在執行前重新校驗每一項, 不會覆蓋已有檔案, 除非明確選擇覆蓋策略.
- 受保護的入口: 服務與介面入口均受插件權限保護, 第三方應用程式無法直接呼叫.

******

### 外掛接口

******

以下資訊面向 AutoJs6 宿主與外掛開發者, 宿主透過這些標識發現外掛並完成能力協商:

```text
application id: io.github.supermonster003.autojs6.plugin.batch.rename
plugin id: batch-rename
engine: batch-rename
variant: default
info service: org.autojs.plugin.INFO (category batch-rename)
capability service: org.autojs.plugin.BATCH_RENAME (category batch-rename)
file manager service: org.autojs.plugin.EXPLORER_ACTION
wake action: org.autojs.plugin.action.WAKE
minimum host build: 5431
native library: libandroidx.graphics.path.so (Jetpack Compose, all ABIs)
```

******

### 開發路線圖

******

功能規劃, 設計決策與完成情況以可勾選清單維護在 ROADMAP.md 中. 未勾選條目表示規劃意向, 不代表目前版本能力.

- [查看 ROADMAP.md](https://github.com/SuperMonster003/AutoJs6-Plugin-Batch-Rename/blob/master/ROADMAP.md)

******

### 發行歷史

******

#### v1.0.0

_2026/10/15_

- `提示` 需要 AutoJs6 6.8.0 (5431) 或更高版本
- `新增` 在 AutoJs6 檔案管理器中批次重新命名檔案與資料夾 (入口: 多選工具列/資料夾選單 "批次重新命名其中的項目"/重新命名對話框 "更多" 選單)
- `新增` 尋找與取代, 支援純文字或 JavaScript 正規表達式, 全部配對, 區分大小寫, 只改名稱或副檔名
- `新增` 計數器, 取檔案修改時間或目前時間的日期變數, 隨機字串與 UUID
- `新增` 大小寫轉換, Unicode 正規化, 全形轉半形, 取代不允許的字元, 去除首尾空格與接續既有名稱的編號
- `新增` 改名前預覽新名稱, 可逐項排除或只顯示改名項目, 新名稱被佔用時可選擇不重新命名, 略過, 加上編號或取代既有項目
- `新增` 復原最近的重新命名批次 (AutoJs6 保存最近 20 批, 檔案管理器與腳本共用)
- `新增` 為 AutoJs6 的 files.batchRename 腳本 API 計算新名稱
- `新增` 介面跟隨 AutoJs6 的主題色, 夜間模式與語言, 可記住上次選項與最近使用的尋找和取代文字
- `依賴` 附加 Jetpack Compose BOM 版本 2026.09.00
- `依賴` 附加 AndroidX Activity Compose 版本 1.12.2
- `依賴` 附加 AndroidX Core KTX 版本 1.15.0
- `依賴` 附加 Kotlin Coroutines 版本 1.11.0
- `依賴` 附加 Desugar JDK Libs 版本 2.1.5
- `依賴` 附加 Rhino (AutoJs6 精簡版, 用於正規表達式)

##### 更多發行歷史可參閱

* [CHANGELOG.md](https://github.com/SuperMonster003/AutoJs6-Plugin-Batch-Rename/blob/master/app/src/main/assets/doc/CHANGELOG-zh-Hant-HK.md)

******

### 構建

******

本節面向希望從源碼構建外掛的開發者.

```powershell
.\gradlew.bat :app:assembleDebug :app:testDebugUnitTest
.\gradlew.bat :app:appendDigestToReleasedFiles
py .python\generate_markdown.py --check
py .python\generate_icon_studio.py --check
py .python\check_repository.py
```

******

### 許可

******

專案程式碼使用 [Mozilla Public License 2.0](https://github.com/SuperMonster003/AutoJs6-Plugin-Batch-Rename/blob/master/LICENSE). 功能設計參照 PowerToys PowerRename 的公開文件, 不包含其程式碼或資源.

******

### 相關連結

******

- AutoJs6 項目: https://github.com/SuperMonster003/AutoJs6
- PowerToys PowerRename 文件 (功能參照): https://learn.microsoft.com/windows/powertoys/powerrename
