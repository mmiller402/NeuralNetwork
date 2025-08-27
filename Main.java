import java.io.IOException;
import java.util.function.Function;
import javax.swing.JFrame;

import ActivationFunctions.*;
import CostFunctions.*;
import Data.*;
import Data.MNIST.*;
import Models.*;

public class Main {
    public static void main(String[] args) throws IOException {
        loadAndTestModel();
    }

    // Load a model and test it
    public static void loadAndTestModel() throws IOException {
        // Load the model with highest accuracy
        NeuralNetwork model = NetworkSaver.loadNetwork("Models\\SavedModels\\mnistNetwork.ser");

        // Read test data
        ImageDataPoint[] testData = MnistReader.readData("Data\\MNIST\\ByteData\\t10k-images.idx3-ubyte", "Data\\MNIST\\ByteData\\t10k-labels.idx1-ubyte", 10);

        // Create model trainer and test model
        ModelTrainer trainer = new ModelTrainer(model, null, false);
        double[] costAccuracy = trainer.evaluateModel(testData);
        System.out.println("Cost: " + costAccuracy[0] + " | Accuracy: " + costAccuracy[1]);

        // Let the user draw digits to test the model
        MnistDrawer drawer = new MnistDrawer(model);
        JFrame drawFrame = createJFrame();
        drawFrame.add(drawer);
        drawFrame.setVisible(true);
    }

    // Create a new model and train it on the MNIST dataset
    public static void createAndTrainModel() throws IOException {
        // Create new model
        NeuralNetwork model = new NeuralNetwork(new int[] {784, 512, 10}, new LeakyReLU(), new Softmax(), new CrossEntropy(), 0.0001, 0.9, 0.999, 0.2);

        // Read train and test data
        ImageDataPoint[] trainData = MnistReader.readData("Data\\MNIST\\ByteData\\train-images.idx3-ubyte", "Data\\MNIST\\ByteData\\train-labels.idx1-ubyte", 10);
        ImageDataPoint[] testData = MnistReader.readData("Data\\MNIST\\ByteData\\t10k-images.idx3-ubyte", "Data\\MNIST\\ByteData\\t10k-labels.idx1-ubyte", 10);

        // Create model trainer
        Function<DataPoint, Void> processFunction = p -> {
            ((ImageDataPoint)p).transformDrawingRandom();
            return null;
        };
        ModelTrainer trainer = new ModelTrainer(model, processFunction, true);
        TrainingGraph graph = trainer.getGraph();

        // Create graph JFrame
        JFrame graphFrame = createJFrame();
        graphFrame.add(graph);
        graphFrame.setVisible(true);

        // Train network
        int batchSize = 50;
        int numIterations = 100;
        double testSplitRatio = 0.05;
        trainer.train(trainData, batchSize, numIterations, testSplitRatio);

        
        //NeuralNetwork model = NetworkSaver.loadNetwork(filename);
        
        double[] costAccuracy = trainer.evaluateModel(testData);
        System.out.println("Cost: " + costAccuracy[0] + " | Accuracy: " + costAccuracy[1]);

        // Save network
        String filename = "newMnistNetworkTest.ser";
        NetworkSaver.saveNetwork(model, filename);

        MnistDrawer drawer = new MnistDrawer(model);

        // Create drawing JFrame 
        JFrame drawFrame = createJFrame();
        drawFrame.add(drawer);
        drawFrame.setVisible(true);
    }

    // Code for creating a JFrame
    public static JFrame createJFrame() {
        JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(600, 600);
        frame.setLocation(100, 100);
        return frame;
    }
}
