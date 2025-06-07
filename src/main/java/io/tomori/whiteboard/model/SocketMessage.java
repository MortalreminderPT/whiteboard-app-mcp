

package io.tomori.whiteboard.model;

import io.tomori.whiteboard.constant.CommandType;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * Message format for socket communication between clients and server.
 * Contain information about the sender, type of update, and associated data.
 */
@Data
public class SocketMessage implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    private final String username;
    private final CommandType type;
    private final Object data;
    private final long timestamp;

    /**
     * Create new socket message with current timestamp.
     *
     * @param username username of the sender
     * @param type     type of update this message represents
     * @param data     data payload of the message
     */
    public SocketMessage(final String username, final CommandType type, final Object data) {
        this.username = username;
        this.type = type;
        this.data = data;
        timestamp = System.currentTimeMillis();
    }
}
