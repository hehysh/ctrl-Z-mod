# Ctrl+Z Mod (Slay the Spire)

战斗内快捷键撤回/重做 Mod：
- `Ctrl + Z`：撤回最近一次玩家操作（单步）
- `Ctrl + Y`：重做刚才撤回的那一步（单步）

## 功能范围（当前版本）
- 仅战斗内生效
- 覆盖打牌、用药、结束回合前状态捕获
- 采用单步快照（最简实现，兼容优先）

## 依赖
请将以下 JAR 放入项目根目录的 `lib/`：
- Slay the Spire 游戏本体 JAR
- ModTheSpire
- BaseMod
- （可选但推荐）SaveStateMod 或提供 `savestate.SaveState` 的兼容实现

> 若缺少 `savestate.SaveState`，Mod 会安全降级为“不执行撤回/重做”，不会导致崩溃。

## 构建
在项目根目录执行：

```bash
./gradlew jar
```

产物位于：
- `build/libs/CtrlZMod-0.2.0.jar`

## 兼容性说明
- 目标：原版 + 主流单机 Mod 组合下尽量兼容
- 明确不保证：联机/多人同步类 Mod 场景
- 由于是便捷优先设计，理论上允许“看到结果后撤回”
