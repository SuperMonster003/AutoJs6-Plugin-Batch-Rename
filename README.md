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

Batch Rename 插件为 AutoJs6 提供批量重命名能力, 功能参照 PowerToys 的 [PowerRename](https://learn.microsoft.com/windows/powertoys/powerrename): 用查找替换, 正则表达式, 计数器, 日期与随机变量一次性重命名多个文件和文件夹, 执行前可完整预览, 执行后可以撤销.

插件是独立安装的 APK, 没有桌面图标. 它提供两个入口: 文件管理器的多选工具栏, 文件夹菜单与重命名对话框中的 "批量重命名", 以及脚本中的 `files.batchRename`. 插件只负责计算新名称, 实际的重命名, 冲突检查与撤销记录由 AutoJs6 完成, 因此插件不需要任何存储权限.

> 当前为开发预览版: 插件可以被 AutoJs6 识别, 但重命名界面, 文件管理器入口与脚本 API 尚未交付. 下文描述的是 1.0.0 的设计目标, 进度见开发路线图.

******

### 功能亮点

******

- 查找与替换: 支持纯文本与正则表达式, 可选择全部匹配或仅替换第一处, 区分或忽略大小写, 只作用于文件名, 扩展名或完整名称.
- 变量: 计数器 (`${start=1;increment=1;padding=3}`), 文件修改时间的日期变量 (`$YYYY`, `$MM`, `$DD` 等) 与随机字符串, 写法与 PowerRename 一致.
- 文本整理: 大小写转换, Unicode 规范化, 全角转半角, 清理非法字符, 修剪首尾空格与点号, 从已有序号继续编号.
- 先预览后执行: 逐项显示原名与新名, 可排除个别项目, 冲突与非法名称会被标出; 冲突可选择整批拒绝, 跳过, 自动编号或覆盖.
- 可以撤销: AutoJs6 保存最近 20 批重命名记录, 文件管理器和脚本都能撤销, 撤销前会检查文件是否已被再次修改.

******

### 使用方法

******

1. 将 AutoJs6 升级到内部版本号 5431 (6.8.0) 或更高版本.
2. 在 AutoJs6 的插件中心安装 Batch Rename, 或从 [Releases](https://github.com/SuperMonster003/AutoJs6-Plugin-Batch-Rename/releases) 下载 APK 安装, 然后在插件中心授权并启用.
3. 在文件管理器中长按进入多选后点击 "批量重命名", 或在脚本中调用 `files.batchRename`.

> 插件只有一个通用安装包, 支持 Android 7.0 (API 24) 及以上的设备. 安装后不会在桌面创建图标.

******

### 替换变量

******

替换文本中可以使用以下变量:

| 变量 | 含义 |
|---|---|
| `${}` `${start=X;increment=X;padding=X}` | 计数器: 可设置起始值, 步长与补零位数, 只对实际改名的项目递增 |
| `$YYYY` `$YY` `$MM` `$MMM` `$DD` `$DDDD` `$hh` `$mm` `$ss` `$fff` | 文件修改时间的年, 月, 日, 时, 分, 秒与毫秒, 也可改用当前时间 |
| `${rstringalnum=N}` `${rstringalpha=N}` `${rstringdigit=N}` `${ruuidv4}` | 随机字母数字串, 字母串, 数字串与 UUID |
| `$1` `$<name>` `$&` `$$` | 正则捕获组与整个匹配, 写法与 JavaScript 的 `String.prototype.replace` 相同 |

******

### 脚本 API

******

脚本通过 `files.batchRename` 使用同一套能力 (1.0.0 设计草案, 以发布版本的文档为准):

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

- `files.batchRename.plan(paths, options)` 只计算不执行, 返回逐项结果; 修改 `items[i].excluded` 后可交给 `apply` 执行.
- `files.batchRename(paths, options)` 一步完成, 返回批次编号与每一项的结果; `files.batchRename.undo(result)` 撤销该批次.
- 正则表达式的语义与脚本中的 `RegExp` 完全一致, 可以直接传入 `RegExp` 对象. 另有 `planAsync` / `applyAsync` / `undoAsync` 异步版本.

******

### 常见问题

******

#### 为什么桌面上没有插件图标?

插件没有独立界面入口, 安装后由 AutoJs6 自动发现, 全部操作都在 AutoJs6 的文件管理器或脚本中进行.

#### 插件需要存储权限吗?

不需要. 插件只根据文件名计算新名称, 读取目录与实际重命名都由 AutoJs6 使用其已有的权限完成.

#### 和 PowerRename 有什么不同?

日期变量使用文件修改时间 (Android 上的创建时间并不可靠), 另外增加了 Unicode 规范化, 全角转半角, 非法字符清理与可撤销的批次记录. 照片 EXIF 变量计划在 1.1.0 提供.

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
minimum host build: 5431
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

#### v1.0.0

_2026/10/15_

- `提示` 开发预览: 插件可以被 AutoJs6 识别, 重命名界面, 文件管理器入口与 files.batchRename 脚本 API 尚未交付
- `提示` 需要 AutoJs6 6.8.0 (5431) 或更高版本 (准确的最低版本号待宿主侧改动落地后回填)
- `新增` 插件骨架: Wake 激活协议, 插件中心身份服务, 脚本能力服务与文件管理器动作服务 (动作目录暂为空)
- `新增` 10 种语言的 README, 插件中心说明与更新日志, 由 JSON 源文件统一生成
- `依赖` 附加 Jetpack Compose BOM 版本 2026.09.00
- `依赖` 附加 AndroidX Activity Compose 版本 1.12.2
- `依赖` 附加 AndroidX Core KTX 版本 1.15.0
- `依赖` 附加 Kotlin Coroutines 版本 1.11.0

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
