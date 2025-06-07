

package io.tomori.whiteboard.model.shapes;

import io.tomori.whiteboard.constant.ShapeConstant;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.w3c.dom.Element;

import java.awt.*;

/**
 * Circle shape implementation for the whiteboard.
 * Represent a circle with center position and radius.
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class CircleShape extends Shape {
    /**
     * X-coord of the center point
     */
    private int x;
    /**
     * Y-coord of the center point
     */
    private int y;
    /**
     * Radius of the circle
     */
    private int radius;

    /**
     * Create a circle with the specified center and radius.
     *
     * @param x      x-coord of the center
     * @param y      y-coord of the center
     * @param radius radius of the circle
     */
    public CircleShape(final int x, final int y, final int radius) {
        this.x = x;
        this.y = y;
        this.radius = radius;
        typeId = ShapeConstant.CIRCLE_SHAPE;
    }

    /**
     * Create a circle from an SVG element.
     *
     * @param element SVG element
     */
    public CircleShape(final Element element) {
        super(element);
        try {
            x = Integer.parseInt(element.getAttribute("cx"));
            y = Integer.parseInt(element.getAttribute("cy"));
            radius = Integer.parseInt(element.getAttribute("r"));
            typeId = ShapeConstant.CIRCLE_SHAPE;
        } catch (final Exception e) {
            throw new IllegalArgumentException("Invalid SVG circle element", e);
        }
    }

    /**
     * Draw the circle on the graphics context.
     *
     * @param g2d graphics context
     */
    @Override
    public void draw(final Graphics2D g2d) {
        final Stroke originalStroke = g2d.getStroke();
        final Color originalColor = g2d.getColor();

        if (fill != null) {
            g2d.setColor(fill.toAwtColor());
            g2d.fillOval(x - radius, y - radius, radius * 2, radius * 2);
        }

        g2d.setStroke(new BasicStroke(strokeWidth));
        g2d.setColor(color.toAwtColor());
        g2d.drawOval(x - radius, y - radius, radius * 2, radius * 2);

        g2d.setStroke(originalStroke);
        g2d.setColor(originalColor);
    }

    /**
     * Check if a point is on the circle's perimeter.
     *
     * @param point point to check
     * @return True if the point is on the perimeter
     */
    @Override
    public boolean contains(final Point point) {
        final double distance = Math.sqrt(
                Math.pow(point.x - x, 2) +
                        Math.pow(point.y - y, 2)
        );
        final int tolerance = Math.max(3, (int) strokeWidth);
        return Math.abs(distance - radius) <= tolerance;
    }

    /**
     * Move the circle by the specified delta.
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
     * Get the bounding rectangle of the circle.
     *
     * @return circle's bounding rectangle
     */
    @Override
    public Rectangle getBounds() {
        return new Rectangle(x - radius, y - radius, radius * 2, radius * 2);
    }

    /**
     * Create a copy of this circle.
     *
     * @return A new circle with same properties
     */
    @Override
    public Shape clone() {
        final CircleShape copy = new CircleShape(x, y, radius);
        copy.setColor(color);
        copy.setStrokeWidth(strokeWidth);
        copy.setId(id);
        copy.setFill(fill);
        return copy;
    }

    /**
     * Convert the circle to an SVG string.
     *
     * @return SVG representation of the circle
     */
    @Override
    public String toSvgString() {
        final String fillValue = fill != null ? fill.toHex() : "none";
        return String.format("<circle cx=\"%d\" cy=\"%d\" r=\"%d\" stroke=\"%s\" stroke-width=\"%f\" fill=\"%s\" id=\"%s\" typeId=\"%s\"/>",
                x, y, radius, color.toHex(), strokeWidth, fillValue, id, typeId);
    }

    /**
     * Resize the circle by adjusting its radius.
     *
     * @param dw delta width
     * @param dh delta height
     */
    @Override
    public void resize(final int dw, final int dh) {
        int dr = (Math.abs(dw) + Math.abs(dh)) / 2;
        if (dw < 0 || dh < 0) {
            dr = -dr;
        }
        if (radius + dr > 5) {
            radius += dr;
        }
    }
} 
