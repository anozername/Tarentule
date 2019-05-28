package main.app.test;

import main.app.core.entity.Index;
import main.app.core.entity.Lines;
import main.app.core.search.*;

import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.UriInfo;
import java.text.SimpleDateFormat;
import java.util.*;

//c'est pas vraiment un test mais c'est bien utile
public class TestCSVReader {
    private static Parser parser;

    public static void main(String[] args) {
        CSVHelper.determineColumnsAndTypes();
        String file = "test.csv";
        CSVWriter writer = new CSVWriter(file);
        writer.writeCSVFile(1, 10000);
        CSVReader reader = new CSVReader(file);
        Index index = new Index(file, reader.readForIndexing());
        parser = new Parser(index);
        String s = parser.parse("SELECT * WHERE (passenger_count = 3 AND VendorID = 1 OR trip_distance = 1.9) OR (VendorID = 1 AND trip_distance = 10.9) GROUPBY VendorID, total_amount");
        System.out.println(s);
    }

}
