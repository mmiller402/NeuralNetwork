package Data;

public class DataPoint {
    
    private double[] inputs;
    private double[] outputs;

    public DataPoint() {
        this(0, 0);
    }

    public DataPoint(double[] inputs, double[] outputs) {
        setInputs(inputs);
        setOutputs(outputs);
    }

    public DataPoint(int inputSize, int outputSize) {
        this(new double[inputSize], new double[outputSize]);
    }

    public double[] getInputs() {
        return inputs.clone();
    }
    public void setInputs(double[] inputs) {
        this.inputs = inputs;
    }
    public void setInput(int index, double value) {
        inputs[index] = value;
    }

    public double[] getOutputs() {
        return outputs.clone();
    }
    public void setOutputs(double[] outputs) {
        this.outputs = outputs;
    }
    public void setOutput(int index, double value) {
        outputs[index] = value;
    }
}
