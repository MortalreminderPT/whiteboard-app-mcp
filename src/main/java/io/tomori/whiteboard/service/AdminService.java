

package io.tomori.whiteboard.service;

import io.tomori.whiteboard.constant.CommandType;
import io.tomori.whiteboard.core.Server;
import io.tomori.whiteboard.gui.panels.UserPanel;
import io.tomori.whiteboard.model.SocketMessage;
import io.tomori.whiteboard.model.shapes.Shape;

import java.util.List;

/**
 * Service for managing admin operations.
 * Handle user join/leave events and server admin tasks.
 */
public class AdminService {
    private static AdminService instance;
    private final String adminUsername;
    private final Server server;

    /**
     * Create admin service with the specified username and server.
     *
     * @param adminUsername admin username
     * @param server        server instance
     */
    private AdminService(final String adminUsername, final Server server) {
        this.adminUsername = adminUsername;
        this.server = server;
    }

    /**
     * Return the singleton instance of AdminService.
     *
     * @return AdminService instance
     * @throws IllegalStateException if the service is not initialized
     */
    public static synchronized AdminService getInstance() {
        if (instance == null) {
            throw new IllegalStateException("UserService not initialized. Call initialize() first.");
        }
        return instance;
    }

    /**
     * Initialize AdminService singleton.
     *
     * @param adminUsername  admin username
     * @param networkManager server instance
     * @return initialized AdminService instance
     */
    public static AdminService initialize(final String adminUsername, final Server networkManager) {
        if (instance == null) {
            instance = new AdminService(adminUsername, networkManager);
        }
        return instance;
    }

    /**
     * Process a join request by display a dialog to admin.
     *
     * @param username username requesting to join
     * @return true if the join request is accepted
     */
    public static boolean processJoinRequest(final String username) {
        return UserPanel.showJoinRequestDialog(username);
    }

    /**
     * Handle a user joining whiteboard by sending current state.
     *
     * @param username username of the joined user
     */
    public void onUserJoined(final String username) {
        final List<Shape> currentShapes = WhiteboardService.getInstance().getShapes();
        final SocketMessage whiteboardUpdate = new SocketMessage(
                adminUsername,
                CommandType.UPDATE_SHAPES,
                currentShapes
        );
        server.sendUpdate(whiteboardUpdate);
        final SocketMessage chatUpdate = new SocketMessage(
                adminUsername,
                CommandType.UPDATE_CHAT_HISTORY,
                ChatService.getInstance().getChatHistory()
        );
        server.sendUpdate(chatUpdate);
    }

    /**
     * Handle a user leaving whiteboard.
     *
     * @param username username of the user who left
     */
    public void onUserLeft(final String username) {
        server.removeUser(username);
    }

    /**
     * Kick a user from whiteboard.
     *
     * @param username username of the user to kick
     */
    public void kickUser(final String username) {
        server.kickUser(username);
    }
}
