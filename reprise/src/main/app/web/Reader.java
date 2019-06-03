package main.app.web;

import main.Main;
import main.app.core.entity.Index;
import main.app.core.search.*;

//c'est pas vraiment un test mais c'est bien utile
public class Reader {
    private static Parser parser;

    public static void main(String[] args) {
        CSVHelper.determineColumnsAndTypes();
        String file = "test.csv";
        CSVWriter writer = new CSVWriter(file);
        writer.writeCSVFile(1, 100);
        main.app.core.search.CSVReader reader = new main.app.core.search.CSVReader(file);
        Index index = new Index(file, reader.readForIndexing());
        parser = new Parser(index);
        String s = parser.parse("SELECT * WHERE (trip_distance = 1.9) OR (vendor_id = CMT) GROUPBY rate_code, total_amount");// 'x = M' ok // "x=M' not
        System.out.println(s);
    }

}
