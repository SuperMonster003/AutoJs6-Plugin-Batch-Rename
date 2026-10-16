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

### 2.1 1.1.0 固定决策 (D31-D42)

以下决策由维护者于 2026-10-16 经六轮选择题确认, 是 P6 的依据.

| 编号 | 决策 | 含义 |
| --- | --- | --- |
| D31 | 1.1.0 范围 | 纳入元数据变量, 拼音, 命名预设, 脚本单名试算, 宿主重命名历史页面, P4.4 失败路径补测; 工具栏动作图标 (Q8) 不做 |
| D32 | 发布与宿主依赖 | 合并为一个 1.1.0 发布; 允许提高最低宿主版本, 新的宿主接口随宿主 6.8.0 一起发布 |
| D33 | 实施顺序 | 插件独立项 (拼音, 预设, 单名试算) -> 宿主项 (元数据变量, 历史页面) -> 补测, 图标, 文档与发布 |
| D34 | 元数据变量 | PowerRename 第 2 组全部 25 个 (附录 A); 显式选项 `metadata` 开启 (界面 "元数据变量", 默认关闭), 关闭时按字面保留; 来源 EXIF + XMP (EXIF 缺作者, 版权等时读 XMP); 宿主解析后随请求传字段, 脚本与文件管理器两条路径一致, 插件不接触文件; 缺失展开为空串并在预览中标出, `$DATE_TAKEN_*` 缺失时回退到修改时间 |
| D35 | 拼音 | 插件内置单字 + 常用词组词典 (多音字按词组判断, 繁体同样转换, APK 仍在 3.5 MB 预算内); 同时提供名称整理选项 "汉字转拼音" 与替换变量 `${pinyin}`; 风格可选 (声调: 无 / 符号 / 数字; 音节分隔: 无 / 空格 / 下划线 / 短横线; 大小写: 小写 / 首字母大写; 仅首字母), 默认无声调, 无分隔, 小写 |
| D36 | 命名预设 | 插件界面保存 / 加载 / 重命名 / 删除; 脚本 `options.preset` 引用 (显式选项覆盖预设), `files.batchRename.presets()` 列出, `files.batchRename.importPresets(json)` 导入; 导入导出同时支持系统文件选择器 (无需存储权限) 与剪贴板; 预设存于插件, 由插件在计算方案时解析 |
| D37 | 脚本单名试算 | `files.batchRename.preview(nameOrItems, options)` 接受名字, `{ name, isDirectory, lastModified }` 或其数组, 返回 `{ newName, status, reason }` 或其数组; 数组时计数器按顺序递增, 只检查数组内重名; 不访问磁盘, 不展开元数据变量 |
| D38 | 重命名历史页面 | 宿主页面, 入口为文件管理器菜单 (与 "回收站" 同级) 与插件界面历史下拉末尾的 "全部历史..."; 列表 (来源, 状态, 时间, 条目数), 明细 (旧名 -> 新名与状态), 整批撤销, 删除单批, 清空, 按来源 / 状态筛选, 按文件名 (旧名或新名) 搜索 |
| D39 | 日志保留与清理 | 每个来源保留 20 / 50 / 100 批 (默认 20), 在历史页面的菜单中设置; 脚本 `deleteHistory(batchId)` (也接受 apply 的结果) 与 `clearHistory({ source })` 可清理全部批次, 包括文件管理器产生的批次 |
| D40 | P4.4 补测 | 版本过低的插件与旧宿主 (协议 24) 用专门构建的包测; FAT32 / exFAT 上只改大小写先在挂载 FAT32 虚拟 SD 卡的模拟器上测, 发布前在维护者租用的带 SD 卡三星设备上复核 |
| D41 | 图标 | 由我按 Q1 主题 ("两行文本行与一个替换箭头") 重新设计亮 / 暗两张矢量源图, 维护者确认后经 Icon Studio 生成并更新索引目录图标 |
| D42 | 工具栏动作图标 | 1.1.0 不做, Q8 维持现状 |
| D43 | 脚本导入同名预设 | `importPresets(json, { onConflict })` 默认 `skip` (保留已有预设, 结果中列出被跳过的名称), 可选 `overwrite` / `rename` (另存为 `名称 (n)`); 界面导入时询问覆盖 / 另存为副本 / 跳过 (2026-10-16 维护者选择) |
| D44 | 带预设的 `plan.options` | 显示合并后的完整选项: 插件在应答中回传 "默认值 < 预设 < 显式选项" 的结果 (契约追加 `options` 结果键), `apply` 按这份选项重算, 预设在 plan 与 apply 之间被改不影响结果 (同日选择) |
| D45 | 预设导出范围 | 管理面板中可多选, 默认全选; 文件与剪贴板使用同一种带版本号的 JSON 格式, 导入两者通用 (同日选择) |
| D46 | 预设入口 | 顶栏新增 "预设" 按钮: 下拉列出预设 (点按即加载), 末尾 "将当前选项保存为预设..." 与 "管理预设..." (管理面板内重命名 / 删除 / 导入 / 导出); 顶栏共 3 个图标 (同日选择) |
| D47 | 元数据数值格式 | 带单位且文件名安全: 光圈 `f2.8`, 快门 `1-250s` (分数用短横线; 1 秒及以上写 `2s` / `1.5s`), 焦距 `50mm`, 曝光补偿 `+0.3EV`, 海拔 `120m` (2026-10-16 维护者选择) |
| D48 | 拍摄时间 | `$DATE_TAKEN_*` 用 EXIF DateTimeOriginal 记录的当地时间原样展开, 不换算时区; 缺失时回退到修改时间 (D34) (同日选择) |
| D49 | 枚举文字 | 固定英文词: `$FLASH` 为 `On` / `Off`; `$ORIENTATION` 为 `0` / `90` / `180` / `270`, 镜像时加 `M` (如 `90M`); `$COLOR_SPACE` 为 `sRGB` / `AdobeRGB` / `Uncalibrated` (同日选择) |
| D50 | GPS 格式 | 十进制度, 6 位小数, 南纬与西经为负 (`31.230416` / `-121.473701`) (同日选择) |
| D51 | 重命名历史的入口 | 宿主入口为文件或文件夹 "重命名" 对话框右上角 "更多" 菜单 (现有 "批量重命名...") 末尾的 "重命名历史" (2026-10-16 维护者指定, 取代 D38 的 "文件管理器菜单, 与回收站同级": 宿主的回收站只在侧边栏); 插件界面入口相应放在顶栏 "更多" 菜单末尾, 而非 D38 所写的查找历史下拉 (该下拉只在有最近文本时出现) |
| D52 | 1.1.0 最低宿主版本 | 回填为 5455 (宿主 `dbf16a347`, 包含 P6 的全部宿主改动), 不再沿用 1.0.0 的 5438; v1.0.0 的 changelog 提示改为字面的 5438, 保留历史事实 (2026-10-16 维护者选择) |
| D53 | 三星 SD 卡复核 | 先完成 R1-R3, 复核通过后再打 tag 与登记索引 (同日选择). 维护者租用的三星折叠屏 SM-F971B (Android 17) 没有 SD 卡槽, 也无法在线增加; 改用 Android 的测试用虚拟 SD 卡 (`sm set-virtual-disk`) 或放弃该项, 待维护者决定 |
| D54 | 关联仓库推送 | 文档站, 离线文档, 声明 (4.42.0), Ace (2.4.0) 的提交随 1.1.0 发布一起推送 (同日选择) |

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
- 照片 EXIF / XMP 变量 (已纳入 1.1.0, D34).
- 宿主内置的 "重命名历史" 页面 (已纳入 1.1.0, D38).

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
| `{REQUIRES_HOST_VERSION}` | 5438 (宿主 `852afb776`, P5.4 回填; P1.3 临时值 5431, P0.1 占位 5352) (D26) |
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
| P1 | 宿主 6.8.0 | 宿主契约 `batch-rename-api`, explorer-action v25, 注册与入口 (含重命名对话框 "更多") | 已完成 (2026-10-15; 宿主 4 个提交在本地, 待维护者合并) |
| P2 | 1.0.0 | 插件引擎 (纯 JVM): 正则, 变量, 格式化, 本土扩展, 校验, 冲突 | 已完成 (2026-10-15; 服务的 `plan` 已接入引擎) |
| P3 | 宿主 6.8.0 | 宿主执行器, 撤销日志, 脚本 API `files.batchRename` | 已完成 (2026-10-15; 宿主提交在本地, 待维护者合并) |
| P4 | 1.0.0 | 插件服务, 管理器入口, Compose 界面 | 进行中 (P4.1 - P4.3 与真机联调已完成 2026-10-15; 剩三项失败路径) |
| P5 | 1.0.0 | 文档, 声明, 四仓库同步, 兼容矩阵, 发布与索引登记 | 已完成 (2026-10-16, v1.0.0 已发布并登记索引) |
| P6 | 1.1.0 | 拼音, 命名预设, 脚本单名试算, 元数据变量, 宿主重命名历史页面, P4.4 补测, 新图标 | 进行中 (D31-D51 定于 2026-10-16; P6.1 拼音, P6.3 脚本试算已完成; P6.2 预设已完成; P6.4 元数据变量, P6.5 重命名历史已完成; P6.6 补测除 exFAT 与三星 SD 卡复核外已完成; 下一步 P6.7) |

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
- R6 宿主侧有改动时, 宿主 `.changelog` (6.8.0 只写 `lang_zh-Hans.json`, 其他语言由维护者同步) 与 `docs/dev/` 同步, 并说明最低宿主版本变化; 文档, d.ts, 离线文档, Ace 四仓库按各自 `AGENTS.md` 生成, 版本号递增并提交.

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

- [x] (宿主) `ExplorerActionProtocol.VERSION = 25` (新增 `MAX_RENAME_ENTRIES_PER_REQUEST = 2_000`, `MAX_RENAME_BATCH_ID_LENGTH = 128`, `MAX_RENAME_BATCH_RESULTS = 20`, `MAX_RENAME_NAME_BYTES = 255`); `ExplorerActionCatalogKeys.TARGET_RENAME = "targetRename"`; `ExplorerActionValues.PLACEMENT_RENAME_DIALOG = 4`; `ExplorerActionHostSessionKeys` / `Values` 新增重命名条目, 批次与状态; `IExplorerActionHostSession.aidl` 在 `openRelatedFile` 之后追加 5 个事务, v1-v24 事务号不变. 证据: 宿主提交 `95c9f0455` (本地, 待维护者合并; `VERSION_BUILD=3778`, versionCode 5430). 偏差: 编写协议文档时发现插件无法得知文件目标父目录中的既有名称 (文件目标没有 `readSiblings` 就不能列出父目录, 而 `targetRename` 不能与之组合; 只选了文件夹时更无从列出), 因此追加第 5 个事务 `listRenameParent(offset, limit)`: 只分页返回请求目标共同父目录中条目的名称与类型, 以及 `RENAME_CASE_INSENSITIVE` / `RENAME_RESTRICTED_CHARACTERS` 两项文件系统特征, 不使任何条目变得可读; 另增 `RENAME_ALL_OR_NOTHING` 请求标志 (先全部复核, 任一不通过则一个都不改), 冲突策略由插件在方案中解决, 宿主只执行确切名称 (见附录 C.2).
- [x] (宿主) `ExplorerActionCatalogPolicy`: `targetRename` 仅对协议 >= 25, `ACCESS_READ_ONLY`, Activity 呈现, 无输出模式, 无未匹配文件探测, 无 `filesystemPath`, 无 `targetTrash`, 且不与 `readSiblings` / `playbackProgress` / 相关文件后缀组合时有效, 文件, 目录与混合目标均可; `PLACEMENT_RENAME_DIALOG` 仅对协议 >= 25, `CARDINALITY_SINGLE` 且声明了 `targetRename` 时有效; `matches` 接受新放置位置; 旧宿主通过既有的宣告版本检查拒绝协议 25 catalog. 证据: 同上.
- [x] (宿主) `ExplorerActionEntryPoint.RENAME_DIALOG(PLACEMENT_RENAME_DIALOG)`; `ExplorerActionRegistry.actionsFor` 无需改动 (既有的目标数量, 共同父目录与 `ExplorerActionPolicy.allows` 按放置位置过滤即覆盖新入口); `ExplorerActionLauncher` 对 `targetRename` 动作总是创建会话并传入 `allowTargetRename`. 证据: 同上.
- [x] (宿主) `ExplorerActionHostSession` 的 5 个 v25 方法先做访问检查 (调用方 UID, 动作声明了 `targetRename`, 会话未关闭), 随后抛出 `UnsupportedOperationException`; 会话工厂拒绝把重命名与输出, 替换或回收站权限组合. 真实的条目校验, 执行与日志随 P3.1 落地. 证据: 同上; 协议文档 "Status" 一节写明两步之间不得发布宿主.
- [x] (测试) `ExplorerActionCatalogPolicyTest` 新增 `protocolV25TargetRenameIsLimitedToReadOnlyActivitiesWithoutOtherSessionAuthority` (三种 Batch Rename 声明形态, 协议门限, 9 种非法组合) 与 `protocolV25RenameDialogPlacementOnlyHoldsSingleRenameActions` (单目标且须声明重命名, 匹配文件与文件夹, 仅在主文件管理器的重命名对话框入口出现); 新增 `ExplorerActionAidlOrderTest` (`plugin-api/explorer-action-api`, 冻结插件接口 5 个与宿主会话接口 35 个事务的顺序). 证据 (E2): 宿主 explorer 包 JVM 测试 118 项与契约模块 3 项通过.
- [x] (宿主) `docs/dev/explorer-action-protocol-v25.md` (目的, 状态, 二进制兼容性, 目录声明, 列出名称, 重命名, 历史与撤销, 客户端责任, 验证), 并在 `explorer-action-directory-browsing.md` 补充 `targetRename` 一条. 证据: 同上. 文档写明实现 `IExplorerActionHostSession.Stub` 的插件测试替身 (3-Maple Image, 3-Stack Archive, 3-Amber Player) 换用 v25 AAR 时需补齐 5 个方法.
- [x] (插件) 更新 `libs/explorer-action-api.aar` 到 v25 构建, 锁文件, `libs/README.md` 同步; `BatchRenamePlugin.EXPLORER_PROTOCOL_VERSION` 提升为 25 (目录仍为空). 证据: 本条所在提交; AAR SHA-256 `df4bcec8f68f502476c2eb41c8ff3ef513f30abfd28e06b94d1d235041cf3855`; 插件 JVM 守卫 26 项与设备契约测试 7 项 (Sony XQ-AT72, API 31) 通过. 未执行: 宿主 changelog (按 `AUTOJS6_CHANGELOG_RELEASE_ISSUE_AGENTS.md` 第 3.2 节, 用户可感知的变化随 P1.4 / P3 / P4 一并记录, 不为中间形态单列).

### P1.3 注册, 协议文档, changelog 与版本回填

- [x] (宿主) Manifest `<queries>` 加入 `org.autojs.plugin.BATCH_RENAME` (category `batch-rename`); `InstalledPluginRepository` 的服务查询表与 `discoverPackage` 映射加入 `BatchRenamePluginHost.ACTION_BATCH_RENAME`; `PluginCenterViewModel` 的 engine -> action 表与 `PluginCenterFragment` 的探测分派加入 Batch Rename (三服务包的条目仍以通用 INFO 端点为准, 默认启用); `PluginInstallWizardCatalog` `FILE_MANAGER` 分类末尾加入 `entry(official("batch.rename"), "Batch Rename", FILE_MANAGER)`; R8 keep 规则由契约模块的 `consumer-rules.pro` 提供 (P1.1), app 无需另加. 证据: 宿主提交 `dcc5bd98d` (本地, 待维护者合并; `VERSION_BUILD=3779`, versionCode 5431); 新增 `PluginCenterBatchRenameRegistrationTest` (engine 解析, INFO 端点优先, 默认启用, 向导分类) 与向导目录计数 48 项, 宿主插件中心 JVM 测试 155 项与 explorer 118 项通过. 偏差: 向导条目未设 `standard = true` (只在文件管理分类中列出, 不随标准方案自动安装), 是否纳入标准方案由维护者决定.
- [x] (宿主) `core/plugin/batchrename/BatchRenamePluginHost` (`AidlPluginHost` 薄封装: `discover`, `probe`, `queryServiceCount`, `plan(context, request)` 先确认插件声明的契约版本在宿主支持范围内再调用, RemoteException 后不重试; `contractVersionOf`), 暂不被脚本 API 调用. 证据: 同上.
- [x] (宿主) `docs/dev/batch-rename-plugin-protocol-v1.md` (目的, 发现, Binder 接口, 请求与选项, 应答, 上限, 宿主职责, 状态). 证据: 同上.
- [x] (插件) 临时 `REQUIRES_HOST_VERSION` 回填为 5431 (D26, 即宿主 `dcc5bd98d` 的 versionCode, 首个同时包含契约, 协议 v25 与注册的构建): `app/build.gradle.kts` 的 `requiredHostVersion` (Manifest meta-data 经 `@string/plugin_requires_host_version` 引用), `BatchRenamePlugin.REQUIRED_HOST_VERSION`, `.readme/common.json` 及其生成的 README / changelog / `plugin_instruction`. 证据: 本条所在提交; `PluginRuntimeInfoTest` 冻结 5431 并比对构建脚本与 `common.json`; `generate_markdown.py --check` 与 `check_repository.py` 通过. 原文把本条标为 "(宿主)", 实际改动都在插件仓库.
- [x] (插件) 能力服务提前实现为真实的 `IBatchRenamePlugin.Stub`: `getInfo()` 返回批量重命名身份及契约能力 (`batchRenameContractVersion=1`, `batchRenameFeatures` 为空数组, `batchRenameMaxItems=2000`, `batchRenameRegexDialect=ecmascript`), INFO 服务上报同一组能力; `plan()` 在引擎接入前不猜测方案: 缺少请求返回 `INVALID_REQUEST`, 契约版本不受支持返回 `UNSUPPORTED_CONTRACT_VERSION`, 其余返回 `INTERNAL_ERROR`, 均回传 `contractVersion` 且不带 `items`. 证据: 本条所在提交; JVM 守卫 26 项; 设备契约测试 7 项 (含 `getInfo` 与 `plan` 的显式 Parcel 往返) 在 Sony XQ-AT72 (QV710AF65F, API 31) 与 Sony G8441 (BH900ASK9E, API 28) 通过. 偏差: 原计划在 P4.1 实现; 宿主插件中心按 engine 经能力服务探测插件, 占位 Binder 会让探测失败, 因此与注册同步提前. `batchRenameFeatures` 待 P2 引擎接入后按实际实现填写 (P4.1).
- 偏差: 宿主 changelog 条目不在本步写入. 按 `AUTOJS6_CHANGELOG_RELEASE_ISSUE_AGENTS.md` 第 3.2 节, 中间形态 (只有注册而无可用入口) 不单列, 改为随用户可感知的行为一并记录: 管理器路径见 P4.4, `files.batchRename` 见 P3.2. 宿主 6.8.0 changelog 只写简体中文 (`lang_zh-Hans.json`), 其他语言由维护者同步.

### P1.4 宿主入口 (D10)

- [x] (宿主) 多选工具栏: 无需新增代码 (既有机制自动列出 `SELECTION_TOOLBAR` 动作). 证据: 新增 `ExplorerBatchRenameEntryPointsTest` 用 D23 的三种 catalog 形态按 `ExplorerActionRegistry.actionsFor` 的组合方式 (匹配 + 放置位置) 验证: 文件与文件夹混选, 只选文件, 只选一个文件夹时工具栏都只列出 `batch-rename-selection`. 压缩包视图内不可见由既有代码保证 (`ExplorerView.updateSelectionToolbar` 对压缩包条目选区不解析插件动作, 注册表的 `targetFor` 拒绝不可读路径), 未另写测试. 设备上的可见性随 P4.1 catalog 在 P4.4 联调中验证.
- [x] (宿主) 文件夹溢出菜单: 既有 `OVERFLOW_MENU` 机制. 证据: 同一测试验证单个文件夹只列出 `batch-rename-directory`, 单个文件的溢出菜单不列出任何 Batch Rename 动作 (D10); `ExplorerPageViewHolder` 先载入 `menu_dir_options`, 插件动作以 `PLUGIN_ACTION_MENU_ORDER_START + index` 追加在内置项之后 (代码核对). 标签 "批量重命名其中的项目" 由插件 catalog 提供 (P4.1).
- [x] (宿主) 重命名对话框 "更多" (D10c, D29): `ScriptOperations.rename(item, titleOptions)` 把选项放进 material-dialogs 既有的标题选项按钮; `ExplorerRenameDialogMenu` 解析 `RENAME_DIALOG` 动作并生成菜单 (至多 10 项, 选择动作时先关闭对话框再经 `ExplorerView.dispatchPluginAction` 启动插件), 为按钮设置无障碍标签 "更多" 并以 `TouchDelegate` 把触控区域扩到 48 dp; 没有动作时只显示 "批量重命名...", 点击后由 `ExplorerBatchRenamePluginUi` 说明原因 (未安装 / 未启用 / 未授权 / 需要升级) 并可前往插件中心, 重命名对话框保持打开; 文件 (`ExplorerBuiltInActionHandler`) 与文件夹 (`ExplorerPageViewHolder`) 两个重命名入口均已接入. 证据: 宿主提交 `26656b7c4` (本地, 待维护者合并; `VERSION_BUILD=3780`, versionCode 5432). 偏差: (1) 未自建标题视图, 沿用 material-dialogs 的标题选项按钮 (27 dp 宽, 与插件中心等对话框一致), 无障碍标签与 48 dp 触控区域的做法与 Ace 编辑器字体对话框相同; (2) D29 的 "进入插件中心安装向导" 改为打开插件中心并以插件包名搜索 (与 HTML / Markdown / EPUB / 视频播放器插件的引导一致), 安装向导没有预选单个条目的入口; (3) "更多" 菜单只在主文件管理器中出现, 压缩包内条目不显示; (4) "插件返回后刷新列表" 由 P3.1 的会话执行器负责 (与 v24 `targetTrash` 执行后刷新文件管理器的方式相同), 本步不在对话框侧轮询.
- [x] (宿主) 字符串: `text_batch_rename` ("批量重命名...", 10 种语言与默认 `values`, ASCII 省略号并带 `tools:ignore="TypographyEllipsis"`, 按 name 排序, `values` 与 `values-en` 一致), `plugin_batch_rename_name` ("Batch Rename", `strings_donottranslate.xml`); 引导文案复用既有的插件状态字符串. 证据: 同上.
- [x] (测试) 宿主 JVM: `ExplorerRenameDialogMenuTest` (无插件 / 有插件 / 插件未启用三种注册表快照: 未启用的插件不进注册表, 因此与无插件相同, 显示引导; 有动作时按注册表顺序替换引导; 10 项上限; 无插件入口的文件管理器不显示菜单), `ExplorerBatchRenameAvailabilityPolicyTest`, `ExplorerBatchRenameEntryPointsTest` (4 项); 宿主 explorer 相关 JVM 测试 184 项通过. instrumentation: `ExplorerRenameDialogMenuInstrumentationTest` (2 项) 经无障碍节点点击带标签的 "更多" 按钮, 验证引导与插件动作两条路径, 在 AVD_API_24 (只读实例) 与 Sony XQ-AT72 (QV710AF65F, API 31) 通过; 用 `adb shell am instrument` 运行 (Gradle 的 connected 任务会卸载宿主). 测试中发现: 全新 API 24 模拟器上 `MainActivity` 显示期间主线程从不报告空闲, `startActivitySync` 与 `ActivityScenario` 都会超时, 测试改为 `startActivity` 后等待 RESUMED.

---

## P2: 插件引擎 (纯 JVM)

目标: `engine/` 包在无 Android 依赖下实现 D5 全部能力, 由 JVM 单元测试完整覆盖; 本阶段不触碰 Binder 与界面.

### P2.1 正则引擎 (D6, D15)

- [x] (插件) `engine/regex`: `JsRegexEngine` / `JsRegexSession` 接口 (`compile(source, flags)`, `test`, `replace`) 与 `RhinoRegexEngine` 实现: 所有线程共享一个以 `initSafeStandardObjects` 建立并封存的作用域, 每个会话在当前线程进入自己的 `Context` (解释模式, `VERSION_ECMASCRIPT`); 求值经三个固定的 JS 辅助函数 (`new RegExp`, `re.test`, `String.prototype.replace`), 用户文本只作为参数传递, 从不拼接进源码; 每次调用前 `lastIndex = 0`, 粘连与全局表达式对每个名称都从头匹配. 编译错误抛 `JsRegexSyntaxException` (Rhino 的 `details()`, P2.5 映射为 `INVALID_OPTIONS`). `JsRegexSyntax` 提供纯文本查找转义, 替换串字面化 (`$` -> `$$`) 与 `g` / `i` / `msuy` 标志拼装 (按 `RegExp.prototype.flags` 规范顺序, 非法或重复标志被拒). 证据: 本条所在提交. 偏差: (1) 接口改为会话形式, 一个方案或一次预览共享一个时间预算 (默认 2 s); Rhino 会把正则回溯计入指令观察器, `observeInstructionCount` 到期即以 `Error` 中止, 灾难性回溯 (`^(a+)+$` 对 40 个 `a`) 在预算处以 `JsRegexTimeoutException` 结束而不阻塞 Binder 线程; (2) 纯文本模式下替换串也按字面处理 (PowerRename 关闭正则时不解释 `$1`), 与 D15 "转义后走同一条正则路径" 一致.
- [x] (插件) Rhino 来源决策 (附录 D Q4): 采用宿主 `slimRhinoJar` 产物 `libs/org-mozilla-rhino-2_0_0-SNAPSHOT-slim.jar`, 由 `locks/rhino-jar.lock` 以 SHA-256 `8c6f3369da55bf1f4dbb514e57a6e9ff098c1ba9a1a56fecb75e4e43ce528aaf` 锁定 (来源: 宿主提交 `ba2e7d30a` 的 Rhino jar), 构建在配置阶段校验; R8 只额外保留按名称反射加载的 `Interpreter` 与 `VMBridge_jdk18`. 评估 Maven Central `org.mozilla:rhino` 最新版 1.9.1 (2026-02): 同一张 V8 对照表通过 79 / 80, 失败项为 `u` 与 `i` 同时使用 (报 "invalid flag 'u and i'"), 而默认不区分大小写的查找加上 `u` 正是这一组合, 故不采用 (其 API 与宿主分支也不同). release 体积: 389,143 字节; 目前没有组件引用引擎, R8 会剔除 Rhino, 引擎接入服务后 (P2.5 / P4.1) 记录真实体积. 偏差: (1) 插件开启 core library desugaring (`desugar_jdk_libs` 2.1.5, 与宿主相同): Rhino 每次建作用域都初始化使用 `java.time` 的 `NativeDate`, API 24 / 25 缺少该类; (2) Rhino 用 `java.util.regex` 命名分组解析 `\p{...}`, Android 自 API 26 起才支持, 因此 API 24 / 25 上 Unicode 属性转义报语法错误 (`UNICODE_PROPERTIES_UNAVAILABLE`), 不再崩溃; 宿主脚本在 API 24 / 25 上同样受影响, 已另提 Rhino 分支修复任务, 修复后重新暂存 jar 并删除此回退. 已完成 (2026-10-15): Rhino-For-AutoJs6 `90693268c` 改用编号分组, 宿主 `259467203` 换入新 jar; 插件重新暂存 slim jar (SHA-256 `3e67e8636e1c33b993b9b12b83515a35bdcb7c79daeed1ca3eaf9f2da1afaac4`, 来源宿主 `259467203`), 删除 `UNICODE_PROPERTIES_UNAVAILABLE` 回退, 设备测试改为在所有 API 级别断言 `\p{L}` 与 `\p{Script=Han}` 可用, AVD_API_24 与 Sony G8441 (API 28) 通过.
- [x] (测试) `EcmaRegexParityTest`: 80 条表驱动用例, 期望值全部由 Node.js 24 (V8) 以相同输入计算 (附录 A 第 3 组的 PowerRename 文档示例, `$1` `$10` `$<name>` `$&` `` $` `` `$'` `$$` 等替换模式, 未匹配的捕获组, `g` `i` `m` `s` `u` `y`, 命名组, 前后行断言, 反向引用, Unicode 属性与大小写折叠, 代理对, `test`), 15 条纯文本查找 (`.`, `$`, `(`, `\`, `[`, `|` 等转义与字面替换), `matchAll` / `caseSensitive` 到 `g` / `i` 的映射; `RhinoRegexEngineTest` 7 项 (语法错误, 时间预算, 粘连与全局重置, 会话归属, 嵌套会话, 8 线程并发, 1024 字符名称); `RhinoJarLockTest` 4 项 (锁与字节, slim 构建, 记录, 只有 `engine/regex` 导入 Rhino). 证据: 插件 JVM 测试 40 项通过; 变异检查 (替换串不转义, 去掉 `lastIndex` 重置) 均被捕获; 设备测试 `RhinoRegexEngineDeviceTest` 3 项在 AVD_API_24 (只读实例), Sony G8441 (API 28) 与 Sony XQ-AT72 (API 31) 通过, 契约测试同时通过 (各 10 项).

### P2.2 流水线, 变量与选项 (D16, D17)

- [x] (插件) `RenameOptions` 与 `RenameOptionsParser` (D17 默认值; 字符串, 布尔与闭合取值的类型校验, 非法值报 `INVALID_OPTIONS`; 文本选项超过 4096 字符报 `LIMIT_EXCEEDED`; 未知键忽略并在 `ParsedOptions.unknownKeys` 中返回, 由服务记录日志), `RenameItem` / `RenameParent` / `RenameRequest` / `PlanItem` / `RenamePlan` / `PlanItemStatus` / `PlanReason` 模型 (取值直接引用契约常量), `PlanException` (契约错误码). `RequestChecks` 执行契约上限与一致性规则 (条目, 父目录与既有名称数量, `index` 等于位置, 父目录 id 唯一且被引用, 名称为单个路径段). 证据: 本条所在提交. 偏差: `dateTime` 的关闭值按契约为 `"none"` (D17 原写 `false`, 脚本侧 P3.2 负责映射).
- [x] (插件) `variables/`: `ReplacementTemplate` 把 `replace` 拆为字面文本与变量, `VariableExpander` 生成单个条目的 ECMAScript 替换串. 计数器 `${}` 与 `${start=X;increment=X;padding=X}` (键顺序任意, 多个计数器共用同一序号但各自取值, 负数保留符号, 起始值与增量至多 15 位, 填充至多 255 位, 越界报 `INVALID_OPTIONS`); 20 个日期时间变量 (区分大小写, 最长名称优先, 24 小时制, 月份与星期名称取请求区域的独立形式, 缺失时回退格式形式); 随机值 `${rstringalnum=N}` `${rstringalpha=N}` `${rstringdigit=N}` `${ruuidv4}` (`SecureRandom`, N 为 1 到 256, 超过 256 报 `LIMIT_EXCEEDED`); 变量值中的 `$` 转义为 `$$`. 选项关闭的变量与无法解析的内容保持字面 (PowerRename 行为). 证据: 同上. 澄清: (1) `$$` 在两种模式下都表示一个字面 `$`, 并使其后文本不被识别为变量 (`$$YYYY` 得到 `$YYYY`, 与 PowerRename 的转义规则一致); (2) 纯文本模式下替换串整体按字面处理, 只有变量会展开; (3) 原文 "`$Mm` 不展开" 改为按最长匹配解释: `$Mm` 是 `$M` 加字面 `m` (PowerRename 同样逐个变量替换); (4) `${}` 从 0 开始 (PowerRename 文档 "start from zero for the first renamed file").
- [x] (插件) `RenameEngine.plan(request)`: 按 D16 逐项处理 (调用方排除 -> `includeFiles` / `includeFolders` / `includeSubfolders` 过滤 -> 取 `target` 部分 -> 查找不命中则 `unchanged` / `noMatch` -> 展开变量并执行 ECMAScript 替换 -> 文本格式化 -> 本土扩展 -> 拼回名称 -> 与原名相同则 `unchanged` / `sameName`). `NameParts` 只对文件按最后一个点号切分扩展名: 文件夹, 无点号, 仅以点号开头 (`.bashrc`) 与以点号结尾的名称都没有扩展名; 删除扩展名时不留点号. 计数器只对名称发生变化的条目递增 (在冲突判定之前计数, 之后被判冲突的条目同样占用序号). 正则语法错误报 `INVALID_OPTIONS`, 会话超时报 `LIMIT_EXCEEDED`. 证据: 同上. 澄清: 空查找串表示每个被包含的条目都参与, 但不执行替换, 只应用格式化与本土扩展 (脚本可借此只做大小写转换); 查找串为必填键.
- [x] (测试) `RenameEngineTest` 19 项 (PowerRename 文档的 "全部匹配", "仅文件名 / 仅扩展名", 计数器 `Image_${padding=4;increment=2;start=10}_`, 日期 `$MMM-$DD-$YY-powertoys` 与正则示例; 计数器只对改名项递增且被排除项不占序号; 多计数器与负数; 关闭的变量保持字面; `$$` 转义; `modified` 与 `now` 两种日期来源及 20 个变量逐一取值; 中文与俄语名称; 随机串字符集与长度; 包含选项与深度; `target` 切分; 原因码; 空查找串; 格式化与本土扩展顺序; 序号接续; 整体错误码), `RenameOptionsParserTest` 4 项, `ReplacementTemplateTest` 7 项. 证据: 插件 JVM 测试 80 项通过; 变异检查 (计数器对每项递增, 日期变量改为最短匹配) 均被捕获.

### P2.3 文本格式化与本土扩展 (D18, D5)

- [x] (插件) `format/TextFormatter` 四种模式, 大小写转换使用请求区域 (土耳其语 `i` -> `İ`), `title` 只把整段首字符大写, `capitalize` 把每个以空白分隔的词首字符大写, 按码点处理; `local/NameNormalizer` 按 D16 顺序执行 NFC / NFD 规范化, 全角转半角 (U+FF01-U+FF5E 与 U+3000), `sanitize` (把 `\ : * ? " < > |` 与控制字符替换为 `sanitizeReplacement`, 首个点号之前为 Windows 设备名 `CON` `PRN` `AUX` `NUL` `COM1-9` `LPT1-9` 时追加替换串, 替换串为空时追加 `_`), `trim` (去除开头空白与结尾的空白和点号). `sanitizeReplacement` 不得含被替换的字符, `conflictNumberPattern` 须含 `{n}` 且不含 `/` 与 NUL. 证据: 本条所在提交. 澄清: 格式化与本土扩展作用于 `target` 部分 (D16), 设备名检查只在该部分以主名开头时进行 (目标为扩展名时不检查). 序号接续定义为尽力而为: 在所有父目录的既有名称中查找模板里第一个计数器两侧字面文本之间的数字, 计数器从最大值之后继续 (增量不为正, 两侧都无字面文本, 或正则模式下两侧含 `$` 时照常从起始值开始); 多个计数器保持同步.
- [x] (测试) `TextFormatterTest` 4 项 (en / zh / tr 的大小写转换, `title` 与 `capitalize`, 代理对与 U+10428 等增补平面字母), `NameNormalizerTest` 6 项 (规范化含组合字符与韩文字母, 全角转半角对照全部 94 个字符与全角空格, 清理与设备名, 修剪, 流水线顺序), 序号接续在 `RenameEngineTest` 中覆盖 (既有 `Photo_0012.jpg` 时从 13 开始, 起始 10 增量 5 时为 15). 证据: 同 P2.2.

### P2.4 校验与冲突 (D19, D20 判定部分)

- [x] (插件) `validate/NameValidator` (D19, 原因码 `empty` / `dotName` / `separator` (含 `/` 或 NUL) / `tooLong` (按码点计算的 UTF-8 字节数超过 255) / `restrictedCharacter` (父目录标记 `restrictedCharacters` 时的 `\ : * ? " < > |` 与控制字符)); `ConflictResolver`: 先校验, 再按目录判定冲突 (仍被占用的名称 = 既有名称 - 本批在该目录中改名条目的旧名; 同批重名为 `duplicateInBatch`, 与仍被占用的名称相同为 `existingEntry`; 父目录标记 `caseInsensitive` 时不区分大小写比较), 保留原名的条目 (无效, 跳过, 冲突) 继续占用原名, 因此反复判定直到稳定 (例如 a -> keep 被拒后, b -> a 随之冲突). 四种策略: `fail` 报告每个冲突条目并拒绝方案; `skip` 让冲突条目保留原名; `number` 在扩展名之前追加模式 (默认 ` ({n})`, 可配置, 从 1 起取第一个空闲序号, 超过 999 或名称超长时为 `numbersExhausted` 冲突); `overwrite` 对 `existingEntry` 标记 `overwrites` (原因 `overwrite`), 同批重名仍为冲突. 后三种策略下一组重名条目中请求顺序最前的条目保留该名称. 无效条目在任何策略下都使方案被拒绝. 证据: 本条所在提交. 澄清: (1) `conflict` 与 `invalid` 条目的 `newName` 为被拒绝的候选名 (供预览显示), 是否执行只看 `status`; (2) `twoPhase` 的范围比 D20 原文更宽: 不区分大小写目录中的仅大小写变化, 以及目标名仍被本批另一改名条目占用的情况 (链, 交换与循环) 都标记, 宿主执行器可先把这些条目改为临时名, 不依赖执行顺序; (3) `number` 得到的候选名恰为条目自身原名时该条目视为 `unchanged` / `sameName`. 宿主协议文档的相应说明随 P2.5 一并补充.
- [x] (测试) `ConflictResolverTest` 11 项 (交换, 三元循环与链的 `twoPhase`; 不区分大小写目录中的仅大小写改名; `fail` 下重名与既有条目及计数; 大小写敏感与不敏感比较; `skip` 保留首项并级联; `number` 的首个空闲序号, 自定义模式, 文件夹与隐藏文件, 999 上限与名称长度上限; `overwrite` 只覆盖既有条目; 无效条目在四种策略下都拒绝方案并继续占用原名; 经过整个引擎的重名与编号), `NameValidatorTest` 2 项 (各原因码, UTF-8 字节数含增补平面与孤立代理). 性能: 2000 项方案 (正则, 计数器, 格式化, 4000 个既有名称) 在 JVM 上预热后 34 ms, 低于 300 ms 预算. 证据: 插件 JVM 测试 93 项通过; 变异检查 (`twoPhase` 只看大小写, `fail` 策略只报告重名组首项之外的条目) 均被捕获.

### P2.5 编解码与上限

- [x] (插件) 编解码拆为两层: `service/PlanCodec` 在 Bundle 形态的纯 Map 与引擎模型之间互转 (附录 C.1 结构; 契约版本不受支持时以请求中的版本应答 `UNSUPPORTED_CONTRACT_VERSION`; 缺键, 类型错误, 未知 `kind`, 未知时区, 空父目录 id 报 `INVALID_REQUEST`; 条目数, 父目录数与整个请求超过 262144 字符报 `LIMIT_EXCEEDED`; 选项交给 `RenameOptionsParser`; 应答带全部键与每种状态的计数, 失败应答只有契约版本, 错误码与至多 1024 字符的消息), `BatchRenamePlanner` 从不抛出异常 (意外错误为 `INTERNAL_ERROR`, 未知选项键写日志), `service/BundleMaps` 负责 Bundle 与 Map 的互转. 宿主传入的字符串只作为数据. 证据: 本条所在提交. 偏差: 原计划的单个 `BundleCodec` 拆成可在 JVM 上完整测试的 `PlanCodec` 与只在设备上运行的 `BundleMaps` (JVM 单元测试中没有可用的 `Bundle`).
- [x] (测试) `PlanCodecTest` 7 项 (经真实引擎的往返与全部应答键, 区域 / 时区 / 方案时间到达日期变量, 可选键的默认值, 未知选项键记录日志, 16 种畸形请求与各项上限, 错误消息长度, 2000 个随机损坏的请求从不抛出也不落入 `INTERNAL_ERROR`); 设备契约测试改为经真实 Parcel 往返的 `plan` (Sony XQ-AT72, API 31). 证据: 插件 JVM 测试 101 项通过, 设备测试 10 项通过.
- [x] (插件, 提前完成 P4.1 第一条) `BatchRenamePluginService.plan` 经 `BundleMaps` 与 `BatchRenamePlanner` 调用引擎 (进程内共用一个引擎, 并发安全), `BatchRenamePlugin.FEATURES` 声明 `counter` / `dateTime` / `random` / `cleanup` 四项. 正则时间预算改为每次求值 1 s, 每个会话 30 s (原为整个会话 2 s, 慢速设备上的 2000 项方案可能被误判超时). 证据: 宿主提交 `383d7dab8` (本地, 待维护者合并; `VERSION_BUILD=3781`) 新增 `BatchRenamePluginRoundTripTest`, 经 `BatchRenamePluginHost.plan` 跨进程调用已安装的签名 release 插件, 3 项在 Sony XQ-AT72 上通过. 联调中发现: release 插件被宿主调用时 `RegExp` 未定义, 原因是 Rhino 首次运行在插件进程的 Binder 线程上, 该线程的上下文类加载器不是插件的, `ServiceLoader` 读不到 `META-INF/services`; 插件自身的设备测试为同进程调用, 无法发现. 修复: `RhinoRegexEngine` 在首次进入 Rhino 时临时设置上下文类加载器. 曾怀疑 R8 改名所致, 加 `-keepnames` 后仍失败, 修复类加载器后去掉该规则同样通过, 因此 R8 规则保持不变. release APK 799,045 字节 (D28 预算 3.5 MB). 宿主协议文档补充 "Plan semantics" 一节 (同一宿主提交).

---

## P3: 宿主执行器, 撤销日志与脚本 API

目标: 宿主能以脚本路径完成 plan / apply / undo / history, 并把同一执行器暴露给 v25 session.

### P3.1 执行器与日志 (D20, D21, D30)

- [x] (宿主) `BatchRenameExecutor.apply(plan, policy, progress)`: 重新校验每项 (存在, `lastModified` / `length` 未变, 否则 `stale`), 深度降序, 两阶段改名, `overwrite` 受害文件先进回收站, 逐项结果, 进度回调, 已改名文件的 `relocateHistory` (版本历史) 与 `notifyFileChanged`; `undo(batchId)`: 逆序恢复, 占用检查, 回收站受害文件恢复. 证据: 宿主提交 `3bcd17aba` (本地, 待维护者合并; `VERSION_BUILD=3783`, versionCode 5435), `core/plugin/batchrename/BatchRenameExecutor.kt`; 宿主 JVM `BatchRenameExecutorTest` 16 项通过. 偏差: (1) 签名为 `execute(operations, source, pluginUid, allOrNothing, scope, progress)`: 冲突策略只存在于插件方案中 (附录 C.2), 执行器只执行确切名称, `overwrite` 按条目给出, 不再接收 `plan` / `policy`; (2) 两阶段不读方案中的 `twoPhase`, 由执行器按文件系统重新判定 (仅大小写不同, 新名称被本批另一条目占用), 临时名为 `.batch-rename-<批次 id 末 12 位>-<序号>.tmp` (D20 原写含原名, 可能超过 255 字节); (3) 同一深度的最终改名按轮次进行直到没有条目可以移动, 链, 交换与循环都不依赖请求顺序; (4) 撤销按深度升序 (目录先于其中条目), 每条记录保存改名那一刻的路径, 等价于 D20 的 "逆序"; 目录只复核存在与类型 (子项改名会改变目录的修改时间); 原名被占用或 I/O 失败的条目为 `recoveryRequired`, 障碍消除后可再次撤销; (5) 文件管理器在每批之后整体刷新一次 (`Explorers.workspace().refreshAll()`, 与 v24 `targetTrash` 相同), 不逐文件 `notifyFileChanged`; (6) 记录中的路径映射按每段至多一条规则处理, 目录交换时其下已改名条目的位置也正确 (JVM 用例覆盖).
- [x] (宿主) `BatchRenameJournalStore`: JSON 持久化, 20 批上限与淘汰, 损坏文件跳过并记日志, 批次 id 为随机不可猜测字符串; `history(limit)` 读取. 证据: 同上, `BatchRenameJournalStore.kt` (Gson 树 API, 格式 1, 先写临时文件再改名). 偏差: (1) 位置为 `noBackupFilesDir/batch-rename/journals` (D21 原写 `filesDir`): 日志含绝对路径, 不应进入备份; (2) 20 批上限按所有者分别计算 (脚本一份, 每个插件 UID 一份), 一个插件不能挤掉脚本或其他插件的批次; (3) 读取接口为 `list()` (最新在前) 与 `load(batchId)`, 条数限制由调用方施加 (`history(limit)` 随 P3.2); (4) 记录额外保存来源, 插件 UID, 会话父目录 (`scope`), 逐项 `renamed` 标志与管理器的目标 id / 相对路径.
- [x] (宿主) `ExplorerActionHostSession` v25 五个方法 (含 `listRenameParent`) 接入执行器 (替换 P1.2 的占位), 条目到路径的解析经 session 既有的目标与 `listChildren` 路径校验; 执行与撤销后刷新文件管理器 (同 v24 `targetTrash` 的 `notifyWorkspaceChanged`), 这是重命名对话框等入口 "插件返回后刷新列表" 的唯一来源 (P1.4). 证据: 同上; `BatchRenameHostServices` (进程级执行器与日志, 脚本与会话共用) 与 `AndroidBatchRenameEnvironment` (宿主回收站, 版本历史, 刷新); `ExplorerActionHostInputAccess` 在可改名的会话中记录 `listChildren` 返回的后代 (类型, 修改时间, 大小), 改名与撤销后目标与记录随之移动; 协议文档 `explorer-action-protocol-v25.md` 的 Status, 重命名, 历史与撤销, 验证各节已改写. 偏差: (1) 结果条目只含 `RENAME_ITEM_STATE` 与 `RENAME_NEW_RELATIVE_PATH`, 按请求顺序排列, 不重复目标 id 与相对路径 (2000 项时可省下约一半 Parcel 体积); 协议文档提醒客户端按 `Parcel.dataSize()` 拆分过大的请求 (每个请求成为单独批次); (2) 历史按插件 UID 与会话父目录精确匹配 (原写 "会话目标之下"), 范围外的批次 id 一律答 `RENAME_BATCH_STATE_UNKNOWN`, 不抛异常; `listRenameBatches` 只返回摘要, 不含条目; (3) 畸形请求 (条目数, 缺键, 未知目标, 重复条目, 文件目标带相对路径, 路径逃逸或符号链接, 未列出的后代) 整体抛 `IllegalArgumentException` 且不改名; 非法新名称按条目报告为 `NOT_RENAMED`; 同一会话同一时刻只允许一个改名或撤销; (4) `create()` 可注入执行器 (测试用私有日志).
- [x] (测试) 宿主 JVM: 临时目录下的执行器用例 (交换, 仅大小写, 父子同批, 单项失败不回滚, stale 跳过, 撤销后再撤销报 `UNDO_UNAVAILABLE`, 日志淘汰); v25 session 用例 (非法 `relativePath` 逃逸拒绝, 跨 session 批次不可撤销). 证据: `BatchRenameExecutorTest` 16 项 (交换, 循环, 链, 仅大小写, 父子同批与撤销, 目录交换携带已改名子项, 单项失败不回滚, stale, allOrNothing, 覆盖经回收站并撤销, 原名被占用后重试撤销, 进度, 按所有者淘汰与损坏文件跳过, 范围与 `renamed` 标志持久化) 与 explorer 包既有用例共 138 项通过. 偏差: (1) "撤销后再撤销报 `UNDO_UNAVAILABLE`" 是脚本 API 的错误映射, 随 P3.2 测试; 执行器层面验证为已恢复的批次保持 `RESTORED` 且 `undoable` 为假; (2) 会话依赖 Binder 调用方 UID 与 `Os.stat`, 宿主 JVM 测试没有 Robolectric, 两个 session 用例改在下一条 instrumentation 中覆盖.
- [x] (测试) instrumentation `ExplorerActionRenameV25InstrumentationTest` (以 `ExplorerActionTargetTrashV24InstrumentationTest` 为模板): 真实插件 APK 或测试桩 catalog, `renameEntries` 往返, 撤销, 进程重启后 `listRenameBatches` 仍可见. 证据 (E3, 2026-10-15): 7 项在 `AVD_API_24` (emulator-5590) 与 `AVD_API_35_16K` (emulator-5594) 上全部通过: 父目录分页与文件系统特征, 未列出的后代被拒, 目标与列出的后代改名后会话跟随 (目标 id 不变, 记录移到新相对路径), 逐项结果 (被占用, 非法, stale, allOrNothing, 交换, 仅大小写), 畸形请求与路径逃逸, 覆盖经真实宿主回收站并撤销, 历史按插件与父目录限定且新日志实例仍可见, 未声明 `targetRename` 与已关闭的会话, 默认接线写入宿主日志并刷新文件管理器; 变异检查 (去掉改名后的会话跟随, 去掉历史的 UID 限定) 使对应用例失败, 恢复后重新通过. 偏差: 会话在测试进程内创建 (与 v24 模板相同), 未经真实插件 APK, 跨进程往返随 P4 的插件界面验证; "进程重启" 以同一目录上的新日志与执行器实例模拟.

### P3.2 脚本 API `files.batchRename` (D2, D9, D27)

- [x] (宿主) `BatchRenameTargetCollector`: 路径解析 (相对脚本工作目录), 去重, 目录递归 (不跟随符号链接, 深度 32, 总数 2000), 收集父目录既有名与大小写敏感性探测, 产出请求 Bundle. (原列于 P3.1, 只有脚本路径使用, 随脚本 API 实现) 证据: 宿主提交 `852afb776` (本地, 待维护者合并; `VERSION_BUILD=3786`, versionCode 5438), `core/plugin/batchrename/BatchRenameTargetCollector.kt`; JVM `BatchRenameTargetCollectorTest` 5 项. 偏差: (1) 产出脚本模型 (`BatchRenameTargets`), 由 `BatchRenameBundles` 转为请求 Bundle, 纯 JVM 可测; (2) 给出的路径本身是符号链接时报 `INVALID_PATHS`, 目录下遇到的链接跳过但仍计入 `existingNames`; 列目录失败报 `IO_FAILED`; (3) 深度按给出路径计 (给出的路径为 0), 只有更深一层确实有条目时才报 `LIMIT_EXCEEDED`; 父目录数 512 与既有名称 20000 的契约上限在宿主侧先行检查.
- [x] (宿主) `augment/files/BatchRename.kt`: `keys = listOf(AugmentableKey("batchRename", withDollarPrefix = false))`, `Augmentable` + `Invokable`; `ScriptRuntime.kt:1044` 改为 `Files(this).augment(target, files).also { BatchRename(this).augment(it) }`; `selfAssignmentFunctions`: `plan`, `apply`, `undo`, `history`, `isAvailable`, `planAsync`, `applyAsync`, `undoAsync`; 参数守卫 (`ArgumentGuards`), `RegExp` 对象提取 (`NativeRegExp` 的 `source` / `flags`), `onProgress` 在脚本线程回调, 返回 `NativeObject` (plan / result / history 结构见附录 B.3), `BatchRenameErrorMapper` 映射错误码与插件中心引导文案. 证据: 同上; `runtime/api/augment/files/BatchRename.kt` 与 `BatchRenameJsErrors.kt` 只做 Rhino 转换, 逻辑在纯 Kotlin 的 `BatchRenameScriptService` (方案登记, 重新询问, 执行, 结果映射, 撤销, 历史) 与 `BatchRenameScriptArguments` (参数与 D17 默认值); `BatchRenamePluginHost.planForScript` / `isAvailable` 把选择失败分为 `PLUGIN_UNAVAILABLE` / `PLUGIN_DISABLED` / `PLUGIN_INCOMPATIBLE`, `TransactionTooLargeException` 为 `LIMIT_EXCEEDED`. 偏差: (1) 错误以脚本可见的 `files.batchRename.BatchRenameError` 类抛出 (`code`, `message`, 隐藏的 `javaException`), 与 `EpubError` 同构, 没有单独的 `BatchRenameErrorMapper` 类: 插件错误码映射在 `BatchRenameScriptCodes.fromPluginErrorCode`, 缺插件文案沿用 `AidlPluginHost` 选择失败的 `error_missing_required_plugin_for_module_label`; (2) `apply` 以 `applyPlan` 实现并以别名 `apply` 挂载 (避免与 Kotlin 作用域函数同名); (3) 同步形态以脚本的 Job 运行但去掉调度器, 执行器与 `onProgress` 都在脚本线程上; `onProgress` 抛出的异常不中断批次, 批次记入日志后再抛出; (4) 返回对象按附录 B.3 (P3.2 修订): 方案条目以 `overwrites` 代替 `twoPhase`, 结果计数增加 `excluded`, 增加撤销结果与历史条目的形态; 每个脚本保留 16 个方案, 修改 `excluded` 或覆盖 `onConflict` 时重新询问插件, 方案至多执行一次 (`PLAN_STALE`).
- [x] (宿主) Async 变体: 复用宿主既有 Promise 桥接 (与其他 `xxxAsync` 方法一致), 后台线程执行, 异常转 reject. 证据: 同上; `planAsync` / `applyAsync` / `undoAsync` 在脚本协程作用域的 IO 线程上运行, 经 `ScriptAsyncDispatcher` 在脚本线程上送达进度与结果 (与 `EpubPromises` 相同的管道), 参数错误直接拒绝 Promise. 偏差: `history` 与 `isAvailable` 只有同步形态 (与附录 B.1 一致).
- [x] (测试) 宿主 JVM: 参数规范化 (字符串 / 数组 / RegExp / 非法类型), 默认值, 错误码映射, plan 对象 `excluded` 回传; instrumentation: 安装插件后在真机上 `plan` / `apply` / `undo` / `history` 往返, 未安装时 `isAvailable()` 为 `false` 且 `plan` 抛 `PLUGIN_UNAVAILABLE`. 证据: JVM `BatchRenameScriptArgumentsTest` 7 项, `BatchRenameTargetCollectorTest` 5 项, `BatchRenameScriptServiceTest` 6 项 (替身方案器 + 临时目录上的真实执行器: 执行一次, 排除与换策略后重新询问, 拒绝, 覆盖失败, stale, 空批次, 撤销与历史, 方案淘汰, 应答与请求不符), 连同执行器用例共 34 项通过; instrumentation `BatchRenameScriptApiTest` (E3, 2026-10-15) 经正式 Rhino 引擎运行脚本, 在 `AVD_API_24` (emulator-5590) 与 `AVD_API_35_16K` (emulator-5594) 上: 未安装插件时通过 (`isAvailable()` 为假, 同步与异步都报 `PLUGIN_UNAVAILABLE`, 错误是 `BatchRenameError` 实例); 装上签名 release 插件 (804,745 字节, 插件 `78d1464` 构建) 后完整往返通过: 相对工作目录的路径, `RegExp` 搜索, 排除一项后重新询问, 带进度执行, 再次执行报 `PLAN_STALE`, 历史, 撤销与再次撤销 (`UNDO_UNAVAILABLE`), `PLAN_REJECTED`, 宿主与插件两侧的 `INVALID_OPTIONS`, `INVALID_PATHS`, 回调抛错不中断批次, `planAsync` / `applyAsync` / `undoAsync` 往返 (原始状态码确认插件用例实际执行, 另一用例按设计跳过); 变异检查 (Rhino 层忽略脚本设置的 `excluded`) 使往返用例失败, 恢复后重新通过; 宿主全部 JVM 测试 4437 项通过. 未执行: 真机.
- [x] (宿主) `.changelog/lang_zh-Hans.json` 6.8.0 补全 `files.batchRename` 条目 (参阅 项目文档 链接; 其他语言由维护者同步), `docs/dev/batch-rename-plugin-protocol-v1.md` 补充脚本路径章节. 证据: 同上; changelog 只写 zh-Hans 一条 feature (链接暂指项目文档 "文件" 页, `batchRename` 小节随 P5.1 补上), 其他语言由维护者同步; 生成的 `CHANGELOG*.md` 与 README 发行历史随提交更新; 协议文档新增 "Script path" 一节并更新 Status.

---

## P4: 插件服务, 管理器入口与界面

目标: 插件可被宿主发现并完成三处入口的完整闭环.

### P4.1 服务与 catalog (D23)

- [x] (插件) `BatchRenamePluginService`: `getInfo()` 与契约能力已在 P1.3 实现 (`explorerActionProtocolVersion` 只由管理器服务上报); 本条把 `plan()` 从 `planWithoutEngine` 改为经 `BundleCodec` 调引擎 (并发安全, 无状态), 并让 `BatchRenamePlugin.FEATURES` 声明引擎实际实现的功能. 证据: 已在 P2.5 提前完成 (见该节).
- [x] (插件) `BatchRenameExplorerActionService`: `getActionCatalog()` 三个动作 (D23), 协议 25, `getInfo()` 与能力服务一致; `BatchRenameActivity` 的 `EXPLORER_ACTION_EXECUTE` 入口解析请求 (目标, session token, 协议版本), 协议低于 25 时显示本地化错误并退出. 证据: 插件提交 `3aca80f`; `explorer/BatchRenameCatalog.kt` (三个动作均为只读, Activity 呈现, 声明 `targetRename`, 标签字符串经 `res/raw/keep.xml` 在 release 包中保留), `explorer/ExplorerRequest.kt` 解析请求 (动作 id, 协议版本, 请求 id, 目标, 会话 Binder 的描述符校验, 目标形态与动作是否相符); 协议低于 25 或宿主构建号低于最低值时提示 "请更新 AutoJs6 后再使用批量重命名" 并关闭会话, 其他不可处理的请求关闭会话后退出. 偏差: 目录动作的目标类型为 `TARGET_DIRECTORY` 且 MIME 列表为空 (与宿主策略测试的形态一致).
- [x] (测试) JVM: catalog 快照测试, `ExplorerActionManifestTest` (每个 catalog Activity 带 EXECUTE + DEFAULT); instrumentation `BatchRenamePluginContractTest`: action / category 发现只命中一个服务, 显式绑定与 descriptor, `getInfo` 字段, `plan` happy path / 非法参数 / 超限, 绑定解绑不泄漏. 证据: 插件提交 `3aca80f`; JVM `BatchRenameCatalogTest` 4 项 (形态快照, 每种语言都有标签且 keep.xml 保留, Activity 与 Manifest 一致, 复数资源齐全) 与既有 `ManifestContractTest` (改为只允许查询宿主包, 供宿主外观设置使用); 设备 `BatchRenamePluginContractTest` 改为断言三个动作 (只读, Activity 呈现, `targetRename`, 宿主能按名称取到标签). 插件 JVM 测试共 116 项通过; 设备测试 13 项在 `AVD_API_24` 与 `AVD_API_35_16K` 上通过. 偏差: `plan` 的契约用例已在 P1.3 / P2.5 覆盖, 本条只补 Explorer Action 部分.

### P4.2 界面 (D4, D11, D24)

- [x] (插件) 主题与脚手架: Compose + Material 3, 跟随宿主语言 / 夜间 / 主题色 (`AutoJs6HostSettingsContract`, 回退系统), 预测性返回, 进程恢复 (`SavedStateHandle` 保存选项与输入, 目标集由请求重建). 证据: 插件提交 `3aca80f`; `ui/HostAppearance.kt` 经 `AutoJs6HostSettingsContract` 读取宿主主题色, 当前深色状态与语言 (主线程至多等 400 ms, 之后用上次结果, 从未应答时跟随系统), `attachBaseContext` 应用语言与夜间模式, `BatchRenameTheme` 以宿主主题色构造 Material 3 配色 (无宿主时 Android 12 起动态取色); `enableOnBackInvokedCallback` 与 `BackHandler`. 偏差: 未用 `SavedStateHandle`: 选项与输入随每次预览写入偏好 ("记住上次选项" 开启时), 进程重建后由请求重新枚举; 勾选排除不跨进程保留.
- [x] (插件) 查找 / 替换输入: 历史下拉 (D25), 变量插入按钮 (底部面板列出计数器 / 日期 / 随机变量及一行说明, 点击插入到光标处), 正则编译错误即时提示. 证据: 插件提交 `3aca80f`; `ui/OptionsPanel.kt` (最近使用下拉, `{}` 按钮打开变量面板并插入到光标处, 正则或选项被拒时在查找框下方即时显示原因).
- [x] (插件) 选项区: 正则, 全部匹配, 大小写敏感, 作用范围 (分段按钮), 包含文件 / 文件夹 / 子文件夹, 文本格式化 (下拉), 计数器, 随机串, 日期来源, 本土扩展 (折叠组), 冲突策略 (折叠组, `overwrite` 选中时弹出确认). 证据: 插件提交 `3aca80f`. 偏差: 作用范围, 包含与格式变量收进默认折叠的 "范围与格式" 分组 (竖屏手机上首屏即可看到预览), 名称清理与冲突策略各自折叠; 选中 `overwrite` 时先弹出确认.
- [x] (插件) 预览列表: `ExplorerTargetEnumerator` 经 `listChildren` 分页枚举 (包含子文件夹时递归, 进度指示, 2000 上限提示), 每行复选框 / 图标 / 原名 / 新名 / 状态 (改名: 主题色, 冲突或非法: 错误色并显示原因, 未命中: 次要色), 列头切换 "仅显示将重命名", 去抖 150 ms 后台重算, 大列表 `LazyColumn` 与 key 稳定. 证据: 插件提交 `3aca80f`; `explorer/RenameCandidates.kt` (`RenameCandidateEnumerator` 经 `HostSessionClient` 分页 `listRenameParent` / `listChildren`, 符号链接不进入也不改名但保留名称, 超过 2000 项或 32 层时截断并提示) 与 `explorer/PreviewPlanner.kt` (插件自身引擎, `now` 在打开界面时固定). 偏差: 只有切换 "子文件夹中的项目" 时重新枚举, 其他选项只重新计算预览; 文件夹菜单动作改名的是文件夹之中的条目 (深度从 0 起).
- [x] (插件) 应用流程: 底栏统计 (将改名 / 冲突 / 排除), "应用" 禁用条件 (有冲突且策略 `fail`, 或无改名项), 执行进度对话框, 结果摘要, Snackbar "撤销" (`undoRenameBatch`), 顶栏 "撤销上一批" (读取 `listRenameBatches` 中本插件最近一批), 失败项列表可复制原因. 证据: 插件提交 `3aca80f`; `renameEntries` 经 `RenameRequestSplitter` 按目录分组, 深层先发, 每个请求至多 512 KiB Parcel (同一目录过大时提示减少条目), 撤销按批次倒序; "重命名" 在预览与当前选项一致之前禁用 (修复了快速点击执行旧方案的竞态); 执行后目标名称随结果更新, 撤销时只有目标条目恢复才改回原名; 撤销早先界面的批次且涉及请求目标时关闭界面. 偏差: 宿主执行期间没有逐项进度 (v25 没有进度回调), 进度对话框为不确定进度; 失败列表只区分 "未重命名" 与 "列出后已被修改" (v25 结果不含原因).
- [x] (插件) 10 语言字符串, 无障碍 (内容描述, 焦点顺序, TalkBack 下状态朗读), RTL, 大字体, 横屏与平板宽度 (>= 600 dp 时列表与选项左右分栏). 证据: 插件提交 `3aca80f`; 118 条字符串与 3 组复数 (`plurals.xml`) 覆盖 10 种语言, 全部半角标点, 含 `...` 的条目带 `tools:ignore="TypographyEllipsis"`; 预览行合并语义并朗读 "已包含 / 已排除", 折叠分组朗读展开状态; `supportsRtl` 与自动镜像图标; 宽度 >= 600 dp 时左右分栏. 未执行: 实际的 TalkBack, RTL, 大字体与横屏走查.
- [x] (测试) Compose 测试: 输入后预览更新, 复选框排除, 过滤切换, 冲突禁用应用, 历史下拉; 截图采集 (R2). 证据: 插件提交 `3aca80f`; `ui/BatchRenameScreenTest` 3 项 (输入后预览更新, 仅显示改名项, 取消勾选排除, 执行到达会话, 撤销, 历史下拉; 冲突禁用; 旧协议请求关闭会话并退出), 使用内存宿主会话 `FakeHostSession`, 断言只用测试标签与会话, 不依赖翻译文本; 截图写入应用 `files/screenshots/preview.png` (R2, 已在 `AVD_API_35_16K` 上取回检查).

### P4.3 持久化与设置 (D25)

- [x] (插件) `PreferencesStore`: 上次选项, MRU, "记住上次选项" 开关; 顶栏溢出 "清除历史" / "恢复默认选项"; 不记录路径与文件名. 证据: 插件提交 `3aca80f`; `settings/PreferencesStore.kt` (选项经插件自身的选项解析器读回, 被拒的值回退默认; 最近使用各 10 条, 去重, 最新在前). 偏差: 使用 SharedPreferences 而不是 DataStore (不增加依赖, 数据量只有几十个键).
- [x] (测试) DataStore 往返, MRU 去重与上限, 关闭 "记住上次选项" 后不恢复. 证据: 插件提交 `3aca80f`; JVM `PreferencesStoreTest` 5 项 (往返与默认值, 关闭记住后清除并不再保存, 被拒的存储值回退, 最近使用的顺序去重与上限, 选项校验).

### P4.4 本阶段联调

- [x] (测试) 三处入口在 API 24 AVD, API 28 / 33 / 35 真机上各走一遍: 多选 (文件 + 文件夹混合, 含子文件夹), 文件夹菜单 (含 / 不含子文件夹), 重命名对话框 "更多" (单文件与单文件夹); 撤销后文件名与版本历史均恢复; 宿主列表即时刷新. 证据 (E3, 2026-10-15): 宿主测试 `BatchRenameExplorerFlowTest` (宿主提交 `f1088b0f7`, `805cf4f7c`, `24588bd1e`, 本地, 待维护者合并) 经宿主真实的 Explorer Action 注册表与启动器打开插件界面 (真实 v25 会话), 用 `UiAutomation` 按插件暴露为资源 ID 的测试标签输入并点击, 在磁盘上核对宿主执行器改名与插件撤销恢复; 三处入口为多选 (文件 + 含子项的文件夹), 文件夹菜单 (含子文件夹), 重命名对话框 (单个文件). 全部 5 个用例 (含下一条的两个失败路径) 通过: Sony G8441 (BH900ASK9E, API 28), Sony XQ-AT72 (QV710AF65F, API 31, 两轮), Sony XQ-DQ72 (QV770340J7, API 33, 两轮), 小米平板 23046RP50C (968e9f18, API 35, 两轮, 宽屏), OnePlus OPD2413 (63751f63, API 35, ColorOS; 维护者在手机上确认了插件安装), `AVD_API_24` (debug 插件两轮, 签名 release 插件一轮), `AVD_API_35_16K`; 真机上使用宿主 debug 构建 versionCode 5441 与 debug 插件 (维护者同意在真机上安装宿主 debug). 偏差: (1) 版本历史的恢复由宿主执行器的 `relocateHistory` 负责 (P3.1), 本条只核对文件名; 宿主列表刷新由执行器的 `notifyChanged` 触发, 未目视核对; (2) 单个文件夹走重命名对话框未单列 (与单文件同一动作, 文件夹目标在多选用例中覆盖); (3) 发现 ColorOS 拒绝绑定从未运行过的插件, 文件管理器的 Explorer Action 注册表刷新时不会像插件选择那样自动唤醒, 动作因此不出现 (同机的其他插件同样如此); 已由宿主提交 `0a7ab1864` (本地, 待维护者合并; `VERSION_BUILD=3793`) 修复: 注册表刷新时唤醒服务绑定失败且在插件中心已启用, 已授权的插件 (每个包每分钟至多一次, 新增 `PluginDiscoveryRecovery` 与 JVM 测试 5 项) 并对这些包重新发现一次; 测试去掉了手动唤醒, OnePlus 上插件处于 stopped 状态时两轮 5/5 通过, 日志确认由宿主唤醒插件 (同机三个已停用的插件不被唤醒).
- [x] (测试) 失败路径: 插件未安装 / 未启用 / 版本过低 / 宿主协议 24 (旧宿主) / 目标在执行前被删除 / 可移动存储 (FAT32) 上仅大小写改名. 进展: 已执行: 插件在插件中心被停用时三处入口都不出现, 重命名对话框保留 "批量重命名..." 引导, 重新启用后动作恢复 (`BatchRenameExplorerFlowTest`, 上条全部设备); 目标在预览后被删除时, 其余条目照常改名, 被删条目列入失败清单, 撤销正常 (同上); 旧协议请求结束界面并关闭会话 (`BatchRenameScreenTest`); 插件未安装时重命名对话框的引导 (P1.4). 2026-10-16 在 P6.6 补测: 要求更高宿主的插件, 旧宿主 (协议 24) 上的表现与 FAT32 虚拟 SD 卡上的仅大小写改名均已执行 (见 P6.6). 未执行: exFAT 与真实 SD 卡 (三星设备) 上的复核, 列在 P6.6.
- [x] (宿主) `.changelog/lang_zh-Hans.json` 6.8.0 条目 (由 P1.3 移入; 其他语言由维护者同步): `feature` 文件管理器支持批量重命名 (多选工具栏 / 文件夹菜单 / 重命名对话框 "更多" 菜单, 需要安装 Batch Rename 插件), 插件中心与安装向导的 Batch Rename 条目并入同一条或列为 `improvement`. 措辞按 `AUTOJS6_CHANGELOG_RELEASE_ISSUE_AGENTS.md`, 不写协议号与 Binder 细节. 证据: 宿主提交 `f1088b0f7` (本地, 待维护者合并; `VERSION_BUILD=3790`, versionCode 5442); 一条 feature (文件管理器经 Batch Rename 插件批量重命名, 列出三处入口), 插件中心条目并入该条, 不单列; 生成的 zh-Hans README 与 CHANGELOG 随提交更新.

---

## P5: 文档, 声明, 四仓库同步与发布

### P5.1 文档 (AutoJs6-Documentation)

- [x] (文档) `api/files.md` 新增 `batchRename` 条目组 (当前风格: `## [m+] batchRename`, `### files.batchRename(paths, options)`, `### files.batchRename.plan(paths, options)` 等, 版本徽章 `6.8.0`), 开头加 "由外置 Batch Rename 插件提供, 调用前必须安装并启用与当前 AutoJs6 版本兼容的插件, 未发现可用插件时抛出异常" 的说明段 (措辞与 `mediainfo.md` 一致); 选项表, 变量表 (附录 A), 冲突策略, 撤销, 错误码, 示例 (>= 6 个, `let`, 可运行); `api/progress.md` 更新; `api/sidebar.md` 无需新页 (挂在 files 下). 证据 (2026-10-15): 文档仓库 `021a6a6` (正文, `api/progress.md`, 文档更新日志 10 种语言) 与 `62f55d8` (小节标题补英文锚点); `## [m+] batchRename` 含 `files.batchRename(paths, options)`, `plan`, `apply`, `undo`, `history`, `isAvailable` 与三个 `Async` 形态, 开头为插件依赖说明 (措辞与 `mediainfo.md` / `epub.md` 一致); 选项表, 替换变量 (计数器, 20 个日期时间变量, 随机值), 冲突策略, 方案 / 结果 / 撤销结果 / 历史对象 (含原因码表) 与 `BatchRenameError` 错误代码放在页末的独立章节; 示例 12 个, 全部用 `let`, 并在 OnePlus OPD2413 (63751f63, API 35, 宿主 5444) 上经 `RunIntentActivity` 实际运行核对输出 (据此修正了概览示例的输出注释: 给出的文件夹自身也是条目). 偏差: 类型未另开页面, 也未用 `BatchRenamePlan` 等纯英文标题 (生成器会把它们与同名方法的锚点合并), 而是 "批量重命名方案 (BatchRenamePlan)" 这类标题.
- [x] (文档) 运行 `generator/auto-generate-for-autojs6.bat`, 同步 `AutoJs6-Plugin-Offline-Docs`, 两仓库版本号与宿主对齐后提交. 证据: 以 `--sync-offline --increment-versions` 全量生成 (文档 versionCode 116 -> 117, 文档仓库 `980e48b`), 页内锚点全部可解析; Offline-Docs `75b8b4b` (`VERSION_BUILD=145`, 两份 `SOURCE_PROVENANCE.md` 记为 `62f55d8884`, 6.8.6 更新日志 10 种语言, 单元测试通过). 未推送.

### P5.2 声明 (AutoJs6-TypeScript-Declarations, Ace Editor)

- [x] (文档) `declarations/autojs6/aj6-int-files.d.ts`: `Internal.Files` 增加 `batchRename: Internal.FilesBatchRename`; 新增 `Internal.FilesBatchRename` 可调用接口与 `BatchRenameOptions` / `BatchRenamePlan` / `BatchRenamePlanItem` / `BatchRenameResult` / `BatchRenameHistoryEntry` 类型 (附录 B.4 草案), JSDoc 含 `@example` 与 "Source code summary (zh-CN: 源代码摘要)", 头部 `@Source` 指向宿主 augment 与桥接文件; 以 "provided by the external Batch Rename plugin" 说明插件依赖. 证据: 声明仓库 `f53adbd` (4.39.0 -> 4.40.0): `Internal.Files` 增加 `readonly batchRename: FilesBatchRename`, 可调用接口含 `plan` / `apply` / `undo` / `history` / `isAvailable` / 三个 `Async` 形态与 `BatchRenameError` 构造器, 头部 `@Source` 指向宿主 augment, 参数, 服务, 错误码与契约值文件; 冒烟夹具新增 10 行 (含两条 `@ts-expect-error`), `verify-api-coverage` 在 TypeScript 5.3.3 / 5.9.3 下通过, 其余校验器与文档生成检查通过; 更新日志 10 种语言. 偏差: 类型位于 `Internal.FilesBatchRename` 命名空间 (`Options`, `ApplyOptions`, `NormalizedOptions`, `Plan`, `PlanItem`, `Result`, `ResultItem`, `UndoResult`, `HistoryEntry` 等), 而非附录 B.4 草案的顶层 `BatchRename*` 名称; `dateTime` / `normalize` 另接受宿主同样接受的 `'none'`.
- [x] (文档) Ace Editor 仓库同步同一 d.ts, 运行 `:app:generateAutoJs6LspDeclarations`; 两仓库版本号 +1 与版本名递增 (minor) 后提交. `aj6dts.bat -Publish` 只在宿主 Java 签名变化时运行, `aj6-int-*.d.ts` 需手工同步. 证据: Ace `c874d1d` (2.1.0 -> 2.2.0, `VERSION_BUILD=276`): 随附声明 4.40.0, 经宿主 `tools/ace-completion/generate-editor-assets.mjs` 重建静态索引, `lib.autojs6.d.ts` 与 LSP 声明组并完成其校验, `:app:generateAutoJs6LspDeclarations` 与 `:app:verifyAutoJs6LspRuntime` 通过; 宿主 `e3331a725` (`VERSION_BUILD=3794`, 本地) 提交补全索引源. 未推送.

### P5.3 README, changelog 与兼容矩阵

- [x] (插件) `.readme/lang_*.json`: 简介, 功能 (对照 PowerRename 的用户可感知差异: 修改时间代替创建时间, 本土扩展, 撤销), 安装 (插件中心), 文件管理器用法 (三处入口, 截图), 脚本示例 (与文档一致), 兼容性 (最低宿主版本, 可移动存储说明), 常见问题 (正则与 JS 一致, 为什么计数器不递增, 为什么对话框里看不到入口), 发行历史, 许可证与第三方声明; `plugin_instruction` 10 语言. 证据 (2026-10-15): 插件 `cdac57e`; 去掉开发预览说明, 10 种语言新增 "在文件管理器中使用" (三处入口与预览, 排除, 撤销) 与 "兼容性" (最低宿主, FAT32 / exFAT 的大小写与不允许字符, 2000 项与 32 层上限) 两节, 脚本 API 改为最终形态并链接 AutoJs6 文档, 常见问题新增正则, 计数器与对话框入口三问; 插件中心说明同步加入三处入口; `generate_markdown.py` 渲染两个新列表; 界面标签与宿主 "更多" 的叫法逐语言核对过 (西语去掉了非 ASCII 的 U+00BF). 未执行: README 截图 (仓库尚无 `docs/images/screenshots`, 1.0.0 先不放图).
- [x] (插件) `.changelog` 10 语言 `v1.0.0` 条目 (`feature` 为主, 不写协议号); `generate_markdown.py` 与 `--check` 通过. 证据: 插件 `cdac57e` (提示只保留最低宿主, 8 条 feature, 依赖补充 Desugar JDK Libs 2.1.5 与 Rhino) 与 `adb7100` (回填 5438); `generate_markdown.py --check` 与 JVM 守卫 (含 ASCII 标点) 通过.
- [x] (测试) 兼容矩阵 (附录 F): 每台在线设备完成 R3 冒烟; 16 KB 页设备或 AVD 一次; 低端设备 (G8441, API 28) 2000 项预览耗时与内存记录. 证据 (E3, 2026-10-15/16): 插件 `.python/device_smoke.py` (安装宿主 debug 与其测试包, 换上 release 包, 强制停止插件后跑宿主 `BatchRenameExplorerFlowTest` 5 项, `BatchRenameScriptApiTest` 2 项, `BatchRenamePluginRoundTripTest` 3 项). 发布包 `autojs6-plugin-batch-rename-v1.0.0-35aa6dd3.apk` (versionCode 29, 2,203,815 字节, SHA-256 `e57a8efe081140aed129fa31715b8ce8eb1cfead7b6acbf4c99ae395c7741a4d`) 全部通过: Sony G8441 (API 28), Sony XQ-AT72 (API 31), 小米平板 23046RP50C (API 35), OnePlus OPD2413 (API 35, ColorOS, 维护者在手机上确认安装; 宿主 5447 自动唤醒了被停止的插件); 候选包 versionCode 26 (代码相同) 另在 Sony XQ-DQ72 (API 33), `AVD_API_35_16K` (16 KB 页) 与 `AVD_API_24` 上全部通过, XQ-DQ72 在发布包冒烟时已离线. G8441 上 2000 项预览重算中位数 130 ms (500 项 41 ms), Java 堆增长约 12 MB, 测试进程 PSS 约 99 MB (`PreviewPlannerBenchmarkTest`, debug 包). 未执行: Samsung API 36 / 37 (未连接), Redmi bek749scrwv4wo8h (为 MCP Server 保留旧宿主 5352).
- [x] (测试) 性能与体积: 2000 项 `plan` 往返 (宿主计时) < 1 s (API 33 真机); 界面 500 项预览重算 < 100 ms; release APK 体积与 D28 预算对照. 证据 (2026-10-16): 2000 项 `plan` 往返在 XQ-DQ72 (API 33) 上由脚本计时 (含枚举 1999 个文件与文件夹本身, Binder 往返, 插件计算与结果转换, release 候选包): 首次 478 ms, 之后 302 / 263 / 272 / 261 ms; 界面 500 项预览重算中位数 15 ms (XQ-DQ72) / 41 ms (G8441), 2000 项 39 ms / 130 ms (插件 `PreviewPlannerBenchmarkTest`, debug 包, 正则 + 计数器 + 日期变量); release APK 2,203,815 字节, 低于 D28 预算 3.5 MB, 签名与 16 KB 对齐校验通过 (`appendDigestToReleasedFiles`).

### P5.4 1.0.0 发布

- [x] (宿主) 最终 `REQUIRES_HOST_VERSION` 回填 (D26), 宿主 `docs/dev/` 与 `.changelog` 终稿; 宿主侧全部提交落本地并在本路线图记录 hash, 提示维护者合并. 证据: 插件 `adb7100` 回填为 5438 (宿主 `852afb776` 即 `files.batchRename` 的 VERSION_BUILD 3786 + 1652; 此后的宿主提交只有测试, 文档与通用的唤醒修复 `0a7ab1864`), 构建脚本, `BatchRenamePlugin.REQUIRED_HOST_VERSION`, `common.json`, 冻结测试与生成的 README / changelog / 说明同步; 宿主 `51ae81cb0` 把协议文档状态改为终稿; 宿主 6.8.0 zh-Hans changelog 的两条 (P3.2 / P4.4) 不变. 宿主本地提交 (维护者同意合并, 暂不推送): `aa4fd4f6d`, `95c9f0455`, `dcc5bd98d`, `26656b7c4`, `383d7dab8`, `3bcd17aba`, `852afb776`, `f1088b0f7`, `805cf4f7c`, `24588bd1e`, `0a7ab1864`, `e3331a725`, `51ae81cb0`. 声明仓库的提示行由 5437 更正为 5438 (`9b92d5c`, 已推送).
- [x] (发布) R1-R4: 版本, changelog, README, 推送前检查, 签名发布, tag `v1.0.0`, GitHub Release. 证据 (E5, 2026-10-16): 发布提交 `7615b9d` (10 种语言 changelog 日期 2026/10/16) 与 `0342338` (路线图标点; `VERSION_BUILD=29` = 提交数); `prepush_check.py` 6/6; `autojs6-plugin-batch-rename-v1.0.0-35aa6dd3.apk` (versionCode 29, 2,203,815 字节, SHA-256 `e57a8efe081140aed129fa31715b8ce8eb1cfead7b6acbf4c99ae395c7741a4d`), 签名证书 SHA-256 `31a681fc...c213`, 4 ABI 的 `libandroidx.graphics.path.so` 未压缩且 16 KB 对齐 (`verifySignedReleaseArtifacts`); 新建公开仓库 `SuperMonster003/AutoJs6-Plugin-Batch-Rename` 并推送, 轻量 tag `v1.0.0` = `0342338`, GitHub Release "Batch Rename v1.0.0" (Latest, APK + `SHA256SUMS`), 下载复核哈希一致. CI: Markdown integrity 与 Icon Studio recipe 通过; Build integrity 的仓库检查 / 单元测试 / APK 与 API 35 契约任务通过, API 24 契约任务因测试截图调用 API 26 才有的 `PixelCopy.request(Window, ...)` 失败 (测试问题, 发布包不受影响), 已由 `7dc23f7` 改为 API 26 起才截图, 本地 `AVD_API_24` 上 14 项通过.
- [x] (索引) R5: `official-repositories.json` 加入仓库名 (README 中的 "48 个官方项目" 同步为 49), 发布清单登记, 生成索引与索引 README. 证据: 索引仓库 `07f08e9` (已推送): `official-repositories.json` 加入本仓库 (README 中的项目数由 48 变为 49), `release-manifests/io.github.supermonster003.autojs6.plugin.batch.rename/29.json`, 目录图标 (插件中心日夜图标, recipe SHA-256 `4ea4af23...`), 生成的 `plugins.official.generated.json` 新增条目 (requiresHostVersion 5438, nativePageAlignment 16384); 生成器原本只解析字符串常量, 本次让它也接受顶层整数字面量 (`val requiredHostVersion = 5438`), 并补测试 (索引仓库 76 项测试通过).
- [x] (文档) R6: 四仓库提交 hash 回填本节. 证据: 文档 `021a6a6` / `62f55d8` / `980e48b`, Offline-Docs `75b8b4b`, 声明 `f53adbd` / `9b92d5c`, Ace `c874d1d` (均已推送, 见 P5.1 / P5.2); 宿主本地提交 (维护者同意合并, 暂不推送) 见 P5.4 第一条, 另有通用的 ColorOS 自动唤醒 `f6462fdb6`.

---

## P6: 1.1.0

决策见 D31-D42 (2026-10-16 经六轮选择题确认), 按 P6.1 -> P6.7 的顺序实施 (D33). 最低宿主版本随宿主改动提高 (D32); 契约按需升级 (`batch-rename-api` v2, Explorer Action v26), 旧宿主与旧插件按既有的协议门处理.

### P6.1 拼音 (D35)

- [x] (插件) 词典: 单字 + 常用词组 (多音字按词组判断, 繁体同样转换), 来源与许可证写入第三方声明; 记录 APK 增量 (预算 3.5 MB). 证据 (2026-10-16): 数据源经维护者选定为 mozillazg 数据, 原样存于 `third_party/` (`-text`, SHA-256 由生成器校验): pinyin-data `9193766` (MIT, 单字取首个读音), phrase-pinyin-data `cee0ed6` (MIT, 只用 `pinyin.txt`; `large_pinyin.txt` 含 CC BY-SA 的 CC-CEDICT 数据, 不用), OpenCC `ver.1.4.2` 的 `TSCharacters.txt` (Apache-2.0). `.python/generate_pinyin_dictionary.py` 生成 Java 资源 `pinyin/` (JVM 测试与设备共用 ClassLoader 加载): 单字 44,435 个, 只保留读音与逐字读音不同的词组 8,758 个, 繁转简单字 3,222 个, 另有三份许可证全文的 `NOTICE.txt`; 本仓库修正 `.python/pinyin_overwrite.txt` 4 条 (34 个常见词样本中上游错 3 个: 照片, 相片, 还款; 另补影片). Build integrity 工作流运行 `--check`. 第三方声明新增 "Pinyin dictionary" 一节. release APK 2,520,244 字节 (1.0.0 为 2,203,815 字节, 增 316,429 字节, 其中 `pinyin/` 四个文件压缩后 303,629 字节), 在 D28 预算 3.5 MB 内. 词典首次加载 (即首次拼音预览的额外延迟): XQ-AT72 148 ms (再次约 55 ms), 低端 G8441 471 ms (再次约 130 ms), 均在后台线程.
- [x] (插件) 名称整理选项 "汉字转拼音" (流水线位置: 替换之后, 文本格式之前) 与替换变量 `${pinyin}` (展开为 `target` 指定部分的原名拼音), 共用风格选项: 声调 `none` / `mark` / `number`, 音节分隔 `none` / `space` / `underscore` / `hyphen`, 大小写 `lower` / `capitalize`, 仅首字母; 默认无声调, 无分隔, 小写; 非汉字原样保留. 证据: `engine/pinyin` (`PinyinDictionary` 惰性加载, 码点有序数组二分查找; `PinyinConverter` 在繁转简后的文本上按最长词组匹配, 单字优先用本字读音). 实施细节: 分隔符只放在相邻音节之间 (`IMG_照片2024` -> `IMG_zhao_pian2024`); 无声调与数字声调时 `ü` 写作 `v`, `ê` 写作 `e`, 使名称保持 ASCII; 轻声不加数字; 仅首字母取每个音节的第一个字母且不带声调; `${pinyin}` 与其他变量不同, 无需开关 (`$${pinyin}` 保持字面); 界面在 "名称清理" 首行加开关, 开关打开或替换文本含 `${pinyin}` 时显示四项样式, 变量面板新增 "拼音" 组; 10 种语言 16 条字符串; 选项随 "记住上次选项" 保存. `BatchRenamePlugin.FEATURES` 加入 `pinyin`. E4 (2026-10-16): XQ-AT72 (API 31) 上 Compose 测试截图 `.bak/visual/1.1.0/pinyin.png`, 已检查开关与四项样式的显示.
- [x] (契约 / 宿主) 新选项键 (契约追加键或 v2, 设计时定), 脚本参数解析与默认值. 证据: 定为契约 v1 追加键 + 可选功能 `pinyin` (不升 v2): 宿主提交 `09b781edf` (`BatchRenameOptionKeys` 5 个键, `BatchRenameValues` 取值集合与 `FEATURE_PINYIN`, 协议文档; `VERSION_BUILD=3797`), 插件 `libs/batch-rename-api.aar` 由此重新暂存 (SHA-256 `65886980...cc7b9`). 宿主提交 `d4f69c187`: `files.batchRename` 解析五个选项 (`pinyinTone` / `pinyinSeparator` 接受 `false`, `plan.options` 显示默认值); 插件未声明 `pinyin` 时不发送这些键, 脚本用到 `pinyin` 选项或 `${pinyin}` 变量则报 `PLUGIN_INCOMPATIBLE`; zh-Hans changelog 两条既有条目补充 "汉字转拼音". 两个宿主提交均仅在本地.
- [x] (测试) JVM: 常见多音字 ("重庆", "银行", "长大", "行长"), 繁体, 各风格组合; Compose 界面用例; 宿主脚本 API 用例. 证据: 插件 JVM `PinyinConverterTest` 8 项 (19 个多音词, 6 个繁体词, 三种声调, 分隔, 大小写, 首字母, 非汉字与扩展 B 区字符), `RenameEngineTest` 新增流水线顺序与变量 2 项, 选项解析, 模板, 偏好往返同步扩充, 共 127 项通过; 设备 (XQ-AT72, API 31) `connectedDebugAndroidTest` 15 项通过 (含 `pinyinCleanupRenamesChineseNames`), `PinyinDictionaryDeviceTest` 在 XQ-AT72 与 G8441 通过. 宿主 JVM `BatchRenameScriptArgumentsTest` 新增 2 项 (共 9 项通过); 设备: `device_smoke.py` 以 release 候选包 (versionCode 33) 在 XQ-AT72 上 5 + 2 + 4 项通过, 其中 `BatchRenamePluginRoundTripTest.pinyinComesFromTheBuiltInDictionary` 证明 R8 后的词典可跨进程使用, `BatchRenameScriptApiTest.scriptsConvertChineseNamesToPinyin` 通过; 换回 1.0.0 发布包后 `olderPluginsRefuseScriptsThatUsePinyin` 通过 (设备保持 1.0.0 插件).

### P6.2 命名预设 (D36)

- [x] (插件) 预设存储: 名称 -> 契约选项 (不含 `paths` 与 `onProgress`), 带版本号的 JSON; 界面顶栏入口: 保存当前选项为预设, 加载, 重命名, 删除. 证据 (2026-10-16): 插件 `presets/`: `Json` (严格的小型 JSON 读写器: 插件不带 JSON 库, JVM 测试与设备运行同一份代码; 拒绝重复成员名, 深度上限 32), `PresetDocument` (格式 `autojs6-batch-rename-presets` 版本 1, 选项用契约键, 缺省键取默认值, 未知键丢弃, 名称去除首尾空白, 逐项校验), `PresetStore` (以一个文档存于插件偏好键 `presets`, 界面与能力服务同进程共用一把锁; 至多 100 个, 名称 1-64 字符, 合计 131,072 字符; 保存前经选项解析器校验, 读不回来的选项不保存). 选项到契约值的编码统一为 `RenameOptionsParser.values` (预设, 方案应答与 "记住上次选项" 共用). 界面按 D46: 顶栏 "预设" 按钮 (`ui/PresetsUi.kt`, 点按即加载), 保存对话框 (同名时确认替换), 管理面板 (勾选导出, 重命名, 删除); 10 种语言 46 条字符串, 3 个 Material 图标. E4: XQ-AT72 (API 31) 上 Compose 测试截图 `.bak/visual/1.1.0/presets-menu.png`, `presets-manage.png`, `presets-conflict.png` 已检查; 据此把同名对话框的四个按钮改为单列 (原先 "取消" 与 "覆盖" 挤在一行). release APK 2,595,236 字节 (P6.1 后为 2,520,244 字节).
- [x] (插件) 导入导出: 系统文件选择器读写 JSON 文件, 剪贴板复制 / 粘贴; 导入时逐项校验并报告被拒的条目, 同名时询问覆盖或另存. 证据: `CreateDocument` / `OpenDocument` (无需存储权限, 启动器注册在管理面板之外, 面板关闭后仍能收到结果) 与剪贴板 (Android 13 起由系统提示已复制); 导入先整体解码, 有同名时询问覆盖 / 另存为副本 / 跳过 (D43 的三种策略), 有被拒条目时列出序号, 名称与本地化原因; 读取文件最多读上限加 1 个字符. 剪贴板往返由 Compose 测试覆盖, 经内容解析器的文件读写往返由 `PresetFileRoundTripTest` 覆盖; 系统文件选择器界面本身的往返由维护者人工验收 (见下方测试条目).
- [x] (契约 / 宿主) 脚本 `options.preset` (显式选项覆盖预设), `files.batchRename.presets()`, `files.batchRename.importPresets(json)`; 插件在计算方案时解析预设, 契约新增列出与导入方法. 证据: 宿主提交 `bc65c8256` (契约 v1 追加 AIDL 事务 `presets` / `importPresets`, 选项键 `preset`, 结果键 `options`, `BatchRenamePresetKeys`, 同名策略取值, 三项上限与功能 `presets`; 协议文档新增 "Presets" 一节; `VERSION_BUILD=3798`), 插件 `libs/batch-rename-api.aar` 由此重新暂存 (SHA-256 `f1fe6ab1...6a16`). 插件: `PlanCodec.withPreset` 按 "默认值 < 预设 < 请求" 合并, 未知预设为 `INVALID_OPTIONS`, 每个方案应答都回传生效选项 (D44); `PresetCalls` 实现两个事务; `FEATURES` 加入 `presets`. 宿主提交 `faa9df686`: 带 `preset` 时宿主只发送脚本给出的键与预设名, 由插件解析; `plan.options` 显示插件回传的完整选项, `apply` 重算时发送这份选项 (不带预设); `presets()` / `importPresets(json, { onConflict })` 及其 `Async` 形态; 插件未声明 `presets` 时三者均为 `PLUGIN_INCOMPATIBLE`; 文档超过上限时在进入 Binder 前即为 `LIMIT_EXCEEDED`; zh-Hans changelog 两条既有条目补充命名预设. 实施偏差: 宿主为按预设的 `includeSubfolders` 枚举目标, 会先经 `presets` 读取该预设 (同时对不存在的名称提前报错), 解析仍由插件完成. 文档, d.ts, 离线文档与 Ace 留待 P6.7 统一更新.
- [x] (测试) 存储往返, 导入校验, 脚本引用与覆盖顺序 (JVM 与设备), 文件选择器往返 (E4). 证据 (2026-10-16): 插件 JVM `PresetDocumentTest` 5 项 (往返, 默认值, 逐项拒绝, 整体拒绝, JSON 读取器), `PresetStoreTest` 6 项 (保存 / 替换 / 重命名 / 删除, 导出, 三种导入策略, 数量与大小上限), `PlanCodecTest` 新增 2 项 (预设合并与回传选项, 两个事务), 共 140 项通过; 插件设备 `connectedDebugAndroidTest` 18 项在 Sony G8441 (API 28) 上通过 (含 `presetsAreSavedLoadedAndRoundTripThroughTheClipboard` 与 `PresetFileRoundTripTest`), 此前 17 项 (不含文件往返) 在 XQ-AT72 (API 31) 上通过. 设备测试修正: 安装后首次启动较慢时 Compose 测试会因 "No compose hierarchies" 立即失败, 等待函数改为把它视为尚未出现. 未计入的运行: XQ-AT72 自动锁屏后的一轮, 以及小米平板上因其他会话的权限对话框在前台而失败的一轮. 宿主 JVM `BatchRenameScriptArgumentsTest` 新增 2 项 (共 11), `BatchRenameScriptServiceTest` 新增 1 项 (共 7), 均通过; 宿主设备: `device_smoke.py` 以 release 候选包 (versionCode 35) 在 XQ-AT72 上 5 + 5 + 4 项通过, 其中 `scriptsImportListAndPlanWithPresets` 已单独确认执行; 换回 1.0.0 发布包后 `olderPluginsRefuseScriptsThatUsePinyin` (含 `presets()` 与 `preset` 选项为 `PLUGIN_INCOMPATIBLE`) 通过, 卸载时插件数据被清除, 测试预设未残留. E4 (2026-10-16): 维护者在 XQ-DQ72 (API 33, 插件候选包 versionCode 36) 上经系统文件选择器把预设导出到文件再导入, 未发现问题.

### P6.3 脚本单名试算 (D37)

- [x] (契约 / 宿主 / 插件) `files.batchRename.preview(nameOrItems, options)` 与 `previewAsync`: 字符串, `{ name, isDirectory, lastModified }` 或其数组; 返回 `{ newName, status, reason }` 或其数组; 数组时计数器按顺序递增, 只检查数组内重名; 不访问磁盘, `metadata` 选项被拒 (`INVALID_OPTIONS`). 证据 (2026-10-16): 宿主提交 `45e535560`, 只改宿主, 契约与插件无需改动: 宿主把这些名称作为同一目录中仅有的条目 (既有名称即这些名称, 不带文件系统特征) 经现有的 `plan` 事务询问插件, 因此计数器按数组顺序递增, 冲突只来自数组本身, 不枚举也不改名任何文件; `lastModified` 接受毫秒时间戳或 `Date` (`JSON.stringify` 得到的 ISO 文本), 缺省时取调用时刻; 名称必须是 1-1024 字符且不含 `/` 的单个文件名, 至多 2000 个; 预设与其他选项同样可用; 协议文档 "Script path" 一节已补充. `metadata` 选项尚不存在, 其拒绝留待 P6.4 实现.
- [x] (测试) 宿主参数解析 JVM 用例, 设备脚本用例. 证据: 宿主 JVM `BatchRenameScriptArgumentsTest.preview takes names, items or arrays of them` (名称, 条目, 数组, `Date` 文本, 11 种非法输入, 2001 个为 `LIMIT_EXCEEDED`), `BatchRenameScriptServiceTest.preview plans names as one directory without touching the disk`, 宿主相关 JVM 共 41 项通过; 设备 `BatchRenameScriptApiTest.scriptsPreviewNamesWithoutFiles` (Sony G8441, API 28, 2026-10-16, release 候选包 versionCode 36 与宿主 debug 5451, 由 device_smoke.py 一并运行): 单个名称, 计数器按数组顺序 (`01_a.txt`, `02_b.txt`), `includeFolders: false` 时文件夹为 `excluded`, `Date` 作 `lastModified`, 数组内重名为 `conflict`, 非法名称为 `INVALID_OPTIONS`, `previewAsync`, 磁盘内容不变. 另修正插件 `.python/device_smoke.py`: 跳过的用例 (状态 -4) 单独计数, 失败时只报告真正失败用例的堆栈 (此前会把跳过用例的 AssumptionViolatedException 当作失败原因).

### P6.4 元数据变量 (D34)

- [x] (契约 / 插件) 选项 `metadata` 与 PowerRename 第 2 组 25 个变量; 条目携带元数据字段 (契约追加键), 引擎按字段展开; 缺失为空串, `$DATE_TAKEN_*` 缺失时回退到修改时间; 预览标出缺少元数据的条目; 元数据变量先于日期变量匹配 (最长名称优先). 证据 (2026-10-16): 宿主提交 `1e82d595d` (契约 v1 追加选项 `metadata`, 条目键 `metadata` / `metadataMissing`, `BatchRenameMetadataKeys` 19 个原始值键, `METADATA_VARIABLES` 25 个名称, 色彩空间取值与功能 `metadata`; `VERSION_BUILD=3800`), 插件 AAR 由此重新暂存. 插件 `engine/variables/MetadataVariables.kt`: 元数据变量先于日期变量匹配, 最长名称优先, 区分大小写; 选项关闭时整段保持字面 (因此 `$DATE_TAKEN_YYYY` 不会被当作 `$D`); 按 D47-D50 格式化 (文本中的 `/` 改为 `-`); 缺失值展开为空并在应答条目上标 `metadataMissing`, 拍摄时间缺失时回退到请求时区中的修改时间且不算缺失. 实施细节: 宿主只发送原始值, 格式化全部在插件, 两条路径因此一致.
- [x] (宿主) 脚本路径: `metadata` 开启且模板含元数据变量时, 对图片条目用 `androidx.exifinterface` 读 EXIF, 缺作者 / 版权等时解析 XMP 包, 字段随请求发送; 懒读取, 并发上限; 2000 项在 API 33 真机上的读取耗时实测并记录. 证据: 宿主 `1e82d595d` / `bb4d06061`: `BatchRenameMetadataReader` 经 `androidx.exifinterface` (原为传递依赖 1.3.6, 现显式声明同一版本, APK 不变) 读取 EXIF, 作者与版权缺失时读 XMP `dc:creator` / `dc:rights` (按命名空间与本地名匹配: `Xml.newPullParser` 会解析命名空间, 首版按带前缀的名称匹配而读不到, 设备测试发现后修正); 只有 `metadata: true` 且 `replace` 含元数据变量时才读取, 只读文件, 每次并发 4 个; 插件未声明 `metadata` 时 `metadata: true` 为 `PLUGIN_INCOMPATIBLE`, `preview` 带 `metadata: true` 为 `INVALID_OPTIONS` (D37); 脚本方案条目带 `metadataMissing`. 读取耗时: 2000 张生成的小 JPEG 在 `AVD_API_35_16K` 上 415 / 378 / 396 ms (`BatchRenameMetadataBenchmarkTest`). API 33 真机 (2026-10-16): 同一测试在 XQ-DQ72 上 108 / 76 / 77 ms (宿主 debug 5453).
- [x] (宿主 / 插件) 文件管理器路径: Explorer Action v26 新增会话事务, 插件按需批量请求已列出条目的元数据 (宿主读取, 插件仍不接触文件); v25 宿主上界面隐藏 "元数据变量". 证据: Explorer Action v26 (宿主 `1e82d595d`, 协议文档 `docs/dev/explorer-action-protocol-v26.md`): `IExplorerActionHostSession` 末尾追加 `readMetadata`, 只对声明 `targetRename` 的动作开放, 只能读请求目标与本会话列出过的后代, 每次至多 128 个, 应答 `ExplorerPhotoMetadataKeys` (与 `BatchRenameMetadataKeys` 同值, 宿主测试断言两者一致). 插件按 `HOST_VERSION_CODE >= METADATA_HOST_VERSION` (5452, 发布前按宿主最终提交回填) 判断宿主是否支持 (请求中的协议版本是动作目录声明的版本, 不能用来判断); 开关打开且替换文本含元数据变量时才按 128 个一批读取已列出的文件, 结果按条目缓存; 旧宿主上隐藏开关与变量组. 界面: "范围与格式" 中的 "元数据变量" 开关, 变量面板 "照片元数据" 组 25 项, 缺值的行标 "缺少元数据"; 10 种语言 28 条字符串. E4: `AVD_API_35_16K` 上 Compose 测试截图 `.bak/visual/1.1.0/metadata.png` 已检查 (有元数据的行 `Pixel 92026_1.jpg`, 缺值的行 `2023_2.jpg` 并标 "missing metadata").
- [x] (测试) 宿主读取用例 (无 EXIF, 截图, JPEG / PNG / WebP / HEIF / DNG, XMP 作者与版权), 引擎用例, 两条路径的设备联调. 证据 (2026-10-16): 插件 JVM `MetadataVariablesTest` 5 项 (25 个变量的格式, 取整与单位, 缺失与回退, 先于日期变量且关闭时为字面, 引擎按条目展开并标记缺失), 选项解析扩充, 共 145 项通过; 插件设备 19 项在 `AVD_API_35_16K` 上通过 (含 `metadataVariablesNameRowsFromTheHostMetadata`, 测试替身实现 `readMetadata`). 宿主 JVM: 参数与门控, 只在需要时读取, 两套键一致, 共 175 项相关测试通过; 宿主设备 (`AVD_API_35_16K`, 宿主 debug 5452, release 候选包 versionCode 37): `BatchRenameMetadataReaderDeviceTest` 4 项 (生成的 JPEG 全部字段, EXIF 作者优先而 XMP 补版权, AdobeRGB 判定, PNG 与 WebP 的 EXIF, 无 EXIF 的截图只有尺寸, 非图片与伪图片), `ExplorerActionMetadataV26InstrumentationTest` 2 项, `BatchRenameScriptApiTest.scriptsExpandMetadataVariables`, `BatchRenamePluginRoundTripTest.metadataTravelsWithTheItems` (R8 后的 release 包) 与 `device_smoke.py` 其余用例通过; 换回 1.0.0 后 `metadata: true` 为 `PLUGIN_INCOMPATIBLE`. 真实样本 (`BatchRenameMetadataSamplesDeviceTest`, 宿主 `d251c1e8c`, XQ-DQ72): 维护者放在 `/sdcard/samples` 的 14 个文件 (ARW 2, CR2 2 (其一名为 broken), DNG 3, HEIC 3, NEF 1, PEF 1, RAW 1, RW2 1) 读取均未抛出, 每个 0-18 ms, 全部读到尺寸; 13 个读到相机, 拍摄时间与曝光参数, iPhone 12 的 HEIC 另有 GPS 与镜头. 缺少的字段在本机解析 TIFF / EXIF 结构后确认文件中本来就没有: Nikon D90 NEF 无标准 ISO 标签 (只在厂商注释中), Leica M8 DNG 无光圈, 社交应用保存的 HEIC 完全没有 EXIF (只读到尺寸). 真机联调 (XQ-DQ72, 插件 debug versionCode 38): `BatchRenameScriptApiTest.scriptsExpandMetadataVariables`, `BatchRenamePluginRoundTripTest` 4 项 (含 `metadataTravelsWithTheItems`), `ExplorerActionMetadataV26InstrumentationTest` 3 项通过. 未执行: 在真机的文件管理器中经插件界面使用元数据变量的人工操作 (该路径由宿主会话测试与插件测试替身分段覆盖).

### P6.5 重命名历史页面与保留策略 (D38, D39, D51)

- [x] (宿主) 页面: 入口为文件或文件夹 "重命名" 对话框 "更多" 菜单末尾的 "重命名历史" (D51, 取代 D38 的 "与回收站同级"); 列表 (来源, 状态, 时间, 条目数), 明细 (旧名 -> 新名与状态), 整批撤销, 删除单批, 清空; 按来源 / 状态筛选, 按文件名 (旧名或新名) 搜索; 菜单中设置每个来源的保留数 20 / 50 / 100 (默认 20). 证据 (2026-10-16): 宿主 `d251c1e8c` (`VERSION_BUILD=3802`, versionCode 5454): `ui/storage/BatchRenameHistoryActivity` 基于 `BaseSearchSortListActivity` (新增 `matchesFilters` 钩子, 供菜单筛选) 与回收站的行布局; 每行为第一个条目的 `旧名 -> 新名`, 时间, 来源, 状态, 条目数与文件夹, 撤销未完成的批次高亮; 明细最多列出 200 个条目, "撤销" 在后台线程调用执行器, 有条目未能恢复时提示并再次显示明细; 删除与清空先确认; 保留数为单选对话框, 调低时立即裁剪. `ExplorerRenameDialogMenu` 末尾始终为 "重命名历史" (插件动作因此至多 9 项, 合计不超过 material-dialogs 标题选项的 10 项), 选择后先关闭重命名对话框 (撤销可能改变正在编辑的名称). 11 个字符串文件各 25 条, 复数 2 组. E4: XQ-DQ72 (API 33) 上测试截图 `.bak/visual/1.1.0/rename-history-list.png`, `rename-history-details.png`, `rename-history-retention.png`, `rename-dialog-menu.png` 已检查.
- [x] (宿主 / 插件) 插件界面顶栏 "更多" 菜单末尾 "重命名历史" 打开同一页面 (D51, 取代 D38 的历史下拉入口). 证据: 契约追加 `BatchRenamePluginActions.RENAME_HISTORY` (`org.autojs.autojs6.action.RENAME_HISTORY`), 宿主导出该页面并要求 `org.autojs.permission.PLUGIN`; 插件 AAR 由宿主 `d251c1e8c` 重新暂存. 插件 `ui/HostRenameHistory` 按宿主能否解析该动作决定是否显示菜单项, 旧宿主上自动隐藏, 无需宿主版本常量. 顶栏原 "清除历史" (清除查找与替换的最近使用记录) 改为 "清除最近使用" (10 种语言), 以免与 "重命名历史" 混淆. E4: XQ-DQ72 上测试截图 `.bak/visual/1.1.0/rename-history-menu.png` 已检查. 实施细节: 在宿主页面撤销后返回插件界面, 预览不会自动刷新 (撤销按钮再点会提示没有可撤销的批次), 需重新打开.
- [x] (宿主) 脚本 `files.batchRename.deleteHistory(batchId | result)` 与 `clearHistory({ source })` 作用于全部批次; `BatchRenameJournalStore` 支持可调保留数与删除. 证据: 宿主 `d251c1e8c`: `deleteHistory` 返回是否删除, `clearHistory` 返回删除的批数 (`source` 为 `script` / `explorer` 或省略), 参数错误为 `INVALID_OPTIONS`; `history()` 不带参数时列出全部批次; `BatchRenameJournalStore` 增加 `retention` 回调 (读取页面设置), `delete`, `clear`, `trim`. 删除历史不改动文件; 协议文档 `docs/dev/batch-rename-plugin-protocol-v1.md` 新增 "Rename history page" 一节.
- [x] (测试) 日志存储 JVM 用例, 页面 instrumentation (列表, 筛选, 搜索, 撤销, 删除, 清空, 保留数), 脚本用例; 宿主 zh-Hans changelog. 证据 (2026-10-16): 宿主 JVM `BatchRenameExecutorTest` 新增 2 项 (保留数跟随设置且 `trim` 立即生效; `delete` / `clear` 作用于两种来源), `BatchRenameScriptArgumentsTest` 与 `BatchRenameScriptServiceTest` 各扩充 1 项, `ExplorerRenameDialogMenuTest` 4 项按新菜单更新, 相关共 50 项通过. 宿主设备 (XQ-DQ72, API 33, 宿主 debug 5453 / 5454 测试包): `BatchRenameHistoryActivityTest` 4 项 (列表与副标题, 来源 / 状态筛选, 忽略大小写搜索旧名与新名, 明细中撤销与删除, 菜单清空, 保留数 55 -> 50, 契约动作解析到导出且受权限保护的页面), `ExplorerRenameDialogMenuInstrumentationTest` 3 项 (含新增的 "重命名历史" 用例), `BatchRenameScriptApiTest` 8 项 (6 项通过, 2 项按设备状态跳过; 新增 `scriptsDeleteAndClearTheHistoryOfEverySource`, 宿主 `f24589547`) 与 `BatchRenameExplorerFlowTest` 5 项通过. 页面测试与新脚本用例期间移开设备原有日志, 结束后放回, 已核对前后文件一致; 顺带修复了进度回调用例每次遗留 1 个批次的问题. 插件 JVM 145 项, 插件设备 20 项 (含 `renameHistoryOpensTheHostPageWhenTheHostHasOne`, 在宿主中打开页面并返回) 在 XQ-DQ72 上通过. 宿主 zh-Hans changelog 已在 `d251c1e8c` 中补充 (`files.batchRename` 条目加 "历史记录的查询与清理", 新增 "重命名历史" 页面条目).

### P6.6 P4.4 失败路径补测 (D40)

- [x] (测试) 版本过低的插件 (专门构建要求更高宿主版本的包) 与旧宿主 (协议 24) 上的表现. 证据 (2026-10-16): (1) 要求更高宿主的插件: 特制包的做法是临时把 `app/build.gradle.kts` 的 `requiredHostVersion` 与 `BatchRenamePlugin.REQUIRED_HOST_VERSION` 改为 9999, 构建 debug 包后立即还原 (不提交). 装在 XQ-DQ72 (API 33, 宿主 debug 5454) 上, 宿主 `BatchRenameNewerHostPluginDeviceTest` 2 项通过 (宿主 `dbf16a347`; 正常插件下按设计跳过): 脚本 `isAvailable()` 为 false, `plan` 为 `PLUGIN_INCOMPATIBLE`, 消息为 "需要升级 AutoJs6" 的提示 (要求 9999, 当前 5454); 文件管理器所有入口都不提供该插件的动作, 重命名对话框菜单为 "批量重命名..." 与 "重命名历史". 发现并修复: 此时 "批量重命名..." 的引导原先提示 "插件协议与当前 AutoJs6 不兼容, 需升级插件版本", 方向相反; 宿主 `dbf16a347` 改为读取插件服务的 `requiresHostVersion` meta-data, 说明要求与当前 versionCode 并提示升级 AutoJs6 (`ExplorerBatchRenameAvailabilityPolicyTest` 新增 1 项). E4: `.bak/visual/1.1.0/newer-host-guidance.png`, `newer-host-plugin-center.png` 已检查. 插件中心显示列表时会把要求更高宿主的插件写为停用 (宿主既有行为, 对所有插件一致; 据代码, 宿主升级后需在插件中心重新启用); 测试后装回正常包, 并经 `BatchRenameExplorerFlowTest` 的停用用例恢复启用, 脚本往返用例随后通过. (2) 旧宿主 (协议 24): 只读 `AVD_API_33` 上装宿主 5352 (提交 `8acb4e75f`, Explorer Action 协议 24, 尚无 `files.batchRename`; x86_64 debug 包取自宿主 `.bak/maple-host-acceptance-20261015/`, 未使用维护者为 MCP Server 保留旧宿主的 Redmi) 与当前插件 (versionCode 39, 已获 PLUGIN 权限): 宿主运行正常; 脚本中 `typeof files.batchRename` 为 `"undefined"`; 文件管理器多选工具栏与其 "More" 菜单只有宿主自带的项 ("Copy path"); 插件页中 Batch Rename 的开关为关, 点按时提示 "需要 versionCode 5438, 当前 5352, 需要升级 AutoJs6". E4: `.bak/visual/1.1.0/old-host-selection.png`, `old-host-selection-more.png`, `old-host-plugins.png`, `old-host-plugin-toggle.png` 已检查. 插件界面收到协议 24 请求时结束并关闭会话, 由 `BatchRenameScreenTest.anOlderProtocolEndsTheScreenAndClosesTheSession` 覆盖 (旧宿主不收录插件的动作, 实际不会发出这种请求).
- [ ] (测试) FAT32 / exFAT 上只改大小写: 挂载 FAT32 虚拟 SD 卡的模拟器; 发布前在维护者租用的带 SD 卡三星设备上复核. 已完成部分 (2026-10-16): `mksdcard` 制作 512 MB FAT32 镜像, 以 `-sdcard` 挂到只读 `AVD_API_33` (公共卷 `0000-0000`, 底层 `vfat`); 宿主 `BatchRenameRemovableStorageDeviceTest` (宿主 `450d2b4b5`) 在 SD 卡上宿主自有目录中先确认该卷不区分大小写, 再经 `files.batchRename` 把 `IMG_1.JPG`, `Notes.TXT`, 文件夹 `Album` 及其中的 `Cover.PNG` 改为小写并撤销, 从目录读回的确切名称与预期一致, 通过. 未执行: exFAT (模拟器上未制作 exFAT 卷), 维护者租用的带 SD 卡三星设备上的复核 (发布前).

### P6.7 图标, 文档与 1.1.0 发布 (D41)

- [ ] (插件) 新图标: 按 "两行文本行与一个替换箭头" 设计亮 / 暗两张矢量源图, 维护者确认后经 Icon Studio 生成, 同步更新索引目录图标. 已完成部分 (2026-10-16): 两轮候选 (按 Icon Studio 视觉尺寸 V = 0.52 归一化, 插件中心亮 / 暗底 48 / 64 / 96 / 160 px 对比): 第一轮三个 "空心旧名称行 + 箭头 + 实心新名称行" 方案, 维护者认为看起来奇怪 (简洁可取), 要求换思路; 第二轮改为不带箭头的隐喻, 维护者选定 "三行长短不一的文本行 + 贯穿三行的文本光标 (I 形)", 即同时编辑多行名称 (偏离主题原文: 不再有替换箭头). 矢量原稿 `.icons/source/batch-rename.svg`; 渲染为 512 px 透明源图 (`mipmap/ic_launcher.png` 与 `mipmap-night/ic_launcher.png`, 同一 alpha, 分别为 #272727 与 #D8D8D8, 并按摘要存入 `.icons/assets/`), 配方只改源图引用, 其余参数不变, `generate_icon_studio.py` 生成 7 个图像资源, `--check` 15 项一致. 未执行: 索引目录图标 (随 1.1.0 发布登记索引时更新), 真机插件中心与系统应用信息页的目视检查.
- [x] (文档) 文档, d.ts, 离线文档, Ace 同步 (新选项, 变量, `preview`, 预设, 历史清理). 证据 (2026-10-16, 均未推送): 文档仓库 `989d08a` (`api/files.md`: `preview` 两个重载, `deleteHistory`, `clearHistory`, `presets`, `importPresets` 及异步形态, 拼音 / `metadata` / `preset` 选项, `${pinyin}` 与 25 个元数据变量, 方案条目 `metadataMissing`, `history()` 省略 limit 返回全部, 错误代码补充; 6.8.0 的 files.batchRename 条目在 10 种语言中合并更新) 与 `fa3fb08` (计数 121); 离线文档插件 `f99c84d` (构建 149); 声明仓库 `aa2dfb3` (4.42.0, `Options` 改为 `search` 与 `preset` 二选一的联合类型, 冒烟用例扩充, `verify-api-coverage` 诊断 0); 宿主 `bd75aa240` (补全索引源); Ace 编辑器插件 `5ddc74f` (2.4.0, 构建 279, 推送前检查 6/6). 文档示例的输出在只读 `AVD_API_33` 上以宿主 debug 与插件 debug 实测 (试算, 拼音三种声调, `20090210_NIKON D90.NEF`, 预设导入的拒绝条目, 历史删除).
- [ ] (发布) 10 种语言 README 与 changelog, 兼容矩阵冒烟 (含 OnePlus), R1-R6 (最低宿主版本按宿主最终提交回填). 已完成部分 (2026-10-16): R1 `VERSION_NAME=1.1.0`, 10 种语言 v1.1.0 changelog (提示, 5 项新增, 2 项优化; v1.0.0 的提示改为字面 5438); 最低宿主回填为 5455 (D52, `app/build.gradle.kts`, `BatchRenamePlugin.REQUIRED_HOST_VERSION`, `.readme/common.json`, `PluginRuntimeInfoTest`); R2 10 种语言 README 与插件中心说明: 简介, 3 项新功能, 撤销与重命名历史, 文件管理器入口, 变量表 `${pinyin}` 与元数据两行, 脚本 API 要点, 常见问题 (更新 PowerRename 对比, 新增元数据为空的说明); README 不含截图, 无需重采; 插件 JVM 145 项通过. 未执行: R3 设备矩阵冒烟, 三星复核 (D53), R4-R6.

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
        reason: null,         // 原因码 (契约 BatchRenameValues.REASON_*), 无则为 null
        excluded: false,      // 脚本排除标志, 可改为 true 后再 apply (选项过滤掉的条目显示为 status "excluded" / reason "notIncluded")
        overwrites: false,    // onConflict 为 "overwrite" 时将替换既有条目 (P3.2 起代替 twoPhase, 两阶段由宿主执行器自行判定)
    }],
    counts: { rename: 12, unchanged: 3, conflict: 0, invalid: 0, excluded: 1 },
    rejected: false,          // 存在 invalid, 或 onConflict 为 "fail" 且存在 conflict 时为 true
}
// result (apply)
{
    batchId: "b_19c4...",     // 没有可改名的条目时为 null
    counts: { renamed: 12, skipped: 3, failed: 0, stale: 0, excluded: 1 },
    items: [{ path, newPath, status: "renamed" | "skipped" | "failed" | "stale" | "excluded", reason }],  // 与 plan.items 一一对应, newPath 为执行后所在位置
    undoable: true,
}
// undo result
{ batchId, state: "restored" | "recoveryRequired", counts: { restored, stale, failed }, items: [{ path, newPath, status, reason }], undoable }
// history entry
{ batchId, source: "script" | "explorer", state, createdAt, updatedAt, counts: { renamed, notRenamed, restored }, undoable }
```

P3.2 实现的补充约定: 每个脚本保留最近 16 个方案供 `apply` 使用; 脚本修改了 `excluded` 或经 `applyOptions.onConflict` 换了策略时, 宿主以同一枚举结果与 `now` 重新询问插件; 每个方案至多执行一次, 再次执行或执行已不保留的方案报 `PLAN_STALE`; 被拒绝的方案报 `PLAN_REJECTED` 且不改名. `onProgress` 抛出的异常不中断批次, 批次完成并记入日志后再抛出 (或拒绝 Promise). `undo` 也可撤销管理器路径产生的批次 (`history` 本就列出它们).

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
    includeFolders: false, // 给出目录时, 目录本身是深度 0 的条目, 其下条目经 includeSubfolders (默认 true) 加入
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

P1.2 已落地 (宿主 `95c9f0455`), 以宿主 `docs/dev/explorer-action-protocol-v25.md` 与 `plugin-api/explorer-action-api` 源码为准; 下面只列摘要.

- Catalog: `targetRename` (Boolean, `ExplorerActionCatalogKeys.TARGET_RENAME`); placement 新值 `PLACEMENT_RENAME_DIALOG = 4` (单目标, 须声明 `targetRename`).
- Session (追加在 `openRelatedFile` 之后, `ExplorerActionAidlOrderTest` 冻结顺序):

```java
/** v25 */ Bundle listRenameParent(int offset, int limit);
/** v25 */ Bundle renameEntries(in Bundle request);
/** v25 */ Bundle queryRenameBatch(String renameBatchId);
/** v25 */ Bundle listRenameBatches();
/** v25 */ Bundle undoRenameBatch(String renameBatchId);
```

- `listRenameParent`: 请求目标共同父目录的条目 (`ITEMS` 中只有 `DISPLAY_NAME` 与 `KIND`), `NEXT_OFFSET`, `COMPLETE`, 外加 `RENAME_CASE_INSENSITIVE` 与 `RENAME_RESTRICTED_CHARACTERS`.
- `renameEntries` 请求: `RENAME_ENTRIES` (1 至 2000 个 Bundle: `RENAME_TARGET_ID`, `RELATIVE_PATH` (空为目标本身, 否则为目录目标下经 `listChildren` 规则校验的后代), `RENAME_NEW_NAME` (单段, 至多 255 个 UTF-8 字节), 可选 `RENAME_OVERWRITE`), 可选 `RENAME_ALL_OR_NOTHING`. 冲突策略只存在于插件方案中, 宿主执行确切名称并重新校验.
- 响应: `RENAME_BATCH_ID`, `RENAME_BATCH_STATE` (`COMMITTED` / `PARTIAL` / `REJECTED`, 撤销后 `RESTORED` / `RECOVERY_REQUIRED`), 创建与更新时间, `RENAME_RENAMED_COUNT`, `RENAME_NOT_RENAMED_COUNT`, `RENAME_RESTORED_COUNT`, `RENAME_ENTRIES` 按请求顺序逐条给出 `RENAME_ITEM_STATE` (`RENAMED` / `NOT_RENAMED` / `STALE`, 撤销后 `RESTORED` / `STALE` / `RECOVERY_REQUIRED`) 与 `RENAME_NEW_RELATIVE_PATH` (P3.1 起不再重复目标 id 与相对路径, 插件按位置对应). 畸形请求整体抛 `IllegalArgumentException` 且不改名; 请求在一次 Binder 事务中传输, 过大时由插件拆分 (每个请求成为单独批次).
- 历史: 按插件 UID 与会话父目录限定 (范围外的批次 id 答 `RENAME_BATCH_STATE_UNKNOWN`), `listRenameBatches` 只返回摘要; 脚本与每个插件各至多保留 20 批 (共用 `noBackupFilesDir/batch-rename/journals`, D21).

---

## 附录 D. 待决事项

| 编号 | 事项 | 影响阶段 | 默认 (未拍板时) |
| --- | --- | --- | --- |
| Q1 | 图标源图: 维护者提供两张黑白源图 (亮色模式深色图案, 暗色模式浅色图案), 主题建议 "两行文本行与一个替换箭头" | P0.1 | 已定 (D41): 由我重新设计亮 / 暗矢量源图, 维护者确认后使用 |
| Q2 | 撤销日志保留批次数 (默认 20) 与是否对脚本暴露清理方法 | P3.1 | 已定 (D39): 每个来源 20 / 50 / 100 可调, 脚本可清理全部批次 |
| Q3 | 宿主是否增加 "重命名历史" 页面 (1.1.0) | P6 | 已定 (D38): 1.1.0 做, 含筛选与搜索 |
| Q4 | Rhino 来源: 宿主 slim jar (锁 SHA) 还是 Maven Central 发布版 | P2.1 | 已定 (P2.1): 宿主 slim jar; Maven Central 1.9.1 不支持 `u` 与 `i` 同时使用 |
| Q5 | 单批上限 2000 是否足够; 超过时是否自动分批 (脚本路径) | P2.5 / P3.1 | 2000, 超限报错 |
| Q6 | `target` 默认 `"all"` (与 PowerRename 一致) 还是 `"name"` (更保守) | P2.2 | `"all"` |
| Q7 | 多选工具栏在插件缺失时是否显示安装引导条目 | P1.4 | 不显示 (沿用既有行为), 对话框 "更多" 显示 |
| Q8 | 工具栏插件动作图标 (当前统一为归档图标) 是否在 v26 允许 catalog 自定义 | P6 | 已定 (D42): 1.1.0 不做 |
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
| 跨插件的转写变量 (繁简转换等) | 需跨插件调用 (3-Hanzi CC), 宿主中转协议未定; 1.1.0 后再评估. 拼音已改为插件内置词典 (D35, 1.1.0) |
| MediaStore 扫描与相册式选择 | 由 3-Maple Image 负责, 本插件只处理宿主交来的目标 |
