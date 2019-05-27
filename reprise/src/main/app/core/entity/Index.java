package main.app.core.entity;

import main.app.core.search.CSVFinder;
import main.app.core.search.Results;

import javax.ws.rs.core.MultivaluedMap;
import java.util.*;
import java.util.HashMap;

public class Index {

    private static HashMap<String, MultivaluedMap<Object, Integer>> hashmap;
    private static CSVFinder finder;

    public Index(String fileName, HashMap hashmap) {
        this.finder = new CSVFinder(fileName);
        this.hashmap = hashmap;
    }
    
    public HashMap<String, MultivaluedMap<Object, Integer>> getHashmap() {
    	return hashmap;
    }
    
    public MultivaluedMap<Object, Integer> findInHashMap(String attribute) {
    	return hashmap.get(attribute);
    }


    public Lines getWithoutIndexGroupBy(Map<String, Object[]> queries, List<String> groupBy, List<Object[]> lines) {
        if (lines.isEmpty()) {
            if(groupBy.isEmpty()) {
                return new Lines(finder.getValueWithoutIndex(queries));
            }
            return new Lines(finder.getValueWithoutIndexGB(queries, groupBy));
        }
        else {
            if (groupBy.isEmpty()) {
                return new Lines(finder.getValueWithoutIndex(queries, lines));
            }
            return new Lines(finder.getValueWithoutIndexGB(queries, groupBy, lines));
        }
    }

    public List<Integer> getValueWithIndex(String key, Object value) {
        return findInHashMap(key).get(value);
    }

    public List<Object[]> findWithIDS(List<Integer> ids) {
        return finder.findLinesWithIds(ids);
    }


}
