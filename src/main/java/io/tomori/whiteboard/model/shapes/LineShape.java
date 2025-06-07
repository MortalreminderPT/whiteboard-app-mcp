

package io.tomori.whiteboard.model.shapes;

import io.tomori.whiteboard.constant.ShapeConstant;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.w3c.dom.Element;

import java.awt.*;
import java.awt.geom.Line2D;

/**
 * Line shape implementation for the whiteboard.
 * Represent a straight line between two points.
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class LineShape extends Shape {
    /**
     * X-coord of the start point
     */
    private int x1;
    /**
     * Y-coord of the start point
     */
    private int y1;
    /**
     * X-coord of the end point
     */
    private int x2;
    /**
     * Y-coord of the end point
     */
    private int y2;

    /**
     * Create a line with the specified endpoints.
     *
     * @param x1 x-coord of the start point
     * @param y1 y-coord of the start point
     * @param x2 x-coord of the end point
     * @param y2 y-coord of the end point
     */
    public LineShape(final int x1, final int y1, final int x2, final int y2) {
        this.x1 = x1;
        this.y1 = y1;
        this.x2 = x2;
        this.y2 = y2;
        typeId = ShapeConstant.LINE_SHAPE;
    }

    /**
     * Create a line from an SVG element.
     *
     * @param element SVG element
     */
    public LineShape(final Element element) {
        super(element);
        try {
            x1 = Integer.parseInt(element.getAttribute("x1"));
            y1 = Integer.parseInt(element.getAttribute("y1"));
            x2 = Integer.parseInt(element.getAttribute("x2"));
            y2 = Integer.parseInt(element.getAttribute("y2"));
            typeId = ShapeConstant.LINE_SHAPE;
        } catch (final Exception e) {
            throw new IllegalArgumentException("Invalid SVG line element", e);
        }
    }

    /**
     * Draw the line on the graphics context.
     *
     * @param g2d graphics context
     */
    @Override
    public void draw(final Graphics2D g2d) {
        final Stroke originalStroke = g2d.getStroke();
        final Color originalColor = g2d.getColor();
        g2d.setStroke(new BasicStroke(strokeWidth));
        g2d.setColor(color.toAwtColor());
        g2d.drawLine(x1, y1, x2, y2);
        g2d.setStroke(originalStroke);
        g2d.setColor(originalColor);
    }

    /**
     * Check if a point is on the line.
     *
     * @param point point to check
     * @return True if the point is on the line
     */
    @Override
    public boolean contains(final Point point) {
        final int tolerance = Math.max(3, (int) strokeWidth);
        final Line2D.Double line = new Line2D.Double(x1, y1, x2, y2);
        return line.ptSegDist(point) <= tolerance;
    }

    /**
     * Move the line by the specified delta.
     *
     * @param dx x-axis distance
     * @param dy y-axis distance
     */
    @Override
    public void move(final int dx, final int dy) {
        x1 += dx;
        y1 += dy;
        x2 += dx;
        y2 += dy;
    }

    /**
     * Get the bounding rectangle of the line.
     *
     * @return line's bounding rectangle
     */
    @Override
    public Rectangle getBounds() {
        return new Rectangle(
                Math.min(x1, x2),
                Math.min(y1, y2),
                Math.abs(x2 - x1),
                Math.abs(y2 - y1)
        );
    }

    /**
     * Create a copy of this line.
     *
     * @return A new line with same properties
     */
    @Override
    public Shape clone() {
        final LineShape copy = new LineShape(x1, y1, x2, y2);
        copy.setColor(color);
        copy.setStrokeWidth(strokeWidth);
        copy.setId(id);
        copy.setFill(fill);
        return copy;
    }

    /**
     * Convert the line to an SVG string.
     *
     * @return SVG representation of the line
     */
    @Override
    public String toSvgString() {
        return String.format("<line x1=\"%d\" y1=\"%d\" x2=\"%d\" y2=\"%d\" stroke=\"%s\" stroke-width=\"%f\" id=\"%s\" typeId=\"%s\"/>",
                x1, y1, x2, y2, color.toHex(), strokeWidth, id, typeId);
    }

    /**
     * Resize the line by moving its endpoint.
     *
     * @param dw delta width
     * @param dh delta height
     */
    @Override
    public void resize(final int dw, final int dh) {
        if (x2 >= x1) {
            x2 += dw;
        } else {
            x2 -= dw;
        }
        if (y2 >= y1) {
            y2 += dh;
        } else {
            y2 -= dh;
        }
    }
} 
