package ActivationFunctions;

public class LeakyReLU extends ActivationFunction {
    private final double a = 0.01;

    public double f(double x) {
        return (x > 0) ? x : x * a;
    }

    public double df(double x) {
        return (x > 0) ? 1 : a;
    }
}
