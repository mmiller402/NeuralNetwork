package ActivationFunctions;

public class SiLU extends ActivationFunction {
    public double f(double x) {
        double sigmoid = 1 / (1 + Math.exp(-x));
        return x * sigmoid;
    }

    public double df(double x) {
        double sigmoid = 1 / (1 + Math.exp(-x));
        return sigmoid + x * sigmoid * (1 - sigmoid);
    }
}