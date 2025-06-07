

package io.tomori.whiteboard.model.tools;

import io.tomori.whiteboard.model.Color;
import io.tomori.whiteboard.model.shapes.PathShape;
import io.tomori.whiteboard.model.shapes.Shape;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

/**
 * Tool for freehand drawing on the whiteboard.
 * Create path shapes by tracking mouse movements during drag operations.
 */
public class PenTool extends Tool {
    /**
     * List of points that make up the current drawing path
     */
    private final List<Point> temporaryPoints = new ArrayList<>();

    /**
     * Handle mouse press to start a new path.
     *
     * @param e      mouse event
     * @param shapes shapes list in the whiteboard
     * @return True if the event was handled
     */
    @Override
    public boolean mousePressed(final MouseEvent e, final List<Shape> shapes) {
        temporaryPoints.clear();
        temporaryPoints.add(e.getPoint());
        return true;
    }

    /**
     * Handle mouse drag to add points to the current path.
     *
     * @param e      mouse event
     * @param shapes shapes list in the whiteboard
     * @return True if the event was handled
     */
    @Override
    public boolean mouseDragged(final MouseEvent e, final List<Shape> shapes) {
        temporaryPoints.add(e.getPoint());
        return true;
    }

    /**
     * Handle mouse release to finish and create the final path.
     *
     * @param e      mouse event
     * @param shapes shapes list in the whiteboard
     * @return True if a path was created
     */
    @Override
    public boolean mouseReleased(final MouseEvent e, final List<Shape> shapes) {
        temporaryPoints.add(e.getPoint());
        if (temporaryPoints.size() >= 2) {
            final PathShape currentPath = new PathShape(new ArrayList<>(temporaryPoints));
            currentPath.setColor(color);
            currentPath.setStrokeWidth(strokeWidth);
            shapes.add(currentPath);
            temporaryPoints.clear();
            return true;
        }
        return false;
    }

    /**
     * Draw a preview of the path during creation.
     *
     * @param g2d graphics context to draw on
     */
    @Override
    public void drawTemporary(final Graphics2D g2d) {
        if (temporaryPoints.size() >= 2) {
            final Stroke originalStroke = g2d.getStroke();
            final Color originalColor = new Color(g2d.getColor());
            g2d.setStroke(new BasicStroke(strokeWidth, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g2d.setColor(color.toAwtColor());
            for (int i = 0; i < temporaryPoints.size() - 1; i++) {
                final Point p1 = temporaryPoints.get(i);
                final Point p2 = temporaryPoints.get(i + 1);
                g2d.drawLine(p1.x, p1.y, p2.x, p2.y);
            }
            g2d.setStroke(originalStroke);
            g2d.setColor(originalColor.toAwtColor());
        }
    }

    /**
     * Create a copy of this tool.
     *
     * @return A new PenTool with same properties
     */
    @Override
    public Tool clone() {
        final PenTool tool = new PenTool();
        tool.setColor(color);
        tool.setStrokeWidth(strokeWidth);
        return tool;
    }
} 
