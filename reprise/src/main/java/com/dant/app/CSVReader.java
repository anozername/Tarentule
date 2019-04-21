package com.dant.app;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import java.util.*;

public class CSVReader {

    public static List<Object[]> readLines() {

        String csvFile = "input.csv";
        String line = "";
        String cvsSplitBy = ",";
        List<Object[]> res = new ArrayList<Object[]>();
        try (BufferedReader br = new BufferedReader(new FileReader(csvFile))) {

            while ((line = br.readLine()) != null) {

                // use comma as separator
                String[] trip = line.split(cvsSplitBy);
                res.add(trip);


            }


        } catch (IOException e) {
            e.printStackTrace();
        }
        return res;
    }

}
