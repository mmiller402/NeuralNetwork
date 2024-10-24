package Models;

import java.util.function.Function;
import Data.DataPoint;
import java.util.List;
import java.util.Arrays;
import java.util.Collections;

public class ModelTrainer {
    
    private NeuralNetwork model;
    private Function<DataPoint, Void> processFunction;
    private TrainingGraph graph;

    private int patience = 5; // Number of epochs to wait before early stopping

    // Constructor
    public ModelTrainer(NeuralNetwork model, Function<DataPoint, Void> processFunction, boolean generateGraph) {
        this.model = model;
        this.processFunction = processFunction;
        
        if (generateGraph) {
            graph = new TrainingGraph();
        }
    }

    public TrainingData train(DataPoint[] data, int batchSize, int numEpochs, double testSplitRatio) {

        // Hold all training info in TrainingData object
        TrainingData trainingData = new TrainingData(true);
        if (graph != null)
            graph.setTrainingData(trainingData);
        
        // Split data into training and test sets
        DataPoint[][] trainTestSplit = createTrainTestSplit(data, testSplitRatio);
        DataPoint[] trainData = trainTestSplit[0];
        DataPoint[] testData = trainTestSplit[1];

        // Initialize mini batches array
        DataPoint[][] miniBatches;

        // Early stopping
        double bestTestCost = Double.MAX_VALUE;
        int epochsWithoutImprovement = 0;

        // Initial epoch 0 evaluation
        double[] initialEvaluation = evaluateModel(testData);
        double initialTestCost = initialEvaluation[0];
        double initialTestAccuracy = initialEvaluation[1];
        System.out.printf("Epoch: %d, Validation Cost: %.5f, Validation Accuracy: %.2f%%%n", 0, initialTestCost, initialTestAccuracy);
        
        trainingData.addEpochData(initialTestCost, initialTestCost, initialTestAccuracy, initialTestAccuracy);
        
        if (graph != null)
            graph.repaint();

        // Loop over all data numEpochs times
        for (int epoch = 0; epoch < numEpochs; epoch++) {

            // Create mini batches with data
            miniBatches = createMiniBatches(trainData, batchSize, true);
            int numBatches = miniBatches.length;

            // Track cost and accuracy of each epoch
            double trainCost = 0;
            int numCorrect = 0;

            for (int batch = 0; batch < numBatches; batch++) {

                for (int pointIndex = 0; pointIndex < miniBatches[batch].length; pointIndex++) {

                    DataPoint point = miniBatches[batch][pointIndex];

                    // Process data point if applicable
                    if (processFunction != null) {
                        processFunction.apply(point);
                    }
                    
                    // Forward and back propagate data
                    double[] expectedOutputs = point.getOutputs();
                    double[] outputs = model.forwardPropagate(point.getInputs(), true);
                    model.backPropagate(point.getInputs(), expectedOutputs);

                    // Update cost
                    trainCost += model.getCost(point.getOutputs(), outputs);

                    // Update accuracy
                    int guessedLabel = getLabel(outputs);
                    int realLabel = getLabel(expectedOutputs);
                    if (realLabel == guessedLabel)
                        numCorrect++;
                        
                }

                // Update weights and biases
                int numUpdates = epoch * numBatches + batch + 1;
                model.updateWeightsAndBiases(batchSize, numUpdates);
            }

            // Learning rate decay
            //model.setLearningRate(model.getLearningRate() * learningRateDecay);

            // Train statistics
            int numDataPoints = numBatches * batchSize;
            trainCost /= numDataPoints;
            double trainAccuracy = (double)numCorrect / numDataPoints * 100;

            // Evaluate model on test data
            double[] evaluation = evaluateModel(testData);
            double testCost = evaluation[0];
            double testAccuracy = evaluation[1];
            System.out.printf("Epoch: %d, Train Cost: %.5f, Validation Cost: %.5f, Train Accuracy: %.2f%%, Validation Accuracy: %.2f%%%n", 
                              epoch + 1, trainCost, testCost, trainAccuracy, testAccuracy);
            
            // Add data to TrainingData
            trainingData.addEpochData(trainCost, testCost, trainAccuracy, testAccuracy);
            if (graph != null)
                graph.repaint();

            // Early stopping check
            if (testCost < bestTestCost) {
                bestTestCost = testCost;
                epochsWithoutImprovement = 0;
            } else {
                epochsWithoutImprovement++;
            }

            if (epochsWithoutImprovement >= patience) {
                System.out.println("Early stopping triggered.");
                break;
            }
        }

        // Return training data
        return trainingData;
    }

    // Create mini batches from given data
    private DataPoint[][] createMiniBatches(DataPoint[] data, int batchSize, boolean shuffle) {
        if (shuffle) {
            List<DataPoint> dataList = Arrays.asList(data);
            Collections.shuffle(dataList);
            data = dataList.toArray(new DataPoint[0]);
        }

        int numBatches = (data.length + batchSize - 1) / batchSize; // Handle last batch if not full
        DataPoint[][] batches = new DataPoint[numBatches][];
        for (int batch = 0; batch < numBatches; batch++) {
            int start = batch * batchSize;
            int end = Math.min(start + batchSize, data.length);
            batches[batch] = Arrays.copyOfRange(data, start, end);
        }

        return batches;
    }

    // Split data into training and testing sets
    private DataPoint[][] createTrainTestSplit(DataPoint[] data, double testSplitRatio) {
        int testSize = (int) (data.length * testSplitRatio);
        int trainSize = data.length - testSize;
        DataPoint[] trainData = Arrays.copyOfRange(data, 0, trainSize);
        DataPoint[] testData = Arrays.copyOfRange(data, trainSize, data.length);
        return new DataPoint[][] { trainData, testData };
    }

    // Evaluate model performance on test data
    // Returns array with 2 values: {cost, accuracy}
    public double[] evaluateModel(DataPoint[] testData) {
        double totalCost = 0;
        int numCorrect = 0;

        for (DataPoint point : testData) {
            double[] expectedOutputs = point.getOutputs();
            double[] outputs = model.forwardPropagate(point.getInputs(), false);
            totalCost += model.getCost(expectedOutputs, outputs);

            int guessedLabel = getLabel(outputs);
            int realLabel = getLabel(expectedOutputs);
            if (realLabel == guessedLabel) {
                numCorrect++;
            }
        }

        double accuracy = (double) numCorrect / testData.length * 100;
        double cost = totalCost / testData.length;

        return new double[] {cost, accuracy};
    }

    // Get the label with highest output value
    private int getLabel(double[] outputs) {
        int guessedLabel = 0;
        for (int label = 1; label < outputs.length; label++) {
            if (outputs[label] > outputs[guessedLabel]) {
                guessedLabel = label;
            }
        }
        return guessedLabel;
    }

    // Getters and setters
    public NeuralNetwork getModel() {
        return model;
    }

    public void setModel(NeuralNetwork model) {
        this.model = model;
    }

    public Function<DataPoint, Void> getProcessFunction() {
        return processFunction;
    }

    public void setProcessFunction(Function<DataPoint, Void> processFunction) {
        this.processFunction = processFunction;
    }

    public TrainingGraph getGraph() {
        return graph;
    }

    public void setGraph(TrainingGraph graph) {
        this.graph = graph;
    }
}