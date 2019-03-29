package com.dant.entity;

import java.util.HashMap;
import java.util.Date;

public class HashMapValues extends HashMap<Object, Integer[]> {
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
    }*/

    public Object[] put(Object value, Object[] ids) {
        return this.put(value, ids);
    }

    public boolean hasValue(Object value) {
        return this.containsKey(value);
    }
}
