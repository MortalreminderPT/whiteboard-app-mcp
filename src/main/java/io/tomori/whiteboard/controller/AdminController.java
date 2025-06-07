

package io.tomori.whiteboard.controller;

import io.tomori.whiteboard.annotations.Command;
import io.tomori.whiteboard.constant.CommandType;
import io.tomori.whiteboard.model.SocketMessage;
import io.tomori.whiteboard.service.AdminService;

/**
 * Controller for admin command.
 * Handle user join and leave events.
 */
public class AdminController {
    private static AdminController instance;
    private final AdminService adminService;

    /**
     * Private constructor for singleton pattern.
     */
    private AdminController() {
        adminService = AdminService.getInstance();
    }

    /**
     * Return the singleton instance of AdminController.
     *
     * @return AdminController instance
     */
    public static synchronized AdminController getInstance() {
        if (instance == null) {
            instance = new AdminController();
        }
        return instance;
    }

    /**
     * Process a user join request.
     *
     * @param update Socket message containing join information
     */
    @Command(CommandType.JOIN_REQUEST)
    public void join(final SocketMessage update) {
        System.out.println("Processing join request...");
        adminService.onUserJoined(update.getUsername());
    }

    /**
     * Process a user leave request.
     *
     * @param update Socket message containing leave information
     */
    @Command(CommandType.LEAVE)
    public void leave(final SocketMessage update) {
        System.out.println("Processing leave request...");
        adminService.onUserLeft(update.getUsername());
    }
}
