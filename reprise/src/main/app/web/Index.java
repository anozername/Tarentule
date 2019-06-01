package main.app.web;

import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.util.*;
import java.io.File;

import main.Main;
import main.app.core.search.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;

@Path("/test/index")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class Index {
    private static Parser parser;

    @GET
    @Produces(MediaType.TEXT_HTML)
    public String helloWorld() throws Exception{
        File input = new File(Main.file_path);
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
        return "insert ok";
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
        writer.writeCSVFile(1, 100);
        CSVReader reader = new CSVReader(file);
        main.app.core.entity.Index index = new main.app.core.entity.Index(file, reader.readForIndexing());
        parser = new Parser(index);
    }


}
