package Models;

import javax.swing.JPanel;
import java.awt.*;
import java.awt.geom.*;
import java.util.List;

public class TrainingGraph extends JPanel {

    // Constants for graph labels
    private static final String TITLE_COST = "Cost Data";
    private static final String TITLE_ACCURACY = "Accuracy Data";
    private static final String COST_LABEL = "Cost";
    private static final String ACCURACY_LABEL = "Accuracy %";
    private static final String EPOCH_LABEL = "Epoch";
    
    private static final int MARGIN = 64;
    private static final int RADIUS = 2;
    private static final int TICK_LENGTH = MARGIN / 16;

    private static final int LEGEND_MARGIN = 10;

    // Y-axis label intervals
    private static final double COST_LABEL_INTERVAL = 0.25;
    private static final double ACCURACY_LABEL_INTERVAL = 10.0;

    // Colors
    private static final Color TRAINING_COST_COLOR = Color.RED;
    private static final Color VALIDATION_COST_COLOR = Color.BLUE;
    private static final Color TRAINING_ACCURACY_COLOR = Color.RED;
    private static final Color VALIDATION_ACCURACY_COLOR = Color.BLUE;

    private TrainingData trainingData;

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D graph = (Graphics2D) g;
        graph.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int width = getWidth();
        int height = getHeight();
        int halfWidth = width / 2;

        if (trainingData == null) return;

        // Draw Cost Graph
        drawGraphBox(graph, 0, 0, halfWidth, height);
        drawLabels(graph, 0, 0, halfWidth, height, TITLE_COST, COST_LABEL, EPOCH_LABEL);

        drawTicks(graph, 0, 0, halfWidth, height, true);

        drawCostPlot(graph, 0, 0, halfWidth, height);

        drawLegend(graph, 0, 0, halfWidth, LEGEND_MARGIN, true);

        if (trainingData.useAccuracy()) {
            // Draw Accuracy Graph
            drawGraphBox(graph, halfWidth, 0, halfWidth, height);
            drawLabels(graph, halfWidth, 0, halfWidth, height, TITLE_ACCURACY, ACCURACY_LABEL, EPOCH_LABEL);

            drawTicks(graph, halfWidth, 0, halfWidth, height, false);

            drawAccuracyPlot(graph, halfWidth, 0, halfWidth, height);

            drawLegend(graph, halfWidth, 0, halfWidth, LEGEND_MARGIN, false);
        }
    }

    private void drawGraphBox(Graphics2D graph, int xStart, int yStart, int width, int height) {
        graph.drawLine(xStart + MARGIN, yStart + MARGIN, xStart + MARGIN, yStart + height - MARGIN);
        graph.drawLine(xStart + MARGIN, yStart + height - MARGIN, xStart + width - MARGIN, yStart + height - MARGIN);
        graph.drawLine(xStart + width - MARGIN, yStart + MARGIN, xStart + width - MARGIN, yStart + height - MARGIN);
        graph.drawLine(xStart + MARGIN, yStart + MARGIN, xStart + width - MARGIN, yStart + MARGIN);
    }

    private void drawLabels(Graphics2D graph, int xStart, int yStart, int width, int height, String title, String yLabel, String xLabel) {
        Font defaultFont = getFont();
        FontMetrics metrics = getFontMetrics(defaultFont);

        // Title
        drawCenteredString(graph, title, xStart + width / 2, yStart + MARGIN / 2 + metrics.getAscent() / 2);

        // Y axis label
        drawRotatedString(graph, yLabel, xStart + MARGIN / 3, yStart + (height - yStart) / 2, -90);

        // X axis label
        drawCenteredString(graph, xLabel, xStart + width / 2, yStart + height - MARGIN / 3 + metrics.getAscent() / 2);
    }

    private void drawTicks(Graphics2D graph, int xStart, int yStart, int width, int height, boolean isCost) {
        int numEpochs = trainingData.getNumEpochs();
        double horizontalScale = (numEpochs > 1) ? ((double) (width - 2 * MARGIN) / (numEpochs - 1)) : 0;

        // Epoch ticks
        for (int i = 0; i < numEpochs; i++) {
            double x = xStart + MARGIN + i * horizontalScale;
            graph.drawLine((int) x, yStart + height - MARGIN - TICK_LENGTH / 2, (int) x, yStart + height - MARGIN + TICK_LENGTH / 2);
            drawCenteredString(graph, Integer.toString(i), (int) x, yStart + height - MARGIN + MARGIN / 3 + getFontMetrics(getFont()).getAscent() / 2);
        }

        // Y axis ticks
        if (isCost) {
            drawYAxisTicks(graph, xStart, yStart, height, MARGIN, COST_LABEL_INTERVAL, getMaxCost(), true);
        } else {
            drawYAxisTicks(graph, xStart, yStart, height, xStart + MARGIN, ACCURACY_LABEL_INTERVAL, 100, false);
        }
    }

    private void drawYAxisTicks(Graphics2D graph, int xStart, int yStart, int height, double xOffset, double labelInterval, double maxHeight, boolean isCost) {
        int numYLabels = (int) (maxHeight / labelInterval);
        double verticalScale = (double) (height - 2 * MARGIN) / maxHeight;

        for (int i = 0; i <= numYLabels; i++) {
            double y = yStart + height - MARGIN - i * verticalScale * labelInterval;
            graph.drawLine((int) xOffset - TICK_LENGTH / 2, (int) y, (int) xOffset + TICK_LENGTH / 2, (int) y);
            String text = Double.toString(i * labelInterval);
            drawCenteredString(graph, text, (int) xOffset - MARGIN / 3, (int) y + getFontMetrics(getFont()).getAscent() / 2);
        }
    }

    private void drawCostPlot(Graphics2D graph, int xStart, int yStart, int width, int height) {
        List<Double> trainingCost = trainingData.getTrainingCost();
        List<Double> validationCost = trainingData.getValidationCost();
        int numEpochs = trainingData.getNumEpochs();
        double horizontalScale = (double) (width - 2 * MARGIN) / (numEpochs - 1);
        double costVerticalScale = (double) (height - 2 * MARGIN) / getMaxCost();

        drawPlot(graph, trainingCost, TRAINING_COST_COLOR, xStart, yStart, width, height, horizontalScale, costVerticalScale);
        drawPlot(graph, validationCost, VALIDATION_COST_COLOR, xStart, yStart, width, height, horizontalScale, costVerticalScale);
    }

    private void drawAccuracyPlot(Graphics2D graph, int xStart, int yStart, int width, int height) {
        List<Double> trainingAccuracy = trainingData.getTrainingAccuracy();
        List<Double> validationAccuracy = trainingData.getValidationAccuracy();
        int numEpochs = trainingData.getNumEpochs();
        double horizontalScale = (double) (width - 2 * MARGIN) / (numEpochs - 1);
        double accuracyVerticalScale = (double) (height - 2 * MARGIN) / 100;

        drawPlot(graph, trainingAccuracy, TRAINING_ACCURACY_COLOR, xStart, yStart, width, height, horizontalScale, accuracyVerticalScale);
        drawPlot(graph, validationAccuracy, VALIDATION_ACCURACY_COLOR, xStart, yStart, width, height, horizontalScale, accuracyVerticalScale);
    }

    private void drawPlot(Graphics2D graph, List<Double> data, Color color, int xStart, int yStart, int width, int height, double horizontalScale, double verticalScale) {
        // If no data, don't draw
        if (data.size() == 0) return;
        
        // All points except the last one
        graph.setPaint(color);
        int numEpochs = trainingData.getNumEpochs();
        for (int i = 0; i < numEpochs - 1; i++) {
            double x1 = xStart + MARGIN + i * horizontalScale;
            double y1 = yStart + height - MARGIN - data.get(i) * verticalScale;
            double x2 = xStart + MARGIN + (i + 1) * horizontalScale;
            double y2 = yStart + height - MARGIN - data.get(i + 1) * verticalScale;
            graph.fill(new Ellipse2D.Double(x1 - RADIUS, y1 - RADIUS, RADIUS * 2, RADIUS * 2));
            graph.draw(new Line2D.Double(x1, y1, x2, y2));
        }

        // Final point
        double x = xStart + MARGIN + (numEpochs - 1) * horizontalScale;
        double y = yStart + height - MARGIN - data.get(numEpochs - 1) * verticalScale;
        graph.fill(new Ellipse2D.Double(x - RADIUS, y - RADIUS, RADIUS * 2, RADIUS * 2));
        graph.setPaint(Color.BLACK);
    }

    private double getMaxCost() {
        List<Double> trainingCost = trainingData.getTrainingCost();
        List<Double> validationCost = trainingData.getValidationCost();
        return Math.max(
                trainingCost.stream().max(Double::compare).orElse(0.0),
                validationCost.stream().max(Double::compare).orElse(0.0)
        );
    }

    private void drawLegend(Graphics2D graph, int xStart, int yStart, int width, int legendMargin, boolean isCost) {
        Font defaultFont = getFont();
        FontMetrics metrics = getFontMetrics(defaultFont);

        // Define legend items
        String[] labels;
        Color[] colors;
        if (isCost) {
            labels = new String[] { "Training Cost", "Validation Cost" };
            colors = new Color[] { TRAINING_COST_COLOR, VALIDATION_COST_COLOR };
        } else {
            labels = new String[] { "Training Accuracy", "Validation Accuracy" };
            colors = new Color[] { TRAINING_ACCURACY_COLOR, VALIDATION_ACCURACY_COLOR };
        }

        int boxHeight = 20;
        int boxMargin = 5;
        int legendWidth = Math.max(metrics.stringWidth(labels[0]), metrics.stringWidth(labels[1])) + boxMargin * 3 + boxHeight;
        int legendHeight = boxHeight * 2 + boxMargin * 3;
        int legendX = xStart + width - MARGIN - legendWidth - legendMargin;
        int legendY = yStart + MARGIN + legendMargin;
        

        // Draw the legend box
        graph.setPaint(getBackground());
        graph.fillRect(legendX, legendY, legendWidth, legendHeight);
        graph.setPaint(Color.BLACK);
        graph.drawRect(legendX, legendY, legendWidth, legendHeight);

        // Draw the legend items
        int itemY = legendY + boxMargin;
        for (int i = 0; i < labels.length; i++) {
            graph.setPaint(colors[i]);
            graph.fillRect(legendX + boxMargin, itemY, boxHeight, boxHeight);
            graph.setPaint(Color.BLACK);
            graph.drawRect(legendX + boxMargin, itemY, boxHeight, boxHeight);
            graph.drawString(labels[i], legendX + boxMargin * 2 + boxHeight, itemY + boxHeight - 4);
            itemY += boxHeight + boxMargin;
        }
    }

    private void drawCenteredString(Graphics2D graph, String text, int x, int y) {
        FontMetrics metrics = getFontMetrics(getFont());
        graph.drawString(text, x - metrics.stringWidth(text) / 2, y);
    }

    private void drawRotatedString(Graphics2D graph, String text, int x, int y, int angle) {
        Font defaultFont = getFont();
        FontMetrics metrics = getFontMetrics(defaultFont);
        AffineTransform transform = graph.getTransform();
        transform.rotate(Math.toRadians(angle), x, y);
        graph.setTransform(transform);
        graph.drawString(text, x - metrics.stringWidth(text) / 2, y);
        transform.rotate(Math.toRadians(-angle), x, y);
        graph.setTransform(transform);
    }

    public void setTrainingData(TrainingData trainingData) {
        this.trainingData = trainingData;
        repaint(); // Trigger a repaint when new data is set
    }
}