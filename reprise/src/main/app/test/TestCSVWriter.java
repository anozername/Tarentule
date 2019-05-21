package main.app.test;

import main.app.core.search.CSVWriter;

public class TestCSVWriter {

    public static void main(String[] args) {
        CSVWriter writer = new CSVWriter("test.csv");
        writer.writeCSVFile(0, 10);
        System.out.println("ok");
    }
}
