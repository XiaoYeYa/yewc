# 夜的结构生成器 (YEtools - Ye's Structure Generator)

一个 Minecraft Fabric 1.21.5 模组，基于 MaLiLib，允许玩家在任意位置设置结构类型并生成对应的建筑。

### 依赖
- Minecraft 1.21.5
- Fabric Loader 0.16.14+
- Fabric API
- MaLiLib（可选，用于 GUI 和热键）

### 安装方式
1. 下载 `yewc-1.0.0.jar`
2. 放入 `mods` 文件夹
3. （可选）安装 MaLiLib 以启用 GUI 和热键功能

## 使用方法

### 命令
| 命令 | 说明 |
|------|------|
| `/setstructure <structure>` | 在当前位置设置结构 |
| `/setstructure clear` | 清除当前区块的结构 |
| `/setstructure list` | 列出当前区块的结构 |
`/setstructure minecraft:swamp_hut`     # 生成女巫小屋


### 热键（需要 MaLiLib）
| 热键 | 功能 |
|------|------|
| `Y + C` | 打开配置界面 |
| `Y + S` | 设置结构 |
| `Y + X` | 清除结构 |
| `Y + L` | 列出结构 |

## 服务端/客户端兼容性

| 环境 | 功能 |
|------|------|
| 服务端 | `/setstructure` 命令 |
| 客户端（无 MaLiLib） | `/setstructure` 命令 |
| 客户端（有 MaLiLib） | 命令 + GUI + 热键 |

## 开源协议

MIT License

