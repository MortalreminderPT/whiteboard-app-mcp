

package io.tomori.whiteboard.core;

import com.corundumstudio.socketio.SocketIOClient;
import io.socket.client.Socket;
import io.tomori.whiteboard.util.JsonUtil;
import lombok.Data;

/**
 * Represent a network connection between server and client.
 * Handle both client-side and server-side socket connections.
 */
@Data
public class Connection {
    private final String username;
    private Socket clientSideSocket;
    private SocketIOClient serverSideSocket;
    private boolean connected;

    /**
     * Create a server-side connection with the given socket and username.
     *
     * @param serverSideSocket server-side socket
     * @param username         username of the connected client
     */
    public Connection(final SocketIOClient serverSideSocket, final String username) {
        this.serverSideSocket = serverSideSocket;
        this.username = username;
        connected = true;
    }

    /**
     * Create a client-side connection with the given socket and username.
     *
     * @param clientSideSocket client-side socket
     * @param username         username for this connection
     */
    public Connection(final Socket clientSideSocket, final String username) {
        this.clientSideSocket = clientSideSocket;
        this.username = username;
        connected = true;
    }

    /**
     * Send a message through the appropriate socket.
     *
     * @param message message to send
     */
    public void sendMessage(final Object message) {
        final String json = JsonUtil.toJson(message);
        System.out.println("Sending message: " + json);
        if (serverSideSocket != null) {
            serverSideSocket.sendEvent("whiteboard", json);
        } else if (clientSideSocket != null) {
            clientSideSocket.emit("whiteboard", json);
        }
    }

    /**
     * Close the connection and disconnects the socket.
     */
    public void close() {
        connected = false;
        if (serverSideSocket != null) {
            serverSideSocket.disconnect();
        } else if (clientSideSocket != null) {
            clientSideSocket.disconnect();
        }
    }
}
