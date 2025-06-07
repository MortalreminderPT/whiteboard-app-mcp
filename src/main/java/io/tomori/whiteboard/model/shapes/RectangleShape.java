

package io.tomori.whiteboard.model.shapes;

import io.tomori.whiteboard.constant.ShapeConstant;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.w3c.dom.Element;

import java.awt.*;

/**
 * Rectangle shape implementation for the whiteboard.
 * Represent a rectangular region with position, width, and height.
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class RectangleShape extends Shape {
    /**
     * X-coord of the top-left corner
     */
    private int x;
    /**
     * Y-coord of the top-left corner
     */
    private int y;
    /**
     * Width of the rectangle
     */
    private int width;
    /**
     * Height of the rectangle
     */
    private int height;

    /**
     * Create a rectangle with the specified dimensions.
     *
     * @param x      x-coord
     * @param y      y-coord
     * @param width  width
     * @param height height
     */
    public RectangleShape(final int x, final int y, final int width, final int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        typeId = ShapeConstant.RECTANGLE_SHAPE;
    }

    /**
     * Create a rectangle from an SVG element.
     *
     * @param element SVG element
     */
    public RectangleShape(final Element element) {
        super(element);
        try {
            x = Integer.parseInt(element.getAttribute("x"));
            y = Integer.parseInt(element.getAttribute("y"));
            width = Integer.parseInt(element.getAttribute("width"));
            height = Integer.parseInt(element.getAttribute("height"));
            typeId = ShapeConstant.RECTANGLE_SHAPE;
        } catch (final Exception e) {
            throw new IllegalArgumentException("Invalid SVG rectangle element", e);
        }
    }

    /**
     * Draw the rectangle on the graphics context.
     *
     * @param g2d graphics context
     */
    @Override
    public void draw(final Graphics2D g2d) {
        final Stroke originalStroke = g2d.getStroke();
        final Color originalColor = g2d.getColor();

        if (fill != null) {
            g2d.setColor(fill.toAwtColor());
            g2d.fillRect(x, y, width, height);
        }

        g2d.setStroke(new BasicStroke(strokeWidth));
        g2d.setColor(color.toAwtColor());
        g2d.drawRect(x, y, width, height);

        g2d.setStroke(originalStroke);
        g2d.setColor(originalColor);
    }

    /**
     * Check if a point is on the rectangle's border.
     *
     * @param point point to check
     * @return True if the point is on the border
     */
    @Override
    public boolean contains(final Point point) {
        final int tolerance = Math.max(3, (int) strokeWidth);
        final int left = x - tolerance;
        final int right = x + width + tolerance;
        final int top = y - tolerance;
        final int bottom = y + height + tolerance;
        if (point.x >= left && point.x <= right && point.y >= top && point.y <= bottom) {
            return point.x <= x + tolerance || point.x >= x + width - tolerance ||
                    point.y <= y + tolerance || point.y >= y + height - tolerance;
        }
        return false;
    }

    /**
     * Move the rectangle by the specified delta.
     *
     * @param dx x-axis distance
     * @param dy y-axis distance
     */
    @Override
    public void move(final int dx, final int dy) {
        x += dx;
        y += dy;
    }

    /**
     * Get the bounding rectangle.
     *
     * @return rectangle as a bounding box
     */
    @Override
    public Rectangle getBounds() {
        return new Rectangle(x, y, width, height);
    }

    /**
     * Create a copy of this rectangle.
     *
     * @return A new rectangle with same properties
     */
    @Override
    public Shape clone() {
        final RectangleShape copy = new RectangleShape(x, y, width, height);
        copy.setColor(color);
        copy.setStrokeWidth(strokeWidth);
        copy.setId(id);
        copy.setFill(fill);
        return copy;
    }

    /**
     * Convert the rectangle to an SVG string.
     *
     * @return SVG representation of the rectangle
     */
    @Override
    public String toSvgString() {
        final String fillValue = fill != null ? fill.toHex() : "none";
        return String.format("<rect x=\"%d\" y=\"%d\" width=\"%d\" height=\"%d\" stroke=\"%s\" stroke-width=\"%f\" fill=\"%s\" id=\"%s\" typeId=\"%s\"/>",
                x, y, width, height, color.toHex(), strokeWidth, fillValue, id, typeId);
    }

    /**
     * Resize the rectangle by the specified deltas.
     *
     * @param dw delta width
     * @param dh delta height
     */
    @Override
    public void resize(final int dw, final int dh) {
        if (width + dw > 0) {
            width += dw;
        }
        if (height + dh > 0) {
            height += dh;
        }
    }
} 
