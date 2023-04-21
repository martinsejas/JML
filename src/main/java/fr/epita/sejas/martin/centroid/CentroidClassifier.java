package fr.epita.sejas.martin.centroid;

import fr.epita.sejas.martin.images.Image;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static fr.epita.sejas.martin.Main.calculateDistribution;

public class CentroidClassifier {


    //map of the centroids
    protected Map<String, double[][]> centroids;

    //hashmap of predictions for confusion matrix
    public Map<String, Integer> predictionDistribution = new HashMap<>();

    //hashmap of wrong predictions
    public Map<String, HashMap<String,Integer>> wrongPredictions = new HashMap<>();

    //hashmap of the classifier metrics
    public Map<String, HashMap<String,Integer>> classifierMetrics;




    //constructor for just training centroid, without metrics
    public CentroidClassifier(List<Image> trainingImages) throws IOException {
        this.centroids = trainCentroids(trainingImages);

    }

    //constructor with testimages included to generate metrics instantly
    public CentroidClassifier(List<Image> trainingImages, List<Image> testImages) throws IOException {
        this.centroids = trainCentroids(trainingImages);
        this.generateClassifierMetrics(testImages);
    }

    public CentroidClassifier() {

    }


    // main method that will generate centroids from a list of images
    public  Map<String, double[][]> trainCentroids(List<Image> images) throws IOException
    {
        // the hashmap will have the label, and then the matching centroid (data) matrix for the label
        Map<String, double[][]> centroids = new HashMap<>();



        //looping through all images and adding all indices on values
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

                // get the sum for each index by looping the 2d array
                for (int i = 0; i < centroidMatrix.length; i++) {
                    for (int j = 0; j < centroidMatrix[i].length; j++) {
                        centroidMatrix[i][j] = (imageDataMatrix[i][j] + centroidMatrix[i][j]) ;
                    }
                }

                //place the new centroid matrix on the hashmap
                centroids.put(imageLabel, centroidMatrix);
            }
        }

        //get distribution of labels to properly divide centroid values and get proper centroid matrix
       Map<String, Integer> trainingDistribution = calculateDistribution(images);

        for(String label:trainingDistribution.keySet())
        {
            //get the size of n for each centroid
            int numberOfItems = trainingDistribution.get(label);

            //get the sum of all values in the centroid
            double[][] centroidMatrix = centroids.get(label);

            //get the average value for each index
            for(int i = 0; i < centroidMatrix.length; i++)
            {
                for(int j = 0; j < centroidMatrix[i].length; j++)
                {
                    centroidMatrix[i][j] = centroidMatrix[i][j]/(double) numberOfItems;
                }
            }

            //put it back on the hashmap
            centroids.put(label, centroidMatrix);

        }


        // return centroids
        return centroids;

    }


    // method for predicting the label of an image
    public String predict(Image image){

        //getting dataMatrix
        double [][] dataMatrix = image.getDataMatrix();

        //var for determining prediction
        double min_distance = Double.POSITIVE_INFINITY;

        String prediction = "";

        //for every label and its centroid get the minimum distance to the image to get a prediction
        for( String label:this.centroids.keySet())
        {
            double distance = calcCentroidDistance(dataMatrix, centroids.get(label));

            if(distance < min_distance){
                min_distance = distance;
                prediction = label;
            }
        }

        return prediction;

    }

    // Helper function to calculate distance from the centroid
    protected double calcCentroidDistance(double [][] dataMatrix, double[][] trainedCentroid)
    {
        double distance = 0;

        //loop through every index of the matrix
        for (int i = 0; i < dataMatrix.length; i++){
            for(int j = 0; j <dataMatrix[i].length; j++){
                distance = Math.abs( dataMatrix[i][j] - trainedCentroid[i][j]) + distance;
            }
        }
        return Math.sqrt(distance);
    }


    public Map<String, double[][]> getCentroids() {
        return centroids;
    }


    //Method that calculates the metrics for the classifier
    public void generateClassifierMetrics( List<Image> testImages) throws IOException {

        //create main HashMap that will store metrics

        //e.g centroidMetric(label) : ( True_Positives: n, True_Negatives: n, False_Positives: n, False_Negatives: n )
        Map<String, HashMap<String,Integer>> centroidMetrics = new HashMap<>();

        // array to hold labels and generate metric hashmap
        String[] labels = {"0", "1", "2", "3", "4", "5", "6", "7", "8", "9"};



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
            this.predictionDistribution.put(label, 0);
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
            this.wrongPredictions.put(label,centroidWrongPredictions);
        }

        //main loop
        for (Image image:testImages)
        {
            //actual value
            String imageLabel = image.getLabel();

            //predict from matrix
            String predictedLabel = this.predict(image);

            int predictionCount = this.predictionDistribution.get(predictedLabel);
            predictionCount++;
            this.predictionDistribution.put(predictedLabel,predictionCount);


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
                this.wrongPredictions.put(predictedLabel,centroidWrongPredictions);

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


        this.classifierMetrics = centroidMetrics;

    }


    //Method that prints the classifier metrics
    public void printMetrics() {
        System.out.println();
        System.out.println("====================== CENTROID METRICS =========================");

        for(String centroid: this.classifierMetrics.keySet())
        {

            HashMap<String, Integer> metrics = this.classifierMetrics.get(centroid);



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
            System.out.println(" Centroid: ("+centroid+")      | Predicted: "+centroid+"   | Predicted Not: "+centroid+ "  | Total");
            System.out.println(" Times Correct:     |      "+truePositives+" (TP)  |      "+trueNegatives+ " (TN)    |  "+(trueNegatives+truePositives));
            System.out.println(" Times Wrong:       |      "+falsePositives+" (FP)  |      "+falseNegatives+" (FN)     |  "+(falseNegatives+falsePositives));

            if(truePositives+falsePositives < 1000) {
                System.out.println(" Total:             |      " + (truePositives + falsePositives) + "       |       " + (trueNegatives + falseNegatives));
            }
            else{
                System.out.println(" Total:             |      " + (truePositives + falsePositives) + "      |      " + (trueNegatives + falseNegatives));
            }



            System.out.println();
            System.out.println("Accuracy: " + (String.format("%,.2f", accuracy * 100)) + "%");
            System.out.println("Precision: " + (String.format("%,.2f", precision * 100)) + "%");
            System.out.println("Sensitivity: " + (String.format("%,.2f", sensitivity * 100)) + "%");
            System.out.println("Specificity: " + (String.format("%,.2f", specificity * 100)) + "%");

        }
        System.out.println("=================================================================");
        System.out.println();
    }


    //Method that prints the confusion matrix
    public void printConfusionMatrix(){
        System.out.println("                                  == CONFUSION MATRIX ==                              ");
        System.out.println("-----------------------------------------------------------------------------------------");
        System.out.println();

        System.out.println("                                                (TRUE)                                      ");
        System.out.println("             ================================================================================");
        System.out.println("(PREDICTED) ||   0   |   1   |   2   |   3   |   4   |   5   |   6   |   7   |   8   |   9   |");
        System.out.println("============||================================================================================");

        for(String centroid: this.wrongPredictions.keySet()) {
            Map<String, Integer> centroidWrongPredictions = this.wrongPredictions.get(centroid);

            System.out.print("     ["+centroid+"]    ||");
            for (String badPrediction:this.wrongPredictions.keySet()){

                // if matching label, print true positives
                if(badPrediction.equals(centroid))
                {
                    int correctPredictions = this.classifierMetrics.get(centroid).get("True Positives");
                    System.out.print(stringFormatter(correctPredictions));
                }

                //else get wrong predictions hashmap
                else{
                    int wrongPredictions = this.wrongPredictions.get(centroid).get(badPrediction);
                    System.out.print(stringFormatter(wrongPredictions));
                }
            }
            System.out.print("\n");
        }
        System.out.println("-----------------------------------------------------------------------------------------");

    }

    private String stringFormatter(int number){
        String formatted = "";

        if(number >= 1000)
        {
            formatted = "  "+ number+ " |";
            return formatted;
        }

        else if(number >= 100)
        {
            formatted = "  "+ number+ "  |";
            return formatted;
        } else if (number >= 10) {
            formatted = "  "+number+"   |";
            return formatted;
        }
        else {
            formatted = "  "+number+"    |";
            return formatted;
        }
    }
}


