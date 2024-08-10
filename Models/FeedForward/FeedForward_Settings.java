package Models.FeedForward;

import ActivationFunctions.ActivationFunction;
import CostFunctions.CostFunction;

public class FeedForward_Settings {

    private int[] dimensions;

    private ActivationFunction hiddenActivation, outputActivation;
    private CostFunction costFunction;

    private double learningRate;

    private double regularization;

    private double beta1, beta2;

    public int[] getDimensions() {
        return dimensions;
    }

    public void setDimensions(int[] dimensions) {
        this.dimensions = dimensions;
    }

    public ActivationFunction getHiddenActivation() {
        return hiddenActivation;
    }

    public ActivationFunction getOutputActivation() {
        return outputActivation;
    }

    public void setActivations(ActivationFunction hiddenActivation, ActivationFunction outputActivation) {
        this.hiddenActivation = hiddenActivation;
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

    public double getRegularization() {
        return regularization;
    }

    public void setRegularization(double regularization) {
        this.regularization = regularization;
    }

    public double getBeta1() {
        return beta1;
    }

    public double getBeta2() {
        return beta2;
    }

    public void setBeta(double beta1, double beta2) {
        this.beta1 = beta1;
        this.beta2 = beta2;
    }
    
}
