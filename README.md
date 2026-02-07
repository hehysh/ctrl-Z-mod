# Ctrl+Z Mod (Slay the Spire)

战斗内快捷键撤回/重做 Mod：
- `Ctrl + Z`：撤回最近一次玩家操作（单步）
- `Ctrl + Y`：重做刚才撤回的那一步（单步）

## 功能范围（当前版本）
- 仅战斗内生效
- 覆盖打牌、用药、结束回合前状态捕获
- 采用单步快照（最简实现，兼容优先）

## v1.0.0 安全逻辑（防坏档）
- 仅操作**战斗内内存态**，不主动写入本地存档/云存档文件。
- 每场战斗有独立会话 token，快照不可跨战斗复用。
- 快照仅允许使用 SaveState 的工厂接口创建；不再走高风险构造兜底。
- 任一步骤异常会触发“本场禁用撤回/重做”（严格安全优先），战斗结束自动恢复。
- 撤回仅在安全窗口触发（战斗内且可操作阶段），降低状态机中途回滚风险。

## 依赖
请将以下 JAR 放入项目根目录的 `lib/`：
- Slay the Spire 游戏本体 JAR
- ModTheSpire
- BaseMod
- （可选但推荐）SaveStateMod 或提供 `savestate.SaveState` 的兼容实现

建议命名示例：
- `desktop-1.0.jar`
- `ModTheSpire*.jar`
- `BaseMod*.jar`

> 若缺少 `savestate.SaveState`，Mod 会安全降级为“不执行撤回/重做”，不会导致崩溃。

> 说明：仓库内包含最小 **compile stubs**（仅用于 CI 编译通过）。这些 stubs 不会被打进最终 jar，也不会在游戏中替代真实 API。

## 构建
构建机要求：
- Gradle 9.x 运行需要 JDK 17+
- 本项目编译目标仍为 Java 8（`release = 8`）

在项目根目录执行：

```bash
./gradlew jar
```

如果报大量 `package ... does not exist`，通常不是源码问题，而是 `lib/` 里缺依赖 JAR。

产物位于：
- `build/libs/CtrlZMod-1.0.0.jar`

## 兼容性说明
- 目标：原版 + 主流单机 Mod 组合下尽量兼容
- 明确不保证：联机/多人同步类 Mod 场景
- 由于是便捷优先设计，理论上允许“看到结果后撤回”

## 用户安全自检步骤（建议）
1. 先在非关键存档开一场战斗，执行一次 `Ctrl+Z` / `Ctrl+Y`。
2. 结束战斗后切到其他角色或其他存档槽，确认状态正常。
3. 重启游戏后读取同一存档，确认可正常继续。
4. 若日志出现“disabled for current combat”，表示本场已自动熔断保护。

## 不兼容/不保证清单
- 联机/多人同步类 Mod。
- 依赖外部状态强耦合、且不满足可回滚前提的高改动 Mod。
- 战斗外流程（地图/商店/事件/奖励）不提供撤回。

## 版本一致性说明
- 本项目使用**单一版本源**（`gradle.properties` 的 `modVersion`）。
- CI 会校验：Release tag、jar 文件名、构建后的 `ModTheSpire.json` 版本三者一致。

## CI 发布说明
- 每次 push 到 `main` 会触发自动构建与发布。
- 不需要配置任何仓库变量/密钥。
- CI 通过内置 compile stubs 完成编译并发布 jar。
