

package io.tomori.whiteboard.model.tools;

import io.tomori.whiteboard.model.shapes.Shape;
import io.tomori.whiteboard.model.shapes.TriangleShape;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.util.List;

/**
 * Tool for creating triangle shapes on the whiteboard.
 * Create isosceles triangles by defining the apex and dragging to determine the base width and height.
 */
public class TriangleTool extends Tool {
    /**
     * Start point where the mouse was pressed (apex of the triangle)
     */
    private Point startPoint = null;
    /**
     * Current point of the mouse during dragging
     */
    private Point currentPoint = null;

    /**
     * Handle mouse press to set the triangle's apex point.
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
     * Handle mouse drag to update the triangle's base width and height.
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
     * Handle mouse release to create the final triangle.
     *
     * @param e      mouse event
     * @param shapes shapes list in the whiteboard
     * @return True if a triangle was created
     */
    @Override
    public boolean mouseReleased(final MouseEvent e, final List<Shape> shapes) {
        if (startPoint != null && !startPoint.equals(e.getPoint())) {
            currentPoint = e.getPoint();
            final int width = Math.abs(currentPoint.x - startPoint.x) * 2;
            final int height = Math.abs(currentPoint.y - startPoint.y);
            if (width > 0 && height > 0) {
                final Point p1;
                final Point p2;
                final Point p3;
                final boolean inverted = currentPoint.y < startPoint.y;
                if (inverted) {
                    p1 = new Point(startPoint.x, startPoint.y);
                    p2 = new Point(startPoint.x - width / 2, currentPoint.y);
                    p3 = new Point(startPoint.x + width / 2, currentPoint.y);
                } else {
                    p1 = new Point(startPoint.x, startPoint.y);
                    p2 = new Point(startPoint.x - width / 2, startPoint.y + height);
                    p3 = new Point(startPoint.x + width / 2, startPoint.y + height);
                }
                final TriangleShape triangle = new TriangleShape(p1, p2, p3);
                triangle.setColor(color);
                triangle.setStrokeWidth(strokeWidth);
                shapes.add(triangle);
            }
            startPoint = null;
            currentPoint = null;
            return true;
        }
        return false;
    }

    /**
     * Draw a preview of the triangle during creation.
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
            final int width = Math.abs(currentPoint.x - startPoint.x) * 2;
            final int height = Math.abs(currentPoint.y - startPoint.y);
            final boolean inverted = currentPoint.y < startPoint.y;
            final int[] xPoints = new int[3];
            final int[] yPoints = new int[3];
            if (inverted) {
                xPoints[0] = startPoint.x;
                yPoints[0] = startPoint.y;
                xPoints[1] = startPoint.x - width / 2;
                yPoints[1] = currentPoint.y;
                xPoints[2] = startPoint.x + width / 2;
                yPoints[2] = currentPoint.y;
            } else {
                xPoints[0] = startPoint.x;
                yPoints[0] = startPoint.y;
                xPoints[1] = startPoint.x - width / 2;
                yPoints[1] = startPoint.y + height;
                xPoints[2] = startPoint.x + width / 2;
                yPoints[2] = startPoint.y + height;
            }
            g2d.drawPolygon(xPoints, yPoints, 3);
            g2d.setStroke(originalStroke);
            g2d.setColor(originalColor);
        }
    }

    /**
     * Create a copy of this tool.
     *
     * @return A new TriangleTool with same properties
     */
    @Override
    public Tool clone() {
        final TriangleTool tool = new TriangleTool();
        tool.setColor(color);
        tool.setStrokeWidth(strokeWidth);
        return tool;
    }
} 
