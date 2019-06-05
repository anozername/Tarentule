package main.app.core.entity;

import main.app.core.search.CSVFinder;
import javax.ws.rs.core.MultivaluedMap;
import java.util.*;
import java.util.HashMap;

public class Index {
    private HashMap<String, MultivaluedMap<Object, Integer>> hashmap;
    private CSVFinder finder;

    public Index(String fileName, HashMap hashmap) {
        this.finder = new CSVFinder(fileName);
        this.hashmap = hashmap;
    }
    
    public HashMap<String, MultivaluedMap<Object, Integer>> getHashmap() {
    	return hashmap;
    }
    
    private MultivaluedMap<Object, Integer> findInHashMap(String attribute) {
        return hashmap.get(attribute);
    }

    public Lines getWithoutIndexGroupBy(Map<String, Object> queriesAND, Map<String, Object> queriesOR, List<String> groupBy) {
        if(groupBy.isEmpty()) {
            return new Lines(finder.getValueWithoutIndex(queriesAND, queriesOR));
        }
        else {
            return new Lines(finder.getValueWithoutIndexGB(queriesAND, queriesOR, groupBy));
        }
    }

    public Lines getWithoutIndexGroupBy(Map<String, Object> queriesAND, Map<String, Object> queriesOR, List<String> groupBy, List<Object[]> lines) {
        if (lines.isEmpty()) {
            if(groupBy.isEmpty()) {
                return new Lines(finder.getValueWithoutIndex(queriesAND, queriesOR));
            }
            else {
                return new Lines(finder.getValueWithoutIndexGB(queriesAND, queriesOR, groupBy));
            }
        }
        else {
            if (groupBy.isEmpty()) {
                return new Lines(finder.getValueWithoutIndex(queriesAND, queriesOR, lines));
            }
            else {
                return new Lines(finder.getValueWithoutIndexGB(queriesAND, queriesOR, groupBy, lines));
            }
        }
    }

    public List<Integer> getValueWithIndex(String key, Object value) {
        if (findInHashMap(key) != null) {
            return findInHashMap(key).get(value);
        }
        else {
            return new ArrayList<>();
        }
    }

    public List<Object[]> findWithIDS(List<Integer> ids) {
        return finder.findLinesWithIds(ids);
    }
}
