package Models;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

// Class for saving and loading neural networks
public class NetworkSaver {
    
    // Save neural network to a file
    public static void saveNetwork(NeuralNetwork network, String filename) {
        try {
            FileOutputStream file = new FileOutputStream(filename);
            ObjectOutputStream out = new ObjectOutputStream(file);

            out.writeObject(network);

            out.close();
            file.close();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    // Retrieve network from a file
    public static NeuralNetwork loadNetwork(String filename) {
        NeuralNetwork network = null;
        try {
            FileInputStream file = new FileInputStream(filename);
            ObjectInputStream in = new ObjectInputStream(file);

            network = (NeuralNetwork)in.readObject();

            in.close();
            file.close();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        return network;
    }
}
