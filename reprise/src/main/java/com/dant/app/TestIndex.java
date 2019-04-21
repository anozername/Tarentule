package com.dant.app;

import com.dant.entity.Account;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;
import java.io.File;
import java.util.Map;

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

        // Read data from CSV file
        List<Object> readAll = csvMapper.readerFor(Map.class).with(csvSchema).readValues(input).readAll();

        ObjectMapper mapper = new ObjectMapper();

        // Write JSON formated data to output.json file
        mapper.writerWithDefaultPrettyPrinter().writeValue(output, readAll);

        // Write JSON formated data to stdout
        //System.out.println(mapper.writerWithDefaultPrettyPrinter().writeValueAsString(readAll));

        return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(readAll);
    }

    @POST
    @Path("/entity")
    public Account getAccount(Account account) {
        System.out.println("Received account " + account);
        account.setUpdated(System.currentTimeMillis());
        return account;
    }

    @GET
    @Path("/exception")
    public Response exception() {
        throw new RuntimeException("oups...");
    }

}
