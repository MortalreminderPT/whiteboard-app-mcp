

package io.tomori.whiteboard.model.tools;

import io.tomori.whiteboard.model.shapes.OvalShape;
import io.tomori.whiteboard.model.shapes.Shape;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.util.List;

/**
 * Tool for creating oval shapes on the whiteboard.
 * Create ovals by defining a center point and dragging to determine the radii.
 */
public class OvalTool extends Tool {
    /**
     * Start point where the mouse was pressed
     */
    private Point startPoint = null;
    /**
     * Current point of the mouse during dragging
     */
    private Point currentPoint = null;

    /**
     * Handle mouse press to set the oval's center point.
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
     * Handle mouse drag to update the oval's radii.
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
     * Handle mouse release to create the final oval.
     *
     * @param e      mouse event
     * @param shapes shapes list in the whiteboard
     * @return True if an oval was created
     */
    @Override
    public boolean mouseReleased(final MouseEvent e, final List<Shape> shapes) {
        if (startPoint != null) {
            currentPoint = e.getPoint();
            final int radiusX = Math.abs(currentPoint.x - startPoint.x);
            final int radiusY = Math.abs(currentPoint.y - startPoint.y);
            final int centerX = startPoint.x;
            final int centerY = startPoint.y;
            if (radiusX > 0 && radiusY > 0) {
                final OvalShape oval = new OvalShape(centerX, centerY, radiusX, radiusY);
                oval.setColor(color);
                oval.setStrokeWidth(strokeWidth);
                shapes.add(oval);
            }
            startPoint = null;
            currentPoint = null;
            return true;
        }
        return false;
    }

    /**
     * Draw a preview of the oval during creation.
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
            final int radiusX = Math.abs(currentPoint.x - startPoint.x);
            final int radiusY = Math.abs(currentPoint.y - startPoint.y);
            g2d.drawOval(startPoint.x - radiusX, startPoint.y - radiusY, radiusX * 2, radiusY * 2);
            g2d.setStroke(originalStroke);
            g2d.setColor(originalColor);
        }
    }

    /**
     * Create a copy of this tool.
     *
     * @return A new OvalTool with same properties
     */
    @Override
    public Tool clone() {
        final OvalTool tool = new OvalTool();
        tool.setColor(color);
        tool.setStrokeWidth(strokeWidth);
        return tool;
    }
} 
