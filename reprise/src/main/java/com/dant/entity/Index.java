package com.dant.entity;

import java.util.*;
import java.util.HashMap;

public class Index {

    private Lines lines;
    private HashMap<String, HashMapValues> hashmap = new HashMap<String, HashMapValues>();

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

    public Lines get(String key) {
        if(hashmap.containsKey(key)) {
            return getValueWithIndex(key);
        }
        return getValueWithoutIndex(key);
    }

    public Lines get(String key, Object value) {
        if(hashmap.containsKey(key)) {
            return getValueWithIndex(key, value);
        }
        return getValueWithoutIndex(key, value);
    }

    /* return (all...) the data by ids of lines in hashmap -> GROUPBY attribute ? return map<attribute, object[]> puis print ? */
    public Lines getValueWithIndex(String key) {
        HashMapValues hashmapvalues = findInHashMap(key);
        ArrayList<Object[]> res = new ArrayList<Object[]>();
        for (ArrayList<Integer> ids : hashmapvalues.values()) {
            res.addAll(lines.getLines(ids));
        }
        return new Lines(null, null, res);
    }

    public Lines getValueWithIndex(String key, Object value) {
        ArrayList<Integer> ids = findInHashMap(key).findInHashMapValues(value);
        return new Lines(null, null, lines.getLines(ids));
    }

    public Lines getValueWithoutIndex(String key){
        return lines;
    }

    public Lines getValueWithoutIndex(String key, Object value){
        int pos = lines.getPosNameIndex(key);
        ArrayList<Object[]> res = new ArrayList<Object[]>();
        for (Object[] line : lines) {
            if (line[pos].equals(value)) {
                res.add(line);
            }
        }
        return new Lines(null, null, res);
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
