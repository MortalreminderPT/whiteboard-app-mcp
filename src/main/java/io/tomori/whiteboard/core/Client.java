

package io.tomori.whiteboard.core;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.tomori.whiteboard.config.ClientConfig;
import io.tomori.whiteboard.constant.CommandType;
import io.tomori.whiteboard.model.SocketMessage;
import io.tomori.whiteboard.service.UserService;
import io.tomori.whiteboard.util.JsonUtil;

import java.net.URISyntaxException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * Client implementation of the NetworkManager interface.
 * Handle socket connection to the whiteboard server and message processing.
 */
public class Client implements NetworkManager {
    private final String serverIP;
    private final int serverPort;
    private final String username;
    private Connection connection;
    private Socket socket;

    /**
     * Create a client with the specified config.
     *
     * @param config client config containing host, port and username
     */
    public Client(final ClientConfig config) {
        serverIP = config.getHost();
        serverPort = config.getPort();
        username = config.getUsername();
    }

    /**
     * Connect to the whiteboard server and processes the join response.
     *
     * @return server's response to the join request, or null if connection failed
     */
    public SocketMessage connect() {
        try {
            final IO.Options options = new IO.Options();
            options.forceNew = true;
            options.reconnection = true;
            socket = IO.socket("http://" + serverIP + ":" + serverPort, options);

            final CountDownLatch connectionLatch = new CountDownLatch(1);
            final boolean[] connectionSuccessful = {false};
            socket.on("connect", args -> {
                System.out.println("Connected to server");
                connectionSuccessful[0] = true;
                connectionLatch.countDown();
            });
            socket.on("connect_error", args -> {
                System.out.println("Connection error: " + args[0]);
                connectionLatch.countDown();
                UserService.serverShutdown();
            });
            socket.connect();
            if (!connectionLatch.await(5, TimeUnit.SECONDS) || !connectionSuccessful[0]) {
                socket.disconnect();
                return null;
            }
            final CountDownLatch joinLatch = new CountDownLatch(1);
            final SocketMessage[] joinResult = {null};
            socket.on("whiteboard", args -> {
                final String jsonData = args[0].toString();
                final SocketMessage update = JsonUtil.fromJson(jsonData, SocketMessage.class);
                if (update.getType() == CommandType.JOIN_ACCEPTED) {
                    joinResult[0] = update;
                    connection = new Connection(socket, username);
                    joinLatch.countDown();
                } else if (update.getType() == CommandType.JOIN_REJECTED) {
                    joinResult[0] = update;
                    joinLatch.countDown();
                } else {
                    handleMessage(update);
                }
            });
            socket.on(Socket.EVENT_DISCONNECT, args -> handleDisconnect());
            sendJoinRequest();
            if (!joinLatch.await(10, TimeUnit.SECONDS)) {
                socket.disconnect();
                return null;
            }
            if (joinResult[0] != null && joinResult[0].getType() == CommandType.JOIN_REJECTED) {
                socket.disconnect();
            }
            return joinResult[0];
        } catch (final URISyntaxException | InterruptedException e) {
            System.out.println("Unable to connect to server: " + e.getMessage());
            if (socket != null) {
                socket.disconnect();
            }
            return null;
        }
    }

    /**
     * Send a join request to the server.
     */
    private void sendJoinRequest() {
        final SocketMessage joinRequest = new SocketMessage(
                username,
                CommandType.JOIN_REQUEST,
                null
        );
        final String json = JsonUtil.toJson(joinRequest);
        System.out.println("Sending join request: " + json);
        socket.emit("join", json);
    }

    /**
     * Process income messages from the server.
     *
     * @param update socket message to process
     */
    private void handleMessage(final SocketMessage update) {
        if (username.equals(update.getUsername())) {
            return;
        }
        Registry.getInstance().process(update);
    }

    /**
     * Handle socket disconnection events.
     */
    private void handleDisconnect() {
        close();
    }

    /**
     * Send an update to the server.
     *
     * @param update message to send
     */
    @Override
    public void sendUpdate(final SocketMessage update) {
        System.out.println("Client Sending update: " + update);
        if (connection != null && connection.isConnected()) {
            connection.sendMessage(update);
        }
    }

    /**
     * Close the connection to the server.
     */
    @Override
    public void close() {
        if (socket == null || !socket.connected()) {
            return;
        }
        if (socket.connected()) {
            final SocketMessage update = new SocketMessage(
                    username,
                    CommandType.LEAVE,
                    null
            );
            connection.sendMessage(update);
        }
        if (socket != null) {
            socket.disconnect();
        }
    }

    /**
     * Return the username of this client.
     *
     * @return username
     */
    @Override
    public String getUsername() {
        return username;
    }
}
