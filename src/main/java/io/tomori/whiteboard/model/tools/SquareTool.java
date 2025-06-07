

package io.tomori.whiteboard.model.tools;

import io.tomori.whiteboard.model.shapes.RectangleShape;
import io.tomori.whiteboard.model.shapes.Shape;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.util.List;

/**
 * Tool for creating square shapes on the whiteboard.
 * Create perfect squares by using the maximum of width and height from mouse interactions.
 */
public class SquareTool extends Tool {
    /**
     * Start point where the mouse was pressed
     */
    private Point startPoint = null;
    /**
     * Current point of the mouse during dragging
     */
    private Point currentPoint = null;

    /**
     * Handle mouse press to set the square's anchor point.
     *
     * @param e      mouse event
     * @param shapes shapes list in the whiteboard
     * @return True if the event was handled
     */
    @Override
    public boolean mousePressed(final MouseEvent e, final List<Shape> shapes) {
        startPoint = e.getPoint();
        currentPoint = e.getPoint();
        return true;
    }

    /**
     * Handle mouse drag to update the square's size.
     *
     * @param e      mouse event
     * @param shapes shapes list in the whiteboard
     * @return True if the event was handled
     */
    @Override
    public boolean mouseDragged(final MouseEvent e, final List<Shape> shapes) {
        currentPoint = e.getPoint();
        return true;
    }

    /**
     * Handle mouse release to create the final square.
     *
     * @param e      mouse event
     * @param shapes shapes list in the whiteboard
     * @return True if a square was created
     */
    @Override
    public boolean mouseReleased(final MouseEvent e, final List<Shape> shapes) {
        if (startPoint != null) {
            currentPoint = e.getPoint();
            final int size = Math.max(
                    Math.abs(currentPoint.x - startPoint.x),
                    Math.abs(currentPoint.y - startPoint.y)
            );
            int x = startPoint.x;
            int y = startPoint.y;
            if (currentPoint.x < startPoint.x) {
                x = startPoint.x - size;
            }
            if (currentPoint.y < startPoint.y) {
                y = startPoint.y - size;
            }
            if (size > 0) {
                final RectangleShape square = new RectangleShape(x, y, size, size);
                square.setColor(color);
                square.setStrokeWidth(strokeWidth);
                shapes.add(square);
            }
            startPoint = null;
            currentPoint = null;
            return true;
        }
        return false;
    }

    /**
     * Draw a preview of the square during creation.
     *
     * @param g2d graphics context to draw on
     */
    @Override
    public void drawTemporary(final Graphics2D g2d) {
        if (startPoint != null && currentPoint != null) {
            final Stroke originalStroke = g2d.getStroke();
            final Color originalColor = g2d.getColor();
            g2d.setStroke(new BasicStroke(strokeWidth));
            g2d.setColor(color.toAwtColor());
            final int size = Math.max(
                    Math.abs(currentPoint.x - startPoint.x),
                    Math.abs(currentPoint.y - startPoint.y)
            );
            int x = startPoint.x;
            int y = startPoint.y;
            if (currentPoint.x < startPoint.x) {
                x = startPoint.x - size;
            }
            if (currentPoint.y < startPoint.y) {
                y = startPoint.y - size;
            }
            g2d.drawRect(x, y, size, size);
            g2d.setStroke(originalStroke);
            g2d.setColor(originalColor);
        }
    }

    /**
     * Create a copy of this tool.
     *
     * @return A new SquareTool with same properties
     */
    @Override
    public Tool clone() {
        final SquareTool tool = new SquareTool();
        tool.setColor(color);
        tool.setStrokeWidth(strokeWidth);
        return tool;
    }
} 
