

package io.tomori.whiteboard.gui;

import bibliothek.gui.dock.common.CControl;
import bibliothek.gui.dock.common.CGrid;
import bibliothek.gui.dock.common.DefaultSingleCDockable;
import bibliothek.gui.dock.common.SingleCDockable;
import io.tomori.whiteboard.gui.panels.ChatPanel;
import io.tomori.whiteboard.gui.panels.DrawingPanel;
import io.tomori.whiteboard.gui.panels.ToolPanel;
import io.tomori.whiteboard.gui.panels.UserPanel;
import io.tomori.whiteboard.service.WhiteboardService;
import io.tomori.whiteboard.util.DocumentManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.io.File;

/**
 * Main application window for the whiteboard application.
 * Implement a singleton pattern and provide dockable interface for all panels and menus.
 */
public class WhiteboardFrame extends JFrame {
    /**
     * Singleton instance of the WhiteboardFrame
     */
    private static WhiteboardFrame instance;
    /**
     * Docking controller
     */
    CControl control;
    /**
     * Document manager for file operations
     */
    private DocumentManager documentManager;
    /**
     * File chooser dialog for open/save operations
     */
    private JFileChooser fileChooser;
    /**
     * Application menu
     */
    private JMenu appMenu;
    /**
     * File operations menu
     */
    private JMenu fileMenu;
    /**
     * Editing operations menu
     */
    private JMenu editMenu;

    /**
     * Private constructor for singleton pattern.
     */
    private WhiteboardFrame() {
        super("Whiteboard Application");
        control = new CControl(this);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 800);
        setLocationRelativeTo(null);
        initComponents();
        createMenuBar();
    }

    /**
     * Return the singleton instance of the WhiteboardFrame.
     *
     * @return WhiteboardFrame instance
     */
    public static synchronized WhiteboardFrame getInstance() {
        if (instance == null) {
            instance = new WhiteboardFrame();
        }
        return instance;
    }

    /**
     * Exit the application completely.
     */
    public static void closeApplication() {
        System.exit(0);
    }

    /**
     * Create a dockable panel with the given parameters.
     *
     * @param id        unique identifier for the dockable
     * @param title     displayed title for the dockable
     * @param component component to be placed in the dockable
     * @return A configured SingleCDockable object
     */
    private static SingleCDockable createDockable(final String id, final String title, final Component component) {
        final DefaultSingleCDockable dockable = new DefaultSingleCDockable(id, title);
        dockable.setTitleText(title);
        dockable.add(component);
        dockable.setCloseable(false);
        dockable.setExternalizable(false);
        dockable.setMaximizable(true);
        dockable.setMinimizable(true);
        return dockable;
    }

    /**
     * Create the application menu with about and exit options.
     *
     * @return configured application menu
     */
    private static JMenu createAppMenu() {
        final JMenu appMenu = new JMenu("App");
        final JMenuItem aboutMenuItem = new JMenuItem("About");
        aboutMenuItem.addActionListener(e -> {
            JOptionPane.showMessageDialog(null,
                    "Whiteboard Application\nAuthor: Pengtao Zhao",
                    "About",
                    JOptionPane.INFORMATION_MESSAGE);
        });
        appMenu.add(aboutMenuItem);
        final JMenuItem exitMenuItem = new JMenuItem("Exit", KeyEvent.VK_Q);
        exitMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q, InputEvent.CTRL_DOWN_MASK));
        exitMenuItem.addActionListener(e -> closeApplication());
        appMenu.add(exitMenuItem);
        return appMenu;
    }

    /**
     * Create the edit menu with undo, redo, and clear canvas options.
     *
     * @return configured edit menu
     */
    private static JMenu createEditMenu() {
        final JMenu editMenu = new JMenu("Edit");
        final JMenuItem undoMenuItem = new JMenuItem("Undo", KeyEvent.VK_Z);
        undoMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Z, InputEvent.CTRL_DOWN_MASK));
        undoMenuItem.addActionListener(e -> {
            WhiteboardService.getInstance().undo();
        });
        final JMenuItem redoMenuItem = new JMenuItem("Redo", KeyEvent.VK_Z);
        redoMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Z,
                InputEvent.SHIFT_DOWN_MASK | InputEvent.CTRL_DOWN_MASK));
        redoMenuItem.addActionListener(e -> {
            WhiteboardService.getInstance().redo();
        });
        final JMenuItem clearMenuItem = new JMenuItem("Clear Canvas", KeyEvent.VK_C);
        clearMenuItem.addActionListener(e -> {
            final int option = JOptionPane.showConfirmDialog(
                    null,
                    "Are you sure you want to clear the canvas?",
                    "Confirm",
                    JOptionPane.YES_NO_OPTION
            );
            if (option == JOptionPane.YES_OPTION) {
                WhiteboardService.getInstance().clearAll();
            }
        });
        editMenu.add(undoMenuItem);
        editMenu.add(redoMenuItem);
        editMenu.addSeparator();
        editMenu.add(clearMenuItem);
        return editMenu;
    }

    /**
     * Initialize all components of the whiteboard interface.
     */
    private void initComponents() {
        add(control.getContentArea(), BorderLayout.CENTER);
        documentManager = DocumentManager.getInstance();
        final DrawingPanel drawingPanel = DrawingPanel.getInstance();
        final ToolPanel toolPanel = ToolPanel.getInstance();
        final ChatPanel chatPanel = ChatPanel.getInstance();
        final UserPanel userPanel = UserPanel.getInstance();
        final SingleCDockable canvasDockable = createDockable("canvas", "Canvas", drawingPanel);
        final SingleCDockable chatDockable = createDockable("chat", "Chat", chatPanel);
        final SingleCDockable userDockable = createDockable("user", "Users", userPanel);
        final SingleCDockable toolDockable = createDockable("tool", "Tools", toolPanel);
        final CGrid grid = new CGrid(control);
        grid.add(0, 0, 1, 4, toolDockable);
        grid.add(1, 0, 3, 4, canvasDockable);
        grid.add(4, 0, 1, 4, chatDockable);
        grid.add(5, 0, 1, 4, userDockable);
        control.getContentArea().deploy(grid);
        canvasDockable.setVisible(true);
        chatDockable.setVisible(true);
        toolDockable.setVisible(true);
        fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new javax.swing.filechooser.FileFilter() {
            @Override
            public boolean accept(final File f) {
                return f.isDirectory() || f.getName().toLowerCase().endsWith(".svg");
            }

            @Override
            public String getDescription() {
                return "Scalable Vector Graphics (*.svg)";
            }
        });
    }

    /**
     * Create and configure the application menu bar.
     */
    private void createMenuBar() {
        final JMenuBar menuBar = new JMenuBar();
        appMenu = createAppMenu();
        fileMenu = createFileMenu();
        editMenu = createEditMenu();
        menuBar.add(appMenu);
        menuBar.add(fileMenu);
        menuBar.add(editMenu);
        setJMenuBar(menuBar);
    }

    /**
     * Enable the file menu for users with appropriate permissions.
     */
    public void enableFileMenu() {
        fileMenu.setEnabled(true);
    }

    /**
     * Create the file menu with new, open, save, and save as options.
     *
     * @return configured file menu
     */
    private JMenu createFileMenu() {
        final JMenu fileMenu = new JMenu("File");
        final JMenuItem newMenuItem = new JMenuItem("New", KeyEvent.VK_N);
        newMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, InputEvent.CTRL_DOWN_MASK));
        newMenuItem.addActionListener(e -> newDocument());
        final JMenuItem openMenuItem = new JMenuItem("Open", KeyEvent.VK_O);
        openMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, InputEvent.CTRL_DOWN_MASK));
        openMenuItem.addActionListener(e -> openDocument());
        final JMenuItem closeMenuItem = new JMenuItem("Close", KeyEvent.VK_W);
        closeMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_W, InputEvent.CTRL_DOWN_MASK));
        closeMenuItem.addActionListener(e -> closeDocument());
        final JMenuItem saveMenuItem = new JMenuItem("Save", KeyEvent.VK_S);
        saveMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_DOWN_MASK));
        saveMenuItem.addActionListener(e -> saveDocument(false));
        final JMenuItem saveAsMenuItem = new JMenuItem("Save As", KeyEvent.VK_S);
        saveAsMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S,
                InputEvent.SHIFT_DOWN_MASK | InputEvent.CTRL_DOWN_MASK));
        saveAsMenuItem.addActionListener(e -> saveDocument(true));
        fileMenu.add(newMenuItem);
        fileMenu.add(openMenuItem);
        fileMenu.add(closeMenuItem);
        fileMenu.addSeparator();
        fileMenu.add(saveMenuItem);
        fileMenu.add(saveAsMenuItem);
        fileMenu.setEnabled(false);
        return fileMenu;
    }

    /**
     * Create a new whiteboard document.
     */
    private void newDocument() {
        if (checkSaveBeforeAction()) {
            documentManager.newDocument();
            WhiteboardService.getInstance().clearAll();
            WhiteboardService.getInstance().setModified(false);
            setTitle("Whiteboard Application - Untitled");
            enableAllFileMenuItems();
        }
    }

    /**
     * Open an existing whiteboard document from file.
     */
    private void openDocument() {
        if (checkSaveBeforeAction()) {
            final int result = fileChooser.showOpenDialog(this);
            if (result == JFileChooser.APPROVE_OPTION) {
                final File file = fileChooser.getSelectedFile();
                final boolean opened = WhiteboardService.getInstance().loadFromDocument(file);
                if (opened) {
                    setTitle("Whiteboard Application - " + file.getName());
                    enableAllFileMenuItems();
                } else {
                    JOptionPane.showMessageDialog(
                            null,
                            "Cannot open file: " + file.getName(),
                            "Error",
                            JOptionPane.ERROR_MESSAGE
                    );
                }
            }
        }
    }

    /**
     * Check if the current document needs to be saved before proceeding.
     *
     * @return true if the operation should continue, false if cancelled
     */
    private boolean checkSaveBeforeAction() {
        if (documentManager.getCurrentFile() != null && WhiteboardService.getInstance().isModified()) {
            final int option = JOptionPane.showConfirmDialog(
                    null,
                    "Current document has been modified. Save changes?",
                    "Save Confirmation",
                    JOptionPane.YES_NO_CANCEL_OPTION
            );
            if (option == JOptionPane.CANCEL_OPTION) {
                return false;
            } else if (option == JOptionPane.YES_OPTION) {
                return saveDocument(false);
            }
        }
        return true;
    }

    /**
     * Save the current whiteboard document to a file.
     *
     * @param saveAs True to force "Save As" dialog, false for normal save
     * @return True if the save was successful
     */
    private boolean saveDocument(final boolean saveAs) {
        if (saveAs || documentManager.getCurrentFile() == null) {
            final int result = fileChooser.showSaveDialog(this);
            if (result != JFileChooser.APPROVE_OPTION) {
                return false;
            }
            File file = fileChooser.getSelectedFile();
            if (!file.getName().toLowerCase().endsWith(".svg")) {
                file = new File(file.getAbsolutePath() + ".svg");
            }
            documentManager.setCurrentFile(file);
        }
        documentManager.saveShapes(WhiteboardService.getInstance().getShapes());
        if (documentManager.saveDocument()) {
            setTitle("Whiteboard Application - " + documentManager.getCurrentFile().getName());
            WhiteboardService.getInstance().setModified(false);
            return true;
        } else {
            JOptionPane.showMessageDialog(
                    null,
                    "Cannot save file: " + documentManager.getCurrentFile().getName(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
            return false;
        }
    }

    /**
     * Close the current document.
     * This will check if the document needs to be saved before closing.
     * After closing, the UI will show an empty whiteboard and disable file operations
     * that require an open document.
     */
    private void closeDocument() {
        if (checkSaveBeforeAction()) {
            documentManager.closeDocument();
            WhiteboardService.getInstance().clearAll();
            WhiteboardService.getInstance().setModified(false);
            setTitle("Whiteboard Application");
            disableFileMenuItemsForClosedDocument();
        }
    }

    /**
     * Enable all items in the file menu.
     */
    private void enableAllFileMenuItems() {
        for (int i = 0; i < fileMenu.getItemCount(); i++) {
            final JMenuItem item = fileMenu.getItem(i);
            if (item != null) {
                item.setEnabled(true);
            }
        }
    }

    /**
     * Disable file menu items that require an open document.
     * Only "New" and "Open" will remain enabled.
     */
    private void disableFileMenuItemsForClosedDocument() {
        for (int i = 0; i < fileMenu.getItemCount(); i++) {
            final JMenuItem item = fileMenu.getItem(i);
            if (item != null) {
                final String text = item.getText();
                if (!"New".equals(text) && !"Open".equals(text)) {
                    item.setEnabled(false);
                }
            }
        }
    }
}
