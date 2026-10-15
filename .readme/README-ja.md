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

******

### 機能ハイライト

******

- 検索と置換: プレーンテキストまたは JavaScript の正規表現, すべて一致または最初の一致のみ, 大文字と小文字の区別の有無, 名前, 拡張子またはその両方に適用.
- 変数: カウンター (`${start=1;increment=1;padding=3}`), 更新日時または現在時刻の日付変数 (`$YYYY`, `$MM`, `$DD` など) とランダム文字列. 書き方は PowerRename と同じです.
- 名前の整理: 大文字と小文字の変換, Unicode 正規化, 全角から半角への変換, 使用できない文字の置き換え, 前後の空白の削除, 既存の名前の番号の続き.
- 実行前のプレビュー: 元の名前と新しい名前を項目ごとに表示し, チェックを外して除外したり, 変更される項目だけを表示したりできます. 新しい名前が使用中のときは, 変更しない, スキップ, 番号を付ける, 既存の項目を置き換える, から選べます.
- 元に戻す: AutoJs6 は最近 20 回分の名前変更を保存し, ファイルマネージャーからもスクリプトからも元に戻せます. 元に戻す前にファイルが再び変更されていないか確認します.
- 画面は AutoJs6 のテーマカラー, ダークモード, 言語に従い, 前回のオプションと最近使った検索/置換テキストを記憶できます.

******

### 使用方法

******

1. AutoJs6 をビルド 5438 (6.8.0) 以降に更新します.
2. AutoJs6 のプラグインセンターから Batch Rename をインストールするか, [Releases](https://github.com/SuperMonster003/AutoJs6-Plugin-Batch-Rename/releases) から APK をダウンロードしてインストールし, プラグインセンターで許可して有効にします.
3. ファイルマネージャーで長押しして項目を選択し "一括で名前を変更" をタップするか, スクリプトで `files.batchRename` を呼び出します.

> プラグインは Android 7.0 (API 24) 以降に対応する単一のユニバーサル APK として配布され, ランチャーにアイコンを追加しません.

******

### ファイルマネージャーでの使い方

******

プラグインをインストールして有効にすると, AutoJs6 のファイルマネージャーに 3 つの入口が加わります:

- 複数選択: ファイルやフォルダーを選択し, 選択バーの "一括で名前変更" をタップします (プラグインの操作が複数あるときは "詳細" の中にあります).
- フォルダーメニュー: フォルダーのメニューで "中の項目を一括で名前変更" を選ぶと, そのフォルダー内の項目の名前を変更します.
- 名前変更ダイアログ: ファイルやフォルダーを 1 つ名前変更するとき, ダイアログ右上の "詳細" メニューから "一括で名前変更..." を選びます.
- プラグインの画面で検索と置換の内容を入力するとプレビューがすぐに更新されます. チェックを外して除外し, "名前を変更" で実行します. その後 "前回の一括変更を元に戻す" で元の名前に戻せます.

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

スクリプトからは AutoJs6 の `files.batchRename` で同じエンジンを使えます. 詳細は [AutoJs6 ドキュメント](https://docs.autojs6.com/#/files?id=m-batchrename) を参照してください:

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

- `files.batchRename.plan(paths, options)` は名前を変更せずに計算し, 各項目の新しい名前と状態を返します. `items[i].excluded` を `true` にしてから `apply` に渡します.
- `files.batchRename(paths, options)` は一度に実行します. `files.batchRename.undo(result)` でそのバッチを元に戻し, `files.batchRename.history()` で最近のバッチ (ファイルマネージャーのバッチを含む) を一覧できます.
- 正規表現はスクリプトの `RegExp` と同じで, `RegExp` オブジェクトをそのまま渡せます. 非同期版の `planAsync` / `applyAsync` / `undoAsync` もあり, 失敗すると `code` 付きの `BatchRenameError` が投げられます.

******

### 互換性

******

- AutoJs6 6.8.0 (ビルド 5438) 以降と Android 7.0 (API 24) 以降が必要です.
- SD カードや USB メモリなどの FAT32 / exFAT ストレージは大文字と小文字を区別せず, `: * ? " < > |` やバックスラッシュなどの文字を使えません. これらを含む新しい名前は使用不可と表示され, "使用できない文字を置き換える" をオンにすると自動で置き換えます.
- 1 回のバッチは最大 2000 項目です. スクリプトで指定したフォルダーの下は最大 32 階層までです.

******

### よくある質問

******

#### ホーム画面にプラグインのアイコンがないのはなぜですか?

プラグインには単独で開く画面がありません. インストール後は AutoJs6 が自動的に見つけ, 操作はすべて AutoJs6 のファイルマネージャーかスクリプトで行います.

#### プラグインにストレージの権限は必要ですか?

必要ありません. プラグインはファイル名から新しい名前を計算するだけで, フォルダーの読み取りと実際の名前変更は AutoJs6 が既存の権限で行います.

#### PowerRename との違いは何ですか?

日付変数は更新日時を使います (Android では作成日時が信頼できないため). また Unicode 正規化, 全角から半角への変換, 使用できない文字の置き換え, 元に戻せるバッチ記録を追加しています. 写真の EXIF 変数は 1.1.0 で提供予定です.

#### 正規表現の書き方は JavaScript と同じですか?

同じです. プラグインは AutoJs6 のスクリプトと同じ Rhino の正規表現エンジンを使い, 置換テキストでは `$1`, `$<name>`, `$&` を使えます. 1 回の一致が 1 秒を超えると, 画面が固まらないよう中止されます.

#### カウンターが増えないのはなぜですか?

まず "カウンター" オプションをオンにしてください (スクリプトでは `enumerate: true`). オフのときは `${}` がそのまま残ります. カウンターは名前が変わる項目だけを数え, 名前が変わらない項目や除外した項目は番号を使いません.

#### 名前変更ダイアログに一括で名前変更が表示されないのはなぜですか?

入口はダイアログ右上の "詳細" メニューにあります. メニューに案内しか表示されない場合は, プラグインが未インストール, プラグインセンターで無効, または AutoJs6 の更新が必要です. 案内に従えば使えるようになります.

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
minimum host build: 5438
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

- `ヒント` AutoJs6 6.8.0 (5438) 以降が必要です
- `機能` AutoJs6 のファイルマネージャーでファイルとフォルダーの名前を一括で変更 (入口: 複数選択ツールバー/フォルダーメニュー "中の項目を一括で名前変更"/名前変更ダイアログの "詳細" メニュー)
- `機能` 検索と置換: プレーンテキストまたは JavaScript の正規表現, すべて一致, 大文字と小文字の区別, 名前または拡張子のみの変更
- `機能` カウンター, 更新日時または現在時刻の日付変数, ランダム文字列と UUID
- `機能` 大文字と小文字の変換, Unicode 正規化, 全角から半角への変換, 使用できない文字の置き換え, 前後の空白の削除, 既存の名前の番号の続き
- `機能` 変更前に新しい名前をプレビューし, 項目ごとの除外や変更される項目だけの表示ができ, 新しい名前が使用中のときは変更しない, スキップ, 番号を付ける, 既存の項目を置き換えるから選べます
- `機能` 最近の一括変更を元に戻す (AutoJs6 が最近 20 回分を保存し, ファイルマネージャーとスクリプトで共有)
- `機能` AutoJs6 の files.batchRename スクリプト API のために新しい名前を計算
- `機能` 画面は AutoJs6 のテーマカラー, ダークモード, 言語に従い, 前回のオプションと最近使った検索/置換テキストを記憶できます
- `依存関係` Jetpack Compose BOM 2026.09.00 を追加
- `依存関係` AndroidX Activity Compose 1.12.2 を追加
- `依存関係` AndroidX Core KTX 1.15.0 を追加
- `依存関係` Kotlin Coroutines 1.11.0 を追加
- `依存関係` Desugar JDK Libs 2.1.5 を追加
- `依存関係` Rhino (AutoJs6 の軽量版, 正規表現用) を追加

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
