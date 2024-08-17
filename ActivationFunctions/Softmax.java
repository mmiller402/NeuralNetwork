package ActivationFunctions;

import java.util.Arrays;

/*
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

    // Use with CrossEntropy loss to be handled during backpropagation
    // IMPORTANT: will not work unless CrossEntropy is selected
    public double[] dfArray(double[] x) {
        double[] array = new double[x.length];
        Arrays.fill(array, 1.0);
        return array;
    }
}
*/

public class Softmax extends ActivationFunction {
    public double[] fArray(double[] x) {
        double expSum = 0;
        for (int i = 0; i < x.length; i++) {
            expSum += Math.exp(x[i]);
        }

        double[] returnArray = new double[x.length];
        for (int i = 0; i < returnArray.length; i++) {
            returnArray[i] = Math.exp(x[i]) / expSum;
        }

        return returnArray;
    }

    public double[] dfArray(double[] x) {
        double expSum = 0;
        for (int i = 0; i < x.length; i++)
        {
            expSum += Math.exp(x[i]);
        }

        double[] returnArray = new double[x.length];
        for (int i = 0; i < returnArray.length; i++) {
            double ex = Math.exp(x[i]);
            returnArray[i] = (ex * expSum - ex * ex) / (expSum * expSum);
        }

        return returnArray;
    }
}