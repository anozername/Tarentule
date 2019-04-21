package com.dant.app;

import com.dant.entity.*;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;
import java.io.File;
import java.util.Map;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;


@Path("/index/test")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class TestIndex {

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
    public String getIndex() {
        List<Object[]> content = CSVReader.readLines();

        //definir ici les index
        int[] defineIndex = {3};
        Object[] attributes = content.remove(0);
        Lines lines = new Lines(defineIndex, attributes, content);
        Index index = new Index(lines);
        index.putValues();
        return index.get("passenger_count").toString();
    }

    @GET
    @Path("/exception")
    public Response exception() {
        throw new RuntimeException("oups...");
    }

}
