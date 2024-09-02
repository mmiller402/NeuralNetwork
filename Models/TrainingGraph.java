/*

package Models;

import java.awt.*;
import java.awt.geom.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import javax.swing.JPanel;
import java.util.Arrays;

public class TrainingGraph extends JPanel {
    
    // String labels
    private final String title = "Training Data";
    private final String costLabel = "Cost";
    private final String accuracyLabel = "Accuracy %";
    private final String epochLabel = "Epoch";

    private int margin = 64;

    // Radius of plot points
    private int radius = 2;

    // Axis labels
    private int tickLength = margin / 16; // Length of tick marks on axes
    private double costLabelInterval = 0.25;
    private double accuracyLabelInterval = 10.0;
    
    // Data
    private TrainingData trainingData;

    // Data colors
    private final Color trainingCostColor = Color.RED;
    private final Color validationCostColor = Color.GREEN;
    private final Color trainingAccuracyColor = Color.BLUE;
    private final Color validationAccuracyColor = Color.MAGENTA;

    // Draw function
    protected void paintComponent(Graphics g) {

        // Initialize local variables to be reused
        double x, y;

        super.paintComponent(g);
        Graphics2D graph = (Graphics2D)g;

        graph.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int width = getWidth();
        int height = getHeight();

        // Draw graph box
        graph.drawLine(margin, margin, margin, height - margin);
        graph.drawLine(margin, height - margin, width - margin, height - margin);
        graph.drawLine(width - margin, margin, width - margin, height - margin);
        graph.drawLine(margin, margin, width - margin, margin);

        // Draw graph labels
        Font defaultFont = g.getFont();
        FontMetrics metrics = g.getFontMetrics(defaultFont);

        // Title
        x = width / 2 - metrics.stringWidth(title) / 2;
        y = margin / 2 + metrics.getAscent() / 2;
        graph.drawString(title, (float)x, (float)y);

        // Y axis cost label
        AffineTransform costAffineTransform = new AffineTransform();
        costAffineTransform.rotate(Math.toRadians(-90), 0, 0);
        Font costLabelFont = defaultFont.deriveFont(costAffineTransform);
        g.setFont(costLabelFont);
        x = margin / 3 + metrics.getAscent() / 2;
        y = height / 2 + metrics.stringWidth(costLabel) / 2;
        graph.drawString(costLabel, (float)x, (float)y); 
        g.setFont(defaultFont);

        // X axis epochs label
        x = width / 2 - metrics.stringWidth(epochLabel) / 2;
        y = height - margin / 3 + metrics.getAscent() / 2;
        graph.drawString(epochLabel, (float)x, (float)y);

        // X axis epochs ticks
        int numEpochs = trainingData.getNumEpochs();
        double horizontalScale = (double)(width - 2 * margin) / (numEpochs - 1);
        for (int i = 0; i <= numEpochs; i++) {
            x = margin + i * horizontalScale;
            y = height - margin;
            graph.draw(new Line2D.Double(x, y - tickLength / 2, x, y + tickLength / 2));

            String text = Integer.toString(i);
            x = x - metrics.stringWidth(text) / 2;
            y = y + margin / 3 + metrics.getAscent() / 2;
            graph.drawString(text, (float)x, (float)y);
        }

        // Y axis cost ticks
        List<Double> trainingCost = trainingData.getTrainingCost();
        List<Double> validationCost = trainingData.getValidationCost();
        double maxHeight = Math.max(trainingCost.stream().max(Double::compareTo).get(), validationCost.stream().max(Double::compareTo).get());
        int numYLabels = (int) (maxHeight / costLabelInterval);
        double costVerticalScale = (double)(height - 2 * margin) / maxHeight;
        for (int i = 0; i <= numYLabels; i++) {
            x = margin;
            y = height - margin - i * costVerticalScale * costLabelInterval;
            graph.draw(new Line2D.Double(x - tickLength / 2, y, x + tickLength / 2, y));

            String text = Double.toString(i * costLabelInterval);
            x = x - margin / 3 - metrics.stringWidth(text) / 2;
            y = y + metrics.getAscent() / 2;
            graph.drawString(text, (float)x, (float)y);
        }

        // Train cost
        graph.setPaint(trainingCostColor);
        for (int i = 0; i < numEpochs - 1; i++) {
            // Draw line between adjacent points
            double x1 = margin + i * horizontalScale;
            double y1 = height - margin - trainingCost.get(i) * costVerticalScale;
            double x2 = margin + (i + 1) * horizontalScale;
            double y2 = height - margin - trainingCost.get(i + 1) * costVerticalScale;
            graph.fill(new Ellipse2D.Double(x1 - radius, y1 - radius, radius * 2, radius * 2));
            graph.draw(new Line2D.Double(x1, y1, x2, y2));
        }
        // Final point
        x = margin + (numEpochs - 1) * horizontalScale;
        y = height - margin - trainingCost.get(numEpochs - 1) * costVerticalScale;
        graph.fill(new Ellipse2D.Double(x - radius, y - radius, radius * 2, radius * 2));
        graph.setPaint(Color.BLACK);

        // Validation cost
        graph.setPaint(validationCostColor);
        for (int i = 0; i < numEpochs - 1; i++) {
            // Draw line between adjacent points
            double x1 = margin + i * horizontalScale;
            double y1 = height - margin - validationCost.get(i) * costVerticalScale;
            double x2 = margin + (i + 1) * horizontalScale;
            double y2 = height - margin - validationCost.get(i + 1) * costVerticalScale;
            graph.fill(new Ellipse2D.Double(x1 - radius, y1 - radius, radius * 2, radius * 2));
            graph.draw(new Line2D.Double(x1, y1, x2, y2));
        }
        // Final point
        x = margin + (numEpochs - 1) * horizontalScale;
        y = height - margin - validationCost.get(numEpochs - 1) * costVerticalScale;
        graph.fill(new Ellipse2D.Double(x - radius, y - radius, radius * 2, radius * 2));
        graph.setPaint(Color.BLACK);

        // Stop here if not using accuracy data
        if (!trainingData.useAccuracy())
            return;

        // Y axis accuracy label
        AffineTransform accuracyAffineTransform = new AffineTransform();
        accuracyAffineTransform.rotate(Math.toRadians(90), 0, 0);
        Font accuracyLabelFont = defaultFont.deriveFont(accuracyAffineTransform);
        g.setFont(accuracyLabelFont);
        x = width - margin / 3 + metrics.getAscent() / 2;
        y = height / 2 + metrics.stringWidth(costLabel) / 2;
        graph.drawString(accuracyLabel, (float)x, (float)y); 
        g.setFont(defaultFont);

        // Y axis accuracy ticks
        List<Double> trainingAccuracy = trainingData.getTrainingAccuracy();
        List<Double> validationAccuracy = trainingData.getValidationAccuracy();
        maxHeight = 100;
        numYLabels = (int) (maxHeight / accuracyLabelInterval);
        double accuracyVerticalScale = (double)(height - 2 * margin) / maxHeight;
        for (int i = 0; i <= numYLabels; i++) {
            x = width - margin;
            y = height - margin - i * accuracyVerticalScale * accuracyLabelInterval;
            graph.draw(new Line2D.Double(x - tickLength / 2, y, x + tickLength / 2, y));

            String text = Double.toString(i * accuracyLabelInterval);
            x = x + margin / 3 - metrics.stringWidth(text) / 2;
            y = y + metrics.getAscent() / 2;
            graph.drawString(text, (float)x, (float)y);
        }

        // Train accuracy
        graph.setPaint(trainingAccuracyColor);
        for (int i = 0; i < numEpochs - 1; i++) {
            // Draw line between adjacent points
            double x1 = margin + i * horizontalScale;
            double y1 = height - margin - trainingAccuracy.get(i) * accuracyVerticalScale;
            double x2 = margin + (i + 1) * horizontalScale;
            double y2 = height - margin - trainingAccuracy.get(i + 1) * accuracyVerticalScale;
            graph.fill(new Ellipse2D.Double(x1 - radius, y1 - radius, radius * 2, radius * 2));
            graph.draw(new Line2D.Double(x1, y1, x2, y2));
        }
        // Final point
        x = margin + (numEpochs - 1) * horizontalScale;
        y = height - margin - trainingAccuracy.get(numEpochs - 1) * accuracyVerticalScale;
        graph.fill(new Ellipse2D.Double(x - radius, y - radius, radius * 2, radius * 2));
        graph.setPaint(Color.BLACK);

        // Validation accuracy
        graph.setPaint(validationAccuracyColor);
        for (int i = 0; i < numEpochs - 1; i++) {
            // Draw line between adjacent points
            double x1 = margin + i * horizontalScale;
            double y1 = height - margin - validationAccuracy.get(i) * accuracyVerticalScale;
            double x2 = margin + (i + 1) * horizontalScale;
            double y2 = height - margin - validationAccuracy.get(i + 1) * accuracyVerticalScale;
            graph.fill(new Ellipse2D.Double(x1 - radius, y1 - radius, radius * 2, radius * 2));
            graph.draw(new Line2D.Double(x1, y1, x2, y2));
        }
        // Final point
        x = margin + (numEpochs - 1) * horizontalScale;
        y = height - margin - validationAccuracy.get(numEpochs - 1) * accuracyVerticalScale;
        graph.fill(new Ellipse2D.Double(x - radius, y - radius, radius * 2, radius * 2));
        graph.setPaint(Color.BLACK);
    }

    public void setTrainingData(TrainingData trainingData) {
        this.trainingData = trainingData;
    }
}

*/

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

    private static final int LEGEND_WIDTH = 150;
    private static final int LEGEND_HEIGHT = 60;
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
        double horizontalScale = (double) (width - 2 * MARGIN) / (numEpochs - 1);

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

        for (int i = 0; i < numYLabels; i++) {
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
        AffineTransform originalTransform = graph.getTransform();
        AffineTransform transform = new AffineTransform();
        transform.rotate(Math.toRadians(angle), x, y);
        graph.setTransform(transform);
        graph.drawString(text, x - metrics.stringWidth(text) / 2, y);
        graph.setTransform(originalTransform);
    }

    public void setTrainingData(TrainingData trainingData) {
        this.trainingData = trainingData;
        repaint(); // Trigger a repaint when new data is set
    }
}