import java.io.IOException;
import java.util.function.Function;
import javax.swing.JFrame;

import ActivationFunctions.*;
import CostFunctions.*;
import Data.DataPoint;
import Data.ImageDataPoint;
import Data.ImageReader;
import Data.MNIST.*;
import Models.ModelTrainer;
import Models.TrainingGraph;
import Models.FeedForward.FeedForward_NN;
import Models.FeedForward.FeedForward_Settings;

public class Main {

    public static void main(String[] args) throws IOException {

        // Create new model
        FeedForward_NN model = createModel();

        // Read train and test data
        ImageReader reader = new ImageReader();
        ImageDataPoint[] trainData = reader.readData("Data\\MNIST\\ByteData\\train-images.idx3-ubyte", "Data\\MNIST\\ByteData\\train-labels.idx1-ubyte", 10);
        ImageDataPoint[] testData = reader.readData("Data\\MNIST\\ByteData\\t10k-images.idx3-ubyte", "Data\\MNIST\\ByteData\\t10k-labels.idx1-ubyte", 10);

        // Create model trainer
        
        Function<DataPoint, Void> processFunction = p -> {
            ((ImageDataPoint)p).transformDrawingRandom();
            return null;
        };
        //ModelTrainer trainer = new ModelTrainer(model, processFunction, false);
        ModelTrainer trainer = new ModelTrainer(model, null, false);

        // Add graph to JFrame
        /* 
        JFrame frame = createFrame();
        TrainingGraph graph = trainer.getGraph();

        frame.add(graph);
        frame.setVisible(true);
        */

        // Train network
        int batchSize = 100;
        int numIterations = 20;
        double testSplitRatio = 0.05;
        trainer.train(trainData, batchSize, numIterations, testSplitRatio);

        // Test network
        int numCorrect = 0;

        for (int i = 0; i < testData.length; i++) {

            //testData[i].transformDrawingRandom();

            double[] inputs = testData[i].getInputs();
            double[] outputs = testData[i].getOutputs();

            double[] calculatedOutputs = model.forwardPropagate(inputs, false);

            int maxIndex = 0;
            int label = 0;
            for (int j = 1; j < calculatedOutputs.length; j++) {
                maxIndex = (calculatedOutputs[j] > calculatedOutputs[maxIndex]) ? j : maxIndex;
                label = (outputs[j] > outputs[label]) ? j : label;
            }

            if (maxIndex == label)
                numCorrect++;
        }

        System.out.println("Test data results: " + numCorrect + " correct out of " + testData.length + " | " + ((double)numCorrect / testData.length * 100) + "%");
        
        /*
        JFrame frame = createFrame();
        MnistDrawer drawer = new MnistDrawer(model);

        frame.add(drawer);
        frame.setVisible(true);
        

        /*
        MnistReader reader = new MnistReader();
        MnistDataPoint[] trainData = reader.readData("MNIST\\ByteData\\train-images.idx3-ubyte", "MNIST\\ByteData\\train-labels.idx1-ubyte");

        JFrame frame = createFrame();
        MnistVisualizer visualizer = new MnistVisualizer();
        visualizer.setDataPoint(trainData[463]);
        frame.add(visualizer);
        frame.setVisible(true);
        */
    }

    public static JFrame createFrame() {

        JFrame frame = new JFrame();
        // Set size, layout and location for frame.  
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 400);
        frame.setLocation(200, 200);
        frame.setVisible(true);

        return frame;
    }

    public static FeedForward_NN createModel() {

        int[] dimensions = new int[] {784, 100, 10};
        CostFunction costFunction = new CrossEntropy();
        ActivationFunction hiddenActivation = new LeakyReLU();
        ActivationFunction outputActivation = new Softmax();
        double learningRate = 0.0001;
        double lambda = 0.1;
        double dropoutRate = 0.2;
        double beta1 = 0.9;
        double beta2 = 0.999;

        FeedForward_NN model = new FeedForward_NN(dimensions, costFunction, hiddenActivation, outputActivation, learningRate, lambda, dropoutRate, beta1, beta2);
        model.randomizeWeights();

        return model;
    }
}
