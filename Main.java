import java.io.IOException;
import javax.swing.JFrame;

import ActivationFunctions.*;
import CostFunctions.*;

import MNIST.*;
import Networks.DataPoint;
import Networks.NetworkTrainingGraph;
import Networks.FeedForward.FeedForward_NN;

public class Main {
    public static void main(String[] args) throws IOException {
        
        // Set up graph
        String title = "Graph";
        String xAxisLabel = "Number of Epochs";
        String yAxisLabel = "Accuracy %";
        int margin = 64;
        int radius = 2;
        int tickLength = margin / 16;
        int xLabelInterval = 50;
        double yLabelInterval = 10;
        double maxHeight = 100;

        NetworkTrainingGraph graph = new NetworkTrainingGraph(title, xAxisLabel, yAxisLabel, margin, radius, tickLength, xLabelInterval, yLabelInterval);
        graph.setMaxHeight(maxHeight);

        JFrame frame = new JFrame();
        // Set size, layout and location for frame.  
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(graph);
        frame.setSize(400, 400);
        frame.setLocation(200, 200);
        frame.setVisible(true);

        // Set up model
        int[] dim = new int[] {784, 100, 10};
        CostFunction error = new CrossEntropy();
        ActivationFunction hidden = new Tanh();
        ActivationFunction output = new Softmax();
        double learningRateMin = 0.000001;
        double learningRateMax = 0.01;
        double momentum = 0.9;
        double weightDecay = 0.005;
        int warmRestartInterval = 100;
        int warmRestartIntervalMult = 1;

        FeedForward_NN model = new FeedForward_NN(dim, error, hidden, output, learningRateMin, learningRateMax, momentum, weightDecay, warmRestartInterval, warmRestartIntervalMult);
        model.randomizeWeights();

        // Read train and test data
        MnistReader reader = new MnistReader();
        DataPoint[] trainData = reader.readData("MNIST\\ByteData\\train-images.idx3-ubyte", "MNIST\\ByteData\\train-labels.idx1-ubyte");
        DataPoint[] testData = reader.readData("MNIST\\ByteData\\t10k-images.idx3-ubyte", "MNIST\\ByteData\\t10k-labels.idx1-ubyte");

        // Train network
        int batchSize = 100;
        int numIterations = 2;

        model.learn(trainData, batchSize, numIterations, graph);

        // Test network
        int numCorrect = 0;

        for (int i = 0; i < testData.length; i++) {

            double[] inputs = trainData[i].getInputs();
            double[] outputs = trainData[i].getOutputs();

            double[] calculatedOutputs = model.forwardPropagate(inputs);

            int maxIndex = 0;
            int label = 0;
            for (int j = 1; j < calculatedOutputs.length; j++) {
                maxIndex = (calculatedOutputs[j] > calculatedOutputs[maxIndex]) ? j : maxIndex;
                label = (outputs[j] > outputs[label]) ? j : label;
            }

            if (maxIndex == label)
                numCorrect++;
        }

        System.out.println("Test data results: " + numCorrect + " correct out of " + trainData.length + " | " + ((double)numCorrect / trainData.length * 100) + "%");
        
        /*
        
        MnistVisualizer visualizer = new MnistVisualizer();

        //MnistReader reader = new MnistReader();
        //MnistDataPoint[] data = reader.readData("MNIST\\ByteData\\train-images.idx3-ubyte", "MNIST\\ByteData\\train-labels.idx1-ubyte");
        int dataNum = 0;
        
        visualizer.testModel(model, testData);

        frame.remove(graph);
        // Set size, layout and location for frame.  
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(visualizer);
        frame.setSize(400, 400);
        frame.setLocation(200, 200);
        frame.setVisible(true);

        
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                frame.repaint();
            }
        }, 50, 50);
        */
        
    }
}
