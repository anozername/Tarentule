
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

    public Object[] findID(int index, Object value) {
        ArrayList<Object> ids = new ArrayList<Object>();
        for (Object[] line : lines) {
            if (line[index].equals(value)) {
                ids.add(line[lines.getPosID()]);
            }
        }
        return ids.toArray();
    }


    public void putValues(Lines lines) {
        HashMapValues values = new HashMapValues();
        for (int index : lines.getPosIndex()) {
            for (Object[] line : lines) {
                if (!values.hasValue(line[index])) {
                    values.put(line[index], findID(index, line[index]));
                }
            }
            hashmap.put(lines.getNameIndex()[index], values);
        }
    }

    public List<Object[]> get(String key) {
        if(hashmap.containsKey(key)) {
            return getValueWithoutIndex(key);
        }
        return getValueWithIndex(key);
    }

    public List<Object[]> getValueWithIndex(String key){
        HashMapValues hashmapvalues = new HashMapValues();
        Integer[] res;
        for (Integer[] ids : hashmapvalues.values()) {
            //res = (Integer[])ArrayUtils.addAll(res, ids);

        }

    }


    public List<Object[]> getValueWithoutIndex(String key){
        List<Object[]> res = new //TODO
        for(Object[] line : lines){
            if (line[0].equals(key)){
                res.add(line);
            }
        }
        return res;
    }

}
