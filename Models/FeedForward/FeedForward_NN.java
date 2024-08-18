package Models.FeedForward;

import ActivationFunctions.*;
import CostFunctions.*;

import java.util.Arrays;
import java.util.Random;

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
    private double[][][] weights;
    private double[][][] gradientW;

    // nodeError[i][j] = value of error of node j in layer i + 1
    //  (i must be less than the max number of layers)
    private double[][] nodeError;

    private ActivationFunction hiddenActivation;
    private ActivationFunction outputActivation;

    private CostFunction costFunction;

    private double learningRate;

    // L2 regularization parameter lambda
    private double lambda;

    // Proportion of hidden layer nodes that are set to 0
    private double dropoutRate;

    // ADAM parameters
    private double beta1; // First moment decay
    private double beta2; // Second moment decay
    private final double epsilon = 1e-8; // Tiny value to avoid division by 0
    private double[][][] mW; // First moment for weights
    private double[][][] vW; // Second moment for weights
    private double[][] mB; // First moment for biases
    private double[][] vB; // Second moment for biases

    // Random number generator
    private Random rnd;

    public FeedForward_NN(int[] dimensions, CostFunction costFunction, ActivationFunction hiddenActivation, ActivationFunction outputActivation, 
                          double learningRate, double lambda, double dropoutRate, double beta1, double beta2) {

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
        mB = new double[dimensions.length - 1][];
        vB = new double[dimensions.length - 1][];
        

        nodeError = new double [dimensions.length - 1][];
        for (int i = 0; i < dimensions.length - 1; i++) {
            biases[i] = new double[dimensions[i + 1]];
            gradientB[i] = new double[dimensions[i + 1]];
            mB[i] = new double[dimensions[i + 1]];
            vB[i] = new double[dimensions[i + 1]];

            nodeError[i] = new double[dimensions[i + 1]];
        }

        // Initialize weights array dimensions
        weights = new double[dimensions.length - 1][][];
        gradientW = new double[dimensions.length - 1][][];
        mW = new double[dimensions.length - 1][][];
        vW = new double[dimensions.length - 1][][];
        for (int i = 0; i < dimensions.length - 1; i++) {
            weights[i] = new double[dimensions[i]][];
            gradientW[i] = new double[dimensions[i]][];
            mW[i] = new double[dimensions[i]][];
            vW[i] = new double[dimensions[i]][];
            for (int j = 0; j < dimensions[i]; j++) {
                weights[i][j] = new double[dimensions[i + 1]];
                gradientW[i][j] = new double[dimensions[i + 1]];
                mW[i][j] = new double[dimensions[i + 1]];
                vW[i][j] = new double[dimensions[i + 1]];
            }
        }

        // Initialize functions
        this.costFunction = costFunction;
        
        this.hiddenActivation = hiddenActivation;
        this.outputActivation = outputActivation;

        this.learningRate = learningRate;
        this.lambda = lambda;
        this.dropoutRate = dropoutRate;

        this.beta1 = beta1;
        this.beta2 = beta2;

        // Create random number generator
        rnd = new Random();

        // Randomize weights to start
        randomizeWeights();
    }

    // Call when initializing model
    public void randomizeWeights() {
        // Fill up weights in a normal distribution
        for (int i = 0; i < weights.length; i++) {
            for (int j = 0; j < weights[i].length; j++) {
                for (int k = 0; k < weights[i][j].length; k++) {
                    weights[i][j][k] = rnd.nextGaussian() * Math.sqrt(2.0 / (values[i].length + values[i + 1].length));
                }
            }
        }
    }

    // Forward propagate given inputs and return a set of outputs
    public double[] forwardPropagate(double[] inputs, boolean training) {

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

                    // Reduce weights when not training due to dropout
                    double weight = weights[startLayer][startNode][endNode];
                    if (!training)
                        weight *= (1 - dropoutRate);

                    values[endLayer][endNode] += values[startLayer][startNode] * weight;
                }
                values[endLayer][endNode] += biases[startLayer][endNode];
            }

            if (endLayer < values.length - 1) {
                // Apply activation function
                outputs[endLayer] = hiddenActivation.fArray(values[endLayer]);

                // Apply dropout in hidden layers
                if (training) {
                    for (int node = 0; node < outputs[endLayer].length; node++) {
                        if (Math.random() < dropoutRate) {
                            values[endLayer][node] = 0;
                            outputs[endLayer][node] = 0;
                        }
                    }
                }
            } 
            else {
                // Apply output activation function with no dropout
                outputs[endLayer] = outputActivation.fArray(values[endLayer]);
            }
           
        }

        // Return array of outputs
        double[] returnArray = new double[outputs[outputs.length - 1].length];
        for (int i = 0; i < outputs[outputs.length - 1].length; i++) {
            returnArray[i] = outputs[outputs.length - 1][i];
        }
        return returnArray;
    }

    // Back propagate after a forward iteration
    public void backPropagate(double[] expectedOutputs) {

        // Reset error array
        resetError();
        
        // Backtrack through layers to get each node's errors and update weights and biases
        // Output layer
        int finalLayer = outputs.length - 1;
        double[] outputActivationDerivative = outputActivation.dfArray(values[finalLayer]);
        for (int node = 0; node < outputs[finalLayer].length; node++) {
            double costFunctionDerivative = costFunction.dcost(expectedOutputs[node], outputs[finalLayer][node]);
            nodeError[finalLayer - 1][node] = outputActivationDerivative[node] * costFunctionDerivative;
        }

        // Hidden layers
        for (int startLayer = finalLayer - 1; startLayer >= 1; startLayer--) {
            int endLayer = startLayer + 1;

            double[] hiddenActivationDerivative = hiddenActivation.dfArray(values[startLayer]);

            for (int startNode = 0; startNode < outputs[startLayer].length; startNode++) {
                for (int endNode = 0; endNode < outputs[endLayer].length; endNode++) {
                    nodeError[startLayer - 1][startNode] += weights[startLayer][startNode][endNode] * nodeError[endLayer - 1][endNode];

                    // Update weights
                    double d = nodeError[endLayer - 1][endNode] * outputs[startLayer][startNode];
                    gradientW[startLayer][startNode][endNode] += d;
                }
                nodeError[startLayer - 1][startNode] *= hiddenActivationDerivative[startNode];
            }

            // Update biases
            for (int endNode = 0; endNode < outputs[endLayer].length; endNode++) {
                gradientB[endLayer - 1][endNode] += nodeError[endLayer - 1][endNode];
            }
        }

        // Input layer
        for (int startNode = 0; startNode < outputs[0].length; startNode++) {
            for (int endNode = 0; endNode < outputs[1].length; endNode++) {
                // Update weights
                double d = nodeError[0][endNode] * outputs[0][startNode];
                gradientW[0][startNode][endNode] += d;
            }
        }

        // Update biases
        for (int endNode = 0; endNode < outputs[1].length; endNode++) {
            gradientB[0][endNode] += nodeError[0][endNode];
        }
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
        for (int i = 0; i < nodeError.length; i++) {
            for (int j = 0; j < nodeError[i].length; j++) {
                nodeError[i][j] = 0;
            }
        }
    }

    // Update weights and biases
    public void updateWeightsAndBiases(int numBatches, int batchSize, int epoch) {

        // Adjust learning rate
        //double alpha = learningRate * Math.exp(-0.001 * epoch);
        double alpha = learningRate;
        
        // Ensure gradients are not too large
        double gradientThreshold = 5.0;
        clipGradients(gradientW, gradientThreshold);

        // Adam updates
        for (int i = 0; i < weights.length; i++) {
            for (int j = 0; j < weights[i].length; j++) {
                for (int k = 0; k < weights[i][j].length; k++) {
                    // Update the first moment estimate
                    mW[i][j][k] = beta1 * mW[i][j][k] + (1 - beta1) * gradientW[i][j][k] / batchSize;
    
                    // Update the second moment estimate
                    vW[i][j][k] = beta2 * vW[i][j][k] + (1 - beta2) * (gradientW[i][j][k] / batchSize) * (gradientW[i][j][k] / batchSize);
    
                    // Compute bias-corrected estimates
                    double mW_hat = mW[i][j][k] / (1 - Math.pow(beta1, numBatches));
                    double vW_hat = vW[i][j][k] / (1 - Math.pow(beta2, numBatches));
    
                    // Update weights
                    weights[i][j][k] = weights[i][j][k] - alpha * (mW_hat / (Math.sqrt(vW_hat) + epsilon) + lambda * weights[i][j][k]);
    
                    // Reset gradient
                    gradientW[i][j][k] = 0;
                }
            }
        }
    
        // Update biases
        for (int i = 0; i < biases.length; i++) {
            for (int j = 0; j < biases[i].length; j++) {
                // Update the first moment estimate
                mB[i][j] = beta1 * mB[i][j] + (1 - beta1) * gradientB[i][j] / batchSize;
    
                // Update the second moment estimate
                vB[i][j] = beta2 * vB[i][j] + (1 - beta2) * (gradientB[i][j] / batchSize) * (gradientB[i][j] / batchSize);
    
                // Compute bias-corrected estimates
                double mB_hat = mB[i][j] / (1 - Math.pow(beta1, numBatches));
                double vB_hat = vB[i][j] / (1 - Math.pow(beta2, numBatches));
    
                // Update biases
                biases[i][j] += -alpha * mB_hat / (Math.sqrt(vB_hat) + epsilon);
    
                // Reset gradient
                gradientB[i][j] = 0;
            }
        }
    }

    private void clipGradients(double[][][] gradients, double threshold) {
        for (int i = 0; i < gradients.length; i++) {
            for (int j = 0; j < gradients[i].length; j++) {
                for (int k = 0; k < gradients[i][j].length; k++) {
                    if (Math.abs(gradients[i][j][k]) > threshold) {
                        gradients[i][j][k] = Math.signum(gradients[i][j][k]) * threshold;
                    }
                }
            }
        }
    }

    // Get current cost of model, given a set of expected outputs
    public double getCost(double[] expectedOutputs) {
        // Cost function
        double originalCost = costFunction.cost(expectedOutputs, outputs[outputs.length - 1]);

        // L2 Regularization
        double weightCost = 0;
        for (int i = 0; i < weights.length; i++) {
            for (int j = 0; j < weights[i].length; j++) {
                for (int k = 0; k < weights[i][j].length; k++) {
                    weightCost += lambda * weights[i][j][k] * weights[i][j][k] / 2;
                }
            }
        }

        //return originalCost + weightCost;
        return originalCost;
    }

    // Getters and setters
    public double[][] getValues() {
        return values;
    }

    public double[][] getOutputs() {
        return outputs;
    }

    public double[][] getBiases() {
        return biases;
    }

    public double[][][] getWeights() {
        return weights;
    }

    public ActivationFunction getHiddenActivation() {
        return hiddenActivation;
    }

    public void setHiddenActivation(ActivationFunction hiddenActivation) {
        this.hiddenActivation = hiddenActivation;
    }

    public ActivationFunction getOutputActivation() {
        return outputActivation;
    }

    public void setOutputActivation(ActivationFunction outputActivation) {
        this.outputActivation = outputActivation;
    }

    public CostFunction getCostFunction() {
        return costFunction;
    }

    public void setCostFunction(CostFunction costFunction) {
        this.costFunction = costFunction;
    }

    public double getLearningRate() {
        return learningRate;
    }

    public void setLearningRate(double learningRate) {
        this.learningRate = learningRate;
    }

    public double getLambda() {
        return lambda;
    }

    public void setLambda(double lambda) {
        this.lambda = lambda;
    }

    public double getDropoutRate() {
        return dropoutRate;
    }

    public void setDropoutRate(double dropoutRate) {
        this.dropoutRate = dropoutRate;
    }

    public double getBeta1() {
        return beta1;
    }

    public void setBeta1(double beta1) {
        this.beta1 = beta1;
    }

    public double getBeta2() {
        return beta2;
    }

    public void setBeta2(double beta2) {
        this.beta2 = beta2;
    }

    public double getEpsilon() {
        return epsilon;
    }

    public Random getRnd() {
        return rnd;
    }

    public void setRnd(Random rnd) {
        this.rnd = rnd;
    }
}
