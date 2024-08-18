/*
package Models;

import java.util.function.Function;

import Data.DataPoint;
import Models.TrainingGraph;
import Models.FeedForward.FeedForward_NN;

import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

public class ModelTrainer {
    
    private FeedForward_NN model;
    private Function<DataPoint, Void> processFunction;
    private TrainingGraph graph;

    private int costSampleNum = 10;

    // Constructor
    public ModelTrainer(FeedForward_NN model, Function<DataPoint, Void> processFunction, boolean generateGraph) {
        this.model = model;
        this.processFunction = processFunction;
        
        if (generateGraph) {
            String title = "Network Training Graph";
            String xAxisLabel = "Epoch";
            String yAxisLabel = "Average Cost";
            int margin = 64;
            int radius = 2;
            int tickLength = margin / 16;
            int xLabelInterval = 50;
            double yLabelInterval = 0.25;

            graph = new TrainingGraph(title, xAxisLabel, yAxisLabel, margin, radius, tickLength, xLabelInterval, yLabelInterval);
        }
    }

    public void train(DataPoint[] trainData, int batchSize, int numEpochs) {
        
        // Initialize mini batches array
        DataPoint[][] miniBatches;

        List<Double> previousCost = new ArrayList<Double>();

        // Loop over all data numEpochs times
        for (int epoch = 0; epoch < numEpochs; epoch++) {

            // Create mini batches with data
            miniBatches = createMiniBatches(trainData, batchSize, true);

            int numBatches = miniBatches.length;

            for (int batch = 0; batch < numBatches; batch++) {

                // Find average cost and num correct over this batch
                double batchCost = 0;
                int numCorrect = 0;

                for (int pointIndex = 0; pointIndex < batchSize; pointIndex++) {

                    DataPoint point = miniBatches[batch][pointIndex];

                    // Process data point if applicable
                    if (processFunction != null) {
                        processFunction.apply(point);
                    }
                    
                    // Forward and back propagate data
                    double[] expectedOutputs = point.getOutputs();
                    double[] outputs = model.forwardPropagate(point.getInputs(), true);
                    model.backPropagate(expectedOutputs);

                    // Update previous cost history
                    batchCost += model.getCost(point.getOutputs());

                    // Find if point is correct
                    int guessedLabel = 0;
                    int realLabel = 0;
                    for (int label = 1; label < outputs.length; label++) {
                        guessedLabel = (outputs[label] > outputs[guessedLabel]) ? label : guessedLabel;
                        realLabel = (expectedOutputs[label] > expectedOutputs[realLabel]) ? label : realLabel;
                    }
                    if (realLabel == guessedLabel)
                        numCorrect++;
                        
                }

                // Average cost of this batch
                double averageCostThisBatch = batchCost / batchSize;
                previousCost.add(averageCostThisBatch);
                if (previousCost.size() > costSampleNum) {
                    previousCost.remove(0);
                }

                // Average cost over last few batches
                double averageCost = previousCost.stream().mapToDouble(Double::doubleValue).average().orElse(0);

                // Average num correct of this batch
                double percentCorrect = (double)numCorrect / batchSize;

                // Print an update of model's performance
                int count = epoch * numBatches * batchSize + batch * batchSize;
                System.out.printf("Epoch: %d, Batch: %d, Count: %d, Average cost this batch: %.5f, Average cost last %d batches: %.5f, Percent correct this batch: %.3f%n", epoch, batch, count, averageCostThisBatch, costSampleNum, averageCost, percentCorrect);

                // Add data points to graph
                if (graph != null) {
                    graph.addPoint(epoch * batchSize + batch, averageCost);
                }
                
                int numUpdates = epoch * batchSize + batch + 1;
                model.updateWeightsAndBiases(numUpdates, batchSize, epoch + 1);
            }
        }
    }

    // Create mini batches from given data
    public DataPoint[][] createMiniBatches(DataPoint[] data, int batchSize, boolean shuffle) {
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

    // Getters and setters
    public FeedForward_NN getModel() {
        return model;
    }

    public void setModel(FeedForward_NN model) {
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
*/

package Models;

import java.util.function.Function;
import Data.DataPoint;
import Models.FeedForward.FeedForward_NN;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

public class ModelTrainer {
    
    private FeedForward_NN model;
    private Function<DataPoint, Void> processFunction;
    private TrainingGraph graph;

    private int costSampleNum = 10;
    private double learningRateDecay = 0.99; // Learning rate decay factor
    private int patience = 5; // Number of epochs to wait before early stopping

    // Constructor
    public ModelTrainer(FeedForward_NN model, Function<DataPoint, Void> processFunction, boolean generateGraph) {
        this.model = model;
        this.processFunction = processFunction;
        
        if (generateGraph) {
            String title = "Network Training Graph";
            String xAxisLabel = "Epoch";
            String yAxisLabel = "Average Cost";
            int margin = 64;
            int radius = 2;
            int tickLength = margin / 16;
            int xLabelInterval = 50;
            double yLabelInterval = 0.25;

            graph = new TrainingGraph(title, xAxisLabel, yAxisLabel, margin, radius, tickLength, xLabelInterval, yLabelInterval);
        }
    }

    public void train(DataPoint[] data, int batchSize, int numEpochs, double testSplitRatio) {
        // Split data into training and test sets
        DataPoint[][] trainTestSplit = createTrainTestSplit(data, testSplitRatio);
        DataPoint[] trainData = trainTestSplit[0];
        DataPoint[] testData = trainTestSplit[1];

        // Initialize mini batches array
        DataPoint[][] miniBatches;

        List<Double> previousCost = new ArrayList<>();
        double bestTestCost = Double.MAX_VALUE;
        int epochsWithoutImprovement = 0;

        // Loop over all data numEpochs times
        for (int epoch = 0; epoch < numEpochs; epoch++) {

            // Create mini batches with data
            miniBatches = createMiniBatches(trainData, batchSize, true);

            int numBatches = miniBatches.length;

            for (int batch = 0; batch < numBatches; batch++) {

                // Find average cost and num correct over this batch
                double batchCost = 0;
                int numCorrect = 0;

                for (int pointIndex = 0; pointIndex < batchSize; pointIndex++) {

                    DataPoint point = miniBatches[batch][pointIndex];

                    // Process data point if applicable
                    if (processFunction != null) {
                        processFunction.apply(point);
                    }
                    
                    // Forward and back propagate data
                    double[] expectedOutputs = point.getOutputs();
                    double[] outputs = model.forwardPropagate(point.getInputs(), true);
                    model.backPropagate(expectedOutputs);

                    // Update previous cost history
                    batchCost += model.getCost(point.getOutputs());

                    // Find if point is correct
                    int guessedLabel = getLabel(outputs);
                    int realLabel = getLabel(expectedOutputs);
                    if (realLabel == guessedLabel)
                        numCorrect++;
                        
                }

                // Average cost of this batch
                double averageCostThisBatch = batchCost / batchSize;
                previousCost.add(averageCostThisBatch);
                if (previousCost.size() > costSampleNum) {
                    previousCost.remove(0);
                }

                // Average cost over last few batches
                double averageCost = previousCost.stream().mapToDouble(Double::doubleValue).average().orElse(0);

                // Average num correct of this batch
                double percentCorrect = (double) numCorrect / batchSize;

                // Print an update of model's performance
                int count = epoch * numBatches * batchSize + batch * batchSize;
                System.out.printf("Epoch: %d, Batch: %d, Count: %d, Average cost this batch: %.5f, Average cost last %d batches: %.5f, Percent correct this batch: %.3f%n", 
                                  epoch, batch, count, averageCostThisBatch, costSampleNum, averageCost, percentCorrect);

                // Add data points to graph
                if (graph != null) {
                    graph.addPoint(epoch * batchSize + batch, averageCost);
                }
                
                int numUpdates = epoch * batchSize + batch + 1;
                model.updateWeightsAndBiases(numUpdates, batchSize, epoch + 1);
            }

            // Learning rate decay
            model.setLearningRate(model.getLearningRate() * learningRateDecay);

            // Evaluate model on test data
            double testCost = evaluateModel(testData);
            System.out.printf("Epoch: %d, Test cost: %.5f%n", epoch, testCost);

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
    private double evaluateModel(DataPoint[] testData) {
        double totalCost = 0;
        int numCorrect = 0;

        for (DataPoint point : testData) {
            double[] expectedOutputs = point.getOutputs();
            double[] outputs = model.forwardPropagate(point.getInputs(), false);
            totalCost += model.getCost(expectedOutputs);

            int guessedLabel = getLabel(outputs);
            int realLabel = getLabel(expectedOutputs);
            if (realLabel == guessedLabel) {
                numCorrect++;
            }
        }

        double accuracy = (double) numCorrect / testData.length;
        System.out.printf("Test Accuracy: %.2f%%%n", accuracy * 100);

        return totalCost / testData.length;
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
    public FeedForward_NN getModel() {
        return model;
    }

    public void setModel(FeedForward_NN model) {
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