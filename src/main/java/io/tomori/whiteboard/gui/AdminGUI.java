

package io.tomori.whiteboard.gui;

import io.tomori.whiteboard.CreateWhiteBoard;
import io.tomori.whiteboard.gui.panels.UserPanel;

import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * GUI management class for admin clients.
 * Initialize and configure the whiteboard interface with admin privileges.
 */
public class AdminGUI {
    /**
     * server instance for network operations
     */
    private final CreateWhiteBoard serverInstance;

    /**
     * Create a new AdminGUI for the specified admin user.
     *
     * @param username       username of the admin
     * @param serverInstance server instance handling network operations
     */
    public AdminGUI(final String username, final CreateWhiteBoard serverInstance) {
        this.serverInstance = serverInstance;
        try {
            UIManager.setLookAndFeel("com.formdev.flatlaf.FlatDarculaLaf");
        } catch (final Exception e) {
            System.out.println("Failed to set look and feel: " + e.getMessage());
        }
        initializeWhiteboard(username);
    }

    /**
     * Initialize the whiteboard interface with admin privileges.
     *
     * @param username username to display in the window title
     */
    private void initializeWhiteboard(final String username) {
        final WhiteboardFrame whiteboardFrame = WhiteboardFrame.getInstance();
        whiteboardFrame.setTitle("Whiteboard Application (Admin: " + username + ")");
        whiteboardFrame.enableFileMenu();
        UserPanel.getInstance().enableKickButton();
        whiteboardFrame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(final WindowEvent e) {
                serverInstance.notifyClientsOfShutdown();
                System.exit(0);
            }
        });
        whiteboardFrame.setVisible(true);
    }
}
