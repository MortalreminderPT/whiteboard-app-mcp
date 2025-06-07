

package io.tomori.whiteboard.model.shapes;

import io.tomori.whiteboard.constant.ShapeConstant;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.w3c.dom.Element;

import java.awt.*;

/**
 * Oval shape implementation for the whiteboard.
 * Represent an elliptical shape with center position and radii for width and height.
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class OvalShape extends Shape {
    /**
     * X-coord of the center point
     */
    private int x;
    /**
     * Y-coord of the center point
     */
    private int y;
    /**
     * Horizontal radius (half-width) of the oval
     */
    private int halfWidth;
    /**
     * Vertical radius (half-height) of the oval
     */
    private int halfHeight;

    /**
     * Create an oval with the specified center and dimensions.
     *
     * @param x          x-coord of the center
     * @param y          y-coord of the center
     * @param halfWidth  horizontal radius
     * @param halfHeight vertical radius
     */
    public OvalShape(final int x, final int y, final int halfWidth, final int halfHeight) {
        this.x = x;
        this.y = y;
        this.halfWidth = halfWidth;
        this.halfHeight = halfHeight;
        typeId = ShapeConstant.OVAL_SHAPE;
    }

    /**
     * Create an oval from an SVG element.
     *
     * @param element SVG element
     */
    public OvalShape(final Element element) {
        super(element);
        try {
            x = Integer.parseInt(element.getAttribute("cx"));
            y = Integer.parseInt(element.getAttribute("cy"));
            halfWidth = Integer.parseInt(element.getAttribute("rx"));
            halfHeight = Integer.parseInt(element.getAttribute("ry"));
            typeId = ShapeConstant.OVAL_SHAPE;
        } catch (final Exception e) {
            throw new IllegalArgumentException("Invalid SVG ellipse element", e);
        }
    }

    /**
     * Draw the oval on the graphics context.
     *
     * @param g2d graphics context
     */
    @Override
    public void draw(final Graphics2D g2d) {
        final Stroke originalStroke = g2d.getStroke();
        final Color originalColor = g2d.getColor();

        if (fill != null) {
            g2d.setColor(fill.toAwtColor());
            g2d.fillOval(x - halfWidth, y - halfHeight, halfWidth * 2, halfHeight * 2);
        }

        g2d.setStroke(new BasicStroke(strokeWidth));
        g2d.setColor(color.toAwtColor());
        g2d.drawOval(x - halfWidth, y - halfHeight, halfWidth * 2, halfHeight * 2);

        g2d.setStroke(originalStroke);
        g2d.setColor(originalColor);
    }

    /**
     * Check if a point is on the oval's perimeter.
     *
     * @param point point to check
     * @return True if the point is on the perimeter
     */
    @Override
    public boolean contains(final Point point) {
        final int tolerance = Math.max(3, (int) strokeWidth);
        final double normalizedX = (double) (point.x - x) / halfWidth;
        final double normalizedY = (double) (point.y - y) / halfHeight;
        final double distanceSquared = normalizedX * normalizedX + normalizedY * normalizedY;
        return Math.abs(distanceSquared - 1.0) * Math.min(halfWidth, halfHeight) <= tolerance;
    }

    /**
     * Move the oval by the specified delta.
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
     * Get the bounding rectangle of the oval.
     *
     * @return oval's bounding rectangle
     */
    @Override
    public Rectangle getBounds() {
        return new Rectangle(x - halfWidth, y - halfHeight, halfWidth * 2, halfHeight * 2);
    }

    /**
     * Create a copy of this oval.
     *
     * @return A new oval with same properties
     */
    @Override
    public Shape clone() {
        final OvalShape copy = new OvalShape(x, y, halfWidth, halfHeight);
        copy.setColor(color);
        copy.setStrokeWidth(strokeWidth);
        copy.setId(id);
        copy.setFill(fill);
        return copy;
    }

    /**
     * Convert the oval to an SVG string.
     *
     * @return SVG representation of the oval
     */
    @Override
    public String toSvgString() {
        final String fillValue = fill != null ? fill.toHex() : "none";
        return String.format("<ellipse cx=\"%d\" cy=\"%d\" rx=\"%d\" ry=\"%d\" stroke=\"%s\" stroke-width=\"%f\" fill=\"%s\" id=\"%s\" typeId=\"%s\"/>",
                x, y, halfWidth, halfHeight, color.toHex(), strokeWidth, fillValue, id, typeId);
    }

    /**
     * Resize the oval by adjusting its radii.
     *
     * @param dw delta width
     * @param dh delta height
     */
    @Override
    public void resize(final int dw, final int dh) {
        if (halfWidth + dw / 2 > 5) {
            halfWidth += dw / 2;
        }
        if (halfHeight + dh / 2 > 5) {
            halfHeight += dh / 2;
        }
    }
} 
