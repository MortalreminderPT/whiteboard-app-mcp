

package io.tomori.whiteboard.model.shapes;

import io.tomori.whiteboard.constant.ShapeConstant;
import io.tomori.whiteboard.model.Color;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.w3c.dom.Element;

import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.font.TextLayout;
import java.awt.geom.Rectangle2D;

/**
 * Text shape implementation for the whiteboard.
 * Represent a text element with position and font attributes.
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class TextShape extends Shape {
    /**
     * X-coord of the text position
     */
    private int x, y;
    /**
     * text content
     */
    private String text;
    /**
     * Name of the font
     */
    private String fontName;
    /**
     * Size of the font in pixels
     */
    private int fontSize;
    /**
     * Style of the font (PLAIN, BOLD, ITALIC)
     */
    private int fontStyle;

    /**
     * Create a text shape at the specified position.
     *
     * @param x    x-coord
     * @param y    y-coord
     * @param text text content
     */
    public TextShape(final int x, final int y, final String text) {
        this.x = x;
        this.y = y;
        this.text = text;
        fontName = "SansSerif";
        fontSize = 14;
        fontStyle = Font.PLAIN;
        typeId = ShapeConstant.TEXT_SHAPE;
    }

    /**
     * Create a text shape from an SVG element.
     *
     * @param element SVG element
     */
    public TextShape(final Element element) {
        super(element);
        try {
            color = Color.fromHex(element.getAttribute("color"));
            x = Integer.parseInt(element.getAttribute("x"));
            y = Integer.parseInt(element.getAttribute("y"));
            text = element.getTextContent();
            if (text == null) {
                text = "";
            }
            final String fontFamily = element.getAttribute("font-family");
            if (fontFamily != null && !fontFamily.isEmpty()) {
                fontName = fontFamily;
            } else {
                fontName = "SansSerif";
            }
            final String fontSize = element.getAttribute("font-size");
            if (fontSize != null && !fontSize.isEmpty()) {
                try {
                    this.fontSize = Integer.parseInt(fontSize);
                } catch (final NumberFormatException e) {
                    this.fontSize = 14;
                }
            } else {
                this.fontSize = 14;
            }
            fontStyle = Font.PLAIN;
            typeId = "TEXT_SHAPE";
        } catch (final Exception e) {
            throw new IllegalArgumentException("Invalid SVG text element", e);
        }
    }

    /**
     * Draw the text on the graphics context.
     *
     * @param g2d graphics context
     */
    @Override
    public void draw(final Graphics2D g2d) {
        if (fill != null) {
            g2d.setColor(fill.toAwtColor());
        } else {
            g2d.setColor(color.toAwtColor());
        }

        g2d.setFont(new Font(fontName, fontStyle, fontSize));
        g2d.drawString(text, x, y);
    }

    /**
     * Check if a point is within the text bounds.
     *
     * @param point point to check
     * @return True if the point is within the text bounds
     */
    @Override
    public boolean contains(final Point point) {
        final Rectangle bounds = getBounds();
        bounds.grow(5, 5);
        return bounds.contains(point);
    }

    /**
     * Move the text by the specified delta.
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
     * Get the bounding rectangle of the text.
     *
     * @return text's bounding rectangle
     */
    @Override
    public Rectangle getBounds() {
        final FontRenderContext frc = new FontRenderContext(null, true, true);
        final Font font = new Font(fontName, fontStyle, fontSize);
        final TextLayout layout = new TextLayout(text, font, frc);
        final Rectangle2D bounds = layout.getBounds();
        return new Rectangle(
                x,
                y - (int) bounds.getHeight(),
                (int) bounds.getWidth(),
                (int) bounds.getHeight()
        );
    }

    /**
     * Create a copy of this text shape.
     *
     * @return A new text shape with same properties
     */
    @Override
    public Shape clone() {
        final TextShape copy = new TextShape(x, y, text);
        copy.setFontName(fontName);
        copy.setFontSize(fontSize);
        copy.setFontStyle(fontStyle);
        copy.setColor(color);
        copy.setStrokeWidth(strokeWidth);
        copy.setId(id);
        copy.setFill(fill);
        return copy;
    }

    /**
     * Convert the text to an SVG string.
     *
     * @return SVG representation of the text
     */
    @Override
    public String toSvgString() {
        final String escapedText = text.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&apos;");

        final String fillValue = fill != null ? fill.toHex() : color.toHex();

        return String.format("<text x=\"%d\" y=\"%d\" font-family=\"%s\" font-size=\"%d\" fill=\"%s\" id=\"%s\" typeId=\"%s\" color=\"%s\">%s</text>",
                x, y, fontName, fontSize, fillValue, id, typeId, color.toHex(), escapedText);
    }

    /**
     * Resize the text by adjusting the font size.
     *
     * @param dw delta width
     * @param dh delta height
     */
    @Override
    public void resize(final int dw, final int dh) {
        final Rectangle bounds = getBounds();
        if (bounds.width == 0) {
            return;
        }
        final double ratio = (bounds.width + dw) / (double) bounds.width;
        final int newFontSize = (int) (fontSize * ratio);
        if (newFontSize >= 8 && newFontSize <= 72) {
            fontSize = newFontSize;
        }
    }
} 
