package com.dant.entity;

import java.util.HashMap;
import java.util.Date;
import java.util.*;

public class HashMapValues extends HashMap<Object, ArrayList<Integer>> {
    public HashMapValues() {
        super();
    }

    /*public Integer[] put(int value, Integer[] ids) {
        return this.put(value, ids);
    }
 
    public Integer[] put(float value, Integer[] ids) {
        return this.put(value, ids);
    }
    
    public Integer[] put(String value, Integer[] ids) {
        return this.put(value, ids);
    }
    
    public Integer[] put(Date value, Integer[] ids) {
        return this.put(value, ids);
    }

    public ArrayList<Integer> put(Object value, ArrayList<Integer> ids) {
        return this.put(value, ids);
    }*/

    public ArrayList<Integer> findInHashMapValues(Object key) {
        for (Object val : keySet()) {
            if (val.equals(key)) {
                return get(val);
            }
        }
        return null;
    }

    public boolean hasValue(Object value) {
        return this.containsKey(value);
    }
}
