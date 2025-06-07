

package io.tomori.whiteboard.gui.panels;

import io.tomori.whiteboard.model.ChatMessage;
import io.tomori.whiteboard.service.ChatService;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.List;

/**
 * Panel for displaying and sending chat messages.
 * Provide a text area for message history and a text field for composing new messages.
 */
public class ChatPanel extends JPanel {
    /**
     * Singleton instance of the ChatPanel
     */
    private static ChatPanel instance;
    /**
     * Text area for displaying chat messages
     */
    private final JTextArea chatArea;
    /**
     * Text field for composing new messages
     */
    private final JTextField messageField;

    /**
     * Private constructor for singleton pattern implementation.
     */
    private ChatPanel() {
        setLayout(new BorderLayout(5, 5));
        setBorder(BorderFactory.createTitledBorder("Chat History"));
        chatArea = new JTextArea();
        chatArea.setEditable(false);
        chatArea.setLineWrap(true);
        chatArea.setWrapStyleWord(true);
        final JScrollPane chatScrollPane = new JScrollPane(chatArea);
        final JPanel messagePanel = new JPanel(new BorderLayout(5, 0));
        messageField = new JTextField();
        messageField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(final KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    sendMessage();
                }
            }
        });
        final JButton sendButton = new JButton("Send");
        sendButton.addActionListener((e) -> sendMessage());
        messagePanel.add(messageField, BorderLayout.CENTER);
        messagePanel.add(sendButton, BorderLayout.EAST);
        final JPanel chatAndMessagePanel = new JPanel(new BorderLayout(0, 5));
        chatAndMessagePanel.add(chatScrollPane, BorderLayout.CENTER);
        chatAndMessagePanel.add(messagePanel, BorderLayout.SOUTH);
        add(chatAndMessagePanel, BorderLayout.CENTER);
        setPreferredSize(new Dimension(500, 400));
    }

    /**
     * Return the singleton instance of the ChatPanel.
     *
     * @return ChatPanel instance
     */
    public static synchronized ChatPanel getInstance() {
        if (instance == null) {
            instance = new ChatPanel();
        }
        return instance;
    }

    /**
     * Add a new message to the chat history.
     *
     * @param message chat message to add
     */
    public void addMessage(final ChatMessage message) {
        SwingUtilities.invokeLater(() -> {
            chatArea.append(message.getDisplayContent() + "\n");
            chatArea.setCaretPosition(chatArea.getDocument().getLength());
        });
    }

    /**
     * Load messages list into the chat history.
     *
     * @param history list of chat messages to display
     */
    public void loadChatHistory(final List<ChatMessage> history) {
        SwingUtilities.invokeLater(() -> {
            chatArea.setText("");
            for (final ChatMessage message : history) {
                chatArea.append(message.getDisplayContent() + "\n");
            }
            chatArea.setCaretPosition(chatArea.getDocument().getLength());
        });
    }

    /**
     * Send the current message and clears the input field.
     */
    private void sendMessage() {
        final String message = messageField.getText().trim();
        if (!message.isEmpty()) {
            ChatService.getInstance().sendMessage(message);
            messageField.setText("");
        }
    }
} 
