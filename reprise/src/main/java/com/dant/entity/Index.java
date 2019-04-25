package com.dant.entity;

import java.util.*;
import java.util.HashMap;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.MultivaluedHashMap;

public class Index {

    private Lines lines;
    private static final HashMap<String, HashMapValues> hashmap = new HashMap<String, HashMapValues>();

    public Index(Lines lines) {
        this.lines = lines;
    }
    
    public Lines getLines() {
    	return lines;
    }
    
    public HashMap<String, HashMapValues> getHashmap() {
    	return hashmap;
    }

    public void setLines(Lines lines) {
        this.lines = lines;
    }
    
    /********************************************************		helpers		*/
    
    public HashMapValues findInHashMap(String attribute) {
    	return hashmap.get(attribute);
    }

    /********************************************************		insert		*/

    public void putValues() {
        HashMapValues values = new HashMapValues();
        ArrayList<Integer> ids;
        int id = 0;
        for (int index : lines.getPosIndex()) {
            for (Object[] line : lines) {
                if (values.hasValue(line[index])) {
                    ids = values.get(line[index]);
                    ids.add(id);
                    values.replace(line[index], ids);
                }
                else {
                    ids = new ArrayList<Integer>();
                    ids.add(id);
                    //nouvelle valeur a chaque fois
                    values.put(line[index], ids);
                }
                id++;
            }
            hashmap.put((String)lines.getNameIndex()[index], values);
        }
    }
    
    /********************************************************		find		*/

    //@TODO renvoyer une List ids pour queries et puis recherche de lines avec ids

    /*public List<Integer> get(String key) {
        if(hashmap.containsKey(key)) {
            return getValueWithIndex(key);
        }
        return getValueWithoutIndex(key);
    }

    public List<Integer> get(String key, Object value) {
        if(hashmap.containsKey(key)) {
            return getValueWithIndex(key, value);
        }
        return getValueWithoutIndex(key, value);
    }*/

    /* return (all...) the data by ids of lines in hashmap -> GROUPBY attribute ? return map<attribute, object[]> puis print ? */
    public List<Integer> getValueWithIndex(String key) {
        HashMapValues hashmapvalues = findInHashMap(key);
        List<Integer> res = new ArrayList<>();
        for (ArrayList<Integer> ids : hashmapvalues.values()) {
            res.addAll(ids);
        }
        return res;
    }

    public List<Integer> getValueWithIndex(String key, Object value) {
        return findInHashMap(key).findInHashMapValues(value);
    }

    public List<Integer> getValueWithoutIndex(String key){
        List<Integer> res = new ArrayList<>();
        for (int i=0; i<lines.size(); i++) {
            res.add(i);
        }
        return res;
    }

    public List<Integer> getValueWithoutIndex(Map<String, Object[]> queries){
        Map<Integer, Object> conditions = new HashMap<>();
        for (Map.Entry<String, Object[]> query : queries.entrySet()) {
            conditions.put(lines.getPosNameIndex(query.getKey()), query.getValue()[0]);
        }
        List<Integer> res = new ArrayList<>();
        for (Object[] line : lines) {
            for (Map.Entry<Integer, Object> condition : conditions.entrySet()) {
                //0 car seule la premiere entree est consideree pour l instant
                if (line[condition.getKey()].equals(condition.getValue())) {
                    res.add((Integer) line[lines.getPosID()]);
                }
            }
        }
        return res;
    }
    
    /********************************************************		print		*/
    
    public void printResults(List<Object[]> result) {
    	for (Object[] res : result) {
    		for (Object e : res) {
    			System.out.println(e.toString() + " ");
    		}
    		System.out.println("\n");
    	}
    }

}
