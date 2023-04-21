package test;

import fr.epita.sejas.martin.images.Image;
import fr.epita.sejas.martin.images.ImageCsvDAO;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;



public class TaskE_trainCentroidsMethod {

    @Test
     void trainCentroidsTest() throws  IOException
    {
        // Original function takes a list of images, so we need to generate a list of images first

        String testPath = "dataset/mnist_test.csv";
        String trainPath = "dataset/mnist_train.csv";

        //Making an instance of my csvService Object
        ImageCsvDAO csvService = new ImageCsvDAO();


        // I'm only printing for the test
        List<Image> images = csvService.getAllImages(testPath);
//        List<Image> trainingImages = csvService.getAllImages(trainPath);

        // With the list of images we can proceed with the method itself

        // the hashmap will have the label, and then the matching centroid (data) matrix for the label
        Map<String, double[][]> centroids = new HashMap<>();

        // we'll get the cumulative average, at every loop we add the values of the new matrix to the centroid matrix
        // and then divide by two, and store it on the hashmap

        //looping through all images
        for (Image image:images)
        {
            String imageLabel = image.getLabel();
            double[][] imageDataMatrix =  image.getDataMatrix();

            //if the label is not in the hashmap as a key, create it and store its matrix as centroid
            if(!centroids.containsKey(imageLabel))
            {
                centroids.put(imageLabel, imageDataMatrix);
            }
            // if it is, get the stored centroid matrix, get the new average, and store it back
            else {
                double [][] centroidMatrix = centroids.get(imageLabel);

                // get the average for each index by looping the 2d array
                for( int i = 0; i < centroidMatrix.length; i++ ){
                    for( int j = 0; j<centroidMatrix[i].length; j++ ){
                        centroidMatrix[i][j] = (imageDataMatrix[i][j] + centroidMatrix[i][j])/2;
                    }
                }

                //place the new centroid matrix on the hashmap
                centroids.put(imageLabel, centroidMatrix);

            }


        }

        // return centroids on the original function

        //in this case we will print the keys
        System.out.println(centroids.keySet());
    }
}
