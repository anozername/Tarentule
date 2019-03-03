package com.dant.entity;

import java.util.HashMap;

public class Index {
    private Column[] attributes;
    private HashMap<String, HashMapValues> hashmap = new HashMap<String, HashMapValues>();

    public Index(Column[] attributes) {
        this.attributes = attributes;
    }
    
  //TODO remplir les index avec un appel sql sur cols: get ResultSet 

    public void putValues(Column attribute, HashMapValues values) {
        hashmap.put(attribute.getName(), values);
    }

    // inutile
    public int findID(String attribute) {
        for (int i=0; i<attributes.length; ++i) {
            if (attributes[i].getName().equals(attribute)) {
                return i;
            }
        }
        return -1;
    }
}
