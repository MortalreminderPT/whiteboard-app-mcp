

package io.tomori.whiteboard.service;

import io.tomori.whiteboard.constant.CommandType;
import io.tomori.whiteboard.core.NetworkManager;
import io.tomori.whiteboard.core.Server;
import io.tomori.whiteboard.gui.panels.DrawingPanel;
import io.tomori.whiteboard.model.SocketMessage;
import io.tomori.whiteboard.model.shapes.Shape;
import io.tomori.whiteboard.model.tools.EraserTool;
import io.tomori.whiteboard.util.DocumentManager;
import io.tomori.whiteboard.util.SvgUtil;
import lombok.Getter;

import java.io.File;
import java.util.List;
import java.util.Stack;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Service for managing whiteboard drawing operation.
 * Handle shape creation, modification, and sync among sever and clients.
 */
public class WhiteboardService {
    private static final int MAX_UNDO_STEPS = 20;
    private static final int SYNC_INTERVAL = 10000;
    private static WhiteboardService instance;
    private final NetworkManager networkManager;
    private final DocumentManager documentManager;
    private final Stack<CopyOnWriteArrayList<Shape>> undoStack = new Stack<>();
    private final Stack<CopyOnWriteArrayList<Shape>> redoStack = new Stack<>();
    /**
     * Current shapes list on whiteboard
     */
    @Getter
    private CopyOnWriteArrayList<Shape> shapes = new CopyOnWriteArrayList<>();

    /**
     * Flag to track if the document has been modified since the last save
     */
    private boolean modified = false;

    /**
     * Create a whiteboard service with the specified network manager.
     *
     * @param networkManager network manager for sending updates
     */
    private WhiteboardService(final NetworkManager networkManager) {
        this.networkManager = networkManager;
        documentManager = DocumentManager.getInstance();
    }

    /**
     * Return the singleton instance of WhiteboardService.
     *
     * @return WhiteboardService instance
     * @throws IllegalStateException if service is not initialized
     */
    public static synchronized WhiteboardService getInstance() {
        if (instance == null) {
            throw new IllegalStateException("WhiteboardService not initialized. Call initialize() first.");
        }
        return instance;
    }

    /**
     * Initialize the WhiteboardService singleton.
     *
     * @param networkManager network manager for sending updates
     * @return initialized WhiteboardService instance
     */
    public static WhiteboardService initialize(final NetworkManager networkManager) {
        if (instance == null) {
            instance = new WhiteboardService(networkManager);
        }
        return instance;
    }

    /**
     * Generate a Base64 preview of the current whiteboard state.
     *
     * @return Base64 encoded string of the whiteboard preview
     */
    public static String previewAsBase64() {
        return DrawingPanel.getInstance().toBase64(0.3f);
    }

    /**
     * Check if the current document has been modified since the last save.
     *
     * @return true if the document has been modified, false otherwise
     */
    public boolean isModified() {
        return modified;
    }

    /**
     * Set the modification state of the current document.
     *
     * @param modified true to mark the document as modified, false otherwise
     */
    public void setModified(final boolean modified) {
        this.modified = modified;
    }

    /**
     * Synchronize shapes with all connected clients.
     */
    public void syncShapes() {
        System.out.println("Syncing all shapes to clients...");
        if (networkManager instanceof Server) {
            final SocketMessage update = new SocketMessage(
                    networkManager.getUsername(),
                    CommandType.UPDATE_SHAPES,
                    shapes
            );
            networkManager.sendUpdate(update);
        }
    }

    /**
     * Clear all shapes from the whiteboard.
     *
     * @param silent If true, doesn't broadcast the update to other clients
     */
    public void clearAll(final boolean silent) {
        shapes.clear();
        undoStack.clear();
        redoStack.clear();
        DrawingPanel.getInstance().repaint();
        if (!silent) {
            final SocketMessage update = new SocketMessage(
                    networkManager.getUsername(),
                    CommandType.UPDATE_SHAPES,
                    shapes
            );
            networkManager.sendUpdate(update);
        }
    }

    /**
     * Clear all shapes and broadcasts the update to other clients.
     */
    public void clearAll() {
        clearAll(false);
        setModified(true);
    }

    /**
     * Update the whiteboard with a new set of shapes.
     *
     * @param shapes new shapes list to display
     */
    public synchronized void updateShapes(final List<Shape> shapes) {
        this.shapes = new CopyOnWriteArrayList<>();
        for (final Shape shape : shapes) {
            this.shapes.add(shape.clone());
        }
        DrawingPanel.getInstance().repaint();
        setModified(true);
    }

    /**
     * Add shapes from svg to the whiteboard and broadcasts the update.
     *
     * @param svgString SVG string representation of the shapes
     */
    public synchronized void addShapesSvg(final String svgString) {
        final List<Shape> svgShapes = SvgUtil.fromSvg(svgString);
        shapes.addAll(svgShapes);
        DrawingPanel.getInstance().repaint();
        final SocketMessage update = new SocketMessage(
                networkManager.getUsername(),
                CommandType.UPDATE_SHAPES,
                shapes
        );
        networkManager.sendUpdate(update);
        setModified(true);
    }

    /**
     * Remove shapes from the whiteboard.
     *
     * @param shapeIds ID of the shapes to remove
     */
    public synchronized void removeShapes(final List<String> shapeIds) {
        for (final String shapeId : shapeIds) {
            shapes.removeIf(shape -> shape.getId().equals(shapeId));
        }
        DrawingPanel.getInstance().repaint();
        final SocketMessage update = new SocketMessage(
                networkManager.getUsername(),
                CommandType.UPDATE_SHAPES,
                shapes
        );
        networkManager.sendUpdate(update);
        setModified(true);
    }

    /**
     * Save the current state for undo operations.
     */
    public void saveState() {
        final CopyOnWriteArrayList<Shape> currentState = new CopyOnWriteArrayList<>();
        for (final Shape shape : shapes) {
            currentState.add(shape.clone());
        }
        undoStack.push(currentState);
        if (undoStack.size() > MAX_UNDO_STEPS) {
            undoStack.removeFirst();
        }
        redoStack.clear();
        setModified(true);
    }

    /**
     * Undo the last drawing operation.
     */
    public void undo() {
        if (!undoStack.isEmpty()) {
            final CopyOnWriteArrayList<Shape> currentState = new CopyOnWriteArrayList<>();
            for (final Shape shape : shapes) {
                currentState.add(shape.clone());
            }
            redoStack.push(currentState);
            shapes = undoStack.pop();
            DrawingPanel.getInstance().repaint();
            final SocketMessage update = new SocketMessage(
                    networkManager.getUsername(),
                    CommandType.UPDATE_SHAPES,
                    shapes
            );
            networkManager.sendUpdate(update);
            setModified(true);
        }
    }

    /**
     * Redo the last undone drawing operation.
     */
    public void redo() {
        if (!redoStack.isEmpty()) {
            final CopyOnWriteArrayList<Shape> currentState = new CopyOnWriteArrayList<>();
            for (final Shape shape : shapes) {
                currentState.add(shape.clone());
            }
            undoStack.push(currentState);
            shapes = redoStack.pop();
            DrawingPanel.getInstance().repaint();
            final SocketMessage update = new SocketMessage(
                    networkManager.getUsername(),
                    CommandType.UPDATE_SHAPES,
                    shapes
            );
            networkManager.sendUpdate(update);
            setModified(true);
        }
    }

    /**
     * Load shapes from a document file.
     *
     * @param file file to load shapes from
     * @return True if loaded successfully
     */
    public boolean loadFromDocument(final File file) {
        shapes = documentManager.loadShapes(file);
        undoStack.clear();
        redoStack.clear();
        DrawingPanel.getInstance().repaint();
        final SocketMessage update = new SocketMessage(
                networkManager.getUsername(),
                CommandType.UPDATE_SHAPES,
                shapes
        );
        networkManager.sendUpdate(update);
        setModified(false);
        return !shapes.isEmpty();
    }

    /**
     * Handle tool release events and synchronizes the updated shapes.
     *
     * @param eraserTool eraser tool that was used
     */
    public void handleToolReleased(final EraserTool eraserTool) {
        if (eraserTool != null) {
            eraserTool.clearErasedShapeIds();
        }
        final SocketMessage update = new SocketMessage(
                networkManager.getUsername(),
                CommandType.UPDATE_SHAPES,
                shapes
        );
        networkManager.sendUpdate(update);
    }

    /**
     * Return username of this whiteboard.
     *
     * @return the username
     */
    public String getUsername() {
        return networkManager.getUsername();
    }
}
