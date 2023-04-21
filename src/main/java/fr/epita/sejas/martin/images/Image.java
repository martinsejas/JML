package fr.epita.sejas.martin.images;

import java.io.IOException;
import java.util.List;

//TASK C
public class Image {

    private String label;

    private double[][] dataMatrix;

    //Constructor for class image
    public Image(String label, List<Double> imageRawData) throws IOException {
        this.label = label;
        this.dataMatrix = unflattenDataToMatrix(imageRawData);
    }

    public String getLabel() {
        return label;
    }

    public double[][] getDataMatrix() {
        return dataMatrix;
    }

    // Helper function to get a list of doubles and convert it to a 28 by 28 matrix
    public double[][] unflattenDataToMatrix(List<Double> imageRawData) throws IOException {

        double[][] matrix = new double[28][28];
        List<Double> testRow = imageRawData;

        // for every value in a row
        int rowCount = 0;
        int itemCount = 0;
        for (int i = 1; i < testRow.size(); i++ )
        {
            matrix[rowCount][itemCount] = testRow.get(i);
            itemCount++;

            // if matrix row is complete, add it to the matrix as a list and empty it
            if (itemCount == 28)
            {
                itemCount = 0;
                rowCount++;
            }
        }
        matrix[27][27] = 0.0;
        return matrix;

    }


}
