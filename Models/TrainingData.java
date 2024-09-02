package Models;

import java.util.List;
import java.util.ArrayList;

public class TrainingData {
    
    // Cost
    private List<Double> trainingCost;
    private List<Double> validationCost;

    // Accuracy
    private boolean useAccuracy;
    private List<Double> trainingAccuracy;
    private List<Double> validationAccuracy;

    public TrainingData(boolean useAccuracy) {

        trainingCost = new ArrayList<Double>();
        validationCost = new ArrayList<Double>();

        this.useAccuracy = useAccuracy;
        if (useAccuracy) {
            trainingAccuracy = new ArrayList<Double>();
            validationAccuracy = new ArrayList<Double>();
        }
    }

    // Add data
    public void addEpochData(double trainingCost, double validationCost) {
        this.trainingCost.add(trainingCost);
        this.validationCost.add(validationCost);
    }

    public void addEpochData(double trainingCost, double validationCost, double trainingAccuracy, double validationAccuracy) {
        addEpochData(trainingCost, validationCost);
        this.trainingAccuracy.add(trainingAccuracy);
        this.validationAccuracy.add(validationAccuracy);
    }

    // Getters
    public List<Double> getTrainingCost() {
        return trainingCost;
    }

    public List<Double> getValidationCost() {
        return validationCost;
    }

    public boolean useAccuracy() {
        return useAccuracy;
    }

    public List<Double> getTrainingAccuracy() {
        return trainingAccuracy;
    }

    public List<Double> getValidationAccuracy() {
        return validationAccuracy;
    }

    public int getNumEpochs() {
        return trainingCost.size();
    }
}
