package Networks.FeedForward;

import Networks.*;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import ActivationFunctions.*;
import CostFunctions.*;

public class FeedForward_NN {
    
    /*
     * values[i][j] = value of node j in layer i (before activation function)
     * outputs[i][j] = value of node j in layer i (after activation function)
     * biases[i][j] = bias added to node j of layer i + 1 
     *  (i must be less than the max number of layers)
     * weights[i][j][k] = weight of node j in layer i to node k in layer i + 1
     *  (i must be less than the max number of layers)
     */
    private double[][] values;
    private double[][] outputs;
    private double[][] biases;
    private double[][] gradientB;
    private double[][] velocityB;
    private double[][][] weights;
    private double[][][] gradientW;
    private double[][][] velocityW;

    // valuesErr[i][j] = value of error of node j in layer i + 1
    //  (i must be less than the max number of layers)
    private double[][] valuesErr;

    private ActivationFunction hiddenActivation;
    private ActivationFunction outputActivation;

    private CostFunction costFunction;

    private double learningRateMin;
    private double learningRateMax;
    private double momentumCoefficient;
    private double weightDecay;
    private int warmRestartInterval;
    private int warmRestartIntervalMult;

    private double averageError;
    private int tempIterations; // Counts every iteration of forward propagation and resets when weights and biases are updated
    private int numEpochs;

    public FeedForward_NN(int[] dimensions, CostFunction costFunction, ActivationFunction hiddenActivation, ActivationFunction outputActivation, 
                 double learningRateMin, double learningRateMax, double momentumCoefficient, double weightDecay, int warmRestartInterval, int warmRestartIntervalMult) {

        // Initialize values array dimensions
        values = new double[dimensions.length][];
        outputs = new double[dimensions.length][];
        for (int i = 0; i < dimensions.length; i++) {
            values[i] = new double[dimensions[i]];
            outputs[i] = new double[dimensions[i]];
        }

        // Initialize biases array dimensions
        biases = new double[dimensions.length - 1][];
        gradientB = new double[dimensions.length - 1][];
        velocityB = new double[dimensions.length - 1][];

        valuesErr = new double [dimensions.length - 1][];
        for (int i = 0; i < dimensions.length - 1; i++) {
            biases[i] = new double[dimensions[i + 1]];
            gradientB[i] = new double[dimensions[i + 1]];
            velocityB[i] = new double[dimensions[i + 1]];

            valuesErr[i] = new double[dimensions[i + 1]];
        }

        // Initialize weights array dimensions
        weights = new double[dimensions.length - 1][][];
        gradientW = new double[dimensions.length - 1][][];
        velocityW = new double[dimensions.length - 1][][];
        for (int i = 0; i < dimensions.length - 1; i++) {
            weights[i] = new double[dimensions[i]][];
            gradientW[i] = new double[dimensions[i]][];
            velocityW[i] = new double[dimensions[i]][];
            for (int j = 0; j < dimensions[i]; j++) {
                weights[i][j] = new double[dimensions[i + 1]];
                gradientW[i][j] = new double[dimensions[i + 1]];
                velocityW[i][j] = new double[dimensions[i + 1]];
            }
        }

        // Initialize functions
        this.costFunction = costFunction;
        
        this.hiddenActivation = hiddenActivation;
        this.outputActivation = outputActivation;

        this.learningRateMin = learningRateMin;
        this.learningRateMax = learningRateMax;
        this.momentumCoefficient = momentumCoefficient;
        this.weightDecay = weightDecay;

        this.warmRestartInterval = warmRestartInterval;
        this.warmRestartIntervalMult = warmRestartIntervalMult;
    }

    public FeedForward_NN(FeedForward_Settings settings) {
        this(settings.getDimensions(),
             settings.getCostFunction(),
             settings.getHiddenActivation(),
             settings.getOutputActivation(),
             settings.getLearningRateMin(),
             settings.getLearningRateMax(),
             settings.getMomentumCoefficient(),
             settings.getWeightDecay(),
             settings.getWarmRestartInterval(),
             settings.getWarmRestartIntervalMult());
    }

    public void randomizeWeights() {
        // Fill up weights in a normal distribution
        for (int i = 0; i < weights.length; i++) {
            for (int j = 0; j < weights[i].length; j++) {
                for (int k = 0; k < weights[i][j].length; k++) {
                    weights[i][j][k] = randomInNormalDistribution(0, 1) / Math.sqrt(weights[i].length);
                }
            }
        }
    }

    double randomInNormalDistribution(double mean, double standardDeviation) {
        double x1 = 1 - Math.random();
        double x2 = 1 - Math.random();

        double y1 = Math.sqrt(-2.0 * Math.log(x1)) * Math.cos(2.0 * Math.PI * x2);
        return y1 * standardDeviation + mean;
    }

    public double[] updateGradients(double[] inputs, double[] expectedOutputs) {
        double[] returnArray = forwardPropagate(inputs);
        backPropagate(expectedOutputs);
        resetValues();
        return returnArray;
    }

    public double[] forwardPropagate(double[] inputs) {

        // Reset values array
        resetValues();

        // Set input values
        for (int i = 0; i < inputs.length; i++) {
            values[0][i] = inputs[i];
            outputs[0][i] = inputs[i];
        }

        // Propagate every layer
        for (int startLayer = 0; startLayer < values.length - 1; startLayer++) {
            int endLayer = startLayer + 1;

            // Loop through end node in the outer loop to add bias at the end
            for (int endNode = 0; endNode < values[endLayer].length; endNode++) {
                for (int startNode = 0; startNode < values[startLayer].length; startNode++) {
                    values[endLayer][endNode] += values[startLayer][startNode] * weights[startLayer][startNode][endNode];
                }
                values[endLayer][endNode] += biases[startLayer][endNode];
            }

            // Apply activation function
            outputs[endLayer] = (endLayer < values.length - 1) ? hiddenActivation.fArray(values[endLayer]) : outputActivation.fArray(values[endLayer]);
        }

        // Return array of outputs
        double[] returnArray = new double[outputs[outputs.length - 1].length];
        for (int i = 0; i < outputs[outputs.length - 1].length; i++) {
            returnArray[i] = outputs[outputs.length - 1][i];
        }
        return returnArray;
    }

    public double backPropagate(double[] expectedOutputs) {

        // Reset error array
        resetError();
        
        // Find error value
        double error = costFunction.cost(expectedOutputs, outputs[outputs.length - 1]);
        if (Double.isNaN(error)) {
            System.out.println("Oops");
        }

        averageError += error;

        // Backtrack through layers to get each node's errors and update weights and biases
        // Output layer
        int finalLayer = outputs.length - 1;
        valuesErr[finalLayer - 1] = outputActivation.dfArray(values[finalLayer]);
        for (int node = 0; node < outputs[finalLayer].length; node++) {
            valuesErr[finalLayer - 1][node] *= costFunction.dcost(expectedOutputs[node], outputs[finalLayer][node]);
        }

        // Hidden layers
        for (int startLayer = finalLayer - 1; startLayer >= 1; startLayer--) {
            int endLayer = startLayer + 1;

            double[] activationDerivative = hiddenActivation.dfArray(values[startLayer]);

            for (int startNode = 0; startNode < values[startLayer].length; startNode++) {
                for (int endNode = 0; endNode < values[endLayer].length; endNode++) {
                    valuesErr[startLayer - 1][startNode] += weights[startLayer][startNode][endNode] * valuesErr[endLayer - 1][endNode];

                    // Update weights
                    double d = valuesErr[endLayer - 1][endNode] * outputs[startLayer][startNode];
                    //System.out.print(d + " ");
                    gradientW[startLayer][startNode][endNode] += d;
                }
                valuesErr[startLayer - 1][startNode] *= activationDerivative[startNode];
            }

            // Update biases
            for (int endNode = 0; endNode < values[endLayer].length; endNode++) {
                gradientB[endLayer - 1][endNode] += valuesErr[endLayer - 1][endNode];
            }
        }

        // Input layer
        for (int startNode = 0; startNode < values[0].length; startNode++) {
            for (int endNode = 0; endNode < values[1].length; endNode++) {
                // Update weights
                double d = valuesErr[0][endNode] * outputs[0][startNode];
                //System.out.print(d + " ");
                gradientW[0][startNode][endNode] += d;
            }
        }

        // Update biases
        for (int endNode = 0; endNode < values[1].length; endNode++) {
            gradientB[0][endNode] += valuesErr[0][endNode];
        }

        tempIterations++;

        // Return error
        return error;
    }

    // Called before forward propagation
    public void resetValues() {
        for (int i = 0; i < values.length; i++) {
            for (int j = 0; j < values[i].length; j++) {
                values[i][j] = 0;
            }
        }
    }

    // Called before backpropagation
    public void resetError() {
        for (int i = 0; i < valuesErr.length; i++) {
            for (int j = 0; j < valuesErr[i].length; j++) {
                valuesErr[i][j] = 0;
            }
        }
    }

    // Returns average error over this generation
    public double updateWeightsAndBiases() {
        // Calculate the alpha for this update
        double alpha = learningRateMin + (learningRateMax - learningRateMin) / 2 * (1 + Math.cos((numEpochs % warmRestartInterval) / warmRestartInterval * Math.PI));

        for (int i = 0; i < weights.length; i++) {
            for (int j = 0; j < weights[i].length; j++) {
                for (int k = 0; k < weights[i][j].length; k++) {
                    double velocity = velocityW[i][j][k] * momentumCoefficient - gradientW[i][j][k] / tempIterations * alpha;
                    velocityW[i][j][k] = velocity;
                    weights[i][j][k] += velocity + weights[i][j][k] * alpha * weightDecay;
                    gradientW[i][j][k] = 0;
                }
            }
        }

        for (int i = 0; i < biases.length; i++) {
            for (int j = 0; j < biases[i].length; j++) {
                double velocity = velocityB[i][j] * momentumCoefficient - gradientB[i][j] / tempIterations * alpha;
                velocityB[i][j] = velocity;
                biases[i][j] += velocity;
                gradientB[i][j] = 0;
            }
        }

        numEpochs++;
        if (numEpochs % warmRestartInterval == 0)
            warmRestartInterval *= warmRestartIntervalMult;

        // Average error
        averageError /= tempIterations;
        double tempError = averageError;
        averageError = 0;
        tempIterations = 0;
        return tempError;
    }

    // Conduct all forward and backward propagations allowed by train data then return the accuracy on the test data
    public void learn(DataPoint[] trainData, int batchSize, int numIterations, NetworkTrainingGraph graph) {

        // Get JFrame that graph is attached to
        JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(graph);

        // Loop through all data points numIterations number of times
        for (int iteration = 0; iteration < numIterations; iteration++) {

            // Loop through all data points
            for (int pointIndex = 0; pointIndex < trainData.length; pointIndex++) {

                // Feed data forward and backward
                forwardPropagate(trainData[pointIndex].getInputs());
                backPropagate(trainData[pointIndex].getOutputs());

                // Update after every batch
                if (pointIndex % batchSize == 0) {
                    double averageError = updateWeightsAndBiases();

                    System.out.println("Count: " + (pointIndex + iteration * trainData.length) + " | Average Error: " + averageError);
                    graph.addDataPoint(averageError);
                    frame.repaint();
                }
            }
        }
    }

    @Override
    public String toString() {
        String returnString = "";
        String format = "%.4g";

        // Outputs
        returnString += "outputs=[";
        for (int i = 0; i < outputs.length; i++) {
            returnString += "[";
            for (int j = 0; j < outputs[i].length; j++) {
                returnString += String.format(format, outputs[i][j]) + ",";
            }
            returnString += "\b],";
        }
        returnString += "\b]\n";

        // Weights
        returnString += "weights=[";
        for (int i = 0; i < weights.length; i++) {
            returnString += "[";
            for (int j = 0; j < weights[i].length; j++) {
                returnString += "[";
                for (int k = 0; k < weights[i][j].length; k++) {
                    returnString += String.format(format, weights[i][j][k]) + ",";
                }
                returnString += "\b],";
            }
            returnString += "\b],";
        }
        returnString += "\b]\n";

        // Biases
        returnString += "biases=[";
        for (int i = 0; i < biases.length; i++) {
            returnString += "[";
            for (int j = 0; j < biases[i].length; j++) {
                returnString += String.format(format, biases[i][j]) + ",";
            }
            returnString += "\b],";
        }
        returnString += "\b]\n";

        
        return returnString;
    }
}
