package test;


import fr.epita.sejas.martin.centroid.CentroidClassifier;
import fr.epita.sejas.martin.images.Image;
import fr.epita.sejas.martin.images.ImageCsvDAO;
import org.ejml.simple.SimpleMatrix;
import org.junit.jupiter.api.Test;
import fr.epita.sejas.martin.Main.*;

import java.io.IOException;
import java.util.*;

public class TaskI_TestingStream {
    @Test

    public void testingStream()
    {
        double[][] testMatrix = new double[10][10];

        for (int i = 0; i < testMatrix.length; i++)
        {
            for(int j=0; j < testMatrix[i].length; j++)
            {
                testMatrix[i][j] = 10;
            }
        }

        double imageMean = Arrays.stream(testMatrix).flatMapToDouble(Arrays::stream).average().getAsDouble();

        //should print 10
        System.out.println(imageMean);
    }




    @Test
    public void testingCovarianceCalculation()
    {
        double[][] testMatrix = new double[3][3];

        testMatrix[0][0] = 1;
        testMatrix[0][1] = 5;
        testMatrix[0][2] = 10;

        testMatrix[1][0] = 2;
        testMatrix[1][1] = 10;
        testMatrix[1][2] = 20;

        testMatrix[2][0] = 3;
        testMatrix[2][1] = 15;
        testMatrix[2][2] = 30;

        SimpleMatrix myMatrix = new SimpleMatrix(testMatrix);

        System.out.println(myMatrix.toString());


        List<double[][]> matricesOfCentroid = new ArrayList<>(756);

        System.out.println(matricesOfCentroid.size());


    }

}
