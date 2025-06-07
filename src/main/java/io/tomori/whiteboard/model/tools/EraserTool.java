

package io.tomori.whiteboard.model.tools;

import io.tomori.whiteboard.model.shapes.Shape;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

/**
 * Tool for erasing shapes from the whiteboard.
 * Remove shapes that come in contact with the circular eraser area.
 */
public class EraserTool extends Tool {
    /**
     * Minimum size of the eraser in pixels
     */
    private static final int MIN_SIZE = 10;
    /**
     * List of IDs of erased shapes for potential undo operations
     */
    private final List<String> erasedShapeIds = new ArrayList<>();
    /**
     * Current position of the eraser
     */
    private Point currentPoint = null;

    /**
     * Check if a shape intersects with the eraser circle.
     *
     * @param shape        shape to check
     * @param eraserCenter center point of the eraser
     * @param eraserRadius radius of the eraser
     * @return True if the shape intersects with the eraser
     */
    private static boolean shapeContainsEraserPoint(final Shape shape, final Point eraserCenter, final int eraserRadius) {
        final double[] angles = {0, Math.PI / 4, Math.PI / 2, 3 * Math.PI / 4, Math.PI, 5 * Math.PI / 4, 3 * Math.PI / 2, 7 * Math.PI / 4};
        for (final double angle : angles) {
            final int x = (int) (eraserCenter.x + eraserRadius * Math.cos(angle));
            final int y = (int) (eraserCenter.y + eraserRadius * Math.sin(angle));
            final Point pointOnCircle = new Point(x, y);
            if (shape.contains(pointOnCircle)) {
                return true;
            }
        }
        if (shape.contains(eraserCenter)) {
            return true;
        }
        return false;
    }

    /**
     * Handle mouse press to start erasing shapes.
     *
     * @param e      mouse event
     * @param shapes shapes list in the whiteboard
     * @return True if the event was handled
     */
    @Override
    public boolean mousePressed(final MouseEvent e, final List<Shape> shapes) {
        currentPoint = e.getPoint();
        erasedShapeIds.clear();
        eraseAt(currentPoint, shapes);
        return true;
    }

    /**
     * Handle mouse drag to continue erasing shapes.
     *
     * @param e      mouse event
     * @param shapes shapes list in the whiteboard
     * @return True if the event was handled
     */
    @Override
    public boolean mouseDragged(final MouseEvent e, final List<Shape> shapes) {
        currentPoint = e.getPoint();
        eraseAt(currentPoint, shapes);
        return true;
    }

    /**
     * Handle mouse release to finish erasing.
     *
     * @param e      mouse event
     * @param shapes shapes list in the whiteboard
     * @return True if the event was handled
     */
    @Override
    public boolean mouseReleased(final MouseEvent e, final List<Shape> shapes) {
        currentPoint = null;
        return true;
    }

    /**
     * Draw a preview of the eraser circle.
     *
     * @param g2d graphics context to draw on
     */
    @Override
    public void drawTemporary(final Graphics2D g2d) {
        if (currentPoint != null) {
            final Stroke originalStroke = g2d.getStroke();
            final Color originalColor = g2d.getColor();
            g2d.setColor(Color.LIGHT_GRAY);
            g2d.setStroke(new BasicStroke(1));
            final int size = calculateEraserSize();
            g2d.drawOval(currentPoint.x - size / 2, currentPoint.y - size / 2, size, size);
            g2d.setStroke(originalStroke);
            g2d.setColor(originalColor);
        }
    }

    /**
     * Erase shapes at the specified point.
     *
     * @param point  center point of the eraser
     * @param shapes shapes list to check against
     */
    private void eraseAt(final Point point, final List<Shape> shapes) {
        final int eraserSize = calculateEraserSize();
        final int eraserRadius = eraserSize / 2;
        final List<Shape> shapesToRemove = new ArrayList<>();
        for (final Shape shape : shapes) {
            if (shapeContainsEraserPoint(shape, point, eraserRadius)) {
                erasedShapeIds.add(shape.getId());
                shapesToRemove.add(shape);
            }
        }
        shapes.removeAll(shapesToRemove);
    }

    /**
     * Clear the list of erased shape IDs.
     */
    public void clearErasedShapeIds() {
        erasedShapeIds.clear();
    }

    /**
     * Calculate the size of the eraser based on stroke width.
     *
     * @return diameter of the eraser in pixels
     */
    private int calculateEraserSize() {
        return Math.max((int) (strokeWidth * 6), MIN_SIZE);
    }

    /**
     * Set stroke width which also affects eraser size.
     *
     * @param width new stroke width
     */
    @Override
    public void setStrokeWidth(final float width) {
        super.setStrokeWidth(width);
    }

    /**
     * Create a copy of this tool.
     *
     * @return A new EraserTool with same properties
     */
    @Override
    public Tool clone() {
        final EraserTool tool = new EraserTool();
        tool.setColor(color);
        tool.setStrokeWidth(strokeWidth);
        return tool;
    }
} 
