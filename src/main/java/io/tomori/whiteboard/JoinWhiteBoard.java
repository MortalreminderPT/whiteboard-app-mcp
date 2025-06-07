

package io.tomori.whiteboard;

import io.tomori.whiteboard.config.ClientConfig;
import io.tomori.whiteboard.constant.CommandType;
import io.tomori.whiteboard.controller.ChatController;
import io.tomori.whiteboard.controller.UserController;
import io.tomori.whiteboard.controller.WhiteboardController;
import io.tomori.whiteboard.core.Client;
import io.tomori.whiteboard.core.Registry;
import io.tomori.whiteboard.gui.UserGUI;
import io.tomori.whiteboard.model.SocketMessage;
import io.tomori.whiteboard.service.ChatService;
import io.tomori.whiteboard.service.WhiteboardService;

/**
 * Main class for join an existing whiteboard server as a client.
 * Handle connection to the server and initialize client-side component.
 */
public class JoinWhiteBoard {
    private final Client networkManager;

    /**
     * Construct a client with given config.
     *
     * @param config client config
     */
    public JoinWhiteBoard(final ClientConfig config) {
        networkManager = new Client(config);
        final WhiteboardService whiteboardService = WhiteboardService.initialize(networkManager);
        final ChatService chatService = ChatService.initialize(networkManager);
        Registry.getInstance().registerController(WhiteboardController.getInstance());
        Registry.getInstance().registerController(UserController.getInstance());
        Registry.getInstance().registerController(ChatController.getInstance());
    }

    /**
     * Application entry point for the whiteboard client.
     *
     * @param args Command line args for client config
     */
    public static void main(final String[] args) {
        final ClientConfig config = new ClientConfig(args);
        final JoinWhiteBoard client = new JoinWhiteBoard(config);
        client.start();
    }

    /**
     * Start client connection and initialize user GUI.
     */
    public void start() {
        final UserGUI gui = new UserGUI(networkManager.getUsername(), this);
        final SocketMessage result = networkManager.connect();
        if (result != null && result.getType() == CommandType.JOIN_ACCEPTED) {
        } else if (result != null && result.getType() == CommandType.JOIN_REJECTED) {
            UserGUI.showRejectedMessage((String) result.getData());
            System.exit(0);
        } else {
            UserGUI.showConnectionFailedMessage();
        }
    }

    /**
     * Disconnect from the whiteboard server.
     */
    public void leaveWhiteboard() {
        networkManager.close();
    }
}
