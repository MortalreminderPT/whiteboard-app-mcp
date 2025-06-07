

package io.tomori.whiteboard.model.shapes;

import io.tomori.whiteboard.constant.ShapeConstant;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.w3c.dom.Element;

import java.awt.*;
import java.awt.geom.Path2D;

/**
 * Triangle shape implementation for the whiteboard.
 * Represent a triangle defined by three points.
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class TriangleShape extends Shape {
    /**
     * First vertex of the triangle
     */
    private Point p1;
    /**
     * Second vertex of the triangle
     */
    private Point p2;
    /**
     * Third vertex of the triangle
     */
    private Point p3;

    /**
     * Create a triangle with the specified vertices.
     *
     * @param p1 first vertex
     * @param p2 second vertex
     * @param p3 third vertex
     */
    public TriangleShape(final Point p1, final Point p2, final Point p3) {
        this.p1 = new Point(p1);
        this.p2 = new Point(p2);
        this.p3 = new Point(p3);
        typeId = ShapeConstant.TRIANGLE_SHAPE;
    }

    /**
     * Create a triangle from an SVG element.
     *
     * @param element SVG element
     */
    public TriangleShape(final Element element) {
        super(element);
        try {
            final String pointsStr = element.getAttribute("points");
            if (pointsStr != null && !pointsStr.isEmpty()) {
                final String[] points = pointsStr.split(" ");
                if (points.length >= 3) {
                    final String[] p1Coords = points[0].split(",");
                    final String[] p2Coords = points[1].split(",");
                    final String[] p3Coords = points[2].split(",");
                    if (p1Coords.length >= 2 && p2Coords.length >= 2 && p3Coords.length >= 2) {
                        p1 = new Point(
                                Integer.parseInt(p1Coords[0]),
                                Integer.parseInt(p1Coords[1])
                        );
                        p2 = new Point(
                                Integer.parseInt(p2Coords[0]),
                                Integer.parseInt(p2Coords[1])
                        );
                        p3 = new Point(
                                Integer.parseInt(p3Coords[0]),
                                Integer.parseInt(p3Coords[1])
                        );
                    } else {
                        throw new IllegalArgumentException("Invalid polygon points format");
                    }
                } else {
                    throw new IllegalArgumentException("Insufficient points for triangle");
                }
            } else {
                throw new IllegalArgumentException("Missing points attribute");
            }
            typeId = "TRIANGLE_SHAPE";
        } catch (final Exception e) {
            throw new IllegalArgumentException("Invalid SVG triangle element", e);
        }
    }

    /**
     * Check if a point is near a line segment within the given tolerance.
     *
     * @param start     start point of the line
     * @param end       end point of the line
     * @param point     point to check
     * @param tolerance maximum distance considered "near"
     * @return True if the point is near the line
     */
    private static boolean isPointNearLine(final Point start, final Point end, final Point point, final int tolerance) {
        final double lineLength = start.distance(end);
        if (lineLength == 0) {
            return point.distance(start) <= tolerance;
        }
        final double u = ((point.x - start.x) * (end.x - start.x) + (point.y - start.y) * (end.y - start.y)) /
                (lineLength * lineLength);
        if (u < 0 || u > 1) {
            final double distToStart = point.distance(start);
            final double distToEnd = point.distance(end);
            return Math.min(distToStart, distToEnd) <= tolerance;
        } else {
            final double px = start.x + u * (end.x - start.x);
            final double py = start.y + u * (end.y - start.y);
            final double distance = point.distance(new Point((int) px, (int) py));
            return distance <= tolerance;
        }
    }

    /**
     * Draw the triangle on the graphics context.
     *
     * @param g2d graphics context
     */
    @Override
    public void draw(final Graphics2D g2d) {
        final Stroke originalStroke = g2d.getStroke();
        final Color originalColor = g2d.getColor();
        final Path2D path = createTrianglePath();

        if (fill != null) {
            g2d.setColor(fill.toAwtColor());
            g2d.fill(path);
        }

        g2d.setStroke(new BasicStroke(strokeWidth));
        g2d.setColor(color.toAwtColor());
        g2d.draw(path);

        g2d.setStroke(originalStroke);
        g2d.setColor(originalColor);
    }

    /**
     * Create a Path2D representation of the triangle.
     *
     * @return triangle as a Path2D object
     */
    private Path2D createTrianglePath() {
        final Path2D path = new Path2D.Double();
        path.moveTo(p1.x, p1.y);
        path.lineTo(p2.x, p2.y);
        path.lineTo(p3.x, p3.y);
        path.closePath();
        return path;
    }

    /**
     * Check if a point is on the triangle's perimeter.
     *
     * @param point point to check
     * @return True if the point is on the perimeter
     */
    @Override
    public boolean contains(final Point point) {
        final int tolerance = Math.max(3, (int) strokeWidth);
        return isPointNearLine(p1, p2, point, tolerance) ||
                isPointNearLine(p2, p3, point, tolerance) ||
                isPointNearLine(p3, p1, point, tolerance);
    }

    /**
     * Move the triangle by the specified delta.
     *
     * @param dx x-axis distance
     * @param dy y-axis distance
     */
    @Override
    public void move(final int dx, final int dy) {
        p1.translate(dx, dy);
        p2.translate(dx, dy);
        p3.translate(dx, dy);
    }

    /**
     * Get the bounding rectangle of the triangle.
     *
     * @return triangle's bounding rectangle
     */
    @Override
    public Rectangle getBounds() {
        final int minX = Math.min(Math.min(p1.x, p2.x), p3.x);
        final int minY = Math.min(Math.min(p1.y, p2.y), p3.y);
        final int maxX = Math.max(Math.max(p1.x, p2.x), p3.x);
        final int maxY = Math.max(Math.max(p1.y, p2.y), p3.y);
        return new Rectangle(minX, minY, maxX - minX, maxY - minY);
    }

    /**
     * Create a copy of this triangle.
     *
     * @return A new triangle with same properties
     */
    @Override
    public Shape clone() {
        final TriangleShape copy = new TriangleShape(p1, p2, p3);
        copy.setColor(color);
        copy.setStrokeWidth(strokeWidth);
        copy.setId(id);
        copy.setFill(fill);
        return copy;
    }

    /**
     * Convert the triangle to an SVG string.
     *
     * @return SVG representation of the triangle
     */
    @Override
    public String toSvgString() {
        final String pointsStr = String.format("%d,%d %d,%d %d,%d",
                p1.x, p1.y, p2.x, p2.y, p3.x, p3.y);
        final String fillValue = fill != null ? fill.toHex() : "none";
        return String.format("<polygon points=\"%s\" stroke=\"%s\" stroke-width=\"%f\" fill=\"%s\" id=\"%s\" typeId=\"%s\"/>",
                pointsStr, color.toHex(), strokeWidth, fillValue, id, typeId);
    }

    /**
     * Resize the triangle by scaling its vertices.
     *
     * @param dw delta width
     * @param dh delta height
     */
    @Override
    public void resize(final int dw, final int dh) {
        final Rectangle bounds = getBounds();
        if (bounds.width + dw < 10 || bounds.height + dh < 10) {
            return;
        }
        final int centerX = bounds.x + bounds.width / 2;
        final int centerY = bounds.y + bounds.height / 2;
        final double widthRatio = (double) (bounds.width + dw) / bounds.width;
        final double heightRatio = (double) (bounds.height + dh) / bounds.height;
        p1.x = centerX + (int) ((p1.x - centerX) * widthRatio);
        p1.y = centerY + (int) ((p1.y - centerY) * heightRatio);
        p2.x = centerX + (int) ((p2.x - centerX) * widthRatio);
        p2.y = centerY + (int) ((p2.y - centerY) * heightRatio);
        p3.x = centerX + (int) ((p3.x - centerX) * widthRatio);
        p3.y = centerY + (int) ((p3.y - centerY) * heightRatio);
    }
}
