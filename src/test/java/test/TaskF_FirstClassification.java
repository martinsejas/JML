package test;

import fr.epita.sejas.martin.images.Image;
import fr.epita.sejas.martin.images.ImageCsvDAO;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static fr.epita.sejas.martin.Main.showMatrix;


public class TaskF_FirstClassification {


    // Main Test function that will print out the distances between all 10 centroids
    @Test
    public void firstClassificationTest() throws IOException{

        // need to generate data for this test we will use the training set and test set
        String trainPath = "dataset/mnist_train.csv";
        String testPath = "dataset/mnist_test.csv";

        //Making an instance of my csvService Object
        ImageCsvDAO csvService = new ImageCsvDAO();

        // list of the training images and testing
        List<Image> trainingImages = csvService.getAllImages(trainPath);
        List<Image> testImages = csvService.getAllImages(testPath);


        // getting the centroids from the training set
        Map<String, double[][]> centroids = trainCentroids(trainingImages);

        //make an array that will hold 10 images with label 0
        Image[] first10ZeroImages = new Image[10];


        // getting the first 10 zeros from the list
        for (int i = 0, j = 0; j < 10; i++ )
        {
            if(testImages.get(i).getLabel().equals("0")){
                first10ZeroImages[j] = testImages.get(i);
                j++;
            }
        }

        // Calculating the distance for each image
        for (Image image:first10ZeroImages){
            System.out.println("For this image, these are the distances: ");
            showMatrix(image);

            //looping through centroid hashmap to print distance for each
            for( String label:centroids.keySet())
            {
                double distance = calcCentroidDistance(image, centroids.get(label));
                System.out.println("For centroid of: "+label+ " distance is: "+ distance);
            }
            System.out.println();
        }

    }


    // Helper function to calculate distance from centroid
    public double calcCentroidDistance(Image image, double[][] trainedCentroid)
    {
        double [][] dataMatrix = image.getDataMatrix();
        double distance = 0;

        for (int i = 0; i < dataMatrix.length; i++){
            for(int j = 0; j <dataMatrix[i].length; j++){
                distance = Math.abs( dataMatrix[i][j] - trainedCentroid[i][j]) + distance;
            }
        }
        return Math.sqrt(distance);
    }



    public static Map<String, double[][]> trainCentroids(List<Image> images) throws  IOException
    {
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

        // return centroids
        return centroids;


    }
}
