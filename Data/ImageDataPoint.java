package Data;

import java.util.Random;

public class ImageDataPoint extends DataPoint {

    // Number of x and y values
    int xDim, yDim;

    // Array of inputs after transformations
    double[] transformedInputs;

    // Constructors
    public ImageDataPoint() {
        super();
    }

    public ImageDataPoint(int xDim, int yDim, int outputSize) {
        super(xDim * yDim, outputSize);
        this.xDim = xDim;
        this.yDim = yDim;
    }
    
    public ImageDataPoint(int xDim, int yDim, double[] inputs, double[] outputs) {
        super(inputs, outputs);
        assert(xDim * yDim == inputs.length);
        this.xDim = xDim;
        this.yDim = yDim;
    }


    // Returns a 2D array of the values, rather than a 1D array
    public double[][] getImage() {
        double[][] image = new double[xDim][yDim];
        double[] inputs = getInputs();

        for (int y = 0; y < yDim; y++) {
            for (int x = 0; x < xDim; x++) {
                image[x][y] = inputs[y * yDim + x];
            }
        }

        return image;
    }

    // Transform a greyscale drawing by the given parameters
    public void transformDrawing(double angleInDegrees, double scale, double xOffset, double yOffset, double noiseProbability, double noiseStrength) {

        // Dimensions
        double xDim = getXDim();
        double yDim = getYDim();

        // Get inputs and initialize transformed inputs
        double[] inputs = getUnfilteredInputs();
        transformedInputs = new double[inputs.length];

        // Convert angle and get iHat and jHat
        double angle = Math.toRadians(angleInDegrees);

        double iHatX = Math.cos(angle) / scale;
        double iHatY = Math.sin(angle) / scale;

        double jHatX = -iHatY;
        double jHatY = iHatX;

        for (int y = 0; y < yDim; y++) {
            for (int x = 0; x < xDim; x++) {
                double u = x / (xDim - 1);
                double v = y / (yDim - 1);

                double uTransformed = iHatX * (u - 0.5) + jHatX * (v - 0.5) + 0.5 - xOffset / xDim;
                uTransformed = Math.max(Math.min(uTransformed, 1), 0);
                double texX = uTransformed * (xDim - 1);

                double vTransformed = iHatY * (u - 0.5) + jHatY * (v - 0.5) + 0.5 - yOffset / yDim;
                vTransformed = Math.max(Math.min(vTransformed, 1), 0);
                double texY = vTransformed * (yDim - 1);

                // Integer part
                int xi = (int)texX;
                int yi = (int)texY;

                // Fractional part
                double xf = texX - xi;
                double yf = texY - yi;

                double interpolatedValue = 
                    (1 - xf) * (1 - yf) * pixelValue(xi, yi, inputs) +
                    (1 - xf) * yf * pixelValue(xi, yi + 1, inputs) +
                    xf * (1 - yf) * pixelValue(xi + 1, yi, inputs) +
                    xf * yf * pixelValue(xi + 1, yi + 1, inputs);

                // Noise
                double noiseValue = 0;
                if (Math.random() <= noiseProbability) {
                    noiseValue = (Math.random() - 0.5) * 2 * noiseStrength;
                }

                // Clamp between 0 and 1
                transformedInputs[y * (int)yDim + x] = Math.max(Math.min(interpolatedValue + noiseValue, 1), 0);
            }
        }
    }

    // Transform a greyscale drawing using random parameters
    public void transformDrawingRandom() {

        // Dimensions
        int xDim = getXDim();
        int yDim = getYDim();

        // Used for random in normal distribution
        Random rnd = new Random();

        double[] inputs = getUnfilteredInputs();

        // Angle
        double angleRange = 2;
        double angleInDegrees = rnd.nextGaussian() * angleRange;

        // Scale
        double scaleRange = 0.05;
        double scale = 1 + rnd.nextGaussian() * scaleRange;

        // Offset
        double offsetReductionFactor = 0.6;

        // Find bounds of offset
        int boundsMinX = xDim;
        int boundsMaxX = 0;
        int boundsMinY = yDim;
        int boundsMaxY = 0; 
        for (int y = 0; y < yDim; y++) {
            for (int x = 0; x < xDim; x++) {
                if (inputs[y * yDim + x] == 0)
                    continue;

                boundsMinX = Math.min(boundsMinX, x);
                boundsMaxX = Math.max(boundsMaxX, x);
                boundsMinY = Math.min(boundsMinY, y);
                boundsMaxY = Math.max(boundsMaxY, y);
            }
        }

        double offsetMinX = -boundsMinX;
        double offsetMaxX = xDim - boundsMaxX;
        double offsetMinY = -boundsMinY;
        double offsetMaxY = yDim - boundsMaxY;

        double xOffset = lerp(offsetMinX, offsetMaxX, Math.random()) * offsetReductionFactor;
        double yOffset = lerp(offsetMinY, offsetMaxY, Math.random()) * offsetReductionFactor;

        // Noise settings
        double noiseProbability = Math.min(Math.random(), Math.random()) * 0.05;
        double noiseStrength = Math.min(Math.random(), Math.random());

        // Transform
        transformDrawing(angleInDegrees, scale, xOffset, yOffset, noiseProbability, noiseStrength);
    }

    // Lerp function for transform
    private double lerp(double a, double b, double f) {
        return (a * (1.0 - f)) + (b * f);
    }

    // Reset the transformations
    public void resetTransformations() {
        transformedInputs = null;
    }

    // Get the value of a pixel given x and y coords
    private double pixelValue(int x, int y, double[] data) {
        return data[Math.min(y * getYDim() + x, data.length - 1)];
    }
    
    // Get the original inputs
    public double[] getUnfilteredInputs() {
        return super.getInputs();
    }

    // Get the transformed inputs if they exist or the original inputs
    public double[] getInputs() {
        return (transformedInputs != null) ? transformedInputs : getUnfilteredInputs();
    }

    // Getters and setters
    public int getXDim() {
        return xDim;
    }

    public void setXDim(int xDim) {
        this.xDim = xDim;
    }

    public int getYDim() {
        return yDim;
    }

    public void setYDim(int yDim) {
        this.yDim = yDim;
    }
}