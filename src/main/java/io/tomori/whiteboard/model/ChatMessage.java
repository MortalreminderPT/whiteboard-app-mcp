

package io.tomori.whiteboard.model;

import com.google.gson.internal.LinkedTreeMap;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Represent a chat message in the whiteboard application.
 * Contain sender information, message content, and timestamp.
 */
@Data
public class ChatMessage implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    private final String username;
    private final String content;
    private final long timestamp;

    /**
     * Create a new chat message with specified details.
     *
     * @param username  sender's username
     * @param content   message content
     * @param timestamp message timestamp in milliseconds
     */
    public ChatMessage(final String username, final String content, final long timestamp) {
        this.username = username;
        this.content = content;
        this.timestamp = timestamp;
    }

    /**
     * Create a ChatMessage from a LinkedTreeMap representation.
     *
     * @param map map containing message data
     * @return A new ChatMessage instance
     */
    public static ChatMessage fromLinkedTreeMap(final LinkedTreeMap<?, ?> map) {
        final String username = (String) map.get("username");
        final String content = (String) map.get("content");
        final long timestamp = ((Number) map.get("timestamp")).longValue();
        return new ChatMessage(username, content, timestamp);
    }

    /**
     * Return the timestamp formatted as a time string.
     *
     * @return Formatted time string (HH:mm:ss)
     */
    public String getFormattedTime() {
        final SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        return sdf.format(new Date(timestamp));
    }

    /**
     * Return the message formatted for display.
     *
     * @return formatted message including time and username
     */
    public String getDisplayContent() {
        return String.format("[%s] %s: %s", getFormattedTime(), username, content);
    }
}
