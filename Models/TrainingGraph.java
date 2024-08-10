package Models;

import java.awt.*;
import java.awt.geom.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import javax.swing.JPanel;

public class TrainingGraph extends JPanel {
    
    // String labels
    private String title;
    private String xAxisLabel;
    private String yAxisLabel;

    private int margin;

    // Radius of plot points
    private int radius;

    // Axis labels
    private int tickLength; // Length of tick marks on axes
    private int xLabelInterval;
    private double yLabelInterval;
    
    // Data
    private List<Point2D.Double> points;
    private double maxHeight;
    private double maxWidth;

    // Constructor
    public TrainingGraph(String title, String xAxisLabel, String yAxisLabel, int margin, int radius,
                              int tickLength, int xLabelInterval, double yLabelInterval, double maxWidth, double maxHeight) {
        this.title = title;
        this.xAxisLabel = xAxisLabel;
        this.yAxisLabel = yAxisLabel;
        this.margin = margin;
        this.radius = radius;
        this.tickLength = tickLength;
        this.xLabelInterval = xLabelInterval;
        this.yLabelInterval = yLabelInterval;
        this.maxWidth = maxWidth;
        this.maxHeight = maxHeight;

        points = new ArrayList<Point2D.Double>();
    }

    // Constructor without maxWidth or maxHeight
    public TrainingGraph(String title, String xAxisLabel, String yAxisLabel, int margin, int radius,
                              int tickLength, int xLabelInterval, double yLabelInterval) {
        this(title, xAxisLabel, yAxisLabel, margin, radius, tickLength, xLabelInterval, yLabelInterval, 0, 0);
    }

    // Draw function
    protected void paintComponent(Graphics g) {

        // Initialize local variables to be reused
        double x, y;

        super.paintComponent(g);
        Graphics2D graph = (Graphics2D)g;

        graph.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int width = getWidth();
        int height = getHeight();

        // Draw graph axes
        graph.drawLine(margin, margin, margin, height - margin);
        graph.drawLine(margin, height - margin, width - margin, height - margin);

        // Draw graph labels
        FontMetrics metrics = g.getFontMetrics(getFont());

        Font defaultFont = g.getFont();
        AffineTransform affineTransform = new AffineTransform();
        affineTransform.rotate(Math.toRadians(-90), 0, 0);
        Font rotatedFont = defaultFont.deriveFont(affineTransform);

        // Y axis label
        g.setFont(rotatedFont);
        x = margin / 3 + metrics.getAscent() / 2;
        y = height / 2 + metrics.stringWidth(yAxisLabel) / 2;
        graph.drawString(yAxisLabel, (float)x, (float)y); 
        g.setFont(defaultFont);

        // X axis label
        x = width / 2 - metrics.stringWidth(xAxisLabel) / 2;
        y = height - margin / 3 + metrics.getAscent() / 2;
        graph.drawString(xAxisLabel, (float)x, (float)y);

        // Title
        x = width / 2 - metrics.stringWidth(title) / 2;
        y = margin / 2 + metrics.getAscent() / 2;
        graph.drawString(title, (float)x, (float)y);
        

        // X axis ticks
        int numXLabels = (points.size() - 1) / xLabelInterval;
        double xLabelScale = (double)(width - 2 * margin) / (points.size() - 1);
        for (int i = 0; i <= numXLabels; i++) {
            x = margin + i * xLabelScale * xLabelInterval;
            y = height - margin;
            graph.draw(new Line2D.Double(x, y - tickLength / 2, x, y + tickLength / 2));

            String text = Integer.toString(i * xLabelInterval);
            x = x - metrics.stringWidth(text) / 2;
            y = y + margin / 3 + metrics.getAscent() / 2;
            graph.drawString(text, (float)x, (float)y);
        }

        // Y axis ticks
        int numYLabels = (int) (maxHeight / yLabelInterval);
        double yLabelScale = (double)(height - 2 * margin) / maxHeight;
        for (int i = 0; i <= numYLabels; i++) {
            x = margin;
            y = height - margin - i * yLabelScale * yLabelInterval;
            graph.draw(new Line2D.Double(x - tickLength / 2, y, x + tickLength / 2, y));

            String text = Double.toString(i * yLabelInterval);
            x = x - margin / 3 - metrics.stringWidth(text) / 2;
            y = y + metrics.getAscent() / 2;
            graph.drawString(text, (float)x, (float)y);
        }
        
        // Find distance between points and vertical scale
        double horizontalScale = (double)(width - 2 * margin) / (points.size() - 1);
        double verticalScale = (double)(height - 2 * margin) / maxHeight;

        //set color for points
        graph.setPaint(Color.RED);

        if (points.size() > 0) {

            // Draw data
            for (int i = 0; i < points.size() - 1; i++) {
                // Draw line between adjacent points
                double x1 = margin + points.get(i).getX() * horizontalScale;
                double y1 = height - margin - points.get(i).getY() * verticalScale;
                double x2 = margin + points.get(i + 1).getX() * horizontalScale;
                double y2 = height - margin - points.get(i + 1).getY() * verticalScale;
                graph.fill(new Ellipse2D.Double(x1 - radius, y1 - radius, radius * 2, radius * 2));
                graph.draw(new Line2D.Double(x1, y1, x2, y2));
            }

            // Final point
            x = margin + points.get(points.size() - 1).getX() * horizontalScale;
            y = height - margin - points.get(points.size() - 1).getY() * verticalScale;
            graph.fill(new Ellipse2D.Double(x - radius, y - radius, radius * 2, radius * 2));
        }
    }

    // Getters and Setters
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getxAxisLabel() {
        return xAxisLabel;
    }

    public void setxAxisLabel(String xAxisLabel) {
        this.xAxisLabel = xAxisLabel;
    }

    public String getyAxisLabel() {
        return yAxisLabel;
    }

    public void setyAxisLabel(String yAxisLabel) {
        this.yAxisLabel = yAxisLabel;
    }

    public int getMargin() {
        return margin;
    }

    public void setMargin(int margin) {
        this.margin = margin;
    }

    public int getRadius() {
        return radius;
    }

    public void setRadius(int radius) {
        this.radius = radius;
    }

    public int getTickLength() {
        return tickLength;
    }

    public void setTickLength(int tickLength) {
        this.tickLength = tickLength;
    }

    public int getxLabelInterval() {
        return xLabelInterval;
    }

    public void setxLabelInterval(int xLabelInterval) {
        this.xLabelInterval = xLabelInterval;
    }

    public double getyLabelInterval() {
        return yLabelInterval;
    }

    public void setyLabelInterval(double yLabelInterval) {
        this.yLabelInterval = yLabelInterval;
    }

    public List<Point2D.Double> getPoints() {
        return points;
    }

    public void addPoint(double x, double y) {
        // Adjust max width and max height accordingly
        maxWidth = Math.max(maxWidth, x);
        maxHeight = Math.max(maxHeight, y);

        // Add to points list
        Point2D.Double point = new Point2D.Double(x, y);
        points.add(point);

        // Sort points by x coordinates to ensure lines match up correctly
        points.sort(Comparator.comparingDouble(Point2D::getX));

        // Repaint and update graph
        repaint();
    }

    public double getMaxWidth() {
        return maxWidth;
    }

    public void setMaxWidth(double maxWidth) {
        this.maxWidth = maxWidth;
    }

    public double getMaxHeight() {
        return maxHeight;
    }

    public void setMaxHeight(double maxHeight) {
        this.maxHeight = maxHeight;
    }
}
