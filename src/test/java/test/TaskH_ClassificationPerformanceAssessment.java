package test;

import fr.epita.sejas.martin.centroid.CentroidClassifier;
import fr.epita.sejas.martin.images.Image;
import fr.epita.sejas.martin.images.ImageCsvDAO;
import org.junit.jupiter.api.Test;
import fr.epita.sejas.martin.Main.*;

import java.io.IOException;
import java.util.*;

import static fr.epita.sejas.martin.Main.calculateDistribution;



public class TaskH_ClassificationPerformanceAssessment {

    //positivesPerCentroid

    //negativesPerCentroid

    //have to make logic as soon as the label is obtained

    //a hashmap linking a hashmap maybe

    // centroid(label) : ( True_Positives: n, True_Negatives: n, False_Positives: n, False_Negatives: n )

    @Test
    public void classificationAssessorTest() throws IOException {


        //generating data for the test
        String trainPath = "dataset/mnist_train.csv";
        String testPath = "dataset/mnist_test.csv";

        //Making an instance of my csvService Object
        ImageCsvDAO csvService = new ImageCsvDAO();

        // list of the training images and testing
        List<Image> trainingImages = csvService.getAllImages(trainPath);
        List<Image> testImages = csvService.getAllImages(testPath);

        //making a classifier from the training images
        CentroidClassifier classifier = new CentroidClassifier(trainingImages,testImages);

        // Need the TruePositive, TrueNegatives, FalsePositive, FalseNegative, TotalPositiveTests, TotalNegativeTests
        // for every centroid

        // To get True Positive and False Positive is easy, depending if the prediction matches the label or not
        // I add 1 to true positive or false positive, and + 1 for TotalPositive tests

        // For the True Negative and False Negative is a bit more complex,
        // e.g if I predict 0 then I'm also predicting its not 1,2,3,4,5,6,7,8,9
        // if the prediction is correct, I have to add + 1 to 1,2,3,4,5,6,7,8,9 as a TrueNegative and TotalNegativeTest

        // if the prediction is wrong,e.g. 1, I need to add +1 to the False Negative of 1, and TrueNegative for the rest
        // except 0

        // as well as TotalNegative for the rest except 0


        //Setting up hashmaps

        //First hashmap is a centroid to its metrics, metrics will be a hashmap of String to integer
        //e.g centroidMetric(label) : ( True_Positives: n, True_Negatives: n, False_Positives: n, False_Negatives: n )

        Map<String, HashMap<String,Integer>> centroidMetrics = new HashMap<>();

        String[] labels = {"0", "1", "2", "3", "4", "5", "6", "7", "8", "9"};

        HashMap<String, Integer> testDataPredictionDistribution = new HashMap<>();

        Map<String, HashMap<String,Integer>> wrongPredictions = new HashMap<>();

        //Nested loop to make deep copies, and not shallow copies
        for(String label:labels) {
            //setting up keys for inner hashmap
            String[] metricNames = {"True Positives", "True Negatives", "False Positives",
                    "False Negatives"};

            //creating hashmap
            HashMap<String, Integer> metrics = new HashMap<String, Integer>();

            //Adding metric hashmap to centroid
            for (String metric : metricNames) {
                metrics.put(metric, 0);
            }
            testDataPredictionDistribution.put(label, 0);
            centroidMetrics.put(label, metrics);
        }


        //Nested loop to set up wrongPredictions hashmap
        for (String label:labels){

            //creating inner hashmap to track where the wrong predictions went
            HashMap<String, Integer> centroidWrongPredictions = new HashMap<String, Integer>();

            //inner loop to populate inner hashmap
            for(String predictedLabel:labels){

                if(!predictedLabel.equals(label)){
                    centroidWrongPredictions.put(predictedLabel,0);
                }
            }

            wrongPredictions.put(label,centroidWrongPredictions);
        }




        // Loop through all test images and attempt to classify

        Map<String, Integer> testDataSetDistribution = calculateDistribution(testImages);


        System.out.println("Printing distribution of test data set");
        System.out.println(testDataSetDistribution);
        System.out.println("Total number of elements in distribution: "+testDataSetDistribution.values().stream().mapToInt(d -> d).sum());


        for (Image image:testImages)
        {
            //actual value
            String imageLabel = image.getLabel();

            //predict from matrix
            String predictedLabel = classifier.predict(image);

            int predictionCount = testDataPredictionDistribution.get(predictedLabel);
            predictionCount++;
            testDataPredictionDistribution.put(predictedLabel,predictionCount);


            // if the prediction is correct, I need to add True Positive for the image centroid
            // and True Negatives for the others

            if(predictedLabel.equals(imageLabel)) {
                // looping through all centroids
                for(String key: centroidMetrics.keySet()) {

                    // get metrics for the centroid
                    HashMap<String, Integer> centroidMetric = centroidMetrics.get(key);

                    // if the centroid is imageLabel add one to True Positive, and to the Number of tests
                    if (key.equals(imageLabel)) {

                        // get required metrics from hashmap
                        int truePositive = centroidMetric.get("True Positives");

                        //update metrics of centroid
                        truePositive++;
                        centroidMetric.put("True Positives", truePositive);

                        // add updated centroid info to main map
                        centroidMetrics.put(imageLabel, centroidMetric);
                    }

                    // if not I need to update the True Negative for this centroid and Total Negative Tests
                    else {
                            //get required metrics
                            int trueNegative = centroidMetric.get("True Negatives");


                            //update metrics of centroid
                            trueNegative++;
                            centroidMetric.put("True Negatives", trueNegative);

                            //add updated centroid info to main map
                            centroidMetrics.put(key, centroidMetric);
                    }
                }
            }

            // if not matching then I need:
            //  0 - Update wrong prediction hashmap for confusion matrix
            //  1 - Update False positive for predictedLabel
            //  2 - Update False negative for imageLabel
            //  3 - Update True negatives for the rest
            else {
                // get the predicted centroid's wrong predictions list
                HashMap<String, Integer> centroidWrongPredictions = wrongPredictions.get(predictedLabel);

                //get the count and increase it
                int count = centroidWrongPredictions.get(imageLabel);
                count++;

                //update centroid count
                centroidWrongPredictions.put(imageLabel, count);

                //place it on the list of centroids
                wrongPredictions.put(predictedLabel,centroidWrongPredictions);

                for (String key: centroidMetrics.keySet()) {
                    // get metrics for the centroid
                    HashMap<String, Integer> centroidMetric = centroidMetrics.get(key);

                    // if case 1
                    if (key.equals(predictedLabel)){
                        // get needed metrics
                        int falsePositive = centroidMetric.get("False Positives");

                        //update values
                        falsePositive++;
                        centroidMetric.put("False Positives", falsePositive);

                        //update main hashmap
                        centroidMetrics.put(key, centroidMetric);
                    }

                    //else case 2
                    else if(key.equals(imageLabel))
                    {
                        //get needed metrics
                        int falseNegative = centroidMetric.get("False Negatives");

                        //updating values
                        falseNegative++;
                        centroidMetric.put("False Negatives", falseNegative);

                        //updating main hashmap
                        centroidMetrics.put(key, centroidMetric);
                    }

                    //else case 3
                    else {
                        //get required metrics
                        int trueNegative = centroidMetric.get("True Negatives");

                        //update metrics of centroid
                        trueNegative++;
                        centroidMetric.put("True Negatives", trueNegative);

                        //add updated centroid info to main map
                        centroidMetrics.put(key, centroidMetric);
                    }
                }
            }
        }

        // END OF MAIN LOOP

        //Printing Confusion Matrix

        System.out.println();
        System.out.println("Printing prediction distribution");
        System.out.println(testDataPredictionDistribution);
        System.out.println("Total Number of elements in distribution: " + testDataPredictionDistribution.values().stream().mapToInt(d -> d).sum());

        for(String centroid: centroidMetrics.keySet())
        {

            HashMap<String, Integer> metrics = centroidMetrics.get(centroid);



            int truePositives = metrics.get("True Positives");
            int trueNegatives = metrics.get("True Negatives");
            int falsePositives = metrics.get("False Positives");
            int falseNegatives = metrics.get("False Negatives");

            //calculate ML statistical parameters
            double accuracy = (((double) trueNegatives)+(double) truePositives)/((double) trueNegatives+ (double) truePositives+ (double) falseNegatives+ (double) falsePositives);
            double precision = ( (double) truePositives)/( (double) truePositives+(double)falsePositives);
            double sensitivity = ((double)truePositives)/((double)truePositives+(double)falseNegatives);
            double specificity = (double)trueNegatives/((double)trueNegatives+(double)falsePositives);



            System.out.println("-----------------------------------------------------------------");
            System.out.println(" Centroid: ("+centroid+")      | Predicted: "+centroid+" | Predicted Not: "+centroid+ " | Total");
            System.out.println(" Times Correct:     |      "+truePositives+"     |       "+trueNegatives+ "       | "+(trueNegatives+truePositives));
            System.out.println(" Times Wrong:       |      "+falsePositives+"     |       "+falseNegatives+"        | "+(falseNegatives+falsePositives));

            if(truePositives+falsePositives < 1000) {
                System.out.println(" Total:             |      " + (truePositives + falsePositives) + "     |       " + (trueNegatives + falseNegatives));
            }
            else{
                System.out.println(" Total:             |      " + (truePositives + falsePositives) + "    |       " + (trueNegatives + falseNegatives));
            }


            System.out.println();
            System.out.println("Accuracy: "+   (String.format("%,.2f", accuracy*100))+"%");
            System.out.println("Precision: "+(String.format("%,.2f", precision*100))+"%");
            System.out.println("Sensitivity: "+(String.format("%,.2f", sensitivity*100))+"%");
            System.out.println("Specificity: "+(String.format("%,.2f", specificity*100))+"%");
        }


        System.out.println();
        System.out.println(wrongPredictions);
    }
}
