package Networks.FeedForward;

import ActivationFunctions.ActivationFunction;
import CostFunctions.CostFunction;

public class FeedForward_Settings {

    private int[] dimensions;

    private ActivationFunction hiddenActivation, outputActivation;
    private CostFunction costFunction;

    private double learningRateMin, learningRateMax;
    private int warmRestartInterval, warmRestartIntervalMult;

    private double momentumCoefficient, weightDecay;

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

    public double getLearningRateMin() {
        return learningRateMin;
    }

    public double getLearningRateMax() {
        return learningRateMax;
    }

    public void setLearningRate(double learningRateMin, double learningRateMax) {
        this.learningRateMin = learningRateMin;
        this.learningRateMax = learningRateMax;
    }

    public int getWarmRestartInterval() {
        return warmRestartInterval;
    }
    public int getWarmRestartIntervalMult() {
        return warmRestartIntervalMult;
    }

    public void setWarmRestart(int warmRestartInterval, int warmRestartIntervalMult) {
        this.warmRestartInterval = warmRestartInterval;
        this.warmRestartIntervalMult = warmRestartIntervalMult;
    }

    public double getMomentumCoefficient() {
        return momentumCoefficient;
    }

    public void setMomentumCoefficient(double momentumCoefficient) {
        this.momentumCoefficient = momentumCoefficient;
    }

    public double getWeightDecay() {
        return weightDecay;
    }

    public void setWeightDecay(double weightDecay) {
        this.weightDecay = weightDecay;
    }

    
}
