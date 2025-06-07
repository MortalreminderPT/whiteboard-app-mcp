

package io.tomori.whiteboard.model.tools;

import io.tomori.whiteboard.model.shapes.Shape;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Tool for selecting and manipulating shapes on the whiteboard.
 * Allow users to select, move, and resize shapes using handles at the corners and edges.
 */
public class SelectionTool extends Tool {
    /**
     * Size of resize handles in pixels
     */
    private static final int HANDLE_SIZE = 6;
    /**
     * Currently selected shape
     */
    private Shape selectedShape = null;
    /**
     * Last position of the mouse during drag operations
     */
    private Point lastPoint = null;
    /**
     * Start point of the current operation
     */
    private Point startPoint = null;
    /**
     * Current resize handle being manipulated
     */
    private ResizeHandle currentHandle = ResizeHandle.NONE;

    /**
     * Check if a point is within a handle's bounds.
     *
     * @param point   point to check
     * @param handleX handle's x-coord
     * @param handleY handle's y-coord
     * @return True if the point is in the handle
     */
    private static boolean isInHandle(final Point point, final int handleX, final int handleY) {
        final int halfSize = HANDLE_SIZE / 2;
        return point.x >= handleX - halfSize && point.x <= handleX + halfSize
                && point.y >= handleY - halfSize && point.y <= handleY + halfSize;
    }

    /**
     * Resize a shape by the specified deltas.
     *
     * @param shape shape to resize
     * @param dw    width delta
     * @param dh    height delta
     */
    private static void resizeShape(final Shape shape, final int dw, final int dh) {
        shape.resize(dw, dh);
    }

    /**
     * Draw a resize handle at the specified position.
     *
     * @param g graphics context
     * @param x x-coord
     * @param y y-coord
     */
    private static void drawHandle(final Graphics2D g, final int x, final int y) {
        final int halfSize = HANDLE_SIZE / 2;
        g.setColor(java.awt.Color.WHITE);
        g.fillRect(x - halfSize, y - halfSize, HANDLE_SIZE, HANDLE_SIZE);
        g.setColor(java.awt.Color.BLUE);
        g.drawRect(x - halfSize, y - halfSize, HANDLE_SIZE, HANDLE_SIZE);
    }

    /**
     * Handle mouse press to select a shape or resize handle.
     *
     * @param e      mouse event
     * @param shapes shapes list in the whiteboard
     * @return True if the event was handled
     */
    @Override
    public boolean mousePressed(final MouseEvent e, final List<Shape> shapes) {
        startPoint = e.getPoint();
        lastPoint = e.getPoint();
        if (selectedShape != null) {
            currentHandle = getResizeHandleAt(e.getPoint());
            if (currentHandle != ResizeHandle.NONE) {
                return true;
            }
        }
        for (int i = shapes.size() - 1; i >= 0; i--) {
            final Shape shape = shapes.get(i);
            if (shape.contains(e.getPoint())) {
                selectedShape = shape;
                if (i < shapes.size() - 1) {
                    final Shape shapeToMove = shapes.get(i);
                    final List<Shape> newShapes = new CopyOnWriteArrayList<>();
                    for (int j = 0; j < shapes.size(); j++) {
                        if (j != i) {
                            newShapes.add(shapes.get(j));
                        }
                    }
                    newShapes.add(shapeToMove);
                    shapes.clear();
                    shapes.addAll(newShapes);
                }
                return true;
            }
        }
        selectedShape = null;
        return true;
    }

    /**
     * Handle mouse drag to move or resize the selected shape.
     *
     * @param e      mouse event
     * @param shapes shapes list in the whiteboard
     * @return True if a shape was manipulated
     */
    @Override
    public boolean mouseDragged(final MouseEvent e, final List<Shape> shapes) {
        if (selectedShape != null) {
            if (currentHandle != ResizeHandle.NONE) {
                resizeShape(e.getPoint());
                lastPoint = e.getPoint();
                return true;
            } else {
                final int dx = e.getX() - lastPoint.x;
                final int dy = e.getY() - lastPoint.y;
                selectedShape.move(dx, dy);
                lastPoint = e.getPoint();
                return true;
            }
        }
        return false;
    }

    /**
     * Handle mouse release to complete the selection operation.
     *
     * @param e      mouse event
     * @param shapes shapes list in the whiteboard
     * @return True if an operation was completed
     */
    @Override
    public boolean mouseReleased(final MouseEvent e, final List<Shape> shapes) {
        if (selectedShape != null) {
            if (Math.abs(e.getX() - startPoint.x) < 5 && Math.abs(e.getY() - startPoint.y) < 5
                    && currentHandle == ResizeHandle.NONE) {
            }
            currentHandle = ResizeHandle.NONE;
            return true;
        }
        return false;
    }

    /**
     * Determine which resize handle is at the given point.
     *
     * @param point point to check
     * @return resize handle at the point, or NONE if none
     */
    private ResizeHandle getResizeHandleAt(final Point point) {
        if (selectedShape == null) {
            return ResizeHandle.NONE;
        }
        final Rectangle bounds = selectedShape.getBounds();
        final int x = bounds.x;
        final int y = bounds.y;
        final int w = bounds.width;
        final int h = bounds.height;
        if (isInHandle(point, x, y)) {
            return ResizeHandle.TOP_LEFT;
        }
        if (isInHandle(point, x + w, y)) {
            return ResizeHandle.TOP_RIGHT;
        }
        if (isInHandle(point, x, y + h)) {
            return ResizeHandle.BOTTOM_LEFT;
        }
        if (isInHandle(point, x + w, y + h)) {
            return ResizeHandle.BOTTOM_RIGHT;
        }
        if (isInHandle(point, x + w / 2, y)) {
            return ResizeHandle.TOP_CENTER;
        }
        if (isInHandle(point, x + w / 2, y + h)) {
            return ResizeHandle.BOTTOM_CENTER;
        }
        if (isInHandle(point, x, y + h / 2)) {
            return ResizeHandle.LEFT_CENTER;
        }
        if (isInHandle(point, x + w, y + h / 2)) {
            return ResizeHandle.RIGHT_CENTER;
        }
        return ResizeHandle.NONE;
    }

    /**
     * Resize selected shape based on the current handle and mouse position.
     *
     * @param point current mouse position
     */
    private void resizeShape(final Point point) {
        if (selectedShape == null || currentHandle == ResizeHandle.NONE) {
            return;
        }
        final int dx = point.x - lastPoint.x;
        final int dy = point.y - lastPoint.y;
        switch (currentHandle) {
            case TOP_LEFT:
                resizeShape(selectedShape, -dx, -dy);
                break;
            case TOP_RIGHT:
                resizeShape(selectedShape, dx, -dy);
                break;
            case BOTTOM_LEFT:
                resizeShape(selectedShape, -dx, dy);
                break;
            case BOTTOM_RIGHT:
                resizeShape(selectedShape, dx, dy);
                break;
            case TOP_CENTER:
                resizeShape(selectedShape, 0, -dy);
                break;
            case BOTTOM_CENTER:
                resizeShape(selectedShape, 0, dy);
                break;
            case LEFT_CENTER:
                resizeShape(selectedShape, -dx, 0);
                break;
            case RIGHT_CENTER:
                resizeShape(selectedShape, dx, 0);
                break;
            default:
                break;
        }
    }

    /**
     * Draw the selection indicators and resize handles for the selected shape.
     *
     * @param g graphics context to draw on
     */
    @Override
    public void drawTemporary(final Graphics2D g) {
        if (selectedShape != null) {
            final Stroke originalStroke = g.getStroke();
            final Color originalColor = g.getColor();
            g.setStroke(new BasicStroke(1.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[]{3}, 0));
            g.setColor(java.awt.Color.BLUE);
            final Rectangle bounds = selectedShape.getBounds();
            g.drawRect(bounds.x, bounds.y, bounds.width, bounds.height);
            g.setStroke(new BasicStroke(1.0f));
            drawHandle(g, bounds.x, bounds.y);
            drawHandle(g, bounds.x + bounds.width, bounds.y);
            drawHandle(g, bounds.x, bounds.y + bounds.height);
            drawHandle(g, bounds.x + bounds.width, bounds.y + bounds.height);
            drawHandle(g, bounds.x + bounds.width / 2, bounds.y);
            drawHandle(g, bounds.x + bounds.width / 2, bounds.y + bounds.height);
            drawHandle(g, bounds.x, bounds.y + bounds.height / 2);
            drawHandle(g, bounds.x + bounds.width, bounds.y + bounds.height / 2);
            g.setStroke(originalStroke);
            g.setColor(originalColor);
        }
    }

    /**
     * Create a copy of this tool.
     *
     * @return A new SelectionTool with same properties
     */
    @Override
    public Tool clone() {
        final SelectionTool tool = new SelectionTool();
        tool.setColor(color);
        tool.setStrokeWidth(strokeWidth);
        return tool;
    }

    /**
     * Enumeration of possible resize handles for shape manipulation.
     */
    private enum ResizeHandle {
        NONE, TOP_LEFT, TOP_RIGHT, BOTTOM_LEFT, BOTTOM_RIGHT, TOP_CENTER, BOTTOM_CENTER, LEFT_CENTER, RIGHT_CENTER
    }
} 
