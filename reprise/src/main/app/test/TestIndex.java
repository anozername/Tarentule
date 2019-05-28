package main.app.test;

import main.Main;
import main.app.core.entity.*;

import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.io.File;

import main.app.core.search.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;

@Path("/test/index")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class TestIndex {
    private static Parser parser;


    @GET
    @Produces(MediaType.TEXT_HTML)
    public String helloWorld() throws Exception{
        File input = new File("input.csv");
        File output = new File("output.json");

        CsvSchema csvSchema = CsvSchema.builder().setUseHeader(true).build();
        CsvMapper csvMapper = new CsvMapper();

       // ObjectMapper mapper2 = new ObjectMapper();
        // Read data from CSV file
        List<Object> readAll = csvMapper.readerFor(Map.class).with(csvSchema).readValues(input).readAll();
        ObjectMapper mapper = new ObjectMapper();

        // Write JSON formated data to output.json file
        mapper.writerWithDefaultPrettyPrinter().writeValue(output, readAll);

        // Write JSON formated data to stdout
        //System.out.println(mapper.writerWithDefaultPrettyPrinter().writeValueAsString(readAll));

        //List<Map<String, Object>> readlines = mapper2.readValue(output, new TypeReference<List<Map<String, Object>>>(){} );

        return readAll.toString();
    }

    @GET
    @Produces(MediaType.TEXT_HTML)
    @Path("/insert")
    public String insert() {
        insertion_test();
        return "insertion ok";
    }

    @GET
    @Produces(MediaType.TEXT_HTML)
    @Path("/find")
    public String getIndex(@QueryParam("query") String query) {
       return parser.parse(query);
    }


    @GET
    @Path("/exception")
    public Response exception() {
        throw new RuntimeException("oups...");
    }


    /********************************************************		helpers		*/

    public void insertion_test() {
        CSVHelper.determineColumnsAndTypes();
        String file = "test.csv";
        CSVWriter writer = new CSVWriter(file);
        writer.writeCSVFile(1, 100000);
        CSVReader reader = new CSVReader(file);
        Index index = new Index(file, reader.readForIndexing());
        parser = new Parser(index);
    }

    //et
    public static List<Integer> computeResults(List<Integer> res_querie1, List<Integer> res_querie2)  {
        if (res_querie1 == null) return new ArrayList<>();
        List<Integer> tmp = new ArrayList<>();
        for (int iq1 : res_querie1) {
            for (int iq2 : res_querie2) {
                if (iq1 == iq2) {
                    tmp.add(iq1);
                    break;
                }
            }
        }
        return tmp;
    }

    public static int getIndiceMax(List<Double> list) {
        Double max = list.get(0);
        for (Double d : list) {
            if (max < d) max = d;
        }
        return list.indexOf(max);
    }

}
