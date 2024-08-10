package CostFunctions;

public class CrossEntropy extends CostFunction {

    // Small value to avoid NaN issues
    private final double smallValue = 1e-10;

    // Calculate the cross entropy cost
    public double cost(double[] expectedOutputs, double[] calculatedOutputs) {
        double sum = 0;
        for (int i = 0; i < expectedOutputs.length; i++) {
            double p = calculatedOutputs[i];
            double y = expectedOutputs[i];

            // Clamp the values to avoid log(0) issues
            p = Math.max(p, smallValue); // to avoid log(0)
            p = Math.min(p, 1 - smallValue); // to avoid log(1)

            if (y == 1) {
                sum -= Math.log(p);
            } else {
                sum -= Math.log(1 - p);
            }
        }
        return sum / expectedOutputs.length; // Average cost over all samples
    }

    // Calculate the derivative of the cost function
    public double dcost(double expected, double calculated) {
        return calculated - expected;
    }
}