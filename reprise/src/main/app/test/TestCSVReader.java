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
    private static Index index;
    private static List<String> selection = new ArrayList<>();
    private static List<String> groupBy = new ArrayList<>();
    private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd' 'HH:mm:ss");
    private static Map<String, Object[]> queriesTMP = new HashMap<>();
    private static Map<String, Object[]> indexTMP = new HashMap<>();
    private static Map<String, Object[]> notIndexTMP = new HashMap<>();

    public static void main(String[] args) {
        MultivaluedMap<String, String> queryParams = new MultivaluedHashMap<>();
        List<String> select = new ArrayList<>();
        queryParams.add("passenger_count", "1");
        queryParams.add("trip_distance", "1.9");
        System.out.println(getIndex(queryParams, select));
    }

    public static String getIndex(MultivaluedMap<String, String> queryParams, List<String> select) {
        insertion_test();
        indexTMP.clear();
        notIndexTMP.clear();
        groupBy.clear();
        selection.clear();
        int acc = 1;
        Results tmp = new Results();
        Lines linesTMP = new Lines();
        Map<String,List<Integer>> queriesWithoutIndex = new HashMap<>();
        parser(queryParams);
        for (Map.Entry<String, Object[]> query : indexTMP.entrySet()) {
            if (acc != 1) {
                tmp = tmp.computeResults(index.getValueWithIndex(query.getKey(), query.getValue()[0]));
            }
            else {
                tmp = new Results(index.getValueWithIndex(query.getKey(), query.getValue()[0]));
            }
            //peut etre set des lines pour recherche ici => creer fct param lines dans csvfinder
            acc++;
        }
        linesTMP.addAll(index.findWithIDS(tmp));
        if (!notIndexTMP.isEmpty()) {
            if (acc != 1) {
                linesTMP = index.getWithoutIndexGroupBy(notIndexTMP, groupBy).computeResults(linesTMP);
            }
            else {
                linesTMP = index.getWithoutIndexGroupBy(notIndexTMP, groupBy);
            }
            acc++;
            System.out.println(index.getWithoutIndexGroupBy(notIndexTMP, groupBy));
        }
        else {

           /* dans le cas ou toutes les recherches sont indexees
            il faut donc formater le resultat par les attributs du grouby
            @TODO regarder si les recherches portent egalement sur le groupby = ne rien faire

            */

            if (!groupBy.isEmpty()) {
                linesTMP = linesTMP.getLinesFormatted(tmp, groupBy);
            }
        }
        //return linesTMP.toString();
        /*return index.getWithoutIndexGroupBy(notIndexTMP, groupBy).toString();
        les 2 queries reoturnent le bon resultat mais ne se computent pas */
        if (!selection.isEmpty()) return linesTMP.getLinesWithSelect(select).toString();
        else return linesTMP.toString();
    }

    public static void insertion_test() {
        CSVHelper.determineColumnsAndTypes();
        String file = "test.csv";
        CSVWriter writer = new CSVWriter(file);
        writer.writeCSVFile(1, 1000);
        CSVReader reader = new CSVReader(file);
        index = new Index(file, reader.readForIndexing());
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

    public static void parser(MultivaluedMap<String,String> queryParams) {
        Object[] numbers;
        List<String> attributes = CSVHelper.getNameIndexes();
        MultivaluedMap<String, List<Object>> mapTMP = new MultivaluedHashMap<>();
        Map.Entry<String, Object[]> entryTMP =  new AbstractMap.SimpleEntry<>(null, null);
        for (Map.Entry<String, List<String>> queries : queryParams.entrySet()) {
            if (queries.getKey().equals("SELECT")) selection = queries.getValue();
            if (queries.getKey().equals("GROUPBY")) groupBy.addAll(queries.getValue());
            else {
                for (int i = 0; i < attributes.size(); i++) {
                    if (queries.getKey().equals(attributes.get(i))) {
                        switch (CSVHelper.getTypes().get(i).toString()) {
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
}
