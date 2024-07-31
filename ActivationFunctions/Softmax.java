package ActivationFunctions;

public class Softmax extends ActivationFunction {
    public double[] fArray(double[] x) {
        double[] array = new double[x.length];

        double sum = 0;
        for (int i = 0; i < x.length; i++) {
            sum += Math.exp(x[i]);
        }

        for (int i = 0; i < array.length; i++) {
            array[i] = Math.exp(x[i]) / sum;
        }

        return array;
    }

    public double[] dfArray(double[] x) {
        double[] array = new double[x.length];

        double sum = 0;
        for (int i = 0; i < x.length; i++) {
            sum += Math.exp(x[i]);
        }

        for (int i = 0; i < x.length; i++) {
            double ex = Math.exp(x[i]);
            array[i] = (ex * sum - ex * ex) / (sum * sum);
        }

        return array;
    }
}
