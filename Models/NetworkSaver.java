package Models;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class NetworkSaver {
    
    // Serialize a neural network
    public static void saveNetwork(NeuralNetwork network, String filename) {
        // Serialize
        try {
            FileOutputStream file = new FileOutputStream(filename);
            ObjectOutputStream out = new ObjectOutputStream(file);

            out.writeObject(network);

            out.close();
            file.close();

            System.out.println("Object has been serialized");
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

            System.out.println("Object has been deserialized");
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        return network;
    }
}
