package edu.bu.ec504.hw0p4;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashSet;

public class Main {

    private static final String DELIMITER = "---"; // delimiter between input sections

    public static void main(String[] args) {
        myFindSimilar myFindSimilar = new myFindSimilar();

	      // Read and process input
        BufferedReader reader =
            new BufferedReader(new InputStreamReader(System.in));
        try {
            // read in dictionary
            for (
                String line = reader.readLine();
                !line.equals(DELIMITER);
                line = reader.readLine())
            {
                myFindSimilar.addToDictionary(line);
            }

//             read in words
            for (
                String line = reader.readLine();
                !line.equals(DELIMITER);
                line = reader.readLine())
            {
                System.out.println(myFindSimilar.closestWord(line));
            }

        } catch (IOException e) {
            System.out.println("Sorry ... something went wrong.");
            e.printStackTrace();
            System.exit(-1);
        }

    }

    // FIELDS
    HashSet<String> dict;  // The dictionary against which to check
}