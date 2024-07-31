package ActivationFunctions;

public class Sigmoid extends ActivationFunction {
    public double f(double x) {
        return 1 / (1 + Math.exp(-x));
    }

    public double df(double x) {
        return this.f(x) * (1 - this.f(x));
    }
}