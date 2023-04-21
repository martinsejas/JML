package test;

import fr.epita.sejas.martin.images.Image;
import fr.epita.sejas.martin.images.ImageCsvDAO;
import services.TaskA_CSVService;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class TaskB_ReshapingData {

//    a.	Add some logic to your existing code,
//    to transform the array of double extracted from the remaining 784 columns to a 2d square matrix (28 x 28)

//Write a method “showMatrix” that prints a matrix in the console.
//In our case, the matrix contains values that range from 0 to 255, which is not so indicative while printed in the console.
//To improve the rendering, print “xx” in the console if the value is above 100, else print “..”.
//o	Use this showMatrix method on the matrix extracted from line 23
//o	Print the first column of the same line. What do you notice?


    @Test
    public void showMatrixTest() throws IOException {
        String testPath = "dataset/mnist_test.csv";
        String trainPath = "dataset/mnist_train.csv";

        //Here I'll be calling TaskA_CSVService only for test, but it could be equally done for train
        TaskA_CSVService firstTask = new TaskA_CSVService(testPath);

        //Fulfilling Task A
        List<List<Double>> testData = firstTask.readCSVFile();

        //need to make an array of array using each line
        String[][] matrix = new String[28][28];

        List<Double> testRow = testData.get(22);

        // for every value in a row

       int rowCount = 0;
       int itemCount = 0;
        for (int i = 1; i < testRow.size(); i++ )
        {

            // string value depending on value
            if(testRow.get(i) > 100 )
            {
                matrix[rowCount][itemCount] = "xx";
                itemCount++;
            }
            else
            {
                matrix[rowCount][itemCount] = "..";
                itemCount++;
            }

            // if matrix row is complete, add it to the matrix as a list and empty it
            if (itemCount == 28)
            {
                    itemCount = 0;
                    rowCount++;
//
            }
        }


      for (int i = 0; i < 28; i++) {
          System.out.println(Arrays.deepToString(matrix[i]));
      }
    }



    @Test
    public  double[][] calculateMatrixTest() throws  IOException {


        String testPath = "dataset/mnist_test.csv";
        String trainPath = "dataset/mnist_train.csv";

        //Here I'll be calling TaskA_CSVService only for test, but it could be equally done for train
        TaskA_CSVService firstTask = new TaskA_CSVService(testPath);

        //Fulfilling Task A
        List<List<Double>> testData = firstTask.readCSVFile();

        //need to make an array of array using each line
        double[][] matrix = new double[28][28];

        List<Double> testRow = testData.get(22);

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
//
            }
        }

        matrix[27][27] = 0.0;
       return matrix;

    }



    // Test for refactoring of matrix
    @Test
    public void showMatrixFromMatrix () throws  IOException {

        String testPath = "dataset/mnist_test.csv";
        String trainPath = "dataset/mnist_train.csv";

        //Making an instance of my csvService Object
        ImageCsvDAO csvService = new ImageCsvDAO();


        // I'm only printing for the test
        List<Image> images = csvService.getAllImages(testPath);

        // print line 23

        double[][] matrixToBePrinted = images.get(23).getDataMatrix();

        System.out.println("This image is supposed to be: "+ images.get(23).getLabel());
        System.out.println();

        //loop thru array to print contents
        for (int i = 0; i < matrixToBePrinted.length; i++){

            String row = "";

            for (int j  = 0; j < matrixToBePrinted[i].length; j++) {
                if(matrixToBePrinted[i][j] > 100){
                    row = row+"xx";
                }
                else {
                    row = row+"..";
                }
            }

            System.out.println(row);
        }
    }

}
