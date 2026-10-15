<!--suppress HtmlDeprecatedAttribute, HttpUrlsUsage -->

<div align="center">
  <p>
    <picture>
      <source srcset="https://github.com/SuperMonster003/AutoJs6-Plugin-Batch-Rename/blob/master/app/src/main/res/mipmap-night/ic_launcher.png?raw=true" media="(prefers-color-scheme: dark)" />
      <img src="https://github.com/SuperMonster003/AutoJs6-Plugin-Batch-Rename/blob/master/app/src/main/res/mipmap/ic_launcher.png?raw=true" alt="autojs6-plugin-batch-rename-ic-launcher" border="0" width="128" />
    </picture>
  </p>

  <p>検索と置換, 正規表現, カウンター, 日付変数でファイルとフォルダーの名前を一括変更</p>

  <p>
    <a href="https://github.com/SuperMonster003/AutoJs6-Plugin-Batch-Rename/releases"><img alt="GitHub release (latest by date)" src="https://img.shields.io/github/v/release/SuperMonster003/AutoJs6-Plugin-Batch-Rename?label=Release"/></a>
    <a href="https://github.com/SuperMonster003/AutoJs6-Plugin-Batch-Rename/issues"><img alt="GitHub closed issues" src="https://img.shields.io/github/issues/SuperMonster003/AutoJs6-Plugin-Batch-Rename?color=A24232&label=Issues"/></a>
    <a href="https://github.com/SuperMonster003/AutoJs6-Plugin-Batch-Rename/blob/master/LICENSE"><img alt="GitHub License" src="https://img.shields.io/github/license/SuperMonster003/AutoJs6-Plugin-Batch-Rename?color=534BAE&label=License"/></a>
  </p>
</div>

******

### 言語 (Languages)

******

現在の README.md は次の言語に対応しています:

- [简体中文 [zh-Hans]](https://github.com/SuperMonster003/AutoJs6-Plugin-Batch-Rename/blob/master/.readme/README-zh-Hans.md)
- [繁體中文 (香港) [zh-Hant-HK]](https://github.com/SuperMonster003/AutoJs6-Plugin-Batch-Rename/blob/master/.readme/README-zh-Hant-HK.md)
- [繁體中文 (台灣) [zh-Hant-TW]](https://github.com/SuperMonster003/AutoJs6-Plugin-Batch-Rename/blob/master/.readme/README-zh-Hant-TW.md)
- [English [en]](https://github.com/SuperMonster003/AutoJs6-Plugin-Batch-Rename/blob/master/.readme/README-en.md)
- [Français [fr]](https://github.com/SuperMonster003/AutoJs6-Plugin-Batch-Rename/blob/master/.readme/README-fr.md)
- [Español [es]](https://github.com/SuperMonster003/AutoJs6-Plugin-Batch-Rename/blob/master/.readme/README-es.md)
- 日本語 [ja] # 現在
- [한국어 [ko]](https://github.com/SuperMonster003/AutoJs6-Plugin-Batch-Rename/blob/master/.readme/README-ko.md)
- [Русский [ru]](https://github.com/SuperMonster003/AutoJs6-Plugin-Batch-Rename/blob/master/.readme/README-ru.md)
- [العربية [ar]](https://github.com/SuperMonster003/AutoJs6-Plugin-Batch-Rename/blob/master/.readme/README-ar.md)

******

### 概要

******

Batch Rename プラグインは, PowerToys の [PowerRename](https://learn.microsoft.com/windows/powertoys/powerrename) を参考にした一括リネーム機能を AutoJs6 に追加します. 検索と置換, 正規表現, カウンター, 日付変数, ランダム文字列で多数のファイルとフォルダーの名前を一度に変更でき, 変更前にすべての結果をプレビューし, 変更後に元に戻せます.

プラグインは個別にインストールする APK で, ランチャーアイコンはありません. 入口は 2 つあります: ファイルマネージャーの複数選択ツールバー, フォルダーメニュー, 名前の変更ダイアログにある "一括で名前を変更" と, スクリプトの `files.batchRename` です. プラグインは新しい名前を計算するだけで, 実際の名前の変更, 競合チェック, 取り消し履歴は AutoJs6 が行うため, プラグインにストレージ権限は不要です.

> 現在は開発プレビューです: AutoJs6 はプラグインを認識しますが, 名前変更画面, ファイルマネージャーの入口, スクリプト API はまだ提供されていません. 以下は 1.0.0 の設計目標で, 進捗はロードマップを参照してください.

******

### 機能ハイライト

******

- 検索と置換: プレーンテキストまたは正規表現, すべての一致または最初の一致のみ, 大文字と小文字の区別の有無を選べ, ファイル名, 拡張子, 名前全体のいずれかに適用できます.
- 変数: カウンター (`${start=1;increment=1;padding=3}`), 更新日時の日付変数 (`$YYYY`, `$MM`, `$DD` など), ランダム文字列を PowerRename と同じ書式で使えます.
- テキストの整理: 大文字小文字の変換, Unicode 正規化, 全角から半角への変換, 使用できない文字の除去, 先頭と末尾の空白とピリオドの削除, 既存の連番からの続き番号に対応します.
- 先にプレビュー: 各項目の旧名と新名を表示し, 個別に除外でき, 競合や無効な名前は強調表示されます. 競合時は一括拒否, スキップ, 自動採番, 上書きから選べます.
- 元に戻す: AutoJs6 は直近 20 回分の一括リネームを記録し, ファイルが再び変更されていないことを確認したうえで, ファイルマネージャーとスクリプトの両方から元に戻せます.

******

### 使用方法

******

1. AutoJs6 をビルド 5431 (6.8.0) 以降に更新します.
2. AutoJs6 のプラグインセンターから Batch Rename をインストールするか, [Releases](https://github.com/SuperMonster003/AutoJs6-Plugin-Batch-Rename/releases) から APK をダウンロードしてインストールし, プラグインセンターで許可して有効にします.
3. ファイルマネージャーで長押しして項目を選択し "一括で名前を変更" をタップするか, スクリプトで `files.batchRename` を呼び出します.

> プラグインは Android 7.0 (API 24) 以降に対応する単一のユニバーサル APK として配布され, ランチャーにアイコンを追加しません.

******

### 置換変数

******

置換テキストでは次の変数を使えます:

| 変数 | 意味 |
|---|---|
| `${}` `${start=X;increment=X;padding=X}` | 開始値, 増分, ゼロ埋め桁数を指定できるカウンター. 実際に名前を変更する項目でのみ進みます |
| `$YYYY` `$YY` `$MM` `$MMM` `$DD` `$DDDD` `$hh` `$mm` `$ss` `$fff` | 更新日時 (または現在時刻) の年, 月, 日, 時, 分, 秒, ミリ秒 |
| `${rstringalnum=N}` `${rstringalpha=N}` `${rstringdigit=N}` `${ruuidv4}` | ランダムな英数字列, 英字列, 数字列と UUID |
| `$1` `$<name>` `$&` `$$` | 正規表現のキャプチャグループと一致全体. JavaScript の `String.prototype.replace` と同じ書式です |

******

### スクリプト API

******

スクリプトからは `files.batchRename` で同じエンジンを使えます (1.0.0 の設計案であり, 公開版のドキュメントが優先されます):

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

- `files.batchRename.plan(paths, options)` は名前を変更せずに計算し, 項目ごとの結果を返します. `items[i].excluded` を変更してから `apply` に渡せます.
- `files.batchRename(paths, options)` は 1 ステップで名前を変更し, バッチ ID と項目ごとの結果を返します. `files.batchRename.undo(result)` でそのバッチを元に戻せます.
- 正規表現の動作はスクリプトの `RegExp` と完全に同じで, `RegExp` オブジェクトをそのまま渡せます. `planAsync`, `applyAsync`, `undoAsync` は Promise を返します.

******

### よくある質問

******

#### ランチャーにアイコンがないのはなぜですか?

プラグインには独立した入口がありません. インストール後に AutoJs6 が自動で検出し, すべての操作は AutoJs6 のファイルマネージャーまたはスクリプトで行います.

#### ストレージ権限は必要ですか?

不要です. プラグインはファイル名から新しい名前を計算するだけで, フォルダーの読み取りと実際の名前の変更は AutoJs6 が既存の権限で行います.

#### PowerRename との違いは何ですか?

Android では作成日時が信頼できないため, 日付変数は更新日時を使います. また Unicode 正規化, 全角変換, 使用できない文字の除去, 元に戻せる一括履歴を追加しています. 写真の EXIF 変数は 1.1.0 で提供予定です.

******

### 権限とセキュリティ

******

プラグインは権限を最小限に抑えています:

- 最小権限: マニフェストは AutoJs6 プラグイン権限 (`org.autojs.permission.PLUGIN`) のみを宣言し, ネットワークやストレージの権限は要求しません.
- ホストによる実行: プラグインは名前変更の計画を返すだけで, AutoJs6 が変更前に各項目を再検証します. 上書きポリシーを明示的に選ばない限り, 既存のファイルを上書きしません.
- 保護された入口: サービスと名前変更画面にはプラグイン権限が必要で, 他のアプリから直接呼び出すことはできません.

******

### プラグインインターフェース

******

以下の情報は AutoJs6 ホストとプラグイン開発者向けです. ホストはこれらの識別子でプラグインを検出し, 機能ネゴシエーションを行います:

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

### 開発ロードマップ

******

計画, 設計上の決定, 進捗は ROADMAP.md のチェックリストで管理しています. 未チェックの項目は計画であり, 現在のバージョンの機能ではありません.

- [ROADMAP.md を見る](https://github.com/SuperMonster003/AutoJs6-Plugin-Batch-Rename/blob/master/ROADMAP.md)

******

### リリース履歴

******

#### v1.0.0

_2026/10/15_

- `ヒント` 開発プレビュー: AutoJs6 はプラグインを認識しますが, 名前変更画面, ファイルマネージャーの入口, files.batchRename スクリプト API はまだ提供されていません
- `ヒント` AutoJs6 6.8.0 (5431) 以降が必要です (正確な最小ビルド番号はホスト側の変更が取り込まれた後に補完されます)
- `機能` プラグインの骨格: Wake による有効化, プラグインセンター向け識別サービス, スクリプト向け機能サービス, ファイルマネージャー向けアクションサービス (アクション一覧は現在空)
- `機能` JSON ソースから生成される 10 言語の README, プラグインセンターの説明, 更新履歴
- `依存関係` Jetpack Compose BOM 2026.09.00 を追加
- `依存関係` AndroidX Activity Compose 1.12.2 を追加
- `依存関係` AndroidX Core KTX 1.15.0 を追加
- `依存関係` Kotlin Coroutines 1.11.0 を追加

##### その他のリリース履歴は以下を参照

* [CHANGELOG.md](https://github.com/SuperMonster003/AutoJs6-Plugin-Batch-Rename/blob/master/app/src/main/assets/doc/CHANGELOG-ja.md)

******

### ビルド

******

この節はソースからプラグインをビルドしたい開発者向けです.

```powershell
.\gradlew.bat :app:assembleDebug :app:testDebugUnitTest
.\gradlew.bat :app:appendDigestToReleasedFiles
py .python\generate_markdown.py --check
py .python\generate_icon_studio.py --check
py .python\check_repository.py
```

******

### ライセンス

******

このプロジェクトは [Mozilla Public License 2.0](https://github.com/SuperMonster003/AutoJs6-Plugin-Batch-Rename/blob/master/LICENSE) で提供されます. 機能設計は PowerToys PowerRename の公開ドキュメントに基づいており, そのコードやリソースは含みません.

******

### 関連リンク

******

- AutoJs6 プロジェクト: https://github.com/SuperMonster003/AutoJs6
- PowerToys PowerRename ドキュメント (機能の参考): https://learn.microsoft.com/windows/powertoys/powerrename
