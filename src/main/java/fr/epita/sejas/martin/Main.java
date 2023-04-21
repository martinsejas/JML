package fr.epita.sejas.martin;

import fr.epita.sejas.martin.centroid.CentroidClassifier;
import fr.epita.sejas.martin.centroid.CentroidClassifierMahalanobis;
import fr.epita.sejas.martin.centroid.CentroidClassifierMedian;
import fr.epita.sejas.martin.images.Image;
import fr.epita.sejas.martin.images.ImageCsvDAO;
import java.io.IOException;
import java.util.*;



public class Main {

    public static void main(String[] args) throws IOException {
        // Task A
        // ================================================================================
        // Check TaskA_CSVServiceTest on the test folder for Task A test code

        // Setting paths for test and training sets
        String testPath = "dataset/mnist_test.csv";
        String trainPath = "dataset/mnist_train.csv";

        //Making object to parse csv files
        ImageCsvDAO csvService = new ImageCsvDAO();

        System.out.println("TASK A.a");
        //FYI this will print the first two lines of each data set
        List<Image> testImages = csvService.getAllImages(testPath);
        List<Image> trainingImages = csvService.getAllImages(trainPath);




        // Task B
        // ================================================================================
        // Check showMatrix function on line 15
        // Check TaskB_ReshapingData in test folder for test code

        // showing matrix of line 23 of testImages
        System.out.println();
        System.out.println("TASK B.b");
        System.out.println("Printing line 23 of testImages");
        showMatrix(testImages.get(22));
        System.out.println();


        //Task C
        // =================================================================================
        // Please refer to the Image and ImageCsvDAO classes under fr.epita.sejas.martin.images


        // TASK D - Calculating distribution for datasets
        // =================================================================================
        // Check calculateDistribution function on line 140
        // Check TaskD_Calculate Distribution for test code

        // calculating for testImages only, but could be for training images (commented out)
        // Map<String, Integer> trainingDataSetDistribution = calculateDistribution(trainingImages);

        System.out.println("Task D");
        Map<String, Integer> testDataSetDistribution = calculateDistribution(testImages);

        System.out.println("Printing distribution of test data set");
        System.out.println(testDataSetDistribution);
        System.out.println("");


        // TASK E - Calculating the average representant
        // =================================================================================
        // Check the trainCentroids function under fr.epita.sejas.martin.centroid.CentroidClassifier
        // Check TaskE_trainCentroidsMethod  for test code
        // Also an implementation in TaskF_FirstClassification


        // TASK F - Performing your first classification
        // =================================================================================
        // This entire task was done on the test folder, please check the TaskF_FirstClassification class
        // Please run the firstClassificationTest() test function

        // F.3
        // I'm not really satisfied by the result, as it seems like for 2/3 values the lowest centroid was for 1 and
        // not 0.
        // Which shows the prediction is not that accurate


        // TASK G - Refactor code to match java standards
        // =================================================================================
        // Please check CentroidClassifer class under fr.epita.sejas.martin.centroid.CentroidClassifier

        // TASK H - Implement Classification Performance Assessment
        // =================================================================================
        // I have implemented two constructors for the classifier, one that just trains the centroids
        // And the other in addition receives the test dataset and automatically generates metrics
        // The following methods are available for the class:
        // calcCentroidDistance, trainCentroids, generateClassifierMetrics, printConfusionMatrix, printMetrics, predict
        // please check the class fr.epita.sejas.martin.centroid.CentroidClassifier
        // the test code is in the test folder <<TaskH_ClassificationPerformanceAssessment >>


        //making classifier
        CentroidClassifier classifier = new CentroidClassifier(trainingImages, testImages);

        // this constructor (with the test images) already calls the generateClassifierMetrics method within the class

       // Task H.2 - Printing classifier metrics
        System.out.println("TASK H.2");
        classifier.printMetrics();

        // Task H.3 - Printing confusion matrix
        System.out.println();
        System.out.println("Task H.3");
        classifier.printConfusionMatrix();

        // Task I - Improving the model

        //I have tried to implement the Mahalanobis distance to the classifier to improve
        // However I was unsuccessful, I'm not sure why which is why I have commented this code

//        CentroidClassifierMahalanobis MahalanobisClassifier = new CentroidClassifierMahalanobis(trainingImages,testImages);
//
//        MahalanobisClassifier.printMetrics();
//        MahalanobisClassifier.printConfusionMatrix();

        // IMPLEMENTING THE MEDIAN CLASSIFIER

        System.out.println();
        System.out.println("RESULTS OF MEDIAN CLASSIFIER");
        CentroidClassifierMedian medianClassifier = new CentroidClassifierMedian(trainingImages, testImages);

        medianClassifier.printMetrics();
        medianClassifier.printConfusionMatrix();




    }

    // TASK B method to show matrix
    public static void showMatrix(Image image) throws IOException {

        //get matrix
        double[][] matrixToBePrinted = image.getDataMatrix();

        //loop through matrix to print contents
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




    // TASK D method to calculate Distribution
    public static Map<String, Integer> calculateDistribution(List<Image> images) throws IOException {

        Map<String, Integer> integerDistribution = new LinkedHashMap<>();

        //Loop through all images and get count
        for (Image image : images) {
            Integer count = integerDistribution.get(image.getLabel());
            if (count == null) {
                count = 1;
            } else {
                count++;
            }
            integerDistribution.put(image.getLabel(), count);
        }


        return integerDistribution;
    }



}