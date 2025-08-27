package Models;

import java.io.Serializable;
import java.util.Random;

import ActivationFunctions.ActivationFunction;
import CostFunctions.CostFunction;

public class FeedForward_Layer implements Serializable {
    
    // Serial ID
    private static final long serialVersionUID = 6529685098267757692L;

    // values is before activations, outputs is after
    private double[] values;
    private double[] outputs;
    private double[] nodeError;
    private double[] dropoutMask;

    // weights[i][j] goes from input node i to this layer node j
    private double[][] weights;
    private double[][] gradientW;

    // biases[i] goes to this layer node i
    private double[] biases;
    private double[] gradientB;

    // L2 regularization parameter lambda
    private double lambda;

    // ADAM parameters
    private final double epsilon = 1e-8; // Tiny value to avoid division by 0
    private double[][] mW; // First moment for weights
    private double[][] vW; // Second moment for weights
    private double[] mB; // First moment for biases
    private double[] vB; // Second moment for biases

    private ActivationFunction activation;

    public FeedForward_Layer(int inDim, int outDim, ActivationFunction activationFunction) {

        // Initialize array sizes
        values = new double[outDim];
        outputs = new double[outDim];
        nodeError = new double[outDim];
        dropoutMask = new double[outDim];

        weights = new double[inDim][outDim];
        gradientW = new double[inDim][outDim];
        mW = new double[inDim][outDim];
        vW = new double[inDim][outDim];

        biases = new double[outDim];
        gradientB = new double[outDim];
        mB = new double[outDim];
        vB = new double[outDim];

        // Initialize weights with Kaiming He initialization
        randomizeWeights();

        this.activation = activationFunction;
    }


    private void randomizeWeights() {
        Random rnd = new Random();
        for (int inNode = 0; inNode < weights.length; inNode++) {
            for (int outNode = 0; outNode < weights[inNode].length; outNode++) {
                weights[inNode][outNode] = rnd.nextGaussian() * Math.sqrt(2.0 / weights.length);
            }
        }
    }

    // Feed inputs through the layer and output an array of outputs
    public double[] forwardPropagate(double[] inputs, boolean training, double dropoutRate) {
        
        // Reset values array
        values = new double[values.length];

        // Feed values through weights and biases
        for (int outNode = 0; outNode < values.length; outNode++) {
            for (int inNode = 0; inNode < inputs.length; inNode++) {
                // Add input * weight to values
                values[outNode] += inputs[inNode] * weights[inNode][outNode];
            }
            // Add bias to values
            values[outNode] += biases[outNode];
        }

        // Apply dropout mask
        if (training) {
            applyDropout(dropoutRate);
            for (int outNode = 0; outNode < values.length; outNode++) {
                values[outNode] *= dropoutMask[outNode];
            }
        }

        // Activate values to relay to outputs
        outputs = activation.fArray(values);

        return outputs;
    }

    private void applyDropout(double dropoutRate) {
        // Dropout mask where 1/(1-dropoutRate) scales the outputs during training
        double scale = 1.0 / (1.0 - dropoutRate);

        for (int i = 0; i < dropoutMask.length; i++) {
            dropoutMask[i] = (Math.random() < dropoutRate) ? 0.0 : scale;
        }
    }

    // Back propagate and fill up node errors
    public double[] backPropagateHiddenLayer(double[] nextLayerError, double[][] nextLayerWeights) {

        // Reset node error array
        nodeError = new double[nodeError.length];

        // Calculate node errors for this layer
        double[] activationDerivative = activation.dfArray(values);

        // inNode refers to the inNode for the next layer, not this one (same with outNode)
        for (int inNode = 0; inNode < values.length; inNode++) {
            for (int outNode = 0; outNode < nextLayerError.length; outNode++) {
                nodeError[inNode] += nextLayerError[outNode] * nextLayerWeights[inNode][outNode];
            }
            nodeError[inNode] *= activationDerivative[inNode];
        }

        // Returns this layer's node errors
        return nodeError;
    }

    // Back propagate and fill up node errors
    public double[] backPropagateOutputLayer(double[] expectedOutputs, CostFunction costFunction) {

        // Calculate node errors for the output layer
        double[] activationDerivative = activation.dfArray(values);

        for (int outNode = 0; outNode < values.length; outNode++) {
            double costFunctionDerivative = costFunction.dcost(expectedOutputs[outNode], outputs[outNode]);
            nodeError[outNode] = costFunctionDerivative * activationDerivative[outNode];
        }

        // Returns this layer's node errors
        return nodeError;
    }

    // Update gradients after backpropagation
    public void updateGradients(double[] inputs) {
        for (int outNode = 0; outNode < values.length; outNode++) {
            for (int inNode = 0; inNode < inputs.length; inNode++) {
                gradientW[inNode][outNode] += inputs[inNode] * nodeError[outNode];
            }
            gradientB[outNode] += nodeError[outNode];
        }
    }

    // Update weights and biases with ADAM optimization
    public void updateWeightsAndBiases(double learningRate, double beta1, double beta2, double batchSize, int numBatches) {
        for (int outNode = 0; outNode < values.length; outNode++) {
            for (int inNode = 0; inNode < weights.length; inNode++) {
                // Update the first moment estimate
                mW[inNode][outNode] = beta1 * mW[inNode][outNode] + (1 - beta1) * gradientW[inNode][outNode] / batchSize;

                // Update the second moment estimate
                vW[inNode][outNode] = beta2 * vW[inNode][outNode] + (1 - beta2) * (gradientW[inNode][outNode] / batchSize) * (gradientW[inNode][outNode] / batchSize);

                // Compute bias-corrected estimates
                double mW_hat = mW[inNode][outNode] / (1 - Math.pow(beta1, numBatches));
                double vW_hat = vW[inNode][outNode] / (1 - Math.pow(beta2, numBatches));

                // Update weights
                weights[inNode][outNode] = weights[inNode][outNode] - learningRate * (mW_hat / (Math.sqrt(vW_hat) + epsilon) + lambda * weights[inNode][outNode]);

                // Reset gradient
                gradientW[inNode][outNode] = 0;
            }

            // Update the first moment estimate
            mB[outNode] = beta1 * mB[outNode] + (1 - beta1) * gradientB[outNode] / batchSize;
    
            // Update the second moment estimate
            vB[outNode] = beta2 * vB[outNode] + (1 - beta2) * (gradientB[outNode] / batchSize) * (gradientB[outNode] / batchSize);

            // Compute bias-corrected estimates
            double mB_hat = mB[outNode] / (1 - Math.pow(beta1, numBatches));
            double vB_hat = vB[outNode] / (1 - Math.pow(beta2, numBatches));

            // Update biases
            biases[outNode] = biases[outNode] - learningRate * mB_hat / (Math.sqrt(vB_hat) + epsilon);

            // Reset gradient
            gradientB[outNode] = 0;
        }
    }

    // Getters and Setters
    public double[] getValues() {
        return values;
    }

    public double[] getOutputs() {
        return outputs;
    }

    public double[][] getWeights() {
        return weights;
    }

    public double[] getBiases() {
        return biases;
    }

    public double getLambda() {
        return lambda;
    }

    public void setLambda(double lambda) {
        this.lambda = lambda;
    }

    public double getEpsilon() {
        return epsilon;
    }

    public ActivationFunction getActivation() {
        return activation;
    }

    public void setActivation(ActivationFunction activation) {
        this.activation = activation;
    }
}
