<!--suppress HtmlDeprecatedAttribute, HttpUrlsUsage -->

<div align="center">
  <p>
    <picture>
      <source srcset="https://github.com/SuperMonster003/AutoJs6-Plugin-Batch-Rename/blob/master/app/src/main/res/mipmap-night/ic_launcher.png?raw=true" media="(prefers-color-scheme: dark)" />
      <img src="https://github.com/SuperMonster003/AutoJs6-Plugin-Batch-Rename/blob/master/app/src/main/res/mipmap/ic_launcher.png?raw=true" alt="autojs6-plugin-batch-rename-ic-launcher" border="0" width="128" />
    </picture>
  </p>

  <p>使用查找替换, 正则表达式, 计数器与日期变量批量重命名文件与文件夹</p>

  <p>
    <a href="https://github.com/SuperMonster003/AutoJs6-Plugin-Batch-Rename/releases"><img alt="GitHub release (latest by date)" src="https://img.shields.io/github/v/release/SuperMonster003/AutoJs6-Plugin-Batch-Rename?label=Release"/></a>
    <a href="https://github.com/SuperMonster003/AutoJs6-Plugin-Batch-Rename/issues"><img alt="GitHub closed issues" src="https://img.shields.io/github/issues/SuperMonster003/AutoJs6-Plugin-Batch-Rename?color=A24232&label=Issues"/></a>
    <a href="https://github.com/SuperMonster003/AutoJs6-Plugin-Batch-Rename/blob/master/LICENSE"><img alt="GitHub License" src="https://img.shields.io/github/license/SuperMonster003/AutoJs6-Plugin-Batch-Rename?color=534BAE&label=License"/></a>
  </p>
</div>

******

### 语言 (Languages)

******

当前 README.md 支持以下语言:

- 简体中文 [zh-Hans] # 当前
- [繁體中文 (香港) [zh-Hant-HK]](https://github.com/SuperMonster003/AutoJs6-Plugin-Batch-Rename/blob/master/.readme/README-zh-Hant-HK.md)
- [繁體中文 (台灣) [zh-Hant-TW]](https://github.com/SuperMonster003/AutoJs6-Plugin-Batch-Rename/blob/master/.readme/README-zh-Hant-TW.md)
- [English [en]](https://github.com/SuperMonster003/AutoJs6-Plugin-Batch-Rename/blob/master/.readme/README-en.md)
- [Français [fr]](https://github.com/SuperMonster003/AutoJs6-Plugin-Batch-Rename/blob/master/.readme/README-fr.md)
- [Español [es]](https://github.com/SuperMonster003/AutoJs6-Plugin-Batch-Rename/blob/master/.readme/README-es.md)
- [日本語 [ja]](https://github.com/SuperMonster003/AutoJs6-Plugin-Batch-Rename/blob/master/.readme/README-ja.md)
- [한국어 [ko]](https://github.com/SuperMonster003/AutoJs6-Plugin-Batch-Rename/blob/master/.readme/README-ko.md)
- [Русский [ru]](https://github.com/SuperMonster003/AutoJs6-Plugin-Batch-Rename/blob/master/.readme/README-ru.md)
- [العربية [ar]](https://github.com/SuperMonster003/AutoJs6-Plugin-Batch-Rename/blob/master/.readme/README-ar.md)

******

### 简介

******

Batch Rename 插件为 AutoJs6 提供批量重命名能力, 功能参照 PowerToys 的 [PowerRename](https://learn.microsoft.com/windows/powertoys/powerrename): 用查找替换, 正则表达式, 计数器, 日期与随机变量, 照片元数据及汉字转拼音一次性重命名多个文件和文件夹, 执行前可完整预览, 执行后可以撤销.

插件是独立安装的 APK, 没有桌面图标. 它提供两个入口: 文件管理器的多选工具栏, 文件夹菜单与重命名对话框中的 "批量重命名", 以及脚本中的 `files.batchRename`. 插件只负责计算新名称, 实际的重命名, 冲突检查与撤销记录由 AutoJs6 完成, 因此插件不需要任何存储权限.

******

### 功能亮点

******

- 查找与替换: 支持纯文本或 JavaScript 正则表达式, 可选全部匹配或只替换第一处, 区分或忽略大小写, 只作用于名称, 扩展名或两者.
- 变量: 计数器 (`${start=1;increment=1;padding=3}`), 取文件修改时间或当前时间的日期变量 (`$YYYY`, `$MM`, `$DD` 等) 与随机字符串, 写法与 PowerRename 一致.
- 汉字转拼音: "名称清理" 中的 "汉字转拼音" 把新名称中的汉字转为拼音, 替换变量 `${pinyin}` 插入原名的拼音; 可选声调, 音节分隔, 大小写与只取首字母, 多音字按词组判断.
- 照片元数据: 开启 "范围与格式" 中的 "元数据变量" 后, 可用拍摄时间, 相机, 镜头, 光圈, 快门, 感光度, GPS 等 25 项信息命名照片 (`$DATE_TAKEN_YYYY`, `$CAMERA_MODEL` 等), 缺少元数据的行会标出.
- 命名预设: 用顶栏的 "预设" 按钮把当前选项保存为预设并一点加载, 可重命名, 删除, 通过文件或剪贴板导入导出, 脚本中也能引用.
- 名称整理: 大小写转换, Unicode 规范化, 全角转半角, 替换不允许的字符, 去除首尾空格, 以及接续既有名称的编号.
- 先预览再执行: 逐项显示原名与新名, 可取消勾选排除条目, 可只显示改名项; 新名称被占用时可选择不重命名, 跳过, 添加编号或替换既有条目.
- 可以撤销: AutoJs6 为脚本与每个插件各保存最近 20 个重命名批次 (可改为 50 或 100), 文件管理器与脚本中都能撤销, 也可在 AutoJs6 的 "重命名历史" 页面中查看, 撤销或删除; 撤销前会检查文件是否已被再次修改.
- 界面跟随 AutoJs6 的主题色, 夜间模式与语言, 可记住上次选项与最近使用的查找和替换文本.

******

### 使用方法

******

1. 将 AutoJs6 升级到内部版本号 5455 (6.8.0) 或更高版本.
2. 在 AutoJs6 的插件中心安装 Batch Rename, 或从 [Releases](https://github.com/SuperMonster003/AutoJs6-Plugin-Batch-Rename/releases) 下载 APK 安装, 然后在插件中心授权并启用.
3. 在文件管理器中长按进入多选后点击 "批量重命名", 或在脚本中调用 `files.batchRename`.

> 插件只有一个通用安装包, 支持 Android 7.0 (API 24) 及以上的设备. 安装后不会在桌面创建图标.

******

### 在文件管理器中使用

******

安装并启用插件后, AutoJs6 文件管理器中有三处入口:

- 多选: 选中若干文件或文件夹, 在选择栏中点 "批量重命名" (有多个插件动作时位于 "更多" 中).
- 文件夹菜单: 在文件夹的菜单中选择 "批量重命名其中的项目", 为该文件夹中的条目改名.
- 重命名对话框: 重命名单个文件或文件夹时, 点对话框右上角的 "更多" 菜单, 选择 "批量重命名...". 同一菜单末尾的 "重命名历史" 可查看与撤销以往的批次.
- 在插件界面中输入查找与替换内容, 预览会即时更新; 取消勾选可排除条目, 点 "重命名" 执行, 之后可以点 "撤销上一批" 恢复原名. 顶栏 "更多选项" 菜单末尾的 "重命名历史" 打开 AutoJs6 的重命名历史页面.

******

### 替换变量

******

替换文本中可以使用以下变量:

| 变量 | 含义 |
|---|---|
| `${}` `${start=X;increment=X;padding=X}` | 计数器: 可设置起始值, 步长与补零位数, 只对实际改名的项目递增 |
| `$YYYY` `$YY` `$MM` `$MMM` `$DD` `$DDDD` `$hh` `$mm` `$ss` `$fff` | 文件修改时间的年, 月, 日, 时, 分, 秒与毫秒, 也可改用当前时间 |
| `${rstringalnum=N}` `${rstringalpha=N}` `${rstringdigit=N}` `${ruuidv4}` | 随机字母数字串, 字母串, 数字串与 UUID |
| `${pinyin}` | 原名的拼音, 风格与 "汉字转拼音" 的选项相同 |
| `$CAMERA_MODEL` `$LENS` `$APERTURE` `$SHUTTER` `$ISO` `$DATE_TAKEN_YYYY` `$LATITUDE` ... | 照片的相机, 镜头, 曝光参数, 尺寸, 作者, 版权, GPS, 拍摄时间, 方向与色彩空间 (共 25 项, 需开启 "元数据变量") |
| `$1` `$<name>` `$&` `$$` | 正则捕获组与整个匹配, 写法与 JavaScript 的 `String.prototype.replace` 相同 |

******

### 脚本 API

******

脚本通过 AutoJs6 的 `files.batchRename` 使用同一个引擎, 完整说明见 [AutoJs6 文档](https://docs.autojs6.com/#/files?id=m-batchrename):

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

- `files.batchRename.plan(paths, options)` 只计算不改名, 返回每一项的新名称与状态; 把 `items[i].excluded` 设为 `true` 后再交给 `apply` 执行.
- `files.batchRename(paths, options)` 一步完成; `files.batchRename.undo(result)` 撤销该批次, `files.batchRename.history()` 列出最近的批次 (包括文件管理器中的批次).
- 正则表达式与脚本中的 `RegExp` 一致, 可以直接传入 `RegExp` 对象. 另有 `planAsync` / `applyAsync` / `undoAsync` 异步形态; 失败时抛出带 `code` 的 `BatchRenameError`.
- `files.batchRename.preview(name, options)` 不访问文件即可试算新名称; `presets()` 与 `importPresets(json)` 读取和导入命名预设, 选项中的 `preset` 引用预设; `deleteHistory` 与 `clearHistory` 清理历史批次; 元数据变量需要 `metadata: true`.

******

### 兼容性

******

- 需要 AutoJs6 6.8.0 (构建 5455) 或更高版本, 以及 Android 7.0 (API 24) 或更高版本.
- SD 卡或 U 盘等 FAT32 / exFAT 存储不区分大小写, 也不允许 `: * ? " < > |` 与反斜杠等字符; 含这些字符的新名称会被标为不可用, 开启 "替换不允许的字符" 可以自动替换.
- 一个批次最多 2000 个条目; 脚本中给出的文件夹之下最多 32 层.

******

### 常见问题

******

#### 为什么桌面上没有插件图标?

插件没有独立的界面入口, 安装后由 AutoJs6 自动发现, 全部操作都在 AutoJs6 的文件管理器或脚本中进行.

#### 插件需要存储权限吗?

不需要. 插件只根据文件名计算新名称, 读取目录与实际重命名都由 AutoJs6 使用其已有的权限完成.

#### 和 PowerRename 有什么不同?

日期变量使用文件修改时间 (Android 上的创建时间并不可靠), 另外增加了汉字转拼音, 命名预设, Unicode 规范化, 全角转半角, 不允许字符的替换与可撤销的批次记录. PowerRename 的 25 个照片元数据变量均已支持.

#### 正则表达式的写法和 JavaScript 一样吗?

一样. 插件使用与 AutoJs6 脚本相同的 Rhino 正则引擎, 替换文本中可以使用 `$1`, `$<name>` 与 `$&`. 单次匹配超过 1 秒会被中止, 以免界面卡住.

#### 为什么计数器没有递增?

需要先开启 "计数器" 选项 (脚本中为 `enumerate: true`), 否则 `${}` 会按原样保留. 计数器只为新名称发生变化的条目计数, 名称不变或被排除的条目不占用编号.

#### 为什么元数据变量展开为空?

需要先开启 "范围与格式" 中的 "元数据变量" (脚本中为 `metadata: true`). 照片中没有记录的信息会展开为空, 该行标为 "缺少元数据": 例如部分 RAW 文件不记录 ISO, 社交应用保存的图片没有 EXIF. 拍摄时间缺失时改用文件修改时间.

#### 为什么重命名对话框中看不到批量重命名?

入口位于对话框右上角的 "更多" 菜单. 如果菜单中只有引导项, 说明插件尚未安装, 未在插件中心启用, 或需要更新 AutoJs6; 按引导处理后即可使用.

******

### 权限与安全

******

插件在设计上尽量收窄权限面:

- 最小权限: 只声明 AutoJs6 插件权限 (`org.autojs.permission.PLUGIN`), 不申请网络或存储权限.
- 宿主执行: 插件只返回改名方案, AutoJs6 在执行前重新校验每一项, 不会覆盖已有文件, 除非明确选择覆盖策略.
- 受保护的入口: 服务与界面入口均受插件权限保护, 第三方应用无法直接调用.

******

### 插件接口

******

以下信息面向 AutoJs6 宿主与插件开发者, 宿主通过这些标识发现插件并完成能力协商:

```text
application id: io.github.supermonster003.autojs6.plugin.batch.rename
plugin id: batch-rename
engine: batch-rename
variant: default
info service: org.autojs.plugin.INFO (category batch-rename)
capability service: org.autojs.plugin.BATCH_RENAME (category batch-rename)
file manager service: org.autojs.plugin.EXPLORER_ACTION
wake action: org.autojs.plugin.action.WAKE
minimum host build: 5455
native library: libandroidx.graphics.path.so (Jetpack Compose, all ABIs)
```

******

### 开发路线图

******

功能规划, 设计决策与完成情况以可勾选清单维护在 ROADMAP.md 中. 未勾选条目表示规划意向, 不代表当前版本能力.

- [查看 ROADMAP.md](https://github.com/SuperMonster003/AutoJs6-Plugin-Batch-Rename/blob/master/ROADMAP.md)

******

### 发行历史

******

#### v1.1.1

_2026/10/16_

- `提示` 需要 AutoJs6 6.8.0 (5455) 或更高版本
- `修复` 部分系统 (如 ColorOS 的应用信息页) 将应用图标显示在黑色底板上, 图标难以辨认的问题

#### v1.1.0

_2026/10/16_

- `提示` 需要 AutoJs6 6.8.0 (5455) 或更高版本
- `新增` 汉字转拼音: "名称清理" 中的 "汉字转拼音" 与替换变量 `${pinyin}`, 可选声调, 音节分隔, 大小写与只取首字母, 多音字按词组判断
- `新增` 照片元数据变量: 用拍摄时间, 相机, 镜头, 光圈, 快门, 感光度, GPS 等 25 项信息命名照片, 缺少元数据的行会标出 (在 "范围与格式" 中开启 "元数据变量")
- `新增` 命名预设: 把当前选项保存为预设并一点加载, 可重命名, 删除, 通过文件或剪贴板导入导出 (入口: 顶栏 "预设" 按钮)
- `新增` 重命名历史: 在 AutoJs6 中查看, 搜索, 撤销或删除文件管理器与脚本的重命名批次, 并设置保留的批数 (入口: 顶栏 "更多选项" > "重命名历史")
- `新增` 为 AutoJs6 的 files.batchRename 脚本 API 提供汉字转拼音, 照片元数据, 命名预设与不访问文件的名称试算
- `优化` 新的插件图标
- `优化` 顶栏 "更多选项" 菜单中的 "清除历史" 改为 "清除最近使用", 以免与 "重命名历史" 混淆

#### v1.0.0

_2026/10/16_

- `提示` 需要 AutoJs6 6.8.0 (5438) 或更高版本
- `新增` 在 AutoJs6 文件管理器中批量重命名文件与文件夹 (入口: 多选工具栏/文件夹菜单 "批量重命名其中的项目"/重命名对话框 "更多" 菜单)
- `新增` 查找与替换, 支持纯文本或 JavaScript 正则表达式, 全部匹配, 区分大小写, 只改名称或扩展名
- `新增` 计数器, 取文件修改时间或当前时间的日期变量, 随机字符串与 UUID
- `新增` 大小写转换, Unicode 规范化, 全角转半角, 替换不允许的字符, 去除首尾空格与接续既有名称的编号
- `新增` 改名前预览新名称, 可逐项排除或只显示改名项, 新名称被占用时可选择不重命名, 跳过, 添加编号或替换既有条目
- `新增` 撤销最近的重命名批次 (AutoJs6 保存最近 20 批, 文件管理器与脚本共用)
- `新增` 为 AutoJs6 的 files.batchRename 脚本 API 计算新名称
- `新增` 界面跟随 AutoJs6 的主题色, 夜间模式与语言, 可记住上次选项与最近使用的查找和替换文本
- `依赖` 附加 Jetpack Compose BOM 版本 2026.09.00
- `依赖` 附加 AndroidX Activity Compose 版本 1.12.2
- `依赖` 附加 AndroidX Core KTX 版本 1.15.0
- `依赖` 附加 Kotlin Coroutines 版本 1.11.0
- `依赖` 附加 Desugar JDK Libs 版本 2.1.5
- `依赖` 附加 Rhino (AutoJs6 精简版, 用于正则表达式)

##### 更多发行历史可参阅

* [CHANGELOG.md](https://github.com/SuperMonster003/AutoJs6-Plugin-Batch-Rename/blob/master/app/src/main/assets/doc/CHANGELOG-zh-Hans.md)

******

### 构建

******

本节面向希望从源码构建插件的开发者.

```powershell
.\gradlew.bat :app:assembleDebug :app:testDebugUnitTest
.\gradlew.bat :app:appendDigestToReleasedFiles
py .python\generate_markdown.py --check
py .python\generate_icon_studio.py --check
py .python\check_repository.py
```

******

### 许可

******

项目代码使用 [Mozilla Public License 2.0](https://github.com/SuperMonster003/AutoJs6-Plugin-Batch-Rename/blob/master/LICENSE). 功能设计参照 PowerToys PowerRename 的公开文档, 不包含其代码或资源.

******

### 相关链接

******

- AutoJs6 项目: https://github.com/SuperMonster003/AutoJs6
- PowerToys PowerRename 文档 (功能参照): https://learn.microsoft.com/windows/powertoys/powerrename
