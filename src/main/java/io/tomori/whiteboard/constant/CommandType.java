

package io.tomori.whiteboard.constant;

/**
 * Enumeration of message types for socket communication.
 * Define all possible update events between server and clients.
 */
public enum CommandType {
    /**
     * Update shapes list on whiteboard
     */
    UPDATE_SHAPES,
    /**
     * Server is shutting down
     */
    SHUTDOWN,
    /**
     * User has been kicked
     */
    KICKED,
    /**
     * User is leaving whiteboard
     */
    LEAVE,
    /**
     * User is requesting to join whiteboard
     */
    JOIN_REQUEST,
    /**
     * User join request has been accepted
     */
    JOIN_ACCEPTED,
    /**
     * User join request has been rejected
     */
    JOIN_REJECTED,
    /**
     * Chat message from a user
     */
    CHAT_MESSAGE,
    /**
     * Update the list of connected users
     */
    UPDATE_USERS,
    /**
     * Update chat history
     */
    UPDATE_CHAT_HISTORY
}
