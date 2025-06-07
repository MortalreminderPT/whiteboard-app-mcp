

package io.tomori.whiteboard.gui.panels;

import io.tomori.whiteboard.model.shapes.Shape;
import io.tomori.whiteboard.model.tools.EraserTool;
import io.tomori.whiteboard.service.WhiteboardService;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.util.Base64;

/**
 * Panel for drawing and rendering shapes on the whiteboard canvas.
 * Handle mouse events for drawing operations and render the current state of shapes.
 */
public class DrawingPanel extends JPanel {
    /**
     * Singleton instance of the DrawingPanel
     */
    private static DrawingPanel instance;
    /**
     * Service for managing whiteboard operations
     */
    private final WhiteboardService whiteboardService;

    /**
     * Private constructor for singleton pattern implementation.
     */
    private DrawingPanel() {
        whiteboardService = WhiteboardService.getInstance();
        setBackground(Color.WHITE);
        setPreferredSize(new Dimension(800, 600));
        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(final MouseEvent e) {
                if (ToolPanel.getInstance().getCurrentActiveTool() != null) {
                    whiteboardService.saveState();
                    if (ToolPanel.getInstance().getCurrentActiveTool().mousePressed(e, whiteboardService.getShapes())) {
                        repaint();
                    }
                }
            }

            @Override
            public void mouseReleased(final MouseEvent e) {
                if (ToolPanel.getInstance().getCurrentActiveTool() != null) {
                    if (ToolPanel.getInstance().getCurrentActiveTool().mouseReleased(e, whiteboardService.getShapes())) {
                        repaint();
                        if (ToolPanel.getInstance().getCurrentActiveTool() instanceof final EraserTool eraserTool) {
                            whiteboardService.handleToolReleased(eraserTool);
                        } else {
                            whiteboardService.handleToolReleased(null);
                        }
                    }
                }
            }
        });
        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(final MouseEvent e) {
                if (ToolPanel.getInstance().getCurrentActiveTool() != null) {
                    if (ToolPanel.getInstance().getCurrentActiveTool().mouseDragged(e, whiteboardService.getShapes())) {
                        repaint();
                        if (ToolPanel.getInstance().getCurrentActiveTool() instanceof final EraserTool eraserTool) {
                            whiteboardService.handleToolReleased(eraserTool);
                        }
                    }
                }
            }
        });
    }

    /**
     * Return the singleton instance of the DrawingPanel.
     *
     * @return DrawingPanel instance
     */
    public static synchronized DrawingPanel getInstance() {
        if (instance == null) {
            instance = new DrawingPanel();
        }
        return instance;
    }

    /**
     * Render all shapes and active drawing tool's temporary state.
     *
     * @param g graphics context to paint on
     */
    @Override
    protected void paintComponent(final Graphics g) {
        super.paintComponent(g);
        final Graphics2D g2d = (Graphics2D) g.create();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        for (final Shape shape : whiteboardService.getShapes()) {
            shape.draw(g2d);
        }
        if (ToolPanel.getInstance().getCurrentActiveTool() != null) {
            ToolPanel.getInstance().getCurrentActiveTool().drawTemporary(g2d);
        }
        g2d.dispose();
    }

    /**
     * Convert the drawing panel to a Base64-encoded PNG image.
     *
     * @param scale scale factor to apply to the image
     * @return Base64-encoded string representation of the image
     */
    public String toBase64(final double scale) {
        final int width = (int) (getWidth() * scale);
        final int height = (int) (getHeight() * scale);
        final BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        final Graphics2D g2d = image.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2d.scale(scale, scale);
        paint(g2d);
        g2d.dispose();
        try (final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {
            ImageIO.write(image, "png", byteArrayOutputStream);
            final byte[] imageBytes = byteArrayOutputStream.toByteArray();
            return Base64.getEncoder().encodeToString(imageBytes);
        } catch (final Exception e) {
            System.out.println("Error converting to Base64: " + e.getMessage());
            return null;
        }
    }
}
