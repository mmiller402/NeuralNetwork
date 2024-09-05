package Data.MNIST;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.*;

import Models.NeuralNetwork;
import Models.FeedForward.FeedForward_NN;

import java.util.Timer;
import java.util.TimerTask;

public class MnistDrawer extends JPanel {
    
    private double brushRadius = 1;

    private int margin = 50;
    private double[] pixelArray;

    private Timer timer;
    private TimerTask task;

    private NeuralNetwork model;

    public MnistDrawer(NeuralNetwork model) {
        this.model = model;

        // Initialize pixel array
        pixelArray = new double[28 * 28];

        // Intialize timer and timer task for drawing
        timer = new Timer();
        addMouseListener(new MouseAdapter() {

            // Start drawing when mouse is pressed
            public void mousePressed(MouseEvent me) {
                System.out.println("Mouse pressed: " + me);
                
                task = new TimerTask() {
                    public void run() {

                        PointerInfo info = MouseInfo.getPointerInfo();
                        Point pointOnScreen = info.getLocation();
                        Point jFramePoint = getLocationOnScreen();
                        double x = pointOnScreen.getX() - jFramePoint.getX();
                        double y = pointOnScreen.getY() - jFramePoint.getY();
                        System.out.println("Running timer task: x " + x + " y " + y);

                        int width = getWidth();
                        int height = getHeight();
                        int pixelSize = (Math.min(width, height) - margin * 2) / 28;
                        int imageSize = pixelSize * 28;
                        int xMargin = (width - imageSize) / 2;
                        int yMargin = (height - imageSize) / 2;

                        // Left click
                        if (me.getButton() == 1) {
                            drawCircle((x - xMargin) / pixelSize, (y - yMargin) / pixelSize, brushRadius);
                        } 
                        // Right click
                        else if (me.getButton() == 3) {
                            eraseCircle((x - xMargin) / pixelSize, (y - yMargin) / pixelSize, brushRadius);
                        }
                        // On center click, erase entire board
                        else if (me.getButton() == 2) {
                            pixelArray = new double[28 * 28];
                        }

                        repaint();
                    }
                };
                timer.scheduleAtFixedRate(task, 0, 2);
                
            }

            // Cancel drawing when mouse is released
            public void mouseReleased(MouseEvent me) {
                System.out.println("Mouse released: " + me);
                task.cancel();
            }
        });
    }

    protected void paintComponent(Graphics g) {

        super.paintComponent(g);
        Graphics2D graph = (Graphics2D)g;

        graph.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        int width = getWidth();
        int height = getHeight();

        // Draw pixels
        int nRows = 28;
        int nCols = 28;
        int pixelSize = (Math.min(width, height) - margin * 2) / nRows;
        int imageSize = pixelSize * nRows;
        int xMargin = (width - imageSize) / 2;
        int yMargin = (height - imageSize) / 2;

        for (int row = 0; row < nRows; row++) {
            for (int col = 0; col < nCols; col++) {
                int brightness = (int)(pixelArray[row * nRows + col] * 255);
                g.setColor(new Color(brightness, brightness, brightness));

                int x = xMargin + pixelSize * col;
                int y = yMargin + pixelSize * row;
                graph.fillRect(x, y, pixelSize, pixelSize);
            }
        }

        // Draw little boxes around the pixels
        g.setColor(Color.GRAY);
        for (int row = 1; row < nRows; row++) {
            int x = xMargin;
            int y = yMargin + pixelSize * row;
            graph.drawLine(x, y, x + imageSize - 1, y); // Idk why you need imageSize - 1 but it works
        }
        for (int col = 1; col < nCols; col++) {
            int x = xMargin + pixelSize * col;
            int y = yMargin;
            graph.drawLine(x, y, x, y + imageSize - 1); // Again, idk why you need imageSize - 1 but it works
        }

        // Draw model predictions on the left
        double[] modelPredictions = model.forwardPropagate(pixelArray, false);

        FontMetrics metrics = g.getFontMetrics(getFont());
        double stringHeight = metrics.getAscent();
        double totalStringHeight = stringHeight * modelPredictions.length;
        double totalStringMargin = imageSize - totalStringHeight;
        double stringMargin = totalStringMargin / (modelPredictions.length - 1);

        for (int i = 0; i < modelPredictions.length; i++) {
            double confidence = (int)(modelPredictions[i] * 10000) / 100.0;
            String displayString = i + ": " + confidence;
            double x = (xMargin - metrics.stringWidth(displayString)) / 2;

            double y = yMargin + stringHeight * (i + 1) + stringMargin * i;
            graph.drawString(displayString, (float)x, (float)y);
        }
        
    }

    private void drawCircle(double centerX, double centerY, double radius) {

        // Loop through every pixel value to see whether it is within circle
        for (int y = 0; y < 28; y++) {
            for (int x = 0; x < 28; x++) {
                double xMin = x;
                double xMax = x + 1;
                double yMin = y;
                double yMax = y + 1;

                double topLeftDist = Math.sqrt(Math.pow(centerX - xMin, 2) + Math.pow(centerY - yMin, 2));
                double topRightDist = Math.sqrt(Math.pow(centerX - xMax, 2) + Math.pow(centerY - yMin, 2));
                double bottomLeftDist = Math.sqrt(Math.pow(centerX - xMin, 2) + Math.pow(centerY - yMax, 2));
                double bottomRightDist = Math.sqrt(Math.pow(centerX - xMax, 2) + Math.pow(centerY - yMax, 2));

                // If not within circle at all, move on
                if (!(topLeftDist < radius || topRightDist < radius || bottomLeftDist < radius || bottomRightDist < radius)) 
                    continue;

                // If totally within circle, set value to 1 and move on
                if (topLeftDist < radius && topRightDist < radius && bottomLeftDist < radius && bottomRightDist < radius) {
                    pixelArray[y * 28 + x] = 1;
                    continue;
                }

                // If partially within circle, find percent value within circle
                int numSteps = 10;
                int numInCircle = 0;
                for (int yStep = 0; yStep < numSteps; yStep++) {
                    for (int xStep = 0; xStep < numSteps; xStep++) {
                        double dist = Math.sqrt(Math.pow(centerX - (x + (double)xStep / (numSteps - 1)), 2) + Math.pow(centerY - (y + (double)yStep / (numSteps - 1)), 2));
                        if (dist < radius)
                            numInCircle++;
                    }
                }
                pixelArray[y * 28 + x] = Math.max(pixelArray[y * 28 + x], (double)numInCircle / (numSteps * numSteps));
            }
        }
    }

    private void eraseCircle(double centerX, double centerY, double radius) {
        // Loop through every pixel value to see whether it is within circle
        for (int y = 0; y < 28; y++) {
            for (int x = 0; x < 28; x++) {
                double xMin = x;
                double xMax = x + 1;
                double yMin = y;
                double yMax = y + 1;

                double topLeftDist = Math.sqrt(Math.pow(centerX - xMin, 2) + Math.pow(centerY - yMin, 2));
                double topRightDist = Math.sqrt(Math.pow(centerX - xMax, 2) + Math.pow(centerY - yMin, 2));
                double bottomLeftDist = Math.sqrt(Math.pow(centerX - xMin, 2) + Math.pow(centerY - yMax, 2));
                double bottomRightDist = Math.sqrt(Math.pow(centerX - xMax, 2) + Math.pow(centerY - yMax, 2));

                // If not within circle at all, move on
                if (!(topLeftDist < radius || topRightDist < radius || bottomLeftDist < radius || bottomRightDist < radius)) 
                    continue;

                // If at all within circle, erase
                pixelArray[y * 28 + x] = 0;
            }
        }
    }
}
