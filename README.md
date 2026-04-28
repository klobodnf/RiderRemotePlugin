# Rider Remote Plugin

A REST API plugin for JetBrains Rider that allows external tools (like Claude Code CLI) to control the IDE via HTTP endpoints.

## Features

| Endpoint | Method | Description |
|----------|--------|-------------|
| `/api/status` | GET | Get current project status |
| `/api/build` | POST | Trigger build (auto-detects available build actions) |
| `/api/run` | POST | Trigger run/debug configuration |
| `/api/diagnostics` | GET | Get compilation diagnostics |
| `/api/stop` | POST | Stop current task |

## Installation

1. Download the latest release `.zip` from [Releases](../../releases)
2. In Rider: `File` → `Settings` → `Plugins` → ⚙️ → `Install Plugin from Disk...`
3. Select the downloaded `.zip` file
4. Restart Rider

The HTTP server starts automatically on port **9878** when a project is opened.

## Usage

### Check Status
```bash
curl http://localhost:9878/api/status
```

### Trigger Build
```bash
curl -X POST http://localhost:9878/api/build
```

### Trigger Debug (Shift+F9)
```bash
curl -X POST http://localhost:9878/api/run \
  -H "Content-Type: application/json" \
  -d '{"mode":"debug"}'
```

### Trigger Run
```bash
curl -X POST http://localhost:9878/api/run \
  -H "Content-Type: application/json" \
  -d '{"mode":"run"}'
```

## Building from Source

```bash
./gradlew buildPlugin
```

The plugin distribution will be available at:
```
build/distributions/RiderRemotePlugin-x.x.x.zip
```

## Requirements

- JetBrains Rider 2025.2+
- Java 17+

## Why This Plugin?

JetBrains' official MCP Server has a known bug where Claude Code CLI connects but registers zero tools ([anthropics/claude-code#41418](https://github.com/anthropics/claude-code/issues/41418)). This plugin provides a stable REST API alternative.

---

## 中文说明

### 功能

通过 HTTP REST API 让外部工具控制 Rider IDE，支持编译、运行、调试和获取诊断信息。

### 安装

1. 下载最新版本的 `.zip` 文件
2. Rider 中：`文件` → `设置` → `插件` → ⚙️ → `从磁盘安装插件...`
3. 选择下载的 `.zip` 文件
4. 重启 Rider

打开项目后，HTTP 服务器会自动在 **9878** 端口启动。

### 使用示例

**查看状态：**
```bash
curl http://localhost:9878/api/status
```

**触发编译：**
```bash
curl -X POST http://localhost:9878/api/build
```

**触发调试（Shift+F9）：**
```bash
curl -X POST http://localhost:9878/api/run \
  -H "Content-Type: application/json" \
  -d '{"mode":"debug"}'
```

### 从源码构建

```bash
./gradlew buildPlugin
```

插件包将生成在：
```
build/distributions/RiderRemotePlugin-x.x.x.zip
```
