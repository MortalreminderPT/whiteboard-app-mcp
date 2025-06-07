

package io.tomori.whiteboard;

import io.tomori.whiteboard.config.ServerConfig;
import io.tomori.whiteboard.controller.AdminController;
import io.tomori.whiteboard.controller.ChatController;
import io.tomori.whiteboard.controller.WhiteboardController;
import io.tomori.whiteboard.core.Registry;
import io.tomori.whiteboard.core.Server;
import io.tomori.whiteboard.gui.AdminGUI;
import io.tomori.whiteboard.service.AdminService;
import io.tomori.whiteboard.service.ChatService;
import io.tomori.whiteboard.service.WhiteboardService;

/**
 * Main class for create and start whiteboard app as admin.
 * Initialize all required services and controllers for whiteboard app.
 */
public class CreateWhiteBoard {
    private final Server networkManager;

    /**
     * Construct a whiteboard server with server config.
     *
     * @param config server config
     */
    public CreateWhiteBoard(final ServerConfig config) {
        networkManager = new Server(config);
        final WhiteboardService whiteboardService = WhiteboardService.initialize(networkManager);
        final AdminService adminService = AdminService.initialize(config.getUsername(), networkManager);
        final ChatService chatService = ChatService.initialize(networkManager);
        Registry.getInstance().registerController(WhiteboardController.getInstance());
        Registry.getInstance().registerController(AdminController.getInstance());
        Registry.getInstance().registerController(ChatController.getInstance());
    }

    /**
     * Application entry point for the whiteboard server.
     *
     * @param args Command line args for server config
     */
    public static void main(final String[] args) {
        final ServerConfig config = new ServerConfig(args);
        final CreateWhiteBoard server = new CreateWhiteBoard(config);
        server.start();
    }

    /**
     * Start the server and initialize the admin GUI.
     */
    public void start() {
        final AdminGUI gui = new AdminGUI(networkManager.getUsername(), this);
        networkManager.start();
    }

    /**
     * Notify all clients about server shutdown and close connections.
     */
    public void notifyClientsOfShutdown() {
        networkManager.close();
    }
}
