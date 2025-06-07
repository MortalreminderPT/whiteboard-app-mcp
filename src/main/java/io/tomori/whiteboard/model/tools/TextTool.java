

package io.tomori.whiteboard.model.tools;

import io.tomori.whiteboard.model.shapes.Shape;
import io.tomori.whiteboard.model.shapes.TextShape;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.util.List;

/**
 * Tool for adding text to the whiteboard.
 * Create text shapes at the clicked location after prompting for input.
 */
public class TextTool extends Tool {
    /**
     * Point where the user clicked to add text
     */
    private Point clickPoint = null;

    /**
     * Handle mouse press to record the text insertion point.
     *
     * @param e      mouse event
     * @param shapes shapes list in the whiteboard
     * @return True if the event was handled
     */
    @Override
    public boolean mousePressed(final MouseEvent e, final List<Shape> shapes) {
        clickPoint = e.getPoint();
        return true;
    }

    /**
     * Text tool doesn't respond to mouse dragging.
     *
     * @param e      mouse event
     * @param shapes shapes list in the whiteboard
     * @return Always false as dragging is not used
     */
    @Override
    public boolean mouseDragged(final MouseEvent e, final List<Shape> shapes) {
        return false;
    }

    /**
     * Handle mouse release to prompt for text and create text shape.
     *
     * @param e      mouse event
     * @param shapes shapes list in the whiteboard
     * @return True if a text shape was created
     */
    @Override
    public boolean mouseReleased(final MouseEvent e, final List<Shape> shapes) {
        if (clickPoint != null && Math.abs(e.getX() - clickPoint.x) < 5 && Math.abs(e.getY() - clickPoint.y) < 5) {
            final String text = JOptionPane.showInputDialog(null, "Enter text:", "Add Text", JOptionPane.PLAIN_MESSAGE);
            if (text != null && !text.trim().isEmpty()) {
                final TextShape textShape = new TextShape(clickPoint.x, clickPoint.y, text);
                textShape.setColor(color);
                textShape.setFontName("SansSerif");
                textShape.setFontSize(14);
                textShape.setFontStyle(Font.PLAIN);
                shapes.add(textShape);
            }
            clickPoint = null;
            return true;
        }
        return false;
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
     * @return A new TextTool with same properties
     */
    @Override
    public Tool clone() {
        final TextTool tool = new TextTool();
        tool.setColor(color);
        tool.setStrokeWidth(strokeWidth);
        return tool;
    }
} 
