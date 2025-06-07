

package io.tomori.whiteboard.model.tools;

import io.tomori.whiteboard.model.shapes.LineShape;
import io.tomori.whiteboard.model.shapes.Shape;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.util.List;

/**
 * Tool for creating straight lines on the whiteboard.
 * Create lines by defining start and end points through mouse interactions.
 */
public class LineTool extends Tool {
    /**
     * Start point where the mouse was pressed
     */
    private Point startPoint = null;
    /**
     * Current point of the mouse during dragging
     */
    private Point currentPoint = null;

    /**
     * Handle mouse press to set the line's start point.
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
     * Handle mouse drag to update the line's end point.
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
     * Handle mouse release to create the final line.
     *
     * @param e      mouse event
     * @param shapes shapes list in the whiteboard
     * @return True if a line was created
     */
    @Override
    public boolean mouseReleased(final MouseEvent e, final List<Shape> shapes) {
        if (startPoint != null) {
            currentPoint = e.getPoint();
            if (startPoint.x != currentPoint.x || startPoint.y != currentPoint.y) {
                final LineShape line = new LineShape(
                        startPoint.x, startPoint.y,
                        currentPoint.x, currentPoint.y
                );
                line.setColor(color);
                line.setStrokeWidth(strokeWidth);
                shapes.add(line);
            }
            startPoint = null;
            currentPoint = null;
            return true;
        }
        return false;
    }

    /**
     * Draw a preview of the line during creation.
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
            g2d.drawLine(startPoint.x, startPoint.y, currentPoint.x, currentPoint.y);
            g2d.setStroke(originalStroke);
            g2d.setColor(originalColor);
        }
    }

    /**
     * Create a copy of this tool.
     *
     * @return A new LineTool with same properties
     */
    @Override
    public Tool clone() {
        final LineTool tool = new LineTool();
        tool.setColor(color);
        tool.setStrokeWidth(strokeWidth);
        return tool;
    }
} 
