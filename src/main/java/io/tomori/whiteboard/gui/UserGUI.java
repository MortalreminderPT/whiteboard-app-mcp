

package io.tomori.whiteboard.gui;

import io.tomori.whiteboard.JoinWhiteBoard;

import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * GUI management class for regular user clients.
 * Initialize and configure the whiteboard interface for non-admin users.
 */
public class UserGUI {
    /**
     * Reference to the client instance for network operations
     */
    private final JoinWhiteBoard clientInstance;

    /**
     * Create a new UserGUI for the specified user.
     *
     * @param username       username of the current user
     * @param clientInstance client instance handling network operations
     */
    public UserGUI(final String username, final JoinWhiteBoard clientInstance) {
        this.clientInstance = clientInstance;
        try {
            UIManager.setLookAndFeel("com.formdev.flatlaf.FlatDarculaLaf");
        } catch (final Exception e) {
            System.out.println("Failed to set look and feel: " + e.getMessage());
        }
        initializeWhiteboard(username);
    }

    /**
     * Display a message when a connection request is rejected.
     *
     * @param reason reason for rejection
     */
    public static void showRejectedMessage(final String reason) {
        JOptionPane.showMessageDialog(
                null,
                reason,
                "Connect Rejected",
                JOptionPane.WARNING_MESSAGE
        );
    }

    /**
     * Display a message when the connection to the server fails.
     */
    public static void showConnectionFailedMessage() {
        JOptionPane.showMessageDialog(
                null,
                "Unable to connect to the server. Please check your network connection and server status.",
                "Connection Failed",
                JOptionPane.ERROR_MESSAGE
        );
        System.exit(0);
    }

    /**
     * Initialize the whiteboard interface for the user.
     *
     * @param username username to display in the window title
     */
    private void initializeWhiteboard(final String username) {
        final WhiteboardFrame whiteboardFrame = WhiteboardFrame.getInstance();
        whiteboardFrame.setTitle("Whiteboard Application (User: " + username + ")");
        whiteboardFrame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(final WindowEvent e) {
                clientInstance.leaveWhiteboard();
                System.exit(0);
            }
        });
        whiteboardFrame.setVisible(true);
    }
}
