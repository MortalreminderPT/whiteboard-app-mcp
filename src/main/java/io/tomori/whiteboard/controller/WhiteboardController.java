

package io.tomori.whiteboard.controller;

import com.google.gson.internal.LinkedTreeMap;
import io.tomori.whiteboard.annotations.Command;
import io.tomori.whiteboard.constant.CommandType;
import io.tomori.whiteboard.model.SocketMessage;
import io.tomori.whiteboard.model.shapes.Shape;
import io.tomori.whiteboard.service.WhiteboardService;

import java.util.List;

/**
 * Controller for whiteboard command.
 * Handle shape updates from connected clients.
 */
public class WhiteboardController {
    private static WhiteboardController instance;
    private final WhiteboardService whiteboardService;

    /**
     * Private constructor for singleton pattern.
     */
    private WhiteboardController() {
        whiteboardService = WhiteboardService.getInstance();
    }

    /**
     * Return the singleton instance of WhiteboardController.
     *
     * @return WhiteboardController instance
     */
    public static synchronized WhiteboardController getInstance() {
        if (instance == null) {
            instance = new WhiteboardController();
        }
        return instance;
    }

    /**
     * Update all shapes on the whiteboard with received data.
     *
     * @param update Socket message containing the updated shapes
     */
    @Command(CommandType.UPDATE_SHAPES)
    public void updateShapes(final SocketMessage update) {
        System.out.println("Updating all shapes...");
        if (update.getData() instanceof List) {
            @SuppressWarnings("unchecked") final List<Shape> shapes = ((List<LinkedTreeMap<?, ?>>) update.getData()).stream().map(Shape::fromLinkedTreeMap).toList();
            whiteboardService.updateShapes(shapes);
        } else {
            System.out.println("Invalid data for update all shapes operation.");
        }
    }
}
