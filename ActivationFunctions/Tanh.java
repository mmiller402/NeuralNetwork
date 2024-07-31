package ActivationFunctions;

public class Tanh extends ActivationFunction {
    public double f(double x) {
        return (Math.exp(x) - Math.exp(-x)) / (Math.exp(x) + Math.exp(-x));
    }

    public double df(double x) {
        return 1 - Math.pow(f(x), 2);
    }
}
