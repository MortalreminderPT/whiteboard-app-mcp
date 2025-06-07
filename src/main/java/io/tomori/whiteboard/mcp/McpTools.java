

package io.tomori.whiteboard.mcp;

import com.google.gson.internal.LinkedTreeMap;
import io.modelcontextprotocol.server.McpServerFeatures;
import io.modelcontextprotocol.spec.McpSchema;
import io.tomori.whiteboard.model.shapes.*;
import io.tomori.whiteboard.service.AdminService;
import io.tomori.whiteboard.service.ChatService;
import io.tomori.whiteboard.service.UserService;
import io.tomori.whiteboard.service.WhiteboardService;
import io.tomori.whiteboard.util.JsonUtil;
import io.tomori.whiteboard.util.SvgUtil;
import reactor.core.publisher.Mono;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Tool provider for Model Context Protocol (MCP) integration with whiteboard app.
 * Implement various tool specifications for MCP client to interact with whiteboard app.
 */
public class McpTools {
    /**
     * Empty JSON schema template for tools without parameters
     */
    public static String EMPTY_JSON_SCHEMA = """
            {
            "$schema": "http://json-schema.org/draft-07/schema#",
            "type": "object",
            "properties": {}
            }
            """;
    /**
     * Singleton instance of the McpTools
     */
    private static McpTools instance;
    /**
     * List of available MCP tools
     */
    List<McpServerFeatures.AsyncToolSpecification> tools = new ArrayList<>();

    /**
     * Private constructor initializing all MCP tool specifications.
     */
    private McpTools() {
        final McpServerFeatures.AsyncToolSpecification previewCanvas = new McpServerFeatures.AsyncToolSpecification(
                new McpSchema.Tool("PreviewCanvas", "Preview the current canvas", EMPTY_JSON_SCHEMA),
                (_, _) -> {
                    final LinkedTreeMap<String, Object> content = new LinkedTreeMap<>();
                    content.put("data", WhiteboardService.previewAsBase64());
                    content.put("type", "png");
                    content.put("mimeType", "image/png");
                    final McpSchema.ImageContent imageContent = JsonUtil.fromJson(
                            JsonUtil.toJson(content),
                            McpSchema.ImageContent.class
                    );
                    return Mono.just(new McpSchema.CallToolResult(List.of(imageContent), false));
                }
        );
        tools.add(previewCanvas);
        final McpServerFeatures.AsyncToolSpecification listShapes = new McpServerFeatures.AsyncToolSpecification(
                new McpSchema.Tool("ListShapes", "List all shapes on canvas", EMPTY_JSON_SCHEMA),
                (_, _) -> Mono.just(new McpSchema.CallToolResult(JsonUtil.toJson(WhiteboardService.getInstance().getShapes()), false))
        );
        tools.add(listShapes);
        final McpServerFeatures.AsyncToolSpecification listUsers = new McpServerFeatures.AsyncToolSpecification(
                new McpSchema.Tool("ListOnlineUsers", "List all users online", EMPTY_JSON_SCHEMA),
                (_, _) -> Mono.just(new McpSchema.CallToolResult(JsonUtil.toJson(UserService.getInstance().getUserList()), false))
        );
        tools.add(listUsers);
        final McpServerFeatures.AsyncToolSpecification listChatHistory = new McpServerFeatures.AsyncToolSpecification(
                new McpSchema.Tool("ListChatHistory", "List chat history", EMPTY_JSON_SCHEMA),
                (_, _) -> Mono.just(new McpSchema.CallToolResult(JsonUtil.toJson(ChatService.getInstance().getChatHistory()), false))
        );
        tools.add(listChatHistory);
        final McpServerFeatures.AsyncToolSpecification kickUser = new McpServerFeatures.AsyncToolSpecification(
                new McpSchema.Tool("KickUser", "Kick user from whiteboard", """
                        {
                        "$schema": "http://json-schema.org/draft-07/schema#",
                        "type": "object",
                        "properties": {
                            "username": {
                                "type": "string"
                            }
                        },
                        "required": ["username"]
                        }
                        """),
                (_, args) -> {
                    final String username = (String) args.get("username");
                    AdminService.getInstance().kickUser(username);
                    return Mono.just(new McpSchema.CallToolResult("User kicked", false));
                }
        );
        tools.add(kickUser);
        final String svgTemplate = SvgUtil.toSvg(List.of(
                new CircleShape(0, 0, 10),
                new LineShape(0, 0, 10, 10),
                new OvalShape(0, 0, 10, 20),
                new PathShape(List.of(new Point(0, 0), new Point(10, 10), new Point(20, 20))),
                new RectangleShape(0, 0, 10, 20),
                new TextShape(0, 0, "Hello World"),
                new TriangleShape(new Point(0, 0), new Point(10, 10), new Point(20, 20))
        ));
        final McpServerFeatures.AsyncToolSpecification addShapes = new McpServerFeatures.AsyncToolSpecification(
                new McpSchema.Tool("AddShapes", "Add shapes from svg format on whiteboard. " +
                        "For a certain shape I listed, except for the id and various numbers which can be modified, " +
                        "you must provide the svg in the format I listed; otherwise, an error will be reported", """
                        {
                        "$schema": "http://json-schema.org/draft-07/schema#",
                        "type": "object",
                        "properties": {
                            "svgString": {
                                "type": "string",
                                "contentMediaType": "text/xml",
                                "default": %s
                            }
                        },
                        "required": ["svgString"]
                        }
                        """.formatted(JsonUtil.toJson(svgTemplate))),
                (_, args) -> {
                    final String svgString = (String) args.get("svgString");
                    WhiteboardService.getInstance().addShapesSvg(svgString);
                    return Mono.just(new McpSchema.CallToolResult("Shapes added from SVG", false));
                }
        );
        tools.add(addShapes);
        final McpServerFeatures.AsyncToolSpecification removeShapes = new McpServerFeatures.AsyncToolSpecification(
                new McpSchema.Tool("RemoveShapes", "Remove shapes by shapeIds on whiteboard", """
                        {
                        "$schema": "http://json-schema.org/draft-07/schema#",
                        "type": "object",
                        "properties": {
                            "shapeIds": {
                                 "type": "array",
                                 "items": { "type": "string" },
                                 "uniqueItems": true,
                                 "default": []
                             }
                        },
                        "required": ["shapeIds"]
                        }
                        """),
                (_, args) -> {
                    final List<String> shapeIds = (List<String>) args.get("shapeIds");
                    WhiteboardService.getInstance().removeShapes(shapeIds);
                    return Mono.just(new McpSchema.CallToolResult("Shapes " + shapeIds + " Removed", false));
                }
        );
        tools.add(removeShapes);
    }

    /**
     * Return singleton instance of the McpTools.
     *
     * @return McpTools instance
     */
    public static synchronized McpTools getInstance() {
        if (instance == null) {
            instance = new McpTools();
        }
        return instance;
    }

    /**
     * Return MCP tool specifications list.
     *
     * @return tool specifications list
     */
    public List<McpServerFeatures.AsyncToolSpecification> listMcpTools() {
        return tools;
    }
}
