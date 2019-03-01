package com.dant.entity;

import java.util.HashMap;

public class HashMapValues extends HashMap<Object, Integer[]> {
    public HashMapValues() {
        super();
    }

    @Override
    public Integer[] put(Object value, Integer[] ids) {
        return this.put(value, ids);
    }
}
