package fr.epita.sejas.martin.centroid;

import fr.epita.sejas.martin.images.Image;
import org.ejml.simple.SimpleMatrix;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CentroidClassifierMahalanobis extends CentroidClassifier{

    Map<String, Double> centroidVariances = new HashMap<>();
    Map<String, Double> centroidMeans = new HashMap<>();

    double[][] coVarianceMatrix = new double[10][10];

    public CentroidClassifierMahalanobis(List<Image> trainingImages) throws IOException {
        super(trainingImages);
        this.calcVarianceOfCentroids();
        this.generateCoVarianceMatrix();


    }

    public CentroidClassifierMahalanobis(List<Image> trainingImages, List<Image> testImages) throws IOException {
        super(trainingImages);
        this.calcVarianceOfCentroids();
        this.generateCoVarianceMatrix();
        this.generateClassifierMetrics(testImages);


    }
    //calculate average of all centroids


    public Map<String,double[][]> centroidsSD;


    //training centroids to hold the average standard deviation from the global average to each index
    // get image[i][j] abs value between global matrix, and then get difference from each centroid

//


   public String predict(Image image){

        //getting dataMatrix
        double [][] dataMatrix = image.getDataMatrix();

       //need to flatten matrix and calc average
       double imageMean = Arrays.stream(dataMatrix).flatMapToDouble(Arrays::stream).average().getAsDouble();

       // with the imageMean we can calculate our input vector
       double[] inputVector = new double[10];

       // calculating imageMean - CentroidMean
       for(String label: this.centroidMeans.keySet())
       {
           inputVector[Integer.parseInt(label)] = imageMean - centroidMeans.get(label);
       }

       //With the input vector we need to multiply it against the covariance matrix
       // and generate  a second vector for the output

       double[] outputVector = new double[10];

       //performing matrix multiplication to calculate Mahalanobis distance
       for(int i = 0; i<inputVector.length; i++)
       {
           for(int j = 0; j< this.coVarianceMatrix[i].length; j++)
           //MAYBE REMOVE LAST INPUT VECTOR MULTI
           {
               outputVector[i] += Math.pow((inputVector[i] * this.coVarianceMatrix[i][j]),2);
           }

           outputVector[i] = Math.sqrt(outputVector[i]);
       }

//       System.out.println(Arrays.toString(outputVector));

       //the output vector should be the Mahalanobis distances for all labels
       // the index represents the label
       // have to get the minimum of this


        //var for determining prediction
        double min_distance = Double.POSITIVE_INFINITY;

        String prediction = "";

        for(int i = 0; i < outputVector.length; i++)
        {
            if(outputVector[i] < min_distance)
            {
                min_distance = outputVector[i];
                prediction = Integer.toString(i);
            }
        }

        return prediction;

    }






    // Helper function to calculate distance from the centroid


    //function that will calculate
    public void calcVarianceOfCentroids()
    {
        //loop through all centroids
        for (String label:this.centroids.keySet())
        {
            //get centroid from label
            double[][] centroid = this.centroids.get(label);

            double centroidMean = 0;

            //get sum of all values
            for (int i = 0; i<centroid.length; i++)
            {
                for (int j = 0; j<centroid[i].length; j++)
                {
                    centroidMean+= centroid[i][j];
                }
            }

            //calculate centroid mean and place it on hashmap
            centroidMean = centroidMean/(28*28);
            this.centroidMeans.put(label, centroidMean);

            // now to calculate variance

            double centroidVariance = 0;

            for(int i =0; i<centroid.length; i++)
            {
                for(int j =0; j<centroid[i].length; j++)
                {
                    centroidVariance = centroidVariance+  Math.pow(centroid[i][j] - centroidMean,2);
                }
            }

            centroidVariance = centroidVariance/(28*28);
            this.centroidVariances.put(label,centroidVariance);

        }
    }

    //function that calculates the covariance between 2 centroids
    protected double calcCoVariance(String firstCentroid, String secondCentroid)
    {
        // get calculated means
        double meanX = this.centroidMeans.get(firstCentroid);
        double meanY = this.centroidMeans.get(secondCentroid);

        // get centroid values
        double[][] X = this.centroids.get(firstCentroid);
        double[][] Y = this.centroids.get(secondCentroid);

        double coVariance = 0.0;

        //calculate covariance
        for(int i = 0; i < X.length; i++){
            for(int j = 0; j < X[i].length; j++)
            {
                coVariance+= (X[i][j] - meanX) * (Y[i][j] - meanY);
            }
        }
        return coVariance/(28*28);
    }

    public void generateCoVarianceMatrix()
    {
        //the covariance matrix will be 10x10, relating to the 10 labels we have
        // also to note that it is a symmetrical matrix so once I calculate the upper diagonal, I just have to mirror
        // this can be accomplished by just assigning coVar[0,1] = coVar[1,0], and filling in with the variance for diagonal values

        //looping thru all rows
        for(int i = 0; i < 10; i++)
        {
            //looping thru all columns from 10 - i
            for(int j = 0; j <10; j++)
            {
                // if coVar(0,0) just pull variance of 0
                if(j == i)
                {
                    this.coVarianceMatrix[i][i] = this.centroidVariances.get(Integer.toString(i));
                }
                //else call helper function
                else
                {
                    this.coVarianceMatrix[i][j] = calcCoVariance(Integer.toString(i), Integer.toString(j));

                }
            }
        }

        System.out.println("Variances");
        System.out.println(Arrays.asList(this.centroidVariances));

        System.out.println("coVarianceMatrix");
        System.out.println(Arrays.deepToString(coVarianceMatrix));

       //need to invert it for the Mahalanobis classification, I will import a helper library
        SimpleMatrix invertedCoVarianceMatrix = new SimpleMatrix(this.coVarianceMatrix);
        invertedCoVarianceMatrix = invertedCoVarianceMatrix.invert();

        for(int i = 0; i< this.coVarianceMatrix.length; i++)
        {
            for(int j = 0; j < this.coVarianceMatrix[i].length; j++)
            {
                this.coVarianceMatrix[i][j] = invertedCoVarianceMatrix.get(i,j);
            }
        }

    }

}
