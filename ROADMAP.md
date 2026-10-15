# Batch Rename Roadmap

更新日期: 2026-10-15

本文是 `AutoJs6-Plugin-Batch-Rename` (显示名 `Batch Rename`; 参照 [PowerToys PowerRename](https://github.com/microsoft/PowerToys/tree/main/src/modules/powerrename) 的功能集与交互, 为 AutoJs6 文件管理器与脚本提供批量重命名能力; 脚本侧挂载为 `files.batchRename`, 无启动器图标) 的可执行状态表.
以 2026-10-15 的宿主本地代码快照 (`AutoJs6 master@944f6fe5cf`, P0.1 的契约 AAR 取自同日 `6c151c900e`, `VERSION_NAME=6.8.0` 尚未发布, `VERSION_BUILD=3700` + `VERSION_CODE_OFFSET=1652`, 即 versionCode 5352; explorer-action 协议 v24; 宿主本地 Rhino `2.0.0-SNAPSHOT`), 平台版本插件 `1.9.1` 为起点, 每个条目均可独立 Check 并落地, 后续会话按阶段逐步推进.
不复制 PowerToys 的任何代码或资源 (PowerToys 为 MIT, 本仓库为 MPL-2.0), 只参照其公开文档 (`learn.microsoft.com/windows/powertoys/powerrename`, 2025-01-23 版) 描述的功能, 选项与变量语法 (附录 A).

使用方式:

1. 每次会话开始时, 从 "阶段总览" (第 6 节) 选取一个或多个未完成条目, 优先级按阶段顺序; 单次会话可完成多个小节, 除非单个小节已足够繁杂.
2. 条目完成后勾选 `[x]`, 并在条目后追加证据 (提交 hash / 测试类名 / 设备型号与 API / 样本文件名 / 度量值), 证据等级见附录 E. 未勾选条目属于规划意向, 不代表当前版本能力.
3. 条目前缀标明主要落点: `(插件)` 本仓库, `(宿主)` `D:/idea-projects/AutoJs6`, `(索引)` `D:/idea-projects/AutoJs6-Official-Plugins-Index`, `(文档)` AutoJs6-Documentation / AutoJs6-TypeScript-Declarations / AutoJs6-Plugin-Ace-Editor / AutoJs6-Plugin-Offline-Docs 四个关联仓库 (均位于 `D:/idea-projects/`, 宿主 `AGENTS.md` 中的 `D:/webstorm-projects/` 路径已失效), `(设计)` 设计文档, `(测试)`, `(发布)`.
4. 涉及宿主公开契约 (batch-rename 契约, explorer-action 协议, 脚本 API) 的条目, 完成后必须同步宿主 `docs/dev/`, 宿主 `.changelog` (10 种语言) 与本仓库 `.changelog`. 宿主侧提交只落本地, 由维护者合并与推送 (与 3-Maple Image 的做法一致).
5. 第 1 节 "固定决策" 由维护者 2026-10-15 经三轮选择题确认, 后续阶段不再重新讨论; 第 2 节 "派生技术决策" 可在进入对应阶段前推翻 (推翻点已逐条标注), 之后视同固定; 附录 D "待决事项" 在进入对应阶段前由维护者拍板, 拍板结果回填到第 1 节或第 2 节.
6. 本仓库骨架 (Gradle / Manifest / 资源 / CI) 在 P0.1 按 `D:/idea-projects/AUTOJS6_PLUGIN_NEW_REPO_AGENTS.md` 生成, 其裁剪版即本仓库 `AGENTS.md`; 本插件没有独立启动入口 (D4), 因此 `AUTOJS6_PLUGIN_STANDALONE_SETTINGS_AGENTS.md` 不适用, 图标按 `AUTOJS6_PLUGIN_BLACK_N_WHITE_ADAPTIVE_ICON_AGENTS.md` 只生成基础 `mipmap/ic_launcher.png` 系列与 Icon Studio 系统图标 (`ic_icon_studio_application`, 2026-10-05 维护者要求); 工程约定 (构建, 资源, 文档生成, 提交计数, 推送前检查 `py D:/idea-projects/prepush_check.py`) 以 `AGENTS.md` 与 `D:/idea-projects/COMMON_AGENTS.md` 为准, 本文件只记录 "改什么" 与证据. 所有文案 (10 种语言) 只用 ASCII 标点.
7. 2026-10-15 的首个会话只落盘路线图并 `git init` + 首次提交 (D12); 同日 P0.1 完成骨架 (提交 `cd353e1`, `c84e840`, `629e0df`), P0.2 完成测试与 CI (提交 `1c99280`, `df5cb8b`), 之后按 `AGENTS.md` 的提交规则工作 (每笔提交前 `VERSION_BUILD = 提交数 + 1`). 与全部官方插件仓库一致, `AGENTS.md` / `CLAUDE.md` 只在本地维护并被 `.gitignore` 忽略, 不入库.

---

## 1. 固定决策

以下决策 D1-D12 已由维护者于 2026-10-15 分三轮确认.

| 编号 | 决策 | 含义 |
| --- | --- | --- |
| D1 | 命名: `AutoJs6-Plugin-Batch-Rename` | 显示名 `Batch Rename`, 插件 ID `batch-rename`, 与 Pinyin / MediaInfo 等无独立入口的能力型插件同一命名层级, 不并入 Three 系列, 不借用 PowerRename 商标词. 身份派生表见 5.4. |
| D2 | 脚本 API 形态: 可调用命名空间 `files.batchRename` | `files.batchRename(paths, options)` 一步完成 (等价于 `apply(plan(paths, options))`); 同时提供 `plan` 预览, `apply` 执行, `undo` 撤销, `history` 批次列表, `isAvailable` 探测. `$files.batchRename` 指向同一对象. 与 `mediainfo` 可调用模块同构 (`Augmentable` + `Invokable`). 草案见附录 B. |
| D3 | 执行者: 宿主执行物理重命名, 插件只算方案 | 插件经 Binder 返回逐项 (旧名 -> 新名 -> 状态 -> 原因) 的方案; 宿主以脚本已有的存储权限做两阶段, 冲突检查的重命名并维护撤销. 插件不申请任何存储权限, 与 explorer-action 协议 "宿主托管写入" 一致. |
| D4 | 界面: 插件自带 Compose + Material 3 界面, 1.0.0 即含, 无启动器图标 | 文件管理器入口 (D10) 打开插件内的 PowerRename 风格界面 (查找 / 替换 / 选项 / 实时预览列表); 物理重命名经宿主 explorer-action 协议 v25 的 `renameEntries` 执行 (D3). 不做宿主内置对话框, 不做独立设置页. |
| D5 | 1.0.0 功能范围: 核心组 + 日期与随机变量 + 本土扩展 | 核心组: 查找 / 替换 (纯文本与正则), 全部匹配或仅首次, 大小写敏感, 作用范围 (文件名 / 扩展名 / 全部), 包含文件 / 文件夹 / 子文件夹, 文本格式化 (小写 / 大写 / 句首 / 每词首字母), 计数器 `${}` `${start=;increment=;padding=}`. 日期与随机变量: `$YYYY` 至 `$f` 全部 20 个日期时间变量 (基于文件修改时间, 可选当前时间), `${rstringalnum=N}` `${rstringalpha=N}` `${rstringdigit=N}` `${ruuidv4}`. 本土扩展: Unicode 规范化 (NFC), 全角转半角, 非法字符清理, 尾部空格与点号修剪, 从现有文件名解析序号后接续. 照片元数据 (EXIF / XMP) 变量排到 1.1.0 (P6). |
| D6 | 正则方言: 严格 ECMAScript 语义 | 查找与替换串的语义与脚本侧 `RegExp` / `String.prototype.replace` 完全一致 (含命名组 `$<name>`, 后行断言, `s` / `u` / `y` 标志, `$&` `` $` `` `$'` `$$`). 实现方式见 D15. 脚本可直接传 `RegExp` 对象. |
| D7 | 冲突策略: `fail` (默认) + `skip` + `number` + `overwrite` | 默认整批拒绝并在预览中标红; `skip` 跳过冲突项; `number` 追加 ` (1)` ` (2)` (模式可配置); `overwrite` 须显式开启, 被覆盖文件先进宿主回收站以便撤销, 界面上需二次确认. |
| D8 | 撤销: 宿主持久化撤销批次 | 宿主记录最近 N 批 (默认 20) 重命名日志, 跨进程重启有效; 脚本 `files.batchRename.undo` 与界面 "撤销" 共用同一机制 (沿用 explorer-action v21 回收站批次的形态); 撤销前校验文件未被再次改动. |
| D9 | 调用模式: 同步为主, 另加 Async 变体 | `plan` / `apply` / `undo` / `history` 同步返回, 与 `files` 其他方法一致; `options.onProgress(done, total, item)` 在执行中回调; 另提供 `planAsync` / `applyAsync` / `undoAsync` 返回 Promise. 单批上限在契约中固定 (D14), 超限报错. |
| D10 | 文件管理器入口: 三处 | (a) 多选工具栏: 长按进入多选后的工具栏, 对选中的文件与文件夹生效 (混合目标); (b) 文件夹溢出菜单 "批量重命名其中的项目", 界面内可勾选是否包含子文件夹; (c) 单文件 / 单文件夹的传统 "重命名" 对话框右上角增加 "更多" 菜单, 其中的 "批量重命名" 菜单项以该项为目标打开插件界面. 单文件溢出菜单不单独增加入口. |
| D11 | 界面辅助功能: 四项全含 | 逐项勾选排除 (PowerRename 的 Original 列), 仅显示将重命名的项 (Renamed 列切换), 记住上次选项 (Restore flags on launch, 可在界面内关闭), 查找 / 替换历史与自动补全 (MRU, 默认 20 条). |
| D12 | 本次会话交付 | 新建 `D:/idea-projects/AutoJs6-Plugin-Batch-Rename`, 只写入 `ROADMAP.md`, 设置仓库级提交身份后做一笔 `docs` 提交. 骨架 (Gradle / Manifest / AGENTS.md 裁剪版) 作为 P0 由后续会话完成. |

---

## 2. 派生技术决策

D13-D30 据第 1 节派生, 进入对应阶段前可推翻 (推翻点见各条), 之后视同固定.

| 编号 | 决策 | 含义 | 推翻点 |
| --- | --- | --- | --- |
| D13 | 两条路径, 一个引擎, 一个执行器 | 脚本路径: 宿主枚举目标 -> Binder `plan` -> 宿主执行器. 管理器路径: 插件经 session `listChildren` 枚举 -> 插件进程内同一引擎实时预览 -> session `renameEntries` -> 宿主执行器. 引擎只在插件内存在一份; 执行器只在宿主内存在一份 (脚本路径与 v25 session 共用), 撤销日志也只有一份. | P1.1 |
| D14 | 契约模块 `plugin-api/batch-rename-api` | 常量 (`BatchRenamePluginActions.BATCH_RENAME = "org.autojs.plugin.BATCH_RENAME"`, `BatchRenamePluginIds`, `BatchRenameContract.CONTRACT_VERSION = 1`, `BatchRenameCapabilityKeys`, `BatchRenameLimits`, 请求 / 条目 / 结果 / 选项的 key 对象) 与 `IBatchRenamePlugin.aidl` (`getInfo` 居首, 其后仅 `plan(in Bundle request)`). 单批上限 `MAX_ITEMS_PER_PLAN = 2000`, 名称上限 1024 字符, 查找 / 替换串上限 4096 字符; 请求以 `Bundle` + `ArrayList<Bundle>` 传输, 不走 JSON 字符串. 草案见附录 C.1. | P1.1 |
| D15 | 正则引擎: 插件内置 Rhino `NativeRegExp` | 插件 `libs/` 收录宿主同款 Rhino slim jar (由宿主 `slimRhinoJar` 任务派生, 2.0.0-SNAPSHOT, 已含命名组 / 后行断言 / `s` `y` 标志 / Unicode 属性; SHA-256 写入 `locks/`), R8 收缩到 `org.mozilla.javascript.regexp` 及其依赖子集. 引擎用 `String.prototype.replace(regexp, replacement)` 在插件进程内求值, 替换串语义天然与脚本一致 (D6); 纯文本查找时对查找串转义后同样走正则路径 (与 PowerRename 相同). 备选: Maven Central `org.mozilla:rhino` 最新版, 仅当 P2.1 的 ECMAScript 特性对照全部通过时可用 (附录 D Q4). | P2.1 |
| D16 | 单项流水线顺序 | 按 `target` 取出文件名 / 扩展名 / 全名 -> 展开逐项变量 (计数器, 日期, 随机) 得到字面替换串, 其中 `$` 转义为 `$$` -> ECMAScript 替换 -> 文本格式化 -> 本土扩展 (规范化, 全角转半角, 清理, 修剪) -> 重新拼接 -> 合法性校验 -> 冲突判定. 变量展开只对 "查找命中" 的项执行, 计数器只对实际改名的项递增 (PowerRename 一致). | P2.2 |
| D17 | 选项默认值与 PowerRename 对齐 | `regex=false`, `matchAll=false`, `caseSensitive=false`, `target="all"`, `includeFiles=true`, `includeFolders=true`, `includeSubfolders=true`, `format="none"`, `enumerate=false` (关闭时 `${...}` 视为字面), `random=false`, `dateTime="modified"` (总是展开, `"now"` 用当前时间, `false` 关闭), `onConflict="fail"`. 传入 `RegExp` 对象时 `regex=true`, 其 `g` 标志映射 `matchAll=true`, 无 `i` 标志映射 `caseSensitive=true`. 本土扩展默认全部关闭. | P2.2 |
| D18 | 日期与文本格式化的 locale | 月份名, 星期名与大小写转换按插件进程当前 locale (跟随宿主语言, 回退系统); `title` = 整串首字符大写其余不变 (PowerRename "first character of sentence"), `capitalize` = 每个以空白分隔的词首字母大写. 测试覆盖 en / zh / tr (土耳其语 i 问题). | P2.3 |
| D19 | 合法性与清理规则 | 始终拒绝: 空名, `.`, `..`, 含 `/` 或 NUL, UTF-8 超过 255 字节 (ext4 / f2fs 上限), 与原名相同但仅路径分隔不同. `sanitize=true` 时额外把 `\ : * ? " < > |` 与控制字符替换为 `sanitizeReplacement` (默认 `_`), 并给 Windows 保留名 (`CON` `PRN` `AUX` `NUL` `COM1-9` `LPT1-9`) 加后缀; `trim=true` 修剪首尾空白与尾部点号. 校验失败的项状态为 `invalid`, 整批视同冲突策略 `fail` 处理 (不执行). | P2.4 |
| D20 | 冲突判定与执行顺序 | 冲突来源: 同批两项新名相同 (同父目录), 新名与磁盘上既有且不在本批改名的条目相同. 比较默认区分大小写; 父目录位于可移动存储 (FAT32 / exFAT) 或 `File.exists` 对不同大小写同时为真时按不区分大小写比较. 仅大小写不同的改名与循环交换 (A->B, B->A) 用两阶段 (先改为 `.<name>.batch-rename-<batchId>-<i>.tmp` 再改为目标名). 同批含父子路径时按深度降序 (子项先于父项) 执行, 撤销按逆序. 单项失败: 已完成项保留, 结果逐项标记, 不自动回滚 (用户可 `undo`). | P3.1 |
| D21 | 撤销日志 | 宿主 `filesDir/batch-rename/journals/<batchId>.json` (批次元数据 + 逐项 旧路径 / 新路径 / 旧 `lastModified` / 旧 `length` / 状态); 保留最近 20 批, 超出按创建时间淘汰; 撤销前逐项校验新路径存在且 `lastModified` / `length` 未变, 否则该项 `stale` 并跳过, 其余照常恢复, 恢复目标被占用时不覆盖. `overwrite` 策略的受害文件走宿主回收站 (复用 v16 / v21 的回收站存储), 撤销时一并恢复. | P3.1 |
| D22 | explorer-action 协议 v25 | 新 catalog key `targetRename=true` (`ExplorerActionCatalogKeys.TARGET_RENAME`), 对 `TARGET_FILE` / `TARGET_DIRECTORY` / `TARGET_MIXED`, `CARDINALITY_SINGLE` / `MULTIPLE`, `ACCESS_READ_ONLY`, Activity 呈现, 协议 >= 25 有效; 新 placement `PLACEMENT_RENAME_DIALOG = 4` 与宿主 `ExplorerActionEntryPoint.RENAME_DIALOG` (D10c); session 末尾追加 `renameEntries` / `queryRenameBatch` / `listRenameBatches` / `undoRenameBatch` 四个事务 (保持 v1-v24 事务号不变); 条目以 (targetId, relativePath) 定位, `relativePath` 为空表示目标本身, 非空表示 `listChildren` 返回的后代 (D10b 与 "包含子文件夹"). 草案见附录 C.2. | P1.2 |
| D23 | 插件 catalog 三个动作 | `batch-rename-selection` (`TARGET_MIXED`, `CARDINALITY_MULTIPLE`, `PLACEMENT_SELECTION_TOOLBAR`, 匹配 `*/*`), `batch-rename-directory` (`TARGET_DIRECTORY`, `CARDINALITY_SINGLE`, `PLACEMENT_OVERFLOW`, 标签 "批量重命名其中的项目"), `batch-rename-single` (`TARGET_MIXED`, `CARDINALITY_SINGLE`, `PLACEMENT_RENAME_DIALOG`). 三者共用同一 Activity 与同一界面, 只是初始目标集不同; 均声明 `targetRename=true`, 不声明输出模式, 不声明 `filesystemPath`, 不声明 `targetTrash`. 每个 catalog Activity 都带 `org.autojs.plugin.EXPLORER_ACTION_EXECUTE` + `DEFAULT` intent-filter (否则宿主静默丢弃, 见 3-Maple Image 教训). | P4.1 |
| D24 | 界面技术与主题 | Jetpack Compose + Material 3 (与 3-Maple Image / 3-Block Compose 同代); 语言, 夜间模式与主题色跟随宿主 (`AutoJs6HostSettingsContract`, 宿主不可用时回退系统), 不提供设置页. 单屏布局: 顶栏 (标题, 撤销上一批, 溢出: 清除历史 / 恢复默认选项 / 记住上次选项开关) -> 查找 / 替换输入 (带历史下拉与变量插入按钮) -> 选项区 (开关与分段按钮) -> 预览列表 (复选框, 原名 -> 新名, 状态色, 列头 "仅显示将重命名") -> 底栏 (统计 + "应用"). 预览重算在后台协程, 输入去抖 150 ms. | P4.2 |
| D25 | 界面状态持久化 | 插件进程 DataStore (Preferences): 上次选项 (D11 "记住上次选项" 开启时恢复), 查找 / 替换 MRU 各 20 条 (去重, 最近优先, 单条上限 4096 字符). 不记录文件名或路径. | P4.3 |
| D26 | `REQUIRES_HOST_VERSION` 回填规则 | 取 "P1 + P3 宿主改动全部落地后" 的宿主 versionCode (`VERSION_BUILD + 1652`), 当前基线 5352, 实际值必大于它; P1.3 先写临时值 (当时的 `VERSION_BUILD + 1 + 1652`), P5.4 发布前按宿主最终提交回填并同步 README / 索引清单 / 测试夹具. 插件 `PluginInfo.capabilities` 同时上报 `batchRenameContractVersion=1`, `explorerActionProtocolVersion=25`. | P1.3 / P5.4 |
| D27 | 错误码 | 脚本侧异常携带 `code`: `PLUGIN_UNAVAILABLE` (未安装), `PLUGIN_DISABLED` (未启用或未授权), `PLUGIN_INCOMPATIBLE` (版本门禁), `INVALID_OPTIONS`, `INVALID_PATHS`, `LIMIT_EXCEEDED`, `PLAN_REJECTED` (冲突或非法项且策略为 `fail`), `PLAN_STALE` (apply 时目标已变化), `IO_FAILED`, `UNDO_UNAVAILABLE`. 形态沿用 `epub` 模块的 `EpubErrorMapper` 与 `isAvailable()` 模式; 缺插件时的提示文案复用宿主 `error_missing_required_plugin_for_module_label`. | P3.2 |
| D28 | 无 ABI 拆分 | 插件无自有原生代码; Compose `ui-graphics` 传递依赖打包 `libandroidx.graphics.path.so` (4 ABI), 与 3-Maple Image 相同: 单 Universal APK, `PluginInfo.supportedAbis = emptyArray()`, Manifest 声明 `NATIVE_PAGE_ALIGNMENT=16384`, 发布门校验该库未压缩且 16 KB 对齐. 体积预算: release APK <= 3.5 MB. | P0.1 |
| D29 | 宿主侧入口的退化行为 | 插件未安装时: 重命名对话框 "更多" 菜单仍显示 "批量重命名..." 条目, 点击后进入插件中心安装向导 (`FILE_MANAGER` 分类); 多选工具栏与文件夹菜单沿用既有行为 (只列已安装插件的动作). 未启用 / 不兼容时走插件中心既有的 "激活" / 版本提示. 脚本 `files.batchRename` 在插件缺失时抛 `PLUGIN_UNAVAILABLE`, `isAvailable()` 返回 `false`. 宿主不保留任何重复的改名引擎实现. | P1.4 |
| D30 | 脚本路径的目标枚举 | `paths` 接受字符串或字符串数组 (相对路径按脚本工作目录解析); 目录本身作为一项 (受 `includeFolders` 过滤), 其后代按 `includeSubfolders` 递归 (不跟随符号链接, 深度上限 32, 总数上限 2000, 超出 `LIMIT_EXCEEDED`); 宿主把每个父目录的既有条目名随请求一并发给插件供冲突判定. 管理器路径由插件经 `listChildren` 做同样的枚举. | P3.1 |

---

## 3. 范围与非目标

范围 (1.0.0):

- 脚本 API `files.batchRename` 全集 (附录 B) 与 10 种语言的文档, d.ts, 离线文档, Ace 声明同步.
- 文件管理器三处入口 (D10) 与 PowerRename 风格界面 (D4, D11).
- D5 列出的变量与选项; D7 四种冲突策略; D8 持久化撤销.
- 宿主契约 `batch-rename-api`, explorer-action v25, 插件中心注册, 安装向导条目, 官方索引登记.

非目标 (不做或排到 1.1.0 之后, 评估记录见附录 G):

- 启动器图标, 独立设置页, 独立启动入口.
- 修改文件内容, 读取媒体库 (MediaStore), 跨目录移动 (只改名不换父目录).
- 压缩包内条目的重命名 (归 3-Stack Archive 与 `IExplorerArchiveSession`).
- AI 生成正则 (PowerRename 的 Copilot 入口).
- 照片 EXIF / XMP 变量 (1.1.0, P6).
- 宿主内置的 "重命名历史" 页面 (1.1.0 候选, 附录 D Q3).

---

## 4. 现状诊断

### 4.1 可直接复用的宿主能力

- `files` 全局对象的挂载: `ScriptRuntime.kt:1044` `Files(this).augment(target, files)`; 子命名空间先例 `ocr.mlkit` 等 (`ScriptRuntime.kt:1008-1012`, 子类 `keys = listOf(AugmentableKey("mlkit", withDollarPrefix = false))`); 可调用模块先例 `mediainfo` (`augment/mediainfo/Mediainfo.kt`, `Augmentable` + `Invokable`).
- 插件发现与绑定: `core/plugin/AidlPluginHost.kt` (`discover` / `selectOrThrow` / `call`, 已含启用, 授权, 宿主版本门禁与缺插件错误文案); 薄封装先例 `core/plugin/mediainfo/MediainfoPluginHost.kt`.
- 公共契约: `plugin-api/common-plugin-api` (`PluginInfo`, `PluginCapabilityKeys.REQUIRES_HOST_VERSION`, `IPluginInfoProvider`, `AutoJs6HostSettingsContract`); 能力契约模板 `plugin-api/mediainfo-api` (Actions / Ids / CapabilityKeys / OptionKeys / AIDL / consumer-rules).
- 文件管理器扩展点: `plugin-api/explorer-action-api` v24 (`IExplorerActionPlugin`, `IExplorerActionHostSession` 含 `listChildren`, 回收站批次与撤销), 宿主 `core/plugin/explorer/` (`ExplorerActionRegistry`, `ExplorerActionCatalogPolicy`, `session/ExplorerActionHostSession.kt` 2051 行, v24 实现与 `ExplorerActionTargetTrashV24InstrumentationTest` 可作模板), `ui/explorer/ExplorerView.kt:783-899` 多选工具栏已自动列出插件动作 (首个为主按钮, 其余进 "更多"), `ExplorerActionEntryPoint` 已有 `MEDIA_INFO_DIALOG` 这类对话框内入口先例.
- 单文件重命名: `ui/common/ScriptOperations.java:344-393` (`showNameInputDialog` MaterialDialog + `rename`, 含 `relocateHistoryAsync` 与 `notifyFileChanged`).
- 错误模型先例: `augment/epub/Epub.kt` 的错误码与 `isAvailable()`.
- 注册表: `core/plugin/center/InstalledPluginRepository.kt` (`queryDeclaredPluginServices` 与 `discoverPackage` 两张表), `core/plugin/center/wizard/PluginInstallWizardCatalog.kt` (`FILE_MANAGER` 分类), `AndroidManifest.xml` `<queries>`.
- Rhino 正则: 宿主本地 `libs/org-mozilla-rhino-2_0_0-SNAPSHOT.jar` (`NativeRegExp` 已实现命名组, 后行断言, `dotAll`, `sticky`, Unicode 属性), `slimRhinoJar` 任务可派生无 JLine 的运行时 jar.

### 4.2 缺口 (需要新建或修改)

- 宿主无批量重命名入口, 无改名执行器, 无改名撤销日志; `PFiles.rename` / `move` 为裸 `File.renameTo`, 会静默覆盖且不解析相对目标 (脚本 API 不能直接复用它们).
- explorer-action 协议没有文件系统改名能力 (改名只存在于压缩包会话), 没有 "重命名对话框" placement.
- 没有 `batch-rename-api` 契约模块; `InstalledPluginRepository` / 向导目录 / `<queries>` 均无条目.
- 文档, d.ts, 离线文档, Ace 声明均无 `files.batchRename`.
- 本仓库尚不存在 (本次会话只落盘路线图).

### 4.3 外部事实

- PowerRename 文档列出的功能与变量 (附录 A) 以 2025-01-23 版为准; 其 "文件创建时间" 在 Android 上不可靠 (ext4 / f2fs 无稳定 birth time, `BasicFileAttributes.creationTime` 常等于修改时间), 故 D17 改用修改时间并提供 `now`.
- PowerRename 使用 ECMAScript 正则 (std::regex ECMAScript 语法, 可选 Boost); 本插件用 Rhino 取得同样的方言且与脚本一致 (D6 / D15).
- Android 文件名: 单段 255 字节 (ext4 / f2fs), 可移动存储常为 FAT32 / exFAT (不区分大小写, 禁用 `\ / : * ? " < > |`).
- 平台版本插件当前 `1.9.1` (`AutoJs6-Gradle-Platform-Versions/version.properties`), 建仓时以该值或更新的已发布版本为准.

---

## 5. 目标架构

### 5.1 数据流: 脚本路径

```text
files.batchRename.plan(paths, options)
  -> augment BatchRename (参数规范化, RegExp 提取 source/flags, 错误映射)
  -> BatchRenameTargetCollector (宿主枚举: 目录递归, 父目录既有名, 上限)         [宿主]
  -> BatchRenamePluginHost.plan(request)  --Binder-->  BatchRenamePluginService  [插件]
                                                        -> RenameEngine (D16)
  <- plan Bundle (逐项 newName / status / reason)
  -> BatchRenamePlan NativeObject (items 可改 excluded)
files.batchRename.apply(plan)
  -> BatchRenameExecutor (重新校验, 排序, 两阶段, 逐项结果, onProgress)          [宿主]
  -> BatchRenameJournalStore (写入批次)                                          [宿主]
  <- BatchRenameResult (batchId, 逐项状态)
files.batchRename.undo(result | batchId) -> BatchRenameJournalStore + Executor 逆序恢复
```

### 5.2 数据流: 管理器路径

```text
ExplorerView 多选工具栏 / 文件夹菜单 / 重命名对话框 "更多"
  -> ExplorerActionLauncher (v25 catalog 校验, session 创建)                     [宿主]
  -> BatchRenameActivity (Compose)                                                [插件]
       -> session.listChildren (D10b / includeSubfolders)
       -> RenameEngine 实时预览 (与脚本路径同一引擎)
       -> session.renameEntries(entries, conflictPolicy)  --Binder-->  宿主 session [宿主]
            -> BatchRenameExecutor + BatchRenameJournalStore (与脚本路径同一执行器与日志)
       <- renameBatchId, 逐项状态  -> Snackbar "撤销" -> session.undoRenameBatch
  -> 宿主刷新列表 (notifyFileChanged), 版本历史重定位 (relocateHistory)
```

### 5.3 包结构

插件 (`app/src/main/java/io/github/supermonster003/autojs6/plugin/batch/rename/`):

```text
engine/            纯 JVM, 无 Android 依赖, 可被 JVM 单元测试完整覆盖
  RenameOptions, RenameItem, RenamePlan, PlanItemStatus
  RenameEngine                 单项流水线与批内冲突判定 (D16, D19, D20 的判定部分)
  regex/JsRegexEngine          接口; RhinoRegexEngine 实现 (D15)
  variables/VariableExpander   计数器 / 日期 / 随机; CounterSpec 解析 `${start=;increment=;padding=}`
  format/TextFormatter         lower / upper / title / capitalize (D18)
  local/NameNormalizer         NFC, 全角转半角, sanitize, trim, 序号接续 (D5 本土扩展)
  validate/NameValidator       D19
service/
  BatchRenamePluginService     IBatchRenamePlugin.Stub, Bundle <-> engine 编解码, 输入边界
  PluginRuntimeInfo            PluginInfo 纯数据映射 (供 JVM 测试)
explorer/
  BatchRenameExplorerActionService   IExplorerActionPlugin (catalog D23)
  ExplorerTargetEnumerator     listChildren 递归与父目录既有名
ui/
  BatchRenameActivity, BatchRenameScreen, BatchRenameViewModel, components/, theme/
data/
  PreferencesStore             DataStore (D25)
WakeActivity
```

宿主 (`D:/idea-projects/AutoJs6`):

```text
plugin-api/batch-rename-api/                         契约 (附录 C.1)
plugin-api/explorer-action-api/                      v25 (附录 C.2)
app/src/main/java/org/autojs/autojs/core/plugin/batchrename/
  BatchRenamePluginHost, BatchRenameTargetCollector, BatchRenameExecutor,
  BatchRenameJournalStore, BatchRenameErrorMapper
app/src/main/java/org/autojs/autojs/core/plugin/explorer/session/ExplorerActionHostSession.kt   v25 四个方法
app/src/main/java/org/autojs/autojs/runtime/api/augment/files/BatchRename.kt                   脚本 API
app/src/main/java/org/autojs/autojs/ui/common/ScriptOperations.java                             重命名对话框 "更多"
docs/dev/batch-rename-plugin-protocol-v1.md, docs/dev/explorer-action-protocol-v25.md
```

### 5.4 身份派生表

| 项 | 值 |
| --- | --- |
| `{PROJECT_NAME}` | `AutoJs6-Plugin-Batch-Rename` |
| `{ROOT_PROJECT_NAME}` | `autojs6-plugin-batch-rename` |
| `{APP_NAME}` (不可翻译) | `Batch Rename` |
| `{APPLICATION_ID}` / namespace | `io.github.supermonster003.autojs6.plugin.batch.rename` |
| `{PLUGIN_ID}` / `{PLUGIN_ENGINE}` / `{PLUGIN_VARIANT}` | `batch-rename` / `batch-rename` / `default` |
| 插件中心身份服务 | `BatchRenamePluginInfoService`, action `org.autojs.plugin.INFO`, category `batch-rename` (P0.1 新增, 参照 3-Shell Terminal 的 INFO + 能力 + 管理器三服务先例) |
| 能力服务 | `BatchRenamePluginService`, action `org.autojs.plugin.BATCH_RENAME`, category `batch-rename` |
| 管理器服务 | `BatchRenameExplorerActionService`, action `org.autojs.plugin.EXPLORER_ACTION`, category `android.intent.category.DEFAULT` (与 File Inspector 一致); `getInfo()` 的 engine 为 `explorer-action` |
| `{CAPABILITY_API}` | `batch-rename-api` (宿主 `plugin-api/batch-rename-api`, AAR 复制到 `libs/batch-rename-api.aar` 并以 `implementation` 打包, 插件在自身进程中需要 AIDL stub; 另需 `libs/common-plugin-api.aar`, `libs/explorer-action-api.aar`, Rhino slim jar; 全部登记在 `locks/host-api-aars.lock`) |
| 宿主契约包 | `org.autojs.plugin.batchrename.api` |
| 宿主桥接包 | `org.autojs.autojs.core.plugin.batchrename` |
| 脚本 API | `files.batchRename` (`$files.batchRename` 同对象) |
| `{REQUIRES_HOST_VERSION}` | P0.1 占位 5352 (当前宿主 versionCode), 待回填 (D26) |
| 向导目录 | `PluginInstallWizardCatalog` `FILE_MANAGER` 分类, `official("batch.rename")`, 回退标题 `Batch Rename` |
| `plugin_description` (zh-Hans) | `使用查找替换, 正则表达式, 计数器与日期变量批量重命名文件与文件夹` (句尾无点号, 不含 "AutoJs6"; P0.1 定稿, 10 语言见 `values*/strings.xml`) |
| 发布文件名 | `autojs6-plugin-batch-rename-v{VERSION_NAME}-{CRC32}.apk` (单 Universal APK, D28) |
| 索引清单 | `release-manifests/io.github.supermonster003.autojs6.plugin.batch.rename/<versionCode>.json` |
| 许可证 | MPL-2.0 (`LICENSE` 完整文本); `THIRD_PARTY_NOTICES.md` 记录 Rhino (MPL-2.0), Compose / AndroidX (Apache-2.0), 宿主 AAR (MPL-2.0) |

---

## 6. 阶段总览

| 阶段 | 版本 | 主题 | 状态 |
| --- | --- | --- | --- |
| P0 | 1.0.0 | 仓库骨架: 构建链, Manifest, 资源, 文档生成器, CI, AGENTS.md, 图标 | 已完成 (2026-10-15; 工作流待首次推送后的远端运行) |
| P1 | 宿主 6.8.0 | 宿主契约 `batch-rename-api`, explorer-action v25, 注册与入口 (含重命名对话框 "更多") | 未开始 |
| P2 | 1.0.0 | 插件引擎 (纯 JVM): 正则, 变量, 格式化, 本土扩展, 校验, 冲突 | 未开始 |
| P3 | 宿主 6.8.0 | 宿主执行器, 撤销日志, 脚本 API `files.batchRename` | 未开始 |
| P4 | 1.0.0 | 插件服务, 管理器入口, Compose 界面 | 未开始 |
| P5 | 1.0.0 | 文档, 声明, 四仓库同步, 兼容矩阵, 发布与索引登记 | 未开始 |
| P6 | 1.1.0 | 候选: EXIF / XMP 变量, 预设, 宿主重命名历史页面, 其他 | 未开始 |

依赖顺序:

```text
P0 (骨架)
 -> P1 (宿主契约, v25, 注册)            P2 (引擎) 可与 P1 并行, 只依赖 P1.1 的常量草案
     -> P3 (宿主执行器 + 脚本 API)      依赖 P1 契约与 P2 引擎 (设备测试需要插件 APK)
     -> P4 (插件服务 + 界面)            依赖 P1 v25 与 P2 引擎
         -> P5 (文档, 发布)             依赖 P3, P4 全部通过
             -> P6 (1.1.0)
```

发布门禁 (每个阶段的 "(发布)" 条目都指向本清单):

- R1 `version.properties` 的 `VERSION_NAME` 更新, `VERSION_BUILD` = 提交数 + 1; 10 种语言 `.changelog/lang_*.json` 补齐该版本的 `hint` / `feature` / `fix` / `improvement` / `dependency`, 日期为发布当日.
- R2 `.readme/lang_*.json` (功能, 安装, 脚本示例, 兼容性, 常见问题), `plugin_instruction` 10 语言, `strings.xml` 描述与 README 一致性校验通过; 界面有变化时重采 `docs/images/screenshots` (API 33 设备或模拟器, 1080 x 2340, 合成素材, 明暗背景可读).
- R3 `py D:/idea-projects/prepush_check.py` 通过 (文档校验, `check_repository.py`, 构建号容差, Gradle 单元测试); 附录 F 设备矩阵中每台在线设备完成冒烟 (安装, 激活, 三处入口各一次, 脚本 `plan` / `apply` / `undo` 各一次), 记录型号 / API / APK 摘要.
- R4 release APK 经 `appendDigestToReleasedFiles` 收集, 原生库 16 KB 对齐校验通过, 生成 `SHA256SUMS`, 创建轻量 tag `vX.Y.Z` 与 GitHub Release (APK + SHA256SUMS), 复核下载产物哈希.
- R5 官方索引: `official-repositories.json` 加入 `AutoJs6-Plugin-Batch-Rename` (首次), `release-manifests/.../<versionCode>.json` 登记 (`supportedAbis`, `requiresHostVersion` 与实际一致), 运行 `tools/generate_official_plugin_index.py` 与索引仓库的 `generate_markdown.py`.
- R6 宿主侧有改动时, 宿主 `.changelog` 10 种语言与 `docs/dev/` 同步, 并说明最低宿主版本变化; 文档, d.ts, 离线文档, Ace 四仓库按各自 `AGENTS.md` 生成, 版本号递增并提交.

---

## P0: 仓库骨架

目标: 建立可构建, 可测试, 可生成文档的插件仓库, 尚无业务代码.

### P0.1 构建链与身份

- [x] (插件) 按 `AUTOJS6_PLUGIN_NEW_REPO_AGENTS.md` 生成骨架: `settings.gradle.kts` (平台插件与 native-alignment 插件 `1.9.1`, 位于 `includeBuild("build-logic")` 之前), 根 `build.gradle.kts` (`com.android.application` 与 `org.jetbrains.kotlin.plugin.compose` 以平台属性声明并 `apply false`), `build-logic`, `version.properties` (`VERSION_NAME=1.0.0`, compileSdk / targetSdk 37 与宿主一致, minSdk 24, `OVERRIDDEN_*=NONE`, 首个含它的提交 `VERSION_BUILD=2`), Gradle Wrapper 9.5.0 (与 43 个插件仓库相同), `.gitignore` (由宿主版本裁剪) / `sign.properties` / `app/sm003.jks` 从宿主复制. 证据 (E1): 提交 `cd353e1`; Temurin 模拟命令 `--no-daemon -Djava.vendor="Eclipse Adoptium" -Djava.vendor.version=Temurin-21.0.12.1+1 :app:assembleDebug :app:testDebugUnitTest` 通过, 版本决策块只打印一次: Gradle 9.5.0 / AGP 9.3.3 (auto-specified) / Kotlin 2.3.20 (nearest-lower-matched) / KSP 2.3.12 / R8 8.13.19, 无 `checkAarMetadata` 错误; `git check-ignore` 确认 `sign.properties`, `app/sm003.jks`, `local.properties`, `AGENTS.md`, `*.pre-platform-versions.bak`, `releases/` 被忽略. 偏差: `build-logic` 改取 3-Block Compose 已精简的版本 (去掉 LibDeployer / 7z 解压 / 本地 AAR 注册及其 commons-compress / xz 依赖), 不再从 3-Maple Image / Pinyin 复制后精简.
- [x] (插件) `app/build.gradle.kts`: `buildFeatures { compose = true; aidl = true; resValues = true }`, Compose BOM `2026.09.00` 与 Material 3, release R8 + `shrinkResources`, 无 ABI 拆分 (D28, 理由注释), `appendDigestToReleasedFiles` 收集单 APK 并追加 CRC32, `verifySignedReleaseArtifacts` 校验签名, APK 集合, 原生库集合与不压缩存储. 证据 (E1 + E5 预检): 提交 `cd353e1`; 宿主 AAR 由 `locks/host-api-aars.lock` 驱动 `implementation(files(...))`, 配置阶段对缺失 / 多余 / 摘要不符的 AAR 直接失败; `:app:appendDigestToReleasedFiles` 产出 `releases/autojs6-plugin-batch-rename-v1.0.0-c059b7b0.apk` (283,341 B, 骨架尚未引用 Compose 代码), 四个 `libandroidx.graphics.path.so` 均为 STORED 且 `Release.json` 16 KB 对齐通过. 偏差: (1) 契约 AAR 改为 `implementation` 而非原写的 `compileOnly`: 本插件是进程外 Binder 插件, 运行时需要 AIDL stub; `batch-rename-api.aar` 在 P1.1 产出后加入锁文件即可, 无需草案 jar; (2) Compose BOM 不管理 `androidx.activity`, `activity-compose` 显式固定为 1.12.2 (与 3-Maple Image 同一 BOM 下的版本); (3) Rhino slim jar 仍按计划在 P2.1 接入.
- [x] (插件) Manifest: `org.autojs.permission.PLUGIN`, `WAKE_ACTIVITY` + `WakeActivity`, `org.autojs.plugin.info.AUTHOR`, 服务上的 `requiresHostVersion` meta-data (引用 `@string/plugin_requires_host_version`, D26 占位 5352), `NATIVE_PAGE_ALIGNMENT=16384`, `BatchRenamePluginInfoService` (INFO, category `batch-rename`), `BatchRenamePluginService` (`org.autojs.plugin.BATCH_RENAME`, category `batch-rename`, 在 P1.1 之前返回只携带 `IBatchRenamePlugin` 描述符的占位 Binder), `BatchRenameExplorerActionService` (`EXPLORER_ACTION` + DEFAULT, 协议 24 的空动作目录, engine `explorer-action`), `BatchRenameActivity` (PLUGIN 权限, `EXPLORER_ACTION_EXECUTE` + DEFAULT, P4.2 之前立即取消), 全部 exported 组件受 PLUGIN 权限保护; 无 launcher; `allowBackup=false` + `dataExtractionRules` + `fullBackupContent=false`; 无存储 / 网络权限. 证据: 提交 `c84e840` (图标引用在 `629e0df`); `aapt2 dump xmltree` 核对签名 release APK 的组件, 权限与 meta-data; 人工设备冒烟 (E3 级别的发现验证, 非 instrumentation): 签名 release APK 安装到 Sony XQ-AT72 (QV710AF65F, API 31) 与 Sony G8441 (BH900ASK9E, API 28), `cmd package query-services` 按三组正式 action / category 查询各自只命中预期组件, `org.autojs.permission.PLUGIN: granted=true` (与宿主签名一致), 验证后已卸载. 偏差: 新增 INFO 服务 (路线图原未列出), 使插件在 P1.3 注册 `BATCH_RENAME` 之前就能以批量重命名身份出现在插件中心. 未执行: Binder 往返, Manifest 契约自动化测试 (P0.2), ColorOS 激活实测.
- [x] (插件) 资源: 11 个 values 目录的 `plugin_description` (5.4 的定稿措辞, 句尾无标点, 默认与 `values-en` 一致); `app_name` / `plugin_author` / `plugin_id` / `plugin_engine` / `plugin_variant` / `plugin_requires_host_version` / `plugin_version_date` 经 `resValue` 生成 (不另建 `strings_donottranslate.xml`), PluginInfo 从这些构建资源读取; 图标由 Icon Studio 1.3 (`.icons/recipe.json`, profile `catalog`, 透明背景, Optical geometry 归一化) 从两张临时源图 (`mipmap*/ic_launcher.png`, 三行名称条 + 右向箭头, 512 px, 亮 `#272727` / 暗 `#D8D8D8`, 同一 alpha) 生成插件中心与系统图标, Manifest 指向 `@mipmap/ic_icon_studio_application`; `raw/keep.xml` 保留跨包读取的 `@raw/plugin_instruction`. 证据: 提交 `c84e840` + `629e0df`; `generate_icon_studio.py --check` 校验 15 个资源; `aapt2 dump resources` 确认 release 包保留 `mipmap/ic_plugin_center`, `mipmap/ic_launcher`, `raw/plugin_instruction`, `color/ic_plugin_center_background` 与全部身份字符串 (加 `keep.xml` 前 `raw/plugin_instruction` 曾被资源收缩移除); `:app:lintDebug` 0 错误, 警告为 `IconDuplicates` x 2 (图标规范第 3 节认可的用途重复), `IconLauncherShape` x 2 (透明背景的兼容图标), `GradleDependency` x 2 与 Gradle 版本提示. 临时图稿待维护者源图替换 (附录 D Q1).
- [x] (插件) `.readme/` + `.changelog/` 10 语言 JSON 与模板, `.python/generate_markdown.py` (+ `.bat`, `--check`, 取自 Pinyin), `.python/check_repository.py` (取自 Pinyin; 标点规则由固定的 CJK 字符表改为覆盖全部语言的非 ASCII 标点判定, 源文件不再含全角字面量), 根 `README.md` 为简体中文 (v1.0.0 记为开发预览, 下文功能为 1.0.0 设计目标); `LICENSE` MPL-2.0; `THIRD_PARTY_NOTICES.md`; `libs/README.md` + `locks/host-api-aars.lock`. 证据: 提交 `629e0df`; `generate_markdown.py` 生成 36 个产物且 `--check` 通过; `check_repository.py` 无错误; 已跟踪文本的非 ASCII 标点只出现在与其余 48 个仓库逐字节一致的 Icon Studio 便携内核 `icon_studio_runtime.py` 及 Gradle 上游 `gradlew` 中 (COMMON_AGENTS 的上游文件例外).
- [x] (插件) `AGENTS.md`: 参考规范裁剪版, 写明本仓库事实: 三个服务与两个 Activity 的身份, 无独立入口, 无存储权限, 无 ABI 拆分理由 (D28), 契约 AAR 的锁定方式, 最低宿主版本的三处同步位置, 宿主提交只落本地, 验证顺序, 推送前检查. 证据: 本地文件 (与全部官方插件仓库一致被 `.gitignore` 忽略, 不入库).

### P0.2 测试与 CI

- [x] (插件) JVM 守卫: `ManifestContractTest` (唯一权限, application 属性, meta-data, Wake, 重命名 Activity, 三个服务的 action / category / permission / `requiresHostVersion` meta-data, 全部 exported 组件受 PLUGIN 权限保护, 无 launcher), `PluginRuntimeInfoTest` (PluginInfo 纯数据映射, `supportedAbis` 显式空列表, 身份常量与 `app/build.gradle.kts` 的 resValue, `requiredHostVersion`, 无 ABI 拆分, `.readme/common.json` 对齐, 声明的 explorer-action 协议不高于暂存 AAR), `StringResourceParityTest` (11 个 values 目录键相同且排序, `values` 与 `values-en` 一致, 身份字符串只来自构建, 描述规则, 描述与 README synopsis 相等, 各语言 `plugin_instruction` 与 `raw/keep.xml`, 图标产物), `ApplicationTextPunctuationTest` (源码, 资源, JSON, 文档与工作流中全部语言的非 ASCII 标点; 上游的 Icon Studio 内核与 `gradlew` 按规范例外), `HostApiAarLockTest` (锁文件, `libs/` 产物集合, 摘要, 无原生代码, `libs/README.md` 与 `THIRD_PARTY_NOTICES.md` 记录, 构建脚本读取锁文件). 证据 (E2): 提交 `1c99280`; `:app:testDebugUnitTest` 5 个类 26 项全部通过; 变异检查: 在 `values-zh` 描述中换入全角逗号, `ApplicationTextPunctuationTest` 与 `StringResourceParityTest` 失败; 去掉管理器服务的 PLUGIN 权限, `ManifestContractTest` 失败 (两次均由脚本恢复原字节). 偏差: 尚无格式化占位符, `StringResourceParityTest` 暂不比对占位符, 引入首个带参数的字符串时补上.
- [x] (插件) 设备契约测试 `BatchRenamePluginContractTest` (路线图原文只写在 CI 中运行, 此处补记): Wake 激活契约, WAKE / EXECUTE / 三组服务 action 各自只命中一个组件, 无 launcher 入口, 显式绑定与 Binder 描述符, 用显式 Parcel 按宿主的 AIDL 事务号调用 `getInfo()` (INFO 与 Explorer Action 两种身份), `getActionCatalog()` (协议 24, 空动作列表) 与 `openArchiveV11()` (返回不可恢复错误), 占位能力 Binder 拒绝事务, 重复绑定, 单个通用 APK 与四个未压缩的 graphics-path 库并实际加载. 证据 (E3, 2026-10-15): `:app:connectedDebugAndroidTest` 7 项在 Sony XQ-AT72 (QV710AF65F, API 31), Sony G8441 (BH900ASK9E, API 28), `AVD_API_24` (google_apis x86, 与 CI 同配置) 与 `AVD_API_35_16K` (16 KB 页) 上全部通过, 测试包运行后自动卸载, 两台 AVD 测后已关闭. 未执行: ColorOS 激活实测, 与真实宿主的跨进程调用 (P1.3 / P3).
- [x] (插件) `.github/workflows/build.yml` (完整克隆后运行 `check_repository.py` 与 `generate_markdown.py --check`; 单元测试, debug / androidTest / 未签名 release 装配, lint; API 24 x86 与 API 35 x86_64 模拟器上的 `connectedDebugAndroidTest`, 单行命令并预先隐藏系统错误对话框), `markdown.yml` (Windows) 与 `icon-studio.yml` (Ubuntu + Windows, Pillow 12.3.0). 证据: 提交 `df5cb8b`; 仓库尚无远端, 只做了本地校验: 临时虚拟环境中的 PyYAML 解析三个工作流, 引用的脚本均存在, 每个作业都有超时, action 版本与兄弟仓库主流一致 (checkout v7, setup-java v6, setup-python v7, upload-artifact v7, android-emulator-runner v2.38.0); verify-build 作业的 Python 与 Gradle 步骤在本地原样运行通过. 签名 release 门禁 (`verifySignedReleaseArtifacts`) 只在本地运行, CI 不持有签名. 待办: 首次推送后记录远端运行结果. 本地 PowerShell 运行时 `-Pautojs.gradle...` 需加引号, 否则会在点号处被拆开 (CI 的 bash 不受影响).
- [x] (插件) 初始提交按 "身份与构建骨架 / 契约与激活 / 资源与文档 / 测试与 CI" 拆分, 每笔提交前 `VERSION_BUILD = 提交数 + 1`, 最终 `|VERSION_BUILD - git rev-list --count HEAD| <= 10` 且工作区干净. 证据: `cd353e1` 构建骨架, `c84e840` 身份与激活, `629e0df` 资源与文档, `b456cee` P0.1 证据, `1c99280` 测试, `df5cb8b` CI, 本提交为 P0.2 证据; 每笔提交时 `VERSION_BUILD` 均等于提交数, `py D:/idea-projects/prepush_check.py` 通过, 工作区干净.

---

## P1: 宿主契约, 协议 v25 与注册

目标: 宿主拥有可编译, 可测试的契约, v25 协议与全部入口; 契约在 P1.1 结束时冻结为 V1. 本阶段宿主侧尚无执行器与脚本 API (P3).

### P1.1 契约模块 `plugin-api/batch-rename-api`

- [x] (宿主) `settings.gradle.kts` `pluginApi` 列表加入 `batch-rename-api`; 模块 `build.gradle.kts` 为 `com.android.library`, `api(project(":plugin-api:common-plugin-api"))`, `buildFeatures.aidl = true`, `aarMetadata.minCompileSdk = 36` (与 epub-api 一致), `consumer-rules.pro` keep `org.autojs.plugin.batchrename.api.**`; `app/build.gradle.kts` `implementation(project(":plugin-api:batch-rename-api"))`. 证据: 宿主提交 `aa4fd4f6d` (本地, 待维护者合并; 宿主 `VERSION_BUILD=3777`, versionCode 5429).
- [x] (宿主) 常量与 key 对象: `BatchRenamePluginActions`, `BatchRenamePluginIds`, `BatchRenameContract`, `BatchRenameCapabilityKeys`, `BatchRenameLimits`, `BatchRenameRequestKeys`, `BatchRenameParentKeys`, `BatchRenameItemKeys`, `BatchRenameOptionKeys`, `BatchRenameResultKeys`, `BatchRenameValues`, `BatchRenameErrorCodes`. 证据: 同上. 偏差 (以模块源码为准, 附录 C.1 已改写): 状态, 冲突策略等闭合取值集合合并为 `BatchRenameValues` (与 `ExplorerActionValues` 同风格), 父目录键独立为 `BatchRenameParentKeys`; 词汇表比草案更细: 请求增加 `timeZone` / `now` (日期变量可复现), 父目录增加 `restrictedCharacters` (FAT32 / exFAT), 条目增加 `excluded` / `overwrites`, 原因码 14 个 (取代草案的 `reasonCode` + `reasonMessage`, 文案由两侧各自本地化), 正则以 `regex` + `regexFlags` + `matchAll` + `caseSensitive` 表达, 日期来源取值 `modified` / `now` / `none` (脚本的 `false` 由宿主映射为 `none`), 新增总字符预算 `MAX_REQUEST_CHARS = 262_144` 与 `MAX_EXISTING_NAMES_PER_PLAN = 20_000`, 使单个请求远低于 Binder 事务缓冲区.
- [x] (宿主) `IBatchRenamePlugin.aidl`: `PluginInfo getInfo()` 居首, `Bundle plan(in Bundle request)` 其后; 注释写明不为空, 非法输入以带 `errorCode` 的 Bundle 应答而不抛异常, 同步, 无状态, 可在 Binder 线程池并发调用, 只能追加事务. 证据: 同上.
- [x] (测试) `BatchRenameContractTest` 快照全部字面量 (发现与身份, 版本与能力键, 上限, 全部 Bundle 键, 取值集合, 错误码, 无重复), `BatchRenameAidlOrderTest` 冻结事务顺序 (`getInfo` 第 1 个, 同步接口). 证据 (E2): `:plugin-api:batch-rename-api:testDebugUnitTest` 2 个类 7 项通过.
- [x] (插件) 契约 AAR 复制到 `libs/batch-rename-api.aar`, 更新 `locks/host-api-aars.lock`, `libs/README.md`, `THIRD_PARTY_NOTICES.md`; `BatchRenamePlugin` 的发现字面值与描述符改为引用契约常量, 只保留插件侧的取值 (最低宿主版本, 声明的协议版本, 说明资源引用). 证据: 本条所在提交; AAR SHA-256 `315e37b94297582fbb975da71b163fcb3ac78d2a870f03b5847c5a42b1d207cb` (只含 classes.jar, 无原生代码); 插件 JVM 守卫 26 项与设备契约测试 7 项 (Sony XQ-AT72, API 31) 通过. 能力服务仍是占位 Binder, 直到 P4.1 接入引擎.

### P1.2 explorer-action 协议 v25

- [ ] (宿主) `ExplorerActionProtocol.VERSION = 25`; `ExplorerActionCatalogKeys.TARGET_RENAME = "targetRename"`; `ExplorerActionValues.PLACEMENT_RENAME_DIALOG = 4`; `ExplorerActionHostSessionKeys` / `Values` 新增 rename 批次与条目的 key 与状态 (附录 C.2); `IExplorerActionHostSession.aidl` 末尾追加 `renameEntries` / `queryRenameBatch` / `listRenameBatches` / `undoRenameBatch`, 注释标注 `v25`, 保持 v1-v24 事务号不变.
- [ ] (宿主) `ExplorerActionCatalogPolicy`: `targetRename` 仅对协议 >= 25, `TARGET_FILE` / `TARGET_DIRECTORY` / `TARGET_MIXED`, `CARDINALITY_SINGLE` / `MULTIPLE`, `ACCESS_READ_ONLY`, Activity 呈现, 无输出模式, 无 `filesystemPath`, 无 `targetTrash` 时有效; `PLACEMENT_RENAME_DIALOG` 仅对 `CARDINALITY_SINGLE` 且协议 >= 25 有效; 旧宿主通过既有的宣告版本检查拒绝协议 25 catalog.
- [ ] (宿主) `ExplorerActionEntryPoint.RENAME_DIALOG(PLACEMENT_RENAME_DIALOG)`; `ExplorerActionRegistry.actionsFor` 对该入口的目标数量与父目录一致性检查.
- [ ] (宿主) `ExplorerActionHostSession` v25 方法先以 "校验 + 委托给 P3.1 执行器" 的形态落地; P3 之前用 `UnsupportedOperationException` 占位并在测试中标注; 条目校验: `targetId` 属于本 session, `relativePath` 为空或是 `listChildren` 可达的后代 (规范化后不得逃逸目标), `newName` 通过 D19.
- [ ] (测试) `ExplorerActionCatalogPolicyTest` 新增 v25 用例 (合法声明, 每条非法组合各一例); AIDL 事务号快照测试更新.
- [ ] (宿主) `docs/dev/explorer-action-protocol-v25.md` (目的, 二进制兼容性, catalog 声明, session 方法, 校验规则, 撤销语义, 与 v16 / v21 回收站的关系), 格式参照 `explorer-action-protocol-v24.md`.
- [ ] (插件) 更新 `libs/explorer-action-api.aar` 到 v25 构建, 锁文件同步.

### P1.3 注册, 协议文档, changelog 与版本回填

- [ ] (宿主) `AndroidManifest.xml` `<queries>` 加入 `org.autojs.plugin.BATCH_RENAME`; `InstalledPluginRepository.queryDeclaredPluginServices` 与 `discoverPackage` 两张表加入 `BatchRenamePluginHost.ACTION_BATCH_RENAME`; `PluginInstallWizardCatalog` `FILE_MANAGER` 分类加入 `entry(official("batch.rename"), "Batch Rename", FILE_MANAGER, standard = true)`; ProGuard / R8 keep 规则; 插件中心的分类图标与排序检查.
- [ ] (宿主) `core/plugin/batchrename/BatchRenamePluginHost` (`AidlPluginHost` 薄封装, `plan(context, request)`, 以 `MediainfoPluginHost` 为模板), 暂不被脚本 API 调用.
- [ ] (宿主) `docs/dev/batch-rename-plugin-protocol-v1.md` (契约目的, 请求 / 响应 Bundle 结构, 上限, 错误码, 流水线顺序 D16, 与 explorer-action v25 的分工 D13).
- [ ] (宿主) `.changelog` 10 种语言 6.8.0 条目: `feature` "文件管理器支持批量重命名 (多选工具栏 / 文件夹菜单 / 重命名对话框 \"更多\" 菜单, 需安装 Batch Rename 插件)" 与 "files.batchRename 模块 ..." (后者在 P3 落地时补全), `improvement` 插件中心新增 Batch Rename 条目. 措辞按 `AUTOJS6_CHANGELOG_RELEASE_ISSUE_AGENTS.md`, 不写协议号与 Binder 细节.
- [ ] (宿主) 临时 `REQUIRES_HOST_VERSION` 回填到插件 Manifest / `PluginRuntimeInfo` / README (D26).

### P1.4 宿主入口 (D10)

- [ ] (宿主) 多选工具栏: 无需新增代码 (既有机制自动列出 `SELECTION_TOOLBAR` 动作); 验证 `TARGET_MIXED` + `targetRename` 动作在文件与文件夹混选时可见, 压缩包视图内不可见.
- [ ] (宿主) 文件夹溢出菜单: 既有 `OVERFLOW_MENU` 对 `TARGET_DIRECTORY` 的动作列出机制; 验证 "批量重命名其中的项目" 出现在 `menu_dir_options` 之后的插件区.
- [ ] (宿主) 重命名对话框 "更多" (D10c, D29): `ScriptOperations.rename` 的 `showNameInputDialog` 增加右上角溢出图标 (自定义标题视图, 48 dp 触控目标, 无障碍标签 "更多"), 菜单项来自 `ExplorerActionRegistry.actionsFor(surface, RENAME_DIALOG, listOf(item))`; 无已安装动作时显示 "批量重命名..." 并跳转插件中心安装向导; 选择动作后关闭对话框并经 `ExplorerActionLauncher` 启动插件; 插件返回后刷新列表. 文件与文件夹两种重命名入口 (`ExplorerBuiltInActionHandler`, `ExplorerPageViewHolder`) 均覆盖.
- [ ] (宿主) 10 语言字符串: `text_batch_rename` 等新增条目按 name 排序, ASCII 标点, `values` 与 `values-en` 一致.
- [ ] (测试) 宿主 JVM: 对话框菜单在 "无插件 / 有插件 / 插件未启用" 三种注册表快照下的条目; instrumentation: 对话框 "更多" 可见且可点击 (API 24 AVD 与一台真机).

---

## P2: 插件引擎 (纯 JVM)

目标: `engine/` 包在无 Android 依赖下实现 D5 全部能力, 由 JVM 单元测试完整覆盖; 本阶段不触碰 Binder 与界面.

### P2.1 正则引擎 (D6, D15)

- [ ] (插件) `JsRegexEngine` 接口 (`compile(source, flags)`, `replace(input, regex, replacement)`, `test`), `RhinoRegexEngine` 实现: 每线程一个 `Context` (`initSafeStandardObjects`), 通过 `String.prototype.replace` 求值, 替换串以 JS 字符串传入 (不经 JS 源码拼接, 避免注入); 编译错误映射为 `INVALID_OPTIONS` 并附 Rhino 的错误信息与位置.
- [ ] (插件) Rhino 来源决策 (附录 D Q4): 先用宿主 `slimRhinoJar` 产物, SHA-256 入锁; 评估 Maven Central `org.mozilla:rhino` 最新版是否通过 P2.1 的特性对照 (命名组与 `$<name>`, 后行断言, `s` / `u` / `y`, Unicode 属性转义, `$&` `` $` `` `$'` `$$`, 超长输入, 灾难性回溯超时); R8 规则保证只保留 regexp 子集, 记录 release 体积.
- [ ] (测试) `EcmaRegexParityTest`: 表驱动用例 (>= 60 条) 覆盖 PowerRename 文档示例 (附录 A 第 3 组) 与 ECMAScript 特性; 纯文本查找转义 (`.`, `$`, `(`, `\` 等); `matchAll` / `caseSensitive` 到 `g` / `i` 的映射; 捕获组引用在 "非全部匹配" 下的行为与文档一致.

### P2.2 流水线, 变量与选项 (D16, D17)

- [ ] (插件) `RenameOptions` 解析与规范化 (默认值 D17, 非法枚举值报 `INVALID_OPTIONS`, 未知 key 忽略但记录), `RenameItem` / `RenamePlan` / `PlanItemStatus` 模型.
- [ ] (插件) `VariableExpander`: 计数器 `${}` 与 `${start=X;increment=X;padding=X}` (任意顺序, 多个计数器各自独立, 负数与超大值边界), 日期时间 20 个变量 (`$YYYY` `$YY` `$Y` `$MMMM` `$MMM` `$MM` `$M` `$DDDD` `$DDD` `$DD` `$D` `$hh` `$h` `$mm` `$m` `$ss` `$s` `$fff` `$ff` `$f`, 最长匹配优先, 大小写敏感, `$Mm` 不展开), 随机 `${rstringalnum=N}` `${rstringalpha=N}` `${rstringdigit=N}` `${ruuidv4}` (`SecureRandom`, N 上限 256); 展开结果中的 `$` 转义为 `$$`.
- [ ] (插件) `RenameEngine.plan(items, options)`: 按 D16 顺序逐项处理, `target` 切分规则 (无扩展名, 以点开头的隐藏文件, 多重扩展名只取最后一段, 文件夹无扩展名), `includeFiles` / `includeFolders` / `includeSubfolders` 过滤 (过滤掉的项状态 `excluded`), 未命中项 `unchanged`.
- [ ] (测试) 表驱动用例覆盖附录 A 全部文档示例 (如 `Image_${padding=4;increment=2;start=10}_` 与 `$MMM-$DD-$YY-powertoys`), 计数器只对改名项递增, `enumerate=false` 时 `${}` 为字面, 日期 `now` 与 `modified` 两种来源, 随机串长度与字符集.

### P2.3 文本格式化与本土扩展 (D18, D5)

- [ ] (插件) `TextFormatter` 四种模式, locale 注入; `NameNormalizer`: NFC 规范化, 全角转半角 (U+FF01-U+FF5E 与 U+3000), `sanitize` 与 `sanitizeReplacement`, `trim`, 序号接续 (`continueNumbering=true` 时扫描父目录既有名中与替换模板匹配的最大序号, 计数器从其后开始).
- [ ] (测试) en / zh / tr locale 的大小写转换; 组合字符与代理对 (emoji) 在规范化, 截断与格式化下不被破坏; 全角转半角对照表; 序号接续在既有 `Photo_0012.jpg` 存在时从 13 开始.

### P2.4 校验与冲突 (D19, D20 判定部分)

- [ ] (插件) `NameValidator` (D19 规则, 返回原因码), `ConflictDetector`: 同批重名, 与父目录既有名冲突 (排除本批将改名的旧名), 大小写敏感 / 不敏感两种比较 (由请求中的父目录属性决定), 四种策略的方案生成 (`number` 模式默认 ` ({n})`, 可配置, 追加后再次校验长度与冲突, 上限 999).
- [ ] (测试) 交换 (A->B, B->A) 判定为可执行且标记 `twoPhase`; 仅大小写改名在不敏感父目录下标记 `twoPhase`; `fail` 策略下任一 `conflict` / `invalid` 使方案 `rejected`; `skip` / `number` / `overwrite` 的逐项状态; 2000 项大批量计划在 JVM 下 < 300 ms.

### P2.5 编解码与上限

- [ ] (插件) `service/BundleCodec`: 请求 / 响应 Bundle 与 engine 模型互转 (附录 C.1 结构), 输入边界 (条目数, 名称长度, 查找 / 替换长度, 父目录数, 未知 kind), 超限返回 `LIMIT_EXCEEDED`; 不信任宿主传入的任何字符串 (名称只作数据).
- [ ] (测试) 编解码往返与边界用例; 畸形 Bundle (缺 key, 类型错误) 不抛未捕获异常.

---

## P3: 宿主执行器, 撤销日志与脚本 API

目标: 宿主能以脚本路径完成 plan / apply / undo / history, 并把同一执行器暴露给 v25 session.

### P3.1 执行器与日志 (D20, D21, D30)

- [ ] (宿主) `BatchRenameTargetCollector`: 路径解析 (相对脚本工作目录), 去重, 目录递归 (不跟随符号链接, 深度 32, 总数 2000), 收集父目录既有名与大小写敏感性探测, 产出请求 Bundle.
- [ ] (宿主) `BatchRenameExecutor.apply(plan, policy, progress)`: 重新校验每项 (存在, `lastModified` / `length` 未变, 否则 `stale`), 深度降序, 两阶段改名, `overwrite` 受害文件先进回收站, 逐项结果, 进度回调, 已改名文件的 `relocateHistory` (版本历史) 与 `notifyFileChanged`; `undo(batchId)`: 逆序恢复, 占用检查, 回收站受害文件恢复.
- [ ] (宿主) `BatchRenameJournalStore`: JSON 持久化, 20 批上限与淘汰, 损坏文件跳过并记日志, 批次 id 为随机不可猜测字符串; `history(limit)` 读取.
- [ ] (宿主) `ExplorerActionHostSession` v25 四个方法接入执行器 (替换 P1.2 的占位), 条目到路径的解析经 session 既有的目标与 `listChildren` 路径校验.
- [ ] (测试) 宿主 JVM: 临时目录下的执行器用例 (交换, 仅大小写, 父子同批, 单项失败不回滚, stale 跳过, 撤销后再撤销报 `UNDO_UNAVAILABLE`, 日志淘汰); v25 session 用例 (非法 `relativePath` 逃逸拒绝, 跨 session 批次不可撤销).
- [ ] (测试) instrumentation `ExplorerActionRenameV25InstrumentationTest` (以 `ExplorerActionTargetTrashV24InstrumentationTest` 为模板): 真实插件 APK 或测试桩 catalog, `renameEntries` 往返, 撤销, 进程重启后 `listRenameBatches` 仍可见.

### P3.2 脚本 API `files.batchRename` (D2, D9, D27)

- [ ] (宿主) `augment/files/BatchRename.kt`: `keys = listOf(AugmentableKey("batchRename", withDollarPrefix = false))`, `Augmentable` + `Invokable`; `ScriptRuntime.kt:1044` 改为 `Files(this).augment(target, files).also { BatchRename(this).augment(it) }`; `selfAssignmentFunctions`: `plan`, `apply`, `undo`, `history`, `isAvailable`, `planAsync`, `applyAsync`, `undoAsync`; 参数守卫 (`ArgumentGuards`), `RegExp` 对象提取 (`NativeRegExp` 的 `source` / `flags`), `onProgress` 在脚本线程回调, 返回 `NativeObject` (plan / result / history 结构见附录 B.3), `BatchRenameErrorMapper` 映射错误码与插件中心引导文案.
- [ ] (宿主) Async 变体: 复用宿主既有 Promise 桥接 (与其他 `xxxAsync` 方法一致), 后台线程执行, 异常转 reject.
- [ ] (测试) 宿主 JVM: 参数规范化 (字符串 / 数组 / RegExp / 非法类型), 默认值, 错误码映射, plan 对象 `excluded` 回传; instrumentation: 安装插件后在真机上 `plan` / `apply` / `undo` / `history` 往返, 未安装时 `isAvailable()` 为 `false` 且 `plan` 抛 `PLUGIN_UNAVAILABLE`.
- [ ] (宿主) `.changelog` 10 语言补全 `files.batchRename` 条目 (参阅 项目文档 链接), `docs/dev/batch-rename-plugin-protocol-v1.md` 补充脚本路径章节.

---

## P4: 插件服务, 管理器入口与界面

目标: 插件可被宿主发现并完成三处入口的完整闭环.

### P4.1 服务与 catalog (D23)

- [ ] (插件) `BatchRenamePluginService`: `getInfo()` (`PluginRuntimeInfo` 纯数据 + Android 读取, `supportedAbis = emptyArray()`, capabilities 含 `requiresHostVersion`, `batchRenameContractVersion`, `explorerActionProtocolVersion`, `batchRenameFeatures`), `plan()` 经 `BundleCodec` 调引擎, 并发安全, 无状态.
- [ ] (插件) `BatchRenameExplorerActionService`: `getActionCatalog()` 三个动作 (D23), 协议 25, `getInfo()` 与能力服务一致; `BatchRenameActivity` 的 `EXPLORER_ACTION_EXECUTE` 入口解析请求 (目标, session token, 协议版本), 协议低于 25 时显示本地化错误并退出.
- [ ] (测试) JVM: catalog 快照测试, `ExplorerActionManifestTest` (每个 catalog Activity 带 EXECUTE + DEFAULT); instrumentation `BatchRenamePluginContractTest`: action / category 发现只命中一个服务, 显式绑定与 descriptor, `getInfo` 字段, `plan` happy path / 非法参数 / 超限, 绑定解绑不泄漏.

### P4.2 界面 (D4, D11, D24)

- [ ] (插件) 主题与脚手架: Compose + Material 3, 跟随宿主语言 / 夜间 / 主题色 (`AutoJs6HostSettingsContract`, 回退系统), 预测性返回, 进程恢复 (`SavedStateHandle` 保存选项与输入, 目标集由请求重建).
- [ ] (插件) 查找 / 替换输入: 历史下拉 (D25), 变量插入按钮 (底部面板列出计数器 / 日期 / 随机变量及一行说明, 点击插入到光标处), 正则编译错误即时提示.
- [ ] (插件) 选项区: 正则, 全部匹配, 大小写敏感, 作用范围 (分段按钮), 包含文件 / 文件夹 / 子文件夹, 文本格式化 (下拉), 计数器, 随机串, 日期来源, 本土扩展 (折叠组), 冲突策略 (折叠组, `overwrite` 选中时弹出确认).
- [ ] (插件) 预览列表: `ExplorerTargetEnumerator` 经 `listChildren` 分页枚举 (包含子文件夹时递归, 进度指示, 2000 上限提示), 每行复选框 / 图标 / 原名 / 新名 / 状态 (改名: 主题色, 冲突或非法: 错误色并显示原因, 未命中: 次要色), 列头切换 "仅显示将重命名", 去抖 150 ms 后台重算, 大列表 `LazyColumn` 与 key 稳定.
- [ ] (插件) 应用流程: 底栏统计 (将改名 / 冲突 / 排除), "应用" 禁用条件 (有冲突且策略 `fail`, 或无改名项), 执行进度对话框, 结果摘要, Snackbar "撤销" (`undoRenameBatch`), 顶栏 "撤销上一批" (读取 `listRenameBatches` 中本插件最近一批), 失败项列表可复制原因.
- [ ] (插件) 10 语言字符串, 无障碍 (内容描述, 焦点顺序, TalkBack 下状态朗读), RTL, 大字体, 横屏与平板宽度 (>= 600 dp 时列表与选项左右分栏).
- [ ] (测试) Compose 测试: 输入后预览更新, 复选框排除, 过滤切换, 冲突禁用应用, 历史下拉; 截图采集 (R2).

### P4.3 持久化与设置 (D25)

- [ ] (插件) `PreferencesStore`: 上次选项, MRU, "记住上次选项" 开关; 顶栏溢出 "清除历史" / "恢复默认选项"; 不记录路径与文件名.
- [ ] (测试) DataStore 往返, MRU 去重与上限, 关闭 "记住上次选项" 后不恢复.

### P4.4 本阶段联调

- [ ] (测试) 三处入口在 API 24 AVD, API 28 / 33 / 35 真机上各走一遍: 多选 (文件 + 文件夹混合, 含子文件夹), 文件夹菜单 (含 / 不含子文件夹), 重命名对话框 "更多" (单文件与单文件夹); 撤销后文件名与版本历史均恢复; 宿主列表即时刷新.
- [ ] (测试) 失败路径: 插件未安装 / 未启用 / 版本过低 / 宿主协议 24 (旧宿主) / 目标在执行前被删除 / 可移动存储 (FAT32) 上仅大小写改名.

---

## P5: 文档, 声明, 四仓库同步与发布

### P5.1 文档 (AutoJs6-Documentation)

- [ ] (文档) `api/files.md` 新增 `batchRename` 条目组 (当前风格: `## [m+] batchRename`, `### files.batchRename(paths, options)`, `### files.batchRename.plan(paths, options)` 等, 版本徽章 `6.8.0`), 开头加 "由外置 Batch Rename 插件提供, 调用前必须安装并启用与当前 AutoJs6 版本兼容的插件, 未发现可用插件时抛出异常" 的说明段 (措辞与 `mediainfo.md` 一致); 选项表, 变量表 (附录 A), 冲突策略, 撤销, 错误码, 示例 (>= 6 个, `let`, 可运行); `api/progress.md` 更新; `api/sidebar.md` 无需新页 (挂在 files 下).
- [ ] (文档) 运行 `generator/auto-generate-for-autojs6.bat`, 同步 `AutoJs6-Plugin-Offline-Docs`, 两仓库版本号与宿主对齐后提交.

### P5.2 声明 (AutoJs6-TypeScript-Declarations, Ace Editor)

- [ ] (文档) `declarations/autojs6/aj6-int-files.d.ts`: `Internal.Files` 增加 `batchRename: Internal.FilesBatchRename`; 新增 `Internal.FilesBatchRename` 可调用接口与 `BatchRenameOptions` / `BatchRenamePlan` / `BatchRenamePlanItem` / `BatchRenameResult` / `BatchRenameHistoryEntry` 类型 (附录 B.4 草案), JSDoc 含 `@example` 与 "Source code summary (zh-CN: 源代码摘要)", 头部 `@Source` 指向宿主 augment 与桥接文件; 以 "provided by the external Batch Rename plugin" 说明插件依赖.
- [ ] (文档) Ace Editor 仓库同步同一 d.ts, 运行 `:app:generateAutoJs6LspDeclarations`; 两仓库版本号 +1 与版本名递增 (minor) 后提交. `aj6dts.bat -Publish` 只在宿主 Java 签名变化时运行, `aj6-int-*.d.ts` 需手工同步.

### P5.3 README, changelog 与兼容矩阵

- [ ] (插件) `.readme/lang_*.json`: 简介, 功能 (对照 PowerRename 的用户可感知差异: 修改时间代替创建时间, 本土扩展, 撤销), 安装 (插件中心), 文件管理器用法 (三处入口, 截图), 脚本示例 (与文档一致), 兼容性 (最低宿主版本, 可移动存储说明), 常见问题 (正则与 JS 一致, 为什么计数器不递增, 为什么对话框里看不到入口), 发行历史, 许可证与第三方声明; `plugin_instruction` 10 语言.
- [ ] (插件) `.changelog` 10 语言 `v1.0.0` 条目 (`feature` 为主, 不写协议号); `generate_markdown.py` 与 `--check` 通过.
- [ ] (测试) 兼容矩阵 (附录 F): 每台在线设备完成 R3 冒烟; 16 KB 页设备或 AVD 一次; 低端设备 (G8441, API 28) 2000 项预览耗时与内存记录.
- [ ] (测试) 性能与体积: 2000 项 `plan` 往返 (宿主计时) < 1 s (API 33 真机); 界面 500 项预览重算 < 100 ms; release APK 体积与 D28 预算对照.

### P5.4 1.0.0 发布

- [ ] (宿主) 最终 `REQUIRES_HOST_VERSION` 回填 (D26), 宿主 `docs/dev/` 与 `.changelog` 终稿; 宿主侧全部提交落本地并在本路线图记录 hash, 提示维护者合并.
- [ ] (发布) R1-R4: 版本, changelog, README, 推送前检查, 签名发布, tag `v1.0.0`, GitHub Release.
- [ ] (索引) R5: `official-repositories.json` 加入仓库名 (README 中的 "48 个官方项目" 同步为 49), 发布清单登记, 生成索引与索引 README.
- [ ] (文档) R6: 四仓库提交 hash 回填本节.

---

## P6: 1.1.0 候选

进入前由维护者逐项拍板是否纳入; 未拍板前只作意向.

- [ ] (插件 / 宿主) 照片元数据变量 (PowerRename EXIF / XMP 组, 附录 A 第 2 组): 宿主以只读 `ParcelFileDescriptor` 随请求交给插件 (脚本路径) 或插件经 `openFile` 读取 (管理器路径), `androidx.exifinterface` 解析, 变量 `$CAMERA_MAKE` ... `$DATE_TAKEN_SS`, 仅对图片 MIME 展开, 读取失败展开为空串并在预览中提示; 性能: 懒读取, 并发上限, 2000 项预算.
- [ ] (插件) 预设: 保存 / 加载 / 删除命名的选项集 (DataStore), 界面顶栏入口, 脚本 `options.preset` 读取同一存储 (需经 Binder 暴露, 评估是否值得).
- [ ] (宿主) 文件管理器 "重命名历史" 页面 (与回收站页面同级): 列出 `BatchRenameJournalStore` 批次, 逐批撤销, 清理 (附录 D Q3).
- [ ] (插件) 工具栏图标: v26 允许 catalog 声明动作图标资源名, 宿主多选工具栏不再对所有插件动作用同一归档图标 (附录 D Q8).
- [ ] (插件) 脚本 `files.batchRename.preview(name, options)`: 单名试算 (纯函数, 不触盘), 便于脚本自行组合.
- [ ] (插件) 拼音 / 转写变量 (`$PINYIN` 等): 依赖 Pinyin 插件, 跨插件调用需宿主中转, 暂缓评估 (附录 G).

---

## 附录 A. 参考功能对照 (PowerRename -> Batch Rename)

来源: `learn.microsoft.com/en-us/windows/powertoys/powerrename` (2025-01-23). "脚本选项" 列为附录 B 的 option 名.

第 1 组: 主面板 (1.0.0, 核心组)

| PowerRename | Batch Rename 1.0.0 | 脚本选项 |
| --- | --- | --- |
| Search for / Replace with | 同 | `search`, `replace` |
| Use regular expressions (ECMAScript, 可选 Boost) | ECMAScript (Rhino), 无 Boost 开关; 后行断言等已在 ECMAScript 2018 内 | `regex` (或传 `RegExp`) |
| Match all occurrences | 同 | `matchAll` (或 `g` 标志) |
| Case sensitive | 同, 默认不敏感 | `caseSensitive` (或无 `i` 标志) |
| Apply to: Filename only / Extension only / 默认两者 | 同 | `target: "name" \| "extension" \| "all"` |
| Include files / folders / subfolders | 同 | `includeFiles`, `includeFolders`, `includeSubfolders` |
| Text formatting: lowercase / UPPERCASE / Title case / Capitalize Every Word | 同 | `format: "none" \| "lower" \| "upper" \| "title" \| "capitalize"` |
| Enumerate items: `${}` `${increment=X}` `${padding=X}` `${start=X}`, 可组合 `${padding=4;increment=2;start=10}` | 同 | `enumerate` |
| Random string values: `${rstringalnum=X}` `${rstringalpha=X}` `${rstringdigit=X}` `${ruuidv4}` | 同 | `random` |
| Preview pane: Original 列勾选, Renamed 列切换 | 同 (D11) | `plan.items[i].excluded` |
| Undo (Explorer Ctrl+Z) | 宿主持久化批次撤销 (D8) | `undo`, `history` |
| Settings: Restore flags on launch, auto-complete, MRU, Show recently used strings | 记住上次选项, MRU 与自动补全 (D11); 无独立设置页 | 无 |
| Settings: Show in context menu / Hide icon / Use Boost | 不适用 | 无 |
| File creation date and time: `$YYYY` `$YY` `$Y` `$MMMM` `$MMM` `$MM` `$M` `$DDDD` `$DDD` `$DD` `$D` `$hh` `$h` `$mm` `$m` `$ss` `$s` `$fff` `$ff` `$f` | 同一套变量, 基于修改时间 (D17), 可选当前时间 | `dateTime: "modified" \| "now" \| false` |

第 2 组: 照片元数据 (1.1.0, P6)

`$CAMERA_MAKE` `$CAMERA_MODEL` `$LENS` `$ISO` `$APERTURE` `$SHUTTER` `$FOCAL` `$FLASH` `$WIDTH` `$HEIGHT` `$AUTHOR` `$COPYRIGHT` `$LATITUDE` `$LONGITUDE` `$ALTITUDE` `$DATE_TAKEN_YYYY` `$DATE_TAKEN_YY` `$DATE_TAKEN_MM` `$DATE_TAKEN_DD` `$DATE_TAKEN_HH` `$DATE_TAKEN_mm` `$DATE_TAKEN_SS` `$EXPOSURE_BIAS` `$ORIENTATION` `$COLOR_SPACE`.

第 3 组: 文档中的正则示例 (作为 P2.1 对照用例)

| Search for | Replace with | 预期 |
| --- | --- | --- |
| `(.*).png` | `foo_$1.png` | PNG 文件名前加 `foo_` |
| `(.*).png` | `$1_foo.png` | PNG 文件名后加 `_foo` |
| `(.*)` | `$1.txt` | 追加 `.txt` |
| `(^\w+\.$)\|(^\w+$)` | `$2.txt` | 仅无扩展名时追加 `.txt` |
| `(\d\d)-(\d\d)-(\d\d\d\d)` | `$3-$2-$1` | `29-03-2020` -> `2020-03-29` |
| `^(.{n})(.*)` | `$1foo$2` | 第 n 个字符后插入 `foo` |
| `^.{n}` | 空 | 删除前 n 个字符 |
| `.+?(?=bar)` / `foo[\s\S]*bar` / `^foo.*bar$` | 任意 | 匹配范围与文档一致 |

第 4 组: 本插件新增 (PowerRename 没有)

| 能力 | 脚本选项 |
| --- | --- |
| Unicode 规范化 (NFC) | `normalize: "nfc" \| "nfd" \| false` |
| 全角转半角 | `halfWidth` |
| 非法字符清理与保留名处理 | `sanitize`, `sanitizeReplacement` |
| 首尾空白与尾部点号修剪 | `trim` |
| 从既有文件名解析序号后接续 | `continueNumbering` |
| 冲突策略 | `onConflict: "fail" \| "skip" \| "number" \| "overwrite"`, `conflictNumberPattern` |
| 进度回调 | `onProgress(done, total, item)` |

---

## 附录 B. 脚本 API 草案

### B.1 方法

| 调用 | 返回 | 说明 |
| --- | --- | --- |
| `files.batchRename(paths, options)` | `BatchRenameResult` | 等价于 `apply(plan(paths, options))` |
| `files.batchRename.plan(paths, options)` | `BatchRenamePlan` | 只计算不执行; `items[i].excluded` 可改后再 `apply` |
| `files.batchRename.apply(plan[, applyOptions])` | `BatchRenameResult` | `applyOptions.onProgress`, `applyOptions.onConflict` 可覆盖 plan 中的策略 |
| `files.batchRename.undo(result \| batchId)` | `BatchRenameUndoResult` | 撤销一批 |
| `files.batchRename.history([limit])` | `BatchRenameHistoryEntry[]` | 最近批次 (含管理器路径产生的批次), 最新在前 |
| `files.batchRename.isAvailable()` | `boolean` | 插件已安装, 启用且兼容 |
| `files.batchRename.planAsync / applyAsync / undoAsync` | `Promise<...>` | 同上的异步变体 |

`paths`: `string | string[]`, 相对路径按脚本工作目录解析; 可为文件或目录 (D30).

### B.2 options

| 名称 | 类型 | 默认 | 说明 |
| --- | --- | --- | --- |
| `search` | `string \| RegExp` | 必填 | 查找串; `RegExp` 时忽略 `regex` / `matchAll` / `caseSensitive`, 由其 flags 决定 (D17) |
| `replace` | `string` | `""` | 替换串, 支持 `$1` `$<name>` `$&` `$$` 与附录 A 的变量 |
| `regex` | `boolean` | `false` | |
| `matchAll` | `boolean` | `false` | |
| `caseSensitive` | `boolean` | `false` | |
| `target` | `"name" \| "extension" \| "all"` | `"all"` | |
| `includeFiles` / `includeFolders` / `includeSubfolders` | `boolean` | `true` | |
| `format` | `"none" \| "lower" \| "upper" \| "title" \| "capitalize"` | `"none"` | |
| `enumerate` | `boolean` | `false` | 关闭时 `${...}` 为字面 |
| `random` | `boolean` | `false` | |
| `dateTime` | `"modified" \| "now" \| false` | `"modified"` | |
| `normalize` | `"nfc" \| "nfd" \| false` | `false` | |
| `halfWidth` / `sanitize` / `trim` / `continueNumbering` | `boolean` | `false` | |
| `sanitizeReplacement` | `string` | `"_"` | |
| `onConflict` | `"fail" \| "skip" \| "number" \| "overwrite"` | `"fail"` | |
| `conflictNumberPattern` | `string` | `" ({n})"` | `{n}` 为序号 |
| `onProgress` | `(done, total, item) => void` | 无 | 仅 `apply` 路径 |

### B.3 返回对象

```js
// plan
{
    id: "p_7f3a...",          // 本次计划的随机 id, apply 时与日志关联
    options: { ... },         // 规范化后的选项
    items: [{
        path: "/sdcard/Pictures/IMG_0001.jpg",
        name: "IMG_0001.jpg",
        kind: "file",         // "file" | "dir"
        depth: 0,
        newName: "Photo_0010_0001.jpg",
        status: "rename",     // "rename" | "unchanged" | "conflict" | "invalid" | "excluded"
        reason: null,         // 冲突或非法的原因码与说明
        excluded: false,      // 脚本可改为 true
        twoPhase: false,
    }],
    counts: { rename: 12, unchanged: 3, conflict: 0, invalid: 0, excluded: 1 },
    rejected: false,          // onConflict 为 "fail" 且存在 conflict / invalid 时为 true
}
// result
{
    batchId: "b_19c4...",
    counts: { renamed: 12, skipped: 0, failed: 0, stale: 0 },
    items: [{ path, newPath, status: "renamed" | "skipped" | "failed" | "stale" | "excluded", reason }],
    undoable: true,
}
// history entry
{ batchId, createdAt, source: "script" | "explorer", counts, undoable }
```

### B.4 d.ts 草案 (节选)

```ts
declare namespace Internal {
    interface FilesBatchRename {
        (paths: string | string[], options: BatchRenameOptions): BatchRenameResult;
        plan(paths: string | string[], options: BatchRenameOptions): BatchRenamePlan;
        apply(plan: BatchRenamePlan, options?: BatchRenameApplyOptions): BatchRenameResult;
        undo(target: BatchRenameResult | string): BatchRenameUndoResult;
        history(limit?: number): BatchRenameHistoryEntry[];
        isAvailable(): boolean;
        planAsync(paths: string | string[], options: BatchRenameOptions): Promise<BatchRenamePlan>;
        applyAsync(plan: BatchRenamePlan, options?: BatchRenameApplyOptions): Promise<BatchRenameResult>;
        undoAsync(target: BatchRenameResult | string): Promise<BatchRenameUndoResult>;
    }
    class Files extends org.autojs.autojs.runtime.api.Files {
        readonly batchRename: FilesBatchRename;
    }
}
```

### B.5 示例

```js
// 1. 日期前缀 + 计数器, 先预览再执行
let plan = files.batchRename.plan("/sdcard/DCIM/Camera", {
    search: /^IMG_(\d+)/,
    replace: "$YYYY-$MM-$DD_${padding=3}_$1",
    enumerate: true,
    includeSubfolders: false,
});
plan.items.filter(it => it.status === "rename").forEach(it => log(it.name, "->", it.newName));
if (!plan.rejected) {
    let result = files.batchRename.apply(plan, { onProgress: (done, total) => log(done + "/" + total) });
    log(result.counts);
}

// 2. 一步到位, 冲突自动编号
let r = files.batchRename(["./a.txt", "./b.txt"], { search: "", replace: "note", target: "name", onConflict: "number" });

// 3. 撤销最近一批
files.batchRename.undo(files.batchRename.history(1)[0].batchId);
```

---

## 附录 C. 契约草案

### C.1 `plugin-api/batch-rename-api`

P1.1 已落地 (宿主 `aa4fd4f6d`), 契约以宿主模块源码 `plugin-api/batch-rename-api/src/main` 为准, 字面值由 `BatchRenameContractTest` 冻结; 下面只列结构摘要.

```java
interface IBatchRenamePlugin {
    org.autojs.plugin.common.api.PluginInfo getInfo();
    Bundle plan(in Bundle request);
}
```

| 对象 | 内容 |
| --- | --- |
| `BatchRenamePluginActions` | `INFO`, `BATCH_RENAME = "org.autojs.plugin.BATCH_RENAME"`, `SERVICE_CATEGORY = "batch-rename"`, `PLUGIN_PERMISSION` |
| `BatchRenamePluginIds` | `ID` / `ENGINE` = `batch-rename`, `VARIANT_DEFAULT = "default"`, `PACKAGE_NAME` |
| `BatchRenameContract` | `CONTRACT_VERSION = 1` (MIN / MAX 同为 1), `REGEX_DIALECT_ECMASCRIPT` |
| `BatchRenameCapabilityKeys` | `requiresHostVersion`, `batchRenameContractVersion`, `batchRenameFeatures`, `batchRenameMaxItems`, `batchRenameRegexDialect` |
| `BatchRenameRequestKeys` | `contractVersion`, `options`, `parents`, `items`, `locale`, `timeZone`, `now` |
| `BatchRenameParentKeys` | `parentId`, `existingNames`, `caseInsensitive`, `restrictedCharacters` |
| `BatchRenameItemKeys` | 请求: `index`, `parentId`, `name`, `kind`, `depth`, `lastModified`, `length`, `excluded`; 应答: `index`, `newName`, `status`, `reason`, `twoPhase`, `overwrites` |
| `BatchRenameOptionKeys` | 与附录 B.2 的脚本选项同名 (22 个), `search` 拆为 `search` + `regex` + `regexFlags` (`m` `s` `u` `y`) + `matchAll` + `caseSensitive` |
| `BatchRenameResultKeys` | `contractVersion`, `errorCode`, `errorMessage`, `rejected`, `items`, `counts` |
| `BatchRenameValues` | 条目类型, 作用范围, 格式化, 日期来源, 规范化, 冲突策略, 状态 (5 个), 原因 (14 个), 功能 (`counter`, `dateTime`, `random`, `cleanup`) |
| `BatchRenameErrorCodes` | `UNSUPPORTED_CONTRACT_VERSION`, `INVALID_REQUEST`, `INVALID_OPTIONS`, `LIMIT_EXCEEDED`, `INTERNAL_ERROR` |
| `BatchRenameLimits` | 条目 2000, 父目录 512, 既有名称合计 20000, 单个名称 1024 字符, 文本选项 4096 字符, 请求合计 262144 字符, 随机串 256, 编号上限 999, 结果名称 255 字节 |

### C.2 explorer-action v25

- Catalog: `targetRename` (Boolean, `ExplorerActionCatalogKeys.TARGET_RENAME`); placement 新值 `PLACEMENT_RENAME_DIALOG = 4`.
- Session (追加在 `openRelatedFile` 之后):

```java
/** v25. */ Bundle renameEntries(in Bundle request);
/** v25. */ Bundle queryRenameBatch(String renameBatchId);
/** v25. */ Bundle listRenameBatches();
/** v25. */ Bundle undoRenameBatch(String renameBatchId);
```

`renameEntries` 请求: `entries` (ArrayList<Bundle>: `targetId`, `relativePath`, `newName`), `conflictPolicy` (int: 0 fail, 1 skip, 2 number, 3 overwrite), `conflictNumberPattern`; 响应: `renameBatchId`, `renameBatchState` (available / partial / failed), `items` (`targetId`, `relativePath`, `newRelativePath`, `state`, `reasonCode`), `counts`. 撤销, 查询与列表的 Bundle 形态对齐 v21 的 `targetTrashBatch*` 系列 key 命名.
- 校验: 条目属于本 session 的目标或其 `listChildren` 可达后代; `newName` 通过宿主 D19 校验 (宿主不信任插件方案, 只把它当作请求); 批次 id 不可猜测; 执行与日志复用 P3.1 执行器.

---

## 附录 D. 待决事项

| 编号 | 事项 | 影响阶段 | 默认 (未拍板时) |
| --- | --- | --- | --- |
| Q1 | 图标源图: 维护者提供两张黑白源图 (亮色模式深色图案, 暗色模式浅色图案), 主题建议 "两行文本行与一个替换箭头" | P0.1 | 临时源图 + 标记待替换 |
| Q2 | 撤销日志保留批次数 (默认 20) 与是否对脚本暴露清理方法 | P3.1 | 20 批, 不暴露清理 |
| Q3 | 宿主是否增加 "重命名历史" 页面 (1.1.0) | P6 | 不做, 以 Snackbar 撤销与脚本 `history` 为准 |
| Q4 | Rhino 来源: 宿主 slim jar (锁 SHA) 还是 Maven Central 发布版 | P2.1 | 宿主 slim jar |
| Q5 | 单批上限 2000 是否足够; 超过时是否自动分批 (脚本路径) | P2.5 / P3.1 | 2000, 超限报错 |
| Q6 | `target` 默认 `"all"` (与 PowerRename 一致) 还是 `"name"` (更保守) | P2.2 | `"all"` |
| Q7 | 多选工具栏在插件缺失时是否显示安装引导条目 | P1.4 | 不显示 (沿用既有行为), 对话框 "更多" 显示 |
| Q8 | 工具栏插件动作图标 (当前统一为归档图标) 是否在 v26 允许 catalog 自定义 | P6 | 不改 |
| Q9 | `number` 策略的默认模式 ` ({n})` 还是 `_{n}` | P2.4 | ` ({n})` (与宿主复制冲突写法一致) |

---

## 附录 E. 证据等级

- E1 代码与配置: 提交 hash 可追溯, 相关文件在仓库中.
- E2 JVM 单元测试: 测试类名, 用例数, 运行命令.
- E3 instrumentation: 测试类名 + 设备型号 + API 级别 (真机或 AVD) + 日期.
- E4 人工验收: 设备, 步骤, 结果, 截图路径 (`.bak/visual/<version>/`, 不入库).
- E5 发布产物: APK 文件名, SHA-256, tag, GitHub Release 链接, 索引清单文件名.

勾选 `[x]` 的条目至少满足 E1 + (E2 或 E3); 涉及界面的条目另需 E4; "(发布)" 条目需 E5. 未执行的验证 (例如 ColorOS 激活实测) 必须如实写 "未执行".

---

## 附录 F. 基线快照与设备矩阵

基线 (2026-10-15):

| 项 | 值 |
| --- | --- |
| 宿主 | `AutoJs6 master@944f6fe5cf` (P0.1 契约 AAR 取自 `6c151c900e`), `VERSION_NAME=6.8.0` (未发布), versionCode 5352 |
| explorer-action 协议 | v24 (`ExplorerActionProtocol.VERSION`) |
| 宿主 Rhino | `libs/org-mozilla-rhino-2_0_0-SNAPSHOT.jar` (命名组, 后行断言, `dotAll`, `sticky`, Unicode 属性已实现) |
| 平台版本插件 | `1.9.1` |
| 参照插件 | 3-Maple Image (Compose, explorer-action v24, 三处 catalog, `verifySignedReleaseArtifacts`), Pinyin (无独立入口, `check_repository.py`, 文档生成器, Icon Studio 配方), MediaInfo (可调用脚本模块与契约模板), 3-Block Compose (路线图与 P0 骨架写法, 精简版 build-logic), 3-Shell Terminal (INFO + 能力 + 管理器三服务) |

设备矩阵 (来自 `COMMON_AGENTS.md`; 2026-10-15 起已连接设备可自由安装 / 卸载官方插件):

| 设备 | API | 用途 |
| --- | --- | --- |
| Sony G8441 (BH900ASK9E) | 28 | 低端性能基线, 旧系统对话框 "更多" 布局 |
| Sony XQ-AT72 (QV710AF65F) | 31 | 常规回归 |
| Sony XQ-DQ72 (QV770340J7) | 33 | 常规回归 (该设备曾因 ART JIT 缺陷 ANR, 与应用无关) |
| Redmi 22120RN86C (bek749scrwv4wo8h) | 33 | 小屏 (720 x 1650) 布局, 无障碍超时较长 |
| Xiaomi Pad 23046RP50C (968e9f18) | 35 | 平板分栏布局 (>= 600 dp), 需先授予存储权限 |
| OnePlus OPD2413 (63751f63) | 35 | ColorOS 激活实测 (Wake Activity) |
| Samsung SM-A566B / SM-F971B (localhost:端口) | 36 / 37 | 16 KB 页, 最新系统 |
| AVD (`E:\.android\avd\`) | 24, 35, 37 (16 KB) | CI 同款契约测试, minSdk 边界 |

1.0.0 至少覆盖: API 24 AVD, API 28, API 33, API 35 真机各一台, 16 KB 页一次.

---

## 附录 G. 已评估暂缓

| 事项 | 结论 |
| --- | --- |
| 宿主内置批量重命名对话框 | 与 "插件拥有真实实现, 宿主只留入口" 冲突 (维护者第一轮已否决) |
| 插件自行申请存储权限 | 授权体验差且与宿主托管写入模型重复 (第一轮已否决) |
| ECMAScript -> java.util.regex 翻译层 | 与 Rhino 直接求值相比成本高且语义难以完全一致; D15 选择 Rhino |
| 跨目录移动 (`move`) | 与改名语义混淆, 宿主已有 "移动到"; 不纳入 |
| 压缩包内条目改名 | `IExplorerArchiveSession` 已有 `MUTATION_RENAME`, 归 3-Stack Archive |
| AI 生成正则 | 需联网或 3-Stone AI, 超出插件职责; 文档中提供正则速查即可 |
| 拼音 / 转写变量 | 需跨插件调用 (Pinyin / 3-Hanzi CC), 宿主中转协议未定; 1.1.0 后再评估 |
| MediaStore 扫描与相册式选择 | 由 3-Maple Image 负责, 本插件只处理宿主交来的目标 |
