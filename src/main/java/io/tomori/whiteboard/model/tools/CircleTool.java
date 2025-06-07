

package io.tomori.whiteboard.model.tools;

import io.tomori.whiteboard.model.Color;
import io.tomori.whiteboard.model.shapes.CircleShape;
import io.tomori.whiteboard.model.shapes.Shape;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.util.List;

/**
 * Tool for creating circle shapes on the whiteboard.
 * Create circles by defining a center point and dragging to determine the radius.
 */
public class CircleTool extends Tool {
    /**
     * Start point where the mouse was pressed
     */
    private Point startPoint = null;
    /**
     * Current point of the mouse during dragging
     */
    private Point currentPoint = null;

    /**
     * Handle mouse press to set the circle's center point.
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
     * Handle mouse drag to update the circle's radius.
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
     * Handle mouse release to create the final circle.
     *
     * @param e      mouse event
     * @param shapes shapes list in the whiteboard
     * @return True if a circle was created
     */
    @Override
    public boolean mouseReleased(final MouseEvent e, final List<Shape> shapes) {
        if (startPoint != null) {
            currentPoint = e.getPoint();
            final int diameter = Math.max(
                    Math.abs(currentPoint.x - startPoint.x),
                    Math.abs(currentPoint.y - startPoint.y)
            ) * 2;
            final int centerX = startPoint.x;
            final int centerY = startPoint.y;
            if (diameter > 0) {
                final CircleShape circle = new CircleShape(centerX, centerY, diameter / 2);
                circle.setColor(color);
                circle.setStrokeWidth(strokeWidth);
                shapes.add(circle);
            }
            startPoint = null;
            currentPoint = null;
            return true;
        }
        return false;
    }

    /**
     * Draw a preview of the circle during creation.
     *
     * @param g2d graphics context to draw on
     */
    @Override
    public void drawTemporary(final Graphics2D g2d) {
        if (startPoint != null && currentPoint != null) {
            final Stroke originalStroke = g2d.getStroke();
            final Color originalColor = new Color(g2d.getColor());
            g2d.setStroke(new BasicStroke(strokeWidth));
            g2d.setColor(color.toAwtColor());
            final int radius = Math.max(
                    Math.abs(currentPoint.x - startPoint.x),
                    Math.abs(currentPoint.y - startPoint.y)
            );
            g2d.drawOval(startPoint.x - radius, startPoint.y - radius, radius * 2, radius * 2);
            g2d.setStroke(originalStroke);
            g2d.setColor(originalColor.toAwtColor());
        }
    }

    /**
     * Create a copy of this tool.
     *
     * @return A new CircleTool with same properties
     */
    @Override
    public Tool clone() {
        final CircleTool tool = new CircleTool();
        tool.setColor(color);
        tool.setStrokeWidth(strokeWidth);
        return tool;
    }
} 
