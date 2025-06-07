

package io.tomori.whiteboard.gui.panels;

import io.tomori.whiteboard.service.AdminService;

import javax.swing.*;
import java.awt.*;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

/**
 * Panel for displaying online users and managing user operations.
 * Provide admin functionality for managing user connections and displaying join requests.
 */
public class UserPanel extends JPanel {
    /**
     * Singleton instance of the UserPanel
     */
    private static UserPanel instance;
    /**
     * List component for displaying connected users
     */
    private final JList<String> userList;
    /**
     * Data model for the user list
     */
    private final DefaultListModel<String> userListModel;
    /**
     * Flag to prevent multiple shutdown dialogs
     */
    private boolean isShutdownDialogShown = false;

    /**
     * Private constructor for singleton pattern implementation.
     */
    private UserPanel() {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBorder(BorderFactory.createTitledBorder("Online Users"));
        userListModel = new DefaultListModel<>();
        userList = new JList<>(userListModel);
        final JScrollPane userListScrollPane = new JScrollPane(userList);
        userListScrollPane.setPreferredSize(new Dimension(120, 0));
        add(userListScrollPane);
    }

    /**
     * Return the singleton instance of the UserPanel.
     *
     * @return UserPanel instance
     */
    public static synchronized UserPanel getInstance() {
        if (instance == null) {
            instance = new UserPanel();
        }
        return instance;
    }

    /**
     * Display a notification that the user has been kicked and exits the application.
     */
    public static void userKicked() {
        SwingUtilities.invokeLater(() -> {
            JOptionPane.showMessageDialog(null,
                    "You have been removed from the whiteboard by the server admin.",
                    "Connection Closed",
                    JOptionPane.INFORMATION_MESSAGE);
            System.exit(0);
        });
    }

    /**
     * Display a dialog asking the admin to approve or deny a join request.
     *
     * @param clientUsername username of the client requesting to join
     * @return True if the join request is approved, false otherwise
     */
    public static boolean showJoinRequestDialog(final String clientUsername) {
        try {
            final FutureTask<Boolean> task = new FutureTask<>(() -> {
                final String message = clientUsername + " wants to share your whiteboard. Allow joining?";
                final int option = JOptionPane.showConfirmDialog(
                        null,
                        message,
                        "Join Request",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.QUESTION_MESSAGE
                );
                return (option == JOptionPane.YES_OPTION);
            });
            SwingUtilities.invokeAndWait(task);
            return task.get();
        } catch (final InterruptedException | InvocationTargetException | ExecutionException e) {
            System.out.println("Error showing join request dialog: " + e.getMessage());
            return false;
        }
    }

    /**
     * Add a kick button to the panel for admin use.
     */
    public void enableKickButton() {
        final JButton kickButton = new JButton("Kick User");
        kickButton.addActionListener(e -> kickSelectedUser());
        add(kickButton, BorderLayout.NORTH);
    }

    /**
     * Kick the selected user from the whiteboard after confirmation.
     */
    private void kickSelectedUser() {
        final int selectedIndex = userList.getSelectedIndex();
        if (selectedIndex <= 0) {
            JOptionPane.showMessageDialog(null, "Cannot kick yourself (Admin)", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        final String selectedUser = userList.getSelectedValue();
        final int result = JOptionPane.showConfirmDialog(
                null,
                "Are you sure you want to kick user \"" + selectedUser + "\"?",
                "Confirm Kick",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE
        );
        if (result == JOptionPane.YES_OPTION) {
            AdminService.getInstance().kickUser(selectedUser);
        }
    }

    /**
     * Update the list of connected users.
     *
     * @param users list of usernames to display
     */
    public void updateUserList(final List<String> users) {
        SwingUtilities.invokeLater(() -> {
            userListModel.clear();
            if (users != null && !users.isEmpty()) {
                for (final String user : users) {
                    userListModel.addElement(user);
                }
            } else {
                userListModel.addElement("No users");
            }
        });
    }

    /**
     * Display a notification that the server has shut down and exits the application.
     */
    public void serverShutdown() {
        SwingUtilities.invokeLater(() -> {
            if (isShutdownDialogShown) {
                return;
            }
            isShutdownDialogShown = true;
            JOptionPane.showMessageDialog(null,
                    "The server has been closed.",
                    "Connection Closed",
                    JOptionPane.INFORMATION_MESSAGE);
            System.exit(0);
        });
    }
}
