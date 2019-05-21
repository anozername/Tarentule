package main.app.test;

import main.app.core.search.CSVReader;
import main.app.core.search.CSVWriter;

public class TestCSVReader {
    public static void main(String[] args) {
        String file = "test.csv";
        CSVWriter writer = new CSVWriter(file);
        writer.writeCSVFile(1, 10);
        CSVReader reader = new CSVReader(file);
        System.out.println(reader.readForIndexing()[0]);
    }
}
