package com.dant.app;

import com.dant.entity.*;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
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

    private static Index index;
    private static Lines lines;
    private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd' 'HH:mm:ss");

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
       /* List<Object[]> content = CSVReader.readLines();

        //definir ici les index
        int[] defineIndex = {3};
        Object[] attributes = content.remove(0);
        lines = new Lines(defineIndex, attributes, content);
        index = new Index(lines);
        index.putValues();
        return index.getLines().toString();*/
       return "pas insert car developpement";
    }

    @GET
    @Produces(MediaType.TEXT_HTML)
    @Path("/find")
    public String getIndex(@Context UriInfo uriInfo) {
        insertion_test();
        List<Integer> tmp = new ArrayList<>();
        MultivaluedMap<String, String> queryParams = uriInfo.getQueryParameters();
        for (Map.Entry<String,List<String>> query : queryParams.entrySet()) {
            if (!tmp.isEmpty()) {
                tmp = computeResults(tmp, parser(query.getKey(), query.getValue().get(0)));
            }
            else {
                tmp = parser(query.getKey(), query.getValue().get(0));
            }
        }
        return lines.getLines(tmp).toString();
    }


    @GET
    @Path("/exception")
    public Response exception() {
        throw new RuntimeException("oups...");
    }

    public static List<Integer> parser(String cmd, String value) {
        switch (cmd) {
            case "vendor_id":
                return (index.get("vendor_id", value));
            case "pickup_datetime":
                try {
                        return (index.get("pickup_datetime", sdf.parse(value)));
                }
                catch (Exception e) {
                    return null;
                }
            case "dropoff_datetime":
                try {
                        return (index.get("dropoff_datetime", sdf.parse(value)));
                }
                catch (Exception e) {
                    return null;
                }
            case "passenger_count":
                return (index.get("passenger_count", Integer.parseInt(value)));
            case "trip_distance":
                return (index.get("trip_distance", Double.parseDouble(value)));
            case "pickup_longitude":
                return (index.get("pickup_longitude", Double.parseDouble(value)));
            case "pickup_latitude":
                return (index.get("pickup_latitude", Double.parseDouble(value)));
            case "rate_code":
                return (index.get("rate_code", Integer.parseInt(value)));
            case "store_and_fwd_flag":
                return (index.get("store_and_fwd_flag", value));
            case "dropoff_longitude":
                return (index.get("dropoff_longitude", Double.parseDouble(value)));
            case "dropoff_latitude":
                return (index.get("dropoff_latitude", Double.parseDouble(value)));
            case "payment_type":
                return (index.get("payment_type", value));
            case "surcharge":
                return (index.get("surcharge", Integer.parseInt(value)));
            case "mta_tax":
                return (index.get("mta_tax", Double.parseDouble(value)));
            case "tip_amount":
                return (index.get("tip_amount", Double.parseDouble(value)));
            case "tolls_amount":
                return (index.get("tolls_amount", Double.parseDouble(value)));
            case "total_amount":
                return (index.get("total_amount", Double.parseDouble(value)));
        }
        return null;
    }

    /********************************************************		helpers		*/

    public void insertion_test() {
        List<Object[]> content = CSVReader.readLines();
        int[] defineIndex = {3};
        Object[] attributes = content.remove(0);
        lines = new Lines(defineIndex, attributes, content);
        index = new Index(lines);
        index.putValues();
    }

    public static List<Integer> computeResults(List<Integer> res_querie1, List<Integer> res_querie2)  {
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

}
