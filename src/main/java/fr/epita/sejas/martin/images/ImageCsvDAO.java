package fr.epita.sejas.martin.images;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

public class ImageCsvDAO {

//TASK C

    public List<Image> getAllImages(String path) throws IOException {
        //read file
        File dataset = new File(path);
        List<String> lines = Files.readAllLines(dataset.toPath());

        List<String> data = new ArrayList<>();

        List<Image> myImages = new ArrayList<>();

        //Read all contents of the file line by line
        for (String line : lines){
            try {
                data.add(line);

                //Task A.a - Printing first two lines
                if(data.size() == 1) {
                    System.out.println("Printing Lines of DataSet");
                    System.out.println(data.get(0));
                }
                if(data.size() == 2) {
                    System.out.println(data.get(1));
                    System.out.println("=========================================================");
                    System.out.println("");
                }

                List <String> splitLineOfData = new ArrayList<>();

                // Task A.b - Splitting a line into an array of strings
                if (line != data.get(0)) {

                    for (String value : line.split(",")) {

                        splitLineOfData.add(value);
                    }

                    String label = splitLineOfData.get(0);
                    splitLineOfData.remove(0);

                    //Task A.c - Convert this array to an array of double, and adding it to a list of arrays mnist_values
                    List<Double> lineAsDouble = new ArrayList<>();

                    for (String value:splitLineOfData) {
                        //convert to double
                        lineAsDouble.add(Double.parseDouble(value));
                    }

                    // add image to array
                    myImages.add(new Image(label, lineAsDouble));
                }

            }catch (Exception e){
                System.out.println(e.getMessage() + " in line " + line);
            }
        }

        //Return the entire array

        return myImages;


    }
}
