

package io.tomori.whiteboard.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * Represent RGB color in the whiteboard app.
 * Provide predefined colors and conversions between formats.
 */
@Data
@AllArgsConstructor
public class Color implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    /**
     * black color (0,0,0)
     */
    public static Color BLACK = new Color(0, 0, 0);
    /**
     * white color (255,255,255)
     */
    public static Color WHITE = new Color(255, 255, 255);
    /**
     * dark red color (128,0,0)
     */
    public static Color DARK_RED = new Color(128, 0, 0);
    /**
     * red color (255,0,0)
     */
    public static Color RED = new Color(255, 0, 0);
    /**
     * dark green color (0,128,0)
     */
    public static Color DARK_GREEN = new Color(0, 128, 0);
    /**
     * green color (0,255,0)
     */
    public static Color GREEN = new Color(0, 255, 0);
    /**
     * dark blue color (0,0,128)
     */
    public static Color DARK_BLUE = new Color(0, 0, 128);
    /**
     * blue color (0,0,255)
     */
    public static Color BLUE = new Color(0, 0, 255);
    /**
     * dark yellow color (128,128,0)
     */
    public static Color DARK_YELLOW = new Color(128, 128, 0);
    /**
     * yellow color (255,255,0)
     */
    public static Color YELLOW = new Color(255, 255, 0);
    /**
     * dark cyan color (0,128,128)
     */
    public static Color DARK_CYAN = new Color(0, 128, 128);
    /**
     * cyan color (0,255,255)
     */
    public static Color CYAN = new Color(0, 255, 255);
    /**
     * purple color (128,0,128)
     */
    public static Color PURPLE = new Color(128, 0, 128);
    /**
     * magenta color (255,0,255)
     */
    public static Color MAGENTA = new Color(255, 0, 255);
    /**
     * gray color (128,128,128)
     */
    public static Color GRAY = new Color(128, 128, 128);
    /**
     * light gray color (192,192,192)
     */
    public static Color LIGHT_GRAY = new Color(192, 192, 192);
    /**
     * Red component (0-255)
     */
    private int r = 0;
    /**
     * Green component (0-255)
     */
    private int g = 0;
    /**
     * Blue component (0-255)
     */
    private int b = 0;

    /**
     * Create Color from AWT color.
     *
     * @param color AWT color to convert
     */
    public Color(final java.awt.Color color) {
        r = color.getRed();
        g = color.getGreen();
        b = color.getBlue();
    }

    /**
     * Create a Color from a hex string.
     *
     * @param hex hex string ("#FFFFFF" or "FFFFFF")
     * @return corresponding Color object
     */
    public static Color fromHex(String hex) {
        if (hex == null || hex.isEmpty()) {
            return Color.BLACK;
        }
        if (hex.startsWith("#")) {
            hex = hex.substring(1);
        }
        if (hex.length() < 6) {
            return Color.BLACK;
        }
        try {
            final int r = Integer.parseInt(hex.substring(0, 2), 16);
            final int g = Integer.parseInt(hex.substring(2, 4), 16);
            final int b = Integer.parseInt(hex.substring(4, 6), 16);
            return new Color(r, g, b);
        } catch (final NumberFormatException e) {
            return Color.BLACK;
        }
    }

    /**
     * Convert to an AWT color.
     *
     * @return equivalent AWT color.
     */
    public java.awt.Color toAwtColor() {
        return new java.awt.Color(r, g, b);
    }

    /**
     * Convert to a hex string.
     *
     * @return color in hex format.
     */
    public String toHex() {
        return String.format("#%02x%02x%02x", r, g, b);
    }
}
