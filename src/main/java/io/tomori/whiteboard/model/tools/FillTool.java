

package io.tomori.whiteboard.model.tools;

import io.tomori.whiteboard.model.shapes.Shape;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.util.List;

/**
 * Tool for filling shapes on the whiteboard.
 * Allow users to apply fill color to any shape by clicking on it.
 */
public class FillTool extends Tool {
    /**
     * selected shape to fill
     */
    private Shape selectedShape = null;

    /**
     * Handle mouse press to select and fill a shape.
     *
     * @param e      mouse event
     * @param shapes shapes list in the whiteboard
     * @return True if a shape was selected and filled
     */
    @Override
    public boolean mousePressed(final MouseEvent e, final List<Shape> shapes) {
        selectedShape = null;
        for (int i = shapes.size() - 1; i >= 0; i--) {
            final Shape shape = shapes.get(i);
            if (shape.contains(e.getPoint())) {
                selectedShape = shape;
                selectedShape.setFill(color);
                return true;
            }
        }
        return false;
    }

    /**
     * Fill tool doesn't respond to mouse dragging.
     *
     * @param e      mouse event
     * @param shapes shapes list in the whiteboard
     * @return Always true to maintain operation
     */
    @Override
    public boolean mouseDragged(final MouseEvent e, final List<Shape> shapes) {
        return true;
    }

    /**
     * Handle mouse release to complete the filling operation.
     *
     * @param e      mouse event
     * @param shapes shapes list in the whiteboard
     * @return True if a shape was filled
     */
    @Override
    public boolean mouseReleased(final MouseEvent e, final List<Shape> shapes) {
        return selectedShape != null;
    }

    /**
     * No temporary drawing is needed.
     *
     * @param g2d graphics context to draw on
     */
    @Override
    public void drawTemporary(final Graphics2D g2d) {
    }

    /**
     * Create a copy of this tool.
     *
     * @return A new FillTool with same properties
     */
    @Override
    public Tool clone() {
        final FillTool tool = new FillTool();
        tool.setColor(color);
        tool.setStrokeWidth(strokeWidth);
        return tool;
    }
}