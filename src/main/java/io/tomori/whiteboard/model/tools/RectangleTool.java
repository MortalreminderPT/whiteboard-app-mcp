

package io.tomori.whiteboard.model.tools;

import io.tomori.whiteboard.model.shapes.RectangleShape;
import io.tomori.whiteboard.model.shapes.Shape;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.util.List;

/**
 * Tool for creating rectangle shapes on the whiteboard.
 * Create rectangles by defining opposite corners through mouse interactions.
 */
public class RectangleTool extends Tool {
    /**
     * Start point where the mouse was pressed
     */
    private Point startPoint = null;
    /**
     * Current point of the mouse during dragging
     */
    private Point currentPoint = null;

    /**
     * Handle mouse press to set the rectangle's first corner.
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
     * Handle mouse drag to update the rectangle's opposite corner.
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
     * Handle mouse release to create the final rectangle.
     *
     * @param e      mouse event
     * @param shapes shapes list in the whiteboard
     * @return True if a rectangle was created
     */
    @Override
    public boolean mouseReleased(final MouseEvent e, final List<Shape> shapes) {
        if (startPoint != null) {
            currentPoint = e.getPoint();
            final int x = Math.min(startPoint.x, currentPoint.x);
            final int y = Math.min(startPoint.y, currentPoint.y);
            final int width = Math.abs(currentPoint.x - startPoint.x);
            final int height = Math.abs(currentPoint.y - startPoint.y);
            if (width > 0 && height > 0) {
                final RectangleShape rectangle = new RectangleShape(x, y, width, height);
                rectangle.setColor(color);
                rectangle.setStrokeWidth(strokeWidth);
                shapes.add(rectangle);
            }
            startPoint = null;
            currentPoint = null;
            return true;
        }
        return false;
    }

    /**
     * Draw a preview of the rectangle during creation.
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
            final int x = Math.min(startPoint.x, currentPoint.x);
            final int y = Math.min(startPoint.y, currentPoint.y);
            final int width = Math.abs(currentPoint.x - startPoint.x);
            final int height = Math.abs(currentPoint.y - startPoint.y);
            g2d.drawRect(x, y, width, height);
            g2d.setStroke(originalStroke);
            g2d.setColor(originalColor);
        }
    }

    /**
     * Create a copy of this tool.
     *
     * @return A new RectangleTool with same properties
     */
    @Override
    public Tool clone() {
        final RectangleTool tool = new RectangleTool();
        tool.setColor(color);
        tool.setStrokeWidth(strokeWidth);
        return tool;
    }
} 
