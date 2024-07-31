package MNIST;

import java.io.*;

import Networks.DataPoint;

public class MnistReader {

    public DataPoint[] readData(String imageFilePath, String labelFilePath) throws IOException {

        // Images
        DataInputStream imageInputStream = new DataInputStream(new BufferedInputStream(new FileInputStream(imageFilePath)));
        int imageMagicNumber = imageInputStream.readInt();
        int numberOfImages = imageInputStream.readInt();
        int nRows = imageInputStream.readInt();
        int nCols = imageInputStream.readInt();

        System.out.println("image magic number is " + imageMagicNumber);
        System.out.println("number of images is " + numberOfImages);
        System.out.println("number of rows is: " + nRows);
        System.out.println("number of cols is: " + nCols);

        // Labels
        DataInputStream labelInputStream = new DataInputStream(new BufferedInputStream(new FileInputStream(labelFilePath)));
        int labelMagicNumber = labelInputStream.readInt();
        int numberOfLabels = labelInputStream.readInt();

        System.out.println("label magic number is " + labelMagicNumber);
        System.out.println("number of labels is " + numberOfLabels);

        // Store data in dataPoints[]
        DataPoint[] dataPoints = new DataPoint[numberOfImages];

        for (int i = 0; i < numberOfImages; i++) {
            DataPoint point = new DataPoint(784, 10);

            int label = labelInputStream.readUnsignedByte();
            point.setOutput(label, 1);

            for (int j = 0; j < 784; j++)
                point.setInput(j, (double)imageInputStream.readUnsignedByte() / 255);
            
            dataPoints[i] = point;
        }

        // Close streams
        imageInputStream.close();
        labelInputStream.close();

        return dataPoints;
    }
}
