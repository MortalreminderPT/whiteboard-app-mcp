

package io.tomori.whiteboard.model.shapes;

import com.google.gson.internal.LinkedTreeMap;
import io.tomori.whiteboard.constant.ShapeConstant;
import io.tomori.whiteboard.model.Color;
import io.tomori.whiteboard.util.JsonUtil;
import lombok.Data;
import org.w3c.dom.Element;

import java.awt.*;
import java.io.Serial;
import java.io.Serializable;
import java.util.UUID;

/**
 * Abstract class for all whiteboard shapes.
 * Define common properties and operations for drawable elements.
 */
@Data
public abstract class Shape implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    /**
     * stroke color of the shape
     */
    protected Color color = Color.BLACK;
    /**
     * fill color of the shape, null means no fill
     */
    protected Color fill = null;
    /**
     * width of the stroke in pixels
     */
    protected float strokeWidth = 2.0f;
    /**
     * Unique identifier for the shape
     */
    protected String id;
    /**
     * Type identifier to distinguish shape types
     */
    protected String typeId = ShapeConstant.ABSTRACT_SHAPE;

    /**
     * Create a new shape with a random UUID.
     */
    public Shape() {
        id = UUID.randomUUID().toString();
    }

    /**
     * Create a shape with the specified ID.
     *
     * @param id shape identifier
     */
    public Shape(final String id) {
        this.id = id;
    }

    /**
     * Create a shape from an SVG element.
     *
     * @param svgElement SVG element containing shape data
     */
    public Shape(final Element svgElement) {
        id = svgElement.getAttribute("id");
        typeId = svgElement.getAttribute("typeId");
        try {
            color = Color.fromHex(svgElement.getAttribute("stroke"));
        } catch (final Exception e) {
            System.out.println("Error parsing stroke color: " + e.getMessage());
            color = Color.BLACK;
        }
        try {
            final String fillColorAttr = svgElement.getAttribute("fill");
            if (!"none".equals(fillColorAttr)) {
                fill = Color.fromHex(fillColorAttr);
            }
        } catch (final Exception e) {
            System.out.println("Error parsing fill color: " + e.getMessage());
            fill = null;
        }
        try {
            strokeWidth = Float.parseFloat(svgElement.getAttribute("stroke-width"));
        } catch (final Exception e) {
            System.out.println("Error parsing stroke width: " + e.getMessage());
            strokeWidth = 2.0f;
        }
    }

    /**
     * Create a shape from a LinkedTreeMap representation.
     *
     * @param map map containing shape data
     * @return A concrete Shape instance based on the typeId
     */
    public static Shape fromLinkedTreeMap(final LinkedTreeMap<?, ?> map) {
        final String mapJson = JsonUtil.toJson(map);
        return switch (map.get("typeId").toString()) {
            case ShapeConstant.CIRCLE_SHAPE -> JsonUtil.fromJson(mapJson, CircleShape.class);
            case ShapeConstant.RECTANGLE_SHAPE -> JsonUtil.fromJson(mapJson, RectangleShape.class);
            case ShapeConstant.PATH_SHAPE -> JsonUtil.fromJson(mapJson, PathShape.class);
            case ShapeConstant.TRIANGLE_SHAPE -> JsonUtil.fromJson(mapJson, TriangleShape.class);
            case ShapeConstant.TEXT_SHAPE -> JsonUtil.fromJson(mapJson, TextShape.class);
            case ShapeConstant.LINE_SHAPE -> JsonUtil.fromJson(mapJson, LineShape.class);
            case ShapeConstant.OVAL_SHAPE -> JsonUtil.fromJson(mapJson, OvalShape.class);
            default -> throw new IllegalArgumentException("Invalid typeId: " + map.get("typeId"));
        };
    }

    /**
     * Set the shape's unique identifier.
     *
     * @param id new identifier
     */
    protected void setId(final String id) {
        this.id = id;
    }

    /**
     * Create a copy of this shape.
     *
     * @return A new instance with same properties
     */
    @Override
    public abstract Shape clone();

    /**
     * Draw the shape on a graphics context.
     *
     * @param g graphics context to draw on
     */
    public abstract void draw(Graphics2D g);

    /**
     * Check if the shape contains a point.
     *
     * @param point point to check
     * @return True if the shape contains the point
     */
    public abstract boolean contains(Point point);

    /**
     * Move the shape by the specified delta.
     *
     * @param dx x-axis distance to move
     * @param dy y-axis distance to move
     */
    public abstract void move(int dx, int dy);

    /**
     * Get the bounding rectangle of the shape.
     *
     * @return shape's bounding rectangle
     */
    public abstract Rectangle getBounds();

    /**
     * Convert the shape to an SVG string representation.
     *
     * @return SVG string representation
     */
    public abstract String toSvgString();

    /**
     * Resize the shape by the specified deltas.
     *
     * @param dw delta width
     * @param dh delta height
     */
    public abstract void resize(int dw, int dh);
}
