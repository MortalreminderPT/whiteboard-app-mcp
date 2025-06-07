

package io.tomori.whiteboard.gui.panels;

import io.tomori.whiteboard.model.Color;
import io.tomori.whiteboard.model.tools.*;
import lombok.Getter;

import javax.swing.*;
import javax.swing.plaf.basic.BasicButtonUI;
import java.awt.*;

/**
 * Panel containing drawing tools, color selectors, and stroke width controls.
 * Manage tool selection and config for the whiteboard application.
 */
public class ToolPanel extends JPanel {
    /**
     * Singleton instance of the ToolPanel
     */
    private static ToolPanel instance;
    /**
     * Button group for tool selection
     */
    private ButtonGroup toolButtonGroup;
    /**
     * Currently active drawing tool
     */
    @Getter
    private Tool currentActiveTool;
    /**
     * Button for displaying and selecting the current color
     */
    private JButton colorButton;
    /**
     * Currently selected color
     */
    private Color currentColor = Color.BLACK;
    /**
     * Spinner for custom stroke width selection
     */
    private JSpinner strokeWidthSpinner;
    /**
     * Currently selected stroke width
     */
    private float currentStrokeWidth = 2.0f;

    /**
     * Private constructor for singleton pattern implementation.
     */
    private ToolPanel() {
        setBorder(BorderFactory.createEtchedBorder());
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        createToolButtons();
        createColorChooser();
        createStrokeWidthChooser();
    }

    /**
     * Return the singleton instance of the ToolPanel.
     *
     * @return ToolPanel instance
     */
    public static synchronized ToolPanel getInstance() {
        if (instance == null) {
            instance = new ToolPanel();
        }
        return instance;
    }

    /**
     * Create and initialize the drawing tool buttons.
     */
    private void createToolButtons() {
        final JPanel toolButtonPanel = new JPanel(new GridLayout(0, 2, 1, 1));
        toolButtonPanel.setBorder(BorderFactory.createTitledBorder("Drawing Tools"));
        toolButtonGroup = new ButtonGroup();
        addToolButton(toolButtonPanel, "Pen", new PenTool());
        addToolButton(toolButtonPanel, "Eraser", new EraserTool());
        addToolButton(toolButtonPanel, "Select", new SelectionTool());
        addToolButton(toolButtonPanel, "Fill", new FillTool());
        addToolButton(toolButtonPanel, "Rectangle", new RectangleTool());
        addToolButton(toolButtonPanel, "Square", new SquareTool());
        addToolButton(toolButtonPanel, "Circle", new CircleTool());
        addToolButton(toolButtonPanel, "Oval", new OvalTool());
        addToolButton(toolButtonPanel, "Triangle", new TriangleTool());
        addToolButton(toolButtonPanel, "Line", new LineTool());
        addToolButton(toolButtonPanel, "Text", new TextTool());
        if (toolButtonGroup.getButtonCount() > 0) {
            final JToggleButton firstButton = (JToggleButton) toolButtonPanel.getComponent(0);
            firstButton.setSelected(true);
            currentActiveTool = new PenTool();
            currentActiveTool.setColor(currentColor);
            currentActiveTool.setStrokeWidth(currentStrokeWidth);
        }
        add(toolButtonPanel);
    }

    /**
     * Add a tool button to the specified panel.
     *
     * @param panel    panel to add the button to
     * @param toolName name of the tool
     * @param tool     tool instance
     */
    private void addToolButton(final JPanel panel, final String toolName, final Tool tool) {
        final JToggleButton button = new JToggleButton(toolName);
        button.setHorizontalAlignment(SwingConstants.LEFT);

        button.addActionListener(e -> {
            final Tool newTool = tool.clone();
            newTool.setColor(currentColor);
            newTool.setStrokeWidth(currentStrokeWidth);
            currentActiveTool = newTool;
        });
        button.setToolTipText(toolName);
        toolButtonGroup.add(button);
        panel.add(button);
    }

    /**
     * Create the color selection interface.
     */
    private void createColorChooser() {
        final JPanel colorPanel = new JPanel();
        colorPanel.setBorder(BorderFactory.createTitledBorder("Color"));
        colorPanel.setLayout(new BoxLayout(colorPanel, BoxLayout.Y_AXIS));
        final JPanel colorButtonPanel = new JPanel();
        colorButton = new JButton();
        colorButton.setUI(new BasicButtonUI());
        colorButton.setContentAreaFilled(true);
        colorButton.setOpaque(true);
        colorButton.setBackground(currentColor.toAwtColor());
        colorButton.setPreferredSize(new Dimension(30, 30));
        colorButton.setToolTipText("Click to choose color");
        colorButton.addActionListener(e -> {
            final java.awt.Color selectedColor = JColorChooser.showDialog(this, "Select Color", currentColor.toAwtColor());
            if (selectedColor != null) {
                currentColor = new Color(selectedColor);
                colorButton.setBackground(currentColor.toAwtColor());
                if (currentActiveTool != null) {
                    currentActiveTool.setColor(currentColor);
                }
            }
        });
        colorButtonPanel.add(colorButton);
        colorPanel.add(colorButtonPanel);
        final JPanel paletteContainer = new JPanel();
        paletteContainer.setLayout(new FlowLayout(FlowLayout.CENTER, 0, 0));
        final JPanel palettePanel = new JPanel(new GridLayout(8, 2, 4, 2));
        palettePanel.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
        createColorButton(palettePanel, Color.BLACK);
        createColorButton(palettePanel, Color.WHITE);
        createColorButton(palettePanel, Color.GRAY);
        createColorButton(palettePanel, Color.LIGHT_GRAY);
        createColorButton(palettePanel, Color.DARK_RED);
        createColorButton(palettePanel, Color.RED);
        createColorButton(palettePanel, Color.DARK_YELLOW);
        createColorButton(palettePanel, Color.YELLOW);
        createColorButton(palettePanel, Color.DARK_GREEN);
        createColorButton(palettePanel, Color.GREEN);
        createColorButton(palettePanel, Color.DARK_CYAN);
        createColorButton(palettePanel, Color.CYAN);
        createColorButton(palettePanel, Color.DARK_BLUE);
        createColorButton(palettePanel, Color.BLUE);
        createColorButton(palettePanel, Color.PURPLE);
        createColorButton(palettePanel, Color.MAGENTA);
        paletteContainer.add(palettePanel);
        colorPanel.add(paletteContainer);
        add(colorPanel);
    }

    /**
     * Create a color selection button.
     *
     * @param panel panel to add the button to
     * @param color color for the button
     */
    private void createColorButton(final JPanel panel, final Color color) {
        final JButton button = new JButton();
        button.setUI(new BasicButtonUI());
        button.setContentAreaFilled(true);
        button.setOpaque(true);
        button.setBorderPainted(false);
        button.setBackground(color.toAwtColor());
        button.setPreferredSize(new Dimension(25, 25));
        button.setMinimumSize(new Dimension(25, 25));
        button.setMaximumSize(new Dimension(25, 25));
        button.addActionListener(e -> {
            currentColor = color;
            colorButton.setBackground(color.toAwtColor());
            if (currentActiveTool != null) {
                currentActiveTool.setColor(currentColor);
            }
        });
        panel.add(button);
    }

    /**
     * Create the stroke width selection interface.
     */
    private void createStrokeWidthChooser() {
        final JPanel strokePanel = new JPanel();
        strokePanel.setBorder(BorderFactory.createTitledBorder("Line Width"));
        strokePanel.setLayout(new BoxLayout(strokePanel, BoxLayout.Y_AXIS));
        final ButtonGroup strokeButtonGroup = new ButtonGroup();
        final JPanel radioPanel = new JPanel(new GridLayout(0, 1));
        final float thickWidth = 6.0f;
        final float mediumWidth = 3.0f;
        final float thinWidth = 1.0f;
        final JRadioButton thickButton = new JRadioButton("Thick");
        final JRadioButton mediumButton = new JRadioButton("Medium");
        final JRadioButton thinButton = new JRadioButton("Thin");
        final JRadioButton customButton = new JRadioButton("Custom");
        thickButton.addActionListener(e -> {
            currentStrokeWidth = thickWidth;
            if (currentActiveTool != null) {
                currentActiveTool.setStrokeWidth(currentStrokeWidth);
            }
            strokeWidthSpinner.setValue(thickWidth);
            strokeWidthSpinner.setVisible(false);
        });
        mediumButton.addActionListener(e -> {
            currentStrokeWidth = mediumWidth;
            if (currentActiveTool != null) {
                currentActiveTool.setStrokeWidth(currentStrokeWidth);
            }
            strokeWidthSpinner.setValue(mediumWidth);
            strokeWidthSpinner.setVisible(false);
        });
        thinButton.addActionListener(e -> {
            currentStrokeWidth = thinWidth;
            if (currentActiveTool != null) {
                currentActiveTool.setStrokeWidth(currentStrokeWidth);
            }
            strokeWidthSpinner.setValue(thinWidth);
            strokeWidthSpinner.setVisible(false);
        });
        customButton.addActionListener(e -> {
            strokeWidthSpinner.setVisible(true);
        });
        strokeButtonGroup.add(thickButton);
        strokeButtonGroup.add(mediumButton);
        strokeButtonGroup.add(thinButton);
        strokeButtonGroup.add(customButton);
        radioPanel.add(thickButton);
        radioPanel.add(mediumButton);
        radioPanel.add(thinButton);
        radioPanel.add(customButton);
        strokePanel.add(radioPanel);
        final JPanel spinnerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        final SpinnerNumberModel spinnerModel = new SpinnerNumberModel(2.0, 0.5, 20.0, 0.5);
        strokeWidthSpinner = new JSpinner(spinnerModel);
        strokeWidthSpinner.addChangeListener(e -> {
            currentStrokeWidth = ((Number) strokeWidthSpinner.getValue()).floatValue();
            if (currentActiveTool != null) {
                currentActiveTool.setStrokeWidth(currentStrokeWidth);
            }
        });
        spinnerPanel.add(strokeWidthSpinner);
        strokePanel.add(spinnerPanel);
        mediumButton.setSelected(true);
        currentStrokeWidth = mediumWidth;
        strokeWidthSpinner.setValue(mediumWidth);
        strokeWidthSpinner.setVisible(false);
        add(strokePanel);
    }
}
