/*package main.app.core.entity;

import javax.ws.rs.core.MultivaluedMap;
import java.util.HashMap;
import java.util.*;

public class HashMapValues extends MultivaluedMap<Object, Integer> {
    public HashMapValues() {
        super();
    }

    @Override
    public void putSingle(Object key, Integer value) {
        put(key, new ArrayList<>(value));
    }

    @Override
    public void add(Object key, Integer value) {
        add(key, value);
    }

    @Override
    public Integer getFirst(Object key) {
        return getFirst(key);
    }

    @Override
    public Integer addAll(K key, List<V> valueList) {
        return getFirst(key);
    }

    public List<Integer> findInHashMapValues(Object key) {
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

}*/
