package Models;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import ActivationFunctions.ActivationFunction;
import CostFunctions.CostFunction;

public class NeuralNetwork implements Serializable {
    
    // Layers in the network
    // TODO Add functionality for different layer types
    private FeedForward_Layer[] layers;

    // Activation and cost functions of the layers
    private ActivationFunction hiddenActivation, outputActivation;
    private CostFunction costFunction;

    // Learning rate of system
    private double learningRate;

    // Dropout rate for regularization
    private double dropoutRate;

    // ADAM parameters
    private double beta1; // First moment decay
    private double beta2; // Second moment decay

    // Constructor
    public NeuralNetwork(int[] dim, ActivationFunction hiddenActivation, ActivationFunction outputActivation,
            CostFunction costFunction, double learningRate, double beta1, double beta2, double dropoutRate) {
        this.hiddenActivation = hiddenActivation;
        this.outputActivation = outputActivation;
        this.costFunction = costFunction;
        this.learningRate = learningRate;
        this.beta1 = beta1;
        this.beta2 = beta2;
        this.dropoutRate = dropoutRate;

        createLayers(dim, hiddenActivation, outputActivation);
    }

    // Propagate layers array with feed forward layers
    private void createLayers(int[] dim, ActivationFunction hiddenActivation, ActivationFunction outputActivation) {
        layers = new FeedForward_Layer[dim.length - 1];

        // Create hidden layers
        for (int layer = 0; layer < layers.length - 1; layer++) {
            layers[layer] = new FeedForward_Layer(dim[layer], dim[layer + 1], hiddenActivation);
        }

        // Create final layer with output activation
        layers[layers.length - 1] = new FeedForward_Layer(dim[layers.length - 1], dim[layers.length], outputActivation);
    }

    // Forward propagate inputs through layers and return the output
    public double[] forwardPropagate(double[] inputs, boolean training) {
        for (int layer = 0; layer < layers.length - 1; layer++) {
            inputs = layers[layer].forwardPropagate(inputs, training, dropoutRate);
        }

        // Final layer
        double[] outputs = layers[layers.length - 1].forwardPropagate(inputs, training, 0);

        return outputs;
    }

    // Back propagate error through layers and update gradients
    public void backPropagate(double[] inputs, double[] expectedOutputs) {
        // Final layer
        double[] error = layers[layers.length - 1].backPropagateOutputLayer(expectedOutputs, costFunction);
        double[] prevLayerOutputs = (layers.length == 1) ? inputs : layers[layers.length - 2].getOutputs();
        layers[layers.length - 1].updateGradients(prevLayerOutputs);

        // Hidden layers
        for (int layer = layers.length - 2; layer >= 0; layer--) {
            error = layers[layer].backPropagateHiddenLayer(error, layers[layer + 1].getWeights());
            prevLayerOutputs = (layer == 0) ? inputs : layers[layer - 1].getOutputs();
            layers[layer].updateGradients(prevLayerOutputs);
        }
    }

    // Update all weights and biases with ADAM optimizer
    public void updateWeightsAndBiases(int batchSize, int numBatches) {
        for (int layer = 0; layer < layers.length; layer++) {
            layers[layer].updateWeightsAndBiases(learningRate, beta1, beta2, batchSize, numBatches);
        }
    }

    // Return the cost
    public double getCost(double[] expectedOutputs, double[] calculatedOutputs) {
        return costFunction.cost(expectedOutputs, calculatedOutputs);
    }
}
