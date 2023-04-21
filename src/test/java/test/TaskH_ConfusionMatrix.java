package test;

import fr.epita.sejas.martin.centroid.CentroidClassifier;
import fr.epita.sejas.martin.images.Image;
import fr.epita.sejas.martin.images.ImageCsvDAO;
import org.junit.jupiter.api.Test;
import fr.epita.sejas.martin.Main.*;

import java.io.IOException;
import java.util.*;

import static fr.epita.sejas.martin.Main.calculateDistribution;

public class TaskH_ConfusionMatrix {


    public String stringFormatter(int number){
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

    @Test
    public void printConfusionMatrix() throws IOException {
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

        //7
//        System.out.println("-----------------------------------------------------------------------------------------");
        System.out.println("                                  == CONFUSION MATRIX ==                              ");
        System.out.println("-----------------------------------------------------------------------------------------");
        System.out.println();

        System.out.println("                                      (PREDICTED)                                      ");
        System.out.println("         ================================================================================");
        System.out.println("(TRUE) ||   0   |   1   |   2   |   3   |   4   |   5   |   6   |   7   |   8   |   9   |");
        System.out.println("=======||================================================================================");

        for(String centroid: classifier.wrongPredictions.keySet()) {
            Map<String, Integer> centroidWrongPredictions = classifier.wrongPredictions.get(centroid);

            System.out.print("  ["+centroid+"]  ||");
            for (String badPrediction:classifier.wrongPredictions.keySet()){

                 // if matching label, print true positives
                 if(badPrediction.equals(centroid))
                 {
                    int correctPredictions = classifier.classifierMetrics.get(centroid).get("True Positives");
                    System.out.print(stringFormatter(correctPredictions));
                 }

                 //else get wrong predictions hashmap
                else{
                    int wrongPredictions = classifier.wrongPredictions.get(centroid).get(badPrediction);
                    System.out.print(stringFormatter(wrongPredictions));
                 }
            }
            System.out.print("\n");
        }
        System.out.println("-----------------------------------------------------------------------------------------");
    }
}
