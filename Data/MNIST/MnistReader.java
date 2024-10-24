package Data.MNIST;

import java.io.*;

import Data.ImageDataPoint;

public class MnistReader {

    // Read MNIST data from the ubyte files
    public static ImageDataPoint[] readData(String imageFilePath, String labelFilePath, int numCategories) throws IOException {

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
        ImageDataPoint[] dataPoints = new ImageDataPoint[numberOfImages];

        for (int i = 0; i < numberOfImages; i++) {
            ImageDataPoint point = new ImageDataPoint(nCols, nRows, numCategories);

            int label = labelInputStream.readUnsignedByte();
            point.setOutput(label, 1);

            // Data is expected to be ints between 0 and 255 and is stored in doubles between 0 and 1
            for (int j = 0; j < nCols * nRows; j++)
                point.setInput(j, (double)imageInputStream.readUnsignedByte() / 255);
            
            dataPoints[i] = point;
        }

        // Close streams
        imageInputStream.close();
        labelInputStream.close();

        return dataPoints;
    }
}
