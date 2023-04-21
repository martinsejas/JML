package test;

import fr.epita.sejas.martin.images.Image;
import fr.epita.sejas.martin.images.ImageCsvDAO;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class TaskD_CalculateDistribution {

    @Test
    void calculateDistributionTest() throws IOException {
        String testPath = "dataset/mnist_test.csv";
        String trainPath = "dataset/mnist_train.csv";

        //Here I'll be calling TaskA_CSVService only for test, but it could be equally done for train
        ImageCsvDAO csvService = new ImageCsvDAO();

        //Fulfilling Task A

        //FYI this will print the first two lines of each data set
        List<Image> testImages = csvService.getAllImages(testPath);
        List<Image> trainingImages = csvService.getAllImages(trainPath);

        Map<String, Integer> integerDistribution = new LinkedHashMap<>();

        for (Image image : testImages) {
            Integer count = integerDistribution.get(image.getLabel());
            if (count == null) {
                count = 1;
            } else {
                count++;
            }
            integerDistribution.put(image.getLabel(), count);
        }

    System.out.println(integerDistribution);
//        return integerDistribution;
    }

}