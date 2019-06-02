package main.app.web;

import main.app.core.entity.Index;
import main.app.core.search.*;

//c'est pas vraiment un test mais c'est bien utile
public class Reader {
    private static Parser parser;

    public static void main(String[] args) {
        CSVHelper.determineColumnsAndTypes();
        String file = "test.csv";
        CSVWriter writer = new CSVWriter(file);
        writer.writeCSVFile(1, 10000);
        main.app.core.search.CSVReader reader = new main.app.core.search.CSVReader(file);
        Index index = new Index(file, reader.readForIndexing());
        parser = new Parser(index);
        String s = parser.parse("SELECT AVG(passenger_count) WHERE (store_and_fwd_flag = M)");
        System.out.println(s);
    }

}
