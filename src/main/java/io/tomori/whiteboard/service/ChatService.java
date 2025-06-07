

package io.tomori.whiteboard.service;

import io.tomori.whiteboard.constant.CommandType;
import io.tomori.whiteboard.core.NetworkManager;
import io.tomori.whiteboard.gui.panels.ChatPanel;
import io.tomori.whiteboard.model.ChatMessage;
import io.tomori.whiteboard.model.SocketMessage;
import lombok.Getter;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Service for managing chat operation.
 * Handle send, receive, and display chat message.
 */
public class ChatService {
    private static ChatService instance;
    private final NetworkManager networkManager;
    /**
     * List of all chat messages order by time.
     */
    @Getter
    private final List<ChatMessage> chatHistory = new ArrayList<>();

    /**
     * Create a chat service with the network manager.
     *
     * @param networkManager network manager for sending messages
     */
    private ChatService(final NetworkManager networkManager) {
        this.networkManager = networkManager;
    }

    /**
     * Return singleton instance of ChatService.
     *
     * @return ChatService instance
     * @throws IllegalStateException if the service is not initialized
     */
    public static synchronized ChatService getInstance() {
        if (instance == null) {
            throw new IllegalStateException("MessageService not initialized. Call initialize() first.");
        }
        return instance;
    }

    /**
     * Initialize the ChatService singleton.
     *
     * @param networkManager network manager for sending message
     * @return initialized ChatService instance
     */
    public static ChatService initialize(final NetworkManager networkManager) {
        if (instance == null) {
            instance = new ChatService(networkManager);
        }
        return instance;
    }

    /**
     * Add a received message to the chat history and update UI.
     *
     * @param username sender's username
     * @param content  message content
     */
    public void receiveMessage(final String username, final String content) {
        final ChatMessage message = new ChatMessage(username, content, System.currentTimeMillis());
        chatHistory.add(message);
        SwingUtilities.invokeLater(() -> {
            ChatPanel.getInstance().addMessage(message);
        });
    }

    /**
     * Send a chat message to all user and add it to the local chat history.
     *
     * @param content message content to send
     */
    public void sendMessage(final String content) {
        final SocketMessage update = new SocketMessage(
                networkManager.getUsername(),
                CommandType.CHAT_MESSAGE,
                content
        );
        networkManager.sendUpdate(update);
        receiveMessage(networkManager.getUsername(), content);
    }

    /**
     * Update chat history with received messages and refresh UI.
     *
     * @param history updated chat messages
     */
    public void fetchChatHistory(final List<ChatMessage> history) {
        chatHistory.clear();
        chatHistory.addAll(history);
        SwingUtilities.invokeLater(() -> {
            ChatPanel.getInstance().loadChatHistory(chatHistory);
        });
    }
} 
