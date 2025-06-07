

package io.tomori.whiteboard.model.shapes;

import io.tomori.whiteboard.constant.ShapeConstant;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.w3c.dom.Element;

import java.awt.*;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;
import java.util.ArrayList;
import java.util.List;

/**
 * Path shape implementation for the whiteboard.
 * Represent a freehand drawing as a series of connected points.
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class PathShape extends Shape {
    /**
     * Points list that make up the path
     */
    private final List<Point> points;

    /**
     * Create a path with the specified points.
     *
     * @param points list of points defining the path
     */
    public PathShape(final List<Point> points) {
        this.points = new ArrayList<>();
        for (final Point p : points) {
            this.points.add(new Point(p));
        }
        typeId = ShapeConstant.PATH_SHAPE;
    }

    /**
     * Create a path from an SVG element.
     *
     * @param element SVG element
     */
    public PathShape(final Element element) {
        super(element);
        points = new ArrayList<>();
        try {
            final String pathData = element.getAttribute("d");
            if (pathData != null && !pathData.isEmpty()) {
                final String[] commands = pathData.split(" ");
                for (int i = 0; i < commands.length; i += 3) {
                    if (i + 2 < commands.length && (commands[i].equals("M") || commands[i].equals("L"))) {
                        try {
                            final int x = Integer.parseInt(commands[i + 1]);
                            final int y = Integer.parseInt(commands[i + 2]);
                            points.add(new Point(x, y));
                        } catch (final NumberFormatException e) {
                        }
                    }
                }
            }
            typeId = ShapeConstant.PATH_SHAPE;
        } catch (final Exception e) {
            throw new IllegalArgumentException("Invalid SVG path element", e);
        }
    }

    /**
     * Draw the path on the graphics context.
     *
     * @param g2d graphics context
     */
    @Override
    public void draw(final Graphics2D g2d) {
        if (points.size() < 2) {
            return;
        }
        final Stroke originalStroke = g2d.getStroke();
        final Color originalColor = g2d.getColor();

        final GeneralPath path = new GeneralPath();
        if (!points.isEmpty()) {
            final Point firstPoint = points.getFirst();
            path.moveTo(firstPoint.x, firstPoint.y);

            for (int i = 1; i < points.size(); i++) {
                final Point point = points.get(i);
                path.lineTo(point.x, point.y);
            }
        }

        if (fill != null && points.size() >= 3) {
            path.closePath();
            g2d.setColor(fill.toAwtColor());
            g2d.fill(path);
        }

        g2d.setStroke(new BasicStroke(strokeWidth, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g2d.setColor(color.toAwtColor());
        for (int i = 0; i < points.size() - 1; i++) {
            final Point p1 = points.get(i);
            final Point p2 = points.get(i + 1);
            g2d.drawLine(p1.x, p1.y, p2.x, p2.y);
        }

        g2d.setStroke(originalStroke);
        g2d.setColor(originalColor);
    }

    /**
     * Check if a point is on the path.
     *
     * @param point point to check
     * @return True if the point is on the path
     */
    @Override
    public boolean contains(final Point point) {
        final int tolerance = Math.max(3, (int) strokeWidth);
        for (int i = 0; i < points.size() - 1; i++) {
            final Point p1 = points.get(i);
            final Point p2 = points.get(i + 1);
            final Line2D.Double line = new Line2D.Double(p1, p2);
            if (line.ptSegDist(point) <= tolerance) {
                return true;
            }
        }
        return false;
    }

    /**
     * Move the path by the specified delta.
     *
     * @param dx x-axis distance
     * @param dy y-axis distance
     */
    @Override
    public void move(final int dx, final int dy) {
        for (final Point p : points) {
            p.translate(dx, dy);
        }
    }

    /**
     * Get the bounding rectangle of the path.
     *
     * @return path's bounding rectangle
     */
    @Override
    public Rectangle getBounds() {
        if (points.isEmpty()) {
            return new Rectangle();
        }
        int minX = Integer.MAX_VALUE;
        int minY = Integer.MAX_VALUE;
        int maxX = Integer.MIN_VALUE;
        int maxY = Integer.MIN_VALUE;
        for (final Point p : points) {
            minX = Math.min(minX, p.x);
            minY = Math.min(minY, p.y);
            maxX = Math.max(maxX, p.x);
            maxY = Math.max(maxY, p.y);
        }
        return new Rectangle(minX, minY, maxX - minX, maxY - minY);
    }

    /**
     * Create a copy of this path.
     *
     * @return A new path with same properties
     */
    @Override
    public Shape clone() {
        final PathShape copy = new PathShape(new ArrayList<>(points));
        copy.setColor(color);
        copy.setStrokeWidth(strokeWidth);
        copy.setId(id);
        copy.setFill(fill);
        return copy;
    }

    /**
     * Convert the path to an SVG string.
     *
     * @return SVG representation of the path
     */
    @Override
    public String toSvgString() {
        final StringBuilder pathData = new StringBuilder();
        if (!points.isEmpty()) {
            final Point first = points.getFirst();
            pathData.append(String.format("M %d %d", first.x, first.y));
            for (int i = 1; i < points.size(); i++) {
                final Point p = points.get(i);
                pathData.append(String.format(" L %d %d", p.x, p.y));
            }

            if (fill != null && points.size() >= 3) {
                pathData.append(" Z");
            }
        }

        final String fillValue = fill != null ? fill.toHex() : "none";
        return String.format("<path d=\"%s\" stroke=\"%s\" stroke-width=\"%f\" fill=\"%s\" id=\"%s\" typeId=\"%s\"/>",
                pathData, color.toHex(), strokeWidth, fillValue, id, typeId);
    }

    /**
     * Resize the path by scaling all points.
     *
     * @param dw delta width
     * @param dh delta height
     */
    @Override
    public void resize(final int dw, final int dh) {
        if (points.isEmpty()) {
            return;
        }
        final Rectangle bounds = getBounds();
        if (bounds.width + dw < 10 || bounds.height + dh < 10) {
            return;
        }
        final int centerX = bounds.x + bounds.width / 2;
        final int centerY = bounds.y + bounds.height / 2;
        final double widthRatio = bounds.width == 0 ? 1.0 : (double) (bounds.width + dw) / bounds.width;
        final double heightRatio = bounds.height == 0 ? 1.0 : (double) (bounds.height + dh) / bounds.height;
        for (final Point p : points) {
            p.x = centerX + (int) ((p.x - centerX) * widthRatio);
            p.y = centerY + (int) ((p.y - centerY) * heightRatio);
        }
    }
} 
