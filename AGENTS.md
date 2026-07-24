# AGENTS.md — YUtils 给 AI / 协作者的工作指南

> 本文面向 **AI 编程助手** 与后续接手的开发者。读完应能独立完成：在 `utils` 加功能、在 `test` 加用例、真机跑测验证。  
> 人类可读的库用法说明见 [project.md](project.md) / [README.md](README.md)。

---

## 1. 仓库是什么

| 项 | 说明 |
|----|------|
| 库名 | `com.kotlinx:yutils`（Maven Central） |
| 结构 | 双模块：`:utils`（库）+ `:test`（演示/分类测试 App） |
| 语言 | Java 17 + Kotlin，AndroidX，目标约 API 35 / Gradle 9.4 |
| 通信 | 与用户对话默认用 **简体中文** |

初始化（使用库的 App）：

```kotlin
YUtils.init(application) // 多数工具依赖 Application Context
```

---

## 2. 目录速查

```
utils/src/main/java/com/yujing/
  base/          # Activity/Fragment/Dialog 基类
  bluetooth/     # BLE / BT
  bus/           # 事件总线 YBusUtil
  crypt/         # 加解密
  db/            # SQLite 辅助
  socket/        # TCP/UDP/长连接
  utils/         # 通用工具（日期、文件、线程、权限…）
  view/          # 相机、播放器、弹窗、YAlertDialogUtils…
  adapter/ contract/

test/src/main/java/com/yujing/test/
  ui/            # TestHomeActivity（LAUNCHER）分类测试台
  suite/         # TestCase / Registry / Runner / CoverageGaps
  cases/         # 按领域分组的用例
  activity/      # 旧版按钮墙 MainActivity、BLE Activity 等
```

**启动入口**：`test` 的 LAUNCHER 是 `TestHomeActivity`；右上角「旧版」可进 `MainActivity`。

---

## 3. 怎么新增库功能（`:utils`）

1. **放对包**：按能力放进 `utils/` / `crypt/` / `bus/` / `socket/` 等既有目录，不要新建无必要的顶层包。
2. **风格对齐**：同类文件的命名、静态工具类写法、Kotlin/Java 混用习惯保持一致；类头注释里给简短用法示例（本仓库惯例）。
3. **Context**：需要 Context 的 API 优先 `YApp.get()` 或显式传参；避免在库内硬编码测试包名。
4. **不要破坏主题**：禁止对已创建的 `Activity` 调用 `setTheme(...)`（会污染全局，导致 AppCompat Dialog 失效）。局部主题用 `ContextThemeWrapper`。参考 `YDateDialog`。
5. **FileProvider**：`YUri.getUri` 依赖宿主 App 的 `FileProvider` + `provider_paths.xml`；库侧只约定 authority = `{packageName}.fileProvider`。
6. **改完必测**：能 Auto 断言的写 Auto；必须交互的写 Manual。同步更新 `CoverageGaps.coveredKeywords`（若新增可测能力）。  
7. **同步文档**：若用法/行为有变，更新根目录 `project.md` 对应章节，并更新该类顶部的「用法」注释块。  
8. **最小改动**：不做无关重构、不擅自扩 scope、不主动 commit/push（除非用户明确要求）。

---

## 4. 测试框架怎么工作

| 类型 | 类 | 判定 |
|------|-----|------|
| 自动 | `AutoTestCase` | `block` 内 `require` / 抛异常 → 失败；否则通过 |
| 人工 | `ManualTestCase` | 点击后 `onRun`；自行 `status = Passed/Failed/Skipped` 并 `TestCoverageStore.persist` |

关键文件：

- `suite/TestCase.kt` — 用例模型  
- `suite/TestCategory.kt` — Tab 分类  
- `suite/TestRegistry.kt` — **注册入口**（漏注册 = 界面看不到）  
- `suite/TestRunner.kt` — 串行跑 Auto  
- `suite/TestCoverageStore.kt` — 用 `YSave` 持久化状态  
- `suite/CoverageGaps.kt` — 覆盖说明 + **刻意不测**清单  
- `ui/TestHomeActivity.kt` — 「一键跑全部 / 跑本类 / 清空 / 说明」

断言约定：`require(条件) { "可读原因" }`，失败原因会进日志与 `case.message`。

---

## 5. 怎么新增测试用例

### 5.1 选分类与文件

| 分类 `TestCategory` | 建议写入 |
|---------------------|----------|
| CONVERT | `cases/ConvertCases.kt` |
| DATE | `cases/DateCases.kt` |
| CRYPT | `cases/CryptCases.kt` |
| STORAGE | `cases/StorageCases.kt` |
| THREAD | `cases/ThreadCases.kt` |
| BUS | `cases/BusCases.kt` |
| NETWORK | `cases/SocketCases.kt` |
| OTHER / DB 等 | `cases/OtherCases`（在 `ManualCases.kt`）或 `DbCases.kt` |
| UI / MEDIA / HARDWARE | `UiManualCases` / `MediaManualCases` / `HardwareManualCases`（均在 `ManualCases.kt`） |

新文件时：在 `TestRegistry.all` 的 `buildList { ... }` 里 `addAll(XxxCases.all())`。

### 5.2 Auto 模板

```kotlin
AutoTestCase(
    id = "convert.xxx.roundtrip",   // 全局唯一，建议 domain.api.场景
    title = "YXxx 某能力往返",
    category = TestCategory.CONVERT,
) {
    val out = YXxx.doSomething("in")
    require(out == "expected") { "实际=$out" }
}
```

注意：

- Auto 在 `Dispatchers.Default` 跑，**不要**在 Auto 里弹 Dialog / 起相机。  
- 网络类优先本机回环（`YTcp`/`YUdp`/`YSocketSync`），外网做成 Manual。  
- 临时文件用 `YApp.get().cacheDir`，测完删除。  
- 断言要贴合**真实 API 行为**（含历史命名坑，如 `YCheck.isInteger_POSITIVE` 实际匹配负数）。

### 5.3 Manual 模板

```kotlin
ManualTestCase(
    id = "ui.alert.xxx",
    title = "YAlertDialog 某能力",
    category = TestCategory.UI,
    description = "用户需点确定",
) { activity, c ->
    YAlertDialogUtils().showMessage("标题", "内容") {
        c.status = TestStatus.Passed
        c.message = "用户确认"
        TestCoverageStore.persist(c)
    }
}
```

或复用 `ManualCases.kt` 里的 `markPassed` / `markFailed`。

### 5.4 更新覆盖说明

改完用例后更新 `CoverageGaps.kt`：

- `coveredKeywords`：新增已覆盖能力关键字  
- `intentionallySkipped`：明确「本期不测」的模块（基类、完整页面组件、GPS/USB/装包、root 等）

---

## 6. 怎么编译、安装、跑测

### 6.1 Gradle（本机常见写法）

PowerShell **不支持** `&&` 链；用 `cmd /c "set ...&& ..."` 或 `;` 分隔。

```bat
set GRADLE_USER_HOME=C:\Users\<user>\.gradle
set ANDROID_HOME=C:\Users\<user>\AppData\Local\Android\Sdk
set http_proxy=http://127.0.0.1:7897
set https_proxy=http://127.0.0.1:7897

gradle :test:installDebug --no-daemon ^
  -PmavenCentralUsername=dummy -PmavenCentralPassword=dummy ^
  -Psigning.keyId=dummy -Psigning.password=dummy
```

（若走本机已解压的 Gradle 发行版，路径按机器调整。）

### 6.2 真机 / adb

```bat
adb devices
adb -s <serial> shell am force-stop com.yujing.test
adb -s <serial> shell am start -n com.yujing.test/.ui.TestHomeActivity
```

App 内操作：

1. （可选）点 **清空** — 清用例状态 + 日志  
2. 点 **一键跑全部** — 只跑 Auto  
3. 看顶部「自动用例：x/y 通过」与底部日志（`✓` / `✗`）  
4. Manual 用例按 Tab 逐个点，人工确认  

右上角：**说明** = `CoverageGaps` 弹窗；**旧版** = 旧按钮墙。

### 6.3 验收标准

- 改完相关能力后，至少 `:test:installDebug` 成功。  
- Auto 应用「一键跑全部」目标 **全绿**（失败用例修断言或修库）。  
- 涉及 Dialog / 主题 / FileProvider / BLE 的，补一轮对应 Manual 冒烟。

---

## 7. 已知坑（改库 / 写测前先看）

| 坑 | 说明 |
|----|------|
| `YDateDialog` 曾 `activity.setTheme` | 会污染 Activity，后续 AppCompat `YAlertDialogUtils` 全挂。已改为 `ContextThemeWrapper`。 |
| FileProvider paths | `test/.../res/xml/provider_paths.xml` 需含 `cache-path` / `files-path` 等，否则 `YUri.getUri(cacheFile)` 失败。 |
| `YSound` | SoundPool 异步 load，播放要用 `play`/`put` 回调后再播。 |
| BLE | 先申请 `BLUETOOTH_CONNECT` 等再 `init`；`BleClient` 记得 `setContentView`。 |
| `YString.insert` | 应按「每隔 N 位插入、末尾不多余分隔符」实现。 |
| `YCheck` 正负整数常量名 | `INTEGER_POSITIVE` / `NEGATIVE` 与注释相反，测时以正则真实匹配为准。 |
| Gson `stringIsJson` | 宽松模式可能把未加引号 token 当合法，反例用 `{bad` 这类。 |
| PowerShell | 不要用 bash 式 `&&`；adb/uiautomator 抓 UI 时注意编码。 |
| 勿测清单 | 见 `CoverageGaps.intentionallySkipped`（相机完整页、YGps、装包、root shell 等）。 |

---

## 8. AI 协作约束（本仓库）

- 默认改代码前先读相关源码，再最小 diff 落地。  
- **不要**主动 `git commit` / `push`，除非用户明确说提交或开 PR。  
- **不要** `git add .` 一把梭；排除密钥、本地 gradle 缓存噪声（如无必要不要提交 `gradle-daemon-jvm.properties`）。  
- 用户要 commit 时：先 `status` / `diff` / `log`，再按仓库中文短句风格写 message。  
- 长任务用 todo；真机验证优先于「口头说已修好」。

---

## 9. 快速检查清单（PR / 提交前）

- [ ] `utils` API 行为明确，破坏性变更有说明  
- [ ] 新能力有 Auto 或 Manual 用例，且已挂进 `TestRegistry`  
- [ ] `CoverageGaps` 已更新  
- [ ] `:test:installDebug` 通过  
- [ ] 真机「一键跑全部」Auto 全绿（或已知失败有理由）  
- [ ] 相关 Manual 冒烟过（尤其 Dialog / 权限 / BLE / FileProvider）

---

*维护提示：测试台或注册流程若有结构性变更，请同步改本文第 4–6 节。*
