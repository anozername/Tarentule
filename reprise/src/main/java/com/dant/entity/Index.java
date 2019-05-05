package com.dant.entity;

import com.dant.app.Results;

import java.util.*;
import java.util.HashMap;
import com.dant.app.Results;

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
        for (int index : lines.getPosIndex()) {
            for (Object[] line : lines) {
                if (values.hasValue(line[index])) {
                    ids = values.get(line[index]);
                    ids.add((Integer)line[lines.getPosID()]);
                    values.replace(line[index], ids);
                }
                else {
                    ids = new ArrayList<Integer>();
                    ids.add((Integer)line[lines.getPosID()]);
                    //nouvelle valeur a chaque fois
                    values.put(line[index], ids);
                }
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
    }*/

    public Lines getWithoutIndexGroupBy(Map<String, Object[]> queries, List<String> groupBy) {
        if(groupBy.isEmpty()) {
            return new Lines(getValueWithoutIndex(queries));
        }
        return new Lines(getValueWithoutIndex(queries, groupBy));
    }

    /* return (all...) the data by ids of lines in hashmap -> GROUPBY attribute ? return map<attribute, object[]> puis print ?
    public List<Integer> getValueWithIndex(String key) {
        HashMapValues hashmapvalues = findInHashMap(key);
        List<Integer> res = new ArrayList<>();
        for (ArrayList<Integer> ids : hashmapvalues.values()) {
            res.addAll(ids);
        }
        return res;
    }

    public List<Integer> getValueWithoutIndex(String key){
        List<Integer> res = new ArrayList<>();
        for (int i=0; i<lines.size(); i++) {
            res.add(i);
        }
        return res;
    }

     */

    public List<Integer> getValueWithIndex(String key, Object value) {
        return findInHashMap(key).findInHashMapValues(value);
       /* List<Integer> ids = findInHashMap(key).findInHashMapValues(value);
        List<Object[]> res = new ArrayList<>();
        for (Integer id : ids) res.add(lines.rechercheDicho(id));
        return res;*/
    }

    public List<Object[]> getValueWithoutIndex(Map<String, Object[]> queries){
        List<Object[]> res = new ArrayList<>();
        int satisfaction = 0;
        for (Object[] line : lines) {
            for (Map.Entry<String, Object[]> query : queries.entrySet()) {
                //0 car seule la premiere entree est consideree pour l instant
                if (line[lines.getPosNameIndex(query.getKey())].equals(query.getValue()[0])) {
                    satisfaction++;
                }
                else satisfaction = 0;
            }
            if (satisfaction == queries.size()) {
                res.add(line);
                satisfaction = 0;
            }
        }
        return res;
    }

    public List<Object[]> getValueWithoutIndex(Map<String, Object[]> queries, List<String> groupBy){
        List<List<Object[]>> groupedRes = new ArrayList<>();
        List<Object[]> res;
        List<Integer> indicesGroup = new ArrayList<>();
        int satisfaction = 0;
        int friend = 0;
        boolean added = false;
        for (String attribute : groupBy) {
            indicesGroup.add(lines.getIndiceForAttribute(attribute));
        }
        for (Object[] line : lines) {
            for (Map.Entry<String, Object[]> query : queries.entrySet()) {
                //0 car seule la premiere entree est consideree pour l instant
                if (line[lines.getPosNameIndex(query.getKey())].equals(query.getValue()[0])) {
                    satisfaction++;
                }
                else satisfaction = 0;
            }
            if (satisfaction == queries.size()) {
                for (List<Object[]> group : groupedRes) {
                    for (Integer i : indicesGroup) {
                        if (line[i].equals(group.get(0)[i])) friend++;
                        else friend = 0;
                    }
                    if (friend == indicesGroup.size()) {
                        group.add(line);
                        friend = 0;
                        added = true;
                    }
                }
                if (!added) {
                    res = new ArrayList<>();
                    res.add(line);
                    groupedRes.add(res);
                }
                added = false;
            }
            satisfaction = 0;
        }
        res = new ArrayList<>();
        for (List<Object[]> l : groupedRes) {
            res.addAll(l);
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
