package ActivationFunctions;

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