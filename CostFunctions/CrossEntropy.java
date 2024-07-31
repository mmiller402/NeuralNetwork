package CostFunctions;

public class CrossEntropy extends CostFunction {

    // Expected outputs should all be 1 or 0
    public double cost(double[] expectedOutputs, double[] calculatedOutputs) {
        double sum = 0;
        for (int i = 0; i < expectedOutputs.length; i++) {
            double cost = (expectedOutputs[i] == 1) ? (-Math.log(calculatedOutputs[i])) : (-Math.log(1 - calculatedOutputs[i]));
            sum += (Double.isInfinite(cost)) ? 0 : cost;
        }
        return sum;
    }

    public double dcost(double expected, double calculated) {
        if (expected == 1)
            return -1 / calculated;

        return 1 / (1 - calculated);


    }
}