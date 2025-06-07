

package io.tomori.whiteboard.model.tools;

import io.tomori.whiteboard.model.Color;
import io.tomori.whiteboard.model.shapes.Shape;
import lombok.Data;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.util.List;

/**
 * Abstract class for all whiteboard drawing tools.
 * Define common properties and operations for tools that can create or manipulate shapes.
 */
@Data
public abstract class Tool {
    /**
     * stroke color used by this tool
     */
    protected Color color = Color.BLACK;
    /**
     * stroke width in pixels
     */
    protected float strokeWidth = 2.0f;

    /**
     * Create a copy of this tool.
     *
     * @return A new instance with same properties
     */
    @Override
    public abstract Tool clone();

    /**
     * Draw a temporary preview of the shape being created.
     *
     * @param g2d graphics context to draw on
     */
    public abstract void drawTemporary(final Graphics2D g2d);

    /**
     * Handle mouse press events for this tool.
     *
     * @param e      mouse event
     * @param shapes shapes list in the whiteboard
     * @return True if the event was handled
     */
    public abstract boolean mousePressed(MouseEvent e, List<Shape> shapes);

    /**
     * Handle mouse drag events for this tool.
     *
     * @param e      mouse event
     * @param shapes shapes list in the whiteboard
     * @return True if the event was handled
     */
    public abstract boolean mouseDragged(MouseEvent e, List<Shape> shapes);

    /**
     * Handle mouse release events for this tool.
     *
     * @param e      mouse event
     * @param shapes shapes list in the whiteboard
     * @return True if the event was handled
     */
    public abstract boolean mouseReleased(MouseEvent e, List<Shape> shapes);
}
