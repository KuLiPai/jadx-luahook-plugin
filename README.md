# jadx-luahook-plugin
为 Jadx 添加 LuaHook 支持，提供“复制为 LuaHook 模板代码片段”功能

## ✨ 功能简介
本插件为 [Jadx](https://github.com/skylot/jadx) 增加了 LuaHook 相关功能，包括：
- 在 Jadx 反编译界面（CLI／GUI）中新增菜单／右键操作，快速将选定的类／方法生成 LuaHook 模板代码片段。
- 自动填充 LuaHook 常用结构、函数名、包名映射等，减少手动生成模版的工作。
- 借鉴并改造自 [jadx-yuki-plugin](https://github.com/luckyzyx/jadx-yuki-plugin) 的部分代码。特此鸣谢。

## 🎯 安装方式
两种方式任选其一：

### CLI 安装
```bash
jadx plugins --install "github:kulipai/jadx-luahook-plugin"
```
GUI 安装

在 Jadx-GUI 中依次点击：Plugins → Install plugin，输入或选择你的 Jar 文件（Release Artifact）即可。
