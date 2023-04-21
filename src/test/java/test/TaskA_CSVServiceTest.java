package test;

import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

public class TaskA_CSVServiceTest {

    @Test
    public List<List<Double>> readCSVFile() throws IOException {
        //read file
    File dataset = new File("dataset/mnist_test.csv");
    List<String> lines = Files.readAllLines(dataset.toPath());

        List<String> data = new ArrayList<>();

        List<List<Double>> mnist_values = new ArrayList<List<Double>>();

        //Read all contents of the file line by line
        for (String line : lines){
            try {
                 data.add(line);

                 //Task A.a - Printing first two lines
                 if(data.size() == 1) {
                     System.out.println(data.get(0));
                 }
                 if(data.size() == 2) {
                     System.out.println(data.get(1));
                 }

                List <String> splitLineOfData = new ArrayList<>();

                 // Task A.b - Splitting a line into an array of strings
                if (line != data.get(0)) {
                    for (String value : line.split(",")) {

                        splitLineOfData.add(value);
                    }

                    //Task A.c - Convert this array to an array of double, and adding it to a list of arrays mnist_values
                    List<Double> lineAsDouble = new ArrayList<>();
                    for (String value:splitLineOfData)
                    {
                        //convert to double
                        lineAsDouble.add(Double.parseDouble(value));
                    }

                    //Add line as double to main variable
                    mnist_values.add(lineAsDouble);

                }

            }catch (Exception e){
                    System.out.println(e.getMessage() + " in line " + line);
            }
        }


        //Return the entire array

        return mnist_values;


    }
}
