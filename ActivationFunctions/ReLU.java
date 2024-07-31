package ActivationFunctions;

public class ReLU extends ActivationFunction {
    public double f(double x) {
        return (x > 0) ? x : 0;
    }

    public double df(double x) {
        return (x > 0) ? 1 : 0;
    }
}
