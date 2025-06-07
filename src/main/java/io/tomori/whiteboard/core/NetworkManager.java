

package io.tomori.whiteboard.core;

import io.tomori.whiteboard.model.SocketMessage;

/**
 * Interface for network communication components.
 * Define methods for sending updates and managing connections.
 */
public interface NetworkManager {
    /**
     * Send a message to connected clients or server.
     *
     * @param update message to send
     */
    void sendUpdate(SocketMessage update);

    /**
     * Close all network connections.
     */
    void close();

    /**
     * Return the username associated with this network manager.
     *
     * @return username
     */
    String getUsername();
}