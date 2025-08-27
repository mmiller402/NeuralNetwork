package ActivationFunctions;

import java.io.Serializable;

public class ActivationFunction implements Serializable {

    // Serial ID
    private static final long serialVersionUID = 6529685098267757694L;

    public double f(double x) {
        return x;
    }

    public double df(double x) {
        return 1;
    }

    public double[] fArray(double[] x) {
        double[] array = new double[x.length];
        for (int i = 0; i < x.length; i++) {
            array[i] = f(x[i]);
        }
        return array;
    }

    public double[] dfArray(double[] x) {
        double[] array = new double[x.length];
        for (int i = 0; i < x.length; i++) {
            array[i] = df(x[i]);
        }
        return array;
    }
}