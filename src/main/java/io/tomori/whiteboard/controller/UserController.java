

package io.tomori.whiteboard.controller;

import io.tomori.whiteboard.annotations.Command;
import io.tomori.whiteboard.constant.CommandType;
import io.tomori.whiteboard.model.SocketMessage;
import io.tomori.whiteboard.service.UserService;

import java.util.List;

/**
 * Controller for user command.
 * Handle user list updates and connection status events.
 */
public class UserController {
    private static UserController instance;
    private final UserService userService;

    /**
     * Private constructor for singleton pattern.
     */
    private UserController() {
        userService = UserService.getInstance();
    }

    /**
     * Return the singleton instance of UserController.
     *
     * @return UserController instance
     */
    public static synchronized UserController getInstance() {
        if (instance == null) {
            instance = new UserController();
        }
        return instance;
    }

    /**
     * Handle notification that the user has been kicked.
     *
     * @param update Socket message with kick information
     */
    @Command(CommandType.KICKED)
    public static void userKicked(final SocketMessage update) {
        System.out.println("User kicked from whiteboard...");
        UserService.userKicked();
    }

    /**
     * Handle notification of server shutdown.
     *
     * @param update Socket message with shutdown information
     */
    @Command(CommandType.SHUTDOWN)
    public static void serverShutdown(final SocketMessage update) {
        System.out.println("Server shutting down...");
        UserService.serverShutdown();
    }

    /**
     * Update the list of connected users.
     *
     * @param update Socket message containing the user list
     */
    @Command(CommandType.UPDATE_USERS)
    public void fetchUsers(final SocketMessage update) {
        System.out.println("Fetching users...");
        if (update.getData() instanceof List<?>) {
            @SuppressWarnings("unchecked") final List<String> userList = (List<String>) update.getData();
            userService.updateUserList(userList);
        }
    }
}
