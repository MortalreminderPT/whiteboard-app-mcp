

package io.tomori.whiteboard.util;

import io.tomori.whiteboard.model.shapes.Shape;
import lombok.Data;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Manage document operation for whiteboard.
 * Handle loading, saving, and tracking the current document file.
 */
@Data
public class DocumentManager {
    private static DocumentManager instance;
    /**
     * Currently open file
     */
    private File currentFile;

    /**
     * Private constructor for singleton pattern.
     */
    private DocumentManager() {
        currentFile = null;
    }

    /**
     * Return the singleton instance of DocumentManager.
     *
     * @return DocumentManager instance
     */
    public static synchronized DocumentManager getInstance() {
        if (instance == null) {
            instance = new DocumentManager();
        }
        return instance;
    }

    /**
     * Reset the current document to a new empty one.
     */
    public void newDocument() {
        currentFile = null;
    }

    /**
     * Close the current document.
     * This will reset the current file to null and notify the system that
     * the document is closed. All users connected to this whiteboard should
     * no longer see it.
     */
    public void closeDocument() {
        currentFile = null;
    }

    /**
     * Save the current document to the existing file.
     *
     * @return True if save preparation success
     */
    public boolean saveDocument() {
        if (currentFile == null) {
            return false;
        }
        try {
            final File parentDir = currentFile.getParentFile();
            if (parentDir != null && !parentDir.exists()) {
                final boolean mkdirs = parentDir.mkdirs();
                if (!mkdirs) {
                    System.out.println("Failed to create directories: " + parentDir.getAbsolutePath());
                    return false;
                }
            }
            return true;
        } catch (final Exception e) {
            System.out.println("Failed to save file: " + e.getMessage());
            return false;
        }
    }

    /**
     * Load shapes from an SVG file.
     *
     * @param file file to load shapes
     * @return shapes list from the file
     */
    public CopyOnWriteArrayList<Shape> loadShapes(final File file) {
        CopyOnWriteArrayList<Shape> shapes = new CopyOnWriteArrayList<>();
        if (file == null || !file.exists() || !file.isFile() || !file.canRead()) {
            System.out.println("Cannot load shapes, invalid file");
            return shapes;
        }
        try {
            final String svgString = Files.readString(file.toPath());
            shapes = SvgUtil.fromSvg(svgString);
            currentFile = file;
        } catch (final Exception e) {
            System.out.println("Error loading shapes: " + e.getMessage());
        }
        return shapes;
    }

    /**
     * Save shapes to the current file in SVG format.
     *
     * @param shapes shapes to save
     */
    public void saveShapes(final List<Shape> shapes) {
        if (currentFile == null) {
            System.out.println("No file specified, cannot save shapes");
            return;
        }
        try (final PrintWriter writer = new PrintWriter(new FileWriter(currentFile))) {
            writer.print(SvgUtil.toSvg(shapes));
            System.out.println("Shapes saved to file: " + currentFile.getAbsolutePath());
        } catch (final IOException e) {
            System.out.println("Error saving shapes: " + e.getMessage());
        }
    }
}
