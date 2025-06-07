# Whiteboard App with MCP Integration

This is a simple whiteboard app that allows multiple users to draw on a shared drawing real time.

It supports features like drawing, erasing, and saving the whiteboard state.

It uses Java for the server and client app.

MCP is integrated to allow llm assistants to enhance the whiteboard app.

## Requirements

- Java 22 (on GraalVM) or higher
- Claude Desktop (optional, for MCP integration)

```bash
java --version
java 22.0.1 2024-04-16
Java(TM) SE Runtime Environment Oracle GraalVM 22.0.1+8.1 (build 22.0.1+8-jvmci-b01)
Java HotSpot(TM) 64-Bit Server VM Oracle GraalVM 22.0.1+8.1 (build 22.0.1+8-jvmci-b01, mixed mode, sharing)
```

## Usage

Start the server:

```bash
java -jar CreateWhiteBoard.jar <ServerIP> <ServerPort> <username> <MCPServerPort>
```

Start client(s):

```bash
java -jar JoinWhiteBoard.jar <ServerIP> <ServerPort> <username>
```

### MCP Configuration(Optional)

Download Claude Desktop from [here](https://claude.ai/download).

Use `./claude_desktop_config.json` replace default mcp config.

Start Claude Desktop from desktop.