

package io.tomori.whiteboard.service;

import io.tomori.whiteboard.gui.panels.UserPanel;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

/**
 * Service for managing user operations.
 * Handle user list update and connection status notification.
 */
@Getter
public class UserService {
    private static UserService instance;
    /**
     * connected users list
     */
    private List<String> userList;

    /**
     * Private constructor for singleton pattern.
     */
    private UserService() {
    }

    /**
     * Return UserService singleton instance.
     *
     * @return UserService instance
     */
    public static synchronized UserService getInstance() {
        if (instance == null) {
            instance = new UserService();
        }
        return instance;
    }

    /**
     * Notify user panel that user has been kicked.
     */
    public static void userKicked() {
        UserPanel.userKicked();
    }

    /**
     * Notify user panel that the server has shut down.
     */
    public static void serverShutdown() {
        UserPanel.getInstance().serverShutdown();
    }

    /**
     * Update the list of connected users and refreshes the UI.
     *
     * @param userList updated user list
     */
    public void updateUserList(final List<String> userList) {
        this.userList = new ArrayList<>(userList);
        UserPanel.getInstance().updateUserList(this.userList);
    }
}
