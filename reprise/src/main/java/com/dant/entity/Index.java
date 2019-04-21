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

    public List<Object[]> get(String key) {
        if(hashmap.containsKey(key)) { //ou regarder dans lines.nameindex...
            return getValueWithIndex(key);
            //return getValueWithoutIndex(key);
        }
        return getValueWithIndex(key);
    }

    /* return (all...) the data by ids of lines in hashmap -> GROUPBY attribute ? return map<attribute, object[]> puis print ? */
    public List<Object[]> getValueWithIndex(String key) {
        HashMapValues hashmapvalues = findInHashMap(key);
        ArrayList<Object[]> res = new ArrayList<Object[]>();
        for (ArrayList<Integer> ids : hashmapvalues.values()) {
            res.addAll(lines.getLines(ids));
        }
        return res;
    }
    
    /* @TODO find lines without index
     * 
    public List<Object[]> getValueWithoutIndex(String key){
        List<Object[]> res = new //TODO
        for(Object[] line : lines){
            if (line[0].equals(key)){
                res.add(line);
            }
        }
        return res;
    }
    */
    
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
