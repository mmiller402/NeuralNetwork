package CostFunctions;

import java.io.Serializable;

public class CostFunction implements Serializable {
    public double cost(double[] expectedOutputs, double[] calculatedOutputs) {
        double sum = 0;
        for (int i = 0; i < expectedOutputs.length; i++) {
            sum += calculatedOutputs[i] - expectedOutputs[i];
        }
        return sum;
    }

    public double dcost(double expected, double calculated) {
        return 1;
    }
}