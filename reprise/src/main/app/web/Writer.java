package main.app.web;

public class Writer {

    public static void main(String[] args) {
        main.app.core.search.CSVWriter writer = new main.app.core.search.CSVWriter("test.csv");
        writer.writeCSVFile(0, 10);
        System.out.println("ok");
    }
}
