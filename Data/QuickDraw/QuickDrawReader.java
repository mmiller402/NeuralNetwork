package Data.QuickDraw;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.ByteBuffer;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import Data.ImageDataPoint;

public class QuickDrawReader {

    public static void unpackDrawing(InputStream inputStream) throws IOException {
        DataInputStream dataInputStream = new DataInputStream(inputStream);

        long keyId = dataInputStream.readLong();
        System.out.println("keyId: " + keyId);

        byte[] countryCodeBytes = new byte[2];
        dataInputStream.readFully(countryCodeBytes);
        String countryCode = new String(countryCodeBytes, "UTF-8");
        System.out.println("CountryCode: " + countryCode);

        byte recognized = dataInputStream.readByte();
        System.out.println("Recognized: " + recognized);

        int timestamp = dataInputStream.readInt();
        System.out.println("Timestamp: " + timestamp);

        int nStrokes = dataInputStream.readUnsignedShort();
        System.out.println("NStrokes: " + nStrokes);

        List<int[]> image = new ArrayList<>();

        for (int i = 0; i < nStrokes; i++) {
            int nPoints = dataInputStream.readUnsignedShort();
            int[] x = new int[nPoints];
            int[] y = new int[nPoints];

            for (int j = 0; j < nPoints; j++) {
                x[j] = dataInputStream.readUnsignedByte();
            }
            for (int j = 0; j < nPoints; j++) {
                y[j] = dataInputStream.readUnsignedByte();
            }

            image.add(new int[]{x.length, y.length});
        }
    }

    public static void unpackDrawings(String filename) throws IOException {
        try (FileInputStream fileInputStream = new FileInputStream(filename)) {
            while (true) {
                try {
                    unpackDrawing(fileInputStream);
                } catch (EOFException e) {
                    break;
                }
            }
        }
    }
}
