package MNIST;

public class MnistVisualizer {
    
}

/*
package MNIST;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.*;
import java.awt.geom.*;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import Networks.*;

public class MnistVisualizer extends JPanel {
    
    private int margin = 50;
    private DataPoint dataPoint;
    private Timer timer;
    private TimerTask task;

    private DataPoint[] testData;
    private int[] guessedLabels;
    private ArrayList<Integer> wrongGuessIndices;
    private int currentViewIndex = 0;

    public MnistVisualizer() {
        timer = new Timer();
        addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent me) {
                System.out.println("Mouse pressed: " + me);
                setDataPoint(testData[wrongGuessIndices.get(++currentViewIndex)]);
                if (currentViewIndex > wrongGuessIndices.size())
                    currentViewIndex = 0;
                
                task = new TimerTask() {
                    public void run() {
                        PointerInfo a = MouseInfo.getPointerInfo();
                        Point pointOnScreen = a.getLocation();
                        Point jFramePoint = getLocationOnScreen();
                        double x = pointOnScreen.getX() - jFramePoint.getX();
                        double y = pointOnScreen.getY() - jFramePoint.getY();
                        System.out.println("Running timer task: x " + x + " y " + y);
                    }
                };
                timer.scheduleAtFixedRate(task, 0, 1000);
                
            }
            public void mouseReleased(MouseEvent me) {
                System.out.println("Mouse released: " + me);
                //task.cancel();
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
        int nRows = dataPoint.getNumberOfRows();
        int nCols = dataPoint.getNumberOfColumns();
        int pixelSize = (Math.min(width, height) - margin * 2) / nRows;
        int imageSize = pixelSize * nRows;
        int xMargin = (width - imageSize) / 2;
        int yMargin = (height - imageSize) / 2;

        for (int row = 0; row < nRows; row++) {
            for (int col = 0; col < nCols; col++) {
                int brightness = (int)(dataPoint.getValue(row, col) * 255);
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

        // Draw label for current number
        FontMetrics metrics = g.getFontMetrics(getFont());
        String label = Integer.toString(dataPoint.getLabel());
        int x = width / 2 - metrics.stringWidth(label) / 2;
        int y = yMargin / 2 + metrics.getAscent() / 2;

        g.setColor(Color.BLACK);
        graph.drawString(label, x, y);
    }

    public int getMargin() {
        return margin;
    }

    public void setMargin(int margin) {
        this.margin = margin;
    }

    public MnistDataPoint getDataPoint() {
        return dataPoint;
    }
    
    public void setDataPoint(MnistDataPoint dataPoint) {
        this.dataPoint = dataPoint;
    }
}
*/