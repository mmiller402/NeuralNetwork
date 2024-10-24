package Data;
import java.awt.*;
import javax.swing.*;

// A class for displaying images from data points
public class ImageVisualizer extends JPanel {
    
    private int margin = 50;
    private ImageDataPoint dataPoint;

    public ImageVisualizer(ImageDataPoint dataPoint) {
        setDataPoint(dataPoint);
    }

    protected void paintComponent(Graphics g) {

        super.paintComponent(g);
        Graphics2D graph = (Graphics2D)g;

        graph.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        int width = getWidth();
        int height = getHeight();

        // Get array of pixels
        double[] pixelArray = dataPoint.getInputs();

        // Draw pixels
        int nRows = dataPoint.getXDim();
        int nCols = dataPoint.getYDim();
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

        // Draw label for current number
        FontMetrics metrics = g.getFontMetrics(getFont());
        String label = Integer.toString(getLabel(dataPoint));
        int x = width / 2 - metrics.stringWidth(label) / 2;
        int y = yMargin / 2 + metrics.getAscent() / 2;

        g.setColor(Color.BLACK);
        graph.drawString(label, x, y);
    }

    public int getLabel(DataPoint point) {
        double[] outputs = point.getOutputs();
        int label = 0;
        for (int i = 1; i < outputs.length; i++)
            label = (outputs[i] > outputs[label]) ? i : label; 
        return label;
    }

    public int getMargin() {
        return margin;
    }

    public void setMargin(int margin) {
        this.margin = margin;
    }

    public DataPoint getDataPoint() {
        return dataPoint;
    }
    
    public void setDataPoint(ImageDataPoint dataPoint) {
        this.dataPoint = dataPoint;
    }
}