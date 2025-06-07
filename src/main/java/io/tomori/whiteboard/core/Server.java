

package io.tomori.whiteboard.core;

import com.corundumstudio.socketio.Configuration;
import com.corundumstudio.socketio.SocketIOServer;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.modelcontextprotocol.server.McpAsyncServer;
import io.modelcontextprotocol.server.transport.WebFluxSseServerTransportProvider;
import io.tomori.whiteboard.config.ServerConfig;
import io.tomori.whiteboard.constant.CommandType;
import io.tomori.whiteboard.mcp.McpTools;
import io.tomori.whiteboard.model.SocketMessage;
import io.tomori.whiteboard.service.AdminService;
import io.tomori.whiteboard.service.UserService;
import io.tomori.whiteboard.util.JsonUtil;
import org.springframework.http.server.reactive.HttpHandler;
import org.springframework.http.server.reactive.ReactorHttpHandlerAdapter;
import org.springframework.web.reactive.function.server.RouterFunctions;
import reactor.netty.DisposableServer;
import reactor.netty.http.server.HttpServer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Server implementation of the NetworkManager interface.
 * Manage SocketIO(For clients) and MCP(For large language model clients) servers for whiteboard communication and handles client connections.
 */
public class Server implements NetworkManager {
    private static final String CUSTOM_SSE_ENDPOINT = "/sse";
    private static final String CUSTOM_MESSAGE_ENDPOINT = "/whiteboard";
    private final String username;
    private final String host;
    private final int port;
    private final int mcpPort;
    private final List<Connection> clients;
    private final ExecutorService executorService;
    private McpAsyncServer mcpServer;
    private SocketIOServer socketIoServer;
    private boolean running = false;

    /**
     * Create a server with the specified config.
     *
     * @param config server config containing host, ports and admin username
     */
    public Server(final ServerConfig config) {
        username = config.getUsername();
        host = config.getHost();
        port = config.getPort();
        mcpPort = config.getMcpPort();
        clients = new CopyOnWriteArrayList<>();
        executorService = Executors.newCachedThreadPool();
    }

    /**
     * Start both the SocketIO and MCP servers.
     */
    public void start() {
        if (running) {
            return;
        }
        startSocketIoServer();
        startMcpServer();
    }

    /**
     * Start the MCP server for large language model client support.
     */
    private void startMcpServer() {
        try {
            final WebFluxSseServerTransportProvider transportProvider = new WebFluxSseServerTransportProvider.Builder()
                    .objectMapper(new ObjectMapper())
                    .messageEndpoint(CUSTOM_MESSAGE_ENDPOINT)
                    .sseEndpoint(CUSTOM_SSE_ENDPOINT)
                    .build();
            final HttpHandler httpHandler = RouterFunctions.toHttpHandler(transportProvider.getRouterFunction());
            final ReactorHttpHandlerAdapter adapter = new ReactorHttpHandlerAdapter(httpHandler);
            final DisposableServer httpServer = HttpServer.create().host(host).port(mcpPort).handle(adapter).bindNow();
            mcpServer = io.modelcontextprotocol.server.McpServer.async(transportProvider)
                    .serverInfo("whiteboard-app", "0.0.1-dev")
                    .tools(McpTools.getInstance().listMcpTools())
                    .build();
            System.out.println("MCP Server started successfully, listening on port: " + mcpPort);
        } catch (final Exception e) {
            System.out.println("Unable to start MCP server: " + e.getMessage());
        }
    }

    /**
     * Start the SocketIO server and set up event listeners.
     */
    private void startSocketIoServer() {
        try {
            final Configuration config = new Configuration();
            config.setHostname(host);
            config.setPort(port);
            // maximum shape size
            config.setMaxHttpContentLength(1024 * 1024 * 10);
            config.setMaxFramePayloadLength(1024 * 1024 * 10);
            socketIoServer = new SocketIOServer(config);
            socketIoServer.addConnectListener(client -> System.out.println("Client connected: " + client.getSessionId()));
            socketIoServer.addEventListener("join", String.class, (client, data, ackRequest) -> {
                final SocketMessage update = JsonUtil.fromJson(data, SocketMessage.class);
                if (update == null) {
                    System.out.println("Received null update");
                    return;
                }
                final Connection pendingClient = new Connection(client, update.getUsername());
                if (clients.stream().anyMatch(c -> c.getUsername().equals(update.getUsername()))) {
                    final SocketMessage response = new SocketMessage(
                            username,
                            CommandType.JOIN_REJECTED,
                            "Repeated username"
                    );
                    pendingClient.sendMessage(response);
                    pendingClient.close();
                    return;
                }
                final boolean approved = AdminService.processJoinRequest(update.getUsername());
                processJoinRequest(pendingClient, approved);
            });
            socketIoServer.addEventListener("whiteboard", String.class, (client, data, ackRequest) -> {
                final SocketMessage update = JsonUtil.fromJson(data, SocketMessage.class);
                if (update != null && !username.equals(update.getUsername())) {
                    Registry.getInstance().process(update);
                    sendUpdate(update);
                }
            });
            socketIoServer.addDisconnectListener(client -> handleClientDisconnect(getConnectionBySocketId(client.getSessionId())));
            socketIoServer.start();
            running = true;
            System.out.println("Socket.IO Server started successfully, listening on port: " + port);
            broadcastUserListUpdate();
        } catch (final Exception e) {
            System.out.println("Unable to start server: " + e.getMessage());
        }
    }

    /**
     * Find a connection by its socket session ID.
     *
     * @param sessionId socket session ID to look for
     * @return matching connection or null if not found
     */
    private Connection getConnectionBySocketId(final Object sessionId) {
        for (final Connection conn : clients) {
            if (conn.getServerSideSocket() != null && conn.getServerSideSocket().getSessionId().equals(sessionId)) {
                return conn;
            }
        }
        return null;
    }

    /**
     * Return usernames of all connected users including the admin.
     *
     * @return List of all usernames with admin marked
     */
    public List<String> getAllUsernames() {
        final List<String> usernames = new ArrayList<>();
        usernames.add(username + " (Admin)");
        for (final Connection client : clients) {
            usernames.add(client.getUsername());
        }
        return usernames;
    }

    /**
     * Broadcast the updated user list to all clients.
     */
    private void broadcastUserListUpdate() {
        final List<String> usernames = getAllUsernames();
        final SocketMessage update = new SocketMessage(
                username,
                CommandType.UPDATE_USERS,
                usernames
        );
        UserService.getInstance().updateUserList(usernames);
        sendUpdate(update);
    }

    /**
     * Process a join request and notifies the client of the result.
     *
     * @param pendingClient client connection requesting to join
     * @param accepted      Whether the join request was accepted
     */
    private void processJoinRequest(final Connection pendingClient, final boolean accepted) throws IOException {
        if (accepted) {
            final SocketMessage response = new SocketMessage(
                    username,
                    CommandType.JOIN_ACCEPTED,
                    null
            );
            pendingClient.sendMessage(response);
            clients.add(pendingClient);
            AdminService.getInstance().onUserJoined(pendingClient.getUsername());
            broadcastUserListUpdate();
        } else {
            final SocketMessage response = new SocketMessage(
                    username,
                    CommandType.JOIN_REJECTED,
                    "Rejected by Admin"
            );
            pendingClient.sendMessage(response);
            pendingClient.close();
        }
    }

    /**
     * Kick a user from the whiteboard.
     *
     * @param username username of the user to kick
     */
    public void kickUser(final String username) {
        Connection clientToRemove = null;
        for (final Connection client : clients) {
            if (client.getUsername().equals(username)) {
                clientToRemove = client;
                break;
            }
        }
        if (clientToRemove != null) {
            final SocketMessage update = new SocketMessage(
                    this.username,
                    CommandType.KICKED,
                    null
            );
            clientToRemove.sendMessage(update);
            clientToRemove.close();
            clients.remove(clientToRemove);
            AdminService.getInstance().onUserLeft(username);
            broadcastUserListUpdate();
        }
    }

    /**
     * Remove a user from the whiteboard.
     *
     * @param username username of the user to remove
     */
    public void removeUser(final String username) {
        Connection clientToRemove = null;
        for (final Connection client : clients) {
            if (client.getUsername().equals(username)) {
                clientToRemove = client;
                break;
            }
        }
        if (clientToRemove != null) {
            clients.remove(clientToRemove);
            broadcastUserListUpdate();
        }
    }

    /**
     * Return the admin username.
     *
     * @return admin username
     */
    @Override
    public String getUsername() {
        return username;
    }

    /**
     * Send a message to all connected clients.
     *
     * @param update message to broadcast
     */
    @Override
    public void sendUpdate(final SocketMessage update) {
        final String json = JsonUtil.toJson(update);
        System.out.println("Broadcasting update: " + json);
        socketIoServer.getBroadcastOperations().sendEvent("whiteboard", json);
    }

    /**
     * Notify all clients of server shutdown.
     */
    private void notifyClientsOfShutdown() {
        final SocketMessage update = new SocketMessage(
                username,
                CommandType.SHUTDOWN,
                null
        );
        sendUpdate(update);
    }

    /**
     * Handle client disconnection.
     *
     * @param client client connection to handle
     */
    private void handleClientDisconnect(final Connection client) {
        if (client == null) {
            return;
        }
        final String username = client.getUsername();
        clients.remove(client);
        client.close();
        AdminService.getInstance().onUserLeft(username);
        broadcastUserListUpdate();
    }

    /**
     * Close all connections and shuts down the servers.
     */
    @Override
    public void close() {
        if (!running) {
            return;
        }
        running = false;
        notifyClientsOfShutdown();
        for (final Connection client : clients) {
            client.close();
        }
        clients.clear();
        if (socketIoServer != null) {
            socketIoServer.stop();
        }
        executorService.shutdown();
    }
} 
