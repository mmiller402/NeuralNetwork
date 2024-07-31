package CostFunctions;

public class MeanSquaredError extends CostFunction {
    public double cost(double[] expectedOutputs, double[] calculatedOutputs) {
        double sum = 0;
        for (int i = 0; i < expectedOutputs.length; i++) {
            sum += Math.pow(calculatedOutputs[i] - expectedOutputs[i], 2);
        }
        return sum / 2;
    }

    public double dcost(double expected, double calculated) {
        return calculated - expected;
    }
}
