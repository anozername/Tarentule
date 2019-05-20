package main.app.test;

import main.Main;
import main.app.core.entity.*;

import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.io.File;

import main.app.core.search.CSVReader;
import main.app.core.search.CastHelper;
import main.app.core.search.Results;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;

@Path("/test/index")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class TestIndex {
    private static Index index;
    private static Lines lines;
    private static List<String> selection = new ArrayList<>();
    private static List<String> groupBy = new ArrayList<>();
    private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd' 'HH:mm:ss");
    private static Map<String, Object[]> queriesTMP = new HashMap<>();
    private static Map<String, Object[]> indexTMP = new HashMap<>();
    private static Map<String, Object[]> notIndexTMP = new HashMap<>();

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
        //insertion_test();
        CSVReader.readForIndexing(0,0);
        return CSVReader.getIndexes().toString();
        //return CSVReader.readForIndexing(0,0).toString();
       //return "pas insert car developpement";
    }

    @GET
    @Produces(MediaType.TEXT_HTML)
    @Path("/find")
    public String getIndex(@Context UriInfo uriInfo, @QueryParam("SELECT") List<String> select) {
        insertion_test();
        indexTMP.clear();
        notIndexTMP.clear();
        groupBy.clear();
        selection.clear();
        int acc = 1;
        index.setLines(lines);
        Results tmp = new Results();
        Lines linesTMP = new Lines();
        Map<String,List<Integer>> queriesWithoutIndex = new HashMap<>();
        MultivaluedMap<String, String> queryParams = uriInfo.getQueryParameters();
        parser(queryParams);
        for (Map.Entry<String, Object[]> query : indexTMP.entrySet()) {
            if (acc != 1) {
                tmp = tmp.computeResults(index.getValueWithIndex(query.getKey(), query.getValue()[0]));
            }
            else {
                tmp = new Results(index.getValueWithIndex(query.getKey(), query.getValue()[0]));
            }
            index.setLines(index.getLines().getLines(tmp));
            acc++;
        }
        linesTMP.addAll(lines.getLines(tmp));
        if (!notIndexTMP.isEmpty()) {
            if (acc != 1) {
                linesTMP = index.getWithoutIndexGroupBy(notIndexTMP, groupBy).computeResults(linesTMP);
            }
            else {
                linesTMP = index.getWithoutIndexGroupBy(notIndexTMP, groupBy);
            }
            acc++;
        }
        else {

           /* dans le cas ou toutes les recherches sont indexees
            il faut donc formater le resultat par les attributs du grouby
            @TODO regarder si les recherches portent egalement sur le groupby = ne rien faire

            */

            if (!groupBy.isEmpty()) {
                linesTMP = new Lines(index.getLines().getLinesFormatted(tmp, groupBy));
            }
        }
        //return linesTMP.toString();
        /*return index.getWithoutIndexGroupBy(notIndexTMP, groupBy).toString();
        les 2 queries reoturnent le bon resultat mais ne se computent pas */
        if (!selection.isEmpty()) return linesTMP.getLinesWithSelect(select).toString();
        else return linesTMP.toString();
    }


    @GET
    @Path("/exception")
    public Response exception() {
        throw new RuntimeException("oups...");
    }

    public static Map.Entry<String, Object[]> castToDateMap(Map.Entry<String, List<String>> entries) {
        List<Date> tmp = new ArrayList<>();
        for (String value : entries.getValue()) {
            try {
                tmp.add(sdf.parse(value));
            }
            catch (Exception e) {
                return null;
            }
        }
        return new AbstractMap.SimpleEntry<>(entries.getKey(), tmp.toArray());
    }

    /*public static Map.Entry<String, Object[]> castToIntegerMap(Map.Entry<String, List<String>> entries) {
        List<Integer> tmp = new ArrayList<>();
        for (String value : entries.getValue()) {
            tmp.add(Integer.parseInt(value));
        }
        return new AbstractMap.SimpleEntry<>(entries.getKey(), tmp.toArray());
    }*/

    public static Map.Entry<String, Object[]> castToDoubleMap(Map.Entry<String, List<String>> entries) {
        List<Double> tmp = new ArrayList<>();
        for (String value : entries.getValue()) {
            tmp.add(Double.parseDouble(value));
        }
        return new AbstractMap.SimpleEntry<>(entries.getKey(), tmp.toArray());
    }


    public static void parser(MultivaluedMap<String,String> queryParams) {
        Object[] numbers;
        Object[] attributes = index.getLines().getNameIndex();
        MultivaluedMap<String, List<Object>> mapTMP = new MultivaluedHashMap<>();
        Map.Entry<String, Object[]> entryTMP =  new AbstractMap.SimpleEntry<>(null, null);
        for (Map.Entry<String, List<String>> queries : queryParams.entrySet()) {
            if (queries.getKey().equals("SELECT")) selection = queries.getValue();
            if (queries.getKey().equals("GROUPBY")) groupBy.addAll(queries.getValue());
            else {
                for (int i = 0; i < attributes.length; i++) {
                    if (queries.getKey().equals(attributes[i].toString())) {
                        switch (index.getLines().getTypes()[i].toString()) {
                            case "date":
                                entryTMP = castToDateMap(queries);
                                queriesTMP.put(entryTMP.getKey(), entryTMP.getValue());
                                break;
                            case "double":
                                //entryTMP = castToDoubleMap(queries);
                                numbers = new Object[queries.getValue().size()];
                                for (int j=0; j<queries.getValue().size(); j++) {
                                    Optional<Integer> it = CastHelper.castToInteger(queries.getValue().get(j));
                                    //Optional<Double> db = CastHelper.castToDouble(queries.getValue().get(j));
                                    if (it.isPresent()) numbers[j] = it.get();
                                    else numbers[j] = Double.parseDouble(queries.getValue().get(j));
                                }
                                entryTMP = new AbstractMap.SimpleEntry<>(queries.getKey(), numbers);
                                queriesTMP.put(entryTMP.getKey(), entryTMP.getValue());
                                break;
                            case "string":
                                entryTMP = new AbstractMap.SimpleEntry<>(queries.getKey(), queries.getValue().toArray());
                                queriesTMP.put(entryTMP.getKey(), entryTMP.getValue());
                                break;
                            default:
                                //entryTMP = null;
                                queriesTMP.put(entryTMP.getKey(), entryTMP.getValue());
                        }
                        if (index.getHashmap().containsKey(queries.getKey())) {
                            indexTMP.put(queries.getKey(), queriesTMP.get(queries.getKey()));
                        }
                        else {
                            notIndexTMP.put(queries.getKey(), queriesTMP.get(queries.getKey()));
                        }
                        break;
                    }
                }
            }
        }
        queriesTMP.clear();
    }


    /*public static List<Integer> parserHashMap(String cmd, String value) {
        Object[] attributes = index.getLines().getNameIndex();
            for (int i = 0; i < attributes.length; i++) {
                if (cmd.equals(attributes[i].toString())) {
                    switch (index.getLines().getTypes()[i].toString()) {
                        case "date":
                            try {
                                return (index.get(cmd, sdf.parse(value)));
                            } catch (Exception e) {
                                return null;
                            }
                        case "double":
                            Optional<Integer> it = CastHelper.castToInteger(value);
                            if (it.isPresent()) return (index.get(cmd, it.get()));
                            return (index.get(cmd, Double.parseDouble(value)));

                        case "string":
                            return (index.get(cmd, value));
                        default:
                            return new ArrayList<>();
                    }
                }
        }
        return null;*/


       /* pourrait servir mais pas generique

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
        *

    }*/

    /********************************************************		helpers		*/

    public void insertion_test() {
        //HashMap content = CSVReader.readForIndexing(0, 0);
        /*List<Integer> defineIndex = new ArrayList<>();
        List<Object> namesIndex = CSVReader.getNameIndexes();
        for (Object indexName : content.keySet()) {
            defineIndex.add(namesIndex.indexOf(indexName));
        }*/
        //Object[] attributes = content.remove(0);
        //Object[] types = content.remove(content.size()-1);
        //lines = new Lines(defineIndex.toArray(), CSVReader.getNameIndexes().toArray(), content, CSVReader.getTypes().toArray());
        //index = new Index(content);
        //index.putValues();
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
