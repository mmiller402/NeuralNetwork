package ActivationFunctions;

public class SiLU extends ActivationFunction {
    public double f(double x) {
        return x / (1 + Math.exp(-x));
    }

    public double df(double x) {
        double sig = 1 / (1 + Math.exp(-x));
        return sig * (1 + x * (1 - sig));
    }
}
