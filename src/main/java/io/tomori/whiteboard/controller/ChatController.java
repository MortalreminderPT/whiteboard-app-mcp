

package io.tomori.whiteboard.controller;

import com.google.gson.internal.LinkedTreeMap;
import io.tomori.whiteboard.annotations.Command;
import io.tomori.whiteboard.constant.CommandType;
import io.tomori.whiteboard.model.ChatMessage;
import io.tomori.whiteboard.model.SocketMessage;
import io.tomori.whiteboard.service.ChatService;

import java.util.List;

/**
 * Controller for chat command.
 * Process income chat messages and chat history updates.
 */
public class ChatController {
    private static ChatController instance;
    private final ChatService chatService;

    /**
     * Private constructor for singleton pattern.
     */
    private ChatController() {
        chatService = ChatService.getInstance();
    }

    /**
     * Return the singleton instance of ChatController.
     *
     * @return ChatController instance
     */
    public static synchronized ChatController getInstance() {
        if (instance == null) {
            instance = new ChatController();
        }
        return instance;
    }

    /**
     * Process a received chat message.
     *
     * @param update Socket message containing the chat message
     */
    @Command(CommandType.CHAT_MESSAGE)
    public void receiveMessage(final SocketMessage update) {
        System.out.println("Receiving chat message...");
        if (update.getData() instanceof final String message) {
            chatService.receiveMessage(update.getUsername(), message);
        } else {
            System.out.println("Invalid data for chat message operation.");
        }
    }

    /**
     * Update the chat history with received messages.
     *
     * @param update Socket message containing chat history data
     */
    @Command(CommandType.UPDATE_CHAT_HISTORY)
    public void fetchChatHistory(final SocketMessage update) {
        System.out.println("Fetching chat history...");
        if (update.getData() instanceof List) {
            @SuppressWarnings("unchecked") final List<ChatMessage> history = ((List<LinkedTreeMap<?, ?>>) update.getData()).stream().map(ChatMessage::fromLinkedTreeMap).toList();
            chatService.fetchChatHistory(history);
        } else {
            System.out.println("Invalid data for fetch chat history operation.");
        }
    }
} 
