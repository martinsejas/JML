package fr.epita.sejas.martin.centroid;

import fr.epita.sejas.martin.images.Image;

import java.io.IOException;
import java.util.*;

import static fr.epita.sejas.martin.Main.calculateDistribution;

public class CentroidClassifierMedian extends CentroidClassifier{

    public CentroidClassifierMedian(List<Image> trainingImages) throws IOException {
        super();
        this.centroids = trainCentroids(trainingImages);
    }

    public CentroidClassifierMedian(List<Image> trainingImages, List<Image> testImages) throws IOException {
        super();
        this.centroids = trainCentroids(trainingImages);
        generateClassifierMetrics(testImages);
    }

     // sort training set in their respective labels
    //  once you have all of them separated, for each centroid:
        // for each index:
                // sort values and get median
                // store in centroid matrix


//    overloading trainCentroids method to get centroids by median
    public Map<String, double[][]> trainCentroids(List<Image> images) throws IOException
    {
        // the hashmap will have the label, and then a list of matrices that correspond to this label
        // we will calculate the median later
        Map<String, List<double[][]>> imagesByLabel = new HashMap<>();

        // we will calculate the distribution of the training set to pre-allocate the lists to improve performance
        // since we will have to use a triple nested loop later on to sort the matrix elements of each centroid
        Map<String, Integer> trainingDistribution = calculateDistribution(images);

        // I'll need a centroidIndexArray to be able to re-assign items on my pre-allocated lists

        int[] centroidIndexes = new int[10];



        //looping through all images to separate them between centroids
        for (Image image:images)
        {
            String imageLabel = image.getLabel();
            double[][] imageDataMatrix =  image.getDataMatrix();

            //if the label is not in the hashmap as a key, we create it, make a new list, and store the first matrix on it

            if(!imagesByLabel.containsKey(imageLabel))
            {
                int neededSize = trainingDistribution.get(imageLabel);
                List<double[][]> matricesOfCentroid = new ArrayList<>(neededSize);
                for(int it = 0; it < neededSize; it++)
                {
                    double[][] emptyMatrix = new double[28][28];
                    matricesOfCentroid.add(emptyMatrix);
                }
                matricesOfCentroid.set(0,imageDataMatrix);
                centroidIndexes[Integer.parseInt(imageLabel)] = 1;
                imagesByLabel.put(imageLabel, matricesOfCentroid);
            }
            // if the label exists, get the list, add an image to the list, update the index and store it back
            else {
                List<double[][]> matricesOfCentroid = imagesByLabel.get(imageLabel);

                // get the average for each index by looping the 2d array
                matricesOfCentroid.set(centroidIndexes[Integer.parseInt(imageLabel)],imageDataMatrix);
                centroidIndexes[Integer.parseInt(imageLabel)]++;

                //place the new centroid matrix on the hashmap
                imagesByLabel.put(imageLabel, matricesOfCentroid);
            }
        }

        // Now  that we have separated the images by their labels
        // We have to get the respective medians for each index of each centroid
        // Unfortunately this will be a quadruple loop

      Map<String, double[][]> centroidsByMedian = new HashMap<>();

        for(String label: imagesByLabel.keySet())
        {
            //get list of matrices to sort for each centroid
            double[][] medianCentroidMatrix = new double[28][28];
            List<double[][]> matricesOfCentroid = imagesByLabel.get(label);
            int matrixRows = matricesOfCentroid.get(0).length;
            for(int i = 0; i < matrixRows; i++)
            {
                for(int j = 0; j < matrixRows; j++)
                {
                    // get all the values of a particular index
                    ArrayList<Double> indexValues = new ArrayList<>(matricesOfCentroid.size());

                    //store it in a list
                    for(int matrixIndex = 0; matrixIndex < matricesOfCentroid.size(); matrixIndex++)
                    {
                        indexValues.add( matricesOfCentroid.get(matrixIndex)[i][j]);
                    }

                    //sort the list and get the median for this index
                   indexValues.sort(Comparator.naturalOrder());

                    //getting median value
                    double median = 0.0;

                    // if list size is odd the median is easy to calculate
                    if(indexValues.size() % 2 != 0)
                    {
                        median = indexValues.get((indexValues.size() - 1)/2);
                    }
                    // if even get the average of the middle values
                    else
                    {
                        median = (indexValues.get(indexValues.size()/2) + indexValues.get( (indexValues.size()+1)/2 ))/2;
                    }

                    medianCentroidMatrix[i][j] = median;


                }
            }

            centroidsByMedian.put(label, medianCentroidMatrix);

        }



        // return centroids
        return centroidsByMedian;

    }
}
